package bbk_beam.mtRooms.exception;

public class InvalidDate extends Exception {
    public InvalidDate() {
        super();
    }

    public InvalidDate(String message) {
        super(message);
    }

    public InvalidDate(String message, Throwable cause) {
        super(message, cause);
    }
}
