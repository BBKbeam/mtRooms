package bbk_beam.mtRooms.reservation.processing;

import bbk_beam.mtRooms.admin.authentication.Token;
import bbk_beam.mtRooms.db.exception.DbQueryException;
import bbk_beam.mtRooms.db.exception.SessionExpiredException;
import bbk_beam.mtRooms.db.exception.SessionInvalidException;
import bbk_beam.mtRooms.reservation.delegate.ISearch;
import bbk_beam.mtRooms.reservation.dto.Room;
import bbk_beam.mtRooms.reservation.dto.RoomProperty;
import bbk_beam.mtRooms.reservation.exception.FailedDbFetch;
import bbk_beam.mtRooms.reservation.scheduling.ScheduleCache;
import bbk_beam.mtRooms.reservation.scheduling.datastructure.TimeSpan;
import eadjlib.datastructure.ObjectTable;
import eadjlib.logger.Logger;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class OptimisedSearch {
    private final Logger log = Logger.getLoggerInstance(OptimisedSearch.class.getName());
    private ISearch db_delegate;
    private ScheduleCache schedule_cache;

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
    List<Room> search(Token session_token, RoomProperty properties) throws FailedDbFetch, SessionExpiredException, SessionInvalidException {
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
    List<Room> search(Token session_token, Integer building_id, RoomProperty properties) throws FailedDbFetch, SessionExpiredException, SessionInvalidException {
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
    List<Room> search(Token session_token, Integer building_id, Integer floor_id, RoomProperty properties) throws FailedDbFetch, SessionExpiredException, SessionInvalidException {
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
     * @param property      Minimum requirements for the room
     * @return Availability as a list of time spans where the room is free
     * @throws FailedDbFetch           when an error occurred getting the record
     * @throws SessionExpiredException When the session for the id provided has expired
     * @throws SessionInvalidException When the session for the id provided does not exist in the tracker
     */
    List<TimeSpan> search(Token session_token, Room room, Date from, Date to, RoomProperty property) throws FailedDbFetch, SessionExpiredException, SessionInvalidException {
        //TODO
        return null;
    }

    /**
     * Searches for available rooms on a floor within a time frame
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
    HashMap<Room, List<TimeSpan>> search(Token session_token, Integer building_id, Integer floor_id, Date from, Date to, RoomProperty property) throws FailedDbFetch, SessionExpiredException, SessionInvalidException {
        //TODO
        return null;
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
    HashMap<Room, List<TimeSpan>> search(Token session_token, Integer building_id, Date from, Date to, RoomProperty property) throws FailedDbFetch, SessionExpiredException, SessionInvalidException {
        //TODO
        return null;
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
    HashMap<Room, List<TimeSpan>> search(Token session_token, Date from, Date to, RoomProperty property) throws FailedDbFetch, SessionExpiredException, SessionInvalidException {
        //TODO
        return null;
    }
}
