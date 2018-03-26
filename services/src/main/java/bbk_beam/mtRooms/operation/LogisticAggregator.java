package bbk_beam.mtRooms.operation;

import bbk_beam.mtRooms.db.IReservationDbAccess;
import eadjlib.logger.Logger;

public class LogisticAggregator {
    private final Logger log = Logger.getLoggerInstance(LogisticAggregator.class.getName());
    private IReservationDbAccess db_access;

    /**
     * Constructor
     *
     * @param reservationDbAccess ReservationDbAccess instance
     */
    public LogisticAggregator(IReservationDbAccess reservationDbAccess) {
        this.db_access = reservationDbAccess;
    }
}
