package bbk_beam.mtRooms.db;

import bbk_beam.mtRooms.db.database.IUserAccDb;
import bbk_beam.mtRooms.db.exception.*;
import bbk_beam.mtRooms.db.session.ICurrentSessions;
import bbk_beam.mtRooms.db.session.SessionType;
import eadjlib.datastructure.ObjectTable;
import eadjlib.logger.Logger;

import java.sql.SQLException;
import java.util.Date;

public class UserAccDbAccess implements IUserAccDbAccess {
    private final Logger log = Logger.getLoggerInstance(UserAccDbAccess.class.getName());
    private ICurrentSessions currentSessions;
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
        this.currentSessions = session_tracker;
        this.db = db;
        if (!db.isConnected()) {
            if (!db.connect()) {
                log.log_Fatal("Could not connect to database.");
                throw new SQLException("Could not connect to database.");
            }
        }
        try {
            if (!db.checkUserAccDB()) {
                log.log_Fatal("User tables in database either corrupted or incomplete.");
                throw new DbBuildException("Database corruption detected.");
            }
        } catch (DbEmptyException e) {
            log.log("None of required user account tables found.");
            if (!db.setupUserAccDB()) {
                log.log_Fatal("Could not build user new database structure.");
                throw new DbBuildException("Could not build new database structure.");
            }
        }
    }

    @Override
    public void checkValidity(String session_id, Date session_expiry) throws SessionExpiredException, SessionCorruptedException, SessionInvalidException {
        this.currentSessions.checkValidity(session_id, session_expiry);
    }

    @Override
    public SessionType getSessionType(String session_id) throws SessionInvalidException {
        return this.currentSessions.getSessionType(session_id);
    }

    @Override
    public void openSession(String session_id, Date expiry, SessionType session_type) throws SessionException {
        this.currentSessions.addSession(session_id, expiry, session_type);
    }

    @Override
    public void closeSession(String session_id) throws SessionInvalidException {
        this.currentSessions.removeSession(session_id);
    }

    @Override
    public boolean pushToDB(String query) throws DbQueryException {
        return this.db.push(query);
    }

    @Override
    public ObjectTable pullFromDB(String query) throws DbQueryException {
        return this.db.pull(query);
    }

}
