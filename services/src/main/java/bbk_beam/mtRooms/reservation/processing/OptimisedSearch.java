package bbk_beam.mtRooms.reservation.processing;

import bbk_beam.mtRooms.admin.authentication.Token;
import bbk_beam.mtRooms.db.TimestampConverter;
import bbk_beam.mtRooms.db.exception.DbQueryException;
import bbk_beam.mtRooms.db.exception.SessionExpiredException;
import bbk_beam.mtRooms.db.exception.SessionInvalidException;
import bbk_beam.mtRooms.reservation.delegate.ISearch;
import bbk_beam.mtRooms.reservation.dto.Room;
import bbk_beam.mtRooms.reservation.dto.RoomProperty;
import bbk_beam.mtRooms.reservation.exception.FailedDbFetch;
import bbk_beam.mtRooms.reservation.scheduling.ScheduleCache;
import bbk_beam.mtRooms.reservation.scheduling.datastructure.TimeSpan;
import bbk_beam.mtRooms.reservation.scheduling.timing.TimestampUTC;
import eadjlib.datastructure.ObjectTable;
import eadjlib.logger.Logger;

import java.util.*;

/**
 * OptimisedSearch
 * <p>
 * Note: Assumes that from/to dates match the slot intervals hard coded into the app.
 * e.g.: if the interval is set to 30mns then the from/to dates must be either on 0 or 30mn of the hour
 * </p>
 */
public class OptimisedSearch {
    private final Logger log = Logger.getLoggerInstance(OptimisedSearch.class.getName());
    private ISearch db_delegate;
    private ScheduleCache schedule_cache;

    /**
     * Get all the free slots available for a list of candidate rooms between 2 dates
     *
     * @param session_token Session token
     * @param candidates    List of candidate rooms for booking
     * @param from          Beginning timestamp to search from
     * @param to            End timestamp to search up to
     * @return List of free times for each candidate rooms
     * @throws DbQueryException        when DB access failed in some way
     * @throws SessionExpiredException When the session for the id provided has expired
     * @throws SessionInvalidException When the session for the id provided does not exist in the tracker
     */
    private HashMap<Room, List<TimeSpan>> calculateFreeSlots(Token session_token, List<Room> candidates, Date from, Date to) throws DbQueryException, SessionExpiredException, SessionInvalidException {
        HashMap<Room, List<TimeSpan>> results = new HashMap<>();
        for (Room candidate_room : candidates) {
            results.put(
                    candidate_room,
                    calculateFreeSlots(session_token, candidate_room, from, to)
            );
            if (results.get(candidate_room).isEmpty())
                results.remove(candidate_room);
        }
        return results;
    }

    /**
     * Get the free slots available for a candidate room between 2 dates
     *
     * @param session_token  Session token
     * @param candidate_room Room candidate
     * @param from           Beginning timestamp to search from
     * @param to             End timestamp to search up to
     * @return List of free times for the candidate room
     * @throws DbQueryException        when DB access failed in some way
     * @throws SessionExpiredException When the session for the id provided has expired
     * @throws SessionInvalidException When the session for the id provided does not exist in the tracker
     */
    private List<TimeSpan> calculateFreeSlots(Token session_token, Room candidate_room, Date from, Date to) throws DbQueryException, SessionExpiredException, SessionInvalidException {
        ObjectTable candidate_bookings = this.db_delegate.search(session_token, candidate_room, from, to);
        log.log_Debug("For [", session_token, "]: found ", candidate_bookings.rowCount(), " booking(s) for ", candidate_room);
        if (candidate_bookings.isEmpty())
            return this.schedule_cache.add(session_token, candidate_room, from, to); //(!) early return

        List<TimeSpan> results = new ArrayList<>();
        TimestampUTC previous_span_end = new TimestampUTC(TimestampConverter.getUTCTimestampString(from));
        //Create empty unbooked spans between the booking(s)
        for (int i = 1; i <= candidate_bookings.rowCount(); i++) {
            HashMap<String, Object> row = candidate_bookings.getRow(i);
            TimeSpan booking_span = new TimeSpan(
                    (String) row.get("timestamp_in"),
                    (String) row.get("timestamp_out")
            );

            if (i == 1) { //First booking
                Optional<TimeSpan> prior_span = createPriorSpan(from, booking_span);
                if (prior_span.isPresent()) //i.e. did 'booking_span' start after the 'from' Date
                    results.addAll(this.schedule_cache.add(session_token, candidate_room, prior_span.get()));
                previous_span_end = booking_span.end();
            }

            if (i > 1 && i <= candidate_bookings.rowCount()) {
                TimeSpan span = new TimeSpan(
                        previous_span_end,
                        booking_span.start()
                );
                results.addAll(this.schedule_cache.add(session_token, candidate_room, span));
                previous_span_end = booking_span.end();
            }

            if (i == candidate_bookings.rowCount()) { //Last booking
                Optional<TimeSpan> post_span = createPostSpan(booking_span, to);
                if (post_span.isPresent()) //i.e. did 'booking_span' end before the 'to' Date
                    results.addAll(this.schedule_cache.add(session_token, candidate_room, post_span.get()));
            }
        }
        return results;
    }

