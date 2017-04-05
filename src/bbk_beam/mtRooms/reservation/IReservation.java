package bbk_beam.mtRooms.reservation;

public interface IReservation {

    /**
     * Gets the revervation ID
     *
     * @return Rservartion ID
     */
    Int getReservationID();


    /**
     * Gets the customer account details
     *
     * @param customerID CustomerID
     * @return Customer details
     *
     */
    public ICustomer getCustomerAccount(String customerID);




}
