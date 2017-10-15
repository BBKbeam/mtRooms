package bbk_beam.mtRooms.db.session;

import bbk_beam.mtRooms.db.exception.SessionException;
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
     * @throws SessionException when trying to add an already tracked session ID
     */
    public void addSession(String session_id, Date expiry, SessionType session_type) throws SessionException;

    /**
     * Removes a session from the tracker
     *
     * @param session_id Session ID
     * @throws SessionInvalidException when trying to remove non-tracked session ID
     */
    public void removeSession(String session_id) throws SessionInvalidException;

    /**
     * Gets the tracked status of a session
     *
     * @param session_id Session ID
     * @return Tracked state
     */
    public boolean isTracked(String session_id);

    /**
     * Gets the expiry timestamp of a tracked session
     *
     * @param session_id Session ID
     * @return Expiry date or null if not found
     */
    public Date getSessionExpiry(String session_id);

    /**
     * Gets the existence state of a session in the tracker
     *
     * @param session_id Session ID
     * @return Existence state in the tracker
     */
    public boolean exists(String session_id);

    /**
     * Gets the validity state (not expired) of a session
     *
     * @param session_id Session ID
     * @return Valid state
     * @throws SessionInvalidException when ID is not in the sessions tracked
     */
    public boolean isValid(String session_id) throws SessionInvalidException;

    /**
     * Gets the type of a session
     *
     * @param session_id Session ID
     * @return Session type
     * @throws SessionInvalidException when ID is not in the sessions tracked
     */
    public SessionType getSessionType(String session_id) throws SessionInvalidException;

    /**
     * Gets the number of sessions currently tracked
     *
     * @return Tracked sessions count
     */
    public int trackedCount();

    /**
     * Gets the no-tracking state
     *
     * @return No sessions tracked state
     */
    public boolean isEmpty();
}
