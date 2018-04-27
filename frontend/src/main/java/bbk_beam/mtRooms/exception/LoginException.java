package bbk_beam.mtRooms.exception;

public class LoginException extends Exception {
    public LoginException() {
        super();
    }

    public LoginException(String message) {
        super(message);
    }

    public LoginException(String message, Throwable cause) {
        super(message, cause);
    }
}
