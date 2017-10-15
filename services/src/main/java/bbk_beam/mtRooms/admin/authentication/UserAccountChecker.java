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
    public boolean isValidUser(Token session_token) {
        try {
            SessionType type = this.user_access.getSessionType(session_token.getSessionId());
            switch (type) {
                case ADMIN: //Admins have user rights
                    log.log("User session '", session_token.getSessionId(), "' [ADMIN] verified.");
                    return this.user_access.checkValidity(session_token.getSessionId());
                case USER:
                    log.log("User session '", session_token.getSessionId(), "' [USER] verified.");
                    return this.user_access.checkValidity(session_token.getSessionId());
            }
        } catch (SessionInvalidException e) {
            log.log_Error("Session '", session_token.getSessionId(), "' not valid.");
        }
        return false;
    }

    @Override
    public boolean isValidAdmin(Token session_token) {
        try {
            SessionType type = this.user_access.getSessionType(session_token.getSessionId());
            switch (type) {
                case ADMIN: //Admins have user rights
                    log.log("Admin session '", session_token.getSessionId(), "' [ADMIN] verified.");
                    return this.user_access.checkValidity(session_token.getSessionId());
                default:
                    return false;
            }
        } catch (SessionInvalidException e) {
            log.log_Error("Session '", session_token.getSessionId(), "' not valid.");
        }
        return false;
    }
}
