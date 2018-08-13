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
import bbk_beam.mtRooms.revenue.IRevenueReporter;
import bbk_beam.mtRooms.revenue.ReportCreator;
import bbk_beam.mtRooms.revenue.RevenueAggregator;
import bbk_beam.mtRooms.uaa.exception.*;
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
                    dbSystemBootstrap.getUserAccDbAccess()
            );
            AdminSession adminSession = new AdminSession(
                    dbSystemBootstrap.getReservationDbAccess(),
                    dbSystemBootstrap.getUserAccDbAccess(),
                    authenticationSystem
            );
            authenticator = new Authenticator(authenticationSystem);
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
                    dbSystemBootstrap.getReservationDbAccess()
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

            return new AuthenticatedFrontDesk(delegate, authenticator);
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
                    new LogisticAggregator(dbSystemBootstrap.getReservationDbAccess())
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
        try {
            log.log("Creating [AuthenticatedRevenuePersonnel] instance...");
            IRevenueReporter revenueReporter = new ReportCreator(
                    new RevenueAggregator(dbSystemBootstrap.getReservationDbAccess())
            );
            return new AuthenticatedRevenuePersonnel(
                    new RevenuePersonnelDelegate(revenueReporter)
            );
        } catch (DbBootstrapException e) {
            throw new FailedSessionSpooling("Could not spool IAuthenticatedRevenuePersonnel dependency chain.", e);
        }
    }

    @Override
    synchronized public void init(String db_file_name) throws SessionActive, FailedSessionSpooling {
        if (instantiated_flag) {
            log.log_Error("Trying to initialise over already active Session (db='", db_file, "'). Reset first.");
            throw new SessionActive("Trying to initialise over already active Session (db='" + db_file + "'). Reset first.");
        }
        try {
            log.log("Spooling up session. Target db is '", db_file_name, "'.");

            //Database
            dbSystemBootstrap = new DbSystemBootstrap();
            dbSystemBootstrap.init(db_file_name);
            db_file = db_file_name;
            //Admin
            authenticated_admin = createAuthenticatedAdmin();
            //Reservation system
            authenticated_front_desk = createAuthenticatedFrontDesk();
            //Operation
            authenticated_logistics_personnel = createAuthenticatedLogisticsPersonnel();
            //Revenue
            authenticated_revenue_personnel = createAuthenticatedRevenuePersonnel();

            instantiated_flag = true;
            log.log("SessionDriver spooled all dependency instances successfully...");
        } catch (DbBootstrapException e) {
            log.log_Fatal("SessionDriver failed spooling.");
            throw new FailedSessionSpooling("SessionDriver failed spooling.", e);
        }
    }

    @Override
    synchronized public void reset() throws SessionLocked {
        if (authenticator.validTokenCount() > 0) {
            log.log_Error("Trying to reset with currently logged-in users.");
            throw new SessionLocked("Trying to reset with currently logged-in users.");
        }
        authenticator.clearExpiredTokens();
        if (!dbSystemBootstrap.closeConnection()) {
            log.log_Error("Could not close the database connection to '", db_file, "'.");
            throw new SessionLocked("Couldn't close the database connection...");
        }
        //Database
        dbSystemBootstrap = null;
        db_file = "";
        authenticated_admin = null;
        authenticated_front_desk = null;
        authenticated_logistics_personnel = null;
        authenticated_revenue_personnel = null;
        instantiated_flag = false;
        log.log("SessionDriver closed all dependency instances successfully...");
    }

    @Override
    synchronized public IAuthenticatedFrontDesk getFrontDeskInstance(Token session_token) throws ServerSessionInactive, AuthenticationFailureException {
        if (!instantiated_flag)
            throw new ServerSessionInactive("Session is inactive. It needs to be initialised.");
        if (!authenticator.isLoggedIn(session_token))
            throw new AuthenticationFailureException("Token [" + session_token + "] is not logged in.");
        if (!authenticator.hasValidAccessRights(session_token, SessionType.USER))
            throw new AuthenticationFailureException("Token [" + session_token + "] has not got access rights to this resource.");
        return authenticated_front_desk;
    }

    @Override
    synchronized public IAuthenticatedAdministration getAdministrationInstance(Token session_token) throws ServerSessionInactive, AuthenticationFailureException {
        if (!instantiated_flag)
            throw new ServerSessionInactive("Session is inactive. It needs to be initialised.");
        if (!authenticator.isLoggedIn(session_token))
            throw new AuthenticationFailureException("Token [" + session_token + "] is not logged in.");
        if (!authenticator.hasValidAccessRights(session_token, SessionType.ADMIN))
            throw new AuthenticationFailureException("Token [" + session_token + "] has not got access rights to this resource.");
        return authenticated_admin;
    }

    @Override
    synchronized public IAuthenticatedRevenuePersonnel getRevenuePersonnelInstance(Token session_token) throws ServerSessionInactive, AuthenticationFailureException {
        if (!instantiated_flag)
            throw new ServerSessionInactive("Session is inactive. It needs to be initialised.");
        if (!authenticator.isLoggedIn(session_token))
            throw new AuthenticationFailureException("Token [" + session_token + "] is not logged in.");
        if (!authenticator.hasValidAccessRights(session_token, SessionType.USER))
            throw new AuthenticationFailureException("Token [" + session_token + "] has not got access rights to this resource.");
        return authenticated_revenue_personnel;
    }

    @Override
    synchronized public IAuthenticatedLogisticsPersonnel getLogisticsPersonnelInstance(Token session_token) throws ServerSessionInactive, AuthenticationFailureException {
        if (!instantiated_flag)
            throw new ServerSessionInactive("Session is inactive. It needs to be initialised.");
        if (!authenticator.isLoggedIn(session_token))
            throw new AuthenticationFailureException("Token [" + session_token + "] is not logged in.");
        if (!authenticator.hasValidAccessRights(session_token, SessionType.USER))
            throw new AuthenticationFailureException("Token [" + session_token + "] has not got access rights to this resource.");
        return authenticated_logistics_personnel;
    }

    @Override
    synchronized public Token login(String username, String password) throws AuthenticationFailureException, ServerSessionInactive {
        if (!instantiated_flag)
            throw new ServerSessionInactive("Server session is not initialised.");
        log.log("Login called for '", username, "'.");
        return authenticator.login(username, password);
    }

    @Override
    synchronized public void logout(Token session_token) throws SessionInvalidException, ServerSessionInactive {
        if (!instantiated_flag)
            throw new ServerSessionInactive("Server session is not initialised.");
        log.log("Logout called for [", session_token, "].");
        authenticator.logout(session_token);
    }

    @Override
    public boolean isInstantiated() {
        return instantiated_flag;
    }

    @Override
    public String currentDB() throws SessionReset {
        if (this.isInstantiated())
            return db_file;
        else
            throw new SessionReset("Session has not been instantiated.");
    }
}
