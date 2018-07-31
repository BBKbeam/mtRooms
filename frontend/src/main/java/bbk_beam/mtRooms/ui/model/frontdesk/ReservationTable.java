package bbk_beam.mtRooms.ui.model.frontdesk;

import bbk_beam.mtRooms.reservation.dto.Reservation;
import bbk_beam.mtRooms.ui.model.GenericModelTable;
import bbk_beam.mtRooms.ui.model.SessionManager;

import java.util.Collection;

public class ReservationTable extends GenericModelTable<Reservation, ReservationModel> {

    /**
     * Constructor
     *
     * @param sessionManager SessionManager instance
     */
    public ReservationTable(SessionManager sessionManager) {
        super(sessionManager);
    }

    @Override
    public void loadData(Collection<Reservation> reservations) {
        if (!this.observableList.isEmpty())
            this.observableList.clear();
        for (Reservation reservation : reservations) {
            this.observableList.add(new ReservationModel(reservation));
        }
    }
}
