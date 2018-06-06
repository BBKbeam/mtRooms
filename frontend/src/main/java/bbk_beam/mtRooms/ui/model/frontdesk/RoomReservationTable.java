package bbk_beam.mtRooms.ui.model.frontdesk;

import bbk_beam.mtRooms.reservation.dto.RoomReservation;
import bbk_beam.mtRooms.ui.model.GenericModelTable;
import bbk_beam.mtRooms.ui.model.SessionManager;

import java.util.Collection;

public class RoomReservationTable extends GenericModelTable<RoomReservation, RoomReservationModel> {

    /**
     * Constructor
     *
     * @param sessionManager SessionManager instance
     */
    public RoomReservationTable(SessionManager sessionManager) {
        super(sessionManager);
    }

    @Override
    public void loadData(Collection<RoomReservation> roomReservations) {
        if (!this.observableList.isEmpty())
            this.observableList.clear();
        for (RoomReservation roomReservation : roomReservations) {
            this.observableList.add(new RoomReservationModel(roomReservation));
        }
    }
}
