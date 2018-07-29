package bbk_beam.mtRooms.ui.model.administration;

import bbk_beam.mtRooms.reservation.dto.Room;

public class RoomTreeItem extends InventoryTreeItem {
    private Room room;

    /**
     * Constructor
     *
     * @param room Room DTO
     */
    public RoomTreeItem(Room room) {
        super(String.valueOf(room.id()) + ": " + room.description());
        this.room = room;
    }

    /**
     * Gets the Room DTO
     *
     * @return Room DTO
     */
    public Room getRoom() {
        return this.room;
    }
}
