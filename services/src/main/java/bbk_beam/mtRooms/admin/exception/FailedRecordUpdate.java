package bbk_beam.mtRooms.admin.exception;

public class FailedRecordUpdate extends Exception {
    public FailedRecordUpdate() {
        super();
    }

    public FailedRecordUpdate(String message) {
        super(message);
    }

    public FailedRecordUpdate(String message, Throwable cause) {
        super(message, cause);
    }
}
