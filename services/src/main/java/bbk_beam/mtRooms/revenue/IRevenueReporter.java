package bbk_beam.mtRooms.revenue;

import bbk_beam.mtRooms.admin.authentication.Token;
import bbk_beam.mtRooms.db.exception.SessionExpiredException;
import bbk_beam.mtRooms.db.exception.SessionInvalidException;
import bbk_beam.mtRooms.reservation.dto.Building;
import bbk_beam.mtRooms.reservation.dto.Customer;
import bbk_beam.mtRooms.reservation.dto.Floor;
import bbk_beam.mtRooms.reservation.dto.Room;
import bbk_beam.mtRooms.reservation.exception.FailedDbFetch;
import bbk_beam.mtRooms.reservation.exception.InvalidCustomer;
import bbk_beam.mtRooms.reservation.exception.InvalidReservation;
import bbk_beam.mtRooms.revenue.dto.CustomerBalance;
import bbk_beam.mtRooms.revenue.dto.Invoice;
import bbk_beam.mtRooms.revenue.dto.Occupancy;
import bbk_beam.mtRooms.revenue.dto.SimpleCustomerBalance;
import bbk_beam.mtRooms.revenue.exception.InvalidPeriodException;

import java.util.Date;
import java.util.List;

public interface IRevenueReporter {
    /**
     * Gets all buildings in real estate portfolio
     *
     * @param session_token Administration session token
     * @return List of Building DTOs
     * @throws FailedDbFetch           when error occurred during fetching of data from DB
     * @throws SessionExpiredException when current user session has expired
     * @throws SessionInvalidException when user session is not valid
     */
    List<Building> getBuildings(Token session_token) throws FailedDbFetch, SessionExpiredException, SessionInvalidException;

    /**
     * Gets all floors in a building
     *
     * @param session_token Administration session token
     * @param building      Building DTO
     * @return List of Floor DTOs
     * @throws FailedDbFetch           when error occurred during fetching of data from DB
     * @throws SessionExpiredException when current user session has expired
     * @throws SessionInvalidException when user session is not valid
     */
    List<Floor> getFloors(Token session_token, Building building) throws FailedDbFetch, SessionExpiredException, SessionInvalidException;

    /**
     * Gets all rooms ina floor
     *
     * @param session_token Administration session token
     * @param floor         Floor DTO
     * @return List of Room DTOs
     * @throws FailedDbFetch           when error occurred during fetching of data from DB
     * @throws SessionExpiredException when current user session has expired
     * @throws SessionInvalidException when user session is not valid
     */
    List<Room> getRooms(Token session_token, Floor floor) throws FailedDbFetch, SessionExpiredException, SessionInvalidException;

    /**
     * Gets the balance for all customers in records.
     *
     * @param session_token Session token
     * @return List of SimpleCustomerBalance DTOs
     * @throws FailedDbFetch           when error occurred during fetching of data from DB
     * @throws SessionExpiredException when current user session has expired
     * @throws SessionInvalidException when user session is not valid
     */
    List<SimpleCustomerBalance> getCustomerBalance(Token session_token) throws FailedDbFetch, SessionExpiredException, SessionInvalidException;

    /**
     * Gets the balance for a Customer
     *
     * @param session_token Session token
     * @param customer      Customer DTO
     * @return CustomerBalance DTO (detailed)
     * @throws FailedDbFetch           when error occurred during fetching of data from DB
     * @throws SessionExpiredException when current user session has expired
     * @throws SessionInvalidException when user session is not valid
     */
    CustomerBalance getCustomerBalance(Token session_token, Customer customer) throws FailedDbFetch, SessionExpiredException, SessionInvalidException;

    /**
     * Gets the balance for a customer
     *
     * @param session_token Session token
     * @param customer      Customer DTO
     * @return SimpleCustomerBalance DTO (summary)
     * @throws FailedDbFetch
     * @throws SessionExpiredException
     * @throws SessionInvalidException
     */
    SimpleCustomerBalance getSimpleCustomerBalance(Token session_token, Customer customer) throws FailedDbFetch, SessionExpiredException, SessionInvalidException;

    /**
     * Gets the occupancy stats for all rooms
     *
     * @param session_token Session token
     * @param from          Start of the date range
     * @param to            End of the date range
     * @return Occupancy of all rooms
     * @throws InvalidPeriodException  when the date period is not valid
     * @throws FailedDbFetch           when an error occurred getting the record
     * @throws SessionExpiredException When the session for the id provided has expired
     * @throws SessionInvalidException When the session for the id provided does not exist in the tracker
     */
    Occupancy getOccupancy(Token session_token, Date from, Date to) throws InvalidPeriodException, FailedDbFetch, SessionExpiredException, SessionInvalidException;

    /**
     * Gets the occupancy stats for all rooms in a building
     *
     * @param session_token Session token
     * @param building      Building DTO
     * @param from          Start of the date range
     * @param to            End of the date range
     * @return Occupancy of all rooms
     * @throws InvalidPeriodException  when the date period is not valid
     * @throws FailedDbFetch           when an error occurred getting the record
     * @throws SessionExpiredException When the session for the id provided has expired
     * @throws SessionInvalidException When the session for the id provided does not exist in the tracker
     */
    Occupancy getOccupancy(Token session_token, Building building, Date from, Date to) throws InvalidPeriodException, FailedDbFetch, SessionExpiredException, SessionInvalidException;

    /**
     * Gets the occupancy stats for all rooms on a floor
     *
     * @param session_token Session token
     * @param floor         Floor DTO
     * @param from          Start of the date range
     * @param to            End of the date range
     * @return Occupancy of all rooms
     * @throws InvalidPeriodException  when the date period is not valid
     * @throws FailedDbFetch           when an error occurred getting the record
     * @throws SessionExpiredException When the session for the id provided has expired
     * @throws SessionInvalidException When the session for the id provided does not exist in the tracker
     */
    Occupancy getOccupancy(Token session_token, Floor floor, Date from, Date to) throws InvalidPeriodException, FailedDbFetch, SessionExpiredException, SessionInvalidException;

    /**
     * Gets the occupancy stats for a room
     *
     * @param session_token Session token
     * @param room          Room DTO
     * @param from          Start of the date range
     * @param to            End of the date range
     * @return Occupancy of all rooms
     * @throws InvalidPeriodException  when the date period is not valid
     * @throws FailedDbFetch           when an error occurred getting the record
     * @throws SessionExpiredException When the session for the id provided has expired
     * @throws SessionInvalidException When the session for the id provided does not exist in the tracker
     */
    Occupancy getOccupancy(Token session_token, Room room, Date from, Date to) throws InvalidPeriodException, FailedDbFetch, SessionExpiredException, SessionInvalidException;

    /**
     * Creates an invoice for a reservation
     *
     * @param session_token  Session token
     * @param reservation_id Reservation ID
     * @return Invoice DTO
     * @throws InvalidReservation      when reservation ID does not match any in records
     * @throws InvalidCustomer         when the customer ID linked to the reservation is not in records
     * @throws FailedDbFetch           when error occurred during fetching of data from DB
     * @throws SessionExpiredException when current user session has expired
     * @throws SessionInvalidException when user session is not valid
     */
    Invoice createInvoice(Token session_token, Integer reservation_id) throws InvalidReservation, InvalidCustomer, FailedDbFetch, SessionExpiredException, SessionInvalidException;
}
