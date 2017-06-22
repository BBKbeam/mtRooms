package bbk_beam.mtRooms.db;

import bbk_beam.mtRooms.db.database.IUserAccDb;
import bbk_beam.mtRooms.db.exception.DbBuildException;
import bbk_beam.mtRooms.db.exception.DbQueryException;
import bbk_beam.mtRooms.db.exception.SessionException;
import bbk_beam.mtRooms.db.exception.SessionInvalidException;
import bbk_beam.mtRooms.db.session.ICurrentSessions;
import bbk_beam.mtRooms.db.exception.*;
import eadjlib.logger.Logger;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class UserAccDbAccess implements IUserAccDbAccess {
    private final Logger log = Logger.getLoggerInstance(UserAccDbAccess.class.getName());
    private ICurrentSessions currentSession;
    private IUserAccDb db;

    /**
     * Constructor
     *
     * @param session_tracker Current session tracker instance
     * @param db              Current Database instance
     * @throws SQLException     when connection to the database fails
     * @throws DbBuildException when database is corrupted or incomplete
     */
    UserAccDbAccess(ICurrentSessions session_tracker, IUserAccDb db) throws SQLException, DbBuildException {
        this.currentSession = session_tracker;
        this.db = db;

        if (!db.isConnected()) {
            if (!db.connect()) {
                log.log_Fatal("Could not connect to database.");
                throw new SQLException("Could not connect to database.");
            } else {
                try {
                    if (!db.checkUserAccDB()) {
                        log.log_Fatal("User tables in database either corrupted or incomplete.");
                        throw new DbBuildException("Database corruption detected.");
                    }
                } catch (DbEmptyException e) {
                    if (!db.setupUserAccDB()) {
                        log.log_Fatal("Could not build user new database structure.");
                        throw new DbBuildException("Could not build new database structure.");
                    }
                }
            }
        }
    }

    @Override
    public boolean checkValidity(String session_id) {
        return (currentSession.isEmpty() && (!currentSession.isTracked(session_id)));
    }

    @Override
    public void openSession(String session_id, Date expiry) throws SessionException {

        try {
            if (currentSession.isValid(session_id)) {
                if (!currentSession.exists(session_id)) {
                    currentSession.addSession(session_id, expiry);
                }
            }
        } catch (SessionException e) {
            log.log_Error("User database access with session_id: [", session_id, "] could not open");
            throw new SessionException("Session could not open.", e);
        }
    }

    @Override
    public void closeSession(String session_id) throws SessionInvalidException {
        try {
            if (currentSession.getSessionType(session_id) == null) {
                currentSession.removeSession(session_id);
            }
        } catch (SessionInvalidException e) {
            log.log_Error(" User database access with session_id: [", session_id, "] could not close");
            throw new SessionInvalidException("Expired session could not close", e);
        }
    }

    @Override
    public ResultSet queryDB(String query) throws DbQueryException {
        return this.db.queryDB(query);
    }
}
