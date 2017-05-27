package bbk_beam.mtRooms.db;

import java.sql.Timestamp;

public interface ITimeZoneTranslation {
    /**
     * Translates a UTC timestamp into a local one
     *
     * @param utc UTC Time stamp
     * @return Local Time stamp
     */
    public Timestamp translate(Timestamp utc);
}
