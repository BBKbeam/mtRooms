package bbk_beam.mtRooms.db;

import bbk_beam.mtRooms.db.exception.InvalidSessionException;
import bbk_beam.mtRooms.db.exception.SessionException;

import java.util.Date;

public interface IDbAccess {
    /**
     * Opens a session with the database
     *
     * @param id     Session id
     * @param expiry Expiry timestamp
     * @throws SessionException when session ID is already tracked
     */
    void openSession(String id, Date expiry) throws SessionException;

    /**
     * Closes a session
     *
     * @param id Session ID
     * @throws InvalidSessionException when session ID isn't tracked
     */
    void closeSession(String id) throws InvalidSessionException;
}
