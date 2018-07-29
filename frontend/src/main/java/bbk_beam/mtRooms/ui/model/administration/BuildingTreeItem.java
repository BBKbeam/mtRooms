package bbk_beam.mtRooms.ui.model.administration;

import bbk_beam.mtRooms.reservation.dto.Building;

public class BuildingTreeItem extends InventoryTreeItem {
    private Building building;

    /**
     * Constructor
     *
     * @param building Building DTO
     */
    public BuildingTreeItem(Building building) {
        super(String.valueOf(building.id()) + ": " + building.name());
        this.building = building;
    }

    /**
     * Gets the Building DTO
     *
     * @return Building DTO
     */
    public Building getBuilding() {
        return building;
    }
}
