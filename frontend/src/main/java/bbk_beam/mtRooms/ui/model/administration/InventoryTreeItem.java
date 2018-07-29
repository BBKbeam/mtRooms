package bbk_beam.mtRooms.ui.model.administration;

/**
 * Base inventory TreeView type
 */
public class InventoryTreeItem {
    private String description;

    /**
     * Default Constructor
     */
    public InventoryTreeItem() {
        this.description = "";
    }

    /**
     * Constructor
     *
     * @param description Description
     */
    public InventoryTreeItem(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return description;
    }
}
