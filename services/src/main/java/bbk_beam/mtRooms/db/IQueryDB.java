package bbk_beam.mtRooms.db;

import bbk_beam.mtRooms.db.exception.DBQueryException;
import bbk_beam.mtRooms.db.exception.InvalidSessionException;
import bbk_beam.mtRooms.db.exception.SessionException;
import bbk_beam.mtRooms.db.exception.SessionExpiredException;

import java.sql.ResultSet;
import java.util.Date;

public interface IQueryDB {
    /**
     * Passes a SQL query to the database
     *
     * @param session_id Session ID of the originating query
     * @param query      SQL Query
     * @return ResultSet of query
     * @throws DBQueryException        When a problem was encountered processing the query
     * @throws SessionExpiredException When the session for the id provided has expired
     * @throws InvalidSessionException When the session for the id provided does not exist in the tracker
     */
    public ResultSet queryDB(String session_id, String query) throws DBQueryException, SessionExpiredException, InvalidSessionException;
}
