package bbk_beam.mtRooms.reservation.delegate;

import bbk_beam.mtRooms.admin.authentication.Token;
import bbk_beam.mtRooms.db.exception.DbQueryException;
import bbk_beam.mtRooms.db.exception.SessionExpiredException;
import bbk_beam.mtRooms.db.exception.SessionInvalidException;
import bbk_beam.mtRooms.reservation.dto.Customer;
import bbk_beam.mtRooms.reservation.dto.Reservation;
import bbk_beam.mtRooms.reservation.dto.RoomReservation;
import bbk_beam.mtRooms.reservation.exception.InvalidDiscount;
import bbk_beam.mtRooms.reservation.exception.InvalidReservation;
import bbk_beam.mtRooms.reservation.exception.InvalidRoomCategory;
import eadjlib.datastructure.ObjectTable;

public interface IReserve {
    /**
     * Creates a reservation in the records
     *
     * @param session_token Session's token
     * @param reservation   Reservation DTO
     * @return ID of the created reservation
     * @throws DbQueryException        when a problem was encountered whilst processing the query
     * @throws SessionExpiredException When the session for the id provided has expired
     * @throws SessionInvalidException When the session for the id provided does not exist in the tracker
     */
    Integer createReservation(Token session_token, Reservation reservation) throws DbQueryException, SessionExpiredException, SessionInvalidException;

    /**
     * Creates a room reservation in the records
     *
     * @param session_token    Session's token
     * @param reservation_id   Reservation ID
     * @param room_reservation RoomReservation DTO
     * @throws DbQueryException        when a problem was encountered whilst processing the query
     * @throws SessionExpiredException When the session for the id provided has expired
     * @throws SessionInvalidException When the session for the id provided does not exist in the tracker
     */
    void createRoomReservation(Token session_token, Integer reservation_id, RoomReservation room_reservation) throws DbQueryException, SessionExpiredException, SessionInvalidException;

    /**
     * Cancels an entire reservation in the records
     *
     * @param session_token Session's token
     * @param reservation   Reservation DTO
     * @return Balance to reimburse on reservation
     * @throws InvalidReservation      when Reservation cannot be validated with the records
     * @throws DbQueryException        when a problem was encountered whilst processing the query
     * @throws SessionExpiredException when the session for the id provided has expired
     * @throws SessionInvalidException when the session for the id provided does not exist in the tracker
     */
    Double cancelReservation(Token session_token, Reservation reservation) throws InvalidReservation, DbQueryException, SessionExpiredException, SessionInvalidException;

    /**
     * Cancels a reserved room inside a reservation from the records
     *
     * @param session_token  Session's token
     * @param reservation_id Reservation ID
     * @param reserved_room  RoomReservation DTO
     * @return Price of the reserved room
     * @throws InvalidReservation      when Reservation cannot be validated with the records
     * @throws DbQueryException        when a problem was encountered whilst processing the query
     * @throws SessionExpiredException when the session for the id provided has expired
     * @throws SessionInvalidException when the session for the id provided does not exist in the tracker
     */
    Double cancelReservedRoom(Token session_token, Integer reservation_id, RoomReservation reserved_room) throws InvalidReservation, DbQueryException, SessionExpiredException, SessionInvalidException;

    /**
     * Deletes the record of a reservation and its reserved rooms
     * WARNING: This will orphan payments made for the reservation! Use at own discretion for cleaning.
     *
     * @param session_token  Session's token
     * @param reservation_id Reservation ID
     * @throws InvalidReservation      when Reservation cannot be validated with the records
     * @throws DbQueryException        when a problem was encountered whilst processing the query
     * @throws SessionExpiredException when the session for the id provided has expired
     * @throws SessionInvalidException when the session for the id provided does not exist in the tracker
     */
    void deleteReservation(Token session_token, Integer reservation_id) throws DbQueryException, InvalidReservation, SessionExpiredException, SessionInvalidException;

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
    ObjectTable getReservation(Token session_token, Integer reservation_id) throws InvalidReservation, DbQueryException, SessionExpiredException, SessionInvalidException;

    /**
     * Gets the reservations associated with a customer
     *
     * @param session_token Session's token
     * @param customer      Customer
     * @return List of customer's reservation
     * @throws DbQueryException        when a problem was encountered whilst processing the query
     * @throws SessionExpiredException when the session for the id provided has expired
     * @throws SessionInvalidException when the session for the id provided does not exist in the tracker
     */
    ObjectTable getReservations(Token session_token, Customer customer) throws DbQueryException, SessionExpiredException, SessionInvalidException;

    /**
     * Gets the rooms listed for a reservation
     *
     * @param session_token Session's token
     * @param reservation   Reservation
     * @return List or rooms for the reservation
     * @throws DbQueryException        when a problem was encountered whilst processing the query
     * @throws SessionExpiredException when the session for the id provided has expired
     * @throws SessionInvalidException when the session for the id provided does not exist in the tracker
     */
    ObjectTable getReservedRooms(Token session_token, Reservation reservation) throws DbQueryException, SessionExpiredException, SessionInvalidException;


    /**
     * Gets a discount's details
     *
     * @param session_token Session's token
     * @param discount_id   Discount's ID
     * @return Details of the discount from the records
     * @throws InvalidDiscount         when the discount ID does not match any within the records
     * @throws DbQueryException        when a problem was encountered whilst processing the query
     * @throws SessionExpiredException when the session for the id provided has expired
     * @throws SessionInvalidException when the session for the id provided does not exist in the tracker
     */
    ObjectTable getDiscount(Token session_token, Integer discount_id) throws InvalidDiscount, DbQueryException, SessionExpiredException, SessionInvalidException;

    /**
     * Gets a Room Category's details
     *
     * @param session_token Session's token
     * @param category_id   RoomCategory's ID
     * @return Details of the room's category from the records
     * @throws InvalidRoomCategory     when the room category ID does not match any within the records
     * @throws DbQueryException        when a problem was encountered whilst processing the query
     * @throws SessionExpiredException when the session for the id provided has expired
     * @throws SessionInvalidException when the session for the id provided does not exist in the tracker
     */
    ObjectTable getRoomCategory(Token session_token, Integer category_id) throws InvalidRoomCategory, DbQueryException, SessionExpiredException, SessionInvalidException;
}
