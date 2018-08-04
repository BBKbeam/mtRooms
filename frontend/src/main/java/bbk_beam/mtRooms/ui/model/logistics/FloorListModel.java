package bbk_beam.mtRooms.ui.model.logistics;

import bbk_beam.mtRooms.reservation.dto.Floor;
import bbk_beam.mtRooms.ui.model.SessionManager;
import bbk_beam.mtRooms.ui.model.common.GenericChoiceBoxItem;
import bbk_beam.mtRooms.ui.model.common.GenericModelTable;

import java.util.Collection;

public class FloorListModel extends GenericModelTable<Floor, GenericChoiceBoxItem<Floor>> {
    /**
     * Constructor
     *
     * @param sessionManager SessionManager instance
     */
    public FloorListModel(SessionManager sessionManager) {
        super(sessionManager);
    }

    @Override
    public void loadData(Collection<Floor> collection) {
        if (!this.observableList.isEmpty())
            this.observableList.clear();
        for (Floor floor : collection) {
            this.observableList.add(
                    new GenericChoiceBoxItem<>(floor, String.valueOf(floor.floorID()))
            );
        }
    }

    @Override
    public void appendData(Collection<Floor> collection) {
        for (Floor floor : collection) {
            this.observableList.add(
                    new GenericChoiceBoxItem<>(floor, String.valueOf(floor.floorID()))
            );
        }
    }
}
