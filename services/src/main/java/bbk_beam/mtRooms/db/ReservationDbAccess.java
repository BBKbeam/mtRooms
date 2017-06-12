package bbk_beam.mtRooms.db;

import bbk_beam.mtRooms.db.session.SessionTracker;

public class ReservationDbAccess implements IReservationDbAccess {
    private SessionTracker sessions = new SessionTracker();

}
