package bbk_beam.mtRooms.admin.exception;

public class IncompleteRecord extends Exception {
    public IncompleteRecord() {
        super();
    }

    public IncompleteRecord(String message) {
        super(message);
    }

    public IncompleteRecord(String message, Throwable cause) {
        super(message, cause);
    }
}
