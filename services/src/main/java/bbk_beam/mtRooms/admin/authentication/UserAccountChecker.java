package bbk_beam.mtRooms.admin.authentication;

import bbk_beam.mtRooms.admin.exception.AuthenticationFailureException;
import bbk_beam.mtRooms.admin.exception.AuthenticationHasherException;
import bbk_beam.mtRooms.db.IUserAccDbAccess;
import bbk_beam.mtRooms.db.TimestampConverter;
import bbk_beam.mtRooms.db.exception.DbQueryException;
import bbk_beam.mtRooms.db.exception.SessionCorruptedException;
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
    private final int SESSION_TIMEOUT_HOURS = 12;
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
                + "UserAccount.id, UserAccount.username, UserAccount.pwd_hash, UserAccount.pwd_salt, UserAccount.active_state, "
                + "AccountType.description "
                + "FROM UserAccount "
                + "LEFT OUTER JOIN AccountType ON UserAccount.account_type_id = AccountType.id "
                + "WHERE username = '" + username + "'";
        try {
            log.log("Login attempt by user '", username, "'.");
            ObjectTable table = user_access.pullFromDB(query);
            HashMap<String, Object> row = table.getRow(1);
            Integer user_id = (Integer) row.get("id");
            String hash = (String) row.get("pwd_hash");
            String salt = (String) row.get("pwd_salt");
            if (PasswordHash.validateHash(password, salt, hash)) {
                log.log("User '", username, "' authenticated (#", user_id, ").");
                Instant now = Instant.now();
                Date created = Date.from(now);
                Date expiry = Date.from(now.plus(SESSION_TIMEOUT_HOURS, ChronoUnit.HOURS));
                String session_id = this.id_generator.nextSessionId();
                while (sessionID_to_Username_Map.containsValue(session_id)) {
                    log.log_Debug("Found user with session id '", session_id, "' already being tracked. Generating new session id.");
                    session_id = this.id_generator.nextSessionId();
                }
                sessionID_to_Username_Map.put(session_id, username);
                log.log("User '", username, "' assigned session id #", session_id, " (", created.toString(), " to ", expiry.toString(), ").");
                if (!updateLastLoginTimestamp(username, created))
                    log.log_Error("Failed update in records for last successful login timestamp (", created, ").");
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
            throw new AuthenticationFailureException("Problem encountered processing hash for user '" + username + "'.", e);
        }
    }

    @Override
    public void logout(Token session_token) throws SessionInvalidException {
        try {
            log.log("Logout initiated for session [", session_token.getSessionId(), "].");
            if (this.sessionID_to_Username_Map.containsKey(session_token.getSessionId())) {
                String username = this.sessionID_to_Username_Map.get(session_token.getSessionId());
                log.log("Session [", session_token.getSessionId(), "] mapped to user '", username, "'.");
            } else {
                log.log_Warning("Session [", session_token.getSessionId(), "] is not tracked in id-username map.");
            }
            this.user_access.closeSession(session_token.getSessionId());
            if (Date.from(Instant.now()).after(session_token.getExpiry())) {
                log.log_Debug("Session [", session_token.getSessionId(), "] was expired (", session_token.getExpiry(), ").");
            }
            log.log("Session [", session_token.getSessionId(), "] logout completed.");
        } catch (SessionInvalidException e) {
            log.log_Error("Session [", session_token.getSessionId(), "] is not valid.");
            throw new SessionInvalidException("Session [" + session_token.getSessionId() + "] is not valid.", e);
        }
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

    @Override
    public boolean isLoggedIn(Token session_token) {
        try {
            log.log_Debug("Checking user session '", session_token, "' is currently logged in.");
            this.user_access.checkValidity(session_token.getSessionId(), session_token.getExpiry());
            return true;
        } catch (SessionExpiredException e) {
            log.log("State: token [", session_token, "]'s session has expired.");
        } catch (SessionInvalidException e) {
            log.log_Warning("State: token [", session_token, "] is invalid.");
            e.printStackTrace();
        } catch (SessionCorruptedException e) {
            log.log_Error("State: token [", session_token, "] is corrupted.");
        }
        return false;
    }

    @Override
    public int validTokenCount() {
        return this.user_access.validSessionCount();
    }

    /**
     * Updates the last successful login timestamp in the records for a username
     *
     * @param username Username for the record to update
     * @param date     Date object of the last successful login
     * @return Success
     */
    private boolean updateLastLoginTimestamp(String username, Date date) {
        String query = "UPDATE UserAccount "
                + "SET last_login = \"" + TimestampConverter.getUTCTimestampString(date) + "\" "
                + "WHERE username = \"" + username + "\"";
        try {
            return this.user_access.pushToDB(query);
        } catch (DbQueryException e) {
            log.log_Error("Could not update last login timestamp for user '" + username + "'.");
            log.log_Exception(e);
        }
        return false;
    }
}
