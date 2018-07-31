package bbk_beam.mtRooms.common;

import bbk_beam.mtRooms.db.TimestampConverter;

import java.io.Serializable;
import java.sql.Date;
import java.time.Instant;

public class TimeSpan implements Comparable<TimeSpan>, Serializable {
    private TimestampUTC start;
    private TimestampUTC end;

    /**
     * Constructor (default)
     * Both start/end timestamps are set to UTC epoch
     */
    public TimeSpan() {
        this.start = new TimestampUTC(TimestampConverter.getUTCTimestampString(Date.from(Instant.EPOCH)));
        this.end = new TimestampUTC(TimestampConverter.getUTCTimestampString(Date.from(Instant.EPOCH)));
    }

    /**
     * Constructor
     *
     * @param start Start timestamp string (UTC)
     * @param end   End timestamp string (UTC)
     */
    public TimeSpan(String start, String end) {
        this.start = new TimestampUTC(start);
        this.end = new TimestampUTC(end);
    }

    /**
     * Constructor
     *
     * @param start Start TimeStampUTC
     * @param end   End TimeStampUTC
     */
    public TimeSpan(TimestampUTC start, TimestampUTC end) {
        this.start = start;
        this.end = end;
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
     * Sets the start timestamp
     *
     * @param date Date object
     */
    protected void setStart(Date date) {
        this.start = new TimestampUTC(TimestampConverter.getUTCTimestampString(date));
    }

    /**
     * Sets the end timestamp
     *
     * @param date Date object
     */
    protected void setEnd(Date date) {
        this.end = new TimestampUTC(TimestampConverter.getUTCTimestampString(date));
    }

    /**
     * Compares the TimeSpan against another
     *
     * @param that TimeSpan to compare against
     * @return (- 1) when span ends before the other, (0) when spans overlaps with other, (1) when span starts after the other
     */
    @Override
    public int compareTo(TimeSpan that) {
        if (this.end().compareTo(that.start()) <= 0)
            return -1;
        if (this.start().compareTo(that.end()) >= 0)
            return 1;
        return 0;
    }

    @Override
    public String toString() {
        return "TimeSpan={ start: " + start + ", end: " + end + " }";
    }
}
