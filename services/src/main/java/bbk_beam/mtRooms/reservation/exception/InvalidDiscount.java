package bbk_beam.mtRooms.reservation.exception;

public class InvalidDiscount extends Exception {
    public InvalidDiscount() {
        super();
    }

    public InvalidDiscount(String message) {
        super(message);
    }

    public InvalidDiscount(String message, Throwable cause) {
        super(message, cause);
    }
}
