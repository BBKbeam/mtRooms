package bbk_beam.mtRooms.reservation.exception;

public class InvalidPaymentType extends Exception {
    public InvalidPaymentType() {
        super();
    }

    public InvalidPaymentType(String message) {
        super(message);
    }

    public InvalidPaymentType(String message, Throwable cause) {
        super(message, cause);
    }
}
