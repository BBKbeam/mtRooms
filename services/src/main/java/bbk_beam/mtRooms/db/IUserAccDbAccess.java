package bbk_beam.mtRooms.db;

import bbk_beam.mtRooms.db.exception.*;
import bbk_beam.mtRooms.db.session.SessionType;
import eadjlib.datastructure.ObjectTable;

import java.util.Date;

public interface IUserAccDbAccess {
    /**
     * Checks the validity of a session ID
     *
     * @param session_id     Session ID
     * @param session_expiry Session expiry timestamp expected
     * @throws SessionExpiredException   when session has expired
     * @throws SessionCorruptedException when given expiry does not match expiry tracked.
     * @throws SessionInvalidException   when session is not tracked
     */
    void checkValidity(String session_id, Date session_expiry) throws SessionExpiredException, SessionCorruptedException, SessionInvalidException;

    /**
     * Gets the type of the session
     *
     * @param session_id Session ID
     * @return Session type
     * @throws SessionInvalidException when session ID is not tracked
     */
    SessionType getSessionType(String session_id) throws SessionInvalidException;

    /**
     * Gets the associated account ID of the session
     *
     * @param session_id Session ID
     * @return Session account ID
     * @throws SessionInvalidException when session ID is not tracked
     */
    Integer getSessionAccountID(String session_id) throws SessionInvalidException;

    /**
     * Opens a session with the database
     *
     * @param session_id Session ID
     * @param expiry     Expiry timestamp
     * @param account_id Associated account ID
     * @throws SessionException when session ID is already tracked
     */
    void openSession(String session_id, Date expiry, SessionType session_type, Integer account_id) throws SessionException;

    /**
     * Closes a session with the database
     *
     * @param session_id Session ID
     * @throws SessionInvalidException when trying to close a non-tracked session
     */
    void closeSession(String session_id) throws SessionInvalidException;

    /**
     * Passes a SQL query to the database
     *
     * @param query SQL Query
     * @return Success
     * @throws DbQueryException when a problem was encountered whilst processing the query
     */
    boolean pushToDB(String query) throws DbQueryException;

    /**
     * Passes a SQL query to the database
     *
     * @param query SQL Query
     * @return Result set container
     * @throws DbQueryException when a problem was encountered whilst processing the query
     */
    ObjectTable pullFromDB(String query) throws DbQueryException;
}
