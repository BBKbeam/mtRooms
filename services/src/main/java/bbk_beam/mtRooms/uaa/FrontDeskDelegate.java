package bbk_beam.mtRooms.uaa;

import bbk_beam.mtRooms.ServiceDriver;
import bbk_beam.mtRooms.admin.authentication.Token;
import bbk_beam.mtRooms.reservation.ReservationSession;

public class FrontDeskDelegate {
    private ServiceDriver driver;

    /**
     * Cosntructor
     *
     * @param driver Service layer driver
     */
    FrontDeskDelegate(ServiceDriver driver) {
        this.driver = driver;
    }

    /**
     * Gets a reservation session instance for the given token
     *
     * @param token Session token
     * @return ReservationSession for the token
     * @throws InstantiationException when token is already currently used with a another different ReservationSession
     */
    ReservationSession getSession(Token token) throws InstantiationException {
        return this.driver.createNewReservationSession(token);
    }

    /**
     * Terminates a ReservationSession
     *
     * @param reservation_session ReservationSession to close
     */
    void removeSession(ReservationSession reservation_session) {
        this.driver.removeReservationSession(reservation_session);
    }
}
