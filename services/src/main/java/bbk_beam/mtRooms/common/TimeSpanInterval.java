package bbk_beam.mtRooms.common;

import java.sql.Timestamp;
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
     * Gets the interval ceiling of a Calendar object (nearest interval time rounded up)
     *
     * @param calendar Calendar instance
     * @return Calendar's interval ceiling
     */
    public Calendar ceilToInterval(Calendar calendar) {
        Integer mns = calendar.get(Calendar.MINUTE);
        Integer frac_increment = mns % this.mns();
        Boolean remainder = frac_increment > 0 || calendar.get(Calendar.SECOND) > 0 || calendar.get(Calendar.MILLISECOND) > 0;
        Integer full_increment = mns / this.mns();
        Integer round_to = remainder
                ? (full_increment + 1) * this.mns()
                : full_increment * this.mns();
        calendar.set(Calendar.MINUTE, round_to);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar;
    }

    /**
     * Gets the interval ceiling of a Date object (nearest interval time rounded up)
     *
     * @param date Date
     * @return Date's interval ceiling
     */
    public Date ceilToInterval(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return ceilToInterval(calendar).getTime();
    }

    /**
     * Gets the interval ceiling of a time (nearest interval time rounded up)
     *
     * @param time Time expressed as numbers of milliseconds UTC since EPOCH
     * @return Time's interval ceiling
     */
    public Long ceilToInterval(Long time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        return ceilToInterval(calendar).getTimeInMillis();
    }

    /**
     * Gets the interval ceiling of a TimeStampUTC (nearest interval time rounded up)
     *
     * @param timestampUTC TimeStampUTC instance
     * @return TimeStampUTC's interval ceiling
     */
    public TimestampUTC ceilToInterval(TimestampUTC timestampUTC) {
        Long time = Timestamp.valueOf(timestampUTC.get()).getTime();
        Timestamp timestamp = new Timestamp(ceilToInterval(time));
        return new TimestampUTC(timestamp.toString());
    }

    /**
     * Gets the interval floor of a Calendar object (nearest interval time rounded down)
     *
     * @param calendar Calendar instance
     * @return Calendar's interval floor
     */
    public Calendar floorToInterval(Calendar calendar) {
        Integer mns = calendar.get(Calendar.MINUTE);
        Integer full_increment = mns / this.mns();
        Integer round_to = full_increment * this.mns();
        calendar.set(Calendar.MINUTE, round_to);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar;
    }

    /**
     * Gets the interval floor of a Date object (nearest interval time rounded down)
     *
     * @param date Date
     * @return Date interval floor
     */
    public Date floorToInterval(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return floorToInterval(calendar).getTime();
    }

    /**
     * Gets the interval floor of a time (nearest interval time rounded down)
     *
     * @param time Time expressed as numbers of milliseconds UTC since EPOCH
     * @return Time's interval floor
     */
    public Long floorToInterval(Long time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        return floorToInterval(calendar).getTimeInMillis();
    }

    /**
     * Gets the interval floor of a TimeStampUTC (nearest interval time rounded down)
     *
     * @param timestampUTC TimeStampUTC instance
     * @return TimeStampUTC's interval floor
     */
    public TimestampUTC floorToInterval(TimestampUTC timestampUTC) {
        Long time = Timestamp.valueOf(timestampUTC.get()).getTime();
        Timestamp timestamp = new Timestamp(floorToInterval(time));
        return new TimestampUTC(timestamp.toString());
    }

    /**
     * Gets the nearest interval of a Calendar object
     *
     * @param calendar Calendar instance
     * @return Calendar's nearest interval
     */
    public Calendar roundToNearestInterval(Calendar calendar) {
        Integer mns = calendar.get(Calendar.MINUTE);
        Boolean ss_roundup = (calendar.get(Calendar.SECOND) / 30) >= 1;
        Integer frac_increment = mns % this.mns();
        Integer full_increment = mns / this.mns();
        Integer half_increment = this.mns() / 2;
        Integer round_to = (frac_increment > half_increment || (frac_increment.compareTo(half_increment) == 0 && ss_roundup))
                ? (full_increment + 1) * this.mns()
                : full_increment * this.mns();
        calendar.set(Calendar.MINUTE, round_to);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar;
    }

    /**
     * Gets the nearest interval date of a Date object
     *
     * @param date Date
     * @return Rounded Date interval
     */
    public Date roundToNearestInterval(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return roundToNearestInterval(calendar).getTime();
    }

    /**
     * Gets the nearest interval time of a time
     *
     * @param time Time expressed as numbers of milliseconds UTC since EPOCH
     * @return Time's nearest interval
     */
    public Long roundToNearestInterval(Long time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        return floorToInterval(calendar).getTimeInMillis();
    }

    /**
     * Gets the nearest interval time of a TimeStampUTC
     *
     * @param timestampUTC TimeStampUTC instance
     * @return TimeStampUTC's nearest interval
     */
    public TimestampUTC roundToNearestInterval(TimestampUTC timestampUTC) {
        Long time = Timestamp.valueOf(timestampUTC.get()).getTime();
        Timestamp timestamp = new Timestamp(roundToNearestInterval(time));
        return new TimestampUTC(timestamp.toString());
    }
}
