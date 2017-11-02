package bbk_beam.mtRooms.db;

import bbk_beam.mtRooms.db.exception.DbQueryException;
import bbk_beam.mtRooms.db.exception.SessionCorruptedException;
import bbk_beam.mtRooms.db.exception.SessionException;
import bbk_beam.mtRooms.db.exception.SessionInvalidException;
import bbk_beam.mtRooms.db.session.SessionType;
import eadjlib.datastructure.ObjectTable;

import java.util.Date;

public interface IUserAccDbAccess {
    /**
     * Checks the validity of a session ID
     *
     * @param session_id     Session ID
     * @param session_expiry Session expiry timestamp expected
     * @return Valid state
     * @throws SessionCorruptedException when given expiry does not match expiry tracked.
     */
    public boolean checkValidity(String session_id, Date session_expiry) throws SessionCorruptedException;

    /**
     * Gets the type of the session
     *
     * @param session_id Session ID
     * @return Session type
     * @throws SessionInvalidException when session ID is not tracked
     */
    public SessionType getSessionType(String session_id) throws SessionInvalidException;

    /**
     * Opens a session with the database
     *
     * @param session_id Session ID
     * @param expiry     Expiry timestamp
     * @throws SessionException when session ID is already tracked
     */
    public void openSession(String session_id, Date expiry, SessionType session_type) throws SessionException;

    /**
     * Closes a session with the database
     *
     * @param session_id Session ID
     * @throws SessionInvalidException when trying to close a non-tracked session
     */
    public void closeSession(String session_id) throws SessionInvalidException;

    /**
     * Passes a SQL query to the database
     *
     * @param query SQL Query
     * @return Success
     * @throws DbQueryException when a problem was encountered whilst processing the query
     */
    public boolean pushToDB(String query) throws DbQueryException;

    /**
     * Passes a SQL query to the database
     *
     * @param query SQL Query
     * @return Result set container
     * @throws DbQueryException when a problem was encountered whilst processing the query
     */
    public ObjectTable pullFromDB(String query) throws DbQueryException;
}
