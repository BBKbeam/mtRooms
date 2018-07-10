package bbk_beam.mtRooms.network;

import bbk_beam.mtRooms.admin.authentication.Token;
import bbk_beam.mtRooms.admin.dto.Account;
import bbk_beam.mtRooms.admin.dto.AccountType;
import bbk_beam.mtRooms.admin.dto.Usage;
import bbk_beam.mtRooms.admin.exception.*;
import bbk_beam.mtRooms.db.exception.SessionCorruptedException;
import bbk_beam.mtRooms.db.exception.SessionExpiredException;
import bbk_beam.mtRooms.db.exception.SessionInvalidException;
import bbk_beam.mtRooms.db.session.SessionType;
import bbk_beam.mtRooms.network.exception.Unauthorised;
import bbk_beam.mtRooms.network.session.ClientSessions;
import bbk_beam.mtRooms.network.session.ClientWrapper;
import bbk_beam.mtRooms.reservation.dto.*;
import eadjlib.logger.Logger;

import java.rmi.RemoteException;
import java.util.List;

public class RmiAdministrationServices implements IRmiAdministrationServices {
    private final Logger log = Logger.getLoggerInstance(RmiAdministrationServices.class.getName());
    private ClientSessions sessions;

    /**
     * Constructor
     *
     * @param sessions ClientSessions instance
     */
    RmiAdministrationServices(ClientSessions sessions) {
        this.sessions = sessions;
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
    public List<Building> getBuildings(Token admin_token) throws FailedRecordFetch, Unauthorised, RemoteException {
        try {
            ClientWrapper client = sessions.getClient(admin_token);
            return client.getAdministrationAccess().getBuildings(admin_token);
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
    public List<Floor> getFloors(Token admin_token, Building building) throws FailedRecordFetch, Unauthorised, RemoteException {
        try {
            ClientWrapper client = sessions.getClient(admin_token);
            return client.getAdministrationAccess().getFloors(admin_token, building);
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
    public List<Room> getRooms(Token admin_token, Floor floor) throws FailedRecordFetch, Unauthorised, RemoteException {
        try {
            ClientWrapper client = sessions.getClient(admin_token);
            return client.getAdministrationAccess().getRooms(admin_token, floor);
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
    public List<Usage<RoomPrice, Integer>> getRoomPrices(Token admin_token, Room room) throws FailedRecordFetch, Unauthorised, RemoteException {
        try {
            ClientWrapper client = sessions.getClient(admin_token);
            return client.getAdministrationAccess().getRoomPrices(admin_token, room);
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
    public List<Usage<RoomCategory, Room>> getRoomCategories(Token admin_token) throws FailedRecordFetch, Unauthorised, RemoteException {
        try {
            ClientWrapper client = sessions.getClient(admin_token);
            return client.getAdministrationAccess().getRoomCategories(admin_token);
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
    public DetailedRoom getRoomDetails(Token admin_token, Room room) throws FailedRecordFetch, Unauthorised, RemoteException {
        try {
            ClientWrapper client = sessions.getClient(admin_token);
            return client.getAdministrationAccess().getRoomDetails(admin_token, room);
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
    public void add(Token admin_token, Building building) throws FailedRecordWrite, Unauthorised, RemoteException {
        try {
            ClientWrapper client = sessions.getClient(admin_token);
            client.getAdministrationAccess().add(admin_token, building);
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
    public void add(Token admin_token, Floor floor) throws FailedRecordWrite, Unauthorised, RemoteException {
        try {
            ClientWrapper client = sessions.getClient(admin_token);
            client.getAdministrationAccess().add(admin_token, floor);
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
    public void add(Token admin_token, Room room, RoomFixtures fixtures) throws FailedRecordWrite, Unauthorised, RemoteException {
        try {
            ClientWrapper client = sessions.getClient(admin_token);
            client.getAdministrationAccess().add(admin_token, room, fixtures);
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
    public RoomPrice add(Token admin_token, RoomPrice roomPrice) throws FailedRecordWrite, FailedRecordFetch, Unauthorised, RemoteException {
        try {
            ClientWrapper client = sessions.getClient(admin_token);
            return client.getAdministrationAccess().add(admin_token, roomPrice);
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
    public RoomCategory add(Token admin_token, RoomCategory roomCategory) throws FailedRecordWrite, FailedRecordFetch, Unauthorised, RemoteException {
        try {
            ClientWrapper client = sessions.getClient(admin_token);
            return client.getAdministrationAccess().add(admin_token, roomCategory);
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
    public void update(Token admin_token, Building building) throws FailedRecordUpdate, Unauthorised, RemoteException {
        try {
            ClientWrapper client = sessions.getClient(admin_token);
            client.getAdministrationAccess().update(admin_token, building);
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
    public void update(Token admin_token, Floor floor) throws FailedRecordUpdate, Unauthorised, RemoteException {
        try {
            ClientWrapper client = sessions.getClient(admin_token);
            client.getAdministrationAccess().update(admin_token, floor);
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
    public void update(Token admin_token, Room room) throws FailedRecordUpdate, Unauthorised, RemoteException {
        try {
            ClientWrapper client = sessions.getClient(admin_token);
            client.getAdministrationAccess().update(admin_token, room);
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
    public boolean remove(Token admin_token, Building building) throws FailedRecordWrite, Unauthorised, RemoteException {
        try {
            ClientWrapper client = sessions.getClient(admin_token);
            return client.getAdministrationAccess().remove(admin_token, building);
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
    public boolean remove(Token admin_token, Floor floor) throws FailedRecordWrite, Unauthorised, RemoteException {
        try {
            ClientWrapper client = sessions.getClient(admin_token);
            return client.getAdministrationAccess().remove(admin_token, floor);
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
    public boolean remove(Token admin_token, Room room) throws FailedRecordWrite, Unauthorised, RemoteException {
        try {
            ClientWrapper client = sessions.getClient(admin_token);
            return client.getAdministrationAccess().remove(admin_token, room);
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
    public boolean remove(Token admin_token, RoomPrice roomPrice) throws FailedRecordWrite, Unauthorised, RemoteException {
        try {
            ClientWrapper client = sessions.getClient(admin_token);
            return client.getAdministrationAccess().remove(admin_token, roomPrice);
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
    public boolean remove(Token admin_token, RoomCategory roomCategory) throws FailedRecordWrite, Unauthorised, RemoteException {
        try {
            ClientWrapper client = sessions.getClient(admin_token);
            return client.getAdministrationAccess().remove(admin_token, roomCategory);
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
    public List<Usage<Membership, Integer>> getMemberships(Token admin_token) throws FailedRecordFetch, Unauthorised, RemoteException {
        try {
            ClientWrapper client = sessions.getClient(admin_token);
            return client.getAdministrationAccess().getMemberships(admin_token);
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
    public List<Discount> getDiscounts(Token admin_token) throws FailedRecordFetch, Unauthorised, RemoteException {
        try {
            ClientWrapper client = sessions.getClient(admin_token);
            return client.getAdministrationAccess().getDiscounts(admin_token);
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
    public Membership add(Token admin_token, Membership membership) throws FailedRecordWrite, Unauthorised, RemoteException {
        try {
            ClientWrapper client = sessions.getClient(admin_token);
            return client.getAdministrationAccess().add(admin_token, membership);
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
    public boolean remove(Token admin_token, Membership membership) throws FailedRecordWrite, Unauthorised, RemoteException {
        try {
            ClientWrapper client = sessions.getClient(admin_token);
            return client.getAdministrationAccess().remove(admin_token, membership);
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
}
