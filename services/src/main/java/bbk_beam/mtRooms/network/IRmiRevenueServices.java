package bbk_beam.mtRooms.network;

import bbk_beam.mtRooms.admin.authentication.Token;
import bbk_beam.mtRooms.network.exception.Unauthorised;
import bbk_beam.mtRooms.reservation.dto.Building;
import bbk_beam.mtRooms.reservation.dto.Customer;
import bbk_beam.mtRooms.reservation.dto.Floor;
import bbk_beam.mtRooms.reservation.dto.Room;
import bbk_beam.mtRooms.reservation.exception.FailedDbFetch;
import bbk_beam.mtRooms.reservation.exception.InvalidCustomer;
import bbk_beam.mtRooms.reservation.exception.InvalidReservation;
import bbk_beam.mtRooms.revenue.dto.*;
import bbk_beam.mtRooms.revenue.exception.InvalidPeriodException;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Date;
import java.util.List;

public interface IRmiRevenueServices extends Remote {
    /**
     * Gets all buildings in real estate portfolio
     *
     * @param session_token Administration session token
     * @return List of Building DTOs
     * @throws FailedDbFetch   when error occurred during fetching of data from DB
     * @throws Unauthorised    when client is not authorised to access the resource
     * @throws RemoteException when network issues occur during the remote call
     */
    public List<Building> getBuildings(Token session_token) throws FailedDbFetch, Unauthorised, RemoteException;

    /**
     * Gets all floors in a building
     *
     * @param session_token Administration session token
     * @param building      Building DTO
     * @return List of Floor DTOs
     * @throws FailedDbFetch   when error occurred during fetching of data from DB
     * @throws Unauthorised    when client is not authorised to access the resource
     * @throws RemoteException when network issues occur during the remote call
     */
    public List<Floor> getFloors(Token session_token, Building building) throws FailedDbFetch, Unauthorised, RemoteException;

    /**
     * Gets all rooms ina floor
     *
     * @param session_token Administration session token
     * @param floor         Floor DTO
     * @return List of Room DTOs
     * @throws FailedDbFetch   when error occurred during fetching of data from DB
     * @throws Unauthorised    when client is not authorised to access the resource
     * @throws RemoteException when network issues occur during the remote call
     */
    public List<Room> getRooms(Token session_token, Floor floor) throws FailedDbFetch, Unauthorised, RemoteException;

    /**
     * Gets the balance for all customers in records.
     *
     * @param session_token Session token
     * @return List of SimpleCustomerBalance DTOs
     * @throws FailedDbFetch   when error occurred during fetching of data from DB
     * @throws Unauthorised    when client is not authorised to access the resource
     * @throws RemoteException when network issues occur during the remote call
     */
    public List<SimpleCustomerBalance> getCustomerBalance(Token session_token) throws FailedDbFetch, Unauthorised, RemoteException;

    /**
     * Gets the balance for a Customer
     *
     * @param session_token Session token
     * @param customer      Customer DTO
     * @return CustomerBalance DTO
     * @throws FailedDbFetch   when error occurred during fetching of data from DB
     * @throws Unauthorised    when client is not authorised to access the resource
     * @throws RemoteException when network issues occur during the remote call
     */
    public CustomerBalance getCustomerBalance(Token session_token, Customer customer) throws FailedDbFetch, Unauthorised, RemoteException;

    /**
     * Gets the occupancy stats for all rooms
     *
     * @param session_token Session token
     * @param from          Start of the date range
     * @param to            End of the date range
     * @return Occupancy of all rooms
     * @throws InvalidPeriodException when the date period is not valid
     * @throws FailedDbFetch          when an error occurred getting the record
     * @throws Unauthorised           when client is not authorised to access the resource
     * @throws RemoteException        when network issues occur during the remote call
     */
    public Occupancy getOccupancy(Token session_token, Date from, Date to) throws InvalidPeriodException, FailedDbFetch, Unauthorised, RemoteException;

    /**
     * Gets the occupancy stats for all rooms in a building
     *
     * @param session_token Session token
     * @param building      Building DTO
     * @param from          Start of the date range
     * @param to            End of the date range
     * @return Occupancy of all rooms
     * @throws InvalidPeriodException when the date period is not valid
     * @throws FailedDbFetch          when an error occurred getting the record
     * @throws Unauthorised           when client is not authorised to access the resource
     * @throws RemoteException        when network issues occur during the remote call
     */
    public Occupancy getOccupancy(Token session_token, Building building, Date from, Date to) throws InvalidPeriodException, FailedDbFetch, Unauthorised, RemoteException;

    /**
     * Gets the occupancy stats for all rooms on a floor
     *
     * @param session_token Session token
     * @param floor         Floor DTO
     * @param from          Start of the date range
     * @param to            End of the date range
     * @return Occupancy of all rooms
     * @throws InvalidPeriodException when the date period is not valid
     * @throws FailedDbFetch          when an error occurred getting the record
     * @throws Unauthorised           when client is not authorised to access the resource
     * @throws RemoteException        when network issues occur during the remote call
     */
    public Occupancy getOccupancy(Token session_token, Floor floor, Date from, Date to) throws InvalidPeriodException, FailedDbFetch, Unauthorised, RemoteException;

    /**
     * Gets the occupancy stats for a room
     *
     * @param session_token Session token
     * @param room          Room DTO
     * @param from          Start of the date range
     * @param to            End of the date range
     * @return Occupancy of all rooms
     * @throws InvalidPeriodException when the date period is not valid
     * @throws FailedDbFetch          when an error occurred getting the record
     * @throws Unauthorised           when client is not authorised to access the resource
     * @throws RemoteException        when network issues occur during the remote call
     */
    public Occupancy getOccupancy(Token session_token, Room room, Date from, Date to) throws InvalidPeriodException, FailedDbFetch, Unauthorised, RemoteException;

    /**
     * Creates an invoice for a reservation
     *
     * @param session_token  Session token
     * @param reservation_id Reservation ID
     * @return Invoice DTO
     * @throws InvalidReservation when reservation ID does not match any in records
     * @throws InvalidCustomer    when the customer ID linked to the reservation is not in records
     * @throws FailedDbFetch      when an error occurred getting the record
     * @throws Unauthorised       when client is not authorised to access the resource
     * @throws RemoteException    when network issues occur during the remote call
     */
    Invoice createInvoice(Token session_token, Integer reservation_id) throws InvalidReservation, InvalidCustomer, FailedDbFetch, Unauthorised, RemoteException;

    /**
     * Gets the list of payments made within a date range
     *
     * @param session_token Session token
     * @param from          Start of the date range
     * @param to            End of the date range
     * @return List of payments ordered by ascending Timestamp
     * @throws InvalidPeriodException when the date period is not valid
     * @throws FailedDbFetch          when an error occurred getting the record
     * @throws Unauthorised           when client is not authorised to access the resource
     * @throws RemoteException        when network issues occur during the remote call
     */
    List<DetailedPayment> getPayments(Token session_token, Date from, Date to) throws InvalidPeriodException, FailedDbFetch, Unauthorised, RemoteException;
}
