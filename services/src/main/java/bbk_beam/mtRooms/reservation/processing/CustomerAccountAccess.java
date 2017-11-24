package bbk_beam.mtRooms.reservation.processing;

import bbk_beam.mtRooms.admin.authentication.Token;
import bbk_beam.mtRooms.db.TimestampConverter;
import bbk_beam.mtRooms.db.exception.DbQueryException;
import bbk_beam.mtRooms.db.exception.SessionExpiredException;
import bbk_beam.mtRooms.db.exception.SessionInvalidException;
import bbk_beam.mtRooms.reservation.delegate.ICustomerAccount;
import bbk_beam.mtRooms.reservation.dto.Customer;
import bbk_beam.mtRooms.reservation.exception.FailedDbFetch;
import bbk_beam.mtRooms.reservation.exception.FailedDbWrite;
import bbk_beam.mtRooms.reservation.exception.InvalidCustomer;
import eadjlib.datastructure.ObjectTable;
import eadjlib.logger.Logger;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CustomerAccountAccess {
    private final Logger log = Logger.getLoggerInstance(CustomerAccountAccess.class.getName());
    private ICustomerAccount db_delegate;

    /**
     * Constructor
     *
     * @param customer_account_delegate ICustomerAccount instance
     */
    public CustomerAccountAccess(ICustomerAccount customer_account_delegate) {
        this.db_delegate = customer_account_delegate;
    }

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
    public Customer getCustomerAccount(Token session_token, Integer customerID) throws InvalidCustomer, DbQueryException, SessionExpiredException, SessionInvalidException {
        ObjectTable table = this.db_delegate.getCustomerAccount(session_token, customerID);
        try {
            HashMap<String, Object> row = table.getRow(1);
            return new Customer(
                    (Integer) row.get("id"),
                    (Integer) row.get("membership_type_id"),
                    TimestampConverter.getDateObject((String) row.get("customer_since")),
                    (String) row.get("title"),
                    (String) row.get("name"),
                    (String) row.get("surname"),
                    (String) row.get("address_1"),
                    (String) row.get("address_2"),
                    (String) row.get("postcode"),
                    (String) row.get("city"),
                    (String) row.get("county"),
                    (String) row.get("country"),
                    (String) row.get("telephone_1"),
                    (String) row.get("telephone_2"),
                    (String) row.get("email"));
        } catch (IndexOutOfBoundsException e) {
            log.log_Error("Customer [", customerID, "] does not exist in records.");
            throw new InvalidCustomer("Customer [" + customerID + "] does not exist in records.", e);
        }
    }

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
    public Customer getCustomerAccount(Token session_token, Customer customer) throws InvalidCustomer, FailedDbFetch, SessionExpiredException, SessionInvalidException {
        try {
            ObjectTable table = this.db_delegate.getCustomerAccount(session_token, customer);
            if (!table.isEmpty()) {
                Integer id = table.getInteger(1, 1);
                return new Customer(
                        id,
                        customer.membershipTypeID(),
                        customer.accountCreationDate(),
                        customer.title(),
                        customer.name(),
                        customer.surname(),
                        customer.address1(),
                        customer.address2(),
                        customer.postCode(),
                        customer.city(),
                        customer.county(),
                        customer.country(),
                        customer.phone1(),
                        customer.phone2(),
                        customer.email());
            } else {
                log.log_Error("Customer does not exist in record: ", customer);
                throw new InvalidCustomer("Customer [" + customer.customerID() + "] does not exist in record.");
            }
        } catch (DbQueryException e) {
            log.log_Error("Failed to fetch customer [", customer.customerID(), "]'s record.");
            throw new FailedDbFetch("Failed to fetch customer [" + customer.customerID() + "]'s record.", e);
        }
    }

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
    public List<Pair<Integer, String>> findCustomer(Token session_token, String surname) throws FailedDbFetch, SessionExpiredException, SessionInvalidException {
        try {
            ObjectTable table = this.db_delegate.findCustomer(session_token, surname);
            List<Pair<Integer, String>> list = new ArrayList<>();
            for (int i = 1; i <= table.rowCount(); i++) {
                HashMap<String, Object> row = table.getRow(i);
                list.add(new Pair<>((Integer) row.get("id"), (String) row.get("name")));
            }
            return list;
        } catch (DbQueryException e) {
            log.log_Error("Failed to fetch customers with surname '", surname, "' from records.");
            throw new FailedDbFetch("Failed to fetch customers with surname '" + surname + "' from records.", e);
        }
    }

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
    public Customer createNewCustomer(Token session_token, Customer customer) throws FailedDbWrite, FailedDbFetch, SessionExpiredException, SessionInvalidException {
        try {
            this.db_delegate.createNewCustomer(session_token, customer);
            return getCustomerAccount(session_token, customer); //Get a fully updated DTO that includes the ID of the new customer
        } catch (DbQueryException e) {
            log.log_Error("Could not process record creation order for customer: ", customer);
            throw new FailedDbWrite("Could not process record creation order for customer", e);
        } catch (FailedDbWrite e) {
            log.log_Error("Could not create new record for customer: ", customer);
            throw new FailedDbWrite("Could not create new record for customer", e);
        } catch (FailedDbFetch e) {
            log.log_Error("Could not get back created record of customer: ", customer);
            throw new FailedDbFetch("Could not get back created record of customer", e);
        } catch (InvalidCustomer e) {
            log.log_Error("Failed to identify created record of customer: ", customer);
            throw new FailedDbFetch("Failed to identify created record of customer", e);
        }
    }

    /**
     * Saves changes of a Customer container to the database
     *
     * @param session_token Session token
     * @param customer      Customer container
     * @throws FailedDbWrite           when record update failed
     * @throws SessionExpiredException When the session for the id provided has expired
     * @throws SessionInvalidException When the session for the id provided does not exist in the tracker
     */
    public void saveCustomerChangesToDB(Token session_token, Customer customer) throws FailedDbWrite, SessionExpiredException, SessionInvalidException {
        try {
            this.db_delegate.saveCustomerChangesToDB(session_token, customer);
        } catch (FailedDbWrite e) {
            log.log_Error("Update to customer [", customer.customerID(), "]'s details unsuccessful: Db write fail.");
            throw new FailedDbWrite("Update to customer [" + customer.customerID() + "]'s details unsuccessful: Db write fail.");
        } catch (DbQueryException e) {
            log.log_Error("Update to customer [", customer.customerID(), "]'s details unsuccessful: SQL Query issue.");
            throw new FailedDbWrite("Update to customer [" + customer.customerID() + "]'s details unsuccessful: SQL Query issue.");
        }
    }
}
