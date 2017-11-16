package bbk_beam.mtRooms.reservation.exception;

public class InvalidCustomer extends Exception {
    public InvalidCustomer() {
        super();
    }

    public InvalidCustomer(String message) {
        super(message);
    }

    public InvalidCustomer(String message, Throwable cause) {
        super(message, cause);
    }
}
