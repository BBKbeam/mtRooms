package bbk_beam.mtRooms.reservation.dto;

import java.util.Objects;

public class Room {
    private Integer id;
    private Integer flood_id;
    private Integer building_id;
    private Integer category_id;

    /**
     * Constructor
     *
     * @param room_id     Room ID
     * @param floor_id    Room's floor ID
     * @param building_id Room's building ID
     * @param category_id Room category ID
     */
    public Room(Integer room_id,
                Integer floor_id,
                Integer building_id,
                Integer category_id) {
        this.id = room_id;
        this.flood_id = floor_id;
        this.building_id = building_id;
        this.category_id = category_id;
    }

    /**
     * Gets the room's ID
     *
     * @return room ID
     */
    public Integer id() {
        return this.id;
    }

    /**
     * Gets the room's floor ID
     *
     * @return floor ID
     */
    public Integer floorID() {
        return this.flood_id;
    }

    /**
     * Gets the room's building ID
     *
     * @return Building ID
     */
    public Integer buildingID() {
        return this.building_id;
    }

    /**
     * Gets the room's category ID
     *
     * @return Room category ID
     */
    public Integer category() {
        return this.category_id;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Room room = (Room) o;
        return Objects.equals(id, room.id) &&
                Objects.equals(flood_id, room.flood_id) &&
                Objects.equals(building_id, room.building_id) &&
                Objects.equals(category_id, room.category_id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, flood_id, building_id, category_id);
    }

    @Override
    public String toString() {
        return "[" + id + "]={ "
                + "flood_id: " + flood_id
                + ", building_id: " + building_id
                + ", category_id: " + category_id
                + " }";
    }
}
