package bbk_beam.mtRooms.db.session;

import bbk_beam.mtRooms.db.exception.SessionException;

import java.util.Date;
import java.util.HashMap;

public class SessionTracker {
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
     * @throws SessionException when trying to remove non-tracked session ID
     */
    public void removeSession(String session_id) throws SessionException {
        if (this.tracker.remove(session_id) == null) {
            throw new SessionException("Trying to remove non tracked session ID.");
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