    /**
     * Creates a TimeSpan spanning the length of time between a date and another TimeSpan
     *
     * @param from Starting Date object
     * @param span TimeSpan beginning after
     * @return TimeSpan between 'from' and 'span.start()'
     */
    private Optional<TimeSpan> createPriorSpan(Date from, TimeSpan span) {
        Date span_start = TimestampConverter.getDateObject(span.start().get());
        if (span_start.after(from)) {
            return Optional.of(
                    new TimeSpan(
                            new TimestampUTC(TimestampConverter.getUTCTimestampString(from)),
                            span.start()
                    )
            );
        }
        return Optional.empty();
    }

    /**
     * Creates a TimeSpan spanning the length of time between a previous TimeSpan and a date
     *
     * @param span TimeSpan before
     * @param to   Ending Date object
     * @return TimeSpan between 'span.end()' and 'to'
     */
    private Optional<TimeSpan> createPostSpan(TimeSpan span, Date to) {
        Date span_end = TimestampConverter.getDateObject(span.end().get());
        if (span_end.before(to)) {
            return Optional.of(
                    new TimeSpan(
                            span.end(),
                            new TimestampUTC(TimestampConverter.getUTCTimestampString(to))
                    )
            );
        }
        return Optional.empty();
    }

    /**
     * Constructor
     *
     * @param search_delegate ISearch instance
     * @param schedule_cache  ScheduleCache instance
     */
    public OptimisedSearch(ISearch search_delegate, ScheduleCache schedule_cache) {
        this.db_delegate = search_delegate;
        this.schedule_cache = schedule_cache;
    }

    /**
     * Searches for any Rooms that match the properties given
     *
     * @param session_token Session token
     * @param properties    Room properties to look for
     * @return List of rooms
     * @throws FailedDbFetch           when an error occurred getting the record
     * @throws SessionExpiredException When the session for the id provided has expired
     * @throws SessionInvalidException When the session for the id provided does not exist in the tracker
     */
    public List<Room> search(Token session_token, RoomProperty properties) throws FailedDbFetch, SessionExpiredException, SessionInvalidException {
        try {
            ObjectTable table = this.db_delegate.search(session_token, properties);
            List<Room> list = new ArrayList<>();
            for (int i = 1; i <= table.rowCount(); i++) {
                HashMap<String, Object> row = table.getRow(i);
                list.add(
                        new Room(
                                (Integer) row.get("room_id"),
                                (Integer) row.get("floor_id"),
                                (Integer) row.get("building_id"),
                                (Integer) row.get("room_category_id")
                        )
                );
            }
            return list;
        } catch (DbQueryException e) {
            log.log_Error("Failed to fetch Room records for [", session_token, "] with properties: ", properties);
            throw new FailedDbFetch("Failed to fetch Room records for [" + session_token + "].", e);
        }
    }

