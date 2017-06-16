package bbk_beam.mtRooms.db;

import bbk_beam.mtRooms.db.exception.DBQueryException;
import bbk_beam.mtRooms.db.exception.SessionException;
import bbk_beam.mtRooms.db.exception.SessionExpiredException;
import bbk_beam.mtRooms.db.session.SessionTracker;

import java.sql.ResultSet;
import java.util.Date;

public class ReservationDbAccess implements IReservationDbAccess {
    private SessionTracker sessions = new SessionTracker();

    @Override
    public void openSession(String id, Date expiry) throws SessionException {
        //TODO
    }

    @Override
    public void closeSession(String id) throws SessionException {
        //TODO
    }

    @Override
    public ResultSet queryDB(String session_id, String query) throws DBQueryException, SessionExpiredException {
        //TODO
        return null;
    }
}
