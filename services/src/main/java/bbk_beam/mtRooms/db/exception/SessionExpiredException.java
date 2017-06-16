package bbk_beam.mtRooms.db.exception;

public class SessionExpiredException extends Exception {
    public SessionExpiredException() {
        super();
    }

    public SessionExpiredException(String message) {
        super(message);
    }

    public SessionExpiredException(String message, Throwable cause) {
        super(message, cause);
    }
}
