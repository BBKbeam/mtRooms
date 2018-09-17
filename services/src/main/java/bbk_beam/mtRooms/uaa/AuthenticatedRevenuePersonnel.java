package bbk_beam.mtRooms.uaa;

import bbk_beam.mtRooms.admin.authentication.Token;
import bbk_beam.mtRooms.db.exception.SessionExpiredException;
import bbk_beam.mtRooms.db.exception.SessionInvalidException;
import bbk_beam.mtRooms.reservation.dto.Building;
import bbk_beam.mtRooms.reservation.dto.Customer;
import bbk_beam.mtRooms.reservation.dto.Floor;
import bbk_beam.mtRooms.reservation.dto.Room;
import bbk_beam.mtRooms.reservation.exception.FailedDbFetch;
import bbk_beam.mtRooms.reservation.exception.InvalidCustomer;
import bbk_beam.mtRooms.reservation.exception.InvalidReservation;
import bbk_beam.mtRooms.revenue.dto.*;
import bbk_beam.mtRooms.revenue.exception.InvalidPeriodException;

import java.util.Date;
import java.util.List;

public class AuthenticatedRevenuePersonnel implements IAuthenticatedRevenuePersonnel {
    private RevenuePersonnelDelegate delegate;

    /**
     * Constructor
     *
     * @param delegate RevenuePersonnelDelegate instance
     */
    public AuthenticatedRevenuePersonnel(RevenuePersonnelDelegate delegate) {
        this.delegate = delegate;
    }

    @Override
    public List<Building> getBuildings(Token session_token) throws FailedDbFetch, SessionExpiredException, SessionInvalidException {
        return this.delegate.getBuildings(session_token);
    }

    @Override
    public List<Floor> getFloors(Token session_token, Building building) throws FailedDbFetch, SessionExpiredException, SessionInvalidException {
        return this.delegate.getFloors(session_token, building);
    }

    @Override
    public List<Room> getRooms(Token session_token, Floor floor) throws FailedDbFetch, SessionExpiredException, SessionInvalidException {
        return this.delegate.getRooms(session_token, floor);
    }

    @Override
    public List<SimpleCustomerBalance> getCustomerBalance(Token session_token) throws FailedDbFetch, SessionExpiredException, SessionInvalidException {
        return this.delegate.getCustomerBalance(session_token);
    }

    @Override
    public CustomerBalance getCustomerBalance(Token session_token, Customer customer) throws FailedDbFetch, SessionExpiredException, SessionInvalidException {
        return this.delegate.getCustomerBalance(session_token, customer);
    }

    @Override
    public Occupancy getOccupancy(Token session_token, Date from, Date to) throws InvalidPeriodException, FailedDbFetch, SessionExpiredException, SessionInvalidException {
        return this.delegate.getOccupancy(session_token, from, to);
    }

    @Override
    public Occupancy getOccupancy(Token session_token, Building building, Date from, Date to) throws InvalidPeriodException, FailedDbFetch, SessionExpiredException, SessionInvalidException {
        return this.delegate.getOccupancy(session_token, building, from, to);
    }

    @Override
    public Occupancy getOccupancy(Token session_token, Floor floor, Date from, Date to) throws InvalidPeriodException, FailedDbFetch, SessionExpiredException, SessionInvalidException {
        return this.delegate.getOccupancy(session_token, floor, from, to);
    }

    @Override
    public Occupancy getOccupancy(Token session_token, Room room, Date from, Date to) throws InvalidPeriodException, FailedDbFetch, SessionExpiredException, SessionInvalidException {
        return this.delegate.getOccupancy(session_token, room, from, to);
    }

    @Override
    public Invoice createInvoice(Token session_token, Integer reservation_id) throws InvalidReservation, InvalidCustomer, FailedDbFetch, SessionExpiredException, SessionInvalidException {
        return this.delegate.createInvoice(session_token, reservation_id);
    }

    @Override
    public List<DetailedPayment> getPayments(Token session_token, Date from, Date to) throws InvalidPeriodException, FailedDbFetch, SessionExpiredException, SessionInvalidException {
        return this.delegate.getPayments(session_token, from, to);
    }
}
