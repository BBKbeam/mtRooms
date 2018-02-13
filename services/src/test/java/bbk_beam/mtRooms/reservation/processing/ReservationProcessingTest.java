package bbk_beam.mtRooms.reservation.processing;

import bbk_beam.mtRooms.admin.authentication.Token;
import bbk_beam.mtRooms.db.DbSystemBootstrap;
import bbk_beam.mtRooms.db.IReservationDbAccess;
import bbk_beam.mtRooms.db.IUserAccDbAccess;
import bbk_beam.mtRooms.db.session.SessionType;
import bbk_beam.mtRooms.reservation.delegate.ReservationDbDelegate;
import bbk_beam.mtRooms.reservation.dto.Customer;
import bbk_beam.mtRooms.reservation.dto.Reservation;
import bbk_beam.mtRooms.test_data.TestDBGenerator;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ReservationProcessingTest {
    private DbSystemBootstrap db_bootstrapper = new DbSystemBootstrap();
    private IReservationDbAccess reservationDbAccess;
    private IUserAccDbAccess userAccDbAccess;
    private Token token = new Token("00001", new Date(), Date.from(Instant.now().plus(1, ChronoUnit.DAYS)));
    private ReservationDbDelegate reservationDbDelegate;
    private ReservationProcessing reservationProcessing;
    private Customer mock_customer = mock(Customer.class);

    @Before
    public void setUp() throws Exception {
        Files.deleteIfExists(Paths.get("reservation_processing_test.db"));
        TestDBGenerator testDBGenerator = new TestDBGenerator();
        testDBGenerator.createTestDB("reservation_processing_test.db");
        this.db_bootstrapper.init("reservation_processing_test.db");
        this.userAccDbAccess = this.db_bootstrapper.getUserAccDbAccess();
        this.reservationDbAccess = this.db_bootstrapper.getReservationDbAccess();
        this.userAccDbAccess.openSession(this.token.getSessionId(), this.token.getExpiry(), SessionType.ADMIN, 1);
        this.reservationDbDelegate = new ReservationDbDelegate(reservationDbAccess);
        this.reservationProcessing = new ReservationProcessing(this.reservationDbDelegate);
    }

    @After
    public void tearDown() throws Exception {
        this.userAccDbAccess.closeSession(this.token.getSessionId());
        this.userAccDbAccess = null;
        this.reservationDbAccess = null;
        this.reservationDbDelegate = null;
        this.reservationProcessing = null;
        //Files.deleteIfExists(Paths.get("reservation_processing_test.db"));
    }

    @Test
    public void createReservation() throws Exception {
        Assert.assertTrue(false);
        //TODO
    }

    @Test
    public void cancelReservation() throws Exception {
        Assert.assertTrue(false);
        //TODO
    }

    @Test
    public void getReservation() throws Exception {
        Reservation reservation = reservationProcessing.getReservation(this.token, 1);
        System.out.println(reservation);
        Assert.assertTrue(false);
        //TODO
    }

    @Test
    public void getReservations() throws Exception {
        when(mock_customer.customerID()).thenReturn(1);
        List<Reservation> reservations = reservationProcessing.getReservations(this.token, mock_customer);
        for (Reservation r : reservations) {
            System.out.println(r);
        }
        Assert.assertTrue(false);
        //TODO
    }
}