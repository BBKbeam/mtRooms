package bbk_beam.mtRooms.reservation.processing;

import bbk_beam.mtRooms.reservation.delegate.IPay;
import eadjlib.logger.Logger;

public class PaymentProcessing {
    private final Logger log = Logger.getLoggerInstance(PaymentProcessing.class.getName());
    private IPay db_delegate;

    /**
     * Constructor
     *
     * @param pay_delegate IPay instance
     */
    public PaymentProcessing(IPay pay_delegate) {
        this.db_delegate = pay_delegate;
    }
}
