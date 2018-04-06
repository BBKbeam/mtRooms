package bbk_beam.mtRooms.uaa.exception;

public class SessionActive extends Exception {
    public SessionActive() {
        super();
    }

    public SessionActive(String message) {
        super(message);
    }

    public SessionActive(String message, Throwable cause) {
        super(message, cause);
    }
}
