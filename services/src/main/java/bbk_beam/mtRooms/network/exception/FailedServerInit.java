package bbk_beam.mtRooms.network.exception;

public class FailedServerInit extends Exception {
    public FailedServerInit() {
        super();
    }

    public FailedServerInit(String message) {
        super(message);
    }

    public FailedServerInit(String message, Throwable cause) {
        super(message, cause);
    }
}
