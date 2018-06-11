package bbk_beam.mtRooms.network;

import bbk_beam.mtRooms.admin.authentication.Token;
import bbk_beam.mtRooms.admin.dto.Account;
import bbk_beam.mtRooms.admin.dto.AccountType;
import bbk_beam.mtRooms.admin.exception.AccountExistenceException;
import bbk_beam.mtRooms.admin.exception.AccountOverrideException;
import bbk_beam.mtRooms.admin.exception.AuthenticationFailureException;
import bbk_beam.mtRooms.db.exception.DbQueryException;
import bbk_beam.mtRooms.db.exception.SessionCorruptedException;
import bbk_beam.mtRooms.db.exception.SessionExpiredException;
import bbk_beam.mtRooms.db.exception.SessionInvalidException;
import bbk_beam.mtRooms.db.session.SessionType;
import bbk_beam.mtRooms.network.exception.DuplicateClient;
import bbk_beam.mtRooms.network.exception.FailedAllocation;
import bbk_beam.mtRooms.network.exception.FailedServerInit;
import bbk_beam.mtRooms.network.exception.Unauthorised;
import bbk_beam.mtRooms.network.session.ClientSessions;
import bbk_beam.mtRooms.network.session.ClientWrapper;
import bbk_beam.mtRooms.network.session.EventWatcherDelegate;
import bbk_beam.mtRooms.operation.dto.LogisticsInfo;
import bbk_beam.mtRooms.reservation.dto.*;
import bbk_beam.mtRooms.reservation.exception.*;
import bbk_beam.mtRooms.reservation.scheduling.datastructure.TimeSpan;
import bbk_beam.mtRooms.revenue.exception.InvalidPeriodException;
import bbk_beam.mtRooms.uaa.ISessionDriver;
import bbk_beam.mtRooms.uaa.SessionDriver;
import bbk_beam.mtRooms.uaa.exception.FailedSessionSpooling;
import bbk_beam.mtRooms.uaa.exception.ServerSessionInactive;
import bbk_beam.mtRooms.uaa.exception.SessionActive;
import bbk_beam.mtRooms.uaa.exception.SessionReset;
import eadjlib.logger.Logger;
import javafx.util.Pair;

