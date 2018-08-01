package bbk_beam.mtRooms.network;

import bbk_beam.mtRooms.admin.authentication.Token;
import bbk_beam.mtRooms.common.TimeSpan;
import bbk_beam.mtRooms.db.exception.DbQueryException;
import bbk_beam.mtRooms.db.exception.SessionExpiredException;
import bbk_beam.mtRooms.db.exception.SessionInvalidException;
import bbk_beam.mtRooms.network.exception.FailedAllocation;
import bbk_beam.mtRooms.network.exception.Unauthorised;
import bbk_beam.mtRooms.network.session.ClientSessions;
import bbk_beam.mtRooms.network.session.ClientWrapper;
import bbk_beam.mtRooms.reservation.dto.*;
import bbk_beam.mtRooms.reservation.exception.*;
import eadjlib.logger.Logger;
import javafx.util.Pair;

import java.rmi.RemoteException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class RmiReservationServices implements IRmiReservationServices {
    private final Logger log = Logger.getLoggerInstance(RmiReservationServices.class.getName());
    private ClientSessions sessions;

    /**
     * Constructor
     *
     * @param sessions ClientSessions instance
     */
    RmiReservationServices(ClientSessions sessions) {
        this.sessions = sessions;
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
    public void updateReservedRoomNote(Token session_token, Reservation reservation, RoomReservation room_reservation) throws InvalidReservation, FailedDbWrite, Unauthorised, RemoteException {
        try {
            ClientWrapper client = sessions.getClient(session_token);
            client.getReservationAccess().updateReservedRoomNote(session_token, reservation, room_reservation);
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
    public synchronized List<RoomPrice> getRoomPrices(Token session_token, Room room) throws FailedDbFetch, Unauthorised, RemoteException {
        try {
            ClientWrapper client = sessions.getClient(session_token);
            return client.getReservationAccess().getRoomPrices(session_token, room);
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
}
