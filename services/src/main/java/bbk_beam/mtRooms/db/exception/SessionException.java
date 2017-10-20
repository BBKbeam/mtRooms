package bbk_beam.mtRooms.db.exception;

public class SessionException extends Exception {
    public SessionException() {
        super();
    }

    public SessionException(String message) {
        super(message);
    }

    public SessionException(String message, Throwable cause) {
        super(message, cause);
    }
}
