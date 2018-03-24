package bbk_beam.mtRooms.reservation;

import bbk_beam.mtRooms.admin.authentication.Token;
import bbk_beam.mtRooms.reservation.processing.CustomerAccountAccess;
import bbk_beam.mtRooms.reservation.processing.OptimisedSearch;
import bbk_beam.mtRooms.reservation.processing.PaymentProcessing;
import bbk_beam.mtRooms.reservation.processing.ReservationProcessing;

import java.util.Observable;

public class ReservationSession implements IReservationSession, IReservationPayment {
    private Token token;
    private CustomerAccountAccess customerAccountAccess;
    private PaymentProcessing paymentProcessing;
    private ReservationProcessing reservationProcessing;
    private OptimisedSearch optimisedSearch;


    //@construction instance should be added to the observers in ScheduleCache
    //@destruction instance should be removed from ScheduleCache

    /**
     * Constructor
     *
     * @param token                 User's session token
     * @param customerAccountAccess CustomerAccountAccess instance
     * @param paymentProcessing     PaymentProcessing instance
     * @param reservationProcessing ReservationProcessing instance
     * @param optimisedSearch       OptimisedSearch instance
     */
    public ReservationSession(Token token,
                              CustomerAccountAccess customerAccountAccess,
                              PaymentProcessing paymentProcessing,
                              ReservationProcessing reservationProcessing,
                              OptimisedSearch optimisedSearch) {
        this.token = token;
        this.customerAccountAccess = customerAccountAccess;
        this.paymentProcessing = paymentProcessing;
        this.reservationProcessing = reservationProcessing;
        this.optimisedSearch = optimisedSearch;
    }

    /**
     * Gets the User's reservation session token
     *
     * @return Session token
     */
    public Token getToken() {
        return token;
    }

    @Override
    public void update(Observable o, Object arg) {

    }
}
