package bbk_beam.mtRooms.reservation.exception;

public class FailedDbWrite extends Exception {
    public FailedDbWrite() {
        super();
    }

    public FailedDbWrite(String message) {
        super(message);
    }

    public FailedDbWrite(String message, Throwable cause) {
        super(message, cause);
    }
}
