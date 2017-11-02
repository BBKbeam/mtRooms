package bbk_beam.mtRooms.db.exception;

public class SessionCorruptedException extends Exception {
    public SessionCorruptedException() {
        super();
    }

    public SessionCorruptedException(String message) {
        super(message);
    }

    public SessionCorruptedException(String message, Throwable cause) {
        super(message, cause);
    }
}
