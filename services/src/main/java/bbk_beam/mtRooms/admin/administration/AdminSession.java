package bbk_beam.mtRooms.admin.administration;

import bbk_beam.mtRooms.admin.authentication.IAuthenticationSystem;
import bbk_beam.mtRooms.admin.authentication.Token;
import bbk_beam.mtRooms.admin.exception.AccountExistenceException;
import bbk_beam.mtRooms.admin.exception.AccountOverrideException;
import bbk_beam.mtRooms.admin.exception.RecordUpdateException;
import bbk_beam.mtRooms.db.IReservationDbAccess;
import bbk_beam.mtRooms.db.IUserAccDbAccess;
import bbk_beam.mtRooms.db.exception.SessionExpiredException;
import bbk_beam.mtRooms.db.exception.SessionInvalidException;
import bbk_beam.mtRooms.db.session.SessionType;
import eadjlib.datastructure.ObjectTable;
import eadjlib.logger.Logger;

import java.util.Date;

public class AdminSession implements IAdminSession {
    private final Logger log = Logger.getLoggerInstance(AdminSession.class.getName());
    private UserAccAdministration administration;
    private ReservationDbMaintenance maintenance;
    private IAuthenticationSystem authenticator;

    /**
     * Constructor
     *
     * @param reservationDbAccess  IReservationDbAccess instance
     * @param userAccDbAccess      IUserAccDbAccess instance
     * @param authenticationSystem IAuthenticationSystem instance
     */
    public AdminSession(IReservationDbAccess reservationDbAccess, IUserAccDbAccess userAccDbAccess, IAuthenticationSystem authenticationSystem) {
        this.administration = new UserAccAdministration(userAccDbAccess);
        this.maintenance = new ReservationDbMaintenance(reservationDbAccess);
        this.authenticator = authenticationSystem;
    }

    /**
     * Constructor (injector used for testing)
     *
     * @param administration_module UserAccAdministration instance
     * @param maintenance_module    ReservationDbMaintenance instance
     * @param authenticationSystem  IAuthenticationSystem instance
     */
    public AdminSession(UserAccAdministration administration_module, ReservationDbMaintenance maintenance_module, IAuthenticationSystem authenticationSystem) {
        this.administration = administration_module;
        this.maintenance = maintenance_module;
        this.authenticator = authenticationSystem;
    }

    @Override
    public void createNewAccount(Token admin_token, SessionType account_type, String username, String password) throws SessionInvalidException, SessionExpiredException, AccountExistenceException, RuntimeException {
        try {
            checkTokenValidity(admin_token);
            this.administration.createNewAccount(account_type, username, password);
        } catch (RecordUpdateException e) {
            log.log_Fatal("Could not add user account to records.");
            throw new RuntimeException("Could not add user account to records.", e);
        }
    }

    @Override
    public void updateAccountPassword(Token admin_token, Integer account_id, String password) throws SessionInvalidException, SessionExpiredException, AccountExistenceException, AccountOverrideException, RuntimeException {
        try {
            checkTokenValidity(admin_token);
            this.administration.updateAccountPassword(account_id, password);
        } catch (RecordUpdateException e) {
            log.log_Fatal("Could not add user account to records.");
            throw new RuntimeException("Could not add user account to records.", e);
        }
    }

    @Override
    public void activateAccount(Token admin_token, Integer account_id) throws SessionInvalidException, SessionExpiredException, AccountExistenceException, AccountOverrideException {
        //TODO
    }

    @Override
    public void deactivateAccount(Token admin_token, Integer account_id) throws SessionInvalidException, SessionExpiredException, AccountExistenceException, AccountOverrideException {
        //TODO
    }

    @Override
    public void deleteAccount(Token admin_token, Integer account_id) throws SessionInvalidException, SessionExpiredException, AccountExistenceException, AccountOverrideException {
        //TODO
    }

    @Override
    public ObjectTable getAccounts(Token admin_token) throws SessionInvalidException, SessionExpiredException {
        //TODO
        return null;
    }

    @Override
    public ObjectTable getAccount(Token admin_token, Integer account_id) throws SessionInvalidException, SessionExpiredException, AccountExistenceException {
        //TODO
        return null;
    }

    @Override
    public ObjectTable getAccount(Token admin_token, String account_username) throws SessionInvalidException, SessionExpiredException, AccountExistenceException {
        //TODO
        return null;
    }

    private void checkTokenValidity(Token token) throws SessionInvalidException, SessionExpiredException {
        if (token.getExpiry().after(new Date())) {
            log.log_Error("Token [", token.getSessionId(), "] has expired.");
            throw new SessionExpiredException("Token expired.");
        }
        if (!this.authenticator.hasValidAccessRights(token, SessionType.ADMIN)) {
            log.log_Error("Token [", token.getSessionId(), "] not valid for administrative access.");
            throw new SessionInvalidException("Token is not valid for administrative access.");
        }
    }
}
