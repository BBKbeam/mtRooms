package bbk_beam.mtRooms.revenue.dto;

import bbk_beam.mtRooms.reservation.dto.Room;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * Container for a room's occupancy
 */
public class RoomOccupancy implements Serializable {
    private Room room;
    private List<ReservationSlot> occupancy;

    /**
     * Constructor
     *
     * @param room Room DTO
     */
    public RoomOccupancy(Room room) {
        this.room = room;
        this.occupancy = new LinkedList<>();
    }

    /**
     * Adds a ReservationSlot to the occupancy list
     *
     * @param reservationSlot ReservationSlot instance
     */
    public void add(ReservationSlot reservationSlot) {
        this.occupancy.add(reservationSlot);
    }

    /**
     * Gets the Room DTO
     *
     * @return Room DTO
     */
    public Room getRoom() {
        return this.room;
    }

    /**
     * Gets the ReservationSlot list
     *
     * @return ReservationSlot list
     */
    public List<ReservationSlot> getOccupancy() {
        return this.occupancy;
    }

    @Override
    public String toString() {
        return "RoomOccupancy{ " + "room=" + room +
                ", occupancy=" + occupancy +
                " }";
    }
}
