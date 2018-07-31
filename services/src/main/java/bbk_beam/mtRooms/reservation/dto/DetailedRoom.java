package bbk_beam.mtRooms.reservation.dto;

import java.io.Serializable;
import java.util.Objects;

/**
 * Detailed Room description
 */
public class DetailedRoom implements Serializable {
    private Room room;
    private Floor floor;
    private Building building;
    private RoomCategory category;
    private RoomFixtures fixtures;

    /**
     * Constructor
     *
     * @param building Building DTO
     * @param floor    Floor DTO
     * @param room     Room DTO
     * @param category RoomCategory DTO
     * @param fixtures RoomFixtures DTO
     */
    public DetailedRoom(Building building, Floor floor, Room room, RoomCategory category, RoomFixtures fixtures) {
        this.building = building;
        this.floor = floor;
        this.room = room;
        this.category = category;
        this.fixtures = fixtures;
    }

    /**
     * Gets the building for the room
     *
     * @return Building DTO
     */
    public Building building() {
        return this.building;
    }

    /**
     * Gets the floor for the room
     *
     * @return Floor DTO
     */
    public Floor floor() {
        return this.floor;
    }

    /**
     * Gets the room DTO
     *
     * @return Room DTO
     */
    public Room room() {
        return this.room;
    }

    /**
     * Gets the RoomFixtures DTO
     *
     * @return RoomFixtures DTO
     */
    public RoomFixtures fixtures() {
        return this.fixtures;
    }

    /**
     * Gets the category of the room
     *
     * @return RoomCategory DTO
     */
    public RoomCategory category() {
        return this.category;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DetailedRoom that = (DetailedRoom) o;
        return Objects.equals(room, that.room) &&
                Objects.equals(floor, that.floor) &&
                Objects.equals(building, that.building) &&
                Objects.equals(category, that.category) &&
                Objects.equals(fixtures, that.fixtures);
    }

    @Override
    public int hashCode() {
        return Objects.hash(room, floor, building, category, fixtures);
    }

    @Override
    public String toString() {
        return "DetailedRoom{" + building + ", " + floor + ", " + room + ", " + category + ", " + fixtures + " }";
    }
}
