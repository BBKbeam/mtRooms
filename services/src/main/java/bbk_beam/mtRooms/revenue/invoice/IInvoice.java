package bbk_beam.mtRooms.revenue.invoice;

import bbk_beam.mtRooms.reservation.ICustomer;
import bbk_beam.mtRooms.reservation.ReservationContainer;

public interface IInvoice {
    /**
     * Gets the customer account details
     *
     * @param customerID CustomerID
     *                   return Customer details
     */
    public ICustomer getCustomerAccount(String customerID);

    /**
     * Gets details of reservation references
     *
     * @param getReservationID
     * @return customer reservation number
     */

    public ReservationContainer getReservationRef(Integer getReservationID);

}
