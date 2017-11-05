package bbk_beam.mtRooms.reservation.processing;

import bbk_beam.mtRooms.reservation.dto.CustomerDTO;
import javafx.util.Pair;

import java.util.Collection;

public class CustomerAccountAccess {
    /**
     * Gets the Customer account details
     *
     * @param customerID Customer ID
     * @return Customer details
     */
    public CustomerDTO getCustomerAccount(String customerID) {
        return null;
    }

    /**
     * Reloads the customer info from the DB
     *
     * @param customer Customer container
     * @return Reloaded Customer container
     */
    public CustomerDTO getCustomerAccount(CustomerDTO customer) {
        return null;
    }

    /**
     * Finds the records for customer from their surname
     *
     * @param surname Surname of customer
     * @return List of customer IDs and name of customers with the specified surname
     */
    public Collection<Pair<String, String>> findCustomer(String surname) {
        return null;
    }

    /**
     * Creates a new customer
     *
     * @param title   Title of customer
     * @param name    Name of customer
     * @param surname Surname of customer
     * @return Customer container
     */
    public CustomerDTO createNewCustomer(String title, String name, String surname) {
        return null;
    }

    /**
     * Saves changes of a Customer container to the database
     *
     * @param customer Customer container
     */
    public void saveCustomerChangesToDB(CustomerDTO customer) {

    }
}
