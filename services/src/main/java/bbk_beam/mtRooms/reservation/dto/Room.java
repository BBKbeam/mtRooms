package bbk_beam.mtRooms.reservation.dto;

public class Room {
    private Integer id;
    private Integer flood_id;
    private Integer building_id;
    private RoomCategory category;

    /**
     * Constructor
     *
     * @param room_id      Room ID
     * @param floor_id     Room's floor ID
     * @param building_id  Room's buidling ID
     * @param roomCategory Price for the reservation of the room
     */
    public Room(Integer room_id,
                Integer floor_id,
                Integer building_id,
                RoomCategory roomCategory) {
        this.id = room_id;
        this.flood_id = floor_id;
        this.building_id = building_id;
        this.category = roomCategory;
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
     * Gets the room's category properties
     *
     * @return Room category
     */
    public RoomCategory category() {
        return this.category;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Room room = (Room) o;

        if (!id.equals(room.id)) return false;
        if (!flood_id.equals(room.flood_id)) return false;
        if (!building_id.equals(room.building_id)) return false;
        return category.equals(room.category);
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + flood_id.hashCode();
        result = 31 * result + building_id.hashCode();
        result = 31 * result + category.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "[" + id + "]={ "
                + "flood_id: " + flood_id
                + ", building_id: " + building_id
                + ", category: " + category
                + " }";
    }
}
