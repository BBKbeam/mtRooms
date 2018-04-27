package bbk_beam.mtRooms.uaa.exception;

public class SessionReset extends Exception {
    public SessionReset() {
        super();
    }

    public SessionReset(String message) {
        super(message);
    }

    public SessionReset(String message, Throwable cause) {
        super(message, cause);
    }
}
