package bbk_beam.mtRooms.uaa;

import bbk_beam.mtRooms.admin.administration.AdminSession;
import bbk_beam.mtRooms.admin.authentication.IAuthenticationSystem;
import bbk_beam.mtRooms.admin.authentication.Token;
import bbk_beam.mtRooms.admin.authentication.UserAccountChecker;
import bbk_beam.mtRooms.admin.exception.AuthenticationFailureException;
import bbk_beam.mtRooms.db.DbSystemBootstrap;
import bbk_beam.mtRooms.db.exception.DbBootstrapException;
import bbk_beam.mtRooms.db.exception.SessionInvalidException;
import bbk_beam.mtRooms.db.session.SessionType;
import bbk_beam.mtRooms.operation.ILogisticReportGenerator;
import bbk_beam.mtRooms.operation.LogisticAggregator;
import bbk_beam.mtRooms.operation.LogisticReportGenerator;
import bbk_beam.mtRooms.reservation.delegate.ReservationDbDelegate;
import bbk_beam.mtRooms.reservation.processing.CustomerAccountAccess;
import bbk_beam.mtRooms.reservation.processing.OptimisedSearch;
import bbk_beam.mtRooms.reservation.processing.PaymentProcessing;
import bbk_beam.mtRooms.reservation.processing.ReservationProcessing;
import bbk_beam.mtRooms.reservation.scheduling.ScheduleCache;
import bbk_beam.mtRooms.uaa.exception.FailedSessionSpooling;
import bbk_beam.mtRooms.uaa.exception.SessionActive;
import bbk_beam.mtRooms.uaa.exception.SessionInactive;
import bbk_beam.mtRooms.uaa.exception.SessionLocked;
import eadjlib.logger.Logger;

public class SessionDriver implements ISessionDriver {
    private final Logger log = Logger.getLoggerInstance(AuthenticatedFrontDesk.class.getName());
    private String db_file = "";
    private boolean instantiated_flag = false;

    private DbSystemBootstrap dbSystemBootstrap;
    private Authenticator authenticator;
    private IAuthenticatedAdministration authenticated_admin;
    private IAuthenticatedFrontDesk authenticated_front_desk;
    private IAuthenticatedLogisticsPersonnel authenticated_logistics_personnel;
    private IAuthenticatedRevenuePersonnel authenticated_revenue_personnel;

    /**
     * Creates an IAuthenticatedAdministration implementation instance + dependency chain
     *
     * @return IAuthenticatedAdministration implementation  instance
     * @throws FailedSessionSpooling when a problem occurred during spooling
     */
    private IAuthenticatedAdministration createAuthenticatedAdmin() throws FailedSessionSpooling {
        try {
            log.log("Creating [AuthenticatedAdministration] instance...");
            IAuthenticationSystem authenticationSystem = new UserAccountChecker(
                    this.dbSystemBootstrap.getUserAccDbAccess()
            );
            AdminSession adminSession = new AdminSession(
                    this.dbSystemBootstrap.getReservationDbAccess(),
                    this.dbSystemBootstrap.getUserAccDbAccess(),
                    authenticationSystem
            );
            this.authenticator = new Authenticator(authenticationSystem);
            return new AuthenticatedAdministration(
                    new AdministrationDelegate(adminSession)
            );
        } catch (DbBootstrapException e) {
            throw new FailedSessionSpooling("Could not spool AuthenticatedAdministration dependency chain.", e);
        }

    }

