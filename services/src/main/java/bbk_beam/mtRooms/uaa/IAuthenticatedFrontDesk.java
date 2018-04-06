package bbk_beam.mtRooms.uaa;

import bbk_beam.mtRooms.admin.authentication.Token;
import bbk_beam.mtRooms.admin.exception.AuthenticationFailureException;
import bbk_beam.mtRooms.db.exception.SessionInvalidException;
import bbk_beam.mtRooms.reservation.ReservationSession;
import bbk_beam.mtRooms.uaa.exception.DuplicateSession;
import bbk_beam.mtRooms.uaa.exception.InvalidAccessRights;

public interface IAuthenticatedFrontDesk {
    /**
     * Gets a ReservationSession
     *
     * @param token Session token
     * @return ReservationSession instance
     * @throws InvalidAccessRights            when token is associated with account that does not have access rights to the resource
     * @throws AuthenticationFailureException when token is not valid/has not got access rights to this resource
     * @throws DuplicateSession               when generated token is already used in another session (should not happen!)
     */
    public ReservationSession getReservationSession(Token token) throws InvalidAccessRights, AuthenticationFailureException, DuplicateSession;

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
