package bbk_beam.mtRooms.ui.model.common;

import bbk_beam.mtRooms.common.TimestampUTC;
import eadjlib.logger.Logger;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

public class YearStamp implements Comparable<YearStamp> {
    private final Logger log = Logger.getLoggerInstance(TimestampUTC.class.getName());
    static private final SimpleDateFormat dateFormat = new SimpleDateFormat("YYYY");
    private String year_stamp;

    /**
     * Constructor
     *
     * @param date Date object
     */
    public YearStamp(Date date) {
        this.year_stamp = dateFormat.format(date);
    }

    /**
     * Gets the year on the date
     *
     * @return YYYY
     */
    public Integer year() {
        return Integer.parseInt(this.year_stamp.substring(0, 4));
    }

    @Override
    public int compareTo(YearStamp that) {
        Integer this_YYYY = Integer.parseInt(this.year_stamp.substring(0, 4));
        Integer that_YYYY = Integer.parseInt(that.year_stamp.substring(0, 4));
        if (this_YYYY < that_YYYY)
            return -1;
        if (this_YYYY > that_YYYY)
            return 1;
        return 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        YearStamp yearStamp = (YearStamp) o;
        return Objects.equals(year_stamp, yearStamp.year_stamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(year_stamp);
    }

    @Override
    public String toString() {
        return String.valueOf(year());
    }
}
