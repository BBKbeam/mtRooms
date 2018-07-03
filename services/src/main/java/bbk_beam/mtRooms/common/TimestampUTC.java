package bbk_beam.mtRooms.common;

import eadjlib.logger.Logger;

import java.io.Serializable;
import java.util.Objects;

public class TimestampUTC implements Comparable<TimestampUTC>, Serializable {
    private final Logger log = Logger.getLoggerInstance(TimestampUTC.class.getName());
    private String timestamp;

    /**
     * Constructor
     *
     * @param utc_timestamp UTC timestamp string
     */
    public TimestampUTC(String utc_timestamp) {
        this.timestamp = utc_timestamp;
    }

    /**
     * Gets the timestamp string
     *
     * @return UTC timestamp string
     */
    public String get() {
        return this.timestamp;
    }

    /**
     * Gets the year on the timestamp
     *
     * @return YYYY
     */
    public Integer year() {
        return Integer.parseInt(this.timestamp.substring(0, 4));
    }

    /**
     * Gets the month on the timestamp
     *
     * @return MM
     */
    public Integer month() {
        return Integer.parseInt(this.timestamp.substring(5, 7));
    }

    /**
     * Gets the day on the timestamp
     *
     * @return DD
     */
    public Integer day() {
        return Integer.parseInt(this.timestamp.substring(8, 10));
    }

    /**
     * Get the hours on the timestamp
     *
     * @return hh
     */
    public Integer hours() {
        return Integer.parseInt(this.timestamp.substring(11, 13));
    }

    /**
     * Gets the minutes on the timestamp
     *
     * @return mm
     */
    public Integer minutes() {
        return Integer.parseInt(this.timestamp.substring(14, 16));
    }

    /**
     * Gets the seconds on the timestamp
     *
     * @return ss
     */
    public Integer seconds() {
        return Integer.parseInt(this.timestamp.substring(17, 19));
    }

    /**
     * Comparison method
     *
     * @param that TimestampUTC to compare to
     * @return (- 1) for less than, (0) for equal, (+1) for larger than
     */
    public int compareTo(TimestampUTC that) {
        Integer this_YYYY = Integer.parseInt(this.timestamp.substring(0, 4));
        Integer that_YYYY = Integer.parseInt(that.timestamp.substring(0, 4));
        if (this_YYYY < that_YYYY)
            return -1;
        if (this_YYYY > that_YYYY)
            return 1;
        Integer this_MM = Integer.parseInt(this.timestamp.substring(5, 7));
        Integer that_MM = Integer.parseInt(that.timestamp.substring(5, 7));
        if (this_MM < that_MM)
            return -1;
        if (this_MM > that_MM)
            return 1;
        Integer this_dd = Integer.parseInt(this.timestamp.substring(8, 10));
        Integer that_dd = Integer.parseInt(that.timestamp.substring(8, 10));
        if (this_dd < that_dd)
            return -1;
        if (this_dd > that_dd)
            return 1;
        Integer this_hh = Integer.parseInt(this.timestamp.substring(11, 13));
        Integer that_hh = Integer.parseInt(that.timestamp.substring(11, 13));
        if (this_hh < that_hh)
            return -1;
        if (this_hh > that_hh)
            return 1;
        Integer this_mm = Integer.parseInt(this.timestamp.substring(14, 16));
        Integer that_mm = Integer.parseInt(that.timestamp.substring(14, 16));
        if (this_mm < that_mm)
            return -1;
        if (this_mm > that_mm)
            return 1;
        Integer this_ss = Integer.parseInt(this.timestamp.substring(17, 19));
        Integer that_ss = Integer.parseInt(that.timestamp.substring(17, 19));
        if (this_ss < that_ss)
            return -1;
        if (this_ss > that_ss)
            return 1;
        return 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TimestampUTC timestamp1 = (TimestampUTC) o;
        return Objects.equals(timestamp, timestamp1.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(timestamp);
    }

    @Override
    public String toString() {
        return "{ " + timestamp + " }";
    }
}
