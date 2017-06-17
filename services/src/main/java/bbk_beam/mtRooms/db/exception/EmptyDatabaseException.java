package bbk_beam.mtRooms.db.exception;

public class EmptyDatabaseException extends Exception {
    public EmptyDatabaseException() {
        super();
    }

    public EmptyDatabaseException(String message) {
        super(message);
    }

    public EmptyDatabaseException(String message, Throwable cause) {
        super(message, cause);
    }
}
