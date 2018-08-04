package bbk_beam.mtRooms.uaa;

import bbk_beam.mtRooms.admin.authentication.Token;
import bbk_beam.mtRooms.db.exception.SessionExpiredException;
import bbk_beam.mtRooms.db.exception.SessionInvalidException;
import bbk_beam.mtRooms.operation.dto.LogisticsInfo;
import bbk_beam.mtRooms.reservation.dto.Building;
import bbk_beam.mtRooms.reservation.dto.Floor;
import bbk_beam.mtRooms.reservation.dto.Room;
import bbk_beam.mtRooms.reservation.exception.FailedDbFetch;
import bbk_beam.mtRooms.revenue.exception.InvalidPeriodException;

import java.util.Date;
import java.util.List;

public class AuthenticatedLogisticsPersonnel implements IAuthenticatedLogisticsPersonnel {
    private LogisticsPersonnelDelegate delegate;

    /**
     * Constructor
     *
     * @param logisticsPersonnelDelegate LogisticsPersonnelDelegate instance
     */
    AuthenticatedLogisticsPersonnel(LogisticsPersonnelDelegate logisticsPersonnelDelegate) {
        this.delegate = logisticsPersonnelDelegate;
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
    synchronized public LogisticsInfo getInfo(Token session_token, Integer building_id, Date from, Date to) throws InvalidPeriodException, FailedDbFetch, SessionExpiredException, SessionInvalidException {
        return this.delegate.getInfo(session_token, building_id, from, to);
    }

    @Override
    synchronized public LogisticsInfo getInfo(Token session_token, Integer building_id, Integer floor_id, Date from, Date to) throws InvalidPeriodException, FailedDbFetch, SessionExpiredException, SessionInvalidException {
        return this.delegate.getInfo(session_token, building_id, floor_id, from, to);
    }

    @Override
    synchronized public LogisticsInfo getInfo(Token session_token, Room room, Date from, Date to) throws InvalidPeriodException, FailedDbFetch, SessionExpiredException, SessionInvalidException {
        return this.delegate.getInfo(session_token, room, from, to);
    }
}
