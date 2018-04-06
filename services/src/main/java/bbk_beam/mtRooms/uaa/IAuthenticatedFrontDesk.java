package bbk_beam.mtRooms.uaa;

import bbk_beam.mtRooms.admin.exception.AuthenticationFailureException;
import bbk_beam.mtRooms.db.exception.SessionInvalidException;
import bbk_beam.mtRooms.reservation.ReservationSession;
import bbk_beam.mtRooms.uaa.exception.DuplicateSession;

public interface IAuthenticatedFrontDesk {
    /**
     * Login and get a ReservationSession
     *
     * @param username Username
     * @param password Password
     * @return User's ReservationSession
     * @throws AuthenticationFailureException when username/password is not valid
     * @throws DuplicateSession               when generated token is already used in another session (should not happen!)
     */
    public ReservationSession login(String username, String password) throws AuthenticationFailureException, DuplicateSession;

    /**
     * Logout from a ReservationSession
     *
     * @param reservation_session ReservationSession to logout from
     * @throws SessionInvalidException when the token used in the ReservationSession is invalid
     */
    public void logout(ReservationSession reservation_session) throws SessionInvalidException;
}
