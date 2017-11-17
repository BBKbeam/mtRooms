package bbk_beam.mtRooms.reservation.delegate;

import bbk_beam.mtRooms.admin.authentication.Token;
import bbk_beam.mtRooms.db.exception.DbQueryException;
import bbk_beam.mtRooms.db.exception.SessionExpiredException;
import bbk_beam.mtRooms.db.exception.SessionInvalidException;
import bbk_beam.mtRooms.reservation.dto.CustomerDTO;
import javafx.util.Pair;

import java.util.Collection;

/**
 * //TODO/NOTES Maybe put a lock on currently accessed customers so that sessions don't corrupt each other?
 */

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
    public CustomerDTO getCustomerAccount(Token session_token, String customerID) throws DbQueryException, SessionExpiredException, SessionInvalidException;

    /**
     * Reloads the customer info from the DB
     *
     * @param session_token Session token
     * @param customer      Customer container
     * @return Reloaded Customer container
     * @throws DbQueryException        when a problem was encountered whilst processing the query
     * @throws SessionExpiredException When the session for the id provided has expired
     * @throws SessionInvalidException When the session for the id provided does not exist in the tracker
     */
    public CustomerDTO getCustomerAccount(Token session_token, CustomerDTO customer) throws DbQueryException, SessionExpiredException, SessionInvalidException;

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
    public Collection<Pair<Integer, String>> findCustomer(Token session_token, String surname) throws DbQueryException, SessionExpiredException, SessionInvalidException;

    /**
     * Creates a new customer
     *
     * @param session_token Session token
     * @param title         Title of customer
     * @param name          Name of customer
     * @param surname       Surname of customer
     * @return Customer container
     * @throws DbQueryException        when a problem was encountered whilst processing the query
     * @throws SessionExpiredException When the session for the id provided has expired
     * @throws SessionInvalidException When the session for the id provided does not exist in the tracker
     */
    public CustomerDTO createNewCustomer(Token session_token, String title, String name, String surname) throws DbQueryException, SessionExpiredException, SessionInvalidException;

    /**
     * Saves changes of a Customer container to the database
     *
     * @param session_token Session token
     * @param customer      Customer container
     * @throws DbQueryException        when a problem was encountered whilst processing the query
     * @throws SessionExpiredException When the session for the id provided has expired
     * @throws SessionInvalidException When the session for the id provided does not exist in the tracker
     */
    public void saveCustomerChangesToDB(Token session_token, CustomerDTO customer) throws DbQueryException, SessionExpiredException, SessionInvalidException;
}