package bbk_beam.mtRooms.db;

import bbk_beam.mtRooms.db.exception.DbQueryException;
import bbk_beam.mtRooms.db.exception.SessionInvalidException;
import bbk_beam.mtRooms.db.exception.SessionExpiredException;

import java.sql.ResultSet;

public interface IReservationDbAccess {
    /**
     * Passes a SQL query to the database
     *
     * @param session_id Session ID of the originating query
     * @param query      SQL Query
     * @return ResultSet of query
     * @throws DbQueryException        When a problem was encountered processing the query
     * @throws SessionExpiredException When the session for the id provided has expired
     * @throws SessionInvalidException When the session for the id provided does not exist in the tracker
     */
    public ResultSet queryDB(String session_id, String query) throws DbQueryException, SessionExpiredException, SessionInvalidException;
}
