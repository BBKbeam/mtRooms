package bbk_beam.mtRooms.admin.exception;

public class AuthenticationFailureException extends Exception {

    public AuthenticationFailureException() {
        super();
    }

    public AuthenticationFailureException(String message) {
        super(message);
    }

    public AuthenticationFailureException(String message, Throwable cause) {
        super(message, cause);
    }
}
