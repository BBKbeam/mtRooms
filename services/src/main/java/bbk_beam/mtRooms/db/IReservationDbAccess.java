package bbk_beam.mtRooms.db;

import bbk_beam.mtRooms.db.exception.DBQueryException;
import bbk_beam.mtRooms.db.exception.SessionException;
import bbk_beam.mtRooms.db.exception.SessionExpiredException;

import java.sql.ResultSet;
import java.util.Date;

public interface IReservationDbAccess {
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
     * @throws SessionException when session ID isn't tracked
     */
    void closeSession(String id) throws SessionException;

    /**
     * Passes a SQL query to the database
     *
     * @param session_id Session ID of the originating query
     * @param query      SQL Query
     * @return ResultSet of query
     * @throws DBQueryException        When a problem was encountered processing the query
     * @throws SessionExpiredException When the session for the id provided has expired
     */
    public ResultSet queryDB(String session_id, String query) throws DBQueryException, SessionExpiredException;
}
