package bbk_beam.mtRooms.common;

import bbk_beam.mtRooms.db.TimestampConverter;
import eadjlib.datastructure.AVLTree;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * TimeSpan allocation tracker class
 *
 * @param <U> Object type to track
 * @param <T> TimeSpan type used for tracking time intervals
 */
public class TimeSpanAllocation<U, T extends TimeSpan> implements Serializable {
    protected TimeSpanInterval interval;
    protected HashMap<U, AVLTree<TimestampUTC, T>> allocations = new HashMap<>();

    /**
     * Work out the number of intervals between two interval rounded time expressed as numbers of milliseconds since EPOCH
     *
     * @param start Start time
     * @param end   End time
     * @return Number of intervals
     */
    private Long intervalCount(Long start, Long end) {
        return TimeUnit.MILLISECONDS.toMinutes(Math.abs(end - start)) / this.interval.mns();
    }

    /**
     * Default constructor
     * <p>
     * Default slot interval = 30mns
     * </p>
     */
    public TimeSpanAllocation() {
        this(TimeSpanInterval.HALF_HOUR);
    }

    /**
     * Constructor
     *
     * @param interval Slot interval granularity
     */
    public TimeSpanAllocation(TimeSpanInterval interval) {
        this.interval = interval;
    }

    /**
     * Work out the number of intervals between two interval rounded dates
     *
     * @param from Start date
     * @param to   End date
     * @return Number of intervals
     */
    protected Long intervalCount(Date from, Date to) {
        return intervalCount(from.getTime(), to.getTime());
    }

    /**
     * Work out the number of intervals in a interval rounded TimeSpan
     *
     * @param time_span TimeSpan
     * @return Number of intervals
     */
    protected Long intervalCount(TimeSpan time_span) {
        return intervalCount(
                Timestamp.valueOf(time_span.start().get()).getTime(),
                Timestamp.valueOf(time_span.end().get()).getTime()
        );
    }

    /**
     * Creates a list of time spans covering the range given
     *
     * @param span TimeSpan range to break into interval spans
     * @return List of time spans of length=interval
     */
    protected List<TimeSpan> convertToUTCSlotIntervals(TimeSpan span) {
        Long start = this.interval.floorToInterval(span.start().getTime());
        Long n = intervalCount(span);

        List<TimeSpan> spans = new ArrayList<>();
        Long slot_start = start;
        Long slot_end = slot_start + TimeUnit.MINUTES.toMillis(this.interval.mns());
        for (long i = 0; i < n; i++) {
            TimeSpan span_slot = new TimeSpan(
                    TimestampConverter.getUTCTimestampString(slot_start),
                    TimestampConverter.getUTCTimestampString(slot_end)
            );
            spans.add(span_slot);
            slot_start = slot_end;
            slot_end = slot_start + TimeUnit.MINUTES.toMillis(this.interval.mns());
        }
        return spans;
    }

    /**
     * Creates a list of time spans covering the range given
     *
     * @param from Start timestamp of the time slot
     * @param to   End timestamp of the time slot
     * @return List of time spans of length=interval
     */
    protected List<TimeSpan> convertToUTCSlotIntervals(Date from, Date to) {
        Date start_date = this.interval.floorToInterval(from);
        Date end_date = this.interval.ceilToInterval(to);
        Long n = intervalCount(start_date, end_date);

        List<TimeSpan> spans = new ArrayList<>();
        Date slot_start = start_date;
        Date slot_end = new Date(slot_start.getTime() + TimeUnit.MINUTES.toMillis(this.interval.mns()));
        for (long i = 0; i < n; i++) {
            TimeSpan span = new TimeSpan(
                    TimestampConverter.getUTCTimestampString(slot_start),
                    TimestampConverter.getUTCTimestampString(slot_end)
            );
            spans.add(span);
            slot_start = slot_end;
            slot_end = new Date(slot_start.getTime() + TimeUnit.MINUTES.toMillis(this.interval.mns()));
        }
        return spans;
    }

    /**
     * Clears all items in the allocations store
     */
    protected void clear() {
        this.allocations.clear();
    }

    /**
     * Gets the empty state of the allocations store
     *
     * @return Empty state
     */
    protected boolean isEmpty() {
        return this.allocations.isEmpty();
    }

    /**
     * Gets the number of objects in the allocation store
     *
     * @return Number of objects in the allocation store
     */
    protected int size() {
        return this.allocations.size();
    }

    /**
     * Gets the number of slots allocated for an object
     *
     * @param object Allocation object
     * @return Allocated slot count
     */
    protected int allocatedSlotCount(U object) {
        if (this.allocations.containsKey(object))
            return this.allocations.get(object).size();
        else
            return 0;
    }

    @Override
    public String toString() {
        String sb = "TimeSpanAllocation{\n" +
                "\tinterval=" + interval + ",\n" +
                "\tallocations=" + allocations + "\n" +
                "}";
        return sb;
    }
}
