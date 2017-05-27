package bbk_beam.mtRooms.db;

import eadjlib.logger.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Database {
    private final Logger log = Logger.getLoggerInstance(Database.class.getName());
    private String path;
    private Connection connection = null;

    /**
     * Constructor
     * @param db_path SQLite Database path
     */
    Database( String db_path ) {
        this.path = db_path;
    }

    /**
     * Connect to a Database
     *
     * @return Success
     */
    public boolean connect() {
        try {
            if( connection != null && !connection.isClosed() ) {
                log.log_Error( "Trying to connect over an existing connection to '", this.path, "'. Please close prior to connecting.");
                return false;
            }
            connection = DriverManager.getConnection("jdbc:sqlite:" + this.path);
            return true;
        } catch (SQLException e) {
            log.log_Error("Could not connect to DB:'", this.path, "'.");
            log.log_Exception(e);
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
                connection = null;
            } catch (SQLException ex) {
                log.log_Error("Could not close the connection to DB:'", this.path, "'.");
                log.log_Exception(ex);
            }
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
           if( connection != null && !connection.isClosed() ) {
               connection.close();
               return true;
           }
        } catch( SQLException e ) {
            if( connection == null ) {
                log.log_Error("Trying to disconnect from null connection.");
            } else {
                log.log_Error("Trying to disconnect from closed connection.");
            }
            log.log_Exception(e);
        }
        return false;
    }

    /**
     * Query the Database
     *
     * @param query Database query string
     * @return ResultSet of the query
     */
    public ResultSet queryDB(String query) {
        return null;
    }
}
