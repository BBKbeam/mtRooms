package bbk_beam.mtRooms.reservation.processing;

import bbk_beam.mtRooms.admin.authentication.Token;
import bbk_beam.mtRooms.db.TimestampConverter;
import bbk_beam.mtRooms.db.exception.DbQueryException;
import bbk_beam.mtRooms.db.exception.SessionExpiredException;
import bbk_beam.mtRooms.db.exception.SessionInvalidException;
import bbk_beam.mtRooms.reservation.delegate.IPay;
import bbk_beam.mtRooms.reservation.dto.Payment;
import bbk_beam.mtRooms.reservation.dto.PaymentMethod;
import bbk_beam.mtRooms.reservation.dto.PaymentType;
import bbk_beam.mtRooms.reservation.dto.Reservation;
import bbk_beam.mtRooms.reservation.exception.FailedDbFetch;
import eadjlib.datastructure.ObjectTable;
import eadjlib.logger.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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

    /**
     * Gets payments on a reservation from the records
     *
     * @param session_token Session's token
     * @param reservation   Reservation ID
     * @return List of payments for reservation
     * @throws FailedDbFetch           when a problem was encountered whilst processing the query
     * @throws SessionExpiredException when the session for the id provided has expired
     * @throws SessionInvalidException when the session for the id provided does not exist in the tracker
     */
    public List<Payment> getPayments(Token session_token, Reservation reservation) throws FailedDbFetch, SessionExpiredException, SessionInvalidException {
        try {
            ObjectTable table = db_delegate.getPayments(session_token, reservation);
            List<Payment> payments = new ArrayList<>();
            for (int i = 1; i <= table.rowCount(); i++) {
                HashMap<String, Object> row = table.getRow(i);
                payments.add(
                        new Payment(
                                (Integer) row.get("id"),
                                (Integer) row.get("amount"),
                                TimestampConverter.getDateObject((String) row.get("timestamp")),
                                new PaymentMethod(
                                        (Integer) row.get("method_id"),
                                        (String) row.get("method_description")
                                )
                        )
                );
            }
            return payments;
        } catch (DbQueryException e) {
            log.log_Error("Fetching of Reservation [", reservation.id(), "] payment details unsuccessful: SQL Query issue.", e);
            throw new FailedDbFetch("Fetching of Reservation [" + reservation.id() + "] payment details unsuccessful: SQL Query issue.", e);
        }
    }

    /**
     * Get all available payment types
     *
     * @param session_token Session's token
     * @return List of all available payment types in records
     * @throws FailedDbFetch           when a problem was encountered whilst processing the query
     * @throws SessionExpiredException when the session for the id provided has expired
     * @throws SessionInvalidException when the session for the id provided does not exist in the tracker
     */
    public List<PaymentType> getPaymentTypes(Token session_token) throws FailedDbFetch, SessionExpiredException, SessionInvalidException {
        try {
            ObjectTable table = db_delegate.getPaymentTypes(session_token);
            if (table.isEmpty()) {
                log.log_Error("Fetching of payment types unsuccessful: No records found.");
                throw new FailedDbFetch("Fetching of payment types unsuccessful: No records found.");
            }
            List<PaymentType> paymentTypes = new ArrayList<>();
            for (int i = 1; i <= table.rowCount(); i++) {
                HashMap<String, Object> row = table.getRow(i);
                paymentTypes.add(new PaymentType((Integer) row.get("id"), (String) row.get("description")));
            }
            return paymentTypes;
        } catch (DbQueryException e) {
            log.log_Error("Fetching of payment types unsuccessful: SQL Query issue.", e);
            throw new FailedDbFetch("Fetching of payment types unsuccessful: SQL Query issue.", e);
        }
    }
}
