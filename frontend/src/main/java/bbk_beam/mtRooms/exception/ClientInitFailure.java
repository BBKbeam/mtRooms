package bbk_beam.mtRooms.exception;

public class ClientInitFailure extends Exception {
    public ClientInitFailure() {
        super();
    }

    public ClientInitFailure(String message) {
        super(message);
    }

    public ClientInitFailure(String message, Throwable cause) {
        super(message, cause);
    }
}
