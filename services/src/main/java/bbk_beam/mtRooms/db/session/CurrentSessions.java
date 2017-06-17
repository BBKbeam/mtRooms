package bbk_beam.mtRooms.db.session;

import bbk_beam.mtRooms.db.exception.InvalidSessionException;
import bbk_beam.mtRooms.db.exception.SessionException;

import java.util.Date;

public class CurrentSessions implements ICurentSessions {
    private SessionTracker reservations_tracker;
    private SessionTracker user_tracker;

    /**
     * Constructor
     */
    public CurrentSessions() {
        this.reservations_tracker = new SessionTracker();
        this.user_tracker = new SessionTracker();
    }

    @Override
    public void addUserAccountSession(String id, Date expiry) throws SessionException {
        this.user_tracker.addSession(id, expiry);
    }

    @Override
    public void removeUserAccountSession(String id) throws InvalidSessionException {
        this.user_tracker.removeSession(id);
    }

    @Override
    public boolean noUserSessions() {
        return this.user_tracker.isEmpty();
    }

    @Override
    public boolean noSessions() {
        return this.user_tracker.isEmpty() && this.reservations_tracker.isEmpty();
    }

    @Override
    public int userAccountSessionsCount() {
        return this.user_tracker.trackedCount();
    }

    @Override
    public boolean userSessionExists(String session_id) {
        return this.user_tracker.isTracked(session_id);
    }

    @Override
    public boolean sessionExists(String session_id) {
        return this.userSessionExists(session_id) || this.reservationSessionExists(session_id);
    }

    @Override
    public boolean userSessionValid(String session_id) throws InvalidSessionException {
        return this.user_tracker.isValid(session_id);
    }

    @Override
    public void addReservationSession(String id, Date expiry) throws SessionException {
        this.reservations_tracker.addSession(id, expiry);
    }

    @Override
    public void removeReservationSession(String id) throws InvalidSessionException {
        this.reservations_tracker.removeSession(id);
    }

    @Override
    public boolean noReservationSessions() {
        return this.reservations_tracker.isEmpty();
    }

    @Override
    public int reservationSessionsCount() {
        return this.reservations_tracker.trackedCount();
    }

    @Override
    public boolean reservationSessionExists(String session_id) {
        return this.reservations_tracker.isTracked(session_id);
    }

    @Override
    public boolean reservationSessionValid(String session_id) throws InvalidSessionException {
        return this.reservations_tracker.isValid(session_id);
    }
}
