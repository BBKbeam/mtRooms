package bbk_beam.mtRooms.reservation;

import bbk_beam.mtRooms.admin.authentication.Token;
import bbk_beam.mtRooms.db.exception.DbQueryException;
import bbk_beam.mtRooms.db.exception.SessionExpiredException;
import bbk_beam.mtRooms.db.exception.SessionInvalidException;
import bbk_beam.mtRooms.reservation.dto.*;
import bbk_beam.mtRooms.reservation.exception.*;
import bbk_beam.mtRooms.reservation.scheduling.datastructure.TimeSpan;
import javafx.util.Pair;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Observer;

public interface IReservationSession extends Observer {
    /**
     * Adds an event watcher to the session to catch updates
     *
     * @param watcher EventWatcher instance
     * @return Previously used event watcher or null if none was set
     */
    IEventWatcher addEventWatcher(IEventWatcher watcher);

    /**
     * Gets the User's reservation session token
     *
     * @return Session token
     */
    Token getToken();

    //---------------------------------------------[ CustomerAccountAccess ]--------------------------------------------

    /**
     * Gets the Customer account details
     *
     * @param session_token Session token
     * @param customerID    Customer ID
     * @return Customer details
     * @throws DbQueryException        when a problem was encountered whilst processing the query
     * @throws SessionExpiredException When the session for the id provided has expired
     * @throws SessionInvalidException When the session for the id provided does not exist in the tracker
     */
    Customer getCustomerAccount(Token session_token, Integer customerID) throws InvalidCustomer, DbQueryException, SessionExpiredException, SessionInvalidException;

    /**
     * Reloads the customer info from the DB
     *
     * @param session_token Session token
     * @param customer      Customer container
     * @return Reloaded Customer container
     * @throws InvalidCustomer         when customer is not in records
     * @throws FailedDbFetch           when an error occurred getting the record
     * @throws SessionExpiredException When the session for the id provided has expired
     * @throws SessionInvalidException When the session for the id provided does not exist in the tracker
     */
    Customer getCustomerAccount(Token session_token, Customer customer) throws InvalidCustomer, FailedDbFetch, SessionExpiredException, SessionInvalidException;

    /**
     * Finds the records for customer from their surname
     *
     * @param session_token Session token
     * @param surname       Surname of customer
     * @return List of customer IDs and name of customers with the specified surname
     * @throws FailedDbFetch           when an error occurred getting the record
     * @throws SessionExpiredException When the session for the id provided has expired
     * @throws SessionInvalidException When the session for the id provided does not exist in the tracker
     */
    List<Pair<Integer, String>> findCustomer(Token session_token, String surname) throws FailedDbFetch, SessionExpiredException, SessionInvalidException;

    /**
     * Creates a new customer
     *
     * @param session_token Session token
     * @param customer      Customer DTO container
     * @return Customer container
     * @throws FailedDbWrite           when new record could not be created
     * @throws FailedDbFetch           when new record could not be fetched back
     * @throws SessionExpiredException When the session for the id provided has expired
     * @throws SessionInvalidException When the session for the id provided does not exist in the tracker
     */
    Customer createNewCustomer(Token session_token, Customer customer) throws FailedDbWrite, FailedDbFetch, SessionExpiredException, SessionInvalidException;

    /**
     * Saves changes of a Customer container to the database
     *
     * @param session_token Session token
     * @param customer      Customer container
     * @throws FailedDbWrite           when record update failed
     * @throws SessionExpiredException When the session for the id provided has expired
     * @throws SessionInvalidException When the session for the id provided does not exist in the tracker
     */
    void saveCustomerChangesToDB(Token session_token, Customer customer) throws FailedDbWrite, SessionExpiredException, SessionInvalidException;

    /**
     * Gets a membership type from records
     *
     * @param session_token Session token
     * @param membership_id Membership ID
     * @return Membership DTO
     * @throws InvalidMembership       when Membership ID does not match any in records
     * @throws FailedDbFetch           when new record could not be fetched back
     * @throws SessionExpiredException When the session for the id provided has expired
     * @throws SessionInvalidException When the session for the id provided does not exist in the tracker
     */
    Membership getMembership(Token session_token, Integer membership_id) throws InvalidMembership, FailedDbFetch, SessionExpiredException, SessionInvalidException;

    /**
     * Gets all membership types from records
     *
     * @param session_token Session token
     * @return List of Membership DTOs
     * @throws FailedDbFetch           when new record could not be fetched back
     * @throws SessionExpiredException When the session for the id provided has expired
     * @throws SessionInvalidException When the session for the id provided does not exist in the tracker
     */
    List<Membership> getMemberships(Token session_token) throws FailedDbFetch, SessionExpiredException, SessionInvalidException;

    //------------------------------------------------[ OptimisedSearch ]-----------------------------------------------

