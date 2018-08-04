package bbk_beam.mtRooms.ui.model.logistics;

import bbk_beam.mtRooms.reservation.dto.Building;
import bbk_beam.mtRooms.ui.model.SessionManager;
import bbk_beam.mtRooms.ui.model.common.GenericChoiceBoxItem;
import bbk_beam.mtRooms.ui.model.common.GenericModelTable;

import java.util.Collection;

public class BuildingListModel extends GenericModelTable<Building, GenericChoiceBoxItem<Building>> {
    /**
     * Constructor
     *
     * @param sessionManager SessionManager instance
     */
    public BuildingListModel(SessionManager sessionManager) {
        super(sessionManager);
    }

    @Override
    public void loadData(Collection<Building> collection) {
        if (!this.observableList.isEmpty())
            this.observableList.clear();
        for (Building building : collection) {
            this.observableList.add(
                    new GenericChoiceBoxItem<>(building, building.id() + " - " + building.name())
            );
        }
    }

    @Override
    public void appendData(Collection<Building> collection) {
        for (Building building : collection) {
            this.observableList.add(
                    new GenericChoiceBoxItem<>(building, building.id() + " - " + building.name())
            );
        }
    }
}
