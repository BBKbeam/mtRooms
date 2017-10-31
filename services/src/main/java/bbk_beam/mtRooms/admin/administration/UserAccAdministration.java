package bbk_beam.mtRooms.admin.administration;

import bbk_beam.mtRooms.admin.authentication.PasswordHash;
import bbk_beam.mtRooms.admin.exception.AccountExistenceException;
import bbk_beam.mtRooms.admin.exception.AccountOverrideException;
import bbk_beam.mtRooms.admin.exception.AuthenticationHasherException;
import bbk_beam.mtRooms.admin.exception.RecordUpdateException;
import bbk_beam.mtRooms.db.IUserAccDbAccess;
import bbk_beam.mtRooms.db.TimestampConverter;
import bbk_beam.mtRooms.db.exception.DbQueryException;
import bbk_beam.mtRooms.db.session.SessionType;
import eadjlib.datastructure.ObjectTable;
import eadjlib.logger.Logger;

import java.util.Date;

public class UserAccAdministration {
    private final Logger log = Logger.getLoggerInstance(UserAccAdministration.class.getName());
    private IUserAccDbAccess db_access;

    /**
     * Constructor
     *
     * @param db_access UserAccDbAccess instance
     */
    public UserAccAdministration(IUserAccDbAccess db_access) {
        this.db_access = db_access;
    }

    /**
     * Creates a new account
     *
     * @param account_type Account type to create
     * @param username     Username of the account to create
     * @param password     Password of the account to create
     * @throws RecordUpdateException     when adding new account fails=
     * @throws AccountExistenceException when account with same name exists already
     */
    public void createNewAccount(SessionType account_type, String username, String password) throws RecordUpdateException, AccountExistenceException {
        try {
            //AccountType ID
            ObjectTable account_type_id = db_access.pullFromDB("SELECT id FROM AccountType WHERE description = \"" + account_type.name() + "\"");
            if (account_type_id.isEmpty()) {
                log.log_Fatal("Cannot get id for account type [", account_type.name(), "] from records.");
                throw new RecordUpdateException("Cannot get id for account type [" + account_type.name() + "] from records.");
            }
            Integer type_id = account_type_id.getInteger(1, 1);
            //UserAccount records
            ObjectTable table = getAccount(username);
            if (table.isEmpty()) {
                String salt = PasswordHash.createSalt();
                String hash = PasswordHash.createHash(password, salt);
                String query = "INSERT INTO UserAccount( "
                        + "username, pwd_hash, pwd_salt, created, last_pwd_change, account_type_id, active_state "
                        + ") VALUES( "
                        + "\"" + username + "\", "
                        + "\"" + hash + "\", "
                        + "\"" + salt + "\", "
                        + "\"" + TimestampConverter.getUTCTimestampString(new Date()) + "\", "
                        + "\"" + TimestampConverter.getUTCTimestampString(new Date()) + "\", "
                        + type_id + ", "
                        + "1 "
                        + ")";
                this.db_access.pushToDB(query);
                ObjectTable changes = db_access.pullFromDB("SELECT changes()");
                if (changes.getInteger(1, 1) == 0) {
                    log.log_Error("Could not create user account (u/n: ", username, ").");
                    throw new RecordUpdateException("Could not create user account (u/n: " + username + ").");
                } else {
                    log.log("User account (u/n: ", username, ", type: ", account_type.name(), ") created.");
                }
            } else {
                log.log_Error("Cannot add new account (u/n: ", username, "): username already exists.");
                throw new AccountExistenceException("Cannot add new account (u/n: " + username + "): username already exists.");
            }
        } catch (DbQueryException e) {
            log.log_Error("Failed to process query to database.");
            throw new RecordUpdateException("Failed to process query to database.", e);
        } catch (AuthenticationHasherException e) {
            log.log_Fatal("Could not create hash for account (u/n: ", username, ").");
            throw new RecordUpdateException("Could not create hash for account (u/n: " + username + ").", e);
        }
    }

    /**
     * Updates existing account's password
     *
     * @param account_id ID of account to update
     * @param password   New password
     * @throws AccountExistenceException when account does not exist in the records
     * @throws AccountOverrideException  when new password is the same as old one
     */
    public void updateAccountPassword(Integer account_id, String password) throws AccountExistenceException, AccountOverrideException {
        //TODO
    }

