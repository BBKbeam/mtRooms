package bbk_beam.mtRooms.db;

import bbk_beam.mtRooms.db.database.Database;
import bbk_beam.mtRooms.db.exception.DbBootstrapException;
import bbk_beam.mtRooms.db.exception.DbBuildException;
import bbk_beam.mtRooms.db.session.SessionTracker;
import eadjlib.logger.Logger;

import java.sql.SQLException;

public class DbSystemBootstrap implements IDbSystemBootstrap {
    private final Logger log = Logger.getLoggerInstance(DbSystemBootstrap.class.getName());
    private IUserAccDbAccess userAccDbAccess = null;
    private IReservationDbAccess reservationDbAccess = null;
    private Database db = null;
    private boolean instantiated_flag = false;

    @Override
    public void init(String database) throws DbBootstrapException {
        try {
            if (!instantiated_flag) {
                //Database connection
                this.db = new Database(database);
                if (!this.db.connect()) {
                    log.log_Fatal("Could not connect to database '", database, "'.");
                    this.db = null;
                    throw new DbBootstrapException("Could not connect to database '" + database + "'.");
                }
                log.log_Debug("Database '", database, "' connection successful.");
                //Access components instantiation
                SessionTracker tracker = new SessionTracker();
                this.userAccDbAccess = new UserAccDbAccess(tracker, db);
                this.reservationDbAccess = new ReservationDbAccess(tracker, db);
                //Flag raised
                instantiated_flag = true;
                log.log("Reservation DB System bootstrapping successful");
            } else {
                log.log_Error("Trying to bootstrap over already active Reservation DB System.");
                if (this.userAccDbAccess != null) {
                    log.log_Error("=> Already instantiated: 'UserAccDbAccess'.");
                }
                if (this.reservationDbAccess != null) {
                    log.log_Error("=> Already instantiated: 'ReservationDbAccess'.");
                }
                throw new DbBootstrapException("Trying to bootstrap over already active or partly active Reservation DB System.");
            }
        } catch (SQLException e) {
            log.log_Fatal("Problems encountered whilst instantiating.");
            try {
                if (!this.db.disconnect()) {
                    log.log_Error("Could not disconnect cleanly from the database.");
                }
            } catch (SQLException exception) {
                log.log_Error("The database was already disconnected from. Weird but good... I guess?!");
            }
            this.userAccDbAccess = null;
            this.reservationDbAccess = null;
            this.db = null;
            throw new DbBootstrapException("Problems encountered whilst instantiating.", e);
        } catch (DbBuildException e) {
            log.log_Fatal( "Checks or Build of database tables did not go well." );
            throw new DbBootstrapException( "Check/Build of the database tables when wrong during instantiation", e  );
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
    public IReservationDbAccess getReservationDbAccess() throws DbBootstrapException {
        if (this.reservationDbAccess == null) {
            log.log_Error("Trying to get instance of [ReservationDbAccess] which hasn't been initialised.");
            throw new DbBootstrapException("Trying to get instance of [ReservationDbAccess] which hasn't been initialised.");
        }
        return this.reservationDbAccess;
    }
}
