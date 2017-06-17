package bbk_beam.mtRooms.db.database;

import bbk_beam.mtRooms.db.exception.DBQueryException;
import eadjlib.logger.Logger;

import java.sql.*;

public class Database {
    private final Logger log = Logger.getLoggerInstance(Database.class.getName());
    private String path;
    private Connection connection = null;
    private boolean connected_flag = false;

    /**
     * Constructor
     *
     * @param db_path SQLite Database path
     */
    public Database(String db_path) {
        this.path = db_path;
    }

    /**
     * Connect to a Database
     *
     * @return Success
     */
    public boolean connect() {
        try {
            if (connection != null && !connection.isClosed()) {
                log.log_Error("Trying to connect over an existing connection to '", this.path, "'. Please close prior to connecting.");
                return false;
            }
            connection = DriverManager.getConnection("jdbc:sqlite:" + this.path);
            connected_flag = true;
            return true;
        } catch (SQLException e) {
            log.log_Error("Could not connect to DB:'", this.path, "'.");
            log.log_Exception(e);
        }
        return false;
    }

    /**
     * Disconnect from the connected Database
     *
     * @return Success
     * @throws SQLException when attempting to disconnect nothing
     */
    public boolean disconnect() throws SQLException {
        try {
            if (connection == null) {
                log.log_Error("Trying to disconnect from null connection.");
                return false;
            } else if( connection.isClosed() ) {
                log.log_Error("Trying to disconnect from closed connection.");
                return false;
            } else { //Closing connection
                connection.close();
                connected_flag = false;
                return true;
            }
        } catch (SQLException e) {
            log.log_Exception(e);
        }
        return false;
    }

    /**
     * Query the Database
     *
     * @param query Database query string
     * @return ResultSet of the query
     * @throws DBQueryException when querying the DB fails
     */
    public ResultSet queryDB(String query) throws DBQueryException {
        try {
            //Error control
            if (this.connection == null) {
                log.log_Error("Cannot pass query \"", query, "\" to a null DB connection.");
                throw new DBQueryException("Database connection does not exist.");
            } else if (this.connection.isClosed()) {
                log.log_Error("Cannot pass query \"", query, "\" to a disconnected DB.");
                throw new DBQueryException("Database is not connected.");
            }
            //Query processing
            Statement statement = this.connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            if (!resultSet.isBeforeFirst()) {
                log.log_Trace("No data from query: ", query);
            }
            return resultSet;
        } catch (SQLException e) {
            log.log_Error("Problem encountered passing query: ", query);
            throw new DBQueryException("Could not process query to DB.", e);
        }
    }

    /**
     * Checks if the connected flag is raised
     * @return Connection to DB flag
     */
    public boolean isConnected() {
        return this.connected_flag;
    }

}
