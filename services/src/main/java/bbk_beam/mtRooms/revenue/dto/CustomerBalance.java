package bbk_beam.mtRooms.revenue.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CustomerBalance implements Serializable {
    List<ReservationBalance> reservationBalances;
    Double balance;

    /**
     * Constructor
     */
    public CustomerBalance() {
        this.reservationBalances = new ArrayList<>();
        this.balance = 0d;
    }

    /**
     * Adds a ReservationBalance
     *
     * @param reservationBalance ReservationBalance object
     */
    public void addReservationBalance(ReservationBalance reservationBalance) {
        this.balance += reservationBalance.getBalance();
        this.reservationBalances.add(reservationBalance);
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
        Double cost = 0d;
        for (ReservationBalance reservationBalance : this.reservationBalances)
            cost += reservationBalance.getDiscountedCost();
        return cost;
    }

    /**
     * Gets the customer's credit (payment + refunds)
     *
     * @return Total of payments/refunds made
     */
    public Double getTotalPaid() {
        Double paid = 0d;
        for (ReservationBalance reservationBalance : this.reservationBalances)
            paid += reservationBalance.getPaymentsTotal();
        return paid;
    }

    /**
     * Gets the reservation balances for the customer
     *
     * @return List of ReservationBalance
     */
    public List<ReservationBalance> getReservationBalances() {
        return Collections.unmodifiableList(this.reservationBalances);
    }

    /**
     * Gets the number of reservations
     *
     * @return Reservation count
     */
    public int getReservationCount() {
        return this.reservationBalances.size();
    }

    @Override
    public String toString() {
        return "CustomerBalance{ " +
                "reservationBalances=" + reservationBalances +
                ", balance=" + balance +
                " }";
    }
}
