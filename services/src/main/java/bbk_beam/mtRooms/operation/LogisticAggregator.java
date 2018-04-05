package bbk_beam.mtRooms.operation;

import bbk_beam.mtRooms.admin.authentication.Token;
import bbk_beam.mtRooms.db.IReservationDbAccess;
import bbk_beam.mtRooms.db.TimestampConverter;
import bbk_beam.mtRooms.db.exception.DbQueryException;
import bbk_beam.mtRooms.db.exception.SessionExpiredException;
import bbk_beam.mtRooms.db.exception.SessionInvalidException;
import bbk_beam.mtRooms.reservation.dto.Room;
import eadjlib.datastructure.ObjectTable;
import eadjlib.logger.Logger;

import java.util.Date;

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

    /**
     * Gets all the logistics related booking info for a building
     *
     * @param session_token Session token
     * @param building_id   ID of the building
     * @param from          Start date
     * @param to            End date
     * @return ObjectTable
     * @throws DbQueryException        when a problem was encountered whilst processing the query
     * @throws SessionExpiredException When the session for the id provided has expired
     * @throws SessionInvalidException When the session for the id provided does not exist in the tracker
     */
    ObjectTable getInfo(Token session_token, Integer building_id, Date from, Date to) throws DbQueryException, SessionExpiredException, SessionInvalidException {
        String query = "SELECT " +
                "Room_has_Reservation.building_id, " +
                "Room_has_Reservation.floor_id, " +
                "Room_has_Reservation.room_id, " +
                "Room.room_category_id, " +
                "Room_has_Reservation.timestamp_in, " +
                "Room_has_Reservation.timestamp_out, " +
                "Room_has_Reservation.seated_count, " +
                "RoomCategory.capacity, " +
                "Room_has_Reservation.catering, " +
                "Room_has_Reservation.notes " +
                "FROM Room_has_Reservation " +
                "LEFT OUTER JOIN Room " +
                "ON Room.id = Room_has_Reservation.room_id" +
                " AND Room.floor_id = Room_has_Reservation.floor_id" +
                " AND Room.building_id = Room_has_Reservation.building_id " +
                "LEFT OUTER JOIN RoomCategory " +
                "ON RoomCategory.id = Room.room_category_id " +
                "WHERE Room_has_Reservation.building_id = " + building_id +
                " AND timestamp_in < \"" + TimestampConverter.getUTCTimestampString(to) + "\"" +
                " AND timestamp_out > \"" + TimestampConverter.getUTCTimestampString(from) + "\"";
        return this.db_access.pullFromDB(session_token.getSessionId(), query);
    }

    /**
     * Gets all the logistics related booking info for a floor
     *
     * @param session_token Session token
     * @param building_id   ID of the building
     * @param floor_id      ID of the floor
     * @param from          Start date
     * @param to            End date
     * @return ObjectTable
     * @throws DbQueryException        when a problem was encountered whilst processing the query
     * @throws SessionExpiredException When the session for the id provided has expired
     * @throws SessionInvalidException When the session for the id provided does not exist in the tracker
     */
    ObjectTable getInfo(Token session_token, Integer building_id, Integer floor_id, Date from, Date to) throws DbQueryException, SessionExpiredException, SessionInvalidException {
        String query = "SELECT " +
                "Room_has_Reservation.building_id, " +
                "Room_has_Reservation.floor_id, " +
                "Room_has_Reservation.room_id, " +
                "Room.room_category_id, " +
                "Room_has_Reservation.timestamp_in, " +
                "Room_has_Reservation.timestamp_out, " +
                "Room_has_Reservation.seated_count, " +
                "RoomCategory.capacity, " +
                "Room_has_Reservation.catering, " +
                "Room_has_Reservation.notes " +
                "FROM Room_has_Reservation " +
                "LEFT OUTER JOIN Room " +
                "ON Room.id = Room_has_Reservation.room_id" +
                " AND Room.floor_id = Room_has_Reservation.floor_id" +
                " AND Room.building_id = Room_has_Reservation.building_id " +
                "LEFT OUTER JOIN RoomCategory " +
                "ON RoomCategory.id = Room.room_category_id " +
                "WHERE Room_has_Reservation.floor_id = " + floor_id +
                " AND Room_has_Reservation.building_id = " + building_id +
                " AND timestamp_in < \"" + TimestampConverter.getUTCTimestampString(to) + "\"" +
                " AND timestamp_out > \"" + TimestampConverter.getUTCTimestampString(from) + "\"";
        return this.db_access.pullFromDB(session_token.getSessionId(), query);
    }

    /**
     * Gets all the logistics related booking info for a room
     *
     * @param session_token Session token
     * @param room          Room DTO
     * @param from          Start date
     * @param to            End date
     * @return ObjectTable
     * @throws DbQueryException        when a problem was encountered whilst processing the query
     * @throws SessionExpiredException When the session for the id provided has expired
     * @throws SessionInvalidException When the session for the id provided does not exist in the tracker
     */
    ObjectTable getInfo(Token session_token, Room room, Date from, Date to) throws DbQueryException, SessionExpiredException, SessionInvalidException {
        String query = "SELECT " +
                "Room_has_Reservation.building_id, " +
                "Room_has_Reservation.floor_id, " +
                "Room_has_Reservation.room_id, " +
                "Room.room_category_id, " +
                "Room_has_Reservation.timestamp_in, " +
                "Room_has_Reservation.timestamp_out, " +
                "Room_has_Reservation.seated_count, " +
                "RoomCategory.capacity, " +
                "Room_has_Reservation.catering, " +
                "Room_has_Reservation.notes " +
                "FROM Room_has_Reservation " +
                "LEFT OUTER JOIN Room " +
                "ON Room.id = Room_has_Reservation.room_id" +
                " AND Room.floor_id = Room_has_Reservation.floor_id" +
                " AND Room.building_id = Room_has_Reservation.building_id " +
                "LEFT OUTER JOIN RoomCategory " +
                "ON RoomCategory.id = Room.room_category_id " +
                "WHERE Room_has_Reservation.room_id = " + room.id() +
                " AND Room_has_Reservation.floor_id = " + room.floorID() +
                " AND Room_has_Reservation.building_id = " + room.buildingID() +
                " AND timestamp_in < \"" + TimestampConverter.getUTCTimestampString(to) + "\"" +
                " AND timestamp_out > \"" + TimestampConverter.getUTCTimestampString(from) + "\"";
        return this.db_access.pullFromDB(session_token.getSessionId(), query);
    }
}
