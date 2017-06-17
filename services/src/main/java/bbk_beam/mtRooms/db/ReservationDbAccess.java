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

public class ReservationDbAccess implements IReservationDbAccess {
    private final Logger log = Logger.getLoggerInstance(ReservationDbAccess.class.getName());
    private SessionTracker sessions;
    private Database db;

    /**
     * Constructor
     *
     * @param tracker Current sessions tracker instance
     * @param db      Current Database instance to use
     * @throws SQLException when connection to the database fails
     */
    public ReservationDbAccess(SessionTracker tracker, Database db) throws SQLException {
        this.sessions = tracker;
        this.db = db;
        if (!this.db.isConnected()) {
            if (!this.db.connect()) {
                log.log_Fatal("Could not connect to database.");
                throw new SQLException("Could not connect to database.");
            }
        }
    }

    @Override
    public void openSession(String id, Date expiry) throws SessionException {
        this.sessions.addSession(id, expiry);
    }

    @Override
    public void closeSession(String id) throws InvalidSessionException {
        this.sessions.removeSession(id);
    }

    @Override
    public ResultSet queryDB(String session_id, String query) throws DBQueryException, InvalidSessionException, SessionExpiredException {
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
