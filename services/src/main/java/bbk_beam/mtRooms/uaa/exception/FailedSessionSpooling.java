package bbk_beam.mtRooms.uaa.exception;

public class FailedSessionSpooling extends Exception {
    public FailedSessionSpooling() {
        super();
    }

    public FailedSessionSpooling(String message) {
        super(message);
    }

    public FailedSessionSpooling(String message, Throwable cause) {
        super(message, cause);
    }
}
