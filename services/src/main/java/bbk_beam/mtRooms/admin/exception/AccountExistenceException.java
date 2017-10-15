package bbk_beam.mtRooms.admin.exception;

public class AccountExistenceException extends Exception {
    public AccountExistenceException() {
        super();
    }

    public AccountExistenceException(String message) {
        super(message);
    }

    public AccountExistenceException(String message, Throwable cause) {
        super(message, cause);
    }
}
