package bbk_beam.mtRooms.uaa.exception;

public class SessionLocked extends Exception {
    public SessionLocked() {
        super();
    }

    public SessionLocked(String message) {
        super(message);
    }

    public SessionLocked(String message, Throwable cause) {
        super(message, cause);
    }
}
