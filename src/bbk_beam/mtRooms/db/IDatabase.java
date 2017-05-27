package bbk_beam.mtRooms.db;

import java.sql.SQLException;
import java.sql.ResultSet;

public interface IDatabase {
    /**
     * Connect to a Database
     *
     * @param host     DB Host address
     * @param username Username
     * @param password Password
     * @return Success
     */
    public boolean connect(String host, String username, String password);

    /**
     * Disconnect from the connected Database
     *
     * @return Success
     * @throws SQLException when attempting to disconnect nothing
     */
    public boolean disconnect() throws SQLException;

    /**
     * Query the Database
     *
     * @param query Database query string
     * @return ResultSet of the query
     */
    public ResultSet queryDB(String query);
}
