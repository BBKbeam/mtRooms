package bbk_beam.mtRooms.network.session;

import bbk_beam.mtRooms.admin.authentication.Token;
import bbk_beam.mtRooms.admin.exception.AuthenticationFailureException;
import bbk_beam.mtRooms.network.IRmiClient;
import bbk_beam.mtRooms.network.exception.FailedAllocation;
import bbk_beam.mtRooms.network.exception.Unauthorised;
import bbk_beam.mtRooms.reservation.IReservationSession;
import bbk_beam.mtRooms.reservation.ReservationSession;
import bbk_beam.mtRooms.uaa.*;
import bbk_beam.mtRooms.uaa.exception.DuplicateSession;
import bbk_beam.mtRooms.uaa.exception.InvalidAccessRights;
import bbk_beam.mtRooms.uaa.exception.SessionInactive;
import eadjlib.logger.Logger;

import java.io.Serializable;
import java.rmi.RemoteException;

/**
 * ClientWrapper
 * <p>
 * Wrapper class for IRmiClient instances that holds references to authenticated resources
 * </p>
 */
public class ClientWrapper implements Serializable {
    private final Logger log = Logger.getLoggerInstance(ClientWrapper.class.getName());
    private EventWatcherDelegate event_watcher_delegate;
    private IRmiClient client;
    private IAuthenticatedAdministration administration = null;
    private IAuthenticatedFrontDesk front_desk = null;
    private ReservationSession reservation_session;
    private IAuthenticatedLogisticsPersonnel logistics = null;
    private IAuthenticatedRevenuePersonnel revenues = null;

    /**
     * Constructor
     *
     * @param driver SessionDriver instance
     * @param client RmiClient
     * @throws RemoteException when communication failure occured with the client
     * @throws SessionInactive when the SessionDriver has not been initialised
     */
    public ClientWrapper(ISessionDriver driver, IRmiClient client, EventWatcherDelegate watcher_delegate) throws RemoteException, SessionInactive {
        this.client = client;
        this.event_watcher_delegate = watcher_delegate;
        try {
            this.administration = driver.getAdministrationInstance(client.getToken());
        } catch (AuthenticationFailureException e) {
            log.log_Warning("IAuthenticatedAdministration: Client failed authentication for this resource.");
        }
        try {
            this.front_desk = driver.getFrontDeskInstance(client.getToken());
        } catch (AuthenticationFailureException e) {
            log.log_Warning("IAuthenticatedFrontDesk: Client failed authentication for this resource.");
        }
        try {
            this.logistics = driver.getLogisticsPersonnelInstance(client.getToken());
        } catch (AuthenticationFailureException e) {
            log.log_Warning("IAuthenticatedLogisticsPersonnel: Client failed authentication for this resource.");
        }
        try {
            this.revenues = driver.getRevenuePersonnelInstance(client.getToken());
        } catch (AuthenticationFailureException e) {
            log.log_Warning("IAuthenticatedRevenuePersonnel: Client failed authentication for this resource.");
        }
    }

    /**
     * Gets the client's session token
     *
     * @return Session Token
     * @throws RemoteException when network issues occur during the remote call
     */
    public Token getToken() throws RemoteException {
        return this.client.getToken();
    }

    /**
     * Updates the observing client
     *
     * @param observable Provenance of the message
     * @param update     Update message
     * @throws RemoteException when network issues occur during the remote call
     */
    public void update(Object observable, Object update) throws RemoteException {
        this.client.update(observable, update);
    }

