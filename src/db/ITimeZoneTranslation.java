package bbk_beam.mtRooms.db;

public interface ITimeZoneTranslation {
    /**
     * Translates a UTC timestamp into a local one
     *
     * @param utc UTC Time stamp
     * @return Local Time stamp
     */
    public TimeStamp translate(TimeStamp utc);
}
