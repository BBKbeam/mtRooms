package bbk_beam.mtRooms.db.exception;

public class DbMissingTableException extends Exception {
    public DbMissingTableException() {
        super();
    }

    public DbMissingTableException(String message) {
        super(message);
    }

    public DbMissingTableException(String message, Throwable cause) {
        super(message, cause);
    }
}
