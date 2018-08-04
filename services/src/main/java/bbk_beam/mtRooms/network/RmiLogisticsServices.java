package bbk_beam.mtRooms.network;

import bbk_beam.mtRooms.admin.authentication.Token;
import bbk_beam.mtRooms.db.exception.SessionExpiredException;
import bbk_beam.mtRooms.db.exception.SessionInvalidException;
import bbk_beam.mtRooms.network.exception.Unauthorised;
import bbk_beam.mtRooms.network.session.ClientSessions;
import bbk_beam.mtRooms.network.session.ClientWrapper;
import bbk_beam.mtRooms.operation.dto.LogisticsInfo;
import bbk_beam.mtRooms.reservation.dto.Building;
import bbk_beam.mtRooms.reservation.dto.Floor;
import bbk_beam.mtRooms.reservation.dto.Room;
import bbk_beam.mtRooms.reservation.exception.FailedDbFetch;
import bbk_beam.mtRooms.revenue.exception.InvalidPeriodException;
import eadjlib.logger.Logger;

import java.rmi.RemoteException;
import java.util.Date;
import java.util.List;

public class RmiLogisticsServices implements IRmiLogisticsServices {
    private final Logger log = Logger.getLoggerInstance(RmiLogisticsServices.class.getName());
    private ClientSessions sessions;

    /**
     * Constructor
     *
     * @param sessions ClientSessions instance
     */
    RmiLogisticsServices(ClientSessions sessions) {
        this.sessions = sessions;
    }

    @Override
    public List<Building> getBuildings(Token session_token) throws FailedDbFetch, Unauthorised, RemoteException {
        try {
            ClientWrapper client = sessions.getClient(session_token);
            return client.getLogisticsAccess().getBuildings(session_token);
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
    public List<Floor> getFloors(Token session_token, Building building) throws FailedDbFetch, Unauthorised, RemoteException {
        try {
            ClientWrapper client = sessions.getClient(session_token);
            return client.getLogisticsAccess().getFloors(session_token, building);
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
    public List<Room> getRooms(Token session_token, Floor floor) throws FailedDbFetch, Unauthorised, RemoteException {
        try {
            ClientWrapper client = sessions.getClient(session_token);
            return client.getLogisticsAccess().getRooms(session_token, floor);
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
