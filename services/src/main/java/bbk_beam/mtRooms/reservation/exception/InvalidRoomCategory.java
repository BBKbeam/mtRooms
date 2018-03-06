package bbk_beam.mtRooms.reservation.exception;

public class InvalidRoomCategory extends Exception {
    public InvalidRoomCategory() {
        super();
    }

    public InvalidRoomCategory(String message) {
        super(message);
    }

    public InvalidRoomCategory(String message, Throwable cause) {
        super(message, cause);
    }
}
