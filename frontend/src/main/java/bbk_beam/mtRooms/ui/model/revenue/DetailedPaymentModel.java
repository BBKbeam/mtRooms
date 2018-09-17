package bbk_beam.mtRooms.ui.model.revenue;

import bbk_beam.mtRooms.revenue.dto.DetailedPayment;
import javafx.beans.property.*;

import java.text.SimpleDateFormat;

public class DetailedPaymentModel {
    static private final SimpleDateFormat dateFormat = new SimpleDateFormat("YYYY/MM/dd - HH:mm:ss");
    private IntegerProperty paymentId = new SimpleIntegerProperty();
    private IntegerProperty reservationId = new SimpleIntegerProperty();
    private IntegerProperty customerId = new SimpleIntegerProperty();
    private DoubleProperty amount = new SimpleDoubleProperty();
    private StringProperty timestamp = new SimpleStringProperty();
    private StringProperty method = new SimpleStringProperty();
    private StringProperty note = new SimpleStringProperty();

    /**
     * Constructor
     *
     * @param payment DetailedPayment DTO
     */
    public DetailedPaymentModel(DetailedPayment payment) {
        this.paymentId.set(payment.paymentID());
        this.reservationId.set(payment.reservationID());
        this.customerId.set(payment.customerID());
        this.amount.set(payment.amount());
        this.timestamp.set(dateFormat.format(payment.timestamp()));
        this.method.set(payment.paymentMethod().description());
        this.note.set(payment.note());
    }

    /**
     * Gets the Payment ID property
     *
     * @return Payment ID property
     */
    public IntegerProperty paymentIdProperty() {
        return paymentId;
    }

    /**
     * Gets the reservation ID property
     *
     * @return Reservation ID property
     */
    public IntegerProperty reservationIdProperty() {
        return reservationId;
    }

    /**
     * Gets the customer ID property
     *
     * @return Customer ID property
     */
    public IntegerProperty customerIdProperty() {
        return customerId;
    }

    /**
     * Gets the payment amount property
     *
     * @return Payment amount property
     */
    public DoubleProperty amountProperty() {
        return amount;
    }

    /**
     * Gets the timestamp property
     *
     * @return Timestamp property
     */
    public StringProperty timestampProperty() {
        return timestamp;
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
     * Gets the payment note property
     *
     * @return Payment note property
     */
    public StringProperty noteProperty() {
        return note;
    }

    /**
     * Gets the note existence state
     *
     * @return Note exists state
     */
    public boolean hasNote() {
        return !this.note.get().isEmpty();
    }
}
