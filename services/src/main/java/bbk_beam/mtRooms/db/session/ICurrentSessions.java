package bbk_beam.mtRooms.db.session;

import bbk_beam.mtRooms.db.exception.SessionCorruptedException;
import bbk_beam.mtRooms.db.exception.SessionException;
import bbk_beam.mtRooms.db.exception.SessionExpiredException;
import bbk_beam.mtRooms.db.exception.SessionInvalidException;

import java.util.Date;

/**
 * Interface for accessing current tracked sessions
 */
public interface ICurrentSessions {
    /**
     * Adds a session to the tracker
     *
     * @param session_id   Session ID
     * @param expiry       Expiry timestamp
     * @param session_type Session type
     * @param account_id   Associated account ID
     * @throws SessionException when trying to add an already tracked session ID
     */
    void addSession(String session_id, Date expiry, SessionType session_type, Integer account_id) throws SessionException;

    /**
     * Removes a session from the tracker
     *
     * @param session_id Session ID
     * @throws SessionInvalidException when trying to remove non-tracked session ID
     */
    void removeSession(String session_id) throws SessionInvalidException;

    /**
     * Gets the tracked status of a session
     *
     * @param session_id Session ID
     * @return Tracked state
     */
    boolean isTracked(String session_id);

    /**
     * Gets the expiry timestamp of a tracked session
     *
     * @param session_id Session ID
     * @return Expiry date
     * @throws SessionInvalidException when ID is not in the sessions tracked
     */
    Date getSessionExpiry(String session_id) throws SessionInvalidException;

    /**
     * Gets the type of a session
     *
     * @param session_id Session ID
     * @return Session type
     * @throws SessionInvalidException when ID is not in the sessions tracked
     */
    SessionType getSessionType(String session_id) throws SessionInvalidException;

    /**
     * Gets the associated account ID for a tracked session
     *
     * @param session_id Session ID
     * @return Account ID
     * @throws SessionInvalidException when ID is not in the sessions tracked
     */
    Integer getSessionAccountID(String session_id) throws SessionInvalidException;

    /**
     * Gets the existence state of a session in the tracker
     *
     * @param session_id Session ID
     * @return Existence state in the tracker
     */
    boolean exists(String session_id);

    /**
     * Gets the validity state (not expired) of a session
     *
     * @param session_id Session ID
     * @throws SessionExpiredException when session is expired
     * @throws SessionInvalidException when ID is not in the sessions tracked
     */
    void checkValidity(String session_id) throws SessionExpiredException, SessionInvalidException;

    /**
     * Gets the validity state (not expired) of a session
     *
     * @param session_id     Session ID
     * @param session_expiry Session's expiry timestamp in token
     * @throws SessionExpiredException   when session is expired
     * @throws SessionCorruptedException when given expiry does not match expiry tracked.
     * @throws SessionInvalidException   when ID is not in the sessions tracked
     */
    void checkValidity(String session_id, Date session_expiry) throws SessionExpiredException, SessionCorruptedException, SessionInvalidException;

    /**
     * Gets the number of sessions currently tracked
     *
     * @return Tracked sessions count
     */
    int trackedCount();

    /**
     * Gets the no-tracking state
     *
     * @return No sessions tracked state
     */
    boolean isEmpty();
}
