package bbk_beam.mtRooms.network.exception;

public class DuplicateClient extends Exception {
    public DuplicateClient() {
        super();
    }

    public DuplicateClient(String message) {
        super(message);
    }

    public DuplicateClient(String message, Throwable cause) {
        super(message, cause);
    }
}
