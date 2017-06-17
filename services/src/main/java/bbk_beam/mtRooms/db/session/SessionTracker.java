package bbk_beam.mtRooms.db.session;

import bbk_beam.mtRooms.db.ReservationDbAccess;
import bbk_beam.mtRooms.db.exception.InvalidSessionException;
import bbk_beam.mtRooms.db.exception.SessionException;
import eadjlib.logger.Logger;

import java.util.Date;
import java.util.HashMap;

public class SessionTracker {
    private final Logger log = Logger.getLoggerInstance(ReservationDbAccess.class.getName());
    private HashMap<String, Date> tracker;

    /**
     * Constructor
     */
    public SessionTracker() {
        new HashMap<>();
    }

    /**
     * Adds a session to the tracker
     *
     * @param session_id Session ID
     * @param expiry     Expiry timestamp
     * @throws SessionException when trying to add an already tracked session ID
     */
    public void addSession(String session_id, Date expiry) throws SessionException {
        if (this.tracker.containsValue(session_id)) {
            throw new SessionException("Trying to track an already tracked session ID");
        } else {
            this.tracker.put(session_id, expiry);
        }
    }

    /**
     * Removes a session from the tracker
     *
     * @param session_id Session ID
     * @throws InvalidSessionException when trying to remove non-tracked session ID
     */
    public void removeSession(String session_id) throws InvalidSessionException {
        if (this.tracker.remove(session_id) == null) {
            throw new InvalidSessionException("Trying to remove non tracked session ID.");
        }
    }

    /**
     * Gets the tracked status of a session
     *
     * @param session_id Session ID
     * @return Tracked state
     */
    boolean isTracked(String session_id) {
        return this.tracker.containsKey(session_id);
    }

    /**
     * Gets the expiry timestamp of a tracked session
     *
     * @param session_id Session ID
     * @return Expiry date or null if not found
     */
    Date getSessionType(String session_id) {
        return this.tracker.get(session_id);
    }

    /**
     * Gets the existence state of a session in the tracker
     *
     * @param session_id Session ID
     * @return Existence state in the tracker
     */
    boolean exists(String session_id) {
        return this.tracker.containsKey(session_id);
    }

    /**
     * Gets the validity state (not expired) of a session
     *
     * @param session_id Session ID
     * @return Valid state
     * @throws InvalidSessionException when ID is not in the session tracked
     */
    boolean isValid(String session_id) throws InvalidSessionException {
        if (this.exists(session_id)) {
            log.log_Debug( "Session [", session_id, "]=", this.tracker.get( session_id ) );
            return this.tracker.get(session_id).compareTo(new Date()) > 0;
        } else {
            throw new InvalidSessionException("Session (id: " + session_id + ") is not tracked.");
        }
    }

    /**
     * Gets the number of sessions currently tracked
     *
     * @return Tracked sessions count
     */
    int trackedCount() {
        return this.tracker.size();
    }

    /**
     * Gets the no-tracking state
     *
     * @return No sessions tracked state
     */
    boolean isEmpty() {
        return this.tracker.isEmpty();
    }
}
