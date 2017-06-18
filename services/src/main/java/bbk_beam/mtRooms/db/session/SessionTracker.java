package bbk_beam.mtRooms.db.session;

import bbk_beam.mtRooms.db.ReservationDbAccess;
import bbk_beam.mtRooms.db.exception.SessionInvalidException;
import bbk_beam.mtRooms.db.exception.SessionException;
import eadjlib.logger.Logger;

import java.util.Date;
import java.util.HashMap;

public class SessionTracker implements ICurentSessions {
    private final Logger log = Logger.getLoggerInstance(ReservationDbAccess.class.getName());
    private HashMap<String, Date> tracker;

    /**
     * Constructor
     */
    public SessionTracker() {
        this.tracker = new HashMap<>();
    }

    @Override
    public void addSession(String session_id, Date expiry) throws SessionException {
        if (this.tracker.containsValue(session_id)) {
            throw new SessionException("Trying to track an already tracked session ID");
        } else {
            this.tracker.put(session_id, expiry);
        }
    }

    @Override
    public void removeSession(String session_id) throws SessionInvalidException {
        if (this.tracker.remove(session_id) == null) {
            throw new SessionInvalidException("Trying to remove non tracked session ID.");
        }
    }

    @Override
    public boolean isTracked(String session_id) {
        return this.tracker.containsKey(session_id);
    }

    @Override
    public Date getSessionType(String session_id) {
        return this.tracker.get(session_id);
    }

    @Override
    public boolean exists(String session_id) {
        return this.tracker.containsKey(session_id);
    }

    @Override
    public boolean isValid(String session_id) throws SessionInvalidException {
        if (this.exists(session_id)) {
            log.log_Debug("Session [", session_id, "]=", this.tracker.get(session_id));
            return this.tracker.get(session_id).compareTo(new Date()) > 0;
        } else {
            throw new SessionInvalidException("Session (id: " + session_id + ") is not tracked.");
        }
    }

    @Override
    public int trackedCount() {
        return this.tracker.size();
    }

    @Override
    public boolean isEmpty() {
        return this.tracker.isEmpty();
    }
}
