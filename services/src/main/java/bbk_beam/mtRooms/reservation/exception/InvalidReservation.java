package bbk_beam.mtRooms.reservation.exception;

public class InvalidReservation extends Exception {
    public InvalidReservation() {
        super();
    }

    public InvalidReservation(String message) {
        super(message);
    }

    public InvalidReservation(String message, Throwable cause) {
        super(message, cause);
    }
}
