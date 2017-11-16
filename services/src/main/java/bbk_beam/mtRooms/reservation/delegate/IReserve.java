package bbk_beam.mtRooms.reservation.delegate;

import bbk_beam.mtRooms.admin.authentication.Token;
import bbk_beam.mtRooms.db.exception.DbQueryException;
import bbk_beam.mtRooms.db.exception.SessionExpiredException;
import bbk_beam.mtRooms.db.exception.SessionInvalidException;
import bbk_beam.mtRooms.reservation.exception.InvalidReservation;
import bbk_beam.mtRooms.reservation.processing.Reservation;

public interface IReserve {
    /**
     * Creates a reservation in the records
     *
     * @param session_token Session's token
     * @param reservation   Reservation DTO
     * @throws DbQueryException        when a problem was encountered whilst processing the query
     * @throws SessionExpiredException When the session for the id provided has expired
     * @throws SessionInvalidException When the session for the id provided does not exist in the tracker
     */
    void createReservation(Token session_token, Reservation reservation) throws DbQueryException, SessionExpiredException, SessionInvalidException;

    /**
     * Deletes the record of a reservation
     *
     * @param session_token Session's token
     * @param reservation   Reservation DTO
     * @return Balance to reimburse on reservation
     * @throws InvalidReservation      when Reservation cannot be validated with the records
     * @throws DbQueryException        when a problem was encountered whilst processing the query
     * @throws SessionExpiredException When the session for the id provided has expired
     * @throws SessionInvalidException When the session for the id provided does not exist in the tracker
     */
    Integer cancelReservation(Token session_token, Reservation reservation) throws InvalidReservation, DbQueryException, SessionExpiredException, SessionInvalidException;
}
