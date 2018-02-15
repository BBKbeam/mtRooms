package bbk_beam.mtRooms.reservation.delegate;

import bbk_beam.mtRooms.admin.authentication.Token;
import bbk_beam.mtRooms.db.exception.DbQueryException;
import bbk_beam.mtRooms.db.exception.SessionExpiredException;
import bbk_beam.mtRooms.db.exception.SessionInvalidException;
import bbk_beam.mtRooms.reservation.dto.Payment;
import bbk_beam.mtRooms.reservation.dto.Reservation;
import eadjlib.datastructure.ObjectTable;

public interface IPay {
    /**
     * Stores a reservation's payment
     *
     * @param session_token Session's token
     * @param reservation   Reservation subject to payment
     * @param payment        Amount payed
     * @throws DbQueryException        when a problem was encountered whilst processing the query
     * @throws SessionExpiredException When the session for the id provided has expired
     * @throws SessionInvalidException When the session for the id provided does not exist in the tracker
     */
    void pay(Token session_token, Reservation reservation, Payment payment) throws DbQueryException, SessionExpiredException, SessionInvalidException;

    /**
     * Gets all payment associated with a given reservation
     *
     * @param session_token Session's token
     * @param reservation   Reservation
     * @return List of all payments
     * @throws DbQueryException        when a problem was encountered whilst processing the query
     * @throws SessionExpiredException When the session for the id provided has expired
     * @throws SessionInvalidException When the session for the id provided does not exist in the tracker
     */
    ObjectTable getPayments(Token session_token, Reservation reservation) throws DbQueryException, SessionExpiredException, SessionInvalidException;

    /**
     * Gets the available payment types from records
     *
     * @param session_token Session's token
     * @return List of available payment types
     * @throws DbQueryException        when a problem was encountered whilst processing the query
     * @throws SessionExpiredException When the session for the id provided has expired
     * @throws SessionInvalidException When the session for the id provided does not exist in the tracker
     */
    ObjectTable getPaymentMethods(Token session_token) throws DbQueryException, SessionExpiredException, SessionInvalidException;
}
