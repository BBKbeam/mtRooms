package bbk_beam.mtRooms.db;

import eadjlib.logger.Logger;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class TimestampConverter {
    private static final Logger log = Logger.getLoggerInstance(TimestampConverter.class.getName());

    /**
     * Gets the UTC timestamp of now
     *
     * @return Now as a database formatted UTC timestamp string
     */
    public static String nowUTC() {
        return new Timestamp(
                Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTimeInMillis()
        ).toString();
    }

    /**
     * Translate a Locale Date object into a database formatted UTC timestamp string
     *
     * @param date Date object
     * @return Formatted database timestamp string
     */
    public static String getUTCTimestampString(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        return format.format(date);
    }

    /**
     * Translate a UTC time into a database formatted UTC timestamp string
     *
     * @param time Time expressed as numbers of milliseconds UTC since EPOCH
     * @return Formatted database timestamp string
     */
    public static String getUTCTimestampString(Long time) {
        return new Timestamp(time).toString();
    }

    /**
     * Translate a UTC timestamp string into a default Locale Date object
     *
     * @param utc_timestamp Timestamp to parse as a Date object
     * @return Date object (if null string was passed then an epoch date object will be returned)
     * @throws RuntimeException when string to Date parsing fails
     */
    public static Date getDateObject(String utc_timestamp) throws RuntimeException {
        if (utc_timestamp == null)
            return Date.from(Instant.EPOCH);
        try {
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            format.setTimeZone(TimeZone.getTimeZone("UTC"));
            return format.parse(utc_timestamp);
        } catch (ParseException e) {
            log.log_Error("Failed parsing '", utc_timestamp, "' into a Date object.");
            throw new RuntimeException("Failed parsing '" + utc_timestamp + "' into a Date object.", e);
        }

    }
}
