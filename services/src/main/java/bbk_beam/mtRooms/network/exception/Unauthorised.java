package bbk_beam.mtRooms.network.exception;

public class Unauthorised extends Exception {
    public Unauthorised() {
        super();
    }

    public Unauthorised(String message) {
        super(message);
    }

    public Unauthorised(String message, Throwable cause) {
        super(message, cause);
    }
}
