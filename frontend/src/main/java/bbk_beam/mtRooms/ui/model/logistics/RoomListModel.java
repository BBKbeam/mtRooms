package bbk_beam.mtRooms.ui.model.logistics;

import bbk_beam.mtRooms.reservation.dto.Room;
import bbk_beam.mtRooms.ui.model.SessionManager;
import bbk_beam.mtRooms.ui.model.common.GenericChoiceBoxItem;
import bbk_beam.mtRooms.ui.model.common.GenericModelTable;

import java.util.Collection;

public class RoomListModel extends GenericModelTable<Room, GenericChoiceBoxItem<Room>> {
    /**
     * Constructor
     *
     * @param sessionManager SessionManager instance
     */
    public RoomListModel(SessionManager sessionManager) {
        super(sessionManager);
    }

    @Override
    public void loadData(Collection<Room> collection) {
        if (!this.observableList.isEmpty())
            this.observableList.clear();
        for (Room room : collection) {
            this.observableList.add(
                    new GenericChoiceBoxItem<>(room, String.valueOf(room.id()))
            );
        }
    }

    @Override
    public void appendData(Collection<Room> collection) {
        for (Room room : collection) {
            this.observableList.add(
                    new GenericChoiceBoxItem<>(room, String.valueOf(room.id()))
            );
        }
    }
}
