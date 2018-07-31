package bbk_beam.mtRooms.uaa;

import bbk_beam.mtRooms.admin.authentication.Token;
import bbk_beam.mtRooms.admin.exception.AuthenticationFailureException;
import bbk_beam.mtRooms.reservation.IReservationSession;
import bbk_beam.mtRooms.uaa.exception.DuplicateSession;
import bbk_beam.mtRooms.uaa.exception.InvalidAccessRights;

public interface IAuthenticatedFrontDesk {
    /**
     * Opens a ReservationSession
     *
     * @param token Session token
     * @return ReservationSession instance
     * @throws InvalidAccessRights            when token is associated with account that does not have access rights to the resource
     * @throws AuthenticationFailureException when token is not valid/has not got access rights to this resource
     * @throws DuplicateSession               when generated token is already used in another session (should not happen!)
     */
    IReservationSession openReservationSession(Token token) throws InvalidAccessRights, AuthenticationFailureException, DuplicateSession;

    /**
     * Closes a ReservationSession
     *
     * @param reservation_session ReservationSession to close
     */
    void closeReservationSession(IReservationSession reservation_session);

    /**
     * Closes a ReservationSession
     *
     * @param token Token associated with the ReservationSession to be closed
     */
    void closeReservationSession(Token token);
}
