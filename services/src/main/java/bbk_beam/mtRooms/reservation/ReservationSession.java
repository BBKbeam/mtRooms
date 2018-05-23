package bbk_beam.mtRooms.reservation;

import bbk_beam.mtRooms.admin.authentication.Token;
import bbk_beam.mtRooms.db.exception.DbQueryException;
import bbk_beam.mtRooms.db.exception.SessionExpiredException;
import bbk_beam.mtRooms.db.exception.SessionInvalidException;
import bbk_beam.mtRooms.reservation.dto.*;
import bbk_beam.mtRooms.reservation.exception.*;
import bbk_beam.mtRooms.reservation.processing.CustomerAccountAccess;
import bbk_beam.mtRooms.reservation.processing.OptimisedSearch;
import bbk_beam.mtRooms.reservation.processing.PaymentProcessing;
import bbk_beam.mtRooms.reservation.processing.ReservationProcessing;
import bbk_beam.mtRooms.reservation.scheduling.datastructure.TimeSpan;
import eadjlib.logger.Logger;
import javafx.util.Pair;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Observable;

public class ReservationSession implements IReservationSession {
    private final Logger log = Logger.getLoggerInstance(ReservationSession.class.getName());
    private Token token;
    private CustomerAccountAccess customerAccountAccess;
    private PaymentProcessing paymentProcessing;
    private ReservationProcessing reservationProcessing;
    private OptimisedSearch optimisedSearch;
    private IEventWatcher eventWatcher = null;

    /**
     * Constructor
     *
     * @param token                 User's session token
     * @param customerAccountAccess CustomerAccountAccess instance
     * @param paymentProcessing     PaymentProcessing instance
     * @param reservationProcessing ReservationProcessing instance
     * @param optimisedSearch       OptimisedSearch instance
     */
    public ReservationSession(Token token,
                              CustomerAccountAccess customerAccountAccess,
                              PaymentProcessing paymentProcessing,
                              ReservationProcessing reservationProcessing,
                              OptimisedSearch optimisedSearch) {
        this.token = token;
        this.customerAccountAccess = customerAccountAccess;
        this.paymentProcessing = paymentProcessing;
        this.reservationProcessing = reservationProcessing;
        this.optimisedSearch = optimisedSearch;
    }

    /**
     * Gets the User's reservation session token
     *
     * @return Session token
     */
    public Token getToken() {
        return token;
    }

    @Override
    public void update(Observable o, Object arg) {
        if (arg instanceof RoomReservation) {
            log.log_Debug("Observable ", o, " called update: ", arg);
            this.eventWatcher.update(this, (RoomReservation) arg);
        } else {
            log.log_Warning("Update argument unsupported by this <<Observer>>: ", arg);
        }
    }

    @Override
    public IEventWatcher addEventWatcher(IEventWatcher watcher) {
        IEventWatcher previous = this.eventWatcher;
        this.eventWatcher = watcher;
        return previous;
    }

    @Override
    public Customer getCustomerAccount(Token session_token, Integer customerID) throws InvalidCustomer, DbQueryException, SessionExpiredException, SessionInvalidException {
        return this.customerAccountAccess.getCustomerAccount(session_token, customerID);
    }

    @Override
    public Customer getCustomerAccount(Token session_token, Customer customer) throws InvalidCustomer, FailedDbFetch, SessionExpiredException, SessionInvalidException {
        return this.customerAccountAccess.getCustomerAccount(session_token, customer);
    }

    @Override
    public List<Pair<Integer, String>> findCustomer(Token session_token, String surname) throws FailedDbFetch, SessionExpiredException, SessionInvalidException {
        return this.customerAccountAccess.findCustomer(session_token, surname);
    }

    @Override
    public Customer createNewCustomer(Token session_token, Customer customer) throws FailedDbWrite, FailedDbFetch, SessionExpiredException, SessionInvalidException {
        return this.customerAccountAccess.createNewCustomer(session_token, customer);
    }

    @Override
    public void saveCustomerChangesToDB(Token session_token, Customer customer) throws FailedDbWrite, SessionExpiredException, SessionInvalidException {
        this.customerAccountAccess.saveCustomerChangesToDB(session_token, customer);
    }

    @Override
    public Membership getMembership(Token session_token, Integer membership_id) throws InvalidMembership, FailedDbFetch, SessionExpiredException, SessionInvalidException {
        return this.customerAccountAccess.getMembership(session_token, membership_id);
    }