    /**
     * Activates an existing account
     *
     * @param account_id ID of account to activate
     * @throws DbQueryException          when querying the records fails
     * @throws AccountExistenceException when account does not exist in the records
     */
    public void activateAccount(Integer account_id) throws DbQueryException, AccountExistenceException {
        db_access.pushToDB("UPDATE UserAccount SET active_state = 1 WHERE id = " + account_id);
        ObjectTable changes = db_access.pullFromDB("SELECT changes()");
        if (changes.getInteger(1, 1) == 0) {
            log.log_Error("Cannot deactivate user account [", account_id, "]: questionable existence of record.");
            throw new AccountExistenceException("Cannot activate user account [" + account_id + "].");
        } else {
            log.log("User account [", account_id, "] activated.");
        }
    }

    /**
     * Deactivates an existing account
     *
     * @param account_id ID of account to deactivate
     * @throws DbQueryException          when querying the records fails
     * @throws AccountExistenceException when account does not exist in the records
     */
    public void deactivateAccount(Integer account_id) throws DbQueryException, AccountExistenceException {
        db_access.pushToDB("UPDATE UserAccount SET active_state = 0 WHERE id = " + account_id);
        ObjectTable changes = db_access.pullFromDB("SELECT changes()");
        if (changes.getInteger(1, 1) == 0) {
            log.log_Error("Cannot deactivate user account [", account_id, "]: questionable existence of record.");
            throw new AccountExistenceException("Cannot deactivate user account [" + account_id + "].");
        } else {
            log.log("User account [", account_id, "] deactivated.");
        }
    }

    /**
     * Deletes an existing  account record
     *
     * @param account_id ID of account to delete
     * @throws DbQueryException          when querying/deleting the records fails
     * @throws AccountExistenceException when account to delete does not exist in the records
     */
    public void deleteAccount(Integer account_id) throws DbQueryException, AccountExistenceException {
        db_access.pushToDB("DELETE FROM UserAccount WHERE id = " + account_id);
        ObjectTable changes = db_access.pullFromDB("SELECT changes()");
        if (changes.getInteger(1, 1) == 0) {
            log.log_Error("Cannot delete user account [", account_id, "]: questionable existence of record.");
            throw new AccountExistenceException("Cannot delete user account [" + account_id + "].");
        } else {
            log.log("User account [", account_id, "] deleted from records.");
        }
    }

    /**
     * Gets all the user accounts on record
     *
     * @return ObjectTable with all account records found
     * @throws DbQueryException when query to get account list fails
     */
    public ObjectTable getAccounts() throws DbQueryException {
        String query = "SELECT "
                + "UserAccount.id, "
                + "UserAccount.username, "
                + "UserAccount.pwd_hash, "
                + "UserAccount.pwd_salt, "
                + "UserAccount.created, "
                + "UserAccount.last_pwd_change, "
                + "UserAccount.last_login, "
                + "UserAccount.active_state, "
                + "AccountType.id AS type_id, "
                + "AccountType.description "
                + "FROM UserAccount "
                + "LEFT OUTER JOIN AccountType ON UserAccount.account_type_id = AccountType.id";
        return db_access.pullFromDB(query);
    }

    /**
     * Gets the records for an account
     *
     * @param account_id Account ID
     * @return ObjectTable containing the account's details
     * @throws DbQueryException when query to get account details fails
     */
    public ObjectTable getAccount(Integer account_id) throws DbQueryException {
        String query = "SELECT "
                + "UserAccount.id, "
                + "UserAccount.username, "
                + "UserAccount.pwd_hash, "
                + "UserAccount.pwd_salt, "
                + "UserAccount.created, "
                + "UserAccount.last_pwd_change, "
                + "UserAccount.last_login, "
                + "UserAccount.active_state, "
                + "AccountType.id AS type_id, "
                + "AccountType.description "
                + "FROM UserAccount "
                + "LEFT OUTER JOIN AccountType ON UserAccount.account_type_id = AccountType.id "
                + "WHERE UserAccount.id = " + account_id;
        return db_access.pullFromDB(query);
    }

    /**
     * Gets the records for an account
     *
     * @param account_username Account username
     * @return ObjectTable containing the account's details
     * @throws DbQueryException when query to get account details fails
     */
    public ObjectTable getAccount(String account_username) throws DbQueryException {
        String query = "SELECT "
                + "UserAccount.id, "
                + "UserAccount.username, "
                + "UserAccount.pwd_hash, "
                + "UserAccount.pwd_salt, "
                + "UserAccount.created, "
                + "UserAccount.last_pwd_change, "
                + "UserAccount.last_login, "
                + "UserAccount.active_state, "
                + "AccountType.id AS type_id, "
                + "AccountType.description "
                + "FROM UserAccount "
                + "LEFT OUTER JOIN AccountType ON UserAccount.account_type_id = AccountType.id "
                + "WHERE UserAccount.username = \"" + account_username + "\"";
        return db_access.pullFromDB(query);
    }
}
