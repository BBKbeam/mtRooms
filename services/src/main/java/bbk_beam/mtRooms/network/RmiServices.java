package bbk_beam.mtRooms.network;

import bbk_beam.mtRooms.admin.authentication.Token;
import bbk_beam.mtRooms.admin.exception.AuthenticationFailureException;
import bbk_beam.mtRooms.db.exception.SessionInvalidException;
import bbk_beam.mtRooms.network.exception.DuplicateClient;
import bbk_beam.mtRooms.network.exception.FailedServerInit;
import bbk_beam.mtRooms.network.session.ClientSessions;
import bbk_beam.mtRooms.network.session.ClientWrapper;
import bbk_beam.mtRooms.network.session.EventWatcherDelegate;
import bbk_beam.mtRooms.uaa.ISessionDriver;
import bbk_beam.mtRooms.uaa.SessionDriver;
import bbk_beam.mtRooms.uaa.exception.FailedSessionSpooling;
import bbk_beam.mtRooms.uaa.exception.ServerSessionInactive;
import bbk_beam.mtRooms.uaa.exception.SessionActive;
import bbk_beam.mtRooms.uaa.exception.SessionReset;
import eadjlib.logger.Logger;

import java.rmi.RemoteException;

public class RmiServices implements IRmiServices {
    private final Logger log = Logger.getLoggerInstance(RmiServices.class.getName());
    static protected ISessionDriver driver = new SessionDriver();
    static protected ClientSessions sessions;
    static protected EventWatcherDelegate event_watcher_delegate;

    /**
     * Constructor
     */
    RmiServices() throws FailedServerInit {
        try {
            new RmiServices("mtRooms.db");
        } catch (FailedSessionSpooling e) {
            log.log_Fatal("Could not spool 'mtRooms.db' session for server.");
            throw new FailedServerInit("Could not spool 'mtRooms.db' session for server.", e);
        } catch (SessionActive e) {
            log.log_Error("Could not spool new 'mtRooms.db' session over active session.");
            throw new FailedServerInit("Could not spool new 'mtRooms.db' session over active session.", e);
        }
    }

    /**
     * Constructor
     *
     * @param db_file_name DB file name
     * @throws FailedSessionSpooling when failure occurred during component instantiation
     * @throws SessionActive         when the server components have already been spooled and are active
     */
    RmiServices(String db_file_name) throws FailedSessionSpooling, SessionActive {
        if (!driver.isInstantiated()) {
            driver.init(db_file_name);
            sessions = new ClientSessions();
            event_watcher_delegate = new EventWatcherDelegate(sessions);
        } else {
            try {
                log.log_Error("RmiServices driver is already instantiated with DB '", driver.currentDB(), "'.");
            } catch (SessionReset e) {
                log.log_Error("(!) Session driver indicated as instantiated without a connected DB.");
                throw new SessionActive("(!) Session driver indicated as instantiated without a connected DB.", e);
            }
        }
    }

    @Override
    public synchronized Token login(IRmiClient client, String username, String password) throws AuthenticationFailureException, ServerSessionInactive, RemoteException {
        Token token = driver.login(username, password);
        try {
            client.setToken(token);
            sessions.addClient(new ClientWrapper(driver, client, event_watcher_delegate));
        } catch (DuplicateClient duplicateClient) {
            log.log_Warning("Client [", token, "] is already registered...");
        }
        return token;
    }

    @Override
    public synchronized void logout(Token session_token) throws SessionInvalidException, ServerSessionInactive, RemoteException {
        sessions.removeClient(session_token);
        driver.logout(session_token);
    }

    @Override
    public synchronized boolean hasAdministrativeAccess(Token token) throws RemoteException {
        try {
            ClientWrapper client = sessions.getClient(token);
            return client.hasAdministrativeAccess();
        } catch (IllegalArgumentException e) {
            log.log_Error("Client [", token, "] checked administrative access rights but isn't tracked in the ClientSessions.");
            return false;
        }
    }

    @Override
    public synchronized boolean hasFrontDeskAccess(Token token) throws RemoteException {
        try {
            ClientWrapper client = sessions.getClient(token);
            return client.hasFrontDeskAccess();
        } catch (IllegalArgumentException e) {
            log.log_Error("Client [", token, "] checked front desk access rights but isn't tracked in the ClientSessions.");
            return false;
        }
    }

    @Override
    public synchronized boolean hasLogisticsAccess(Token token) throws RemoteException {
        try {
            ClientWrapper client = sessions.getClient(token);
            return client.hasLogisiticsAccess();
        } catch (IllegalArgumentException e) {
            log.log_Error("Client [", token, "] checked logistics access rights but isn't tracked in the ClientSessions.");
            return false;
        }
    }

    @Override
    public synchronized boolean hasRevenueAccess(Token token) throws RemoteException {
        try {
            ClientWrapper client = sessions.getClient(token);
            return client.hasRevenueAccess();
        } catch (IllegalArgumentException e) {
            log.log_Error("Client [", token, "] checked revenue access rights but isn't tracked in the ClientSessions.");
            return false;
        }
    }
}
