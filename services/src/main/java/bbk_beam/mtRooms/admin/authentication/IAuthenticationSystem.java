package bbk_beam.mtRooms.admin.authentication;

import bbk_beam.mtRooms.admin.exception.AuthenticationFailureException;
import bbk_beam.mtRooms.db.exception.SessionInvalidException;
import bbk_beam.mtRooms.db.session.SessionType;

public interface IAuthenticationSystem {
    /**
     * Create a session for a user
     *
     * @param username Username
     * @param password Password
     * @return Session token
     * @throws AuthenticationFailureException when username/password are not valid
     */
    Token login(String username, String password) throws AuthenticationFailureException;

    /**
     * Logout from a session
     *
     * @param session_token Session token for the session to log out from
     * @throws SessionInvalidException when session is not valid
     */
    void logout(Token session_token) throws SessionInvalidException;

    /**
     * Check for validity of a token for user level access
     *
     * @param session_token     Session token
     * @param user_session_type Session type of user
     * @return Valid state
     */
    boolean hasValidAccessRights(Token session_token, SessionType user_session_type);

    /**
     * Check for the current login state of a token
     *
     * @param session_token Session token
     * @return Login state
     */
    boolean isLoggedIn(Token session_token);

    /**
     * Gets the number of currently non-expired current tokens
     *
     * @return Unexpired token count
     */
    int validTokenCount();

    /**
     * Clears expired tokens from the tracker
     */
    void clearExpiredTokens();
}
