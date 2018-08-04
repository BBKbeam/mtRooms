package bbk_beam.mtRooms.operation;

import bbk_beam.mtRooms.admin.authentication.Token;
import bbk_beam.mtRooms.db.TimestampConverter;
import bbk_beam.mtRooms.db.exception.DbQueryException;
import bbk_beam.mtRooms.db.exception.SessionExpiredException;
import bbk_beam.mtRooms.db.exception.SessionInvalidException;
import bbk_beam.mtRooms.operation.dto.LogisticsEntry;
import bbk_beam.mtRooms.operation.dto.LogisticsInfo;
import bbk_beam.mtRooms.operation.dto.OpsRoom;
import bbk_beam.mtRooms.reservation.dto.Building;
import bbk_beam.mtRooms.reservation.dto.Floor;
import bbk_beam.mtRooms.reservation.dto.Room;
import bbk_beam.mtRooms.reservation.exception.FailedDbFetch;
import bbk_beam.mtRooms.revenue.exception.InvalidPeriodException;
import eadjlib.datastructure.ObjectTable;
import eadjlib.logger.Logger;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class LogisticReportGenerator implements ILogisticReportGenerator {
    private final Logger log = Logger.getLoggerInstance(LogisticReportGenerator.class.getName());
    private LogisticAggregator aggregator;

    /**
     * Checks start and end dates are chronologically set
     *
     * @param from Start timestamp
     * @param to   End timestamp
     * @throws InvalidPeriodException when Start timestamp is after the End timestamp
     */
    private void checkPeriod(Date from, Date to) throws InvalidPeriodException {
        if (!to.after(from)) {
            log.log_Error("Invalid period (from > to): ", from, " -> ", to);
            throw new InvalidPeriodException("Start date is after end date for period.");
        }
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
    public List<Building> getBuildings(Token admin_token) throws FailedDbFetch, SessionExpiredException, SessionInvalidException {
        try {
            List<Building> list = new ArrayList<>();
            ObjectTable table = this.aggregator.getBuildings(admin_token);
            if (!table.isEmpty()) {
                for (int i = 1; i <= table.rowCount(); i++) {
                    HashMap<String, Object> row = table.getRow(i);
                    list.add(
                            new Building(
                                    (Integer) row.get("id"),
                                    (String) row.get("name"),
                                    (String) row.get("address1"),
                                    (String) row.get("address2"),
                                    (String) row.get("postcode"),
                                    (String) row.get("city"),
                                    (String) row.get("country"),
                                    (String) row.get("telephone")
                            )
                    );
                }
            }
            return list;
        } catch (DbQueryException e) {
            log.log_Fatal("Could not fetch buildings from records.");
            throw new FailedDbFetch("Could not fetch buildings from records.", e);
        }
    }

    @Override
    public List<Floor> getFloors(Token admin_token, Building building) throws FailedDbFetch, SessionExpiredException, SessionInvalidException {
        try {
            List<Floor> list = new ArrayList<>();
            ObjectTable table = this.aggregator.getFloors(admin_token, building);
            if (!table.isEmpty()) {
                for (int i = 1; i <= table.rowCount(); i++) {
                    HashMap<String, Object> row = table.getRow(i);
                    list.add(
                            new Floor(
                                    (Integer) row.get("building_id"),
                                    (Integer) row.get("id"),
                                    (String) row.get("description")
                            )
                    );
                }
            }
            return list;
        } catch (DbQueryException e) {
            log.log_Fatal("Could not fetch floors for Building[", building.id(), "] from records.");
            throw new FailedDbFetch("Could not fetch floors for Buildings[" + building.id() + "] from records.", e);
        }
    }

    @Override
    public List<Room> getRooms(Token admin_token, Floor floor) throws FailedDbFetch, SessionExpiredException, SessionInvalidException {
        try {
            List<Room> list = new ArrayList<>();
            ObjectTable table = this.aggregator.getRooms(admin_token, floor);
            if (!table.isEmpty()) {
                for (int i = 1; i <= table.rowCount(); i++) {
                    HashMap<String, Object> row = table.getRow(i);
                    list.add(
                            new Room(
                                    (Integer) row.get("id"),
                                    (Integer) row.get("floor_id"),
                                    (Integer) row.get("building_id"),
                                    (Integer) row.get("room_category_id"),
                                    (String) row.get("description")
                            )
                    );
                }
            }
            return list;
        } catch (DbQueryException e) {
            log.log_Fatal("Could not fetch rooms from records for floor: ", floor);
            throw new FailedDbFetch("Could not fetch rooms from records for floor: " + floor, e);
        }
    }

    @Override
    public LogisticsInfo getInfo(Token session_token, Integer building_id, Date from, Date to) throws InvalidPeriodException, FailedDbFetch, SessionExpiredException, SessionInvalidException {
        try {
            checkPeriod(from, to);
            ObjectTable table = this.aggregator.getInfo(session_token, building_id, from, to);
            return processEntries(table);
        } catch (DbQueryException e) {
            log.log_Error("Could not retrieve logistics info for building [", building_id, "] for ", from, " -> ", to);
            throw new FailedDbFetch("Could not retrieve logistics info for building [" + building_id + "] for " + from + " -> " + to, e);
        }
    }

    @Override
    public LogisticsInfo getInfo(Token session_token, Integer building_id, Integer floor_id, Date from, Date to) throws InvalidPeriodException, FailedDbFetch, SessionExpiredException, SessionInvalidException {
        try {
            checkPeriod(from, to);
            ObjectTable table = this.aggregator.getInfo(session_token, building_id, floor_id, from, to);
            return processEntries(table);
        } catch (DbQueryException e) {
            log.log_Error("Could not retrieve logistics info for floor [", building_id, ".", floor_id, "] for ", from, " -> ", to);
            throw new FailedDbFetch("Could not retrieve logistics info for floor [" + building_id + "." + floor_id + "] for " + from + " -> " + to, e);
        }
    }

    @Override
    public LogisticsInfo getInfo(Token session_token, Room room, Date from, Date to) throws InvalidPeriodException, FailedDbFetch, SessionExpiredException, SessionInvalidException {
        try {
            checkPeriod(from, to);
            ObjectTable table = this.aggregator.getInfo(session_token, room, from, to);
            return processEntries(table);
        } catch (DbQueryException e) {
            log.log_Error("Could not retrieve logistics info for Room [", room, "] for ", from, " -> ", to);
            throw new FailedDbFetch("Could not retrieve logistics info for Room [" + room + "] for " + from + " -> " + to, e);
        }
    }
}