    /**
     * Searches for any Rooms that match the properties given
     *
     * @param session_token Session token
     * @param properties    Room properties to look for
     * @return List of rooms
     * @throws FailedDbFetch           when an error occurred getting the record
     * @throws SessionExpiredException When the session for the id provided has expired
     * @throws SessionInvalidException When the session for the id provided does not exist in the tracker
     */
    List<Room> search(Token session_token, RoomProperty properties) throws FailedDbFetch, SessionExpiredException, SessionInvalidException;

    /**
     * Searches for Rooms that match the properties given
     *
     * @param session_token Session token
     * @param building_id   ID of the building to search in
     * @param properties    Room properties to look for
     * @return List of rooms
     * @throws FailedDbFetch           when an error occurred getting the record
     * @throws SessionExpiredException When the session for the id provided has expired
     * @throws SessionInvalidException When the session for the id provided does not exist in the tracker
     */
    List<Room> search(Token session_token, Integer building_id, RoomProperty properties) throws FailedDbFetch, SessionExpiredException, SessionInvalidException;

    /**
     * Searches for Rooms that match the properties given
     *
     * @param session_token Session token
     * @param building_id   ID of the building to search in
     * @param floor_id      ID of the floor to search in
     * @param properties    Room properties to look for
     * @return List of rooms
     * @throws FailedDbFetch           when an error occurred getting the record
     * @throws SessionExpiredException When the session for the id provided has expired
     * @throws SessionInvalidException When the session for the id provided does not exist in the tracker
     */
    List<Room> search(Token session_token, Integer building_id, Integer floor_id, RoomProperty properties) throws FailedDbFetch, SessionExpiredException, SessionInvalidException;

    /**
     * Searches for available times for a Room
     *
     * @param session_token Session token
     * @param room          Room DTO
     * @param from          Beginning timestamp to search from
     * @param to            End timestamp to search up to
     * @return Availability as a list of time spans where the room is free
     * @throws FailedDbFetch           when an error occurred getting the record
     * @throws SessionExpiredException When the session for the id provided has expired
     * @throws SessionInvalidException When the session for the id provided does not exist in the tracker
     */
    List<TimeSpan> search(Token session_token, Room room, Date from, Date to) throws FailedDbFetch, SessionExpiredException, SessionInvalidException;

    /**
     * Searches for available rooms in a building within a time frame
     *
     * @param session_token Session token
     * @param building_id   ID of building where the floor is
     * @param floor_id      ID of the floor to search in
     * @param from          Beginning timestamp to search from
     * @param to            End timestamp to search up to
     * @param property      Minimum requirements for the room
     * @return Availability as a Map of the free rooms with their associated list of time spans where the room is free
     * @throws FailedDbFetch           when an error occurred getting the record
     * @throws SessionExpiredException When the session for the id provided has expired
     * @throws SessionInvalidException When the session for the id provided does not exist in the tracker
     */
    HashMap<Room, List<TimeSpan>> search(Token session_token, Integer building_id, Integer floor_id, Date from, Date to, RoomProperty property) throws FailedDbFetch, SessionExpiredException, SessionInvalidException;

    /**
     * Searches for available rooms on a floor within a time frame
     *
     * @param session_token Session token
     * @param building_id   ID of building to search in
     * @param from          Beginning timestamp to search from
     * @param to            End timestamp to search up to
     * @param property      Minimum requirements for the room
     * @return Availability as a Map of the free rooms with their associated list of time spans where the room is free
     * @throws FailedDbFetch           when an error occurred getting the record
     * @throws SessionExpiredException When the session for the id provided has expired
     * @throws SessionInvalidException When the session for the id provided does not exist in the tracker
     */
    HashMap<Room, List<TimeSpan>> search(Token session_token, Integer building_id, Date from, Date to, RoomProperty property) throws FailedDbFetch, SessionExpiredException, SessionInvalidException;

    /**
     * Searches for available rooms anywhere
     *
     * @param session_token Session token
     * @param from          Beginning timestamp to search from
     * @param to            End timestamp to search up to
     * @param property      Minimum requirements for the room
     * @return Availability as a Map of the free rooms with their associated list of time spans where the room is free
     * @throws FailedDbFetch           when an error occurred getting the record
     * @throws SessionExpiredException When the session for the id provided has expired
     * @throws SessionInvalidException When the session for the id provided does not exist in the tracker
     */
    HashMap<Room, List<TimeSpan>> search(Token session_token, Date from, Date to, RoomProperty property) throws FailedDbFetch, SessionExpiredException, SessionInvalidException;

    //-----------------------------------------------[ PaymentProcessing ]----------------------------------------------

