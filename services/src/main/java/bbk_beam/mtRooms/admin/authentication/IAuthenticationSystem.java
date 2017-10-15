package bbk_beam.mtRooms.admin.authentication;

import bbk_beam.mtRooms.admin.exception.AuthenticationFailureException;
import bbk_beam.mtRooms.db.exception.SessionExpiredException;
import bbk_beam.mtRooms.db.exception.SessionInvalidException;

public interface IAuthenticationSystem {
    /**
     * Create a session for a user
     *
     * @param username Username
     * @param password Password
     * @return Session token
     * @throws AuthenticationFailureException when username/password are not valid
     */
    public Token login(String username, String password) throws AuthenticationFailureException;

    /**
     * Logout from a session
     *
     * @param session_token Session token for the session to log out from
     * @throws SessionInvalidException when session is not valid
     * @throws SessionExpiredException when session has already expired
     */
    public void logout(Token session_token) throws SessionInvalidException, SessionExpiredException;

    /**
     * Check for validity of a token for user level access
     *
     * @param session_token Session token
     * @return Valid state
     */
    public boolean isValidUser(Token session_token);

    /**
     * Check for validity of a token for admin level access
     *
     * @param session_token Session token
     * @return Valid state
     */
    public boolean isValidAdmin(Token session_token);
}
