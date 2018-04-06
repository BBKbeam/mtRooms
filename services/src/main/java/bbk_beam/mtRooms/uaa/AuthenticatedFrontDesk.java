package bbk_beam.mtRooms.uaa;

import bbk_beam.mtRooms.ServiceDriver;
import bbk_beam.mtRooms.admin.authentication.Token;
import bbk_beam.mtRooms.admin.exception.AuthenticationFailureException;
import bbk_beam.mtRooms.db.exception.SessionInvalidException;
import bbk_beam.mtRooms.db.session.SessionType;
import bbk_beam.mtRooms.reservation.ReservationSession;
import bbk_beam.mtRooms.uaa.exception.DuplicateSession;
import bbk_beam.mtRooms.uaa.exception.InvalidAccessRights;

public class AuthenticatedFrontDesk implements IAuthenticatedFrontDesk {
    private FrontDeskDelegate delegate;
    private Authenticator authenticator;

    /**
     * Constructor
     *
     * @param driver ServiceDriver instance
     */
    public AuthenticatedFrontDesk(ServiceDriver driver) {
        this.authenticator = new Authenticator(driver);
        this.delegate = new FrontDeskDelegate(driver);
    }

    @Override
    public ReservationSession getReservationSession(Token token) throws InvalidAccessRights, AuthenticationFailureException, DuplicateSession {
        if (!this.authenticator.isLoggedIn(token))
            throw new AuthenticationFailureException(""); //TODO
        if (!this.authenticator.hasValidAccessRights(token, SessionType.USER))
            throw new InvalidAccessRights(""); //TODO
        try {
            return this.delegate.getSession(token);
        } catch (InstantiationException e) {
            throw new DuplicateSession("", e); //TODO
        }
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
