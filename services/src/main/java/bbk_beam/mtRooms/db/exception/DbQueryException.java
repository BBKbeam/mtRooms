package bbk_beam.mtRooms.db.exception;

public class DbQueryException extends Exception {
    public DbQueryException() {
        super();
    }

    public DbQueryException(String message) {
        super(message);
    }

    public DbQueryException(String message, Throwable cause) {
        super(message, cause);
    }
}
