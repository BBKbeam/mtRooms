package bbk_beam.mtRooms.admin.administration.maintenance;

import bbk_beam.mtRooms.admin.authentication.PasswordHash;
import bbk_beam.mtRooms.admin.authentication.Token;
import bbk_beam.mtRooms.admin.exception.AccountExistenceException;
import bbk_beam.mtRooms.admin.exception.AccountOverrideException;
import bbk_beam.mtRooms.admin.exception.AuthenticationHasherException;
import bbk_beam.mtRooms.admin.exception.FailedRecordUpdate;
import bbk_beam.mtRooms.db.IUserAccDbAccess;
import bbk_beam.mtRooms.db.TimestampConverter;
import bbk_beam.mtRooms.db.exception.DbQueryException;
import bbk_beam.mtRooms.db.exception.SessionCorruptedException;
import bbk_beam.mtRooms.db.exception.SessionExpiredException;
import bbk_beam.mtRooms.db.exception.SessionInvalidException;
import bbk_beam.mtRooms.db.session.SessionType;
import eadjlib.datastructure.ObjectTable;
import eadjlib.logger.Logger;

import java.util.Date;
import java.util.HashMap;

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
     * Checks the validity of a session token
     *
     * @param token Session token
     * @throws SessionExpiredException   when session associated with token has expired
     * @throws SessionCorruptedException when tracked and token expiry timestamps do not match for the token's ID
     * @throws SessionInvalidException   when no tracked sessions is associated with token
     */
    public void checkValidity(Token token) throws SessionExpiredException, SessionCorruptedException, SessionInvalidException {
        this.db_access.checkValidity(token.getSessionId(), token.getExpiry());
    }

    /**
     * Checks that the associated account ID of the token is the same as the giver account ID
     *
     * @param token      Session token
     * @param account_id Account ID to verify the token against
     */
    public boolean isSameAccount(Token token, Integer account_id) {
        try {
            return this.db_access.getSessionAccountID(token.getSessionId()).equals(account_id);
        } catch (SessionInvalidException e) {
            return false;
        }
    }

    /**
     * Creates a new account
     *
     * @param account_type Account type to create
     * @param username     Username of the account to create
     * @param password     Password of the account to create
     * @throws FailedRecordUpdate     when adding new account fails
     * @throws AccountExistenceException when account with same name exists already
     */
    public void createNewAccount(SessionType account_type, String username, String password) throws FailedRecordUpdate, AccountExistenceException {
        try {
            //AccountType SessionType.value to db ID translation
            ObjectTable account_type_id = db_access.pullFromDB("SELECT id FROM AccountType WHERE description = \"" + account_type.name() + "\"");
            if (account_type_id.isEmpty()) {
                log.log_Fatal("Cannot get id for account type [", account_type.name(), "] from records.");
                throw new FailedRecordUpdate("Cannot get id for account type [" + account_type.name() + "] from records.");
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
                    log.log_Error("Could not create user account (u/n: ", username, "): process yielded no changes to records.");
                    throw new FailedRecordUpdate("Could not create user account (u/n: " + username + "): process yielded no changes to records.");
                } else {
                    log.log("User account (u/n: ", username, ", type: ", account_type.name(), ") created.");
                }
            } else {
                log.log_Error("Cannot addUsage new account (u/n: ", username, "): username already exists.");
                throw new AccountExistenceException("Cannot addUsage new account (u/n: " + username + "): username already exists.");
            }
        } catch (DbQueryException e) {
            log.log_Error("Failed to process query to database.");
            throw new FailedRecordUpdate("Failed to process query to database.", e);
        } catch (AuthenticationHasherException e) {
            log.log_Fatal("Could not create hash for account (u/n: ", username, ").");
            throw new FailedRecordUpdate("Could not create hash for account (u/n: " + username + ").", e);
        }
    }

    /**
     * Updates existing account's password
     *
     * @param account_id ID of account to update
     * @param password   New password
     * @throws FailedRecordUpdate     when updating account fails
     * @throws AccountExistenceException when account does not exist in the records
     * @throws AccountOverrideException  when new password is the same as old one
     */
    public void updateAccountPassword(Integer account_id, String password) throws FailedRecordUpdate, AccountExistenceException, AccountOverrideException {
        try {
            ObjectTable table = getAccount(account_id);
            if (!table.isEmpty()) {
                HashMap<String, Object> account_row = table.getRow(1);
                String salt = PasswordHash.createSalt();
                String hash = PasswordHash.createHash(password, salt);
                if (hash.equals(account_row.get("pwd_hash"))) {
                    log.log_Error("Updated password for account (id: ", account_id, ") is the same as the old one.");
                    throw new AccountOverrideException("New password is the same as new one given.");
                }
                String query = "UPDATE UserAccount SET "
                        + "pwd_hash = \"" + hash + "\", "
                        + "pwd_salt = \"" + salt + "\", "
                        + "last_pwd_change = \"" + TimestampConverter.getUTCTimestampString(new Date()) + "\" "
                        + "WHERE id = " + account_id;
                this.db_access.pushToDB(query);
                ObjectTable changes = db_access.pullFromDB("SELECT changes()");
                if (changes.getInteger(1, 1) == 0) {
                    log.log_Error("Could not update user account password (id: ", account_id, "): process yielded no changes to records.");
                    throw new FailedRecordUpdate("Could not update user account password (id: " + account_id + "): process yielded no changes to records.");
                } else {
                    log.log("User account password (id: ", account_id, ") updated.");
                }
            } else {
                log.log_Error("Cannot update account (id: ", account_id, "): account with this ID does not exist.");
                throw new AccountExistenceException("Cannot update account (id: " + account_id + "): account with this ID does not exist.");
            }
        } catch (DbQueryException e) {
            log.log_Error("Failed to process query to database.");
            throw new FailedRecordUpdate("Failed to process query to database.", e);
        } catch (AuthenticationHasherException e) {
            log.log_Fatal("Could not create hash for account (id: ", account_id, ").");
            throw new FailedRecordUpdate("Could not create hash for account (id: " + account_id + ").", e);
        }
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

    /**
     * Gets the list of all user account types
     *
     * @return List of account type
     * @throws DbQueryException when query to get account types fails
     */
    public ObjectTable getAccountTypes() throws DbQueryException {
        String query = "SELECT " +
                "AccountType.id, " +
                "AccountType.description " +
                "FROM AccountType";
        return db_access.pullFromDB(query);
    }

    /**
     * Runs the vacuum command on the connected user account database
     *
     * @throws DbQueryException when vacuuming query failed
     */
    public void optimiseDatabase() throws DbQueryException {
        try {
            this.db_access.pushToDB("VACUUM");
            log.log("User account database optimised.");
        } catch (DbQueryException e) {
            log.log_Error("Vacuuming failed.");
            throw new DbQueryException("Failed vacuuming of DB schema.", e);
        }
    }
}