    @Override
    public List<Membership> getMemberships(Token session_token) throws FailedDbFetch, SessionExpiredException, SessionInvalidException {
        return this.customerAccountAccess.getMemberships(session_token);
    }

    @Override
    public List<Room> search(Token session_token, RoomProperty properties) throws FailedDbFetch, SessionExpiredException, SessionInvalidException {
        return this.optimisedSearch.search(session_token, properties);
    }

    @Override
    public List<Room> search(Token session_token, Integer building_id, RoomProperty properties) throws FailedDbFetch, SessionExpiredException, SessionInvalidException {
        return this.optimisedSearch.search(session_token, building_id, properties);
    }

    @Override
    public List<Room> search(Token session_token, Integer building_id, Integer floor_id, RoomProperty properties) throws FailedDbFetch, SessionExpiredException, SessionInvalidException {
        return this.optimisedSearch.search(session_token, building_id, floor_id, properties);
    }

    @Override
    public List<TimeSpan> search(Token session_token, Room room, Date from, Date to) throws FailedDbFetch, SessionExpiredException, SessionInvalidException {
        return this.optimisedSearch.search(session_token, room, from, to);
    }

    @Override
    public HashMap<Room, List<TimeSpan>> search(Token session_token, Integer building_id, Integer floor_id, Date from, Date to, RoomProperty property) throws FailedDbFetch, SessionExpiredException, SessionInvalidException {
        return this.optimisedSearch.search(session_token, building_id, floor_id, from, to, property);
    }

    @Override
    public HashMap<Room, List<TimeSpan>> search(Token session_token, Integer building_id, Date from, Date to, RoomProperty property) throws FailedDbFetch, SessionExpiredException, SessionInvalidException {
        return this.optimisedSearch.search(session_token, building_id, from, to, property);
    }

    @Override
    public HashMap<Room, List<TimeSpan>> search(Token session_token, Date from, Date to, RoomProperty property) throws FailedDbFetch, SessionExpiredException, SessionInvalidException {
        return this.optimisedSearch.search(session_token, from, to, property);
    }

    @Override
    public Double pay(Token session_token, Reservation reservation, Payment payment) throws FailedDbWrite, FailedDbFetch, SessionExpiredException, SessionInvalidException {
        return this.paymentProcessing.pay(session_token, reservation, payment);
    }

    @Override
    public List<Payment> getPayments(Token session_token, Reservation reservation) throws FailedDbFetch, SessionExpiredException, SessionInvalidException {
        return this.paymentProcessing.getPayments(session_token, reservation);
    }

    @Override
    public List<PaymentMethod> getPaymentMethods(Token session_token) throws FailedDbFetch, SessionExpiredException, SessionInvalidException {
        return this.paymentProcessing.getPaymentMethods(session_token);
    }

    @Override
    public Reservation createReservation(Token session_token, Reservation reservation) throws FailedDbWrite, FailedDbFetch, SessionExpiredException, SessionInvalidException {
        return this.reservationProcessing.createReservation(session_token, reservation);
    }

    @Override
    public void createRoomReservation(Token session_token, Reservation reservation, RoomReservation reserved_room) throws InvalidReservation, FailedDbWrite, FailedDbFetch, SessionExpiredException, SessionInvalidException {
        this.reservationProcessing.createRoomReservation(session_token, reservation, reserved_room);
    }

    @Override
    public Double cancelReservation(Token session_token, Reservation reservation) throws InvalidReservation, FailedDbWrite, SessionExpiredException, SessionInvalidException {
        return this.reservationProcessing.cancelReservation(session_token, reservation);
    }

    @Override
    public Double cancelReservedRoom(Token session_token, Reservation reservation, RoomReservation room_reservation) throws InvalidReservation, FailedDbWrite, SessionExpiredException, SessionInvalidException {
        return this.reservationProcessing.cancelReservedRoom(session_token, reservation, room_reservation);
    }

    @Override
    public Reservation getReservation(Token session_token, Integer reservation_id) throws InvalidReservation, FailedDbFetch, SessionExpiredException, SessionInvalidException {
        return this.reservationProcessing.getReservation(session_token, reservation_id);
    }

    @Override
    public List<Reservation> getReservations(Token session_token, Customer customer) throws FailedDbFetch, SessionExpiredException, SessionInvalidException {
        return this.reservationProcessing.getReservations(session_token, customer);
    }

    @Override
    public RoomCategory getRoomCategory(Token session_token, Integer category_id) throws InvalidRoomCategory, FailedDbFetch, SessionExpiredException, SessionInvalidException {
        return this.reservationProcessing.getRoomCategory(session_token, category_id);
    }
}
