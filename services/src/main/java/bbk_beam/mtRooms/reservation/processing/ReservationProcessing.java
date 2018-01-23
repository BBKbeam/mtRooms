package bbk_beam.mtRooms.reservation.processing;

import bbk_beam.mtRooms.admin.authentication.Token;
import bbk_beam.mtRooms.db.exception.DbQueryException;
import bbk_beam.mtRooms.db.exception.SessionExpiredException;
import bbk_beam.mtRooms.db.exception.SessionInvalidException;
import bbk_beam.mtRooms.reservation.delegate.IReserve;
import bbk_beam.mtRooms.reservation.dto.Customer;
import bbk_beam.mtRooms.reservation.dto.Reservation;
import bbk_beam.mtRooms.reservation.exception.FailedDbWrite;
import bbk_beam.mtRooms.reservation.exception.InvalidCustomer;
import bbk_beam.mtRooms.reservation.exception.InvalidReservation;
import eadjlib.logger.Logger;

import java.util.List;

public class ReservationProcessing {
    private final Logger log = Logger.getLoggerInstance(ReservationProcessing.class.getName());
    private IReserve db_delegate;

    /**
     * Constructor
     *
     * @param reserve_delegate IReserve instance
     */
    public ReservationProcessing(IReserve reserve_delegate) {
        this.db_delegate = reserve_delegate;
    }

    /**
     * Creates a reservation in the records
     *
     * @param session_token Session's token
     * @param reservation   Reservation DTO
     * @throws FailedDbWrite           when a problem was encountered whilst processing the query
     * @throws SessionExpiredException When the session for the id provided has expired
     * @throws SessionInvalidException When the session for the id provided does not exist in the tracker
     */
    public void createReservation(Token session_token, Reservation reservation) {
        //TODO
    }

    /**
     * Mark the record of a reservation as cancelled
     *
     * @param session_token Session's token
     * @param reservation   Reservation DTO
     * @return Balance to reimburse on reservation
     * @throws InvalidReservation      when Reservation cannot be validated with the records
     * @throws FailedDbWrite           when a problem was encountered whilst processing the query
     * @throws SessionExpiredException when the session for the id provided has expired
     * @throws SessionInvalidException when the session for the id provided does not exist in the tracker
     */
    public Integer cancelReservation(Token session_token, Reservation reservation) {
        //TODO
        return null;
    }

    /**
     * Gets a reservation's details
     *
     * @param session_token  Session's token
     * @param reservation_id Reservation ID
     * @return Reservation DTO
     * @throws InvalidReservation      when the reservation ID does not match any within the records
     * @throws DbQueryException        when a problem was encountered whilst processing the query
     * @throws SessionExpiredException when the session for the id provided has expired
     * @throws SessionInvalidException when the session for the id provided does not exist in the tracker
     */
    public Reservation getReservation(Token session_token, Integer reservation_id) {

        //TODO
        return null;
    }

    /**
     * Gets the reservations associated with a customer
     *
     * @param session_token Session's token
     * @param customer      Customer
     * @return List of customer's reservation
     * @throws InvalidCustomer         when customer is not in records
     * @throws SessionExpiredException when the session for the id provided has expired
     * @throws SessionInvalidException when the session for the id provided does not exist in the tracker
     */
    public List<Reservation> getReservations(Token session_token, Customer customer) {

        //TODO
        return null;
    }

}
