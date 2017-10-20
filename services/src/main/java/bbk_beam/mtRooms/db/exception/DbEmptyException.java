package bbk_beam.mtRooms.db.exception;

public class DbEmptyException extends Exception {
    public DbEmptyException() {
        super();
    }

    public DbEmptyException(String message) {
        super(message);
    }

    public DbEmptyException(String message, Throwable cause) {
        super(message, cause);
    }
}