    /**
     * Gets access to the client's reservation session
     *
     * @return Client's reservation session
     * @throws Unauthorised     when user has not been authorised to access the resource or resource could not be allocated (see log)
     * @throws FailedAllocation when the reservation session for the client could not be allocated to the client
     * @throws RemoteException  when network issues occur during the remote call
     */
    public IReservationSession getReservationAccess() throws Unauthorised, FailedAllocation, RemoteException {
        if (hasFrontDeskAccess()) {
            if (this.reservation_session == null) {
                try {
                    this.reservation_session = getFrontDeskAccess().openReservationSession(this.client.getToken());
                    this.reservation_session.addEventWatcher(this.event_watcher_delegate);
                } catch (InvalidAccessRights e) {
                    log.log_Warning("ReservationSession: Client [", this.client.getToken(), "] failed authentication for this resource. ");
                } catch (AuthenticationFailureException e) {
                    log.log_Error("IReservationSession: Client [", this.client.getToken(), "] failed authentication for this resource.");
                    throw new Unauthorised("Could not allocate a IReservationSession to client [" + this.client.getToken() + "]", e);
                } catch (DuplicateSession e) {
                    log.log_Fatal("Found a duplicate Token id being used. Should not happen! Please contact the dev. and send full log.");
                    throw new FailedAllocation("Could not allocate a IReservationSession to client [" + this.client.getToken() + "]", e);
                }
            }
            return this.reservation_session;
        } else {
            throw new Unauthorised("Access to IReservationSession resource is not authorised for client.");
        }
    }

    /**
     * Closes the reservation session for the client
     *
     * @throws Unauthorised    when user has not been authorised to access the resource or resource could not be allocated (see log)
     * @throws RemoteException when network issues occur during the remote call
     */
    synchronized public void closeReservationSession() throws Unauthorised, RemoteException {
        if (this.hasFrontDeskAccess())
            this.front_desk.closeReservationSession(this.client.getToken());
        else
            throw new Unauthorised("Access to IReservationSession resource is not authorised for client.");
    }

    /**
     * Gets access to administrative functionality
     *
     * @return IAuthenticatedAdministration instance
     * @throws Unauthorised when user has not been authorised to access the resource
     */
    public IAuthenticatedAdministration getAdministrationAccess() throws Unauthorised {
        if (hasAdministrativeAccess())
            return this.administration;
        else
            throw new Unauthorised("Access to IAuthenticatedAdministration resource is not authorised for client.");
    }

    /**
     * Gets access to front desk (reservations) functionality
     *
     * @return IAuthenticatedFrontDesk instance
     * @throws Unauthorised when user has not been authorised to access the resource
     */
    public IAuthenticatedFrontDesk getFrontDeskAccess() throws Unauthorised {
        if (hasFrontDeskAccess())
            return this.front_desk;
        else
            throw new Unauthorised("Access to IAuthenticatedFrontDesk resource is not authorised for client.");
    }

    /**
     * Gets access to logistical functionality
     *
     * @return IAuthenticatedLogisticsPersonnel instance
     * @throws Unauthorised when user has not been authorised to access the resource
     */
    public IAuthenticatedLogisticsPersonnel getLogisticsAccess() throws Unauthorised {
        if (hasLogisiticsAccess())
            return this.logistics;
        else
            throw new Unauthorised("Access to IAuthenticatedLogisticsPersonnel resource is not authorised for client.");
    }

    /**
     * Gets access to revenue functionality
     *
     * @return IAuthenticatedRevenuePersonnel instance
     * @throws Unauthorised when user has not been authorised to access the resource
     */
    public IAuthenticatedRevenuePersonnel getRevenuesAccess() throws Unauthorised {
        if (hasRevenueAccess())
            return this.revenues;
        else
            throw new Unauthorised("Access to IAuthenticatedRevenuePersonnel resource is not authorised for client.");
    }

    /**
     * Check access rights to administration
     *
     * @return Access rights
     */
    public boolean hasAdministrativeAccess() {
        return this.administration != null;
    }

    /**
     * Check access rights to front desk
     *
     * @return Access rights
     */
    public boolean hasFrontDeskAccess() {
        return this.front_desk != null;
    }

    /**
     * Check access rights to logistics
     *
     * @return Access rights
     */
    public boolean hasLogisiticsAccess() {
        return this.logistics != null;
    }

    /**
     * Check access rights to revenue
     *
     * @return Access rights
     */
    public boolean hasRevenueAccess() {
        return this.revenues != null;
    }
}
