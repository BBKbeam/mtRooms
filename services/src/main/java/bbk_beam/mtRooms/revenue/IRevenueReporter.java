package bbk_beam.mtRooms.revenue;

import bbk_beam.mtRooms.admin.authentication.Token;
import bbk_beam.mtRooms.db.exception.SessionExpiredException;
import bbk_beam.mtRooms.db.exception.SessionInvalidException;
import bbk_beam.mtRooms.reservation.dto.Room;
import bbk_beam.mtRooms.reservation.exception.FailedDbFetch;
import bbk_beam.mtRooms.revenue.dto.RevenueReport;
import bbk_beam.mtRooms.revenue.exception.InvalidPeriodException;

import java.util.Date;

public interface IRevenueReporter {
    /**
     * Gets a general revenue summary report in a date range for all booking assets
     *
     * @param session_token Session token
     * @param from          Start of the date range for the report
     * @param to            End of the date range for the report
     * @return Revenue report
     * @throws InvalidPeriodException  when the date period is not valid
     * @throws FailedDbFetch           when an error occurred getting the record
     * @throws SessionExpiredException When the session for the id provided has expired
     * @throws SessionInvalidException When the session for the id provided does not exist in the tracker
     */
    public RevenueReport getRevenueReport(Token session_token, Date from, Date to) throws InvalidPeriodException, FailedDbFetch, SessionExpiredException, SessionInvalidException;

    /**
     * Generate a building specific revenue report
     *
     * @param session_token Session token
     * @param buildingId    ID of the building to generate the report about
     * @param from          Start of the date range for the report
     * @param to            End of the date range for the report
     * @return Revenue report
     * @throws InvalidPeriodException  when the date period is not valid
     * @throws FailedDbFetch           when an error occurred getting the record
     * @throws SessionExpiredException When the session for the id provided has expired
     * @throws SessionInvalidException When the session for the id provided does not exist in the tracker
     */
    public RevenueReport getRevenueReport(Token session_token, Integer buildingId, Date from, Date to) throws InvalidPeriodException, FailedDbFetch, SessionExpiredException, SessionInvalidException;

    ;

    /**
     * Generate a floor-specific revenue report
     *
     * @param session_token Session token
     * @param buildingId    ID of the building to generate the report about
     * @param floorId       ID of the floor to generate the report about
     * @param from          Start of the date range for the report
     * @param to            End of the date range for the report
     * @return Revenue report
     * @throws InvalidPeriodException  when the date period is not valid
     * @throws FailedDbFetch           when an error occurred getting the record
     * @throws SessionExpiredException When the session for the id provided has expired
     * @throws SessionInvalidException When the session for the id provided does not exist in the tracker
     */
    public RevenueReport getRevenueReport(Token session_token, Integer buildingId, Integer floorId, Date from, Date to) throws InvalidPeriodException, FailedDbFetch, SessionExpiredException, SessionInvalidException;

    /**
     * Generate a Room-specific revenue report
     *
     * @param session_token Session token
     * @param room          Specific room to get the revenue report for
     * @param from          Start of the date range for the report
     * @param to            End of the date range for the report
     * @return Revenue report
     * @throws InvalidPeriodException  when the date period is not valid
     * @throws FailedDbFetch           when an error occurred getting the record
     * @throws SessionExpiredException When the session for the id provided has expired
     * @throws SessionInvalidException When the session for the id provided does not exist in the tracker
     */
    public RevenueReport getRevenueReport(Token session_token, Room room, Date from, Date to) throws InvalidPeriodException, FailedDbFetch, SessionExpiredException, SessionInvalidException;
}
