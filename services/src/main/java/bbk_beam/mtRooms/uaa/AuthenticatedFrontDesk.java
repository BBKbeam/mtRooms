package bbk_beam.mtRooms.uaa;

import bbk_beam.mtRooms.admin.authentication.Token;
import bbk_beam.mtRooms.admin.exception.AuthenticationFailureException;
import bbk_beam.mtRooms.db.exception.SessionInvalidException;
import bbk_beam.mtRooms.db.session.SessionType;
import bbk_beam.mtRooms.reservation.ReservationSession;
import bbk_beam.mtRooms.uaa.exception.DuplicateSession;
import bbk_beam.mtRooms.uaa.exception.InvalidAccessRights;
import eadjlib.logger.Logger;

public class AuthenticatedFrontDesk implements IAuthenticatedFrontDesk {
    private final Logger log = Logger.getLoggerInstance(AuthenticatedFrontDesk.class.getName());
    private FrontDeskDelegate delegate;
    private Authenticator authenticator;

    /**
     * Constructor
     *
     * @param delegate      FrontDeskDelegate instance
     * @param authenticator Authenticator instance
     */
    public AuthenticatedFrontDesk(FrontDeskDelegate delegate, Authenticator authenticator) {
        this.authenticator = authenticator;
        this.delegate = delegate;
    }

    @Override
    public ReservationSession openReservationSession(Token token) throws InvalidAccessRights, AuthenticationFailureException, DuplicateSession {
        if (!this.authenticator.isLoggedIn(token)) {
            log.log_Error("Token [", token, "] is not currently logged in.");
            throw new AuthenticationFailureException("Token [" + token + "] is not currently logged in.");
        }
        if (!this.authenticator.hasValidAccessRights(token, SessionType.USER)) {
            log.log_Error("Token [", token, "] has not got required access rights to resource.");
            throw new InvalidAccessRights("Token [" + token + "] has not got required access rights to resource.");
        }
        try {
            return this.delegate.getSession(token);
        } catch (InstantiationException e) {
            log.log_Fatal("Token [", token, "] is a duplicate.");
            log.log_Exception(e);
            throw new DuplicateSession("Token [" + token + "] is a duplicate.", e);
        }
    }

    @Override
    public void closeReservationSession(ReservationSession reservation_session) throws SessionInvalidException {
        this.delegate.removeSession(reservation_session);
    }
}
