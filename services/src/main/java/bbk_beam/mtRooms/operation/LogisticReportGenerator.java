package bbk_beam.mtRooms.operation;

import bbk_beam.mtRooms.admin.authentication.Token;
import bbk_beam.mtRooms.db.TimestampConverter;
import bbk_beam.mtRooms.db.exception.DbQueryException;
import bbk_beam.mtRooms.db.exception.SessionExpiredException;
import bbk_beam.mtRooms.db.exception.SessionInvalidException;
import bbk_beam.mtRooms.operation.dto.LogisticsEntry;
import bbk_beam.mtRooms.operation.dto.LogisticsInfo;
import bbk_beam.mtRooms.operation.dto.OpsRoom;
import bbk_beam.mtRooms.reservation.dto.Room;
import bbk_beam.mtRooms.reservation.exception.FailedDbFetch;
import bbk_beam.mtRooms.revenue.exception.InvalidPeriodException;
import eadjlib.datastructure.ObjectTable;
import eadjlib.logger.Logger;

import java.util.Date;
import java.util.HashMap;

public class LogisticReportGenerator implements ILogisticReportGenerator {
    private final Logger log = Logger.getLoggerInstance(LogisticReportGenerator.class.getName());
    private LogisticAggregator aggregator;

    /**
     * Checker for dates
     *
     * @param from Start date of period
     * @param to   End date of period
     * @return Start is before end state
     */
    private boolean periodIsValid(Date from, Date to) {
        return to.after(from);
    }

    /**
     * Package logistic entries from an ObjectTable to a LogisticInfo container
     *
     * @param table ObjectTable instance
     * @return LogisticInfo container
     */
    private LogisticsInfo processEntries(ObjectTable table) {
        LogisticsInfo entry_container = new LogisticsInfo();
        for (int i = 1; i <= table.rowCount(); i++) {
            HashMap<String, Object> row = table.getRow(i);
            entry_container.addEntry(
                    new LogisticsEntry(
                            new OpsRoom(
                                    (Integer) row.get("room_id"),
                                    (Integer) row.get("floor_id"),
                                    (Integer) row.get("building_id"),
                                    (String) row.get("room_description"),
                                    (Integer) row.get("room_category_id"),
                                    (Integer) row.get("capacity")
                            ),
                            TimestampConverter.getDateObject((String) row.get("timestamp_in")),
                            TimestampConverter.getDateObject((String) row.get("timestamp_out")),
                            (Integer) row.get("seated_count"),
                            ((Integer) row.get("catering") != 0),
                            (String) row.get("notes")
                    )
            );
        }
        return entry_container;
    }

    /**
     * Constructor
     *
     * @param logisticAggregator LogisticAggregator instance
     */
    public LogisticReportGenerator(LogisticAggregator logisticAggregator) {
        this.aggregator = logisticAggregator;
    }

    @Override
    public LogisticsInfo getInfo(Token session_token, Integer building_id, Date from, Date to) throws InvalidPeriodException, FailedDbFetch, SessionExpiredException, SessionInvalidException {
        if (!periodIsValid(from, to)) {
            log.log_Error("Period not valid: ", from, " -> ", to);
            throw new InvalidPeriodException("Period not valid: " + from + " -> " + to);
        }
        try {
            ObjectTable table = this.aggregator.getInfo(session_token, building_id, from, to);
            return processEntries(table);
        } catch (DbQueryException e) {
            log.log_Error("Could not retrieve logistics info for building [", building_id, "] for ", from, " -> ", to);
            throw new FailedDbFetch("Could not retrieve logistics info for building [" + building_id + "] for " + from + " -> " + to, e);
        }
    }

    @Override
    public LogisticsInfo getInfo(Token session_token, Integer building_id, Integer floor_id, Date from, Date to) throws InvalidPeriodException, FailedDbFetch, SessionExpiredException, SessionInvalidException {
        if (!periodIsValid(from, to)) {
            log.log_Error("Period not valid: ", from, " -> ", to);
            throw new InvalidPeriodException("Period not valid: " + from + " -> " + to);
        }
        try {
            ObjectTable table = this.aggregator.getInfo(session_token, building_id, floor_id, from, to);
            return processEntries(table);
        } catch (DbQueryException e) {
            log.log_Error("Could not retrieve logistics info for floor [", building_id, ".", floor_id, "] for ", from, " -> ", to);
            throw new FailedDbFetch("Could not retrieve logistics info for floor [" + building_id + "." + floor_id + "] for " + from + " -> " + to, e);
        }
    }

    @Override
    public LogisticsInfo getInfo(Token session_token, Room room, Date from, Date to) throws InvalidPeriodException, FailedDbFetch, SessionExpiredException, SessionInvalidException {
        if (!periodIsValid(from, to)) {
            log.log_Error("Period not valid: ", from, " -> ", to);
            throw new InvalidPeriodException("Period not valid: " + from + " -> " + to);
        }
        try {
            ObjectTable table = this.aggregator.getInfo(session_token, room, from, to);
            return processEntries(table);
        } catch (DbQueryException e) {
            log.log_Error("Could not retrieve logistics info for Room [", room, "] for ", from, " -> ", to);
            throw new FailedDbFetch("Could not retrieve logistics info for Room [" + room + "] for " + from + " -> " + to, e);
        }
    }
}
