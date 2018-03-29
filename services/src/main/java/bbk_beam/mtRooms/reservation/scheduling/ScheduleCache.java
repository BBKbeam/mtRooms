package bbk_beam.mtRooms.reservation.scheduling;

import bbk_beam.mtRooms.admin.authentication.Token;
import bbk_beam.mtRooms.db.TimestampConverter;
import bbk_beam.mtRooms.reservation.ReservationSession;
import bbk_beam.mtRooms.reservation.dto.Room;
import bbk_beam.mtRooms.reservation.dto.RoomReservation;
import bbk_beam.mtRooms.reservation.scheduling.datastructure.Schedule;
import bbk_beam.mtRooms.reservation.scheduling.datastructure.TimeSpan;
import eadjlib.logger.Logger;

import java.util.*;

public class ScheduleCache extends Observable {
    private final Logger log = Logger.getLoggerInstance(ScheduleCache.class.getName());
    private HashMap<String, ReservationSession> observers;
    private Schedule cached_schedule;

    /**
     * Constructor
     */
    public ScheduleCache() {
        this.observers = new HashMap<>();
        this.cached_schedule = new Schedule();
    }

    /**
     * Constructor with Schedule injection
     *
     * @param schedule Schedule data-structure
     */
    public ScheduleCache(Schedule schedule) {
        this.observers = new HashMap<>();
        this.cached_schedule = schedule;
    }

    /**
     * Adds a ReservedRoom to the schedule cache
     *
     * @param watcher_token    Watcher token
     * @param room_reservation RoomReservation DTO
     * @return List of the room's free slots as time spans
     */
    public synchronized List<TimeSpan> add(Token watcher_token, RoomReservation room_reservation) {
        return this.cached_schedule.addSlot(
                watcher_token,
                room_reservation.room(),
                room_reservation.reservationStart(),
                room_reservation.reservationEnd()
        );
    }

    /**
     * Adds a Room + time frame to the schedule cache
     *
     * @param watcher_token Watcher token
     * @param room          Room DTO
     * @param from          Start timestamp of the time frame
     * @param to            End timestamp of the time frame
     * @return List of the room's free slots as time spans
     */
    public synchronized List<TimeSpan> add(Token watcher_token, Room room, Date from, Date to) {
        return this.cached_schedule.addSlot(
                watcher_token,
                room,
                from,
                to
        );
    }

    /**
     * Adds a Room + time frame to the schedule cache
     *
     * @param watcher_token Watcher DTO
     * @param room          Room DTO
     * @param time_span     Time frame span
     * @return List of the room's free slots as time spans
     */
    public synchronized List<TimeSpan> add(Token watcher_token, Room room, TimeSpan time_span) {
        return this.cached_schedule.addSlot(
                watcher_token,
                room,
                TimestampConverter.getDateObject(time_span.start().get()),
                TimestampConverter.getDateObject(time_span.end().get())
        );
    }

    /**
     * Broadcasts a booking of a room during a time span to relevant watchers
     * Note: this will remove the RoomReservation's watcher from the cache automatically
     *
     * @param watcher_token    Watcher token that took the reservation
     * @param room_reservation RoomReservation DTO
     */
    public synchronized void broadcastRoomReservation(Token watcher_token, RoomReservation room_reservation) {
        //Remove reservation watcher
        this.cached_schedule.clearWatcherCache(
                watcher_token,
                room_reservation.room(),
                room_reservation.reservationStart(),
                room_reservation.reservationEnd()
        );
        //Get all remaining watchers for the time span of the reservation
        Collection<Token> observers = this.cached_schedule.getWatchers(
                room_reservation.room(),
                room_reservation.reservationStart(),
                room_reservation.reservationEnd()
        );
        //Broadcasts to all remaining watchers concerned the update
        for (Token token : observers) {
            ReservationSession observer = this.observers.get(token.getSessionId());
            if (observer == null)
                log.log_Error("Found a tracked Token [", token, "] that doesn't have a matching ReservationSession in the list of observers.");
            else
                observer.update(this, room_reservation);
        }
    }


    /**
     * Clears watcher from a room
     *
     * @param token Watcher session token
     * @param room  Room
     */
    public void clearWatcherCache(Token token, Room room) {
        this.cached_schedule.clearWatcherCache(token, room);
    }

    /**
     * Clears watcher and any items subsequently unwatched from the cache
     *
     * @param token Watcher session token
     */
    public void clearWatcherCache(Token token) {
        this.cached_schedule.clearWatcherCache(token);
    }


    /**
     * Adds an observer
     *
     * @param o Observer instance
     * @throws NullPointerException when passing a null object
     * @throws ClassCastException   when Observer is not an instance of ReservationSession
     */
    @Override
    public synchronized void addObserver(Observer o) throws NullPointerException, ClassCastException {
        if (o == null)
            throw new NullPointerException();
        if (o instanceof ReservationSession)
            addObserver((ReservationSession) o);
        else
            throw new ClassCastException("Observer is not an instance of ReservationSession.");
    }

    /**
     * Adds an observer
     *
     * @param o ReservationSession instance
     * @throws NullPointerException when passing a null object
     */
    public synchronized void addObserver(ReservationSession o) throws NullPointerException {
        if (o == null)
            throw new NullPointerException();
        this.observers.putIfAbsent(o.getToken().getSessionId(), o);
    }

    /**
     * Removes an observer
     *
     * @param o Observer instance
     * @throws ClassCastException when Observer is not an instance of ReservationSession
     */
    @Override
    public synchronized void deleteObserver(Observer o) {
        if (o instanceof ReservationSession)
            deleteObserver((ReservationSession) o);
        else
            throw new ClassCastException("Observer is not an instance of ReservationSession.");
    }

    /**
     * Removes an observer
     *
     * @param o ReservationSession instance
     */
    public synchronized void deleteObserver(ReservationSession o) {
        this.cached_schedule.clearWatcherCache(o.getToken());
        this.observers.remove(o.getToken().getSessionId());
    }

    @Override
    public synchronized void deleteObservers() {
        this.cached_schedule.clearCache();
        this.observers.clear();
    }

    @Override
    public synchronized int countObservers() {
        return this.observers.size();
    }
}
