package bbk_beam.mtRooms.revenue.invoice;

import bbk_beam.mtRooms.reservation.dto.Customer;
import bbk_beam.mtRooms.reservation.dto.Reservation;

public interface IInvoice {
    /**
     * Gets the customer account details
     *
     * @param customerID CustomerID
     *                   return Customer details
     */
    Customer getCustomerAccount(String customerID);

    /**
     * Gets details of reservation references
     *
     * @param getReservationID
     * @return customer reservation number
     */

    Reservation getReservationRef(Integer getReservationID);

}
