package bbk_beam.mtRooms.admin.administration.maintenance;

import bbk_beam.mtRooms.admin.authentication.Token;
import bbk_beam.mtRooms.db.IReservationDbAccess;
import bbk_beam.mtRooms.db.exception.DbQueryException;
import bbk_beam.mtRooms.db.exception.SessionExpiredException;
import bbk_beam.mtRooms.db.exception.SessionInvalidException;
import eadjlib.logger.Logger;

public class ReservationDbMaintenance {
    private final Logger log = Logger.getLoggerInstance(ReservationDbMaintenance.class.getName());
    private IReservationDbAccess db_access;

    /**
     * Constructor
     *
     * @param db_access ReservationDbAccess instance
     */
    public ReservationDbMaintenance(IReservationDbAccess db_access) {
        this.db_access = db_access;
    }

    /**
     * Runs the vacuum command on the connected reservation database
     *
     * @param admin_token Administration session token
     * @throws SessionInvalidException when administrator session is not valid
     * @throws SessionExpiredException when current administrator session has expired
     * @throws DbQueryException        when vacuuming query failed
     */
    public void vacuumDatabase(Token admin_token) throws SessionInvalidException, SessionExpiredException, DbQueryException {
        try {
            this.db_access.pushToDB(admin_token.getSessionId(), "VACUUM");
            log.log("Reservation database optimised.");
        } catch (DbQueryException e) {
            log.log_Error("Vacuuming failed.");
            throw new DbQueryException("Failed vacuuming of DB schema.", e);
        } catch (SessionExpiredException e) {
            log.log_Error("Session token [", admin_token.getSessionId(), "] has expired.");
            throw new SessionExpiredException("Expired session token.", e);
        } catch (SessionInvalidException e) {
            log.log_Error("Session token [", admin_token.getSessionId(), "] is invalid.");
            throw new SessionInvalidException("Invalid session token.", e);
        }
    }
}
