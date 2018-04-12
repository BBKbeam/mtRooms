package bbk_beam.mtRooms.uaa.exception;

public class DuplicateSession extends Exception {
    public DuplicateSession() {
        super();
    }

    public DuplicateSession(String message) {
        super(message);
    }

    public DuplicateSession(String message, Throwable cause) {
        super(message, cause);
    }
}
