package bbk_beam.mtRooms.ui.model.administration;

import bbk_beam.mtRooms.reservation.dto.Floor;

public class FloorTreeItem extends InventoryTreeItem {
    private Floor floor;

    /**
     * Constructor
     *
     * @param floor Floor DTO
     */
    public FloorTreeItem(Floor floor) {
        super(String.valueOf(floor.floorID()) + ": " + floor.description());
        this.floor = floor;
    }

    /**
     * Gets the Floor DTO
     *
     * @return Floor DTO
     */
    public Floor getFloor() {
        return this.floor;
    }
}
