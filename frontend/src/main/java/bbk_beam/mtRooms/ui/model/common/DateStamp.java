package bbk_beam.mtRooms.ui.model.common;

import bbk_beam.mtRooms.common.TimestampUTC;
import eadjlib.logger.Logger;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

public class DateStamp implements Comparable<DateStamp> {
    private final Logger log = Logger.getLoggerInstance(TimestampUTC.class.getName());
    static private final SimpleDateFormat dateFormat = new SimpleDateFormat("YYYY-MM-dd");
    private String date_stamp;

    /**
     * Constructor
     *
     * @param date Date object
     */
    public DateStamp(Date date) {
        this.date_stamp = dateFormat.format(date);
    }

    /**
     * Gets the date string
     *
     * @return Date string
     */
    public String get() {
        return this.date_stamp;
    }

    /**
     * Gets the year on the date
     *
     * @return YYYY
     */
    public Integer year() {
        return Integer.parseInt(this.date_stamp.substring(0, 4));
    }

    /**
     * Gets the month on the date
     *
     * @return MM
     */
    public Integer month() {
        return Integer.parseInt(this.date_stamp.substring(5, 7));
    }

    /**
     * Gets the day on the date
     *
     * @return DD
     */
    public Integer day() {
        return Integer.parseInt(this.date_stamp.substring(8, 10));
    }

    /**
     * Comparison method
     *
     * @param that DateStamp to compare to
     * @return (- 1) for less than, (0) for equal, (+1) for larger than
     */
    public int compareTo(DateStamp that) {
        Integer this_YYYY = Integer.parseInt(this.date_stamp.substring(0, 4));
        Integer that_YYYY = Integer.parseInt(that.date_stamp.substring(0, 4));
        if (this_YYYY < that_YYYY)
            return -1;
        if (this_YYYY > that_YYYY)
            return 1;
        Integer this_MM = Integer.parseInt(this.date_stamp.substring(5, 7));
        Integer that_MM = Integer.parseInt(that.date_stamp.substring(5, 7));
        if (this_MM < that_MM)
            return -1;
        if (this_MM > that_MM)
            return 1;
        Integer this_dd = Integer.parseInt(this.date_stamp.substring(8, 10));
        Integer that_dd = Integer.parseInt(that.date_stamp.substring(8, 10));
        if (this_dd < that_dd)
            return -1;
        if (this_dd > that_dd)
            return 1;
        return 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DateStamp dateStamp = (DateStamp) o;
        return Objects.equals(date_stamp, dateStamp.date_stamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(date_stamp);
    }

    @Override
    public String toString() {
        return String.format("%02d", day()) + "-" + String.format("%02d", month()) + "-" + year();
    }
}
