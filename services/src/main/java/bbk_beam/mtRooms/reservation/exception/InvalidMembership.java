package bbk_beam.mtRooms.reservation.exception;

public class InvalidMembership extends Exception {
    public InvalidMembership() {
        super();
    }

    public InvalidMembership(String message) {
        super(message);
    }

    public InvalidMembership(String message, Throwable cause) {
        super(message, cause);
    }
}