    /**
     * Searches for Rooms that match the properties given
     *
     * @param session_token Session token
     * @param building_id   ID of the building to search in
     * @param properties    Room properties to look for
     * @return List of rooms
     * @throws FailedDbFetch           when an error occurred getting the record
     * @throws SessionExpiredException When the session for the id provided has expired
     * @throws SessionInvalidException When the session for the id provided does not exist in the tracker
     */
    public List<Room> search(Token session_token, Integer building_id, RoomProperty properties) throws FailedDbFetch, SessionExpiredException, SessionInvalidException {
        try {
            ObjectTable table = this.db_delegate.search(session_token, building_id, properties);
            List<Room> list = new ArrayList<>();
            for (int i = 1; i <= table.rowCount(); i++) {
                HashMap<String, Object> row = table.getRow(i);
                list.add(
                        new Room(
                                (Integer) row.get("room_id"),
                                (Integer) row.get("floor_id"),
                                (Integer) row.get("building_id"),
                                (Integer) row.get("room_category_id")
                        )
                );
            }
            return list;
        } catch (DbQueryException e) {
            log.log_Error("Failed to fetch Room records in building [", building_id, "] for [", session_token, "] with properties: ", properties);
            throw new FailedDbFetch("Failed to fetch Room records in building [" + building_id + "] for [" + session_token + "].", e);
        }
    }

    /**
     * Searches for Rooms that match the properties given
     *
     * @param session_token Session token
     * @param building_id   ID of the building to search in
     * @param floor_id      ID of the floor to search in
     * @param properties    Room properties to look for
     * @return List of rooms
     * @throws FailedDbFetch           when an error occurred getting the record
     * @throws SessionExpiredException When the session for the id provided has expired
     * @throws SessionInvalidException When the session for the id provided does not exist in the tracker
     */
    public List<Room> search(Token session_token, Integer building_id, Integer floor_id, RoomProperty properties) throws FailedDbFetch, SessionExpiredException, SessionInvalidException {
        try {
            ObjectTable table = this.db_delegate.search(session_token, building_id, floor_id, properties);
            List<Room> list = new ArrayList<>();
            for (int i = 1; i <= table.rowCount(); i++) {
                HashMap<String, Object> row = table.getRow(i);
                list.add(
                        new Room(
                                (Integer) row.get("room_id"),
                                (Integer) row.get("floor_id"),
                                (Integer) row.get("building_id"),
                                (Integer) row.get("room_category_id")
                        )
                );
            }
            return list;
        } catch (DbQueryException e) {
            log.log_Error("Failed to fetch Room records in building [", building_id, ".", floor_id, "] and for [", session_token, "] with properties: ", properties);
            throw new FailedDbFetch("Failed to fetch Room records in building [" + building_id + "." + floor_id + "] for [" + session_token + "].", e);
        }
    }

    /**
     * Searches for available times for a Room
     *
     * @param session_token Session token
     * @param room          Room DTO
     * @param from          Beginning timestamp to search from
     * @param to            End timestamp to search up to
     * @return Availability as a list of time spans where the room is free
     * @throws FailedDbFetch           when an error occurred getting the record
     * @throws SessionExpiredException When the session for the id provided has expired
     * @throws SessionInvalidException When the session for the id provided does not exist in the tracker
     */
    public List<TimeSpan> search(Token session_token, Room room, Date from, Date to) throws FailedDbFetch, SessionExpiredException, SessionInvalidException {
        try {
            return calculateFreeSlots(session_token, room, from, to);
        } catch (DbQueryException e) {
            log.log_Error("For [", session_token, "]: Problem accessing records whilst processing search between ", from, " and ", to, " for ", room);
            throw new FailedDbFetch("Problem occurred trying to access the records during a search for " + room, e);
        }
    }

