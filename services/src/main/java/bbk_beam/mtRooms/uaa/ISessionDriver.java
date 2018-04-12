package bbk_beam.mtRooms.uaa;

import bbk_beam.mtRooms.admin.authentication.Token;
import bbk_beam.mtRooms.admin.exception.AuthenticationFailureException;
import bbk_beam.mtRooms.db.exception.SessionInvalidException;
import bbk_beam.mtRooms.uaa.exception.FailedSessionSpooling;
import bbk_beam.mtRooms.uaa.exception.SessionActive;
import bbk_beam.mtRooms.uaa.exception.SessionInactive;
import bbk_beam.mtRooms.uaa.exception.SessionLocked;

public interface ISessionDriver {

    /**
     * Spool all component instances for a session
     *
     * @param db_file_name Target database file name
     * @throws SessionActive         when trying to initialise over a current spooled session
     * @throws FailedSessionSpooling when spooling failed
     */
    public void init(String db_file_name) throws SessionActive, FailedSessionSpooling;

    /**
     * Resets all the spooled instances to null
     *
     * @throws SessionLocked when opened sessions are using the spooled instances of the components
     */
    public void reset() throws SessionLocked;

    /**
     * Gets the IAuthenticatedFrontDesk instance
     *
     * @param session_token Session token
     * @return IAuthenticatedFrontDesk implementation instance
     * @throws SessionInactive                when the SessionDriver has not been initialised
     * @throws AuthenticationFailureException when the token has not got access right to the resource
     */
    public IAuthenticatedFrontDesk getFrontDeskInstance(Token session_token) throws SessionInactive, AuthenticationFailureException;

    /**
     * Gets the IAuthenticatedAdministration instance
     *
     * @param session_token Session token
     * @return IAuthenticatedAdministration implementation instance
     * @throws SessionInactive                when the SessionDriver has not been initialised
     * @throws AuthenticationFailureException when the token has not got access right to the resource
     */
    public IAuthenticatedAdministration getAdministrationInstance(Token session_token) throws SessionInactive, AuthenticationFailureException;

    /**
     * Gets the IAuthenticatedRevenuePersonnel instance
     *
     * @param session_token Session token
     * @return IAuthenticatedRevenuePersonnel implementation instance
     * @throws SessionInactive                when the SessionDriver has not been initialised
     * @throws AuthenticationFailureException when the token has not got access right to the resource
     */
    public IAuthenticatedRevenuePersonnel getRevenuePersonnelInstance(Token session_token) throws SessionInactive, AuthenticationFailureException;

    /**
     * Gets the IAuthenticatedLogisticsPersonnel instance
     *
     * @param session_token Session token
     * @return IAuthenticatedLogisticsPersonnel implementation instance
     * @throws SessionInactive                when the SessionDriver has not been initialised
     * @throws AuthenticationFailureException when the token has not got access right to the resource
     */
    public IAuthenticatedLogisticsPersonnel getLogisticsPersonnelInstance(Token session_token) throws SessionInactive, AuthenticationFailureException;

    /**
     * Create a session for a user
     *
     * @param username Username
     * @param password Password
     * @return Session token
     * @throws AuthenticationFailureException when username/password are not valid
     * @throws SessionInactive                when session has not been initiated
     */
    public Token login(String username, String password) throws AuthenticationFailureException, SessionInactive;

    /**
     * Logout from a session
     *
     * @param session_token Session token for the session to log out from
     * @throws SessionInvalidException when session is not valid
     * @throws SessionInactive         when session has not been initiated
     */
    public void logout(Token session_token) throws SessionInvalidException, SessionInactive;
}
