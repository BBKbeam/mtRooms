package bbk_beam.mtRooms.reservation.processing;

import bbk_beam.mtRooms.admin.authentication.Token;
import bbk_beam.mtRooms.db.TimestampConverter;
import bbk_beam.mtRooms.db.exception.DbQueryException;
import bbk_beam.mtRooms.db.exception.SessionExpiredException;
import bbk_beam.mtRooms.db.exception.SessionInvalidException;
import bbk_beam.mtRooms.reservation.delegate.IReserve;
import bbk_beam.mtRooms.reservation.dto.*;
import bbk_beam.mtRooms.reservation.exception.FailedDbFetch;
import bbk_beam.mtRooms.reservation.exception.FailedDbWrite;
import bbk_beam.mtRooms.reservation.exception.InvalidReservation;
import eadjlib.datastructure.ObjectTable;
import eadjlib.logger.Logger;

import java.util.ArrayList;
import java.util.HashMap;
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
     * @return Reservation object with the ID of the record created
     * @throws FailedDbWrite           when a problem was encountered whilst processing the query
     * @throws FailedDbFetch           when a problem was encountered whilst processing the query
     * @throws SessionExpiredException When the session for the id provided has expired
     * @throws SessionInvalidException When the session for the id provided does not exist in the tracker
     */
    public Reservation createReservation(Token session_token, Reservation reservation) throws FailedDbWrite, FailedDbFetch, SessionExpiredException, SessionInvalidException {
        try {
            Integer reservation_id = this.db_delegate.createReservation(session_token, reservation);
            log.log_Debug("Created reservation in records. Given Reservation.id: ", reservation_id);
            for (RoomReservation reserved_room : reservation.rooms()) {
                this.db_delegate.createRoomReservation(session_token, reservation_id, reserved_room);
            }
            return getReservation(session_token, reservation_id);
        } catch (DbQueryException e) {
            log.log_Error("Failed to save reservation in records: ", reservation);
            throw new FailedDbWrite("Failed to save reservation in records: " + reservation, e);
        } catch (InvalidReservation e) {
            log.log_Error("Could not fetch back created Reservation from records: ", reservation);
            throw new FailedDbFetch("Could not fetch back created Reservation from records: " + reservation, e);
        }
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
    public Integer cancelReservation(Token session_token, Reservation reservation) throws InvalidReservation, FailedDbWrite, SessionExpiredException, SessionInvalidException {
        //TODO iterate through all reserved rooms and set cancelled flag to false
        //TODO get and sum prices for all reserved rooms then return total
        return null;
    }

    /**
     * Mark the record of a room inside a reservation as cancelled
     *
     * @param session_token    Session's token
     * @param reservation      Reservation DTO
     * @param room_reservation RoomReservation DTO
     * @return Balance to reimburse on reservation
     * @throws InvalidReservation      when Reservation cannot be validated with the records
     * @throws FailedDbWrite           when a problem was encountered whilst processing the query
     * @throws SessionExpiredException when the session for the id provided has expired
     * @throws SessionInvalidException when the session for the id provided does not exist in the tracker
     */
    public Integer cancelReservedRoom(Token session_token, Reservation reservation, RoomReservation room_reservation) throws InvalidReservation, FailedDbWrite, SessionExpiredException, SessionInvalidException {
        //TODO set cancelled flag to false on reserved room to cancel
        //TODO return price for reserved room
        return null;
    }

    /**
     * Gets a reservation's details
     *
     * @param session_token  Session's token
     * @param reservation_id Reservation ID
     * @return Reservation DTO
     * @throws InvalidReservation      when the reservation ID does not match any within the records
     * @throws FailedDbFetch           when a problem was encountered whilst processing the query
     * @throws SessionExpiredException when the session for the id provided has expired
     * @throws SessionInvalidException when the session for the id provided does not exist in the tracker
     */
    public Reservation getReservation(Token session_token, Integer reservation_id) throws InvalidReservation, FailedDbFetch, SessionExpiredException, SessionInvalidException {
        try {
            ObjectTable reservation_table = this.db_delegate.getReservation(session_token, reservation_id);
            HashMap<String, Object> reservation_row = reservation_table.getRow(1);

            Discount discount = new Discount(
                    (Integer) reservation_row.get("discount_id"),
                    (Double) reservation_row.get("discount_rate"),
                    (Integer) reservation_row.get("discount_category_id"),
                    (String) reservation_row.get("discount_category_description")
            );
            Reservation reservation = new Reservation(
                    (Integer) reservation_row.get("id"),
                    TimestampConverter.getDateObject((String) reservation_row.get("created_timestamp")),
                    (Integer) reservation_row.get("customer_id"),
                    discount
            );

            ObjectTable rooms_table = this.db_delegate.getReservedRooms(session_token, reservation);
            for (int i = 1; i <= rooms_table.rowCount(); i++) {
                HashMap<String, Object> reserved_room_row = rooms_table.getRow(i);
                reservation.addRoomReservation(new RoomReservation(
                        new Room(
                                (Integer) reserved_room_row.get("room_id"),
                                (Integer) reserved_room_row.get("floor_id"),
                                (Integer) reserved_room_row.get("building_id"),
                                (Integer) reserved_room_row.get("room_category_id")
                        ),
                        TimestampConverter.getDateObject((String) reserved_room_row.get("timestamp_in")),
                        TimestampConverter.getDateObject((String) reserved_room_row.get("timestamp_out")),
                        (String) reserved_room_row.get("notes"),
                        new RoomPrice(
                                (Integer) reserved_room_row.get("price_id"),
                                (Integer) reserved_room_row.get("price"),
                                (Integer) reserved_room_row.get("price_year")
                        ),
                        ((Integer) reserved_room_row.get("cancelled_flag") != 0)
                ));
            }
            return reservation;
        } catch (DbQueryException e) {
            log.log_Error("Fetching of Reservation [", reservation_id, "]'s details unsuccessful: SQL Query issue.", e);
            throw new FailedDbFetch("Fetching of Reservation [" + reservation_id + "]'s details unsuccessful: SQL Query issue.", e);
        }
    }

    /**
     * Gets the reservations associated with a customer
     *
     * @param session_token Session's token
     * @param customer      Customer
     * @return List of customer's reservation
     * @throws FailedDbFetch           when a problem was encountered whilst processing the query
     * @throws SessionExpiredException when the session for the id provided has expired
     * @throws SessionInvalidException when the session for the id provided does not exist in the tracker
     */
    public List<Reservation> getReservations(Token session_token, Customer customer) throws FailedDbFetch, SessionExpiredException, SessionInvalidException {
        try {
            ObjectTable table = this.db_delegate.getReservations(session_token, customer);
            List<Reservation> reservations = new ArrayList<>();
            for (int i = 1; i <= table.rowCount(); i++) {
                Integer reservation_id = table.getInteger(1, i);
                reservations.add(getReservation(session_token, reservation_id));
            }
            return reservations;
        } catch (DbQueryException e) {
            log.log_Error("Fetching of reservations for customer \"", customer.name(), " ", customer.surname(), "\" [" + customer.customerID() + "]'s details unsuccessful: SQL Query issue.");
            throw new FailedDbFetch("Fetching of reservations for customer \"" + customer.name() + " " + customer.surname() + "\" [" + customer.customerID() + "]'s details unsuccessful: SQL Query issue.", e);
        } catch (InvalidReservation e) {
            log.log_Error("Fetching of reservations for customer \"", customer.name(), " ", customer.surname(), "\" [" + customer.customerID() + "]'s details unsuccessful: Invalid reservation ID.");
            throw new FailedDbFetch("Fetching of reservations for customer \"" + customer.name() + " " + customer.surname() + "\" [" + customer.customerID() + "]'s details unsuccessful: Invalid reservation ID.", e);
        }
    }
}
