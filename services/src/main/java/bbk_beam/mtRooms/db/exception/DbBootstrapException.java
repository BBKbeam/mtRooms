package bbk_beam.mtRooms.db.exception;

public class DbBootstrapException extends Exception {

    public DbBootstrapException() {
        super();
    }

    public DbBootstrapException(String message) {
        super(message);
    }

    public DbBootstrapException(String message, Throwable cause) {
        super(message, cause);
    }
}
