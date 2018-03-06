package bbk_beam.mtRooms.reservation.exception;

public class FailedPaymentHashing extends Exception {
    public FailedPaymentHashing() {
        super();
    }

    public FailedPaymentHashing(String message) {
        super(message);
    }

    public FailedPaymentHashing(String message, Throwable cause) {
        super(message, cause);
    }
}
