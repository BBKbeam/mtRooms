package bbk_beam.mtRooms.reservation.processing;

import bbk_beam.mtRooms.admin.authentication.Token;
import bbk_beam.mtRooms.db.TimestampConverter;
import bbk_beam.mtRooms.db.exception.DbQueryException;
import bbk_beam.mtRooms.db.exception.SessionExpiredException;
import bbk_beam.mtRooms.db.exception.SessionInvalidException;
import bbk_beam.mtRooms.reservation.delegate.IPay;
import bbk_beam.mtRooms.reservation.dto.Payment;
import bbk_beam.mtRooms.reservation.dto.PaymentMethod;
import bbk_beam.mtRooms.reservation.dto.Reservation;
import bbk_beam.mtRooms.reservation.exception.FailedDbFetch;
import bbk_beam.mtRooms.reservation.exception.FailedDbWrite;
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
     * Stores a reservation's payment
     *
     * @param session_token Session's token
     * @param reservation   Reservation subject to payment
     * @param payment       Amount payed
     * @return New balance post-discount on the reservation
     * @throws FailedDbWrite           when a problem was encountered whilst processing the query
     * @throws FailedDbFetch           when a problem was encountered whilst processing the query
     * @throws SessionExpiredException when the session for the id provided has expired
     * @throws SessionInvalidException when the session for the id provided does not exist in the tracker
     */
    public Double pay(Token session_token, Reservation reservation, Payment payment) throws FailedDbWrite, FailedDbFetch, SessionExpiredException, SessionInvalidException {
        try {
            log.log_Trace("Reservation [", reservation.id(), "]: attempting to pay with ", payment);
            this.db_delegate.pay(session_token, reservation, payment);
            ObjectTable table = this.db_delegate.getFinancialSummary(session_token, reservation);
            if (table.isEmpty()) {
                log.log_Error("Could not get financial summary for Reservation [", reservation.id(), "].");
                throw new FailedDbFetch("Could not get financial summary for Reservation [" + reservation.id() + "].");
            }
            HashMap<String, Object> row = table.getRow(1);
            Double room_total = (Double) row.get("confirmed_subtotal");
            Double payed_total = (Double) row.get("payment_total");
            Double discount = (Double) row.get("discount_rate");
            log.log_Debug("Reservation [", reservation.id(), "]: confirmed rooms subtotal = ", room_total, ", paid = ", payed_total, ", discount rate = ", discount, ".");
            Double sub_total = room_total * (100 - discount) / 100;
            Double total = sub_total - payed_total;
            log.log_Debug("Reservation [", reservation.id(), "]: left to pay = ", total);
            return total;
        } catch (DbQueryException e) {
            log.log_Error("Could not complete adding Payment to records: ", payment);
            throw new FailedDbWrite("Could not complete adding Payment to records: " + payment, e);
        }
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
                                (String) row.get("hash_id"),
                                (Double) row.get("amount"),
                                TimestampConverter.getDateObject((String) row.get("timestamp")),
                                (String) row.get("notes"),
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
     * Get all available payment methods
     *
     * @param session_token Session's token
     * @return List of all available payment methods from records
     * @throws FailedDbFetch           when a problem was encountered whilst processing the query
     * @throws SessionExpiredException when the session for the id provided has expired
     * @throws SessionInvalidException when the session for the id provided does not exist in the tracker
     */
    public List<PaymentMethod> getPaymentMethods(Token session_token) throws FailedDbFetch, SessionExpiredException, SessionInvalidException {
        try {
            ObjectTable table = db_delegate.getPaymentMethods(session_token);
            if (table.isEmpty()) {
                log.log_Error("Fetching of payment types unsuccessful: No records found.");
                throw new FailedDbFetch("Fetching of payment types unsuccessful: No records found.");
            }
            List<PaymentMethod> paymentMethods = new ArrayList<>();
            for (int i = 1; i <= table.rowCount(); i++) {
                HashMap<String, Object> row = table.getRow(i);
                paymentMethods.add(new PaymentMethod((Integer) row.get("id"), (String) row.get("description")));
            }
            return paymentMethods;
        } catch (DbQueryException e) {
            log.log_Error("Fetching of payment types unsuccessful: SQL Query issue.", e);
            throw new FailedDbFetch("Fetching of payment types unsuccessful: SQL Query issue.", e);
        }
    }
}
