package bbk_beam.mtRooms.uaa.exception;

public class SessionInactive extends Exception {
    public SessionInactive() {
        super();
    }

    public SessionInactive(String message) {
        super(message);
    }

    public SessionInactive(String message, Throwable cause) {
        super(message, cause);
    }
}
