package bbk_beam.mtRooms.reservation.scheduling.datastructure;

import bbk_beam.mtRooms.admin.authentication.Token;
import bbk_beam.mtRooms.db.TimestampConverter;
import bbk_beam.mtRooms.reservation.dto.Room;
import bbk_beam.mtRooms.reservation.scheduling.timing.ScheduleSlotInterval;
import bbk_beam.mtRooms.reservation.scheduling.timing.TimestampUTC;
import eadjlib.datastructure.AVLTree;
import eadjlib.exception.UndefinedException;
import eadjlib.logger.Logger;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class Schedule {
    private final Logger log = Logger.getLoggerInstance(Schedule.class.getName());
    private HashMap<Room, AVLTree<TimestampUTC, ScheduleSlot>> cache = new HashMap<>();
    private ScheduleSlotInterval timeSlot_increment;

    /**
     * Work out the number of intervals between two interval rounded dates
     *
     * @param from Start date
     * @param to   End date
     * @return Number of intervals
     */
    private Long intervalCount(Date from, Date to) {
        Long start = from.getTime();
        Long end = to.getTime();
        Long res = TimeUnit.MILLISECONDS.toMinutes(Math.abs(end - start));
        return (res / this.timeSlot_increment.mns());
    }

    /**
     * Creates a list of schedule slots covering the range given
     *
     * @param watcher_token Watcher's session token
     * @param from          Start timestamp of the time slot
     * @param to            End timestamp of the time slot
     * @return List of schedule slots of length=timeslot_increment
     */
    private List<ScheduleSlot> convertToUTCSlotIntervals(Token watcher_token, Date from, Date to) {
        Date start_date = this.timeSlot_increment.floorToInterval(from);
        Date end_date = this.timeSlot_increment.ceilToInterval(to);
        Long n = intervalCount(start_date, end_date);
        log.log_Debug("Creating ", n, " intervals between ", start_date, " and ", end_date, ".");

        List<ScheduleSlot> slots = new ArrayList<>();
        Date slot_start = start_date;
        Date slot_end = new Date(slot_start.getTime() + TimeUnit.MINUTES.toMillis(this.timeSlot_increment.mns()));
        for (long i = 0; i < n; i++) {
            ScheduleSlot slot = new ScheduleSlot(
                    watcher_token,
                    TimestampConverter.getUTCTimestampString(slot_start),
                    TimestampConverter.getUTCTimestampString(slot_end)
            );
            log.log_Trace("Adding slot: ", slot);
            slots.add(slot);
            slot_start = slot_end;
            slot_end = new Date(slot_start.getTime() + TimeUnit.MINUTES.toMillis(this.timeSlot_increment.mns()));
        }
        return slots;
    }

    /**
     * Constructor
     */
    public Schedule() {
        this(ScheduleSlotInterval.HALF_HOUR); //Default
    }

    /**
     * Constructor
     *
     * @param timeSlot_increment TimeSlot increment in size
     */
    public Schedule(ScheduleSlotInterval timeSlot_increment) {
        this.timeSlot_increment = timeSlot_increment;
    }

    /**
     * Adds a Room's schedule slot to the cache
     *
     * @param watcher_token Watcher token
     * @param room          Room DTO
     * @param from          Start timestamp of the time slot
     * @param to            End timestamp of the time stamp
     */
    public void addSlot(Token watcher_token, Room room, Date from, Date to) {
        try {
            log.log_Debug("Adding schedule slot for ", watcher_token, " between ", from, " -> ", to, " for ", room);
            for (ScheduleSlot slot : convertToUTCSlotIntervals(watcher_token, from, to)) {
                boolean room_exists = this.cache.containsKey(room);
                if (!room_exists)
                    this.cache.putIfAbsent(room, new AVLTree<>());
                if (this.cache.get(room).search(slot.start())) {
                    log.log_Debug("Adding watcher [", watcher_token, "] to ", room, ": ", slot);
                    ScheduleSlot new_slot = this.cache.get(room).apply(slot.start(), (ScheduleSlot s) -> {
                        if (!s.watchers().contains(watcher_token))
                            s.addWatcher(watcher_token);
                        return s;
                    });
                } else {
                    this.cache.get(room).add(slot.start(), slot);
                }
            }
        } catch (UndefinedException e) {
            log.log_Fatal("Error detected in the schedule slot data-structure in cache of Room: ", room);
            log.log_Exception(e);
        }
    }

    /**
     * Gets the number of overlapping slots based on the views from concurrent sessions
     *
     * @param room Room DTO
     * @param from Start timestamp of the time slot
     * @param to   End timestamp of the time slot
     * @return Number of overlapping views on the slot
     */
    public Integer getSlotOverlapCount(Room room, Date from, Date to) {
        ScheduleSlot slot = new ScheduleSlot(
                TimestampConverter.getUTCTimestampString(from),
                TimestampConverter.getUTCTimestampString(to)
        );
        if (!this.cache.containsKey(room)) return 0;
        Collection<ScheduleSlot> slots = cache.get(room).searchValues((ScheduleSlot t) -> (t.compareTo(slot) == 0));
        HashMap<String, Token> watchers = new HashMap<>();
        for (ScheduleSlot s : slots) {
            for (Token t : s.watchers())
                watchers.putIfAbsent(t.getSessionId(), t);
        }
        return watchers.size();
    }

    /**
     * Gets a list of watchers for the specified room + time slot
     *
     * @param room Room
     * @param from Start timestamp of the time slot
     * @param to   End timestamp of the time slot
     * @return List of watcher tokens
     */
    public Collection<Token> getWatchers(Room room, Date from, Date to) {
        ScheduleSlot slot = new ScheduleSlot(
                TimestampConverter.getUTCTimestampString(this.timeSlot_increment.floorToInterval(from)),
                TimestampConverter.getUTCTimestampString(this.timeSlot_increment.ceilToInterval(to))
        );
        try {
            if (!this.cache.containsKey(room)) return Collections.emptyList();
            return cache.get(room).getValue(slot.start()).watchers();
        } catch (NullPointerException e) {
            log.log("Tried to get list of watchers from a un-cached Room (", room, ", ) slot: ", slot);
            return Collections.emptyList();
        }
    }

    /**
     * Clears watcher from a room
     *
     * @param token Watcher session token
     * @param room  Room
     */
    public void clearWatcherCache(Token token, Room room) {
        List<ScheduleSlot> toDelete = new LinkedList<>();
        AVLTree.AVLTreeIterator it = (AVLTree.AVLTreeIterator) this.cache.get(room).iterator();
        while (it.hasNext()) { //Going through slots for Room looking for any matching token
            ScheduleSlot slot = ((ScheduleSlot) it.next().value());
            if (slot.removeWatcher(token))
                log.log_Trace("Cleared watcher ", token, " from cache in ", slot, " from ", room);
            if (slot.watcherCount() == 0) {
                toDelete.add(slot);
            }
        }
        for (ScheduleSlot slot : toDelete) {
            try {
                this.cache.get(room).remove(slot.start());
                log.log_Trace("Cleared ", slot, " from cache as no more watchers on it.");
            } catch (UndefinedException e) {
                log.log_Error("Tried to remove a non-existent slot (", slot, ") in cache for Room: ", room);
                log.log_Exception(e);
            }
        }
        if (this.cache.get(room).isEmpty()) {
            this.cache.remove(room);
            log.log_Trace("Cleared room ", room, " from cache as no more slots on it.");
        }
    }

    /**
     * Clears watcher and any items subsequently unwatched from the cache
     *
     * @param token Watcher session token
     */
    public void clearWatcherCache(Token token) {
        for (Map.Entry<Room, AVLTree<TimestampUTC, ScheduleSlot>> entry : cache.entrySet()) {
            clearWatcherCache(token, entry.getKey());
        }
    }

    /**
     * Clears all items in the cache
     */
    public void clearCache() {
        log.log("Clearing schedule cache...");
        this.cache.clear();
    }

    /**
     * Gets the empty state of the cache
     *
     * @return Empty state
     */
    public boolean cacheIsEmpty() {
        return this.cache.isEmpty();
    }

    /**
     * Gets the number of rooms cached
     *
     * @return Cached room count
     */
    public int cacheSize() {
        return this.cache.size();
    }

    /**
     * Gets the number of slots cached for a room
     *
     * @param room Room
     * @return Cached slots count
     */
    public int cachedSlotsCount(Room room) {
        if (this.cache.containsKey(room))
            return this.cache.get(room).size();
        else
            return 0;
    }

}
