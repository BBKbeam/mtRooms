package bbk_beam.mtRooms.revenue;


import bbk_beam.mtRooms.admin.authentication.Token;
import bbk_beam.mtRooms.db.IReservationDbAccess;
import bbk_beam.mtRooms.db.exception.DbQueryException;
import bbk_beam.mtRooms.db.exception.SessionExpiredException;
import bbk_beam.mtRooms.db.exception.SessionInvalidException;
import bbk_beam.mtRooms.reservation.dto.Room;
import bbk_beam.mtRooms.revenue.exception.InvalidPeriodException;
import eadjlib.datastructure.ObjectTable;
import eadjlib.logger.Logger;

import java.util.Date;

/**
 * RevenueAggregator
 * <p>
 * Acts as a DB delegate for getting the appropriate data
 * required from the records in order to generate revenue reports
 * </p>
 */
public class RevenueAggregator {
    //TODO Sort out queries so that high report abstraction level = less granularity in query data and vice versa
    private final Logger log = Logger.getLoggerInstance(bbk_beam.mtRooms.revenue.RevenueAggregator.class.getName());
    private IReservationDbAccess db_access;

    //TODO !!! This should not be here. check should be done at the point of entry for the dates i.e. In 'ReportCreator'
    private boolean checkDate(Date from, Date to) {
        try {
            if (to.after(from)) {
            }
            throw new InvalidPeriodException("Check date, from should be an earlier date than to");
        } catch (InvalidPeriodException e) {
            log.log("Check date, from should be an earlier date than to");
        }
        return true;
    }

    /**
     * Constructor
     *
     * @param db_access IReservationDbAccess instance
     */
    public RevenueAggregator(IReservationDbAccess db_access) {
        this.db_access = db_access;
    }

    /**
     * Gets revenue data from records needed to build a summary report
     *
     * @param session_token Session token
     * @param from          Start of the date range for the report
     * @param to            End of the date range for the report
     * @return ObjectTable
     * @throws DbQueryException        when a problem was encountered whilst processing the query
     * @throws SessionExpiredException When the session for the id provided has expired
     * @throws SessionInvalidException When the session for the id provided does not exist in the tracker
     */
    public ObjectTable getRevenueData(Token session_token, Date from, Date to) throws DbQueryException, SessionExpiredException, SessionInvalidException {
        //TODO Query
        //total by month and by year
        String query = "";
        ObjectTable table = this.db_access.pullFromDB(session_token.getSessionId(), query);
        if (table.isEmpty()) {
            log.log_Warning("No revenues listed for the chosen period");
            throw new DbQueryException("No revenues listed for the chosen period");
        }
        return table;
    }

    /**
     * Gets revenue data from records needed to build a building-specific report
     *
     * @param session_token Session token
     * @param building_id   ID of the subject building of the report
     * @param from          Start of the date range for the report
     * @param to            End of the date range for the report
     * @return ObjectTable
     * @throws DbQueryException        when a problem was encountered whilst processing the query
     * @throws SessionExpiredException When the session for the id provided has expired
     * @throws SessionInvalidException When the session for the id provided does not exist in the tracker
     */
    public ObjectTable getRevenueData(Token session_token, Integer building_id, Date from, Date to) throws DbQueryException, SessionExpiredException, SessionInvalidException {
        //TODO argument injection into the query to actually get the records we want
        String query = "SELECT " +
                "Room_has_Reservation.reservation_id," +
                "Room_has_Reservation.cancelled_flag," +
                "Room_has_Reservation.building_id," +
                "Reservation_has_Payment.payment_id," +
                "Payment.amount," +
                "strftime('%m', Payment.timestamp) as month," +
                "strftime('%Y', Payment.timestamp) as year" +
                "FROM Room_has_Reservation" +
                "LEFT OUTER JOIN Reservation_has_Payment ON Room_has_Reservation.reservation_id = Reservation_has_Payment.reservation_id" +
                "LEFT OUTER JOIN Payment on Payment.id = Reservation_has_Payment.payment_id" +
                "WHERE Room_has_Reservation.cancelled_flag=0 and Payment.amount NOTNULL";
        ObjectTable table = this.db_access.pullFromDB(session_token.getSessionId(), query);
        if (table.isEmpty()) {
            log.log_Warning("No details exist for revenues by buildingID");
            throw new DbQueryException("No details exist for revenues by buildingID");
        }
        return table;
    }

