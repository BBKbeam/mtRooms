package bbk_beam.mtRooms.db;

import bbk_beam.mtRooms.db.database.Database;
import bbk_beam.mtRooms.db.exception.DBQueryException;
import bbk_beam.mtRooms.db.exception.InvalidSessionException;
import bbk_beam.mtRooms.db.exception.SessionException;
import bbk_beam.mtRooms.db.exception.SessionExpiredException;
import bbk_beam.mtRooms.db.session.SessionTracker;
import eadjlib.logger.Logger;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class UserAccDbAccess implements IUserAccDbAccess {
    private final Logger log = Logger.getLoggerInstance(UserAccDbAccess.class.getName());
    private SessionTracker sessions;
    private Database db;

    /**
     * Constructor
     *
     * @param session_tracker Current session tracker instance
     * @param db              Current Database instance
     * @throws SQLException when connection to the database fails
     */
    UserAccDbAccess(SessionTracker session_tracker, Database db) throws SQLException {
        //TODO
    }

    @Override
    public boolean checkValidity(String session_id) {
        //TODO
        return false;
    }

    @Override
    public void openSession(String session_id, Date expiry) throws SessionException {
        //TODO
    }

    @Override
    public void closeSession(String session_id) throws InvalidSessionException {
        //TODO
    }

    @Override
    public ResultSet queryDB(String session_id, String query) throws DBQueryException, SessionExpiredException, InvalidSessionException {
        //TODO
        return null;
    }
}
