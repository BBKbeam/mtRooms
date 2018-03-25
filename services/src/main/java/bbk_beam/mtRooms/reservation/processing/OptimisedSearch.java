package bbk_beam.mtRooms.reservation.processing;

import bbk_beam.mtRooms.reservation.delegate.ISearch;
import bbk_beam.mtRooms.reservation.dto.Room;
import bbk_beam.mtRooms.reservation.dto.RoomProperty;
import bbk_beam.mtRooms.reservation.scheduling.datastructure.TimeSpan;
import eadjlib.logger.Logger;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class OptimisedSearch {
    private final Logger log = Logger.getLoggerInstance(OptimisedSearch.class.getName());
    private ISearch db_delegate;

    /**
     * Constructor
     *
     * @param search_delegate ISearch instance
     */
    public OptimisedSearch(ISearch search_delegate) {
        this.db_delegate = search_delegate;
    }

    //TODO

    /**
     * Searches for available times for a Room
     *
     * @param room     Room DTO
     * @param from     Beginning timestamp to search from
     * @param to       End timestamp to search up to
     * @param property Minimum requirements for the room
     * @return Availability as a list of time spans where the room is free
     */
    List<TimeSpan> search(Room room, Date from, Date to, RoomProperty property) {
        //TODO
        return null;
    }

    /**
     * Searches for available rooms on a floor within a time frame
     *
     * @param building_id ID of building where the floor is
     * @param floor_id    ID of the floor to search in
     * @param from        Beginning timestamp to search from
     * @param to          End timestamp to search up to
     * @param property    Minimum requirements for the room
     * @return Availability as a Map of the free rooms with their associated list of time spans where the room is free
     */
    HashMap<Room, List<TimeSpan>> search(Integer building_id, Integer floor_id, Date from, Date to, RoomProperty property) {
        //TODO
        return null;
    }

    /**
     * Searches for available rooms on a floor within a time frame
     *
     * @param building_id ID of building to search in
     * @param from        Beginning timestamp to search from
     * @param to          End timestamp to search up to
     * @param property    Minimum requirements for the room
     * @return Availability as a Map of the free rooms with their associated list of time spans where the room is free
     */
    HashMap<Room, List<TimeSpan>> search(Integer building_id, Date from, Date to, RoomProperty property) {
        //TODO
        return null;
    }

    /**
     * Searches for available rooms anywhere
     *
     * @param from     Beginning timestamp to search from
     * @param to       End timestamp to search up to
     * @param property Minimum requirements for the room
     * @return Availability as a Map of the free rooms with their associated list of time spans where the room is free
     */
    HashMap<Room, List<TimeSpan>> search(Date from, Date to, RoomProperty property) {
        //TODO
        return null;
    }
}
