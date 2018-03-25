package bbk_beam.mtRooms.reservation.delegate;

import bbk_beam.mtRooms.admin.authentication.Token;
import bbk_beam.mtRooms.db.exception.DbQueryException;
import bbk_beam.mtRooms.db.exception.SessionExpiredException;
import bbk_beam.mtRooms.db.exception.SessionInvalidException;
import bbk_beam.mtRooms.reservation.dto.Room;
import bbk_beam.mtRooms.reservation.dto.RoomProperty;
import eadjlib.datastructure.ObjectTable;

import java.util.Date;

public interface ISearch {
    /**
     * Searches for available times for a Room
     *
     * @param session_token Session token
     * @param room          Room DTO
     * @param from          Beginning timestamp to search from
     * @param to            End timestamp to search up to
     * @param property      Minimum requirements for the room
     * @return ObjectTable
     * @throws DbQueryException        when a problem was encountered whilst processing the query
     * @throws SessionExpiredException When the session for the id provided has expired
     * @throws SessionInvalidException When the session for the id provided does not exist in the tracker
     */
    ObjectTable search(Token session_token, Room room, Date from, Date to, RoomProperty property) throws DbQueryException, SessionExpiredException, SessionInvalidException;

    /**
     * Searches for available rooms on a floor within a time frame
     *
     * @param session_token Session token
     * @param building_id   ID of building where the floor is
     * @param floor_id      ID of the floor to search in
     * @param from          Beginning timestamp to search from
     * @param to            End timestamp to search up to
     * @param property      Minimum requirements for the room
     * @return ObjectTable
     * @throws DbQueryException        when a problem was encountered whilst processing the query
     * @throws SessionExpiredException When the session for the id provided has expired
     * @throws SessionInvalidException When the session for the id provided does not exist in the tracker
     */
    ObjectTable search(Token session_token, Integer building_id, Integer floor_id, Date from, Date to, RoomProperty property) throws DbQueryException, SessionExpiredException, SessionInvalidException;

    /**
     * Searches for available rooms on a floor within a time frame
     *
     * @param session_token Session token
     * @param building_id   ID of building to search in
     * @param from          Beginning timestamp to search from
     * @param to            End timestamp to search up to
     * @param property      Minimum requirements for the room
     * @return ObjectTable
     * @throws DbQueryException        when a problem was encountered whilst processing the query
     * @throws SessionExpiredException When the session for the id provided has expired
     * @throws SessionInvalidException When the session for the id provided does not exist in the tracker
     */
    ObjectTable search(Token session_token, Integer building_id, Date from, Date to, RoomProperty property) throws DbQueryException, SessionExpiredException, SessionInvalidException;

    /**
     * Searches for available rooms anywhere
     *
     * @param session_token Session token
     * @param from          Beginning timestamp to search from
     * @param to            End timestamp to search up to
     * @param property      Minimum requirements for the room
     * @return ObjectTable
     * @throws DbQueryException        when a problem was encountered whilst processing the query
     * @throws SessionExpiredException When the session for the id provided has expired
     * @throws SessionInvalidException When the session for the id provided does not exist in the tracker
     */
    ObjectTable search(Token session_token, Date from, Date to, RoomProperty property) throws DbQueryException, SessionExpiredException, SessionInvalidException;
}