package bbk_beam.mtRooms.admin.administration;

import bbk_beam.mtRooms.admin.authentication.Token;
import bbk_beam.mtRooms.admin.exception.AccountExistenceException;
import bbk_beam.mtRooms.admin.exception.AccountOverrideException;
import bbk_beam.mtRooms.db.exception.SessionCorruptedException;
import bbk_beam.mtRooms.db.exception.SessionExpiredException;
import bbk_beam.mtRooms.db.exception.SessionInvalidException;
import bbk_beam.mtRooms.db.session.SessionType;
import eadjlib.datastructure.ObjectTable;

public interface IAdminSession {
    /**
     * Creates a new account
     *
     * @param admin_token  Administrator session token
     * @param account_type Account type to create
     * @param username     Username of the account to create
     * @param password     Password of the account to create
     * @throws SessionInvalidException   when administrator session is not valid
     * @throws SessionExpiredException   when current administrator session has expired
     * @throws SessionCorruptedException when tracked and token expiry timestamps do not match for the token's ID
     * @throws AccountExistenceException when account with same name exists already
     * @throws RuntimeException          when non-standard failure occurred during account creation in records
     */
    public void createNewAccount(Token admin_token, SessionType account_type, String username, String password) throws SessionInvalidException, SessionExpiredException, SessionCorruptedException, AccountExistenceException, RuntimeException;

    /**
     * Updates existing account's password
     *
     * @param admin_token Administrator or User session token
     * @param account_id  ID of account to update
     * @param password    New password
     * @throws SessionInvalidException   when session token is not valid
     * @throws SessionExpiredException   when current session has expired
     * @throws SessionCorruptedException when tracked and token expiry timestamps do not match for the token's ID
     * @throws AccountExistenceException when account does not exist in the records
     * @throws AccountOverrideException  when new password is the same as old one
     * @throws RuntimeException          when non-standard failure occurred during account creation in records
     */
    public void updateAccountPassword(Token admin_token, Integer account_id, String password) throws SessionInvalidException, SessionExpiredException, SessionCorruptedException, AccountExistenceException, AccountOverrideException, RuntimeException;

    /**
     * Activates an existing account
     *
     * @param admin_token Administrator session token
     * @param account_id  ID of account to activate
     * @throws SessionInvalidException   when administrator session is not valid
     * @throws SessionExpiredException   when current administrator session has expired
     * @throws SessionCorruptedException when tracked and token expiry timestamps do not match for the token's ID
     * @throws AccountExistenceException when account does not exist in the records
     * @throws AccountOverrideException  when account to activate is the current logged-in administrator's
     * @throws RuntimeException          when non-standard failure occurred during account activation
     */
    public void activateAccount(Token admin_token, Integer account_id) throws SessionInvalidException, SessionExpiredException, SessionCorruptedException, AccountExistenceException, AccountOverrideException, RuntimeException;

    /**
     * Deactivates an existing account
     *
     * @param admin_token Administrator session token
     * @param account_id  ID of account to deactivate
     * @throws SessionInvalidException   when administrator session is not valid
     * @throws SessionExpiredException   when current administrator session has expired
     * @throws SessionCorruptedException when tracked and token expiry timestamps do not match for the token's ID
     * @throws AccountExistenceException when account does not exist in the records
     * @throws AccountOverrideException  when account to deactivates is the current logged-in administrator's
     * @throws RuntimeException          when non-standard failure occurred during account deactivation
     */
    public void deactivateAccount(Token admin_token, Integer account_id) throws SessionInvalidException, SessionExpiredException, SessionCorruptedException, AccountExistenceException, AccountOverrideException, RuntimeException;

    /**
     * Deletes an existing  account record
     *
     * @param admin_token Administrator session token
     * @param account_id  ID of account to delete
     * @throws SessionInvalidException   when administrator session is not valid
     * @throws SessionExpiredException   when current administrator session has expired
     * @throws AccountExistenceException when account to delete does not exist in the records
     * @throws SessionCorruptedException when tracked and token expiry timestamps do not match for the token's ID
     * @throws AccountOverrideException  when account to delete is the current logged-in administrator's
     * @throws RuntimeException          when non-standard failure occurred during account removal from records
     */
    public void deleteAccount(Token admin_token, Integer account_id) throws SessionInvalidException, SessionExpiredException, SessionCorruptedException, AccountExistenceException, AccountOverrideException, RuntimeException;

    /**
     * @param admin_token Administrator session token
     * @return ObjectTable with all account records found
     * @throws SessionInvalidException   when administrator session is not valid
     * @throws SessionExpiredException   when current administrator session has expired
     * @throws SessionCorruptedException when tracked and token expiry timestamps do not match for the token's ID
     * @throws RuntimeException          when non-standard failure occurred during account fetching from records
     */
    public ObjectTable getAccounts(Token admin_token) throws SessionInvalidException, SessionExpiredException, SessionCorruptedException, RuntimeException;

    /**
     * Gets the records for an account
     *
     * @param admin_token Administrator session token
     * @param account_id  Account ID
     * @return ObjectTable containing the account's details
     * @throws SessionInvalidException   when administrator session is not valid
     * @throws SessionExpiredException   when current administrator session has expired
     * @throws SessionCorruptedException when tracked and token expiry timestamps do not match for the token's ID
     * @throws RuntimeException          when non-standard failure occurred during account fetching from records
     */
    public ObjectTable getAccount(Token admin_token, Integer account_id) throws SessionInvalidException, SessionExpiredException, SessionCorruptedException, RuntimeException;

    /**
     * Gets the records for an account
     *
     * @param admin_token      Administrator session token
     * @param account_username Account username
     * @return ObjectTable containing the account's details
     * @throws SessionInvalidException   when administrator session is not valid
     * @throws SessionExpiredException   when current administrator session has expired
     * @throws SessionCorruptedException when tracked and token expiry timestamps do not match for the token's ID
     * @throws RuntimeException          when non-standard failure occurred during account fetching from records
     */
    public ObjectTable getAccount(Token admin_token, String account_username) throws SessionInvalidException, SessionExpiredException, SessionCorruptedException, RuntimeException;

    /**
     * Optimises the reservation database
     *
     * @param admin_token Administrator session token
     * @return Success
     * @throws SessionInvalidException   when administrator session is not valid
     * @throws SessionExpiredException   when current administrator session has expired
     * @throws SessionCorruptedException when tracked and token expiry timestamps do not match for the token's ID
     */
    public boolean optimiseReservationDatabase(Token admin_token) throws SessionInvalidException, SessionExpiredException, SessionCorruptedException;

    /**
     * Optimises the user account database
     *
     * @param admin_token Administrator session token
     * @return Success
     * @throws SessionInvalidException   when administrator session is not valid
     * @throws SessionExpiredException   when current administrator session has expired
     * @throws SessionCorruptedException when tracked and token expiry timestamps do not match for the token's ID
     */
    public boolean optimiseUserAccountDatabase(Token admin_token) throws SessionInvalidException, SessionExpiredException, SessionCorruptedException;
}
