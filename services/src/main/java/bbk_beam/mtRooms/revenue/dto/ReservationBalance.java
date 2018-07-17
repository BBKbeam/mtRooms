package bbk_beam.mtRooms.revenue.dto;

import bbk_beam.mtRooms.reservation.dto.Discount;
import bbk_beam.mtRooms.reservation.dto.Payment;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class ReservationBalance implements Serializable {
    private Integer reservation_id;
    private Integer reserved_room_count;
    private Double reserved_room_cost;
    private List<Payment> payments;
    private Double payment_total;
    private Discount discount;

    /**
     * Constructor
     *
     * @param reservation_id Reservation ID
     * @param room_count     Number of rooms booked in reservation
     * @param room_cost      Total room cost
     * @param discount       Discount DTO of the reservation
     */
    public ReservationBalance(
            Integer reservation_id,
            Integer room_count,
            Double room_cost,
            Discount discount) {
        this.reservation_id = reservation_id;
        this.reserved_room_count = room_count;
        this.reserved_room_cost = room_cost;
        this.payments = new ArrayList<>();
        this.payment_total = 0d;
        this.discount = discount;
    }

    /**
     * Constructor
     *
     * @param reservation_id Reservation ID
     * @param room_count     Number of rooms booked in reservation
     * @param room_cost      Total room cost
     * @param discount       Discount DTO of the reservation
     * @param payments       List of Payment DTOs
     */
    public ReservationBalance(
            Integer reservation_id,
            Integer room_count,
            Double room_cost,
            Discount discount,
            List<Payment> payments) {
        this.reservation_id = reservation_id;
        this.reserved_room_count = room_count;
        this.reserved_room_cost = room_cost;
        this.payments = new ArrayList<>();
        this.payments = payments;
        this.payment_total = 0d;
        this.discount = discount;
        for (Payment payment : this.payments) {
            this.payment_total += payment.amount();
        }
    }

    /**
     * Add a payment
     *
     * @param payment Payment DTO
     */
    public void addPayment(Payment payment) {
        this.payment_total += payment.amount();
        this.payments.add(payment);
    }

    /**
     * Add a list of payments
     *
     * @param payments List of Payment DTOs
     */
    public void addPayments(List<Payment> payments) {
        for (Payment payment : payments) {
            this.payment_total += payment.amount();
            this.payments.add(payment);
        }
    }

    /**
     * Gets the Reservation ID
     *
     * @return Reservation ID
     */
    public Integer getReservationID() {
        return this.reservation_id;
    }

    /**
     * Gets the base cost of all the reserved rooms
     *
     * @return Raw cost of rooms
     */
    public Double getRawCost() {
        return this.reserved_room_cost;
    }

    /**
     * Gets the post-discount cost of the reserved rooms
     *
     * @return Discounted cost of rooms
     */
    public Double getDiscountedCost() {
        return getRawCost() * (100 - discount.rate()) / 100;
    }

    /**
     * Gets the total of the payments made
     *
     * @return Total of the payments
     */
    public Double getPaymentsTotal() {
        return this.payment_total;
    }

    /**
     * Gets the reservation balance (cost - payments)
     *
     * @return Balance
     */
    public Double getBalance() {
        return (payment_total - getDiscountedCost());
    }

    /**
     * Gets the payments for the reservation
     *
     * @return List of payments made
     */
    public List<Payment> getPayments() {
        return Collections.unmodifiableList(this.payments);
    }

    /**
     * Gets the discount associated with the reservation
     *
     * @return Discount DTO
     */
    public Discount getDiscount() {
        return this.discount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReservationBalance that = (ReservationBalance) o;
        return Objects.equals(reservation_id, that.reservation_id) &&
                Objects.equals(reserved_room_count, that.reserved_room_count) &&
                Objects.equals(reserved_room_cost, that.reserved_room_cost) &&
                Objects.equals(payments, that.payments) &&
                Objects.equals(payment_total, that.payment_total) &&
                Objects.equals(discount, that.discount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(reservation_id, reserved_room_count, reserved_room_cost, payments, payment_total, discount);
    }

    @Override
    public String toString() {
        return "ReservationBalance[" + reservation_id + "]={ " +
                "reserved_room_count=" + reserved_room_count +
                ", room cost=" + getRawCost() +
                ", discount=" + discount.rate() +
                ", sub_total=" + getDiscountedCost() +
                ", payments=" + getPaymentsTotal() +
                ", balance=" + getBalance() +
                " }";
    }
}
