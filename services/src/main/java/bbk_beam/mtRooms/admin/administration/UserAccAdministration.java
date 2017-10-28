package bbk_beam.mtRooms.admin.administration;

import bbk_beam.mtRooms.admin.exception.AccountExistenceException;
import bbk_beam.mtRooms.admin.exception.AccountOverrideException;
import bbk_beam.mtRooms.db.IUserAccDbAccess;
import bbk_beam.mtRooms.db.exception.DbQueryException;
import bbk_beam.mtRooms.db.session.SessionType;
import eadjlib.datastructure.ObjectTable;

public class UserAccAdministration {
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
     * @throws AccountExistenceException when account with same name exists already
     */
    public void createNewAccount(SessionType account_type, String username, String password) throws AccountExistenceException {
        //TODO
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
     * Deactivates an existing account
     *
     * @param account_id ID of account to deactivate
     * @throws AccountExistenceException when account does not exist in the records
     * @throws AccountOverrideException  when account to deactivates is the current logged-in administrator's
     */
    public void deactivateAccount(Integer account_id) throws AccountExistenceException, AccountOverrideException {
        //TODO
    }

    /**
     * Deletes an existing  account record
     *
     * @param account_id ID of account to delete
     * @throws AccountExistenceException when account to delete does not exist in the records
     * @throws AccountOverrideException  when account to delete is the current logged-in administrator's
     */
    public void deleteAccount(Integer account_id) throws AccountExistenceException, AccountOverrideException {
        //TODO
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
                + "AccountType.description "
                + "FROM UserAccount "
                + "LEFT OUTER JOIN AccountType ON UserAccount.account_type_id = AccountType.id"
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
                + "AccountType.description "
                + "FROM UserAccount "
                + "LEFT OUTER JOIN AccountType ON UserAccount.account_type_id = AccountType.id"
                + "WHERE UserAccount.username = \"" + account_username + "\"";
        return db_access.pullFromDB(query);
    }

}
