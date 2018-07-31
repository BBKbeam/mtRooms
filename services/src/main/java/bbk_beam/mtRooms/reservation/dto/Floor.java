package bbk_beam.mtRooms.reservation.dto;

import java.io.Serializable;
import java.util.Objects;

public class Floor implements Serializable {
    private Integer building_id;
    private Integer floor_id;
    private String description;

    /**
     * Constructor
     *
     * @param building_id Building ID
     * @param floor_id    Floor ID
     * @param description Floor description
     */
    public Floor(Integer building_id, Integer floor_id, String description) {
        this.building_id = building_id;
        this.floor_id = floor_id;
        this.description = description;
    }

    /**
     * Gets the building ID of the floor
     *
     * @return Building ID
     */
    public Integer buildingID() {
        return this.building_id;
    }

    /**
     * Gets the floor ID
     *
     * @return Floor ID
     */
    public Integer floorID() {
        return this.floor_id;
    }

    /**
     * Gets the floor description
     *
     * @return floor description
     */
    public String description() {
        return this.description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Floor floor = (Floor) o;
        return Objects.equals(building_id, floor.building_id) &&
                Objects.equals(floor_id, floor.floor_id) &&
                Objects.equals(description, floor.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(building_id, floor_id, description);
    }

    @Override
    public String toString() {
        return "Floor[" + building_id + ":" + floor_id + "]={ " + description + " }";
    }
}
