package bbk_beam.mtRooms.db.session;

import bbk_beam.mtRooms.db.exception.InvalidSessionException;
import bbk_beam.mtRooms.db.exception.SessionException;

import java.util.Date;

/**
 * Interface for accessing tracked current user account sessions
 */
public interface ICurentSessions extends ICurrentReservationSessions {
    /**
     * Adds user session to the tracker
     *
     * @param id     Session ID
     * @param expiry Expiry timestamp
     * @throws SessionException when ID is already tracked
     */
    public void addUserAccountSession(String id, Date expiry) throws SessionException;

    /**
     * Removes a user session from the tracker
     *
     * @param id Session ID
     * @throws SessionException when trying to remove non-tracked session ID
     */
    public void removeUserAccountSession(String id) throws SessionException, InvalidSessionException;

    /**
     * Gets the empty state of the user sessions tracker
     *
     * @return Empty state
     */
    public boolean noUserSessions();

    /**
     * Gets the empty state of all the trackers (user and reservation)
     *
     * @return Global empty tracker state
     */
    public boolean noSessions();

    /**
     * Gets the number of sessions currently in the user tracker
     *
     * @return Number of reservation sessions
     */
    public int userAccountSessionsCount();

    /**
     * Gets the existence state of the user session in the tracker
     *
     * @param session_id Session ID
     * @return Existence in the tracker
     */
    public boolean userSessionExists(String session_id);

    /**
     * Gets the existence state of the session in any if the trackers
     *
     * @param session_id Session ID
     * @return Existence in the trackers
     */
    public boolean sessionExists(String session_id);

    /**
     * Gets the valid (not expired) state of a user session
     *
     * @param session_id Session ID
     * @return Valid state of session
     * @throws InvalidSessionException when sesion ID given is not tracked
     */
    public boolean userSessionValid(String session_id) throws InvalidSessionException;
}
