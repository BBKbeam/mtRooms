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
     * Searches for booked times for a Room
     *
     * @param session_token Session token
     * @param room          Room DTO
     * @param from          Beginning timestamp to search from
     * @param to            End timestamp to search up to
     * @return ObjectTable
     * @throws DbQueryException        when a problem was encountered whilst processing the query
     * @throws SessionExpiredException When the session for the id provided has expired
     * @throws SessionInvalidException When the session for the id provided does not exist in the tracker
     */
    ObjectTable search(Token session_token, Room room, Date from, Date to) throws DbQueryException, SessionExpiredException, SessionInvalidException;

    /**
     * Searches for booked rooms on a floor within a time frame
     *
     * @param session_token Session token
     * @param building_id   ID of building where the floor is
     * @param floor_id      ID of the floor to search in
     * @param from          Beginning timestamp to search from
     * @param to            End timestamp to search up to
     * @return ObjectTable
     * @throws DbQueryException        when a problem was encountered whilst processing the query
     * @throws SessionExpiredException When the session for the id provided has expired
     * @throws SessionInvalidException When the session for the id provided does not exist in the tracker
     */
    ObjectTable search(Token session_token, Integer building_id, Integer floor_id, Date from, Date to) throws DbQueryException, SessionExpiredException, SessionInvalidException;

    /**
     * Searches for booked rooms on a floor within a time frame
     *
     * @param session_token Session token
     * @param building_id   ID of building to search in
     * @param from          Beginning timestamp to search from
     * @param to            End timestamp to search up to
     * @return ObjectTable
     * @throws DbQueryException        when a problem was encountered whilst processing the query
     * @throws SessionExpiredException When the session for the id provided has expired
     * @throws SessionInvalidException When the session for the id provided does not exist in the tracker
     */
    ObjectTable search(Token session_token, Integer building_id, Date from, Date to) throws DbQueryException, SessionExpiredException, SessionInvalidException;

    /**
     * Searches for booked rooms anywhere
     *
     * @param session_token Session token
     * @param from          Beginning timestamp to search from
     * @param to            End timestamp to search up to
     * @return ObjectTable
     * @throws DbQueryException        when a problem was encountered whilst processing the query
     * @throws SessionExpiredException When the session for the id provided has expired
     * @throws SessionInvalidException When the session for the id provided does not exist in the tracker
     */
    ObjectTable search(Token session_token, Date from, Date to) throws DbQueryException, SessionExpiredException, SessionInvalidException;

    /**
     * Searches for any Rooms that match the properties given
     *
     * @param session_token Session token
     * @param properties    Room properties to look for
     * @return ObjectTable
     * @throws DbQueryException        when a problem was encountered whilst processing the query
     * @throws SessionExpiredException When the session for the id provided has expired
     * @throws SessionInvalidException When the session for the id provided does not exist in the tracker
     */
    ObjectTable search(Token session_token, RoomProperty properties) throws DbQueryException, SessionExpiredException, SessionInvalidException;

    /**
     * Searches for Rooms that match the properties given
     *
     * @param session_token Session token
     * @param building_id   ID of the building to search in
     * @param properties    Room properties to look for
     * @return ObjectTable
     * @throws DbQueryException        when a problem was encountered whilst processing the query
     * @throws SessionExpiredException When the session for the id provided has expired
     * @throws SessionInvalidException When the session for the id provided does not exist in the tracker
     */
    ObjectTable search(Token session_token, Integer building_id, RoomProperty properties) throws DbQueryException, SessionExpiredException, SessionInvalidException;

    /**
     * Searches for Rooms that match the properties given
     *
     * @param session_token Session token
     * @param building_id   ID of the building to search in
     * @param floor_id      ID of the floor to search in
     * @param properties    Room properties to look for
     * @return ObjectTable
     * @throws DbQueryException        when a problem was encountered whilst processing the query
     * @throws SessionExpiredException When the session for the id provided has expired
     * @throws SessionInvalidException When the session for the id provided does not exist in the tracker
     */
    ObjectTable search(Token session_token, Integer building_id, Integer floor_id, RoomProperty properties) throws DbQueryException, SessionExpiredException, SessionInvalidException;
}