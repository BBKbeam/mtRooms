package bbk_beam.mtRooms.common;

import java.util.Calendar;
import java.util.Date;

public enum TimeSpanInterval {
    QUARTER_HOUR(15),
    HALF_HOUR(30),
    HOUR(60);

    private final Integer minutes;

    /**
     * Constructor
     *
     * @param interval_minutes Number of minutes in interval
     */
    TimeSpanInterval(Integer interval_minutes) {
        this.minutes = interval_minutes;
    }

    /**
     * Gets the interval's size in minutes
     *
     * @return Interval size
     */
    public Integer mns() {
        return this.minutes;
    }

    /**
     * Gets the interval ceiling of a Date object (nearest interval time rounded up)
     *
     * @param date Date
     * @return Date interval ceil
     */
    public Date ceilToInterval(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        Integer mns = cal.get(Calendar.MINUTE);
        Integer frac_increment = mns % this.mns();
        Boolean remainder = frac_increment > 0 || cal.get(Calendar.SECOND) > 0 || cal.get(Calendar.MILLISECOND) > 0;
        Integer full_increment = mns / this.mns();
        Integer round_to = remainder
                ? (full_increment + 1) * this.mns()
                : full_increment * this.mns();
        cal.set(Calendar.MINUTE, round_to);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    /**
     * Gets the interval floor of a Date object (nearest interval time rounded down)
     *
     * @param date Date
     * @return Date interval floor
     */
    public Date floorToInterval(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        Integer mns = cal.get(Calendar.MINUTE);
        Integer full_increment = mns / this.mns();
        Integer round_to = full_increment * this.mns();
        cal.set(Calendar.MINUTE, round_to);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    /**
     * Gets the nearest interval date of a Date object
     *
     * @param date Date
     * @return Rounded Date interval
     */
    public Date roundToNearestInterval(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        Integer mns = cal.get(Calendar.MINUTE);
        Boolean ss_roundup = (cal.get(Calendar.SECOND) / 30) >= 1;
        Integer frac_increment = mns % this.mns();
        Integer full_increment = mns / this.mns();
        Integer half_increment = this.mns() / 2;
        Integer round_to = (frac_increment > half_increment || (frac_increment.compareTo(half_increment) == 0 && ss_roundup))
                ? (full_increment + 1) * this.mns()
                : full_increment * this.mns();
        cal.set(Calendar.MINUTE, round_to);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }
}
