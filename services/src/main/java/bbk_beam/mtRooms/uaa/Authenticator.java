package bbk_beam.mtRooms.uaa;

import bbk_beam.mtRooms.ServiceDriver;
import bbk_beam.mtRooms.admin.authentication.IAuthenticationSystem;
import bbk_beam.mtRooms.admin.authentication.Token;
import bbk_beam.mtRooms.admin.exception.AuthenticationFailureException;
import bbk_beam.mtRooms.db.exception.SessionInvalidException;
import bbk_beam.mtRooms.db.session.SessionType;

public class Authenticator implements IAuthenticationSystem {
    private IAuthenticationSystem authentication_system;

    /**
     * Constructor
     *
     * @param driver Service layer driver
     */
    Authenticator(ServiceDriver driver) {
        this.authentication_system = driver.getAuthenticationSystem();
    }

    @Override
    public Token login(String username, String password) throws AuthenticationFailureException {
        return this.authentication_system.login(username, password);
    }

    @Override
    public void logout(Token session_token) throws SessionInvalidException {
        this.authentication_system.logout(session_token);
    }

    @Override
    public boolean hasValidAccessRights(Token session_token, SessionType user_session_type) {
        return this.authentication_system.hasValidAccessRights(session_token, user_session_type);
    }

    @Override
    public boolean isLoggedIn(Token session_token) {
        return this.authentication_system.isLoggedIn(session_token);
    }
}
