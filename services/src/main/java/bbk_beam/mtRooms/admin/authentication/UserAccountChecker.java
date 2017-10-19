package bbk_beam.mtRooms.admin.authentication;

import bbk_beam.mtRooms.admin.exception.AuthenticationFailureException;
import bbk_beam.mtRooms.admin.exception.AuthenticationHasherException;
import bbk_beam.mtRooms.db.IUserAccDbAccess;
import bbk_beam.mtRooms.db.exception.DbQueryException;
import bbk_beam.mtRooms.db.exception.SessionExpiredException;
import bbk_beam.mtRooms.db.exception.SessionInvalidException;
import bbk_beam.mtRooms.db.session.SessionType;
import eadjlib.datastructure.ObjectTable;
import eadjlib.logger.Logger;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;

public class UserAccountChecker implements IAuthenticationSystem {
    private final Logger log = Logger.getLoggerInstance(UserAccountChecker.class.getName());
    private IUserAccDbAccess user_access;
    private SessionIdGenerator id_generator;
    private HashMap<String, String> sessionID_to_Username_Map;

    /**
     * Constructor
     *
     * @param user_access User account DB Access instance
     */
    public UserAccountChecker(IUserAccDbAccess user_access) {
        this.user_access = user_access;
        this.id_generator = new SessionIdGenerator();
        this.sessionID_to_Username_Map = new HashMap<>();
    }

    @Override
    public Token login(String username, String password) throws AuthenticationFailureException {
        String query = "SELECT "
                + "UserAccount.username, UserAccount.pwd_hash, UserAccount.pwd_salt, UserAccount.active_state, "
                + "AccountType.description "
                + "FROM UserAccount "
                + "LEFT OUTER JOIN AccountType ON UserAccount.account_type_id = AccountType.id "
                + "WHERE username = '" + username + "'";
        try {
            log.log("Login attempt by user '", username, "'.");
            ObjectTable table = user_access.pullFromDB(query);
            HashMap<String, Object> row = table.getRow(1);
            String hash = (String) row.get("pwd_hash");
            String salt = (String) row.get("pwd_salt");
            if (PasswordHash.validateHash(password, salt, hash)) {
                log.log("User '", username, "' authenticated.");
                Instant now = Instant.now();
                Date created = Date.from(now);
                Date expiry = Date.from(now.plus(12, ChronoUnit.HOURS));
                String session_id = this.id_generator.nextSessionId();
                while (sessionID_to_Username_Map.containsValue(session_id)) {
                    log.log_Debug("Found user with session id '", session_id, "' already being tracked. Generating new session id.");
                    session_id = this.id_generator.nextSessionId();
                }
                log.log("User '", username, "' assigned session id #", session_id, " (", created.toString(), " to ", expiry.toString(), ").");
                return new Token(session_id, created, expiry);
            } else {
                log.log_Error("User '", username, "' failed authentication.");
                throw new AuthenticationFailureException("User '" + username + "' failed authentication.");
            }
        } catch (DbQueryException e) {
            log.log_Error("Failed to get UserAccount details.");
            throw new AuthenticationFailureException("Failed to get userAccount details from records.", e);
        } catch (IndexOutOfBoundsException e) {
            log.log_Error("User '", username, "' does not exist.");
            throw new AuthenticationFailureException("User '" + username + "' does not exist.", e);
        } catch (AuthenticationHasherException e) {
            log.log_Error("Problem encountered processing hash for user '", username, "'.");
            throw new AuthenticationFailureException("Problem encountered processing hash for user '" + username + "'.");
        }
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