import java.rmi.RemoteException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

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

    @Override
    public synchronized void createNewAccount(Token admin_token, SessionType account_type, String username, String password) throws Unauthorised, AccountExistenceException, RuntimeException, RemoteException {
        try {
            ClientWrapper client = sessions.getClient(admin_token);
            if (client.hasAdministrativeAccess())
                client.getAdministrationAccess().createNewAccount(admin_token, account_type, username, password);
        } catch (IllegalArgumentException e) {
            log.log_Error("Client [", admin_token, "] is not tracked in ClientSessions.");
            throw new Unauthorised("Client [" + admin_token + "] is not tracked in ClientSessions.", e);
        } catch (SessionExpiredException e) {
            log.log_Error("Client [", admin_token, "] token has expired..");
            throw new Unauthorised("Client [" + admin_token + "] token has expired.", e);
        } catch (SessionInvalidException e) {
            log.log_Error("Client [", admin_token, "] is invalid.");
            throw new Unauthorised("Client [" + admin_token + "] is invalid.", e);
        } catch (SessionCorruptedException e) {
            log.log_Error("Client [", admin_token, "] is using a corrupted Token.");
            throw new Unauthorised("Client [" + admin_token + "] is using a corrupted Token.", e);
        }
    }

    @Override
    public synchronized void updateAccountPassword(Token admin_token, Integer account_id, String password) throws Unauthorised, AccountExistenceException, AccountOverrideException, RuntimeException, RemoteException {
        try {
            ClientWrapper client = sessions.getClient(admin_token);
            if (client.hasAdministrativeAccess())
                client.getAdministrationAccess().updateAccountPassword(admin_token, account_id, password);
        } catch (IllegalArgumentException e) {
            log.log_Error("Client [", admin_token, "] is not tracked in ClientSessions.");
            throw new Unauthorised("Client [" + admin_token + "] is not tracked in ClientSessions.", e);
        } catch (SessionInvalidException e) {
            log.log_Error("Client [", admin_token, "] is invalid.");
            throw new Unauthorised("Client [" + admin_token + "] is invalid.", e);
        } catch (SessionExpiredException e) {
            log.log_Error("Client [", admin_token, "] token has expired..");
            throw new Unauthorised("Client [" + admin_token + "] token has expired.", e);
        } catch (SessionCorruptedException e) {
            log.log_Error("Client [", admin_token, "] is using a corrupted Token.");
            throw new Unauthorised("Client [" + admin_token + "] is using a corrupted Token.", e);
        }
    }

    @Override
    public synchronized void activateAccount(Token admin_token, Integer account_id) throws Unauthorised, AccountExistenceException, AccountOverrideException, RuntimeException, RemoteException {
        try {
            ClientWrapper client = sessions.getClient(admin_token);
            if (client.hasAdministrativeAccess())
                client.getAdministrationAccess().activateAccount(admin_token, account_id);
        } catch (IllegalArgumentException e) {
            log.log_Error("Client [", admin_token, "] is not tracked in ClientSessions.");
            throw new Unauthorised("Client [" + admin_token + "] is not tracked in ClientSessions.", e);
        } catch (SessionInvalidException e) {
            log.log_Error("Client [", admin_token, "] is invalid.");
            throw new Unauthorised("Client [" + admin_token + "] is invalid.", e);
        } catch (SessionExpiredException e) {
            log.log_Error("Client [", admin_token, "] token has expired..");
            throw new Unauthorised("Client [" + admin_token + "] token has expired.", e);
        } catch (SessionCorruptedException e) {
            log.log_Error("Client [", admin_token, "] is using a corrupted Token.");
            throw new Unauthorised("Client [" + admin_token + "] is using a corrupted Token.", e);
        }
    }

    @Override
    public synchronized void deactivateAccount(Token admin_token, Integer account_id) throws Unauthorised, AccountExistenceException, AccountOverrideException, RuntimeException, RemoteException {
        try {
            ClientWrapper client = sessions.getClient(admin_token);
            if (client.hasAdministrativeAccess())
                client.getAdministrationAccess().deactivateAccount(admin_token, account_id);
        } catch (IllegalArgumentException e) {
            log.log_Error("Client [", admin_token, "] is not tracked in ClientSessions.");
            throw new Unauthorised("Client [" + admin_token + "] is not tracked in ClientSessions.", e);
        } catch (SessionInvalidException e) {
            log.log_Error("Client [", admin_token, "] is invalid.");
            throw new Unauthorised("Client [" + admin_token + "] is invalid.", e);
        } catch (SessionExpiredException e) {
            log.log_Error("Client [", admin_token, "] token has expired..");
            throw new Unauthorised("Client [" + admin_token + "] token has expired.", e);
        } catch (SessionCorruptedException e) {
            log.log_Error("Client [", admin_token, "] is using a corrupted Token.");
            throw new Unauthorised("Client [" + admin_token + "] is using a corrupted Token.", e);
        }
    }

    @Override
    public synchronized void deleteAccount(Token admin_token, Integer account_id) throws Unauthorised, AccountExistenceException, AccountOverrideException, RuntimeException, RemoteException {
        try {
            ClientWrapper client = sessions.getClient(admin_token);
            if (client.hasAdministrativeAccess())
                client.getAdministrationAccess().deleteAccount(admin_token, account_id);
        } catch (IllegalArgumentException e) {
            log.log_Error("Client [", admin_token, "] is not tracked in ClientSessions.");
            throw new Unauthorised("Client [" + admin_token + "] is not tracked in ClientSessions.", e);
        } catch (SessionInvalidException e) {
            log.log_Error("Client [", admin_token, "] is invalid.");
            throw new Unauthorised("Client [" + admin_token + "] is invalid.", e);
        } catch (SessionExpiredException e) {
            log.log_Error("Client [", admin_token, "] token has expired..");
            throw new Unauthorised("Client [" + admin_token + "] token has expired.", e);
        } catch (SessionCorruptedException e) {
            log.log_Error("Client [", admin_token, "] is using a corrupted Token.");
            throw new Unauthorised("Client [" + admin_token + "] is using a corrupted Token.", e);
        }
    }

    @Override
    public synchronized List<Account> getAccounts(Token admin_token) throws Unauthorised, RuntimeException, RemoteException {
        try {
            ClientWrapper client = sessions.getClient(admin_token);
            return client.getAdministrationAccess().getAccounts(admin_token);
        } catch (IllegalArgumentException e) {
            log.log_Error("Client [", admin_token, "] is not tracked in ClientSessions.");
            throw new Unauthorised("Client [" + admin_token + "] is not tracked in ClientSessions.", e);
        } catch (SessionInvalidException e) {
            log.log_Error("Client [", admin_token, "] is invalid.");
            throw new Unauthorised("Client [" + admin_token + "] is invalid.", e);
        } catch (SessionExpiredException e) {
            log.log_Error("Client [", admin_token, "] token has expired..");
            throw new Unauthorised("Client [" + admin_token + "] token has expired.", e);
        } catch (SessionCorruptedException e) {
            log.log_Error("Client [", admin_token, "] is using a corrupted Token.");
            throw new Unauthorised("Client [" + admin_token + "] is using a corrupted Token.", e);
        }
    }

    @Override
    public synchronized Account getAccount(Token admin_token, Integer account_id) throws AccountExistenceException, Unauthorised, RuntimeException, RemoteException {
        try {
            ClientWrapper client = sessions.getClient(admin_token);
            return client.getAdministrationAccess().getAccount(admin_token, account_id);
        } catch (IllegalArgumentException e) {
            log.log_Error("Client [", admin_token, "] is not tracked in ClientSessions.");
            throw new Unauthorised("Client [" + admin_token + "] is not tracked in ClientSessions.", e);
        } catch (SessionInvalidException e) {
            log.log_Error("Client [", admin_token, "] is invalid.");
            throw new Unauthorised("Client [" + admin_token + "] is invalid.", e);
        } catch (SessionExpiredException e) {
            log.log_Error("Client [", admin_token, "] token has expired..");
            throw new Unauthorised("Client [" + admin_token + "] token has expired.", e);
        } catch (SessionCorruptedException e) {
            log.log_Error("Client [", admin_token, "] is using a corrupted Token.");
            throw new Unauthorised("Client [" + admin_token + "] is using a corrupted Token.", e);
        }
    }

    @Override
    public synchronized Account getAccount(Token admin_token, String account_username) throws AccountExistenceException, Unauthorised, RuntimeException, RemoteException {
        try {
            ClientWrapper client = sessions.getClient(admin_token);
            return client.getAdministrationAccess().getAccount(admin_token, account_username);
        } catch (IllegalArgumentException e) {
            log.log_Error("Client [", admin_token, "] is not tracked in ClientSessions.");
            throw new Unauthorised("Client [" + admin_token + "] is not tracked in ClientSessions.", e);
        } catch (SessionInvalidException e) {
            log.log_Error("Client [", admin_token, "] is invalid.");
            throw new Unauthorised("Client [" + admin_token + "] is invalid.", e);
        } catch (SessionExpiredException e) {
            log.log_Error("Client [", admin_token, "] token has expired..");
            throw new Unauthorised("Client [" + admin_token + "] token has expired.", e);
        } catch (SessionCorruptedException e) {
            log.log_Error("Client [", admin_token, "] is using a corrupted Token.");
            throw new Unauthorised("Client [" + admin_token + "] is using a corrupted Token.", e);
        }
    }

    @Override
    public List<AccountType> getAccountTypes(Token admin_token) throws Unauthorised, RuntimeException, RemoteException {
        try {
            ClientWrapper client = sessions.getClient(admin_token);
            return client.getAdministrationAccess().getAccountTypes(admin_token);
        } catch (SessionInvalidException e) {
            log.log_Error("Client [", admin_token, "] is invalid.");
            throw new Unauthorised("Client [" + admin_token + "] is invalid.", e);
        } catch (SessionExpiredException e) {
            log.log_Error("Client [", admin_token, "] token has expired..");
            throw new Unauthorised("Client [" + admin_token + "] token has expired.", e);
        } catch (SessionCorruptedException e) {
            log.log_Error("Client [", admin_token, "] is using a corrupted Token.");
            throw new Unauthorised("Client [" + admin_token + "] is using a corrupted Token.", e);
        }
    }

    @Override
    public synchronized boolean optimiseReservationDatabase(Token admin_token) throws Unauthorised, RemoteException {
        try {
            ClientWrapper client = sessions.getClient(admin_token);
            return client.getAdministrationAccess().optimiseReservationDatabase(admin_token);
        } catch (IllegalArgumentException e) {
            log.log_Error("Client [", admin_token, "] is not tracked in ClientSessions.");
            throw new Unauthorised("Client [" + admin_token + "] is not tracked in ClientSessions.", e);
        } catch (SessionInvalidException e) {
            log.log_Error("Client [", admin_token, "] is invalid.");
            throw new Unauthorised("Client [" + admin_token + "] is invalid.", e);
        } catch (SessionExpiredException e) {
            log.log_Error("Client [", admin_token, "] token has expired..");
            throw new Unauthorised("Client [" + admin_token + "] token has expired.", e);
        } catch (SessionCorruptedException e) {
            log.log_Error("Client [", admin_token, "] is using a corrupted Token.");
            throw new Unauthorised("Client [" + admin_token + "] is using a corrupted Token.", e);
        }
    }

    @Override
    public synchronized boolean optimiseUserAccountDatabase(Token admin_token) throws Unauthorised, RemoteException {
        try {
            ClientWrapper client = sessions.getClient(admin_token);
            return client.getAdministrationAccess().optimiseUserAccountDatabase(admin_token);
        } catch (IllegalArgumentException e) {
            log.log_Error("Client [", admin_token, "] is not tracked in ClientSessions.");
            throw new Unauthorised("Client [" + admin_token + "] is not tracked in ClientSessions.", e);
        } catch (SessionInvalidException e) {
            log.log_Error("Client [", admin_token, "] is invalid.");
            throw new Unauthorised("Client [" + admin_token + "] is invalid.", e);
        } catch (SessionExpiredException e) {
            log.log_Error("Client [", admin_token, "] token has expired..");
            throw new Unauthorised("Client [" + admin_token + "] token has expired.", e);
        } catch (SessionCorruptedException e) {
            log.log_Error("Client [", admin_token, "] is using a corrupted Token.");
            throw new Unauthorised("Client [" + admin_token + "] is using a corrupted Token.", e);
        }
    }

    @Override
    public synchronized Customer getCustomerAccount(Token session_token, Integer customerID) throws InvalidCustomer, DbQueryException, Unauthorised, RemoteException {
        try {
            ClientWrapper client = sessions.getClient(session_token);
            return client.getReservationAccess().getCustomerAccount(session_token, customerID);
        } catch (IllegalArgumentException e) {
            log.log_Error("Client [", session_token, "] is not tracked in ClientSessions.");
            throw new Unauthorised("Client [" + session_token + "] is not tracked in ClientSessions.", e);
        } catch (SessionExpiredException e) {
            log.log_Error("Client [", session_token, "] token has expired..");
            throw new Unauthorised("Client [" + session_token + "] token has expired.", e);
        } catch (SessionInvalidException e) {
            log.log_Error("Client [", session_token, "] is invalid.");
            throw new Unauthorised("Client [" + session_token + "] is invalid.", e);
        } catch (FailedAllocation e) {
            log.log_Error("Could not allocated a ReservationSession for client [", session_token, "].");
            throw new Unauthorised("Could not allocated a ReservationSession for client [" + session_token + "].", e);
        }
    }

    @Override
    public synchronized Customer getCustomerAccount(Token session_token, Customer customer) throws InvalidCustomer, FailedDbFetch, Unauthorised, RemoteException {
        try {
            ClientWrapper client = sessions.getClient(session_token);
            return client.getReservationAccess().getCustomerAccount(session_token, customer);
        } catch (IllegalArgumentException e) {
            log.log_Error("Client [", session_token, "] is not tracked in ClientSessions.");
            throw new Unauthorised("Client [" + session_token + "] is not tracked in ClientSessions.", e);
        } catch (SessionExpiredException e) {
            log.log_Error("Client [", session_token, "] token has expired..");
            throw new Unauthorised("Client [" + session_token + "] token has expired.", e);
        } catch (SessionInvalidException e) {
            log.log_Error("Client [", session_token, "] is invalid.");
            throw new Unauthorised("Client [" + session_token + "] is invalid.", e);
        } catch (FailedAllocation e) {
            log.log_Error("Could not allocated a ReservationSession for client [", session_token, "].");
            throw new Unauthorised("Could not allocated a ReservationSession for client [" + session_token + "].", e);
        }
    }

    @Override
    public synchronized List<Pair<Integer, String>> findCustomer(Token session_token, String surname) throws FailedDbFetch, Unauthorised, RemoteException {
        try {
            ClientWrapper client = sessions.getClient(session_token);
            return client.getReservationAccess().findCustomer(session_token, surname);
        } catch (IllegalArgumentException e) {
            log.log_Error("Client [", session_token, "] is not tracked in ClientSessions.");
            throw new Unauthorised("Client [" + session_token + "] is not tracked in ClientSessions.", e);
        } catch (SessionExpiredException e) {
            log.log_Error("Client [", session_token, "] token has expired..");
            throw new Unauthorised("Client [" + session_token + "] token has expired.", e);
        } catch (SessionInvalidException e) {
            log.log_Error("Client [", session_token, "] is invalid.");
            throw new Unauthorised("Client [" + session_token + "] is invalid.", e);
        } catch (FailedAllocation e) {
            log.log_Error("Could not allocated a ReservationSession for client [", session_token, "].");
            throw new Unauthorised("Could not allocated a ReservationSession for client [" + session_token + "].", e);
        }
    }

    @Override
    public synchronized Customer createNewCustomer(Token session_token, Customer customer) throws FailedDbWrite, FailedDbFetch, Unauthorised, RemoteException {
        try {
            ClientWrapper client = sessions.getClient(session_token);
            return client.getReservationAccess().createNewCustomer(session_token, customer);
        } catch (IllegalArgumentException e) {
            log.log_Error("Client [", session_token, "] is not tracked in ClientSessions.");
            throw new Unauthorised("Client [" + session_token + "] is not tracked in ClientSessions.", e);
        } catch (SessionExpiredException e) {
            log.log_Error("Client [", session_token, "] token has expired..");
            throw new Unauthorised("Client [" + session_token + "] token has expired.", e);
        } catch (SessionInvalidException e) {
            log.log_Error("Client [", session_token, "] is invalid.");
            throw new Unauthorised("Client [" + session_token + "] is invalid.", e);
        } catch (FailedAllocation e) {
            log.log_Error("Could not allocated a ReservationSession for client [", session_token, "].");
            throw new Unauthorised("Could not allocated a ReservationSession for client [" + session_token + "].", e);
        }
    }

    @Override
    public synchronized void saveCustomerChangesToDB(Token session_token, Customer customer) throws FailedDbWrite, Unauthorised, RemoteException {
        try {
            ClientWrapper client = sessions.getClient(session_token);
            client.getReservationAccess().saveCustomerChangesToDB(session_token, customer);
        } catch (IllegalArgumentException e) {
            log.log_Error("Client [", session_token, "] is not tracked in ClientSessions.");
            throw new Unauthorised("Client [" + session_token + "] is not tracked in ClientSessions.", e);
        } catch (SessionExpiredException e) {
            log.log_Error("Client [", session_token, "] token has expired..");
            throw new Unauthorised("Client [" + session_token + "] token has expired.", e);
        } catch (SessionInvalidException e) {
            log.log_Error("Client [", session_token, "] is invalid.");
            throw new Unauthorised("Client [" + session_token + "] is invalid.", e);
        } catch (FailedAllocation e) {
            log.log_Error("Could not allocated a ReservationSession for client [", session_token, "].");
            throw new Unauthorised("Could not allocated a ReservationSession for client [" + session_token + "].", e);
        }
    }

    @Override
    public Membership getMembership(Token session_token, Integer membership_id) throws InvalidMembership, FailedDbFetch, Unauthorised, RemoteException {
        try {
            ClientWrapper client = sessions.getClient(session_token);
            return client.getReservationAccess().getMembership(session_token, membership_id);
        } catch (SessionExpiredException e) {
            log.log_Error("Client [", session_token, "] token has expired..");
            throw new Unauthorised("Client [" + session_token + "] token has expired.", e);
        } catch (SessionInvalidException e) {
            log.log_Error("Client [", session_token, "] is invalid.");
            throw new Unauthorised("Client [" + session_token + "] is invalid.", e);
        } catch (FailedAllocation e) {
            log.log_Error("Could not allocated a ReservationSession for client [", session_token, "].");
            throw new Unauthorised("Could not allocated a ReservationSession for client [" + session_token + "].", e);
        }
    }

    @Override
    public List<Membership> getMemberships(Token session_token) throws FailedDbFetch, Unauthorised, RemoteException {
        try {
            ClientWrapper client = sessions.getClient(session_token);
            return client.getReservationAccess().getMemberships(session_token);
        } catch (SessionExpiredException e) {
            log.log_Error("Client [", session_token, "] token has expired..");
            throw new Unauthorised("Client [" + session_token + "] token has expired.", e);
        } catch (SessionInvalidException e) {
            log.log_Error("Client [", session_token, "] is invalid.");
            throw new Unauthorised("Client [" + session_token + "] is invalid.", e);
        } catch (FailedAllocation e) {
            log.log_Error("Could not allocated a ReservationSession for client [", session_token, "].");
            throw new Unauthorised("Could not allocated a ReservationSession for client [" + session_token + "].", e);
        }
    }

    @Override
    public synchronized List<Room> search(Token session_token, RoomProperty properties) throws FailedDbFetch, Unauthorised, RemoteException {
        try {
            ClientWrapper client = sessions.getClient(session_token);
            return client.getReservationAccess().search(session_token, properties);
        } catch (IllegalArgumentException e) {
            log.log_Error("Client [", session_token, "] is not tracked in ClientSessions.");
            throw new Unauthorised("Client [" + session_token + "] is not tracked in ClientSessions.", e);
        } catch (SessionExpiredException e) {
            log.log_Error("Client [", session_token, "] token has expired..");
            throw new Unauthorised("Client [" + session_token + "] token has expired.", e);
        } catch (SessionInvalidException e) {
            log.log_Error("Client [", session_token, "] is invalid.");
            throw new Unauthorised("Client [" + session_token + "] is invalid.", e);
        } catch (FailedAllocation e) {
            log.log_Error("Could not allocated a ReservationSession for client [", session_token, "].");
            throw new Unauthorised("Could not allocated a ReservationSession for client [" + session_token + "].", e);
        }
    }

    @Override
    public synchronized List<Room> search(Token session_token, Integer building_id, RoomProperty properties) throws FailedDbFetch, Unauthorised, RemoteException {
        try {
            ClientWrapper client = sessions.getClient(session_token);
            return client.getReservationAccess().search(session_token, building_id, properties);
        } catch (IllegalArgumentException e) {
            log.log_Error("Client [", session_token, "] is not tracked in ClientSessions.");
            throw new Unauthorised("Client [" + session_token + "] is not tracked in ClientSessions.", e);
        } catch (SessionExpiredException e) {
            log.log_Error("Client [", session_token, "] token has expired..");
            throw new Unauthorised("Client [" + session_token + "] token has expired.", e);
        } catch (SessionInvalidException e) {
            log.log_Error("Client [", session_token, "] is invalid.");
            throw new Unauthorised("Client [" + session_token + "] is invalid.", e);
        } catch (FailedAllocation e) {
            log.log_Error("Could not allocated a ReservationSession for client [", session_token, "].");
            throw new Unauthorised("Could not allocated a ReservationSession for client [" + session_token + "].", e);
        }
    }

    @Override
    public synchronized List<Room> search(Token session_token, Integer building_id, Integer floor_id, RoomProperty properties) throws FailedDbFetch, Unauthorised, RemoteException {
        try {
            ClientWrapper client = sessions.getClient(session_token);
            return client.getReservationAccess().search(session_token, building_id, floor_id, properties);
        } catch (IllegalArgumentException e) {
            log.log_Error("Client [", session_token, "] is not tracked in ClientSessions.");
            throw new Unauthorised("Client [" + session_token + "] is not tracked in ClientSessions.", e);
        } catch (SessionExpiredException e) {
            log.log_Error("Client [", session_token, "] token has expired..");
            throw new Unauthorised("Client [" + session_token + "] token has expired.", e);
        } catch (SessionInvalidException e) {
            log.log_Error("Client [", session_token, "] is invalid.");
            throw new Unauthorised("Client [" + session_token + "] is invalid.", e);
        } catch (FailedAllocation e) {
            log.log_Error("Could not allocated a ReservationSession for client [", session_token, "].");
            throw new Unauthorised("Could not allocated a ReservationSession for client [" + session_token + "].", e);
        }
    }

    @Override
    public synchronized List<TimeSpan> search(Token session_token, Room room, Date from, Date to) throws FailedDbFetch, Unauthorised, RemoteException {
        try {
            ClientWrapper client = sessions.getClient(session_token);
            return client.getReservationAccess().search(session_token, room, from, to);
        } catch (IllegalArgumentException e) {
            log.log_Error("Client [", session_token, "] is not tracked in ClientSessions.");
            throw new Unauthorised("Client [" + session_token + "] is not tracked in ClientSessions.", e);
        } catch (SessionExpiredException e) {
            log.log_Error("Client [", session_token, "] token has expired..");
            throw new Unauthorised("Client [" + session_token + "] token has expired.", e);
        } catch (SessionInvalidException e) {
            log.log_Error("Client [", session_token, "] is invalid.");
            throw new Unauthorised("Client [" + session_token + "] is invalid.", e);
        } catch (FailedAllocation e) {
            log.log_Error("Could not allocated a ReservationSession for client [", session_token, "].");
            throw new Unauthorised("Could not allocated a ReservationSession for client [" + session_token + "].", e);
        }
    }

    @Override
    public synchronized HashMap<Room, List<TimeSpan>> search(Token session_token, Integer building_id, Integer floor_id, Date from, Date to, RoomProperty property) throws FailedDbFetch, Unauthorised, RemoteException {
        try {
            ClientWrapper client = sessions.getClient(session_token);
            return client.getReservationAccess().search(session_token, building_id, floor_id, from, to, property);
        } catch (IllegalArgumentException e) {
            log.log_Error("Client [", session_token, "] is not tracked in ClientSessions.");
            throw new Unauthorised("Client [" + session_token + "] is not tracked in ClientSessions.", e);
        } catch (SessionExpiredException e) {
            log.log_Error("Client [", session_token, "] token has expired..");
            throw new Unauthorised("Client [" + session_token + "] token has expired.", e);
        } catch (SessionInvalidException e) {
            log.log_Error("Client [", session_token, "] is invalid.");
            throw new Unauthorised("Client [" + session_token + "] is invalid.", e);
        } catch (FailedAllocation e) {
            log.log_Error("Could not allocated a ReservationSession for client [", session_token, "].");
            throw new Unauthorised("Could not allocated a ReservationSession for client [" + session_token + "].", e);
        }
    }

    @Override
    public synchronized HashMap<Room, List<TimeSpan>> search(Token session_token, Integer building_id, Date from, Date to, RoomProperty property) throws FailedDbFetch, Unauthorised, RemoteException {
        try {
            ClientWrapper client = sessions.getClient(session_token);
            return client.getReservationAccess().search(session_token, building_id, from, to, property);
        } catch (IllegalArgumentException e) {
            log.log_Error("Client [", session_token, "] is not tracked in ClientSessions.");
            throw new Unauthorised("Client [" + session_token + "] is not tracked in ClientSessions.", e);
        } catch (SessionExpiredException e) {
            log.log_Error("Client [", session_token, "] token has expired..");
            throw new Unauthorised("Client [" + session_token + "] token has expired.", e);
        } catch (SessionInvalidException e) {
            log.log_Error("Client [", session_token, "] is invalid.");
            throw new Unauthorised("Client [" + session_token + "] is invalid.", e);
        } catch (FailedAllocation e) {
            log.log_Error("Could not allocated a ReservationSession for client [", session_token, "].");
            throw new Unauthorised("Could not allocated a ReservationSession for client [" + session_token + "].", e);
        }
    }

    @Override
    public synchronized HashMap<Room, List<TimeSpan>> search(Token session_token, Date from, Date to, RoomProperty property) throws FailedDbFetch, Unauthorised, RemoteException {
        try {
            ClientWrapper client = sessions.getClient(session_token);
            return client.getReservationAccess().search(session_token, from, to, property);
        } catch (IllegalArgumentException e) {
            log.log_Error("Client [", session_token, "] is not tracked in ClientSessions.");
            throw new Unauthorised("Client [" + session_token + "] is not tracked in ClientSessions.", e);
        } catch (SessionExpiredException e) {
            log.log_Error("Client [", session_token, "] token has expired..");
            throw new Unauthorised("Client [" + session_token + "] token has expired.", e);
        } catch (SessionInvalidException e) {
            log.log_Error("Client [", session_token, "] is invalid.");
            throw new Unauthorised("Client [" + session_token + "] is invalid.", e);
        } catch (FailedAllocation e) {
            log.log_Error("Could not allocated a ReservationSession for client [", session_token, "].");
            throw new Unauthorised("Could not allocated a ReservationSession for client [" + session_token + "].", e);
        }
    }

    @Override
    public synchronized Double pay(Token session_token, Reservation reservation, Payment payment) throws FailedDbWrite, FailedDbFetch, Unauthorised, RemoteException {
        try {
            ClientWrapper client = sessions.getClient(session_token);
            return client.getReservationAccess().pay(session_token, reservation, payment);
        } catch (IllegalArgumentException e) {
            log.log_Error("Client [", session_token, "] is not tracked in ClientSessions.");
            throw new Unauthorised("Client [" + session_token + "] is not tracked in ClientSessions.", e);
        } catch (SessionExpiredException e) {
            log.log_Error("Client [", session_token, "] token has expired..");
            throw new Unauthorised("Client [" + session_token + "] token has expired.", e);
        } catch (SessionInvalidException e) {
            log.log_Error("Client [", session_token, "] is invalid.");
            throw new Unauthorised("Client [" + session_token + "] is invalid.", e);
        } catch (FailedAllocation e) {
            log.log_Error("Could not allocated a ReservationSession for client [", session_token, "].");
            throw new Unauthorised("Could not allocated a ReservationSession for client [" + session_token + "].", e);
        }
    }

    @Override
    public synchronized List<Payment> getPayments(Token session_token, Reservation reservation) throws FailedDbFetch, Unauthorised, RemoteException {
        try {
            ClientWrapper client = sessions.getClient(session_token);
            return client.getReservationAccess().getPayments(session_token, reservation);
        } catch (IllegalArgumentException e) {
            log.log_Error("Client [", session_token, "] is not tracked in ClientSessions.");
            throw new Unauthorised("Client [" + session_token + "] is not tracked in ClientSessions.", e);
        } catch (SessionExpiredException e) {
            log.log_Error("Client [", session_token, "] token has expired..");
            throw new Unauthorised("Client [" + session_token + "] token has expired.", e);
        } catch (SessionInvalidException e) {
            log.log_Error("Client [", session_token, "] is invalid.");
            throw new Unauthorised("Client [" + session_token + "] is invalid.", e);
        } catch (FailedAllocation e) {
            log.log_Error("Could not allocated a ReservationSession for client [", session_token, "].");
            throw new Unauthorised("Could not allocated a ReservationSession for client [" + session_token + "].", e);
        }
    }

    @Override
    public synchronized List<PaymentMethod> getPaymentMethods(Token session_token) throws FailedDbFetch, Unauthorised, RemoteException {
        try {
            ClientWrapper client = sessions.getClient(session_token);
            return client.getReservationAccess().getPaymentMethods(session_token);
        } catch (IllegalArgumentException e) {
            log.log_Error("Client [", session_token, "] is not tracked in ClientSessions.");
            throw new Unauthorised("Client [" + session_token + "] is not tracked in ClientSessions.", e);
        } catch (SessionExpiredException e) {
            log.log_Error("Client [", session_token, "] token has expired..");
            throw new Unauthorised("Client [" + session_token + "] token has expired.", e);
        } catch (SessionInvalidException e) {
            log.log_Error("Client [", session_token, "] is invalid.");
            throw new Unauthorised("Client [" + session_token + "] is invalid.", e);
        } catch (FailedAllocation e) {
            log.log_Error("Could not allocated a ReservationSession for client [", session_token, "].");
            throw new Unauthorised("Could not allocated a ReservationSession for client [" + session_token + "].", e);
        }
    }

    @Override
    public synchronized Reservation createReservation(Token session_token, Reservation reservation) throws FailedDbWrite, FailedDbFetch, Unauthorised, RemoteException {
        try {
            ClientWrapper client = sessions.getClient(session_token);
            return client.getReservationAccess().createReservation(session_token, reservation);
        } catch (IllegalArgumentException e) {
            log.log_Error("Client [", session_token, "] is not tracked in ClientSessions.");
            throw new Unauthorised("Client [" + session_token + "] is not tracked in ClientSessions.", e);
        } catch (SessionExpiredException e) {
            log.log_Error("Client [", session_token, "] token has expired..");
            throw new Unauthorised("Client [" + session_token + "] token has expired.", e);
        } catch (SessionInvalidException e) {
            log.log_Error("Client [", session_token, "] is invalid.");
            throw new Unauthorised("Client [" + session_token + "] is invalid.", e);
        } catch (FailedAllocation e) {
            log.log_Error("Could not allocated a ReservationSession for client [", session_token, "].");
            throw new Unauthorised("Could not allocated a ReservationSession for client [" + session_token + "].", e);
        }
    }

    @Override
    public synchronized void createRoomReservation(Token session_token, Reservation reservation, RoomReservation reserved_room) throws InvalidReservation, FailedDbWrite, FailedDbFetch, Unauthorised, RemoteException {
        try {
            ClientWrapper client = sessions.getClient(session_token);
            client.getReservationAccess().createRoomReservation(session_token, reservation, reserved_room);
        } catch (IllegalArgumentException e) {
            log.log_Error("Client [", session_token, "] is not tracked in ClientSessions.");
            throw new Unauthorised("Client [" + session_token + "] is not tracked in ClientSessions.", e);
        } catch (SessionExpiredException e) {
            log.log_Error("Client [", session_token, "] token has expired..");
            throw new Unauthorised("Client [" + session_token + "] token has expired.", e);
        } catch (SessionInvalidException e) {
            log.log_Error("Client [", session_token, "] is invalid.");
            throw new Unauthorised("Client [" + session_token + "] is invalid.", e);
        } catch (FailedAllocation e) {
            log.log_Error("Could not allocated a ReservationSession for client [", session_token, "].");
            throw new Unauthorised("Could not allocated a ReservationSession for client [" + session_token + "].", e);
        }
    }

    @Override
    public synchronized Double cancelReservation(Token session_token, Reservation reservation) throws InvalidReservation, FailedDbWrite, Unauthorised, RemoteException {
        try {
            ClientWrapper client = sessions.getClient(session_token);
            return client.getReservationAccess().cancelReservation(session_token, reservation);
        } catch (IllegalArgumentException e) {
            log.log_Error("Client [", session_token, "] is not tracked in ClientSessions.");
            throw new Unauthorised("Client [" + session_token + "] is not tracked in ClientSessions.", e);
        } catch (SessionExpiredException e) {
            log.log_Error("Client [", session_token, "] token has expired..");
            throw new Unauthorised("Client [" + session_token + "] token has expired.", e);
        } catch (SessionInvalidException e) {
            log.log_Error("Client [", session_token, "] is invalid.");
            throw new Unauthorised("Client [" + session_token + "] is invalid.", e);
        } catch (FailedAllocation e) {
            log.log_Error("Could not allocated a ReservationSession for client [", session_token, "].");
            throw new Unauthorised("Could not allocated a ReservationSession for client [" + session_token + "].", e);
        }
    }

    @Override
    public synchronized Double cancelReservedRoom(Token session_token, Reservation reservation, RoomReservation room_reservation) throws InvalidReservation, FailedDbWrite, Unauthorised, RemoteException {
        try {
            ClientWrapper client = sessions.getClient(session_token);
            return client.getReservationAccess().cancelReservedRoom(session_token, reservation, room_reservation);
        } catch (IllegalArgumentException e) {
            log.log_Error("Client [", session_token, "] is not tracked in ClientSessions.");
            throw new Unauthorised("Client [" + session_token + "] is not tracked in ClientSessions.", e);
        } catch (SessionExpiredException e) {
            log.log_Error("Client [", session_token, "] token has expired..");
            throw new Unauthorised("Client [" + session_token + "] token has expired.", e);
        } catch (SessionInvalidException e) {
            log.log_Error("Client [", session_token, "] is invalid.");
            throw new Unauthorised("Client [" + session_token + "] is invalid.", e);
        } catch (FailedAllocation e) {
            log.log_Error("Could not allocated a ReservationSession for client [", session_token, "].");
            throw new Unauthorised("Could not allocated a ReservationSession for client [" + session_token + "].", e);
        }
    }

    @Override
    public synchronized Reservation getReservation(Token session_token, Integer reservation_id) throws InvalidReservation, FailedDbFetch, Unauthorised, RemoteException {
        try {
            ClientWrapper client = sessions.getClient(session_token);
            return client.getReservationAccess().getReservation(session_token, reservation_id);
        } catch (IllegalArgumentException e) {
            log.log_Error("Client [", session_token, "] is not tracked in ClientSessions.");
            throw new Unauthorised("Client [" + session_token + "] is not tracked in ClientSessions.", e);
        } catch (SessionExpiredException e) {
            log.log_Error("Client [", session_token, "] token has expired..");
            throw new Unauthorised("Client [" + session_token + "] token has expired.", e);
        } catch (SessionInvalidException e) {
            log.log_Error("Client [", session_token, "] is invalid.");
            throw new Unauthorised("Client [" + session_token + "] is invalid.", e);
        } catch (FailedAllocation e) {
            log.log_Error("Could not allocated a ReservationSession for client [", session_token, "].");
            throw new Unauthorised("Could not allocated a ReservationSession for client [" + session_token + "].", e);
        }
    }

    @Override
    public synchronized List<Reservation> getReservations(Token session_token, Customer customer) throws FailedDbFetch, Unauthorised, RemoteException {
        try {
            ClientWrapper client = sessions.getClient(session_token);
            return client.getReservationAccess().getReservations(session_token, customer);
        } catch (IllegalArgumentException e) {
            log.log_Error("Client [", session_token, "] is not tracked in ClientSessions.");
            throw new Unauthorised("Client [" + session_token + "] is not tracked in ClientSessions.", e);
        } catch (SessionExpiredException e) {
            log.log_Error("Client [", session_token, "] token has expired..");
            throw new Unauthorised("Client [" + session_token + "] token has expired.", e);
        } catch (SessionInvalidException e) {
            log.log_Error("Client [", session_token, "] is invalid.");
            throw new Unauthorised("Client [" + session_token + "] is invalid.", e);
        } catch (FailedAllocation e) {
            log.log_Error("Could not allocated a ReservationSession for client [", session_token, "].");
            throw new Unauthorised("Could not allocated a ReservationSession for client [" + session_token + "].", e);
        }
    }

    @Override
    public synchronized RoomCategory getRoomCategory(Token session_token, Integer category_id) throws InvalidRoomCategory, FailedDbFetch, Unauthorised, RemoteException {
        try {
            ClientWrapper client = sessions.getClient(session_token);
            return client.getReservationAccess().getRoomCategory(session_token, category_id);
        } catch (IllegalArgumentException e) {
            log.log_Error("Client [", session_token, "] is not tracked in ClientSessions.");
            throw new Unauthorised("Client [" + session_token + "] is not tracked in ClientSessions.", e);
        } catch (SessionExpiredException e) {
            log.log_Error("Client [", session_token, "] token has expired..");
            throw new Unauthorised("Client [" + session_token + "] token has expired.", e);
        } catch (SessionInvalidException e) {
            log.log_Error("Client [", session_token, "] is invalid.");
            throw new Unauthorised("Client [" + session_token + "] is invalid.", e);
        } catch (FailedAllocation e) {
            log.log_Error("Could not allocated a ReservationSession for client [", session_token, "].");
            throw new Unauthorised("Could not allocated a ReservationSession for client [" + session_token + "].", e);
        }
    }

    @Override
    public synchronized DetailedRoom getRoomDetails(Token session_token, Room room) throws InvalidRoom, FailedDbFetch, Unauthorised, RemoteException {
        try {
            ClientWrapper client = sessions.getClient(session_token);
            return client.getReservationAccess().getRoomDetails(session_token, room);
        } catch (IllegalArgumentException e) {
            log.log_Error("Client [", session_token, "] is not tracked in ClientSessions.");
            throw new Unauthorised("Client [" + session_token + "] is not tracked in ClientSessions.", e);
        } catch (SessionExpiredException e) {
            log.log_Error("Client [", session_token, "] token has expired..");
            throw new Unauthorised("Client [" + session_token + "] token has expired.", e);
        } catch (SessionInvalidException e) {
            log.log_Error("Client [", session_token, "] is invalid.");
            throw new Unauthorised("Client [" + session_token + "] is invalid.", e);
        } catch (FailedAllocation e) {
            log.log_Error("Could not allocated a ReservationSession for client [", session_token, "].");
            throw new Unauthorised("Could not allocated a ReservationSession for client [" + session_token + "].", e);
        }
    }

    @Override
    public synchronized LogisticsInfo getInfo(Token session_token, Integer building_id, Date from, Date to) throws InvalidPeriodException, FailedDbFetch, Unauthorised, RemoteException {
        try {
            ClientWrapper client = sessions.getClient(session_token);
            return client.getLogisticsAccess().getInfo(session_token, building_id, from, to);
        } catch (IllegalArgumentException e) {
            log.log_Error("Client [", session_token, "] is not tracked in ClientSessions.");
            throw new Unauthorised("Client [" + session_token + "] is not tracked in ClientSessions.", e);
        } catch (SessionExpiredException e) {
            log.log_Error("Client [", session_token, "] token has expired..");
            throw new Unauthorised("Client [" + session_token + "] token has expired.", e);
        } catch (SessionInvalidException e) {
            log.log_Error("Client [", session_token, "] is invalid.");
            throw new Unauthorised("Client [" + session_token + "] is invalid.", e);
        }
    }

    @Override
    public synchronized LogisticsInfo getInfo(Token session_token, Integer building_id, Integer floor_id, Date from, Date to) throws InvalidPeriodException, FailedDbFetch, Unauthorised, RemoteException {
        try {
            ClientWrapper client = sessions.getClient(session_token);
            return client.getLogisticsAccess().getInfo(session_token, building_id, floor_id, from, to);
        } catch (IllegalArgumentException e) {
            log.log_Error("Client [", session_token, "] is not tracked in ClientSessions.");
            throw new Unauthorised("Client [" + session_token + "] is not tracked in ClientSessions.", e);
        } catch (SessionExpiredException e) {
            log.log_Error("Client [", session_token, "] token has expired..");
            throw new Unauthorised("Client [" + session_token + "] token has expired.", e);
        } catch (SessionInvalidException e) {
            log.log_Error("Client [", session_token, "] is invalid.");
            throw new Unauthorised("Client [" + session_token + "] is invalid.", e);
        }
    }

    @Override
    public synchronized LogisticsInfo getInfo(Token session_token, Room room, Date from, Date to) throws InvalidPeriodException, FailedDbFetch, Unauthorised, RemoteException {
        try {
            ClientWrapper client = sessions.getClient(session_token);
            return client.getLogisticsAccess().getInfo(session_token, room, from, to);
        } catch (IllegalArgumentException e) {
            log.log_Error("Client [", session_token, "] is not tracked in ClientSessions.");
            throw new Unauthorised("Client [" + session_token + "] is not tracked in ClientSessions.", e);
        } catch (SessionExpiredException e) {
            log.log_Error("Client [", session_token, "] token has expired..");
            throw new Unauthorised("Client [" + session_token + "] token has expired.", e);
        } catch (SessionInvalidException e) {
            log.log_Error("Client [", session_token, "] is invalid.");
            throw new Unauthorised("Client [" + session_token + "] is invalid.", e);
        }
    }
}
