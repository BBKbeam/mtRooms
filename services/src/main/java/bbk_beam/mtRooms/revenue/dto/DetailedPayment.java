package bbk_beam.mtRooms.revenue.dto;

import bbk_beam.mtRooms.reservation.dto.Payment;
import bbk_beam.mtRooms.reservation.dto.PaymentMethod;

import java.io.Serializable;
import java.util.Date;

public class DetailedPayment extends Payment implements Serializable {
    private Integer customer_id;
    private Integer reservation_id;

    /**
     * Constructor
     *
     * @param customer_id    Customer ID
     * @param reservation_id Reservation ID
     * @param payment_id     Payment ID
     * @param hashID         Hash
     * @param amount         Payment amount
     * @param timestamp      Timestamp of payment
     * @param note           Note
     * @param paymentMethod  Method of payment
     */
    public DetailedPayment(
            Integer customer_id,
            Integer reservation_id,
            Integer payment_id,
            String hashID,
            Double amount,
            Date timestamp,
            String note,
            PaymentMethod paymentMethod
    ) {
        super(payment_id, hashID, amount, timestamp, note, paymentMethod);
        this.customer_id = customer_id;
        this.reservation_id = reservation_id;
    }

    /**
     * Gets the customer ID of the payment
     *
     * @return Customer ID
     */
    public Integer customerID() {
        return this.customer_id;
    }

    /**
     * Gets the reservation ID for the payment
     *
     * @return Reservation ID
     */
    public Integer reservationID() {
        return this.reservation_id;
    }

    /**
     * Gets the Payment ID
     *
     * @return Payment ID
     */
    public Integer paymentID() {
        return super.id();
    }

    @Override
    public String hashID() {
        return super.hashID();
    }

    @Override
    public Double amount() {
        return super.amount();
    }

    @Override
    public Date timestamp() {
        return super.timestamp();
    }

    @Override
    public String note() {
        return super.note();
    }

    @Override
    public void setNote(String note) {
        super.setNote(note);
    }

    @Override
    public PaymentMethod paymentMethod() {
        return super.paymentMethod();
    }

    @Override
    public String toString() {
        return "[" + id() + "]={ "
                + "customer ID: " + customer_id + ", "
                + "reservation ID: " + reservation_id + ", "
                + "hashID: " + hashID() + ", "
                + "amount: " + amount() + ", "
                + "timestamp: " + timestamp() + ", "
                + "paymentMethod: " + paymentMethod() + ", "
                + "note: " + note() + " }";
    }
}
