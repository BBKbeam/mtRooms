package bbk_beam.mtRooms.admin.exception;

public class FailedRecordWrite extends Exception {
    public FailedRecordWrite() {
        super();
    }

    public FailedRecordWrite(String message) {
        super(message);
    }

    public FailedRecordWrite(String message, Throwable cause) {
        super(message, cause);
    }
}
