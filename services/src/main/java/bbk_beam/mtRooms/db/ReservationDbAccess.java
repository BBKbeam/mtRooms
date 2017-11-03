package bbk_beam.mtRooms.db;

import bbk_beam.mtRooms.db.database.IReservationDb;
import bbk_beam.mtRooms.db.exception.*;
import bbk_beam.mtRooms.db.session.ICurrentSessions;
import eadjlib.datastructure.ObjectTable;
import eadjlib.logger.Logger;

import java.sql.SQLException;

public class ReservationDbAccess implements IReservationDbAccess {
    private final Logger log = Logger.getLoggerInstance(ReservationDbAccess.class.getName());
    private ICurrentSessions sessions;
    private IReservationDb db;

    /**
     * Constructor
     *
     * @param tracker Current sessions tracker instance
     * @param db      Current Database instance to use
     * @throws SQLException     when connection to the database fails
     * @throws DbBuildException when database is corrupted or incomplete
     */
    ReservationDbAccess(ICurrentSessions tracker, IReservationDb db) throws SQLException, DbBuildException {
        this.sessions = tracker;
        this.db = db;
        if (!this.db.isConnected()) {
            if (!this.db.connect()) {
                log.log_Fatal("Could not connect to database.");
                throw new SQLException("Could not connect to database.");
            }
        }
        try {
            if (!db.checkReservationDB()) {
                log.log_Fatal("Tables in database either corrupted or incomplete.");
                throw new DbBuildException("Database corruption detected.");
            }
        } catch (DbEmptyException e) {
            log.log("None of required reservation tables found.");
            if (!db.setupReservationDB()) {
                log.log_Fatal("Could not build new database structure.");
                throw new DbBuildException("Could not build new database structure.");
            }
        }
    }

    @Override
    public boolean pushToDB(String session_id, String query) throws DbQueryException, SessionExpiredException, SessionInvalidException {
        try {
            sessions.checkValidity(session_id);
            return this.db.push(query);
        } catch (SessionInvalidException e) {
            log.log_Error("Session [", session_id, "] is not valid.");
            throw new SessionInvalidException("Query passed with invalid session.", e);
        }
    }

    @Override
    public ObjectTable pullFromDB(String session_id, String query) throws DbQueryException, SessionExpiredException, SessionInvalidException {
        try {
            sessions.checkValidity(session_id);
            return this.db.pull(query);
        } catch (SessionInvalidException e) {
            log.log_Error("Session [", session_id, "] is not valid.");
            throw new SessionInvalidException("Query passed with invalid session.", e);
        }
    }
}