    /**
     * Gets revenue data from records needed to build a floor-specific report
     *
     * @param session_token Session token
     * @param building_id   ID of the subject building of the report
     * @param floor_id      ID of the subject floor of the report
     * @param from          Start of the date range for the report
     * @param to            End of the date range for the report
     * @return ObjectTable
     * @throws DbQueryException        when a problem was encountered whilst processing the query
     * @throws SessionExpiredException When the session for the id provided has expired
     * @throws SessionInvalidException When the session for the id provided does not exist in the tracker
     */
    public ObjectTable getRevenueData(Token session_token, Integer building_id, Integer floor_id, Date from, Date to) throws DbQueryException, SessionExpiredException, SessionInvalidException {
        //TODO argument injection into the query to actually get the records we want
        String query = "SELECT " +
                "Room_has_Reservation.reservation_id," +
                "Room_has_Reservation.cancelled_flag," +
                "Room_has_Reservation.building_id," +
                "Room_has_Reservation.floor_id," +
                "Reservation_has_Payment.payment_id," +
                "strftime('%m', Payment.timestamp) as month," +
                "strftime('%Y', Payment.timestamp) as year" +
                "FROM Room_has_Reservation" +
                "LEFT OUTER JOIN Reservation_has_Payment ON Room_has_Reservation.reservation_id = Reservation_has_Payment.reservation_id" +
                "LEFT OUTER JOIN Payment on Payment.id = Reservation_has_Payment.payment_id" +
                "WHERE Room_has_Reservation.cancelled_flag=0 and Payment.amount NOTNULL";
        ObjectTable table = this.db_access.pullFromDB(session_token.getSessionId(), query);
        if (table.isEmpty()) {
            log.log_Warning("No details exist for revenues by floorID");
            throw new DbQueryException("No details exist for revenues by buildingID");
        }
        return table;
    }

    /**
     * Gets revenue data from records needed to build a room-specific report
     *
     * @param session_token Session token
     * @param room          Room DTO
     * @param from          Start of the date range for the report
     * @param to            End of the date range for the report
     * @return ObjectTable
     * @throws DbQueryException        when a problem was encountered whilst processing the query
     * @throws SessionExpiredException When the session for the id provided has expired
     * @throws SessionInvalidException When the session for the id provided does not exist in the tracker
     */
    public ObjectTable getRevenueData(Token session_token, Room room, Date from, Date to) throws DbQueryException, SessionExpiredException, SessionInvalidException {
        //TODO argument injection into the query to actually get the records we want
        String query = "SELECT " +
                "Room_has_Reservation.reservation_id," +
                "Room_has_Reservation.cancelled_flag," +
                "Room_has_Reservation.building_id," +
                "Room_has_Reservation.floor_id," +
                "Room_has_Reservation.room_id," +
                "Reservation_has_Payment.payment_id," +
                "strftime('%m', Payment.timestamp) as month," +
                "strftime('%Y', Payment.timestamp) as year" +
                "FROM Room_has_Reservation" +
                "LEFT OUTER JOIN Reservation_has_Payment ON Room_has_Reservation.reservation_id = Reservation_has_Payment.reservation_id" +
                "LEFT OUTER JOIN Payment on Payment.id = Reservation_has_Payment.payment_id" +
                "WHERE Room_has_Reservation.cancelled_flag=0 and Payment.amount NOTNULL";
        ObjectTable table = this.db_access.pullFromDB(session_token.getSessionId(), query);
        if (table.rowCount() == 0) {
            log.log_Warning("No details exist for revenues by floorID");
            throw new DbQueryException("No details exist for revenues by floorID");
        }
        return table;
    }
}