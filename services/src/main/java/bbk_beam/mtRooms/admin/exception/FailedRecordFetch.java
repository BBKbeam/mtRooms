package bbk_beam.mtRooms.admin.exception;

public class FailedRecordFetch extends Exception {
    public FailedRecordFetch() {
        super();
    }

    public FailedRecordFetch(String message) {
        super(message);
    }

    public FailedRecordFetch(String message, Throwable cause) {
        super(message, cause);
    }
}
