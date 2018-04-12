package bbk_beam.mtRooms.uaa;

import bbk_beam.mtRooms.admin.authentication.IAuthenticationSystem;
import bbk_beam.mtRooms.admin.authentication.Token;
import bbk_beam.mtRooms.admin.exception.AuthenticationFailureException;
import bbk_beam.mtRooms.db.exception.SessionInvalidException;
import bbk_beam.mtRooms.db.session.SessionType;

public class Authenticator {
    private IAuthenticationSystem authentication_system;

    /**
     * Constructor
     *
     * @param authentication_system IAuthenticationSystem instance
     */
    Authenticator(IAuthenticationSystem authentication_system) {
        this.authentication_system = authentication_system;
    }

    /**
     * Create a session for a user
     *
     * @param username Username
     * @param password Password
     * @return Session token
     * @throws AuthenticationFailureException when username/password are not valid
     */
    public Token login(String username, String password) throws AuthenticationFailureException {
        return this.authentication_system.login(username, password);
    }

    /**
     * Logout from a session
     *
     * @param session_token Session token for the session to log out from
     * @throws SessionInvalidException when session is not valid
     */
    public void logout(Token session_token) throws SessionInvalidException {
        this.authentication_system.logout(session_token);
    }

    /**
     * Check for validity of a token for user level access
     *
     * @param session_token     Session token
     * @param user_session_type Session type of user
     * @return Valid state
     */
    public boolean hasValidAccessRights(Token session_token, SessionType user_session_type) {
        return this.authentication_system.hasValidAccessRights(session_token, user_session_type);
    }

    /**
     * Check for the current login state of a token
     *
     * @param session_token Session token
     * @return Login state
     */
    public boolean isLoggedIn(Token session_token) {
        return this.authentication_system.isLoggedIn(session_token);
    }

    /**
     * Gets the number of currently non-expired current tokens
     *
     * @return Unexpired token count
     */
    public int validTokenCount() {
        return this.authentication_system.validTokenCount();
    }

    /**
     * Removes all expired tokens from the session tracker
     */
    public void clearExpiredTokens() {
        this.authentication_system.clearExpiredTokens();
    }
}
