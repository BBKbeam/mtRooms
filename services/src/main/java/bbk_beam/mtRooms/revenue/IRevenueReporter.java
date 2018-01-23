package bbk_beam.mtRooms.revenue;

import bbk_beam.mtRooms.reservation.dto.Room;
import bbk_beam.mtRooms.revenue.exception.InvalidPeriodException;

import java.util.Date;

public interface IRevenueReporter {

    /**
     * Set reporting period
     *
     * @param from reporting start date
     * @param to   reporting end date
     * @throws InvalidPeriodException when from higher than to
     */

// TODO invalidPeriodException
    void getRevenueReport(Date from, Date to) throws InvalidPeriodException;

    /**
     * Filter by building, floor, room
     *
     * @param buildingId filter on building
     * @param floorId filter on floor
     * @param room  filter on room
     * @throws InvalidPeriodException when from higher than to
     */


    void getRevenueReport(Integer buildingId, Date from, Date to) throws InvalidPeriodException;

    void getRevenueReport(Integer buildingId, Integer floorId, Date from, Date to) throws InvalidPeriodException;

    void getRevenueReport(Room room, Date from, Date to) throws InvalidPeriodException;


    void getPendingRevenueReport(Integer buildingId, Date from, Date to) throws InvalidPeriodException;

    void getPendingRevenueReport(Integer buildingId, Integer floorId, Date from, Date to) throws InvalidPeriodException;

    void getPendingRevenueReport(Room room, Date from, Date to) throws InvalidPeriodException;


    void getLostRevenueReport(Integer buildingId, Date from, Date to) throws InvalidPeriodException;

    void getLostRevenueReport(Integer buildingId, Integer floorId, Date from, Date to) throws InvalidPeriodException;

    void getLostRevenueReport(Room room, Date from, Date to) throws InvalidPeriodException;


}
