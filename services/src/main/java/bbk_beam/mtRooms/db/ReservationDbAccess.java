package bbk_beam.mtRooms.db;

import bbk_beam.mtRooms.db.database.IReservationDb;
import bbk_beam.mtRooms.db.exception.*;
import bbk_beam.mtRooms.db.session.SessionTracker;
import eadjlib.logger.Logger;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ReservationDbAccess implements IQueryDB {
    private final Logger log = Logger.getLoggerInstance(ReservationDbAccess.class.getName());
    private SessionTracker sessions;
    private IReservationDb db;

    /**
     * Constructor
     *
     * @param tracker Current sessions tracker instance
     * @param db      Current Database instance to use
     * @throws SQLException     when connection to the database fails
     * @throws DbBuildException when database is corrupted or incomplete
     */
    ReservationDbAccess(SessionTracker tracker, IReservationDb db) throws SQLException, DbBuildException {
        this.sessions = tracker;
        this.db = db;
        if (!this.db.isConnected()) {
            if (!this.db.connect()) {
                log.log_Fatal("Could not connect to database.");
                throw new SQLException("Could not connect to database.");
            } else {
                try {
                    if (!db.checkReservationDB()) {
                        log.log_Fatal("Tables in database either corrupted or incomplete.");
                        throw new DbBuildException("Database corruption detected.");
                    }
                } catch (EmptyDatabaseException e) {
                    if (!db.setupReservationDB()) {
                        log.log_Fatal("Could not build new database structure.");
                        throw new DbBuildException("Could not build new database structure.");
                    }
                }
            }
        }
    }

    @Override
    public ResultSet queryDB(String session_id, String query) throws DbQueryException, InvalidSessionException, SessionExpiredException {
        try {
            if (sessions.isValid(session_id)) {
                return this.db.queryDB(query);
            } else {
                log.log_Error("Session [", session_id, "] has expired.");
                throw new SessionExpiredException("Session (id: " + session_id + ") has expired.");
            }
        } catch (InvalidSessionException e) {
            log.log_Error("Session [", session_id, "] is not valid.");
            throw new InvalidSessionException("Query passed with invalid session.", e);
        }
    }
}
