package bbk_beam.mtRooms.db.database;

import bbk_beam.mtRooms.db.exception.DbQueryException;
import eadjlib.logger.Logger;

import java.sql.*;

public class Database implements IDatabase, IUserAccDb, IReservationDb {
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

    @Override
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

    @Override
    public boolean disconnect() throws SQLException {
        try {
            if (connection == null) {
                log.log_Error("Trying to disconnect from null connection.");
                return false;
            } else if (connection.isClosed()) {
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

    @Override
    public ResultSet queryDB(String query) throws DbQueryException {
        try {
            //Error control
            if (this.connection == null) {
                log.log_Error("Cannot pass query \"", query, "\" to a null DB connection.");
                throw new DbQueryException("Database connection does not exist.");
            } else if (this.connection.isClosed()) {
                log.log_Error("Cannot pass query \"", query, "\" to a disconnected DB.");
                throw new DbQueryException("Database is not connected.");
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
            throw new DbQueryException("Could not process query to DB.", e);
        }
    }

    @Override
    public boolean isConnected() {
        return this.connected_flag;
    }

    @Override
    public boolean setupReservationDB() {
        DatabaseBuilder builder = new DatabaseBuilder();
        return builder.buildReservationDB(this);
    }

    @Override
    public boolean checkReservationDB() {
        DatabaseChecker checker = new DatabaseChecker();
        return checker.checkReservationDB(this);
    }

    @Override
    public boolean setupUserAccDB() {
        DatabaseBuilder builder = new DatabaseBuilder();
        return builder.buildUserAccDB(this);
    }

    @Override
    public boolean checkUserAccDB() {
        DatabaseChecker checker = new DatabaseChecker();
        return checker.checkUserAccDB(this);
    }
}