    /**
     * Creates an IAuthenticatedFrontDesk implementation instance + dependency chain
     *
     * @return IAuthenticatedFrontDesk implementation instance
     * @throws FailedSessionSpooling when a problem occurred during spooling
     */
    private IAuthenticatedFrontDesk createAuthenticatedFrontDesk() throws FailedSessionSpooling {
        try {
            log.log("Creating [AuthenticatedFrontDesk] instance...");
            ReservationDbDelegate reservationDbDelegate = new ReservationDbDelegate(
                    this.dbSystemBootstrap.getReservationDbAccess()
            );
            ScheduleCache scheduleCache = new ScheduleCache();

            CustomerAccountAccess customerAccountAccess = new CustomerAccountAccess(
                    reservationDbDelegate
            );
            PaymentProcessing paymentProcessing = new PaymentProcessing(
                    reservationDbDelegate
            );
            ReservationProcessing reservationProcessing = new ReservationProcessing(
                    reservationDbDelegate,
                    scheduleCache
            );
            OptimisedSearch optimisedSearch = new OptimisedSearch(
                    reservationDbDelegate,
                    scheduleCache
            );

            FrontDeskDelegate delegate = new FrontDeskDelegate(
                    scheduleCache,
                    optimisedSearch,
                    reservationProcessing,
                    paymentProcessing,
                    customerAccountAccess
            );

            return new AuthenticatedFrontDesk(delegate, this.authenticator);
        } catch (DbBootstrapException e) {
            throw new FailedSessionSpooling("Could not spool AuthenticatedFrontDesk dependency chain.", e);
        }
    }

    /**
     * Creates an IAuthenticatedLogisticsPersonnel implementation instance + dependency chain
     *
     * @return IAuthenticatedLogisticsPersonnel implementation instance
     * @throws FailedSessionSpooling when a problem occurred during spooling
     */
    private IAuthenticatedLogisticsPersonnel createAuthenticatedLogisticsPersonnel() throws FailedSessionSpooling {
        try {
            log.log("Creating [AuthenticatedLogisticsPersonnel] instance...");
            ILogisticReportGenerator logisticReportGenerator = new LogisticReportGenerator(
                    new LogisticAggregator(this.dbSystemBootstrap.getReservationDbAccess())
            );
            return new AuthenticatedLogisticsPersonnel(
                    new LogisticsPersonnelDelegate(logisticReportGenerator)
            );
        } catch (DbBootstrapException e) {
            throw new FailedSessionSpooling("Could not spool AuthenticatedLogisticsPersonnel dependency chain.", e);
        }
    }

    /**
     * Creates an IAuthenticatedRevenuePersonnel implementation instance + dependency chain
     *
     * @return IAuthenticatedRevenuePersonnel implementation instance
     * @throws FailedSessionSpooling when a problem occurred during spooling
     */
    private IAuthenticatedRevenuePersonnel createAuthenticatedRevenuePersonnel() throws FailedSessionSpooling {
        log.log("Creating [AuthenticatedRevenuePersonnel] instance...");
        log.log_Warning("//createAuthenticatedRevenuePersonnel() is currently unimplemented!");
        //TODO implementation for creating the dependency chain of AuthenticatedRevenuePersonnel
        return null;
    }

    @Override
    public void init(String db_file_name) throws SessionActive, FailedSessionSpooling {
        if (instantiated_flag) {
            log.log_Error("Trying to initialise over already active Session (db='", this.db_file, "'). Reset first.");
            throw new SessionActive("Trying to initialise over already active Session (db='" + this.db_file + "'). Reset first.");
        }
        try {
            log.log("Spooling up session. Target db is '", db_file_name, "'.");

            //Database
            this.dbSystemBootstrap = new DbSystemBootstrap();
            this.dbSystemBootstrap.init(db_file_name);
            this.db_file = db_file_name;
            //Admin
            this.authenticated_admin = createAuthenticatedAdmin();
            //Reservation system
            this.authenticated_front_desk = createAuthenticatedFrontDesk();
            //Operation
            this.authenticated_logistics_personnel = createAuthenticatedLogisticsPersonnel();
            //Revenue
            this.authenticated_revenue_personnel = createAuthenticatedRevenuePersonnel();

            this.instantiated_flag = true;
            log.log("SessionDriver spooled all dependency instances successfully...");
        } catch (DbBootstrapException e) {
            log.log_Fatal("SessionDriver failed spooling.");
            throw new FailedSessionSpooling("SessionDriver failed spooling.", e);
        }
    }

