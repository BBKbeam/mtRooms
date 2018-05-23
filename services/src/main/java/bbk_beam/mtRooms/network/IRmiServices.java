package bbk_beam.mtRooms.network;

import bbk_beam.mtRooms.admin.authentication.Token;
import bbk_beam.mtRooms.admin.dto.Account;
import bbk_beam.mtRooms.admin.dto.AccountType;
import bbk_beam.mtRooms.admin.exception.AccountExistenceException;
import bbk_beam.mtRooms.admin.exception.AccountOverrideException;
import bbk_beam.mtRooms.admin.exception.AuthenticationFailureException;
import bbk_beam.mtRooms.db.exception.DbQueryException;
import bbk_beam.mtRooms.db.exception.SessionInvalidException;
import bbk_beam.mtRooms.db.session.SessionType;
import bbk_beam.mtRooms.network.exception.Unauthorised;
import bbk_beam.mtRooms.operation.dto.LogisticsInfo;
import bbk_beam.mtRooms.reservation.dto.*;
import bbk_beam.mtRooms.reservation.exception.*;
import bbk_beam.mtRooms.reservation.scheduling.datastructure.TimeSpan;
import bbk_beam.mtRooms.revenue.exception.InvalidPeriodException;
import bbk_beam.mtRooms.uaa.exception.ServerSessionInactive;
import javafx.util.Pair;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * IRmiServices
 * <p>
 * Front facing RMI interface for server functionalities
 * </p>
 */
public interface IRmiServices extends Remote {
    /**
     * Create a session for a user
     *
     * @param client   IRmiClient instance
     * @param username Username
     * @param password Password
     * @return Session token
     * @throws AuthenticationFailureException when username/password are not valid
     * @throws ServerSessionInactive          when session has not been initiated
     * @throws RemoteException                when network issues occur during the remote call
     */
    Token login(IRmiClient client, String username, String password) throws AuthenticationFailureException, ServerSessionInactive, RemoteException;

    /**
     * Logout from a session
     *
     * @param session_token Session token for the session to log out from
     * @throws SessionInvalidException when session is not valid
     * @throws ServerSessionInactive   when session has not been initiated
     * @throws RemoteException         when network issues occur during the remote call
     */
    void logout(Token session_token) throws SessionInvalidException, ServerSessionInactive, RemoteException;

    /**
     * Check access rights to administration
     *
     * @return Access rights
     */
    boolean hasAdministrativeAccess(Token token) throws RemoteException;

    /**
     * Check access rights to front desk
     *
     * @return Access rights
     */
    boolean hasFrontDeskAccess(Token token) throws RemoteException;

    /**
     * Check access rights to logistics
     *
     * @return Access rights
     */
    boolean hasLogisticsAccess(Token token) throws RemoteException;

    /**
     * Check access rights to revenue
     *
     * @return Access rights
     */
    boolean hasRevenueAccess(Token token) throws RemoteException;

    //------------------------------------------------[ Administration ]------------------------------------------------

    /**
     * Creates a new account
     *
     * @param admin_token  Administrator session token
     * @param account_type Account type to create
     * @param username     Username of the account to create
     * @param password     Password of the account to create
     * @throws Unauthorised              when client is not authorised to access the resource
     * @throws AccountExistenceException when account with same name exists already
     * @throws RuntimeException          when non-standard failure occurred during account creation in records
     * @throws RemoteException           when network issues occur during the remote call
     */
    void createNewAccount(Token admin_token, SessionType account_type, String username, String password) throws Unauthorised, AccountExistenceException, RuntimeException, RemoteException;

    /**
     * Updates existing account's password
     *
     * @param admin_token Administrator or User session token
     * @param account_id  ID of account to update
     * @param password    New password
     * @throws Unauthorised              when client is not authorised to access the resource
     * @throws AccountExistenceException when account does not exist in the records
     * @throws AccountOverrideException  when new password is the same as old one
     * @throws RuntimeException          when non-standard failure occurred during account creation in records
     * @throws RemoteException           when network issues occur during the remote call
     */
    void updateAccountPassword(Token admin_token, Integer account_id, String password) throws Unauthorised, AccountExistenceException, AccountOverrideException, RuntimeException, RemoteException;

    /**
     * Activates an existing account
     *
     * @param admin_token Administrator session token
     * @param account_id  ID of account to activate
     * @throws Unauthorised              when client is not authorised to access the resource
     * @throws AccountExistenceException when account does not exist in the records
     * @throws AccountOverrideException  when account to activate is the current logged-in administrator's
     * @throws RuntimeException          when non-standard failure occurred during account activation
     * @throws RemoteException           when network issues occur during the remote call
     */
    void activateAccount(Token admin_token, Integer account_id) throws Unauthorised, AccountExistenceException, AccountOverrideException, RuntimeException, RemoteException;

    /**
     * Deactivates an existing account
     *
     * @param admin_token Administrator session token
     * @param account_id  ID of account to deactivate
     * @throws Unauthorised              when client is not authorised to access the resource
     * @throws AccountExistenceException when account does not exist in the records
     * @throws AccountOverrideException  when account to deactivates is the current logged-in administrator's
     * @throws RuntimeException          when non-standard failure occurred during account deactivation
     * @throws RemoteException           when network issues occur during the remote call
     */
    void deactivateAccount(Token admin_token, Integer account_id) throws Unauthorised, AccountExistenceException, AccountOverrideException, RuntimeException, RemoteException;

