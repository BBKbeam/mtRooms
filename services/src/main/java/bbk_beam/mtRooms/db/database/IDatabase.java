package bbk_beam.mtRooms.db.database;

import bbk_beam.mtRooms.db.exception.DbQueryException;
import eadjlib.datastructure.ObjectTable;

import java.sql.SQLException;

/**
 * Universal database interface
 */
public interface IDatabase {
    /**
     * Connect to a Database
     *
     * @return Success
     */
    boolean connect();

    /**
     * Disconnect from the connected Database
     *
     * @return Success
     * @throws SQLException when attempting to disconnect nothing
     */
    boolean disconnect() throws SQLException;

    /**
     * Query the Database
     *
     * @param query Database query string
     * @return Success
     * @throws DbQueryException when querying the DB fails
     */
    boolean push(String query) throws DbQueryException;

    /**
     * Query the Database
     *
     * @param query Database query string
     * @return ObjectTable containing the result set
     * @throws DbQueryException when querying the DB fails
     */
    ObjectTable pull(String query) throws DbQueryException;

    /**
     * Checks if the connected flag is raised
     *
     * @return Connection to DB flag
     */
    boolean isConnected();
}
