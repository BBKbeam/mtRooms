package bbk_beam.mtRooms.network.exception;

public class FailedAllocation extends Exception {
    public FailedAllocation() {
        super();
    }

    public FailedAllocation(String message) {
        super(message);
    }

    public FailedAllocation(String message, Throwable cause) {
        super(message, cause);
    }
}
