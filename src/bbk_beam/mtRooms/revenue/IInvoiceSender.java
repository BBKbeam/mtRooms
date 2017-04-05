package bbk_beam.mtRooms.revenue;

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

    public IReservation getReservationRef(Int getReservationID);

}
