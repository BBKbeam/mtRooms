package bbk_beam.mtRooms.db;

import bbk_beam.mtRooms.db.database.Database;
import bbk_beam.mtRooms.db.exception.DbBootstrapException;
import bbk_beam.mtRooms.db.session.SessionTracker;
import eadjlib.logger.Logger;

import java.sql.SQLException;

public class DbSystemBootstrap implements IDbSystemBootstrap {
    private final Logger log = Logger.getLoggerInstance(DbSystemBootstrap.class.getName());
    private IUserAccDbAccess userAccDbAccess = null;
    private IQueryDB reservationDbAccess = null;

    @Override
    public void init(String database) throws DbBootstrapException {
        try {
            Database db = new Database(database);
            if (!db.connect()) {
                log.log_Fatal("Could not connect to database '", database, "'.");
                db = null;
                throw new DbBootstrapException("Could not connect to database '" + database + "'.");
            }
            log.log_Debug("Database '", database, "' connection successful.");

            SessionTracker tracker = new SessionTracker();
            this.userAccDbAccess = new UserAccDbAccess(tracker, db);
            this.reservationDbAccess = new ReservationDbAccess(tracker, db);

        } catch (SQLException e) {
            log.log_Fatal("Problems encountered whilst instantiating.");
            throw new DbBootstrapException("Problems encountered whilst instantiating.", e);
        }

    }

    @Override
    public IUserAccDbAccess getUserAccDbAccess() throws DbBootstrapException {
        if (this.userAccDbAccess == null) {
            log.log_Error("Trying to get instance of [UserAccDbAccess] which hasn't been initialised.");
            throw new DbBootstrapException("Trying to get instance of [UserAccDbAccess] which hasn't been initialised.");
        }
        return this.userAccDbAccess;
    }

    @Override
    public IQueryDB getReservationDbAccess() throws DbBootstrapException {
        if (this.reservationDbAccess == null) {
            log.log_Error("Trying to get instance of [ReservationDbAccess] which hasn't been initialised.");
            throw new DbBootstrapException("Trying to get instance of [ReservationDbAccess] which hasn't been initialised.");
        }
        return this.reservationDbAccess;
    }
}
