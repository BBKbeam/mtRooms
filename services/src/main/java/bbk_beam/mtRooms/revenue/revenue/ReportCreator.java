package bbk_beam.mtRooms.revenue.revenue;

import bbk_beam.mtRooms.admin.authentication.Token;
import bbk_beam.mtRooms.db.IReservationDbAccess;
import bbk_beam.mtRooms.db.exception.DbQueryException;
import bbk_beam.mtRooms.db.exception.SessionExpiredException;
import bbk_beam.mtRooms.db.exception.SessionInvalidException;
import bbk_beam.mtRooms.reservation.dto.Room;
import bbk_beam.mtRooms.reservation.exception.FailedDbFetch;
import bbk_beam.mtRooms.revenue.dto.RevenueReport;
import bbk_beam.mtRooms.revenue.exception.InvalidPeriodException;
import eadjlib.datastructure.ObjectTable;
import eadjlib.logger.Logger;

import java.util.Date;

public class ReportCreator implements IRevenueReporter {
    private final Logger log = Logger.getLoggerInstance(ReportCreator.class.getName());
    private RevenueAggregator aggregator;

    /**
     * Checks start and end dates are chronologically set
     *
     * @param from Start timestamp
     * @param to   End timestamp
     * @throws InvalidPeriodException when Start timestamp is after the End timestamp
     */
    private void checkDate(Date from, Date to) throws InvalidPeriodException {
        if (from.after(to)) {
            log.log_Error("Negative period (from > to): ", from, " > ", to);
            throw new InvalidPeriodException("Start date is after end date for period.");
        }
    }

    /**
     * Constructor
     *
     * @param db_access IReservationDbAccess instance
     */
    public ReportCreator(IReservationDbAccess db_access) {
        this.aggregator = new RevenueAggregator(db_access);
    }

    @Override
    public RevenueReport getRevenueReport(Token session_token, Date from, Date to) throws InvalidPeriodException, FailedDbFetch, SessionExpiredException, SessionInvalidException {
        try {
            checkDate(from, to);
            ObjectTable table = this.aggregator.getRevenueData(session_token, from, to);
            return null;
        } catch (DbQueryException e) {
            log.log_Error("");
            throw new FailedDbFetch("", e);
        }
    }

    @Override
    public RevenueReport getRevenueReport(Token session_token, Integer buildingId, Date from, Date to) throws InvalidPeriodException, FailedDbFetch, SessionExpiredException, SessionInvalidException {
        try {
            checkDate(from, to);
            ObjectTable table = this.aggregator.getRevenueData(session_token, buildingId, from, to);
            return null;
        } catch (DbQueryException e) {
            log.log_Error("");
            throw new FailedDbFetch("", e);
        }
    }

    @Override
    public RevenueReport getRevenueReport(Token session_token, Integer buildingId, Integer floorId, Date from, Date to) throws InvalidPeriodException, FailedDbFetch, SessionExpiredException, SessionInvalidException {
        try {
            checkDate(from, to);
            ObjectTable table = this.aggregator.getRevenueData(session_token, buildingId, floorId, from, to);
            return null;
        } catch (DbQueryException e) {
            log.log_Error("");
            throw new FailedDbFetch("", e);
        }
    }

    @Override
    public RevenueReport getRevenueReport(Token session_token, Room room, Date from, Date to) throws InvalidPeriodException, FailedDbFetch, SessionExpiredException, SessionInvalidException {
        try {
            checkDate(from, to);
            ObjectTable table = this.aggregator.getRevenueData(session_token, room, from, to);
            return null;
        } catch (DbQueryException e) {
            log.log_Error("");
            throw new FailedDbFetch("", e);
        }
    }
}
