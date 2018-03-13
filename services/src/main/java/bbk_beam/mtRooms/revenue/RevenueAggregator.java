package bbk_beam.mtRooms.revenue;


import bbk_beam.mtRooms.admin.authentication.Token;
import bbk_beam.mtRooms.db.IReservationDbAccess;
import bbk_beam.mtRooms.db.exception.DbQueryException;
import bbk_beam.mtRooms.db.exception.SessionExpiredException;
import bbk_beam.mtRooms.db.exception.SessionInvalidException;
import bbk_beam.mtRooms.revenue.exception.InvalidPeriodException;
import eadjlib.datastructure.ObjectTable;
import eadjlib.logger.Logger;

import java.util.Date;


/**
 * queries data layer for details on actual revenue, pending revenue and lost revenue
 */

public class RevenueAggregator implements IRevenueAggregator {
    private final Logger log = Logger.getLoggerInstance(RevenueAggregator.class.getName());
    String query = new String();
    private IReservationDbAccess db_access;

    /**
     * @param reservationDbAccess
     */
    public RevenueAggregator(IReservationDbAccess reservationDbAccess) {
        this.db_access = reservationDbAccess;
    }//.constructor

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
     * @param session_token
     * @param buildingID
     * @param from          date from the reporting period starts
     * @param to            date to the reporting period ends
     * @return object table for revenues by buildingID
     * @throws DbQueryException
     * @throws SessionExpiredException
     * @throws SessionInvalidException
     */
    public ObjectTable getRevenueReport(Token session_token, Integer buildingID, Date from, Date to) throws DbQueryException, SessionExpiredException, SessionInvalidException {

        if (checkDate(from, to)) {
            query = ("SELECT " +
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
                    "WHERE Room_has_Reservation.cancelled_flag=0 and Payment.amount NOTNULL"
            );
        }
        ObjectTable table = this.db_access.pullFromDB(session_token.getSessionId(), query);
        if (table.rowCount() == 0) {
            log.log_Warning("No details exist for revenues by buildingID");
            throw new DbQueryException("No details exist for revenues by buildingID");
        }
        return table;
    }

    /**
     * @param session_token
     * @param buildingID
     * @param floorID
     * @param from          date from the reporting period starts
     * @param to            date to the reporting period ends
     * @throws DbQueryException
     * @throws SessionExpiredException
     * @throws SessionInvalidException
     * @returno object table for revenues by floorID
     */
    public ObjectTable getRevenueReport(Token session_token, Integer buildingID, Integer floorID, Date from, Date to) throws DbQueryException, SessionExpiredException, SessionInvalidException {

        if (checkDate(from, to)) {
            query = ("SELECT " +
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
                    "WHERE Room_has_Reservation.cancelled_flag=0 and Payment.amount NOTNULL"
            );
        }

        ObjectTable table = this.db_access.pullFromDB(session_token.getSessionId(), query);
        if (table.rowCount() == 0) {
            log.log_Warning("No details exist for revenues by floorID");
            throw new DbQueryException("No details exist for revenues by buildingID");
        }
        return table;
    }

    /**
     * @param session_token
     * @param buildingID
     * @param floorID
     * @param roomID
     * @param from          date from the reporting period starts
     * @param to            date to the reporting period ends
     * @return object table for revenues by roomID
     * @throws DbQueryException
     * @throws SessionExpiredException
     * @throws SessionInvalidException
     */
    public ObjectTable getRevenueReport(Token session_token, Integer buildingID, Integer floorID, Integer roomID, Date from, Date to) throws DbQueryException, SessionExpiredException, SessionInvalidException {
        if (checkDate(from, to)) {
            query = ("SELECT " +
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
                    "WHERE Room_has_Reservation.cancelled_flag=0 and Payment.amount NOTNULL"
            );
        }

        ObjectTable table = this.db_access.pullFromDB(session_token.getSessionId(), query);
        if (table.rowCount() == 0) {
            log.log_Warning("No details exist for revenues by floorID");
            throw new DbQueryException("No details exist for revenues by floorID");
        }
        return table;
    }

    public ObjectTable getRevenueSummary(Token session_token, Date from, Date to) throws DbQueryException, SessionInvalidException, SessionExpiredException {
        //total by month and by year
        ObjectTable table = this.db_access.pullFromDB(session_token.getSessionId(), query);
        if (table.isEmpty()) {
            log.log_Warning("No revenues listed for the chosen period");
            throw new DbQueryException("No revenues listed for the chosen period");
        }
        return table;
    }


}//.class