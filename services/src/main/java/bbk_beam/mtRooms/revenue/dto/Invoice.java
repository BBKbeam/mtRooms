package bbk_beam.mtRooms.revenue.dto;

import bbk_beam.mtRooms.reservation.dto.Customer;
import bbk_beam.mtRooms.reservation.dto.Reservation;

import java.io.Serializable;

public class Invoice implements Serializable {
    private Customer customer;
    private Reservation reservation;
    private ReservationBalance reservation_balance;
    private SimpleCustomerBalance customer_balance;

    /**
     * Constructor
     *
     * @param customer            Customer DTO
     * @param reservation         Reservation DTO
     * @param reservation_balance ReservationBalance DTO
     * @param customer_balance    SimpleCustomerBalance DTO
     */
    public Invoice(
            Customer customer,
            Reservation reservation,
            ReservationBalance reservation_balance,
            SimpleCustomerBalance customer_balance) {
        this.customer = customer;
        this.reservation = reservation;
        this.reservation_balance = reservation_balance;
        this.customer_balance = customer_balance;
    }

    /**
     * Gets the customer information for the invoice
     *
     * @return Customer DTO
     */
    public Customer customer() {
        return this.customer;
    }

    /**
     * Gets the reservation information for the invoice
     *
     * @return Reservation DTO
     */
    public Reservation reservation() {
        return this.reservation;
    }

    /**
     * Gets the balance information for the reservation invoiced
     *
     * @return ReservationBalance DTO
     */
    public ReservationBalance reservationBalance() {
        return this.reservation_balance;
    }

    /**
     * Gets the customer's current balance on their account
     *
     * @return SimpleCustomerBalance DTO
     */
    public SimpleCustomerBalance customerBalance() {
        return this.customer_balance;
    }
}
