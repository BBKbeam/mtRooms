package bbk_beam.mtRooms.db;

import bbk_beam.mtRooms.db.exception.InvalidSessionException;
import bbk_beam.mtRooms.db.exception.SessionException;

import java.util.Date;

public interface IUserAccDbAccess extends IQueryDB {
    /**
     * Checks the validity of a session ID
     *
     * @param session_id Session ID
     * @return Valid state
     */
    public boolean checkValidity(String session_id);

    /**
     * Opens a session with the database
     *
     * @param session_id Session ID
     * @param expiry     Expiry timestamp
     * @throws SessionException when session ID is already tracked
     */
    public void openSession(String session_id, Date expiry) throws SessionException;

    /**
     * Closes a session with the database
     *
     * @param session_id Session ID
     * @throws InvalidSessionException when trying to close a non-tracked session
     */
    public void closeSession(String session_id) throws InvalidSessionException;
}
