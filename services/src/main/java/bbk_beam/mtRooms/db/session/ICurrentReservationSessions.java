package bbk_beam.mtRooms.db.session;

import bbk_beam.mtRooms.db.exception.InvalidSessionException;
import bbk_beam.mtRooms.db.exception.SessionException;

import java.util.Date;

/**
 * Interface for accessing current tracked reservation sessions
 */
public interface ICurrentReservationSessions {

    /**
     * Adds a reservation session to the tracker
     *
     * @param id     Session ID
     * @param expiry Expiry timestamp
     * @throws SessionException when ID is already tracked
     */
    public void addReservationSession(String id, Date expiry) throws SessionException;

    /**
     * Removes a reservation session from the tracker
     *
     * @param id Session ID
     * @throws InvalidSessionException when trying to remove non-tracked session ID
     */
    public void removeReservationSession(String id) throws InvalidSessionException;

    /**
     * Checks the existence state of a session
     *
     * @param id Session ID to check
     * @return Existence state in the tracker
     */
    public boolean sessionExists(String id);

    /**
     * Gets the empty state of the reservation tracker
     *
     * @return Empty state
     */
    public boolean noReservationSessions();

    /**
     * Gets the number of sessions currently in the tracker
     *
     * @return Number of reservation sessions
     */
    public int reservationSessionsCount();

    /**
     * Gets the existence state of the session in the tracker
     *
     * @param session_id Session ID
     * @return Existence in the tracker
     */
    public boolean reservationSessionExists(String session_id);

    /**
     * Gets the valid (not expired) state of a reservation session
     *
     * @param session_id Session ID
     * @return Valid state of session
     * @throws InvalidSessionException when sesion ID given is not tracked
     */
    public boolean reservationSessionValid(String session_id) throws InvalidSessionException;
}
