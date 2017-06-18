package bbk_beam.mtRooms.db;

import bbk_beam.mtRooms.db.exception.DbQueryException;
import bbk_beam.mtRooms.db.exception.SessionInvalidException;
import bbk_beam.mtRooms.db.exception.SessionException;

import java.sql.ResultSet;
import java.util.Date;

public interface IUserAccDbAccess {
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
     * @throws SessionInvalidException when trying to close a non-tracked session
     */
    public void closeSession(String session_id) throws SessionInvalidException;

    /**
     * Passes a SQL query to the database
     *
     * @param query      SQL Query
     * @return ResultSet of query
     * @throws DbQueryException        When a problem was encountered processing the query
     */
    public ResultSet queryDB(String query) throws DbQueryException;
}
