package bbk_beam.mtRooms.reservation.exception;

public class FailedDbFetch extends Exception {
    public FailedDbFetch() {
        super();
    }

    public FailedDbFetch(String message) {
        super(message);
    }

    public FailedDbFetch(String message, Throwable cause) {
        super(message, cause);
    }
}
