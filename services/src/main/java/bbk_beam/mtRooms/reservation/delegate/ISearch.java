package bbk_beam.mtRooms.reservation.delegate;

import bbk_beam.mtRooms.reservation.dto.Room;
import bbk_beam.mtRooms.reservation.dto.RoomProperty;
import eadjlib.datastructure.ObjectTable;

import java.util.Date;

public interface ISearch {
    /**
     * Searches for available times for a Room
     *
     * @param room     Room DTO
     * @param from     Beginning timestamp to search from
     * @param to       End timestamp to search up to
     * @param property Minimum requirements for the room
     * @return ObjectTable
     */
    ObjectTable search(Room room, Date from, Date to, RoomProperty property);

    /**
     * Searches for available rooms on a floor within a time frame
     *
     * @param building_id ID of building where the floor is
     * @param floor_id    ID of the floor to search in
     * @param from        Beginning timestamp to search from
     * @param to          End timestamp to search up to
     * @param property    Minimum requirements for the room
     * @return ObjectTable
     */
    ObjectTable search(Integer building_id, Integer floor_id, Date from, Date to, RoomProperty property);

    /**
     * Searches for available rooms on a floor within a time frame
     *
     * @param building_id ID of building to search in
     * @param from        Beginning timestamp to search from
     * @param to          End timestamp to search up to
     * @param property    Minimum requirements for the room
     * @return ObjectTable
     */
    ObjectTable search(Integer building_id, Date from, Date to, RoomProperty property);

    /**
     * Searches for available rooms anywhere
     *
     * @param from     Beginning timestamp to search from
     * @param to       End timestamp to search up to
     * @param property Minimum requirements for the room
     * @return ObjectTable
     */
    ObjectTable search(Date from, Date to, RoomProperty property);
}