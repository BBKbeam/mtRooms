package bbk_beam.mtRooms.admin.exception;

public class AuthenticationHasherException extends Exception {
    public AuthenticationHasherException() {
        super();
    }

    public AuthenticationHasherException(String message) {
        super(message);
    }

    public AuthenticationHasherException(String message, Throwable cause) {
        super(message, cause);
    }
}
