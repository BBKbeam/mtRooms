package bbk_beam.mtRooms.reservation.exception;

public class InvalidOperation extends Exception {
    public InvalidOperation() {
        super();
    }

    public InvalidOperation(String message) {
        super(message);
    }

    public InvalidOperation(String message, Throwable cause) {
        super(message, cause);
    }
}
