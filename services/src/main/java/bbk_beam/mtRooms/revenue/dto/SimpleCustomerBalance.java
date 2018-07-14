package bbk_beam.mtRooms.revenue.dto;

import java.io.Serializable;

public class SimpleCustomerBalance implements Serializable {
    private Integer customer_id;
    private Integer reservation_count;
    private Double cost;
    private Double paid;
    private Double balance;

    /**
     * Constructor
     *
     * @param customer_id       Customer ID
     * @param reservation_count Number of reservations made by customer
     * @param cost              Total cost of reservations made (inc. discounts)
     * @param paid              Total of all payment transactions made by customer (inc. refunds)
     * @param balance           Final credit balance for customer
     */
    public SimpleCustomerBalance(
            Integer customer_id,
            Integer reservation_count,
            Double cost,
            Double paid,
            Double balance) {
        this.customer_id = customer_id;
        this.reservation_count = reservation_count;
        this.cost = cost;
        this.paid = paid;
        this.balance = balance;
    }

    /**
     * Gets the customer's ID
     *
     * @return Customer ID for the balance
     */
    public Integer getCustomerID() {
        return this.customer_id;
    }

    /**
     * Gets the final balance for the customer
     *
     * @return End balance
     */
    public Double getBalance() {
        return this.balance;
    }

    /**
     * Gets the total cost (inc. discounts) of all reservations
     *
     * @return Total cost of reservations
     */
    public Double getTotalCost() {
        return this.cost;
    }

    /**
     * Gets the customer's credit (payment + refunds)
     *
     * @return Total of payments/refunds made
     */
    public Double getTotalPaid() {
        return this.paid;
    }

    /**
     * Gets the number of reservations
     *
     * @return Reservation count
     */
    public int getReservationCount() {
        return this.reservation_count;
    }

    @Override
    public String toString() {
        return "SimpleCustomerBalance{ " + "customer_id=" + customer_id +
                ", reservation_count=" + reservation_count +
                ", cost=" + cost +
                ", paid=" + paid +
                ", balance=" + balance +
                " }";
    }
}
