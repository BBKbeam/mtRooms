package bbk_beam.mtRooms.revenue;

import bbk_beam.mtRooms.customer.ICustomer;
import bbk_beam.mtRooms.reservation.IReservation;

public interface IInvoiceSender {
    /**
     * Gets the customer account details
     *
     * @param customerID CustomerID
     *  return Customer details
     */
    public ICustomer getCustomerAccount(String customerID);

    /**
     * Gets details of reservation references
     *
     * @param getReservationID
     * @return customer reservation number
     */

    public IReservation getReservationRef(Integer getReservationID);

}
