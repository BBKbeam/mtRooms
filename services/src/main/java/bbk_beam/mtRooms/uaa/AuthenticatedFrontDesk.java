package bbk_beam.mtRooms.uaa;

import bbk_beam.mtRooms.ServiceDriver;
import bbk_beam.mtRooms.admin.authentication.Token;
import bbk_beam.mtRooms.admin.exception.AuthenticationFailureException;
import bbk_beam.mtRooms.db.exception.SessionInvalidException;
import bbk_beam.mtRooms.reservation.ReservationSession;
import bbk_beam.mtRooms.uaa.exception.DuplicateSession;

public class AuthenticatedFrontDesk implements IAuthenticatedFrontDesk {
    private FrontDeskDelegate delegate;
    private Authenticator authenticator;

    public AuthenticatedFrontDesk(ServiceDriver driver) {
        this.authenticator = new Authenticator(driver);
        this.delegate = new FrontDeskDelegate(driver);
    }

    @Override
    public ReservationSession login(String username, String password) throws AuthenticationFailureException, DuplicateSession {
        Token token = this.authenticator.login(username, password);
        try {
            return this.delegate.getSession(token);
        } catch (InstantiationException e) {
            throw new DuplicateSession("Session with Token '" + token + "' already exists.", e);
        }
    }

    @Override
    public void logout(ReservationSession reservation_session) throws SessionInvalidException {
        this.authenticator.logout(reservation_session.getToken());
        this.delegate.removeSession(reservation_session);
    }
}
