package bbk_beam.mtRooms;

import bbk_beam.mtRooms.admin.administration.AdminSession;
import bbk_beam.mtRooms.admin.authentication.IAuthenticationSystem;
import bbk_beam.mtRooms.admin.authentication.Token;
import bbk_beam.mtRooms.admin.authentication.UserAccountChecker;
import bbk_beam.mtRooms.db.DbSystemBootstrap;
import bbk_beam.mtRooms.db.exception.DbBootstrapException;
import bbk_beam.mtRooms.operation.ILogisticReportGenerator;
import bbk_beam.mtRooms.operation.LogisticAggregator;
import bbk_beam.mtRooms.operation.LogisticReportGenerator;
import bbk_beam.mtRooms.reservation.ReservationSession;
import bbk_beam.mtRooms.reservation.delegate.ReservationDbDelegate;
import bbk_beam.mtRooms.reservation.processing.CustomerAccountAccess;
import bbk_beam.mtRooms.reservation.processing.OptimisedSearch;
import bbk_beam.mtRooms.reservation.processing.PaymentProcessing;
import bbk_beam.mtRooms.reservation.processing.ReservationProcessing;
import bbk_beam.mtRooms.reservation.scheduling.ScheduleCache;
import eadjlib.logger.Logger;

/**
 * ServiceDriver
 * <p>
 * Backend driver that instantiates all the required services/components for desktop-only build
 * </p>
 */
public class ServiceDriver {
    private final Logger log = Logger.getLoggerInstance(ServiceDriver.class.getName());
    //Db
    private DbSystemBootstrap dbSystemBootstrap;
    private ScheduleCache scheduleCache;
    //Admin
    private AdminSession adminSession;
    private IAuthenticationSystem authenticationSystem;
    //Reservation
    private OptimisedSearch optimisedSearch;
    private ReservationProcessing reservationProcessing;
    private PaymentProcessing paymentProcessing;
    private CustomerAccountAccess customerAccountAccess;
    //Operation
    private ILogisticReportGenerator logisticReportGenerator;


    /**
     * Constructor
     *
     * @param db_file_name File name for the records database file used
     * @throws DbBootstrapException when a problem occurred during the DB bootstrapping
     */
    public ServiceDriver(String db_file_name) throws DbBootstrapException {
        log.log("Spooling up. Target db is '", db_file_name, "'.");
        //Database
        this.dbSystemBootstrap = new DbSystemBootstrap();
        this.dbSystemBootstrap.init(db_file_name);
        //Administration
        this.authenticationSystem = new UserAccountChecker(
                this.dbSystemBootstrap.getUserAccDbAccess()
        );
        this.adminSession = new AdminSession(
                this.dbSystemBootstrap.getReservationDbAccess(),
                this.dbSystemBootstrap.getUserAccDbAccess(),
                this.authenticationSystem
        );
        //Reservation system
        this.scheduleCache = new ScheduleCache();
        ReservationDbDelegate reservationDbDelegate = new ReservationDbDelegate(
                this.dbSystemBootstrap.getReservationDbAccess()
        );
        this.customerAccountAccess = new CustomerAccountAccess(
                reservationDbDelegate
        );
        this.paymentProcessing = new PaymentProcessing(
                reservationDbDelegate
        );
        this.reservationProcessing = new ReservationProcessing(
                reservationDbDelegate,
                this.scheduleCache
        );
        this.optimisedSearch = new OptimisedSearch(
                reservationDbDelegate,
                this.scheduleCache
        );
        //Operation
        this.logisticReportGenerator = new LogisticReportGenerator(
                new LogisticAggregator(this.dbSystemBootstrap.getReservationDbAccess())
        );
    }

    /**
     * Gets the admin:authentication component
     *
     * @return IAuthenticationSystem instance
     */
    public IAuthenticationSystem getAuthenticationSystem() {
        return this.authenticationSystem;
    }

    /**
     * Gets the admin:administration component
     *
     * @return AdminSession instance
     */
    public AdminSession getAdminSession() {
        return this.adminSession;
    }

    /**
     * Creates a new ReservationSession instance for a session
     *
     * @param session_token Session token
     * @return ReservationSession instance for the token given
     * @throws InstantiationException when token is already currently used with a another different ReservationSession
     */
    public ReservationSession createNewReservationSession(Token session_token) throws InstantiationException {
        ReservationSession session = this.scheduleCache.getReservationSession(session_token);
        if (session != null) {
            log.log("Current ReservationSession requested. Target token is '", session_token, "'.");
        } else {
            log.log("New ReservationSession requested. Target token is '", session_token, "'.");
            session = new ReservationSession(
                    session_token,
                    customerAccountAccess,
                    paymentProcessing,
                    reservationProcessing,
                    optimisedSearch
            );

            if (!this.scheduleCache.exists(session)) {
                this.scheduleCache.addObserver(session);
            } else {
                log.log_Error("Tried to create a reservation session with a currently used Token [", session_token, "].");
                throw new InstantiationException("Tried to create a reservation session with a currently used Token [" + session_token + "].");
            }
        }
        return session;
    }

    /**
     * Delete <<Observer>> ReservationSession instance from the <<Observable>> ScheduleCache
     *
     * @param reservation_session ReservationSession instance
     */
    public void removeReservationSession(ReservationSession reservation_session) {
        this.scheduleCache.deleteObserver(reservation_session);
    }

    /**
     * Gets the operation:ILogisticReportGenerator instance
     *
     * @return LogisticReportGenerator instance
     */
    public ILogisticReportGenerator getLogisticReportGenerator() {
        return this.logisticReportGenerator;
    }


}
