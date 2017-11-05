package bbk_beam.mtRooms.revenue.invoice;

import bbk_beam.mtRooms.reservation.dto.CustomerDTO;
import bbk_beam.mtRooms.reservation.dto.ReservationDTO;

public interface IInvoice {
    /**
     * Gets the customer account details
     *
     * @param customerID CustomerID
     *                   return Customer details
     */
    public CustomerDTO getCustomerAccount(String customerID);

    /**
     * Gets details of reservation references
     *
     * @param getReservationID
     * @return customer reservation number
     */

    public ReservationDTO getReservationRef(Integer getReservationID);

}
