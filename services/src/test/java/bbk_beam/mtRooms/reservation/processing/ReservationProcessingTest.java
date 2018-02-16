package bbk_beam.mtRooms.reservation.processing;

import bbk_beam.mtRooms.admin.authentication.Token;
import bbk_beam.mtRooms.db.DbSystemBootstrap;
import bbk_beam.mtRooms.db.IReservationDbAccess;
import bbk_beam.mtRooms.db.IUserAccDbAccess;
import bbk_beam.mtRooms.db.session.SessionType;
import bbk_beam.mtRooms.reservation.delegate.ReservationDbDelegate;
import bbk_beam.mtRooms.reservation.dto.*;
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
        //Setting up dummy test Reservation
        Room room = new Room(8, 3, 1, 6);
        Discount discount = new Discount(1, .0, 1, "None");
        Date reservation_start = new Date();
        Date reservation_end = Date.from(Instant.now().plus(2, ChronoUnit.HOURS));
        String note = "Note 1";
        RoomPrice room_price = new RoomPrice(12, 110, 2008);
        RoomReservation roomReservation = new RoomReservation(room, reservation_start, reservation_end, note, room_price, false);
        Reservation test_reservation = new Reservation(reservation_start, 1, discount);
        test_reservation.addRoomReservation(roomReservation);
        //Testing
        Reservation fetched_reservation = this.reservationProcessing.createReservation(this.token, test_reservation);
        test_reservation.setID(fetched_reservation.id());
        Assert.assertTrue(test_reservation.equals(fetched_reservation));
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
        Customer mock_customer = mock(Customer.class);
        when(mock_customer.customerID()).thenReturn(1);
        List<Reservation> reservations = reservationProcessing.getReservations(this.token, mock_customer);
        for (Reservation r : reservations) {
            System.out.println(r);
        }
        Assert.assertTrue(false);
        //TODO
    }
}