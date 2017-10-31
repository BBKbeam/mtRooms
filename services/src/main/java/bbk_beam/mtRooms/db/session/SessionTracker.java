package bbk_beam.mtRooms.db.session;

import bbk_beam.mtRooms.db.ReservationDbAccess;
import bbk_beam.mtRooms.db.exception.SessionException;
import bbk_beam.mtRooms.db.exception.SessionInvalidException;
import eadjlib.logger.Logger;

import java.util.Date;
import java.util.HashMap;

public class SessionTracker implements ICurrentSessions {
    private class SessionDetail {
        private Date date;
        private SessionType type;

        /**
         * Constructor
         *
         * @param date Expiry timestamp of session
         * @param type Session type
         */
        SessionDetail(Date date, SessionType type) {
            this.date = date;
            this.type = type;
        }

        /**
         * Gets the expiry date
         *
         * @return Expiry date of session
         */
        Date getDate() {
            return this.date;
        }

        /**
         * Gets the session type
         *
         * @return Session type
         */
        SessionType getSessionType() {
            return this.type;
        }

        @Override
        public String toString() {
            return "[" + this.type.name() + ", " + this.date + "]";
        }
    }

    private final Logger log = Logger.getLoggerInstance(ReservationDbAccess.class.getName());
    private HashMap<String, SessionDetail> tracker;

    /**
     * Constructor
     */
    public SessionTracker() {
        this.tracker = new HashMap<>();
    }

    @Override
    public void addSession(String session_id, Date expiry, SessionType session_type) throws SessionException {
        if (this.tracker.containsKey(session_id)) {
            throw new SessionException("Trying to track an already tracked session ID");
        } else {
            this.tracker.put(session_id, new SessionDetail(expiry, session_type));
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
    public Date getSessionExpiry(String session_id) {
        try {
            return this.tracker.get(session_id).getDate();
        } catch (NullPointerException e) {
            return null;
        }
    }

    @Override
    public boolean exists(String session_id) {
        return this.tracker.containsKey(session_id);
    }

    @Override
    public boolean isValid(String session_id) throws SessionInvalidException {
        if (this.exists(session_id)) {
            log.log_Debug("Session [", session_id, "]=", this.tracker.get(session_id));
            return this.tracker.get(session_id).getDate().compareTo(new Date()) > 0;
        } else {
            throw new SessionInvalidException("Session (id: " + session_id + ") is not tracked.");
        }
    }

    @Override
    public SessionType getSessionType(String session_id) throws SessionInvalidException {
        if (this.exists(session_id)) {
            log.log_Debug("Session [", session_id, "]=", this.tracker.get(session_id));
            return this.tracker.get(session_id).getSessionType();
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
