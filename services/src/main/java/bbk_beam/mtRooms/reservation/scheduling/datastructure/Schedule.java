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
    HashMap<Room, AVLTree<TimestampUTC, ScheduleSlot>> cache = new HashMap<>();
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


        //TODO do time increment slot creation
        //TODO work out logic of time boundaries
        return slots;
    }

    private Integer intervalCount(String utc_from, String utc_to) {
//        Integer this_YYYY = Integer.parseInt(this.timestamp.substring(0, 4));
//        Integer this_MM = Integer.parseInt(this.timestamp.substring(5, 7));
//        Integer this_dd = Integer.parseInt(this.timestamp.substring(8, 10));
//        Integer this_hh = Integer.parseInt(this.timestamp.substring(11, 13));
//        Integer this_mm = Integer.parseInt(this.timestamp.substring(14, 16));
//        Integer this_ss = Integer.parseInt(this.timestamp.substring(17, 19));
//        Integer that_ss = Integer.parseInt(that.timestamp.substring(17, 19));
        return null;
    }

    /**
     * Constructor
     */
    Schedule() {
        this(ScheduleSlotInterval.HALF_HOUR); //Default
    }

    /**
     * Constructor
     *
     * @param timeSlot_increment TimeSlot increment in size
     */
    Schedule(ScheduleSlotInterval timeSlot_increment) {
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
    void addSlot(Token watcher_token, Room room, Date from, Date to) {
        try {
            log.log_Debug("Adding schedule slot for ", watcher_token, " between ", from, " -> ", to, " for ", room);
            for (ScheduleSlot slot : convertToUTCSlotIntervals(watcher_token, from, to)) {
                boolean room_exists = this.cache.containsKey(room);
                if (!room_exists)
                    this.cache.putIfAbsent(room, new AVLTree<>());
                if (this.cache.get(room).search(slot.start())) {
                    log.log_Debug("Adding watcher for ", room, ": ", slot);
                    ScheduleSlot new_slot = this.cache.get(room).apply(slot.start(), (ScheduleSlot s) -> {
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
    Integer getSlotOverlapCount(Room room, Date from, Date to) {
        //TODO work out slot intervals, then check overlaps (unique token ids)
        ScheduleSlot slot = new ScheduleSlot(
                TimestampConverter.getUTCTimestampString(from),
                TimestampConverter.getUTCTimestampString(to)
        );
        if (!this.cache.containsKey(room)) return 0;
        return cache.get(room).searchValues((ScheduleSlot t) -> (t.compareTo(slot) == 0)).size();
    }

    /**
     * Gets a list of watchers for the specified room + time slot
     *
     * @param room Room
     * @param from Start timestamp of the time slot
     * @param to   End timestamp of the time slot
     * @return List of watcher tokens
     */
    Collection<Token> getWatchers(Room room, Date from, Date to) {
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
     * Clears watcher and any items subsequently unwatched from the cache
     *
     * @param token Watcher session token
     */
    void clearWatcherCache(Token token) {
        List<Room> toDelete = new LinkedList<>();
        for (Map.Entry<Room, AVLTree<TimestampUTC, ScheduleSlot>> entry : cache.entrySet()) {
            AVLTree.AVLTreeIterator it = (AVLTree.AVLTreeIterator) entry.getValue().iterator();
            while (it.hasNext()) {
                ScheduleSlot slot = ((ScheduleSlot) it.next().value());
                if (slot.removeWatcher(token))
                    log.log_Trace("Removed watcher ", token, " from cache in ", slot, " from ", entry.getKey());
                if (slot.watcherCount() == 0) {
                    toDelete.add(entry.getKey());
                }
            }
        }
        for (Room room : toDelete) {
            this.cache.remove(room);
        }
    }

    /**
     * Clears all items in the cache
     */
    void clearCache() {
        this.cache.clear();
    }


}
