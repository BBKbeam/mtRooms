package bbk_beam.mtRooms.uaa.exception;

public class ServerSessionInactive extends Exception {
    public ServerSessionInactive() {
        super();
    }

    public ServerSessionInactive(String message) {
        super(message);
    }

    public ServerSessionInactive(String message, Throwable cause) {
        super(message, cause);
    }
}
