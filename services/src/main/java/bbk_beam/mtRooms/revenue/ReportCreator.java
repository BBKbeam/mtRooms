package bbk_beam.mtRooms.revenue;

import bbk_beam.mtRooms.admin.authentication.Token;
import bbk_beam.mtRooms.db.exception.SessionExpiredException;
import bbk_beam.mtRooms.db.exception.SessionInvalidException;
import bbk_beam.mtRooms.reservation.dto.Room;
import bbk_beam.mtRooms.revenue.dto.RevenueReport;
import bbk_beam.mtRooms.revenue.exception.InvalidPeriodException;

import java.util.Date;

public class ReportCreator implements IRevenueReporter {

    @Override
    public RevenueReport getRevenueReport(Token session_token, Date from, Date to) throws InvalidPeriodException, SessionExpiredException, SessionInvalidException {
        return null;
    }

    @Override
    public RevenueReport getRevenueReport(Token session_token, Integer buildingId, Date from, Date to) throws InvalidPeriodException, SessionExpiredException, SessionInvalidException {
        return null;
    }

    @Override
    public RevenueReport getRevenueReport(Token session_token, Integer buildingId, Integer floorId, Date from, Date to) throws InvalidPeriodException, SessionExpiredException, SessionInvalidException {
        return null;
    }

    @Override
    public RevenueReport getRevenueReport(Token session_token, Room room, Date from, Date to) throws InvalidPeriodException, SessionExpiredException, SessionInvalidException {
        return null;
    }
}
