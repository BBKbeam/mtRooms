package bbk_beam.mtRooms.db;

import bbk_beam.mtRooms.db.database.IUserAccDb;
import bbk_beam.mtRooms.db.exception.*;
import bbk_beam.mtRooms.db.session.SessionTracker;
import eadjlib.logger.Logger;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class UserAccDbAccess implements IUserAccDbAccess {
    private final Logger log = Logger.getLoggerInstance(UserAccDbAccess.class.getName());
    private SessionTracker sessions;
    private IUserAccDb db;

    /**
     * Constructor
     *
     * @param session_tracker Current session tracker instance
     * @param db              Current Database instance
     * @throws SQLException     when connection to the database fails
     * @throws DbBuildException when database is corrupted or incomplete
     */
    UserAccDbAccess(SessionTracker session_tracker, IUserAccDb db) throws SQLException, DbBuildException {
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
    public ResultSet queryDB(String session_id, String query) throws DbQueryException, SessionExpiredException, InvalidSessionException {
        //TODO
        return null;
    }
}
