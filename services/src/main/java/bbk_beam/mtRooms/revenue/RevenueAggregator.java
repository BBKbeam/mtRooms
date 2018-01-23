package bbk_beam.mtRooms.revenue;

import bbk_beam.mtRooms.reservation.dto.Room;
import bbk_beam.mtRooms.revenue.exception.InvalidPeriodException;
import eadjlib.logger.Logger;

import java.util.Date;

public class RevenueAggregator implements IRevenueReporter {
    private final Logger log = Logger.getLoggerInstance(RevenueAggregator.class.getName());
    private Date from;
    private Date to;


// create a Connection and check that the mtRooms.db exists
// create an instance of the Statement class
// create an instance of the ResultSet class by calling Statement.executeQuery() > accepts SELECT statement
// output the ResultSet.next().getString()


    @Override
    public void getRevenueReport(Date from, Date to) {
        try {
            if (to.after(from)) {
                this.from = from;
                this.to = to;
            }
            throw new InvalidPeriodException("Date TO should be after date FROM");
        } catch (InvalidPeriodException e) {
            log.log("Date TO should be after date FROM ");
        }

    }

    // lists payments


    @Override
    public void getRevenueReport(Integer buildingId, Date from, Date to) {

    }

    @Override
    public void getRevenueReport(Integer buildingId, Integer floorId, Date from, Date to) {

    }

    @Override
    public void getRevenueReport(Room room, Date from, Date to) {

        /**
         SELECT
         Room_has_Reservation.reservation_id ,
         Room_has_Reservation.room_id,
         Room_has_Reservation.floor_id,
         Room_has_Reservation.building_id,
         Reservation_has_Payment.payment_id,
         Payment.amount,
         strftime('%m', Payment.timestamp) AS month,
         strftime('%Y', Payment.timestamp) AS year

         FROM Room_has_Reservation
         LEFT OUTER JOIN Reservation_has_Payment
         ON Room_has_Reservation.reservation_id = Reservation_has_Payment.reservation_id
         LEFT OUTER JOIN Payment
         ON Reservation_has_Payment.payment_id = Payment.id
         WHERE Room_has_Reservation.cancelled_flag = 0 and Payment.amount notnull;

         */
    }

    // lists pending payments

    @Override
    public void getPendingRevenueReport(Integer buildingId, Date from, Date to) {

    }

    @Override
    public void getPendingRevenueReport(Integer buildingId, Integer floorId, Date from, Date to) {

    }

    @Override
    public void getPendingRevenueReport(Room room, Date from, Date to) {

        // WHERE Room_has_Reservation.cancelled_flag = 0 and Payment.amount is null;

    }


    // lists cancelled reservations
    @Override
    public void getLostRevenueReport(Integer buildingId, Date from, Date to) {

    }

    @Override
    public void getLostRevenueReport(Integer buildingId, Integer floorId, Date from, Date to) {

    }

    @Override
    public void getLostRevenueReport(Room room, Date from, Date to) {

        /**
         SELECT
         Room_has_Reservation.reservation_id,
         Room_has_Reservation.room_id,
         Room_has_Reservation.floor_id,
         Room_has_Reservation.building_id,
         Room_has_Reservation.timestamp_in,
         Room_has_Reservation.timestamp_out,
         Room_has_Reservation.cancelled_flag,
         RoomPrice.price,
         RoomPrice.year

         FROM Room_has_Reservation

         LEFT OUTER JOIN Room_has_RoomPrice
         ON Room_has_RoomPrice.room_id = Room_has_Reservation.room_id
         LEFT OUTER JOIN RoomPrice
         ON Room_has_RoomPrice.price_id = RoomPrice.id

         WHERE Room_has_Reservation.cancelled_flag=1 AND RoomPrice.year = strftime('%Y',Room_has_Reservation.timestamp_in)
         ;
         */

        // TODO
        /**
         * query should throw an error if Room_has_Reservation year (at the time of reservation > timestamp_in)
         * can not be found in RoomPrice year
         */

    }


}