    /**
     * Stores a reservation's payment
     *
     * @param session_token Session's token
     * @param reservation   Reservation subject to payment
     * @param payment       Amount payed
     * @return New balance post-discount on the reservation
     * @throws FailedDbWrite           when a problem was encountered whilst processing the query
     * @throws FailedDbFetch           when a problem was encountered whilst processing the query
     * @throws SessionExpiredException when the session for the id provided has expired
     * @throws SessionInvalidException when the session for the id provided does not exist in the tracker
     */
    Double pay(Token session_token, Reservation reservation, Payment payment) throws FailedDbWrite, FailedDbFetch, SessionExpiredException, SessionInvalidException;

    /**
     * Gets payments on a reservation from the records
     *
     * @param session_token Session's token
     * @param reservation   Reservation ID
     * @return List of payments for reservation
     * @throws FailedDbFetch           when a problem was encountered whilst processing the query
     * @throws SessionExpiredException when the session for the id provided has expired
     * @throws SessionInvalidException when the session for the id provided does not exist in the tracker
     */
    List<Payment> getPayments(Token session_token, Reservation reservation) throws FailedDbFetch, SessionExpiredException, SessionInvalidException;

    /**
     * Get all available payment methods
     *
     * @param session_token Session's token
     * @return List of all available payment methods from records
     * @throws FailedDbFetch           when a problem was encountered whilst processing the query
     * @throws SessionExpiredException when the session for the id provided has expired
     * @throws SessionInvalidException when the session for the id provided does not exist in the tracker
     */
    List<PaymentMethod> getPaymentMethods(Token session_token) throws FailedDbFetch, SessionExpiredException, SessionInvalidException;

    //---------------------------------------------[ ReservationProcessing ]--------------------------------------------

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
    Reservation createReservation(Token session_token, Reservation reservation) throws FailedDbWrite, FailedDbFetch, SessionExpiredException, SessionInvalidException;

    /**
     * Creates a RoomReservation in the records
     *
     * @param session_token Session's token
     * @param reservation   Reservation DTO
     * @param reserved_room RoomReservation DTO
     * @throws InvalidReservation      when Reservation cannot be validated with the records
     * @throws FailedDbWrite           when a problem was encountered whilst processing the query
     * @throws FailedDbFetch           when a problem was encountered whilst processing the query
     * @throws SessionExpiredException When the session for the id provided has expired
     * @throws SessionInvalidException When the session for the id provided does not exist in the tracker
     */
    void createRoomReservation(Token session_token, Reservation reservation, RoomReservation reserved_room) throws InvalidReservation, FailedDbWrite, FailedDbFetch, SessionExpiredException, SessionInvalidException;

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
    Double cancelReservation(Token session_token, Reservation reservation) throws InvalidReservation, FailedDbWrite, SessionExpiredException, SessionInvalidException;

    /**
     * Mark the record of a room inside a reservation as cancelled
     *
     * @param session_token    Session's token
     * @param reservation      Reservation DTO
     * @param room_reservation RoomReservation DTO
     * @return Pre-discount balance to reimburse on reservation
     * @throws InvalidReservation      when Reservation cannot be validated with the records
     * @throws FailedDbWrite           when a problem was encountered whilst processing the query
     * @throws SessionExpiredException when the session for the id provided has expired
     * @throws SessionInvalidException when the session for the id provided does not exist in the tracker
     */
    Double cancelReservedRoom(Token session_token, Reservation reservation, RoomReservation room_reservation) throws InvalidReservation, FailedDbWrite, SessionExpiredException, SessionInvalidException;

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
    Reservation getReservation(Token session_token, Integer reservation_id) throws InvalidReservation, FailedDbFetch, SessionExpiredException, SessionInvalidException;

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
    List<Reservation> getReservations(Token session_token, Customer customer) throws FailedDbFetch, SessionExpiredException, SessionInvalidException;

    /**
     * Gets the RoomCategory DTO from an ID
     *
     * @param session_token Session's token
     * @param category_id   RoomCategory ID
     * @return RoomCategory DTO
     * @throws InvalidRoomCategory     when the ID does not match any RoomCategory in the records
     * @throws FailedDbFetch           when a problem was encountered whilst processing the query
     * @throws SessionExpiredException when the session for the id provided has expired
     * @throws SessionInvalidException when the session for the id provided does not exist in the tracker
     */
    RoomCategory getRoomCategory(Token session_token, Integer category_id) throws InvalidRoomCategory, FailedDbFetch, SessionExpiredException, SessionInvalidException;

    /**
     * Gets details for a Room
     *
     * @param session_token Session's token
     * @param room          Room DTO
     * @return DetailedRoom DTO
     * @throws InvalidRoom             when the room does not match any in the records
     * @throws FailedDbFetch           when a problem was encountered whilst processing the query
     * @throws SessionExpiredException when the session for the id provided has expired
     * @throws SessionInvalidException when the session for the id provided does not exist in the tracker
     */
    public DetailedRoom getRoomDetails(Token session_token, Room room) throws InvalidRoom, FailedDbFetch, SessionExpiredException, SessionInvalidException;
}
