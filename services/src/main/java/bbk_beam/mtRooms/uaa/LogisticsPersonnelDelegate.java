package bbk_beam.mtRooms.uaa;

import bbk_beam.mtRooms.admin.authentication.Token;
import bbk_beam.mtRooms.db.exception.SessionExpiredException;
import bbk_beam.mtRooms.db.exception.SessionInvalidException;
import bbk_beam.mtRooms.operation.ILogisticReportGenerator;
import bbk_beam.mtRooms.operation.dto.LogisticsInfo;
import bbk_beam.mtRooms.reservation.dto.Room;
import bbk_beam.mtRooms.reservation.exception.FailedDbFetch;
import bbk_beam.mtRooms.revenue.exception.InvalidPeriodException;
import eadjlib.logger.Logger;

import java.util.Date;

public class LogisticsPersonnelDelegate {
    private final Logger log = Logger.getLoggerInstance(LogisticsPersonnelDelegate.class.getName());
    private ILogisticReportGenerator logisticReportGenerator;

    /**
     * Constructor
     *
     * @param logisticReportGenerator ILogisticReportGenerator instance
     */
    LogisticsPersonnelDelegate(ILogisticReportGenerator logisticReportGenerator) {
        this.logisticReportGenerator = logisticReportGenerator;
    }

    /**
     * Gets logistical information
     *
     * @param session_token Session token
     * @param building_id   ID of the building
     * @param from          From date
     * @param to            To date
     * @return Logistics information report
     * @throws InvalidPeriodException  when the date period is not valid
     * @throws FailedDbFetch           when an error occurred getting the record
     * @throws SessionExpiredException When the session for the id provided has expired
     * @throws SessionInvalidException When the session for the id provided does not exist in the tracker
     */
    public LogisticsInfo getInfo(Token session_token, Integer building_id, Date from, Date to) throws InvalidPeriodException, FailedDbFetch, SessionExpiredException, SessionInvalidException {
        return this.logisticReportGenerator.getInfo(session_token, building_id, from, to);
    }

    /**
     * Gets logistical information
     *
     * @param session_token Session token
     * @param building_id   ID of the building
     * @param floor_id      ID of the floor
     * @param from          From date
     * @param to            To date
     * @return Logistics information report
     * @throws InvalidPeriodException  when the date period is not valid
     * @throws FailedDbFetch           when an error occurred getting the record
     * @throws SessionExpiredException When the session for the id provided has expired
     * @throws SessionInvalidException When the session for the id provided does not exist in the tracker
     */
    public LogisticsInfo getInfo(Token session_token, Integer building_id, Integer floor_id, Date from, Date to) throws InvalidPeriodException, FailedDbFetch, SessionExpiredException, SessionInvalidException {
        return this.logisticReportGenerator.getInfo(session_token, building_id, floor_id, from, to);
    }

    /**
     * Gets logistical information
     *
     * @param session_token Session token
     * @param room          Room DTO
     * @param from          From date
     * @param to            To date
     * @return Logistics information report
     * @throws InvalidPeriodException  when the date period is not valid
     * @throws FailedDbFetch           when an error occurred getting the record
     * @throws SessionExpiredException When the session for the id provided has expired
     * @throws SessionInvalidException When the session for the id provided does not exist in the tracker
     */
    public LogisticsInfo getInfo(Token session_token, Room room, Date from, Date to) throws InvalidPeriodException, FailedDbFetch, SessionExpiredException, SessionInvalidException {
        return this.logisticReportGenerator.getInfo(session_token, room, from, to);
    }
}