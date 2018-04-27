package bbk_beam.mtRooms.exception;

public class RemoteFailure extends Exception {
    public RemoteFailure() {
        super();
    }

    public RemoteFailure(String message) {
        super(message);
    }

    public RemoteFailure(String message, Throwable cause) {
        super(message, cause);
    }
}
