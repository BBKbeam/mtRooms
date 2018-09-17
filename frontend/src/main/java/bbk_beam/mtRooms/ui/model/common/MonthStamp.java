package bbk_beam.mtRooms.ui.model.common;

import bbk_beam.mtRooms.common.TimestampUTC;
import eadjlib.logger.Logger;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

public class MonthStamp implements Comparable<MonthStamp> {
    private final Logger log = Logger.getLoggerInstance(TimestampUTC.class.getName());
    static private final SimpleDateFormat dateFormat = new SimpleDateFormat("YYYY-MM");
    private String month_stamp;

    /**
     * Constructor
     *
     * @param date Date object
     */
    public MonthStamp(Date date) {
        this.month_stamp = dateFormat.format(date);
    }

    /**
     * Gets the year on the date
     *
     * @return YYYY
     */
    public Integer year() {
        return Integer.parseInt(this.month_stamp.substring(0, 4));
    }

    /**
     * Gets the month on the date
     *
     * @return MM
     */
    public Integer month() {
        return Integer.parseInt(this.month_stamp.substring(5, 7));
    }

    @Override
    public int compareTo(MonthStamp that) {
        Integer this_YYYY = Integer.parseInt(this.month_stamp.substring(0, 4));
        Integer that_YYYY = Integer.parseInt(that.month_stamp.substring(0, 4));
        if (this_YYYY < that_YYYY)
            return -1;
        if (this_YYYY > that_YYYY)
            return 1;
        Integer this_MM = Integer.parseInt(this.month_stamp.substring(5, 7));
        Integer that_MM = Integer.parseInt(that.month_stamp.substring(5, 7));
        if (this_MM < that_MM)
            return -1;
        if (this_MM > that_MM)
            return 1;
        return 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MonthStamp that = (MonthStamp) o;
        return Objects.equals(month_stamp, that.month_stamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(month_stamp);
    }

    @Override
    public String toString() {
        return String.format("%02d", month()) + "-" + year();
    }
}