    @Override
    public void reset() throws SessionLocked {
        if (this.authenticator.validTokenCount() > 0) {
            log.log_Error("Trying to reset with currently logged-in users.");
            throw new SessionLocked("Trying to reset with currently logged-in users.");
        }
        this.authenticator.clearExpiredTokens();
        if (!this.dbSystemBootstrap.closeConnection()) {
            log.log_Error("Could not close the database connection to '", this.db_file, "'.");
            throw new SessionLocked("Couldn't close the database connection...");
        }
        //Database
        this.dbSystemBootstrap = null;
        this.db_file = "";
        this.authenticated_admin = null;
        this.authenticated_front_desk = null;
        this.authenticated_logistics_personnel = null;
        this.authenticated_revenue_personnel = null;
        this.instantiated_flag = false;
        log.log("SessionDriver closed all dependency instances successfully...");
    }

    @Override
    public IAuthenticatedFrontDesk getFrontDeskInstance(Token session_token) throws SessionInactive, AuthenticationFailureException {
        if (!this.instantiated_flag)
            throw new SessionInactive("Session is inactive. It needs to be initialised.");
        if (!this.authenticator.isLoggedIn(session_token))
            throw new AuthenticationFailureException("Token [" + session_token + "] is not logged in.");
        if (!this.authenticator.hasValidAccessRights(session_token, SessionType.USER))
            throw new AuthenticationFailureException("Token [" + session_token + "] has not got access rights to this resource.");
        return this.authenticated_front_desk;
    }

    @Override
    public IAuthenticatedAdministration getAdminInstance(Token session_token) throws SessionInactive, AuthenticationFailureException {
        if (!this.instantiated_flag)
            throw new SessionInactive("Session is inactive. It needs to be initialised.");
        if (!this.authenticator.isLoggedIn(session_token))
            throw new AuthenticationFailureException("Token [" + session_token + "] is not logged in.");
        if (!this.authenticator.hasValidAccessRights(session_token, SessionType.ADMIN))
            throw new AuthenticationFailureException("Token [" + session_token + "] has not got access rights to this resource.");
        return this.authenticated_admin;
    }

    @Override
    public IAuthenticatedRevenuePersonnel getRevenuePersonnelInstance(Token session_token) throws SessionInactive, AuthenticationFailureException {
        if (!this.instantiated_flag)
            throw new SessionInactive("Session is inactive. It needs to be initialised.");
        if (!this.authenticator.isLoggedIn(session_token))
            throw new AuthenticationFailureException("Token [" + session_token + "] is not logged in.");
        if (!this.authenticator.hasValidAccessRights(session_token, SessionType.USER))
            throw new AuthenticationFailureException("Token [" + session_token + "] has not got access rights to this resource.");
        return this.authenticated_revenue_personnel;
    }

    @Override
    public IAuthenticatedLogisticsPersonnel getLogisticsPersonnelInstance(Token session_token) throws SessionInactive, AuthenticationFailureException {
        if (!this.instantiated_flag)
            throw new SessionInactive("Session is inactive. It needs to be initialised.");
        if (!this.authenticator.isLoggedIn(session_token))
            throw new AuthenticationFailureException("Token [" + session_token + "] is not logged in.");
        if (!this.authenticator.hasValidAccessRights(session_token, SessionType.USER))
            throw new AuthenticationFailureException("Token [" + session_token + "] has not got access rights to this resource.");
        return this.authenticated_logistics_personnel;
    }

    @Override
    public Token login(String username, String password) throws AuthenticationFailureException, SessionInactive {
        if (!this.instantiated_flag)
            throw new SessionInactive("Session is not initialised.");
        log.log("Login called for '", username, "'.");
        return this.authenticator.login(username, password);
    }

    @Override
    public void logout(Token session_token) throws SessionInvalidException, SessionInactive {
        if (!this.instantiated_flag)
            throw new SessionInactive("Session is not initialised.");
        log.log("Logout called for [", session_token, "].");
        this.authenticator.logout(session_token);
    }
}
