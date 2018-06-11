package bbk_beam.mtRooms.reservation.exception;

public class InvalidRoom extends Exception {
    public InvalidRoom() {
        super();
    }

    public InvalidRoom(String message) {
        super(message);
    }

    public InvalidRoom(String message, Throwable cause) {
        super(message, cause);
    }
}
