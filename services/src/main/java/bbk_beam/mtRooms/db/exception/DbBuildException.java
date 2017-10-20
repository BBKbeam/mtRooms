package bbk_beam.mtRooms.db.exception;

public class DbBuildException extends Exception {
    public DbBuildException() {
        super();
    }

    public DbBuildException(String message) {
        super(message);
    }

    public DbBuildException(String message, Throwable cause) {
        super(message, cause);
    }
}
