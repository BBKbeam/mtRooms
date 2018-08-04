package bbk_beam.mtRooms.ui.model.frontdesk;

import bbk_beam.mtRooms.reservation.dto.Payment;
import javafx.beans.property.*;

import java.text.SimpleDateFormat;

public class CustomerPaymentsModel {
    static private final String CHECK = "\u2713";
    static private final SimpleDateFormat dateFormat = new SimpleDateFormat("YYYY/MM/dd - HH:mm:ss");
    private Payment payment;
    private IntegerProperty payment_id = new SimpleIntegerProperty();
    private StringProperty timestamp = new SimpleStringProperty();
    private SimpleDoubleProperty amount = new SimpleDoubleProperty();
    private IntegerProperty reservation_id = new SimpleIntegerProperty();
    private StringProperty method = new SimpleStringProperty();
    private StringProperty note = new SimpleStringProperty();

    /**
     * Constructor
     *
     * @param reservation_id Reservation ID linked to payment
     * @param payment        Payment DTO
     */
    public CustomerPaymentsModel(Integer reservation_id, Payment payment) {
        this.payment = payment;
        this.payment_id.set(payment.id());
        this.timestamp.set(dateFormat.format(payment.timestamp()));
        this.amount.set(payment.amount());
        this.reservation_id.set(reservation_id);
        this.method.set(payment.paymentMethod().description());
        this.note.set(payment.note() == null || payment.note().isEmpty() ? "" : CHECK);
    }

    /**
     * Gets the Payment DTO
     *
     * @return Payment DTO
     */
    public Payment getPayment() {
        return payment;
    }

    /**
     * Gets the payment ID property
     *
     * @return Payment ID property
     */
    public IntegerProperty paymentIdProperty() {
        return payment_id;
    }

    /**
     * Gets the payment timestamp property
     *
     * @return Timestamp property
     */
    public StringProperty timestampProperty() {
        return timestamp;
    }

    /**
     * Gets the payment amount property
     *
     * @return Payment amount property
     */
    public SimpleDoubleProperty amountProperty() {
        return amount;
    }

    /**
     * Gets the reservation ID
     *
     * @return Reservation ID
     */
    public Integer getReservationID() {
        return reservation_id.get();
    }

    /**
     * Gets the reservation ID property
     *
     * @return Reservation ID property
     */
    public IntegerProperty reservationIdProperty() {
        return reservation_id;
    }

    /**
     * Gets the payment method property
     *
     * @return Payment method property
     */
    public StringProperty methodProperty() {
        return method;
    }

    /**
     * Gets payment note property
     *
     * @return Note property
     */
    public StringProperty noteProperty() {
        return note;
    }
}