package bbk_beam.mtRooms.db.exception;

public class SessionInvalidException extends SessionException {
    public SessionInvalidException() {
        super();
    }

    public SessionInvalidException(String message) {
        super(message);
    }

    public SessionInvalidException(String message, Throwable cause) {
        super(message, cause);
    }
}
