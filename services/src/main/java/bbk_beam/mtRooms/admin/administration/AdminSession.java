package bbk_beam.mtRooms.admin.administration;

import bbk_beam.mtRooms.admin.authentication.IAuthenticationSystem;
import bbk_beam.mtRooms.admin.authentication.Token;
import bbk_beam.mtRooms.admin.exception.AccountExistenceException;
import bbk_beam.mtRooms.admin.exception.AccountOverrideException;
import bbk_beam.mtRooms.admin.exception.RecordUpdateException;
import bbk_beam.mtRooms.db.IReservationDbAccess;
import bbk_beam.mtRooms.db.IUserAccDbAccess;
import bbk_beam.mtRooms.db.exception.DbQueryException;
import bbk_beam.mtRooms.db.exception.SessionCorruptedException;
import bbk_beam.mtRooms.db.exception.SessionExpiredException;
import bbk_beam.mtRooms.db.exception.SessionInvalidException;
import bbk_beam.mtRooms.db.session.SessionType;
import eadjlib.datastructure.ObjectTable;
import eadjlib.logger.Logger;

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
    public void createNewAccount(Token admin_token, SessionType account_type, String username, String password) throws SessionInvalidException, SessionExpiredException, SessionCorruptedException, AccountExistenceException, RuntimeException {
        try {
            checkTokenValidity(admin_token);
            this.administration.createNewAccount(account_type, username, password);
        } catch (RecordUpdateException e) {
            log.log_Fatal("Could not add user account [id: ", username, "] to records.");
            throw new RuntimeException("Could not add user account to records.", e);
        }
    }

    @Override
    public void updateAccountPassword(Token admin_token, Integer account_id, String password) throws SessionInvalidException, SessionExpiredException, SessionCorruptedException, AccountExistenceException, AccountOverrideException, RuntimeException {
        try {
            checkTokenValidity(admin_token);
            this.administration.updateAccountPassword(account_id, password);
        } catch (RecordUpdateException e) {
            log.log_Fatal("Could not update user account [id: ", account_id, "] in records.");
            throw new RuntimeException("Could not update user account in records.", e);
        }
    }

    @Override
    public void activateAccount(Token admin_token, Integer account_id) throws SessionInvalidException, SessionExpiredException, SessionCorruptedException, AccountExistenceException, AccountOverrideException, RuntimeException {
        try {
            checkTokenValidity(admin_token);
            this.administration.activateAccount(account_id);
        } catch (DbQueryException e) {
            log.log_Fatal("Could not activate user account [id: ", account_id, "] in records.");
            throw new RuntimeException("Could not activate user account in records.", e);
        }
    }

    @Override
    public void deactivateAccount(Token admin_token, Integer account_id) throws SessionInvalidException, SessionExpiredException, SessionCorruptedException, AccountExistenceException, AccountOverrideException, RuntimeException {
        try {
            checkTokenValidity(admin_token);
            this.administration.deactivateAccount(account_id);
        } catch (DbQueryException e) {
            log.log_Fatal("Could not deactivate user account [id: ", account_id, "] in records.");
            throw new RuntimeException("Could not deactivate user account in records.", e);
        }
    }

    @Override
    public void deleteAccount(Token admin_token, Integer account_id) throws SessionInvalidException, SessionExpiredException, SessionCorruptedException, AccountExistenceException, AccountOverrideException, RuntimeException {
        try {
            checkTokenValidity(admin_token);
            this.administration.deleteAccount(account_id);
        } catch (DbQueryException e) {
            log.log_Fatal("Could not delete user account [id: ", account_id, "] from records.");
            throw new RuntimeException("Could not delete user account from records.", e);
        }
    }

    @Override
    public ObjectTable getAccounts(Token admin_token) throws SessionInvalidException, SessionExpiredException, SessionCorruptedException, RuntimeException {
        try {
            checkTokenValidity(admin_token);
            return this.administration.getAccounts();
        } catch (DbQueryException e) {
            log.log_Fatal("Could not fetch user accounts from records.");
            throw new RuntimeException("Could not fetch user accounts from records.", e);
        }
    }

    @Override
    public ObjectTable getAccount(Token admin_token, Integer account_id) throws SessionInvalidException, SessionExpiredException, SessionCorruptedException, AccountExistenceException, RuntimeException {
        try {
            checkTokenValidity(admin_token);
            return this.administration.getAccount(account_id);
        } catch (DbQueryException e) {
            log.log_Fatal("Could not fetch user account [id: ", account_id, "] from records.");
            throw new RuntimeException("Could not fetch user account from records.", e);
        }
    }

    @Override
    public ObjectTable getAccount(Token admin_token, String account_username) throws SessionInvalidException, SessionExpiredException, SessionCorruptedException, AccountExistenceException, RuntimeException {
        try {
            checkTokenValidity(admin_token);
            return this.administration.getAccount(account_username);
        } catch (DbQueryException e) {
            log.log_Fatal("Could not fetch user account [u/n: ", account_username, "] from records.");
            throw new RuntimeException("Could not fetch user account from records.", e);
        }
    }

    @Override
    public boolean optimiseReservationDatabase(Token admin_token) throws SessionInvalidException, SessionExpiredException, SessionCorruptedException {
        try {
            checkTokenValidity(admin_token);
            this.maintenance.vacuumDatabase(admin_token);
            return true;
        } catch (DbQueryException e) {
            log.log_Error("Problem encountered when trying to optimise the reservation database.");
        }
        return false;
    }

    @Override
    public boolean optimiseUserAccountDatabase(Token admin_token) throws SessionInvalidException, SessionExpiredException, SessionCorruptedException {
        try {
            checkTokenValidity(admin_token);
            this.administration.optimiseDatabase();
            return true;
        } catch (DbQueryException e) {
            log.log_Error("Problem encountered when trying to optimise the user account database.");
        }
        return false;
    }

    /**
     * Checks the credential used for the admin session
     *
     * @param token Session token
     * @throws SessionInvalidException   when the session is invalid (not logged-in or not admin)
     * @throws SessionExpiredException   when the session is expired
     * @throws SessionCorruptedException when tracked and token expiry timestamps do not match for the token's ID
     */
    private void checkTokenValidity(Token token) throws SessionInvalidException, SessionExpiredException, SessionCorruptedException {
        if (!this.administration.isValid(token)) {
            log.log_Error("Token [", token.getSessionId(), "] could not be validated.");
            throw new SessionInvalidException("Token [" + token.getSessionId() + "] could not be validated.");
        }
        if (!this.authenticator.hasValidAccessRights(token, SessionType.ADMIN)) {
            log.log_Error("Token [", token.getSessionId(), "] not valid for administrative access.");
            throw new SessionInvalidException("Token is not valid for administrative access.");
        }
    }
}
