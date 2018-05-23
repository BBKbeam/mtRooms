package bbk_beam.mtRooms.uaa;

import bbk_beam.mtRooms.admin.authentication.Token;
import bbk_beam.mtRooms.admin.exception.AuthenticationFailureException;
import bbk_beam.mtRooms.db.exception.SessionInvalidException;
import bbk_beam.mtRooms.uaa.exception.*;

public interface ISessionDriver {

    /**
     * Spool all component instances for a session
     *
     * @param db_file_name Target database file name
     * @throws SessionActive         when trying to initialise over a current spooled session
     * @throws FailedSessionSpooling when spooling failed
     */
    void init(String db_file_name) throws SessionActive, FailedSessionSpooling;

    /**
     * Resets all the spooled instances to null
     *
     * @throws SessionLocked when opened sessions are using the spooled instances of the components
     */
    void reset() throws SessionLocked;

    /**
     * Gets the IAuthenticatedFrontDesk instance
     *
     * @param session_token Session token
     * @return IAuthenticatedFrontDesk implementation instance
     * @throws ServerSessionInactive                when the SessionDriver has not been initialised
     * @throws AuthenticationFailureException when the token has not got access right to the resource
     */
    IAuthenticatedFrontDesk getFrontDeskInstance(Token session_token) throws ServerSessionInactive, AuthenticationFailureException;

    /**
     * Gets the IAuthenticatedAdministration instance
     *
     * @param session_token Session token
     * @return IAuthenticatedAdministration implementation instance
     * @throws ServerSessionInactive                when the SessionDriver has not been initialised
     * @throws AuthenticationFailureException when the token has not got access right to the resource
     */
    IAuthenticatedAdministration getAdministrationInstance(Token session_token) throws ServerSessionInactive, AuthenticationFailureException;

    /**
     * Gets the IAuthenticatedRevenuePersonnel instance
     *
     * @param session_token Session token
     * @return IAuthenticatedRevenuePersonnel implementation instance
     * @throws ServerSessionInactive                when the SessionDriver has not been initialised
     * @throws AuthenticationFailureException when the token has not got access right to the resource
     */
    IAuthenticatedRevenuePersonnel getRevenuePersonnelInstance(Token session_token) throws ServerSessionInactive, AuthenticationFailureException;

    /**
     * Gets the IAuthenticatedLogisticsPersonnel instance
     *
     * @param session_token Session token
     * @return IAuthenticatedLogisticsPersonnel implementation instance
     * @throws ServerSessionInactive                when the SessionDriver has not been initialised
     * @throws AuthenticationFailureException when the token has not got access right to the resource
     */
    IAuthenticatedLogisticsPersonnel getLogisticsPersonnelInstance(Token session_token) throws ServerSessionInactive, AuthenticationFailureException;

    /**
     * Create a session for a user
     *
     * @param username Username
     * @param password Password
     * @return Session token
     * @throws AuthenticationFailureException when username/password are not valid
     * @throws ServerSessionInactive                when session has not been initiated
     */
    Token login(String username, String password) throws AuthenticationFailureException, ServerSessionInactive;

    /**
     * Logout from a session
     *
     * @param session_token Session token for the session to log out from
     * @throws SessionInvalidException when session is not valid
     * @throws ServerSessionInactive         when session has not been initiated
     */
    void logout(Token session_token) throws SessionInvalidException, ServerSessionInactive;

    /**
     * Gets the instantiated state
     *
     * @return Driver instantiated state
     */
    boolean isInstantiated();

    /**
     * Gets the file name of the currently connected DB used
     *
     * @return Connected DB file name
     * @throws SessionReset when session is not connected to a db
     */
    String currentDB() throws SessionReset;
}
