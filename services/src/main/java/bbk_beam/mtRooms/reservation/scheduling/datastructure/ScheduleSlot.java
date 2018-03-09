package bbk_beam.mtRooms.reservation.scheduling.datastructure;

import bbk_beam.mtRooms.admin.authentication.Token;
import bbk_beam.mtRooms.reservation.scheduling.timing.TimestampUTC;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Schedule-slot holder class
 */
class ScheduleSlot implements Comparable<ScheduleSlot> {
    private TimestampUTC start;
    private TimestampUTC end;
    private List<Token> watchers;

    /**
     * Constructor
     *
     * @param start Start timestamp string (UTC)
     * @param end   End timestamp string (UTC)
     */
    public ScheduleSlot(String start, String end) {
        this.start = new TimestampUTC(start);
        this.end = new TimestampUTC(end);
        this.watchers = new ArrayList<>();
    }

    /**
     * Constructor
     *
     * @param watcher_token Watcher's session ID
     * @param start         Start timestamp string (UTC)
     * @param end           End timestamp string (UTC)
     */
    public ScheduleSlot(Token watcher_token, String start, String end) {
        this.start = new TimestampUTC(start);
        this.end = new TimestampUTC(end);
        this.watchers = new ArrayList<>();
        this.watchers.add(watcher_token);
    }

    /**
     * Gets the start TimestampUTC of the schedule slot
     *
     * @return Start TimestampUTC
     */
    public TimestampUTC start() {
        return this.start;
    }

    /**
     * Gets the end TimestampUTC of the schedule slot
     *
     * @return End TimestampUTC
     */
    public TimestampUTC end() {
        return this.end;
    }

    /**
     * Adds a watcher to this exact schedule slot
     *
     * @param watcher_token
     * @return Watcher list changed status
     */
    public boolean addWatcher(Token watcher_token) {
        return this.watchers.add(watcher_token);
    }

    /**
     * Removes a watcher from the watcher list
     *
     * @param watcher_token Watcher token to remove
     * @return Removal status
     */
    public boolean removeWatcher(Token watcher_token) {
        return this.watchers.remove(watcher_token);
    }

    /**
     * Gets the number of watchers of this exact schedule slot
     *
     * @return Watcher count
     */
    public int watcherCount() {
        return this.watchers.size();
    }

    /**
     * Gets a read-only view of the watchers
     *
     * @return List of watchers on the schedule slot
     */
    public Collection<Token> watchers() {
        return Collections.unmodifiableList(this.watchers);
    }

    /**
     * Compares the ScheduleSlot against another
     *
     * @param that ScheduleSlot to compare against
     * @return (- 1) when slot ends before the other, (0) when slot overlaps with other, (1) when slot starts after the other
     */
    @Override
    public int compareTo(ScheduleSlot that) {
        if (this.end().compareTo(that.start()) <= 0)
            return -1;
        if (this.start().compareTo(that.end()) >= 0)
            return 1;
        return 0;
    }

    @Override
    public String toString() {
        return "ScheduleSlot={ start: " + start + ", end: " + end + ", watchers=" + watchers + " }";
    }
}
