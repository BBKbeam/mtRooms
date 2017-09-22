package bbk_beam.mtRooms.db;

import bbk_beam.mtRooms.db.exception.DbQueryException;
import bbk_beam.mtRooms.db.exception.SessionInvalidException;
import bbk_beam.mtRooms.db.exception.SessionExpiredException;
import eadjlib.datastructure.ObjectTable;

import java.sql.ResultSet;

public interface IReservationDbAccess {

    /**
     * Passes a SQL query to the database
     *
     * @param session_id Session ID
     * @param query      SQL Query
     * @return Success
     * @throws DbQueryException        when a problem was encountered whilst processing the query
     * @throws SessionExpiredException When the session for the id provided has expired
     * @throws SessionInvalidException When the session for the id provided does not exist in the tracker
     */
    public boolean pushToDB(String session_id, String query) throws DbQueryException, SessionExpiredException, SessionInvalidException;

    /**
     * Passes a SQL query to the database
     *
     * @param session_id Session ID
     * @param query      SQL Query
     * @return Result set container
     * @throws DbQueryException        when a problem was encountered whilst processing the query
     * @throws SessionExpiredException When the session for the id provided has expired
     * @throws SessionInvalidException When the session for the id provided does not exist in the tracker
     */
    public ObjectTable pullFromDB(String session_id, String query) throws DbQueryException, SessionExpiredException, SessionInvalidException;
}
