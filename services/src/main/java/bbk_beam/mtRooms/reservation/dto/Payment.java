package bbk_beam.mtRooms.reservation.dto;

import java.util.Date;

public class Payment {
    private Integer id;
    private Integer amount;
    private Date timestamp;
    private PaymentMethod paymentMethod;

    /**
     * Constructor
     *
     * @param id            ID
     * @param amount        Amount
     * @param timestamp     Transaction timestamp
     * @param paymentMethod Method used for payment
     */
    public Payment(Integer id, Integer amount, Date timestamp, PaymentMethod paymentMethod) {
        this.id = id;
        this.amount = amount;
        this.timestamp = timestamp;
        this.paymentMethod = paymentMethod;
    }

    /**
     * Gets the payment's ID
     *
     * @return ID
     */
    public Integer id() {
        return this.id;
    }

    /**
     * Gets the payment's amount
     *
     * @return Amount
     */
    public Integer amount() {
        return this.amount;
    }

    /**
     * Gets the payment's transaction timestamp
     *
     * @return Timestamp
     */
    public Date timestamp() {
        return this.timestamp;
    }

    /**
     * Gets the payment's method used
     *
     * @return Payment method
     */
    public PaymentMethod paymentMethod() {
        return this.paymentMethod;
    }

    @Override
    public String toString() {
        return "[" + id + "]={ "
                + "amount: " + amount + ", "
                + "timestamp:" + timestamp + ", "
                + "paymentMethod:" + paymentMethod + " }";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Payment payment = (Payment) o;

        if (!id.equals(payment.id)) return false;
        if (!amount.equals(payment.amount)) return false;
        if (!timestamp.equals(payment.timestamp)) return false;
        return paymentMethod.equals(payment.paymentMethod);
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + amount.hashCode();
        result = 31 * result + timestamp.hashCode();
        result = 31 * result + paymentMethod.hashCode();
        return result;
    }
}
