package bbk_beam.mtRooms.db.exception;

public class DBQueryException extends Exception {
    public DBQueryException() {
        super();
    }

    public DBQueryException(String message) {
        super(message);
    }

    public DBQueryException(String message, Throwable cause) {
        super(message, cause);
    }
}
