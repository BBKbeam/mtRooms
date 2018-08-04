package bbk_beam.mtRooms.ui.model.frontdesk;

import bbk_beam.mtRooms.reservation.dto.Reservation;
import bbk_beam.mtRooms.ui.model.SessionManager;
import bbk_beam.mtRooms.ui.model.common.GenericModelTable;

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

    @Override
    public void appendData(Collection<Reservation> reservations) {
        for (Reservation reservation : reservations) {
            this.observableList.add(new ReservationModel(reservation));
        }
    }
}
