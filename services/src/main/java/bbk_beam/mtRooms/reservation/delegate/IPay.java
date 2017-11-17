package bbk_beam.mtRooms.reservation.delegate;

import bbk_beam.mtRooms.admin.authentication.Token;
import bbk_beam.mtRooms.db.exception.DbQueryException;
import bbk_beam.mtRooms.db.exception.SessionExpiredException;
import bbk_beam.mtRooms.db.exception.SessionInvalidException;
import bbk_beam.mtRooms.reservation.exception.InvalidPaymentType;
import bbk_beam.mtRooms.reservation.exception.InvalidReservation;
import bbk_beam.mtRooms.reservation.processing.Reservation;
import eadjlib.datastructure.ObjectTable;

public interface IPay {
    /**
     * Stores a reservation's payment
     *
     * @param session_token Session's token
     * @param reservation   Reservation subject to payment
     * @param amount        Amount payed
     * @param paymentID     Payment type ID
     * @return Balance remaining to pay on reservation
     * @throws InvalidReservation      when Reservation cannot be validated with the records
     * @throws InvalidPaymentType      when Payment type ID is not in records
     * @throws DbQueryException        when a problem was encountered whilst processing the query
     * @throws SessionExpiredException When the session for the id provided has expired
     * @throws SessionInvalidException When the session for the id provided does not exist in the tracker
     */
    Integer pay(Token session_token, Reservation reservation, Integer amount, Integer paymentID) throws InvalidReservation, InvalidPaymentType, DbQueryException, SessionExpiredException, SessionInvalidException;

    /**
     * Gets the available payment types from records
     *
     * @param session_token Session's token
     * @return ObjectTable of available payment types
     * @throws DbQueryException        when a problem was encountered whilst processing the query
     * @throws SessionExpiredException When the session for the id provided has expired
     * @throws SessionInvalidException When the session for the id provided does not exist in the tracker
     */
    ObjectTable getPayementTypes(Token session_token) throws DbQueryException, SessionExpiredException, SessionInvalidException;
}