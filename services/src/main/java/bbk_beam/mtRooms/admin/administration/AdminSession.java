package bbk_beam.mtRooms.admin.administration;

import bbk_beam.mtRooms.admin.authentication.IAuthenticationSystem;
import bbk_beam.mtRooms.admin.authentication.Token;
import bbk_beam.mtRooms.admin.dto.Account;
import bbk_beam.mtRooms.admin.dto.AccountType;
import bbk_beam.mtRooms.admin.exception.AccountExistenceException;
import bbk_beam.mtRooms.admin.exception.AccountOverrideException;
import bbk_beam.mtRooms.admin.exception.RecordUpdateException;
import bbk_beam.mtRooms.db.IReservationDbAccess;
import bbk_beam.mtRooms.db.IUserAccDbAccess;
import bbk_beam.mtRooms.db.TimestampConverter;
import bbk_beam.mtRooms.db.exception.DbQueryException;
import bbk_beam.mtRooms.db.exception.SessionCorruptedException;
import bbk_beam.mtRooms.db.exception.SessionExpiredException;
import bbk_beam.mtRooms.db.exception.SessionInvalidException;
import bbk_beam.mtRooms.db.session.SessionType;
import eadjlib.datastructure.ObjectTable;
import eadjlib.logger.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
            if (this.administration.isSameAccount(admin_token, account_id)) {
                log.log_Warning("Detected attempt to re-activate current session account used for access!");
                throw new AccountOverrideException("Attempted to re-activate current session account.");
            } else {
                this.administration.activateAccount(account_id);
            }
        } catch (DbQueryException e) {
            log.log_Fatal("Could not activate user account [id: ", account_id, "] in records.");
            throw new RuntimeException("Could not activate user account in records.", e);
        }
    }

    @Override
    public void deactivateAccount(Token admin_token, Integer account_id) throws SessionInvalidException, SessionExpiredException, SessionCorruptedException, AccountExistenceException, AccountOverrideException, RuntimeException {
        try {
            checkTokenValidity(admin_token);
            if (this.administration.isSameAccount(admin_token, account_id)) {
                log.log_Error("Detected attempt to deactivate current session account used for access!");
                throw new AccountOverrideException("Attempted to deactivate current session account.");
            } else {
                this.administration.deactivateAccount(account_id);
            }
        } catch (DbQueryException e) {
            log.log_Fatal("Could not deactivate user account [id: ", account_id, "] in records.");
            throw new RuntimeException("Could not deactivate user account in records.", e);
        }
    }

    @Override
    public void deleteAccount(Token admin_token, Integer account_id) throws SessionInvalidException, SessionExpiredException, SessionCorruptedException, AccountExistenceException, AccountOverrideException, RuntimeException {
        try {
            checkTokenValidity(admin_token);
            if (this.administration.isSameAccount(admin_token, account_id)) {
                log.log_Error("Detected attempt to delete current session account used for access!");
                throw new AccountOverrideException("Attempted to delete current session account.");
            } else {
                this.administration.deleteAccount(account_id);
            }
        } catch (DbQueryException e) {
            log.log_Fatal("Could not delete user account [id: ", account_id, "] from records.");
            throw new RuntimeException("Could not delete user account from records.", e);
        }
    }

    @Override
    public List<Account> getAccounts(Token admin_token) throws SessionInvalidException, SessionExpiredException, SessionCorruptedException, RuntimeException {
        try {
            checkTokenValidity(admin_token);
            ObjectTable table = this.administration.getAccounts();
            List<Account> account_list = new ArrayList<>();
            for (int i = 1; i <= table.rowCount(); i++) {
                HashMap<String, Object> row = table.getRow(i);
                account_list.add(
                        new Account(
                                (Integer) row.get("id"),
                                (String) row.get("username"),
                                TimestampConverter.getDateObject((String) row.get("created")),
                                TimestampConverter.getDateObject((String) row.get("last_login")),
                                TimestampConverter.getDateObject((String) row.get("last_pwd_change")),
                                new AccountType(
                                        (Integer) row.get("type_id"),
                                        (String) row.get("description")
                                ),
                                ((Integer) row.get("active_state") != 0)
                        ));
            }
            return account_list;
        } catch (DbQueryException e) {
            log.log_Fatal("Could not fetch user accounts from records.");
            throw new RuntimeException("Could not fetch user accounts from records.", e);
        }
    }

    @Override
    public Account getAccount(Token admin_token, Integer account_id) throws AccountExistenceException, SessionInvalidException, SessionExpiredException, SessionCorruptedException, RuntimeException {
        try {
            checkTokenValidity(admin_token);
            ObjectTable table = this.administration.getAccount(account_id);
            if (!table.isEmpty()) {
                HashMap<String, Object> row = table.getRow(1);
                return new Account(
                        (Integer) row.get("id"),
                        (String) row.get("username"),
                        TimestampConverter.getDateObject((String) row.get("created")),
                        TimestampConverter.getDateObject((String) row.get("last_login")),
                        TimestampConverter.getDateObject((String) row.get("last_pwd_change")),
                        new AccountType(
                                (Integer) row.get("type_id"),
                                (String) row.get("description")
                        ),
                        ((Integer) row.get("active_state") != 0)
                );
            } else {
                log.log_Error("Account [", account_id, "] does not exist in records.");
                throw new AccountExistenceException("Account [" + account_id + "] does not exist in records.");
            }
        } catch (DbQueryException e) {
            log.log_Fatal("Could not fetch user account [id: ", account_id, "] from records.");
            throw new RuntimeException("Could not fetch user account from records.", e);
        }
    }

    @Override
    public Account getAccount(Token admin_token, String account_username) throws AccountExistenceException, SessionInvalidException, SessionExpiredException, SessionCorruptedException, RuntimeException {
        try {
            checkTokenValidity(admin_token);
            ObjectTable table = this.administration.getAccount(account_username);
            if (!table.isEmpty()) {
                HashMap<String, Object> row = table.getRow(1);
                return new Account(
                        (Integer) row.get("id"),
                        (String) row.get("username"),
                        TimestampConverter.getDateObject((String) row.get("created")),
                        TimestampConverter.getDateObject((String) row.get("last_login")),
                        TimestampConverter.getDateObject((String) row.get("last_pwd_change")),
                        new AccountType(
                                (Integer) row.get("type_id"),
                                (String) row.get("description")
                        ),
                        ((Integer) row.get("active_state") != 0)
                );
            } else {
                log.log_Error("No account with username '", account_username, "' exists in records.");
                throw new AccountExistenceException("No account with username '" + account_username + "' exists in records.");
            }
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
     * @throws SessionExpiredException   when the session is expired
     * @throws SessionCorruptedException when tracked and token expiry timestamps do not match for the token's ID
     * @throws SessionInvalidException   when the session is invalid (not logged-in or not admin)
     */
    private void checkTokenValidity(Token token) throws SessionExpiredException, SessionCorruptedException, SessionInvalidException {
        try {
            this.administration.checkValidity(token);
            if (!this.authenticator.hasValidAccessRights(token, SessionType.ADMIN)) {
                log.log_Error("Token [", token.getSessionId(), "] not valid for administrative access.");
                throw new SessionInvalidException("Token [" + token.getSessionId() + "] not valid for administrative access.");
            }
        } catch (SessionExpiredException e) {
            log.log_Error("Token [", token.getSessionId(), "] could not be validated: EXPIRED.");
            throw new SessionExpiredException("Token [" + token.getSessionId() + "] expired.", e);
        } catch (SessionCorruptedException e) {
            log.log_Error("Token [", token.getSessionId(), "] could not be validated: CORRUPTED.");
            throw new SessionCorruptedException("Token [" + token.getSessionId() + "] corrupted.", e);
        } catch (SessionInvalidException e) {
            log.log_Error("Token [", token.getSessionId(), "] could not be validated: INVALID.");
            throw new SessionInvalidException("Token [" + token.getSessionId() + "] invalid.", e);
        }
    }

}