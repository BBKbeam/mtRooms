package bbk_beam.mtRooms.network;

import bbk_beam.mtRooms.admin.authentication.Token;
import bbk_beam.mtRooms.common.TimeSpan;
import bbk_beam.mtRooms.db.exception.DbQueryException;
import bbk_beam.mtRooms.network.exception.Unauthorised;
import bbk_beam.mtRooms.reservation.dto.*;
import bbk_beam.mtRooms.reservation.exception.*;
import javafx.util.Pair;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public interface IRmiReservationServices extends Remote {
    //---------------------------------------------[ CustomerAccountAccess ]--------------------------------------------

    /**
     * Gets the Customer account details
     *
     * @param session_token Session token
     * @param customerID    Customer ID
     * @return Customer details
     * @throws DbQueryException when a problem was encountered whilst processing the query
     * @throws Unauthorised     when client is not authorised to access the resource
     * @throws RemoteException  when network issues occur during the remote call
     */
    Customer getCustomerAccount(Token session_token, Integer customerID) throws InvalidCustomer, DbQueryException, Unauthorised, RemoteException;

    /**
     * Reloads the customer info from the DB
     *
     * @param session_token Session token
     * @param customer      Customer container
     * @return Reloaded Customer container
     * @throws InvalidCustomer when customer is not in records
     * @throws FailedDbFetch   when an error occurred getting the record
     * @throws Unauthorised    when client is not authorised to access the resource
     * @throws RemoteException when network issues occur during the remote call
     */
    Customer getCustomerAccount(Token session_token, Customer customer) throws InvalidCustomer, FailedDbFetch, Unauthorised, RemoteException;

    /**
     * Finds the records for customer from their surname
     *
     * @param session_token Session token
     * @param surname       Surname of customer
     * @return List of customer IDs and name of customers with the specified surname
     * @throws FailedDbFetch   when an error occurred getting the record
     * @throws Unauthorised    when client is not authorised to access the resource
     * @throws RemoteException when network issues occur during the remote call
     */
    List<Pair<Integer, String>> findCustomer(Token session_token, String surname) throws FailedDbFetch, Unauthorised, RemoteException;

    /**
     * Creates a new customer
     *
     * @param session_token Session token
     * @param customer      Customer DTO container
     * @return Customer container
     * @throws FailedDbWrite   when new record could not be created
     * @throws FailedDbFetch   when new record could not be fetched back
     * @throws Unauthorised    when client is not authorised to access the resource
     * @throws RemoteException when network issues occur during the remote call
     */
    Customer createNewCustomer(Token session_token, Customer customer) throws FailedDbWrite, FailedDbFetch, Unauthorised, RemoteException;

    /**
     * Saves changes of a Customer container to the database
     *
     * @param session_token Session token
     * @param customer      Customer container
     * @throws FailedDbWrite   when record update failed
     * @throws Unauthorised    when client is not authorised to access the resource
     * @throws RemoteException when network issues occur during the remote call
     */
    void saveCustomerChangesToDB(Token session_token, Customer customer) throws FailedDbWrite, Unauthorised, RemoteException;

    /**
     * Gets a membership type from records
     *
     * @param session_token Session token
     * @param membership_id Membership ID
     * @return Membership DTO
     * @throws InvalidMembership when Membership ID does not match any in records
     * @throws FailedDbFetch     when membership record could not be fetched
     * @throws Unauthorised      when client is not authorised to access the resource
     * @throws RemoteException   when network issues occur during the remote call
     */
    Membership getMembership(Token session_token, Integer membership_id) throws InvalidMembership, FailedDbFetch, Unauthorised, RemoteException;

    /**
     * Gets all membership types from records
     *
     * @param session_token Session token
     * @return List of Membership DTOs
     * @throws FailedDbFetch   when new record could not be fetched back
     * @throws Unauthorised    when client is not authorised to access the resource
     * @throws RemoteException when network issues occur during the remote call
     */
    List<Membership> getMemberships(Token session_token) throws FailedDbFetch, Unauthorised, RemoteException;

    //------------------------------------------------[ OptimisedSearch ]-----------------------------------------------

    /**
     * Searches for any Rooms that match the properties given
     *
     * @param session_token Session token
     * @param properties    Room properties to look for
     * @return List of rooms
     * @throws FailedDbFetch   when an error occurred getting the record
     * @throws Unauthorised    when client is not authorised to access the resource
     * @throws RemoteException when network issues occur during the remote call
     */
    List<Room> search(Token session_token, RoomProperty properties) throws FailedDbFetch, Unauthorised, RemoteException;

    /**
     * Searches for Rooms that match the properties given
     *
     * @param session_token Session token
     * @param building_id   ID of the building to search in
     * @param properties    Room properties to look for
     * @return List of rooms
     * @throws FailedDbFetch   when an error occurred getting the record
     * @throws Unauthorised    when client is not authorised to access the resource
     * @throws RemoteException when network issues occur during the remote call
     */
    List<Room> search(Token session_token, Integer building_id, RoomProperty properties) throws FailedDbFetch, Unauthorised, RemoteException;

    /**
     * Searches for Rooms that match the properties given
     *
     * @param session_token Session token
     * @param building_id   ID of the building to search in
     * @param floor_id      ID of the floor to search in
     * @param properties    Room properties to look for
     * @return List of rooms
     * @throws FailedDbFetch   when an error occurred getting the record
     * @throws Unauthorised    when client is not authorised to access the resource
     * @throws RemoteException when network issues occur during the remote call
     */
    List<Room> search(Token session_token, Integer building_id, Integer floor_id, RoomProperty properties) throws FailedDbFetch, Unauthorised, RemoteException;

    /**
     * Searches for available times for a Room
     *
     * @param session_token Session token
     * @param room          Room DTO
     * @param from          Beginning timestamp to search from
     * @param to            End timestamp to search up to
     * @return Availability as a list of time spans where the room is free
     * @throws FailedDbFetch   when an error occurred getting the record
     * @throws Unauthorised    when client is not authorised to access the resource
     * @throws RemoteException when network issues occur during the remote call
     */
    List<TimeSpan> search(Token session_token, Room room, Date from, Date to) throws FailedDbFetch, Unauthorised, RemoteException;

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
     * @throws FailedDbFetch   when an error occurred getting the record
     * @throws Unauthorised    when client is not authorised to access the resource
     * @throws RemoteException when network issues occur during the remote call
     */
    HashMap<Room, List<TimeSpan>> search(Token session_token, Integer building_id, Integer floor_id, Date from, Date to, RoomProperty property) throws FailedDbFetch, Unauthorised, RemoteException;

    /**
     * Searches for available rooms on a floor within a time frame
     *
     * @param session_token Session token
     * @param building_id   ID of building to search in
     * @param from          Beginning timestamp to search from
     * @param to            End timestamp to search up to
     * @param property      Minimum requirements for the room
     * @return Availability as a Map of the free rooms with their associated list of time spans where the room is free
     * @throws FailedDbFetch   when an error occurred getting the record
     * @throws Unauthorised    when client is not authorised to access the resource
     * @throws RemoteException when network issues occur during the remote call
     */
    HashMap<Room, List<TimeSpan>> search(Token session_token, Integer building_id, Date from, Date to, RoomProperty property) throws FailedDbFetch, Unauthorised, RemoteException;

    /**
     * Searches for available rooms anywhere
     *
     * @param session_token Session token
     * @param from          Beginning timestamp to search from
     * @param to            End timestamp to search up to
     * @param property      Minimum requirements for the room
     * @return Availability as a Map of the free rooms with their associated list of time spans where the room is free
     * @throws FailedDbFetch   when an error occurred getting the record
     * @throws Unauthorised    when client is not authorised to access the resource
     * @throws RemoteException when network issues occur during the remote call
     */
    HashMap<Room, List<TimeSpan>> search(Token session_token, Date from, Date to, RoomProperty property) throws FailedDbFetch, Unauthorised, RemoteException;

    //-----------------------------------------------[ PaymentProcessing ]----------------------------------------------

    /**
     * Stores a reservation's payment
     *
     * @param session_token Session's token
     * @param reservation   Reservation subject to payment
     * @param payment       Amount payed
     * @return New balance post-discount on the reservation
     * @throws FailedDbWrite   when a problem was encountered whilst processing the query
     * @throws FailedDbFetch   when a problem was encountered whilst processing the query
     * @throws Unauthorised    when client is not authorised to access the resource
     * @throws RemoteException when network issues occur during the remote call
     */
    Double pay(Token session_token, Reservation reservation, Payment payment) throws FailedDbWrite, FailedDbFetch, Unauthorised, RemoteException;

    /**
     * Gets payments on a reservation from the records
     *
     * @param session_token Session's token
     * @param reservation   Reservation ID
     * @return List of payments for reservation
     * @throws FailedDbFetch   when a problem was encountered whilst processing the query
     * @throws Unauthorised    when client is not authorised to access the resource
     * @throws RemoteException when network issues occur during the remote call
     */
    List<Payment> getPayments(Token session_token, Reservation reservation) throws FailedDbFetch, Unauthorised, RemoteException;

    /**
     * Get all available payment methods
     *
     * @param session_token Session's token
     * @return List of all available payment methods from records
     * @throws FailedDbFetch   when a problem was encountered whilst processing the query
     * @throws Unauthorised    when client is not authorised to access the resource
     * @throws RemoteException when network issues occur during the remote call
     */
    List<PaymentMethod> getPaymentMethods(Token session_token) throws FailedDbFetch, Unauthorised, RemoteException;

    //---------------------------------------------[ ReservationProcessing ]--------------------------------------------

    /**
     * Creates a reservation in the records
     *
     * @param session_token Session's token
     * @param reservation   Reservation DTO
     * @return Reservation object with the ID of the record created
     * @throws FailedDbWrite   when a problem was encountered whilst processing the query
     * @throws FailedDbFetch   when a problem was encountered whilst processing the query
     * @throws Unauthorised    when client is not authorised to access the resource
     * @throws RemoteException when network issues occur during the remote call
     */
    Reservation createReservation(Token session_token, Reservation reservation) throws FailedDbWrite, FailedDbFetch, Unauthorised, RemoteException;

    /**
     * Creates a RoomReservation in the records
     *
     * @param session_token Session's token
     * @param reservation   Reservation DTO
     * @param reserved_room RoomReservation DTO
     * @throws InvalidReservation when Reservation cannot be validated with the records
     * @throws FailedDbWrite      when a problem was encountered whilst processing the query
     * @throws FailedDbFetch      when a problem was encountered whilst processing the query
     * @throws Unauthorised       when client is not authorised to access the resource
     * @throws RemoteException    when network issues occur during the remote call
     */
    void createRoomReservation(Token session_token, Reservation reservation, RoomReservation reserved_room) throws InvalidReservation, FailedDbWrite, FailedDbFetch, Unauthorised, RemoteException;

    /**
     * Mark the record of a reservation as cancelled
     *
     * @param session_token Session's token
     * @param reservation   Reservation DTO
     * @return Balance to reimburse on reservation
     * @throws InvalidReservation when Reservation cannot be validated with the records
     * @throws FailedDbWrite      when a problem was encountered whilst processing the query
     * @throws Unauthorised       when client is not authorised to access the resource
     * @throws RemoteException    when network issues occur during the remote call
     */
    Double cancelReservation(Token session_token, Reservation reservation) throws InvalidReservation, FailedDbWrite, Unauthorised, RemoteException;

    /**
     * Mark the record of a room inside a reservation as cancelled
     *
     * @param session_token    Session's token
     * @param reservation      Reservation DTO
     * @param room_reservation RoomReservation DTO
     * @return Pre-discount balance to reimburse on reservation
     * @throws InvalidReservation when Reservation cannot be validated with the records
     * @throws FailedDbWrite      when a problem was encountered whilst processing the query
     * @throws Unauthorised       when client is not authorised to access the resource
     * @throws RemoteException    when network issues occur during the remote call
     */
    Double cancelReservedRoom(Token session_token, Reservation reservation, RoomReservation room_reservation) throws InvalidReservation, FailedDbWrite, Unauthorised, RemoteException;

    /**
     * Gets a reservation's details
     *
     * @param session_token  Session's token
     * @param reservation_id Reservation ID
     * @return Reservation DTO
     * @throws InvalidReservation when the reservation ID does not match any within the records
     * @throws FailedDbFetch      when a problem was encountered whilst processing the query
     * @throws Unauthorised       when client is not authorised to access the resource
     * @throws RemoteException    when network issues occur during the remote call
     */
    Reservation getReservation(Token session_token, Integer reservation_id) throws InvalidReservation, FailedDbFetch, Unauthorised, RemoteException;

    /**
     * Gets the reservations associated with a customer
     *
     * @param session_token Session's token
     * @param customer      Customer
     * @return List of customer's reservation
     * @throws FailedDbFetch   when a problem was encountered whilst processing the query
     * @throws Unauthorised    when client is not authorised to access the resource
     * @throws RemoteException when network issues occur during the remote call
     */
    List<Reservation> getReservations(Token session_token, Customer customer) throws FailedDbFetch, Unauthorised, RemoteException;

    /**
     * Gets the RoomCategory DTO from an ID
     *
     * @param session_token Session's token
     * @param category_id   RoomCategory ID
     * @return RoomCategory DTO
     * @throws InvalidRoomCategory when the ID does not match any RoomCategory in the records
     * @throws FailedDbFetch       when a problem was encountered whilst processing the query
     * @throws Unauthorised        when client is not authorised to access the resource
     * @throws RemoteException     when network issues occur during the remote call
     */
    RoomCategory getRoomCategory(Token session_token, Integer category_id) throws InvalidRoomCategory, FailedDbFetch, Unauthorised, RemoteException;

    /**
     * Gets details for a Room
     *
     * @param session_token Session's token
     * @param room          Room DTO
     * @return DetailedRoom DTO
     * @throws InvalidRoom     when the room does not match any in the records
     * @throws FailedDbFetch   when a problem was encountered whilst processing the query
     * @throws Unauthorised    when client is not authorised to access the resource
     * @throws RemoteException when network issues occur during the remote call
     */
    public DetailedRoom getRoomDetails(Token session_token, Room room) throws InvalidRoom, FailedDbFetch, Unauthorised, RemoteException;

    /**
     * Gets all the prices on records for a room
     *
     * @param session_token Session's token
     * @param room          Room DTO
     * @return List of RoomPrice DTOs for the Room
     * @throws FailedDbFetch   when a problem was encountered whilst processing the query
     * @throws Unauthorised    when client is not authorised to access the resource
     * @throws RemoteException when network issues occur during the remote call
     */
    public List<RoomPrice> getRoomPrices(Token session_token, Room room) throws FailedDbFetch, Unauthorised, RemoteException;
}
