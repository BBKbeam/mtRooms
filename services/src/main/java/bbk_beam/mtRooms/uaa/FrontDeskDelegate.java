package bbk_beam.mtRooms.uaa;

import bbk_beam.mtRooms.admin.authentication.Token;
import bbk_beam.mtRooms.reservation.ReservationSession;
import bbk_beam.mtRooms.reservation.processing.CustomerAccountAccess;
import bbk_beam.mtRooms.reservation.processing.OptimisedSearch;
import bbk_beam.mtRooms.reservation.processing.PaymentProcessing;
import bbk_beam.mtRooms.reservation.processing.ReservationProcessing;
import bbk_beam.mtRooms.reservation.scheduling.ScheduleCache;
import eadjlib.logger.Logger;

public class FrontDeskDelegate {
    private final Logger log = Logger.getLoggerInstance(FrontDeskDelegate.class.getName());
    private OptimisedSearch optimisedSearch;
    private ReservationProcessing reservationProcessing;
    private PaymentProcessing paymentProcessing;
    private CustomerAccountAccess customerAccountAccess;
    private ScheduleCache scheduleCache;

    /**
     * Constructor
     *
     * @param schedule_cache        ScheduleCache instance
     * @param optimisedSearch       OptimisedSearch instance
     * @param reservationProcessing ReservationProcessing instance
     * @param paymentProcessing     PaymentProcessing instance
     * @param customerAccountAccess CustomerAccountAccess instance
     */
    FrontDeskDelegate(
            ScheduleCache schedule_cache,
            OptimisedSearch optimisedSearch,
            ReservationProcessing reservationProcessing,
            PaymentProcessing paymentProcessing,
            CustomerAccountAccess customerAccountAccess) {
        this.scheduleCache = schedule_cache;
        this.optimisedSearch = optimisedSearch;
        this.reservationProcessing = reservationProcessing;
        this.paymentProcessing = paymentProcessing;
        this.customerAccountAccess = customerAccountAccess;
    }

    /**
     * Creates a new ReservationSession instance for a session
     *
     * @param session_token Session token
     * @return ReservationSession instance for the token given
     * @throws InstantiationException when token is already currently used with a another different ReservationSession
     */
    public ReservationSession getSession(Token session_token) throws InstantiationException {
        ReservationSession session = this.scheduleCache.getReservationSession(session_token);
        if (session != null) {
            log.log("Current ReservationSession requested. Target token is '", session_token, "'.");
        } else {
            log.log("New ReservationSession requested. Target token is '", session_token, "'.");
            session = new ReservationSession(
                    session_token,
                    this.customerAccountAccess,
                    this.paymentProcessing,
                    this.reservationProcessing,
                    this.optimisedSearch
            );

            if (!this.scheduleCache.exists(session)) {
                this.scheduleCache.addObserver(session);
            } else {
                log.log_Error("Tried to create a reservation session with a currently used Token [", session_token, "].");
                throw new InstantiationException("Tried to create a reservation session with a currently used Token [" + session_token + "].");
            }
        }
        return session;
    }

    /**
     * Delete <<Observer>> ReservationSession instance from the <<Observable>> ScheduleCache
     *
     * @param reservation_session ReservationSession instance
     */
    public void removeSession(ReservationSession reservation_session) {
        log.log("Deletion of associated ReservationSession observer requested. Token is '", reservation_session.getToken(), "'.");
        this.scheduleCache.deleteObserver(reservation_session);
    }
}
