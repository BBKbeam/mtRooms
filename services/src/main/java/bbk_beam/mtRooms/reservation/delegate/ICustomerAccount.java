package bbk_beam.mtRooms.reservation.delegate;

import bbk_beam.mtRooms.admin.authentication.Token;
import bbk_beam.mtRooms.db.exception.DbQueryException;
import bbk_beam.mtRooms.db.exception.SessionExpiredException;
import bbk_beam.mtRooms.db.exception.SessionInvalidException;
import bbk_beam.mtRooms.reservation.dto.Customer;
import bbk_beam.mtRooms.reservation.exception.FailedDbWrite;
import eadjlib.datastructure.ObjectTable;

public interface ICustomerAccount {
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
    ObjectTable getCustomerAccount(Token session_token, Integer customerID) throws DbQueryException, SessionExpiredException, SessionInvalidException;

    /**
     * Reloads the customer info from the DB
     *
     * @param session_token Session token
     * @param customer      Customer DTO
     * @return Customer details
     * @throws DbQueryException        when a problem was encountered whilst processing the query
     * @throws SessionExpiredException When the session for the id provided has expired
     * @throws SessionInvalidException When the session for the id provided does not exist in the tracker
     */
    ObjectTable getCustomerAccount(Token session_token, Customer customer) throws DbQueryException, SessionExpiredException, SessionInvalidException;

    /**
     * Finds the records for customer from their surname
     *
     * @param session_token Session token
     * @param surname       Surname of customer
     * @return List of customer IDs and name of customers with the specified surname
     * @throws DbQueryException        when a problem was encountered whilst processing the query
     * @throws SessionExpiredException When the session for the id provided has expired
     * @throws SessionInvalidException When the session for the id provided does not exist in the tracker
     */
    ObjectTable findCustomer(Token session_token, String surname) throws DbQueryException, SessionExpiredException, SessionInvalidException;

    /**
     * Creates a new customer
     *
     * @param session_token Session token
     * @param customer      Customer details in DTO
     * @throws FailedDbWrite           when a problem was encountered whilst writing the customer into the records
     * @throws DbQueryException        when a problem was encountered whilst processing the query
     * @throws SessionExpiredException When the session for the id provided has expired
     * @throws SessionInvalidException When the session for the id provided does not exist in the tracker
     */
    void createNewCustomer(Token session_token, Customer customer) throws FailedDbWrite, DbQueryException, SessionExpiredException, SessionInvalidException;

    /**
     * Saves changes of a Customer container to the database
     *
     * @param session_token Session token
     * @param customer      Customer container
     * @throws FailedDbWrite           when a problem was encountered whilst writing the customer into the records
     * @throws DbQueryException        when a problem was encountered whilst processing the query
     * @throws SessionExpiredException When the session for the id provided has expired
     * @throws SessionInvalidException When the session for the id provided does not exist in the tracker
     */
    void saveCustomerChangesToDB(Token session_token, Customer customer) throws FailedDbWrite, DbQueryException, SessionExpiredException, SessionInvalidException;

    /**
     * Gets a membership type from records
     *
     * @param session_token Session token
     * @param membership_id Membership ID
     * @return Membership details
     * @throws DbQueryException        when a problem was encountered whilst processing the query
     * @throws SessionExpiredException When the session for the id provided has expired
     * @throws SessionInvalidException When the session for the id provided does not exist in the tracker
     */
    ObjectTable getMembership(Token session_token, Integer membership_id) throws DbQueryException, SessionExpiredException, SessionInvalidException;

    /**
     * Gets all membership types from records
     *
     * @param session_token Session token
     * @return Memberships details
     * @throws DbQueryException        when a problem was encountered whilst processing the query
     * @throws SessionExpiredException When the session for the id provided has expired
     * @throws SessionInvalidException When the session for the id provided does not exist in the tracker
     */
    ObjectTable getMemberships(Token session_token) throws DbQueryException, SessionExpiredException, SessionInvalidException;
}
