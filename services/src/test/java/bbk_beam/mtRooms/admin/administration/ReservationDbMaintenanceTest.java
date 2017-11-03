package bbk_beam.mtRooms.admin.administration;

import bbk_beam.mtRooms.admin.authentication.Token;
import bbk_beam.mtRooms.db.DbSystemBootstrap;
import bbk_beam.mtRooms.db.IReservationDbAccess;
import bbk_beam.mtRooms.db.IUserAccDbAccess;
import bbk_beam.mtRooms.db.exception.DbQueryException;
import bbk_beam.mtRooms.db.exception.SessionExpiredException;
import bbk_beam.mtRooms.db.exception.SessionInvalidException;
import bbk_beam.mtRooms.db.session.SessionType;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ReservationDbMaintenanceTest {
    private DbSystemBootstrap db_bootstrapper = new DbSystemBootstrap();
    private IUserAccDbAccess user_db_access;
    private IReservationDbAccess reservation_db_access;
    private IReservationDbAccess mock_reservation_db_access;
    private final String session_user_id = "00001";
    private final Date session_expiry = Date.from(Instant.now().plus(1, ChronoUnit.DAYS));

    @Before
    public void setUp() throws Exception {
        Files.deleteIfExists(Paths.get("reservation_maintenance_test.db"));
        this.db_bootstrapper.init("reservation_maintenance_test.db");
        this.user_db_access = this.db_bootstrapper.getUserAccDbAccess();
        this.reservation_db_access = this.db_bootstrapper.getReservationDbAccess();
        this.mock_reservation_db_access = mock(IReservationDbAccess.class);
        this.user_db_access.openSession(this.session_user_id, this.session_expiry, SessionType.ADMIN);
    }

    @After
    public void tearDown() throws Exception {
        this.user_db_access.closeSession(this.session_user_id);
        this.user_db_access = null;
        Files.deleteIfExists(Paths.get("reservation_maintenance_test.db"));
    }

    @Test
    public void vacuumDatabase() throws Exception {
        ReservationDbMaintenance reservationDbMaintenance = new ReservationDbMaintenance(this.reservation_db_access);
        Token token = new Token(session_user_id, new Date(), session_expiry);
        reservationDbMaintenance.vacuumDatabase(token);
        Assert.assertTrue(true); //Optimising does not fail.
    }

    @Test(expected = SessionInvalidException.class)
    public void vacuumDatabase_invalid_token() throws Exception {
        ReservationDbMaintenance reservationDbMaintenance = new ReservationDbMaintenance(this.reservation_db_access);
        Token token = new Token("InvalidToken0001", new Date(), session_expiry);
        reservationDbMaintenance.vacuumDatabase(token);
    }

    @Test(expected = SessionExpiredException.class)
    public void vacuumDatabase_expired_token() throws Exception {
        ReservationDbMaintenance reservationDbMaintenance = new ReservationDbMaintenance(this.reservation_db_access);
        Token token = new Token(
                "00002",
                Date.from(Instant.now().minus(1, ChronoUnit.HOURS)),
                Date.from(Instant.now().minus(10, ChronoUnit.MINUTES))
        );
        this.user_db_access.openSession(token.getSessionId(), token.getExpiry(), SessionType.ADMIN);
        reservationDbMaintenance.vacuumDatabase(token);
    }

    @Test(expected = DbQueryException.class)
    public void vacuumDatabase_vacuum_fail() throws Exception {
        ReservationDbMaintenance reservationDbMaintenance = new ReservationDbMaintenance(this.mock_reservation_db_access);
        when(this.mock_reservation_db_access.pushToDB(this.session_user_id, "VACUUM")).thenThrow(DbQueryException.class);
        Token token = new Token(session_user_id, new Date(), session_expiry);
        reservationDbMaintenance.vacuumDatabase(token);
    }
}