    /**
     * Deletes an existing  account record
     *
     * @param admin_token Administrator session token
     * @param account_id  ID of account to delete
     * @throws Unauthorised              when client is not authorised to access the resource
     * @throws AccountExistenceException when account to delete does not exist in the records
     * @throws AccountOverrideException  when account to delete is the current logged-in administrator's
     * @throws RuntimeException          when non-standard failure occurred during account removal from records
     * @throws RemoteException           when network issues occur during the remote call
     */
    void deleteAccount(Token admin_token, Integer account_id) throws Unauthorised, AccountExistenceException, AccountOverrideException, RuntimeException, RemoteException;

    /**
     * @param admin_token Administrator session token
     * @return List of Account DTOs
     * @throws Unauthorised     when client is not authorised to access the resource
     * @throws RuntimeException when non-standard failure occurred during account fetching from records
     * @throws RemoteException  when network issues occur during the remote call
     */
    List<Account> getAccounts(Token admin_token) throws Unauthorised, RuntimeException, RemoteException;

    /**
     * Gets the records for an account
     *
     * @param admin_token Administrator session token
     * @param account_id  Account ID
     * @return Account DTO
     * @throws Unauthorised              when client is not authorised to access the resource
     * @throws AccountExistenceException when account does not exist in records
     * @throws RuntimeException          when non-standard failure occurred during account fetching from records
     * @throws RemoteException           when network issues occur during the remote call
     */
    Account getAccount(Token admin_token, Integer account_id) throws AccountExistenceException, Unauthorised, RuntimeException, RemoteException;

    /**
     * Gets the records for an account
     *
     * @param admin_token      Administrator session token
     * @param account_username Account username
     * @return Account DTO
     * @throws Unauthorised              when client is not authorised to access the resource
     * @throws AccountExistenceException when account does not exist in records
     * @throws RuntimeException          when non-standard failure occurred during account fetching from records
     * @throws RemoteException           when network issues occur during the remote call
     */
    Account getAccount(Token admin_token, String account_username) throws AccountExistenceException, Unauthorised, RuntimeException, RemoteException;

    /**
     * Gets the user account types
     *
     * @param admin_token Administrator session token
     * @return List of AccountType DTOs
     * @throws Unauthorised     when client is not authorised to access the resource
     * @throws RuntimeException when non-standard failure occurred during account fetching from records
     * @throws RemoteException  when network issues occur during the remote call
     */
    List<AccountType> getAccountTypes(Token admin_token) throws Unauthorised, RuntimeException, RemoteException;

    /**
     * Optimises the reservation database
     *
     * @param admin_token Administrator session token
     * @return Success
     * @throws Unauthorised    when client is not authorised to access the resource
     * @throws RemoteException when network issues occur during the remote call
     */
    boolean optimiseReservationDatabase(Token admin_token) throws Unauthorised, RemoteException;

    /**
     * Optimises the user account database
     *
     * @param admin_token Administrator session token
     * @return Success
     * @throws Unauthorised    when client is not authorised to access the resource
     * @throws RemoteException when network issues occur during the remote call
     */
    boolean optimiseUserAccountDatabase(Token admin_token) throws Unauthorised, RemoteException;

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
     * @throws InvalidMembership       when Membership ID does not match any in records
     * @throws FailedDbFetch           when new record could not be fetched back
     * @throws Unauthorised    when client is not authorised to access the resource
     * @throws RemoteException when network issues occur during the remote call
     */
    Membership getMembership(Token session_token, Integer membership_id) throws InvalidMembership, FailedDbFetch, Unauthorised, RemoteException;

    /**
     * Gets all membership types from records
     *
     * @param session_token Session token
     * @return List of Membership DTOs
     * @throws FailedDbFetch           when new record could not be fetched back
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


    //----------------------------------------------------[ Logistics ]-------------------------------------------------

    /**
     * Gets logistical information
     *
     * @param session_token Session token
     * @param building_id   ID of the building
     * @param from          From date
     * @param to            To date
     * @return Logistics information report
     * @throws InvalidPeriodException when the date period is not valid
     * @throws FailedDbFetch          when an error occurred getting the record
     * @throws Unauthorised           when client is not authorised to access the resource
     * @throws RemoteException        when network issues occur during the remote call
     */
    LogisticsInfo getInfo(Token session_token, Integer building_id, Date from, Date to) throws InvalidPeriodException, FailedDbFetch, Unauthorised, RemoteException;

    /**
     * Gets logistical information
     *
     * @param session_token Session token
     * @param building_id   ID of the building
     * @param floor_id      ID of the floor
     * @param from          From date
     * @param to            To date
     * @return Logistics information report
     * @throws InvalidPeriodException when the date period is not valid
     * @throws FailedDbFetch          when an error occurred getting the record
     * @throws Unauthorised           when client is not authorised to access the resource
     * @throws RemoteException        when network issues occur during the remote call
     */
    LogisticsInfo getInfo(Token session_token, Integer building_id, Integer floor_id, Date from, Date to) throws InvalidPeriodException, FailedDbFetch, Unauthorised, RemoteException;

    /**
     * Gets logistical information
     *
     * @param session_token Session token
     * @param room          Room DTO
     * @param from          From date
     * @param to            To date
     * @return Logistics information report
     * @throws InvalidPeriodException when the date period is not valid
     * @throws FailedDbFetch          when an error occurred getting the record
     * @throws Unauthorised           when client is not authorised to access the resource
     * @throws RemoteException        when network issues occur during the remote call
     */
    LogisticsInfo getInfo(Token session_token, Room room, Date from, Date to) throws InvalidPeriodException, FailedDbFetch, Unauthorised, RemoteException;

    //-----------------------------------------------------[ Revenue ]--------------------------------------------------

    //TODO Add revenue method description once it's been implemented in the 'revenue' and 'uaa' components
}
