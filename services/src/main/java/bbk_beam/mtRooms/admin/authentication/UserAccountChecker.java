package bbk_beam.mtRooms.admin.authentication;

import bbk_beam.mtRooms.admin.exception.AuthenticationFailureException;
import bbk_beam.mtRooms.db.IUserAccDbAccess;
import bbk_beam.mtRooms.db.exception.SessionExpiredException;
import bbk_beam.mtRooms.db.exception.SessionInvalidException;
import bbk_beam.mtRooms.db.session.SessionType;
import eadjlib.logger.Logger;

public class UserAccountChecker implements IAuthenticationSystem {
    private final Logger log = Logger.getLoggerInstance(UserAccountChecker.class.getName());
    private IUserAccDbAccess user_access;

    /**
     * Constructor
     *
     * @param user_access User account DB Access instance
     */
    public UserAccountChecker(IUserAccDbAccess user_access) {
        this.user_access = user_access;
    }

    @Override
    public Token login(String username, String password) throws AuthenticationFailureException {
        //TODO
        return null;
    }

    @Override
    public void logout(Token session_token) throws SessionInvalidException, SessionExpiredException {
        //TODO
    }

    @Override
    public boolean hasValidAccessRights(Token session_token, SessionType user_session_type) {
        try {
            SessionType type = this.user_access.getSessionType(session_token.getSessionId());
            log.log_Debug("Checking user session '", session_token.getSessionId(), "' [", type.name(), "] has [", user_session_type.name(), "] level access.");
            return user_session_type.level() >= type.level();
        } catch (SessionInvalidException e) {
            log.log_Error("Session '", session_token.getSessionId(), "' not valid.");
        }
        return false;
    }
}
