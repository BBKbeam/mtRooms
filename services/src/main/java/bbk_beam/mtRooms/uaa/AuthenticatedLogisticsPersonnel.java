package bbk_beam.mtRooms.uaa;

import bbk_beam.mtRooms.admin.authentication.Token;
import bbk_beam.mtRooms.db.exception.SessionExpiredException;
import bbk_beam.mtRooms.db.exception.SessionInvalidException;
import bbk_beam.mtRooms.operation.dto.LogisticsInfo;
import bbk_beam.mtRooms.reservation.dto.Room;
import bbk_beam.mtRooms.reservation.exception.FailedDbFetch;
import bbk_beam.mtRooms.revenue.exception.InvalidPeriodException;

import java.util.Date;

public class AuthenticatedLogisticsPersonnel implements IAuthenticatedLogisticsPersonnel {
    private LogisticsPersonnelDelegate logisticsPersonnelDelegate;

    /**
     * Constructor
     *
     * @param logisticsPersonnelDelegate LogisticsPersonnelDelegate instance
     */
    AuthenticatedLogisticsPersonnel(LogisticsPersonnelDelegate logisticsPersonnelDelegate) {
        this.logisticsPersonnelDelegate = logisticsPersonnelDelegate;
    }

    @Override
    public LogisticsInfo getInfo(Token session_token, Integer building_id, Date from, Date to) throws InvalidPeriodException, FailedDbFetch, SessionExpiredException, SessionInvalidException {
        return this.logisticsPersonnelDelegate.getInfo(session_token, building_id, from, to);
    }

    @Override
    public LogisticsInfo getInfo(Token session_token, Integer building_id, Integer floor_id, Date from, Date to) throws InvalidPeriodException, FailedDbFetch, SessionExpiredException, SessionInvalidException {
        return this.logisticsPersonnelDelegate.getInfo(session_token, building_id, floor_id, from, to);
    }

    @Override
    public LogisticsInfo getInfo(Token session_token, Room room, Date from, Date to) throws InvalidPeriodException, FailedDbFetch, SessionExpiredException, SessionInvalidException {
        return this.logisticsPersonnelDelegate.getInfo(session_token, room, from, to);
    }
}
