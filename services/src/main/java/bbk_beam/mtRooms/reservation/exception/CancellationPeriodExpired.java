package bbk_beam.mtRooms.reservation.exception;

public class CancellationPeriodExpired extends Exception {
    public CancellationPeriodExpired() {
        super();
    }

    public CancellationPeriodExpired(String message) {
        super(message);
    }

    public CancellationPeriodExpired(String message, Throwable cause) {
        super(message, cause);
    }
}
