package bbk_beam.mtRooms.revenue.revenue;

import bbk_beam.mtRooms.reservation.dto.Room;
import bbk_beam.mtRooms.revenue.exception.InvalidPeriodException;
import bbk_beam.mtRooms.revenue.exception.NothingToReportException;
import eadjlib.datastructure.ObjectTable;

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
    void reportingPeriod(Date from, Date to) throws InvalidPeriodException;

    /**
     * Filter reporting by building, floor, room
     *
     * @param buildingName     filter on building
     * @param floorDescription filter on floor
     * @param roomDescription  filter on room
     * @throws .... when ...
     */

    void filterBy(Building buildingName, Floor floorDescription, Room roomDescription);


    /**
     * Output  reservations, cancellations, revenue
     *
     * @param reservations  filter  number of reservations
     * @param cancellations filter cancellations
     * @param revenue       filter by net amount
     * @throws NothingToReportException when no data exists
     */
// TODO NothingToReportException
    ObjectTable getResults(Integer reservations, Integer cancellations, Integer revenue) throws NothingToReportException;

}
