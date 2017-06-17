package bbk_beam.mtRooms.db.exception;

public class InvalidSessionException extends Exception {
    public InvalidSessionException() {
        super();
    }

    public InvalidSessionException(String message) {
        super(message);
    }

    public InvalidSessionException(String message, Throwable cause) {
        super(message, cause);
    }
}