    /**
     * Searches for available rooms in a building within a time frame
     *
     * @param session_token Session token
     * @param building_id   ID of building where the floor is
     * @param floor_id      ID of the floor to search in
     * @param from          Beginning timestamp to search from
     * @param to            End timestamp to search up to
     * @param property      Minimum requirements for the room
     * @return Availability as a Map of the free rooms with their associated list of time spans where the room is free
     * @throws FailedDbFetch           when an error occurred getting the record
     * @throws SessionExpiredException When the session for the id provided has expired
     * @throws SessionInvalidException When the session for the id provided does not exist in the tracker
     */
    public HashMap<Room, List<TimeSpan>> search(Token session_token, Integer building_id, Integer floor_id, Date from, Date to, RoomProperty property) throws FailedDbFetch, SessionExpiredException, SessionInvalidException {
        List<Room> candidates = search(session_token, building_id, floor_id, property);
        log.log_Debug("For [", session_token, "]: floor [", building_id, ".", floor_id, "] search found ", candidates.size(), " candidate rooms matching ", property);
        try {
            return calculateFreeSlots(session_token, candidates, from, to);
        } catch (DbQueryException e) {
            log.log_Error("For [", session_token, "]: Problem accessing records whilst processing floor [", building_id, ".", floor_id, "] search between ", from, " and ", to, " for ", property); //TODO
            throw new FailedDbFetch("Problem occurred trying to access the records during a floor [" + building_id + "." + floor_id + "] search.", e);
        }
    }

    /**
     * Searches for available rooms on a floor within a time frame
     *
     * @param session_token Session token
     * @param building_id   ID of building to search in
     * @param from          Beginning timestamp to search from
     * @param to            End timestamp to search up to
     * @param property      Minimum requirements for the room
     * @return Availability as a Map of the free rooms with their associated list of time spans where the room is free
     * @throws FailedDbFetch           when an error occurred getting the record
     * @throws SessionExpiredException When the session for the id provided has expired
     * @throws SessionInvalidException When the session for the id provided does not exist in the tracker
     */
    public HashMap<Room, List<TimeSpan>> search(Token session_token, Integer building_id, Date from, Date to, RoomProperty property) throws FailedDbFetch, SessionExpiredException, SessionInvalidException {
        List<Room> candidates = search(session_token, building_id, property);
        log.log_Debug("For [", session_token, "]: building [", building_id, "] search found ", candidates.size(), " candidate rooms matching ", property);
        try {
            return calculateFreeSlots(session_token, candidates, from, to);
        } catch (DbQueryException e) {
            log.log_Error("For [", session_token, "]: Problem accessing records whilst processing building [", building_id, "] search between ", from, " and ", to, " for ", property); //TODO
            throw new FailedDbFetch("Problem occurred trying to access the records during a building [" + building_id + "] search.", e);
        }
    }

    /**
     * Searches for available rooms anywhere
     *
     * @param session_token Session token
     * @param from          Beginning timestamp to search from
     * @param to            End timestamp to search up to
     * @param property      Minimum requirements for the room
     * @return Availability as a Map of the free rooms with their associated list of time spans where the room is free
     * @throws FailedDbFetch           when an error occurred getting the record
     * @throws SessionExpiredException When the session for the id provided has expired
     * @throws SessionInvalidException When the session for the id provided does not exist in the tracker
     */
    public HashMap<Room, List<TimeSpan>> search(Token session_token, Date from, Date to, RoomProperty property) throws FailedDbFetch, SessionExpiredException, SessionInvalidException {
        List<Room> candidates = search(session_token, property);
        log.log_Debug("For [", session_token, "]: global search found ", candidates.size(), " candidate rooms matching ", property);
        try {
            return calculateFreeSlots(session_token, candidates, from, to);
        } catch (DbQueryException e) {
            log.log_Error("For [", session_token, "]: Problem accessing records whilst processing global search between ", from, " and ", to, " for ", property); //TODO
            throw new FailedDbFetch("Problem occurred trying to access the records during a global search.", e);
        }
    }
}
