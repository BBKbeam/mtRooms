package bbk_beam.mtRooms.customer;

import javafx.util.Pair;
import java.util.Collection;

public interface ICustomerAccountAccess {
    /**
     * Gets the Customer account details
     * @param customerID Customer ID
     * @return Customer details
     */
    public ICustomer getCustomerAccount( String customerID );

    /**
     * Finds the records for customer from their surname
     * @param surname Surname of customer
     * @return List of customer IDs and name of customers with the specified surname
     */
    public Collection<Pair<String,String>> findCustomer( String surname );
}
