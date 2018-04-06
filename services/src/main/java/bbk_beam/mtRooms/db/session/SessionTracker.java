package bbk_beam.mtRooms.db.session;

import bbk_beam.mtRooms.db.exception.SessionCorruptedException;
import bbk_beam.mtRooms.db.exception.SessionException;
import bbk_beam.mtRooms.db.exception.SessionExpiredException;
import bbk_beam.mtRooms.db.exception.SessionInvalidException;
import eadjlib.logger.Logger;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class SessionTracker implements ICurrentSessions {
    private class SessionDetail {
        private Date date;
        private SessionType type;
        private Integer account_id;

        /**
         * Constructor
         *
         * @param date       Expiry timestamp of session
         * @param type       Session type
         * @param account_id Associated account ID
         */
        SessionDetail(Date date, SessionType type, Integer account_id) {
            this.date = date;
            this.type = type;
            this.account_id = account_id;
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

        /**
         * Gets the session's associated account ID
         *
         * @return Account ID
         */
        Integer getAccountID() {
            return this.account_id;
        }

        @Override
        public String toString() {
            return "[" + this.account_id + "/" + this.type.name() + ", " + this.date + "]";
        }
    }

    private final Logger log = Logger.getLoggerInstance(SessionTracker.class.getName());
    private HashMap<String, SessionDetail> tracker;

    /**
     * Constructor
     */
    public SessionTracker() {
        this.tracker = new HashMap<>();
    }

    @Override
    public void addSession(String session_id, Date expiry, SessionType session_type, Integer account_id) throws SessionException {
        if (this.tracker.containsKey(session_id)) {
            throw new SessionException("Trying to track an already tracked session ID");
        } else {
            this.tracker.put(session_id, new SessionDetail(expiry, session_type, account_id));
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
    public Date getSessionExpiry(String session_id) throws SessionInvalidException {
        if (this.exists(session_id)) {
            log.log_Debug("Getting expiry for session [", session_id, "]=", this.tracker.get(session_id));
            return this.tracker.get(session_id).getDate();
        } else {
            throw new SessionInvalidException("Session (id: " + session_id + ") is not tracked.");
        }
    }

    @Override
    public SessionType getSessionType(String session_id) throws SessionInvalidException {
        if (this.exists(session_id)) {
            log.log_Debug("Getting type for session [", session_id, "]=", this.tracker.get(session_id));
            return this.tracker.get(session_id).getSessionType();
        } else {
            throw new SessionInvalidException("Session (id: " + session_id + ") is not tracked.");
        }
    }

    @Override
    public Integer getSessionAccountID(String session_id) throws SessionInvalidException {
        if (this.exists(session_id)) {
            log.log_Debug("Getting associated account ID for session [", session_id, "]=", this.tracker.get(session_id));
            return this.tracker.get(session_id).getAccountID();
        } else {
            throw new SessionInvalidException("Session (id: " + session_id + ") is not tracked.");
        }
    }

    @Override
    public boolean exists(String session_id) {
        return this.tracker.containsKey(session_id);
    }

    @Override
    public void checkValidity(String session_id) throws SessionExpiredException, SessionInvalidException {
        log.log_Debug("Internal validation of session [", session_id, "]=", this.tracker.get(session_id));
        if (!this.exists(session_id)) {
            log.log_Error("Session [", session_id, "]=", this.tracker.get(session_id), ": session not tracked.");
            throw new SessionInvalidException("Session (id: " + session_id + ") is not tracked.");
        }
        if (this.tracker.get(session_id).getDate().compareTo(new Date()) <= 0) {
            log.log_Error("Session [", session_id, "]=", this.tracker.get(session_id), ": token has expired.");
            throw new SessionExpiredException("Token expired.");
        }
    }

    @Override
    public void checkValidity(String session_id, Date session_expiry) throws SessionExpiredException, SessionCorruptedException, SessionInvalidException {
        log.log_Debug("Full validation of session [", session_id, "]=", this.tracker.get(session_id));
        if (!this.exists(session_id)) {
            log.log_Error("Session [", session_id, "]=", this.tracker.get(session_id), ": session not tracked.");
            throw new SessionInvalidException("Session (id: " + session_id + ") is not tracked.");
        }
        if (!this.tracker.get(session_id).getDate().equals(session_expiry)) {
            log.log_Error("Session [", session_id, "]=", this.tracker.get(session_id), ": mismatch between tracked and given expiry.");
            throw new SessionCorruptedException("Mismatching session expiry timestamp found.");
        }
        if (this.tracker.get(session_id).getDate().compareTo(new Date()) <= 0) {
            log.log_Error("Session [", session_id, "]=", this.tracker.get(session_id), ": token has expired.");
            throw new SessionExpiredException("Token expired.");
        }
    }

    @Override
    public int trackedCount() {
        return this.tracker.size();
    }

    @Override
    public int validTrackedCount() {
        int count = 0;
        Date now = new Date();
        for (Map.Entry<String, SessionDetail> entry : tracker.entrySet())
            if (entry.getValue().date.compareTo(now) > 0)
                count++;
        return count;
    }

    @Override
    public boolean isEmpty() {
        return this.tracker.isEmpty();
    }
}
