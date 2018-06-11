package bbk_beam.mtRooms.operation.dto;

import bbk_beam.mtRooms.reservation.dto.Room;

import java.util.Objects;

public class OpsRoom extends Room {
    private Integer capacity;

    /**
     * Constructor
     *
     * @param room_id     Room ID
     * @param floor_id    Room's floor ID
     * @param building_id Room's building ID
     * @param category_id Room category ID
     * @param capacity    Room capacity
     */
    public OpsRoom(Integer room_id, Integer floor_id, Integer building_id, String description, Integer category_id, Integer capacity) {
        super(room_id, floor_id, building_id, category_id, description);
        this.capacity = capacity;
    }

    /**
     * Gets the Room's capacity
     *
     * @return Max number of chairs
     */
    public Integer capacity() {
        return this.capacity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        OpsRoom opsRoom = (OpsRoom) o;
        return Objects.equals(capacity, opsRoom.capacity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), capacity);
    }

    @Override
    public String toString() {
        return "[" + this.id() + "]={ "
                + "floor_id: " + this.floorID()
                + ", building_id: " + this.buildingID()
                + ", category_id: " + this.category()
                + ", capacity: " + this.capacity()
                + " }";
    }
}
