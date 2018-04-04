package bbk_beam.mtRooms.reservation.processing;

import bbk_beam.mtRooms.admin.authentication.Token;
import bbk_beam.mtRooms.db.DbSystemBootstrap;
import bbk_beam.mtRooms.db.IReservationDbAccess;
import bbk_beam.mtRooms.db.IUserAccDbAccess;
import bbk_beam.mtRooms.db.TimestampConverter;
import bbk_beam.mtRooms.db.session.SessionType;
import bbk_beam.mtRooms.reservation.delegate.ReservationDbDelegate;
import bbk_beam.mtRooms.reservation.dto.*;
import bbk_beam.mtRooms.reservation.scheduling.ScheduleCache;
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
        this.reservationProcessing = new ReservationProcessing(this.reservationDbDelegate, new ScheduleCache());
    }

    @After
    public void tearDown() throws Exception {
        this.userAccDbAccess.closeSession(this.token.getSessionId());
        this.userAccDbAccess = null;
        this.reservationDbAccess = null;
        this.reservationDbDelegate = null;
        this.reservationProcessing = null;
        Files.deleteIfExists(Paths.get("reservation_processing_test.db"));
    }

    @Test
    public void createReservation() throws Exception {
        //Setting up dummy test Reservation
        Room room = new Room(8, 3, 1, 6);
        Discount discount = new Discount(1, .0, 1, "None");
        Date reservation_start = new Date();
        Date reservation_end = Date.from(Instant.now().plus(2, ChronoUnit.HOURS));
        String note = "Note 1";
        RoomPrice room_price = new RoomPrice(12, 110.00, 2008);
        RoomReservation roomReservation = new RoomReservation(room, reservation_start, reservation_end, 80, false, note, room_price, false);
        Reservation test_reservation = new Reservation(reservation_start, 1, discount);
        test_reservation.addRoomReservation(roomReservation);
        //Testing
        Reservation fetched_reservation = this.reservationProcessing.createReservation(this.token, test_reservation);
        test_reservation.setID(fetched_reservation.id());
        Assert.assertTrue(test_reservation.equals(fetched_reservation));
    }

    @Test
    public void createRoomReservation() throws Exception {
        //Setting up dummy test Reservation and RoomReservation
        Reservation mock_reservation = mock(Reservation.class);
        when(mock_reservation.id()).thenReturn(5);
        RoomReservation roomReservation = new RoomReservation(
                new Room(8, 3, 1, 6),
                new Date(),
                Date.from(Instant.now().plus(2, ChronoUnit.HOURS)),
                80,
                false,
                "Note 1",
                new RoomPrice(12, 110., 2008),
                false
        );
        //Testing
        Assert.assertEquals(1, this.reservationDbDelegate.getReservedRooms(this.token, mock_reservation).rowCount());
        this.reservationProcessing.createRoomReservation(this.token, mock_reservation, roomReservation);
        Assert.assertEquals(2, this.reservationDbDelegate.getReservedRooms(this.token, mock_reservation).rowCount());
    }

    @Test
    public void cancelReservation() throws Exception {
        Reservation mock_reservation = mock(Reservation.class);
        //Testing
        when(mock_reservation.id()).thenReturn(5);
        Assert.assertEquals(new Double(0), this.reservationProcessing.cancelReservation(this.token, mock_reservation));
        when(mock_reservation.id()).thenReturn(4);
        Assert.assertEquals(new Double(63.00), this.reservationProcessing.cancelReservation(this.token, mock_reservation));
    }

    @Test
    public void cancelReservedRoom() throws Exception {
        Reservation mock_reservation = mock(Reservation.class);
        when(mock_reservation.id()).thenReturn(5);
        RoomReservation mock_roomReservation = mock(RoomReservation.class);
        Room mock_room = mock(Room.class);
        when(mock_roomReservation.room()).thenReturn(mock_room);
        when(mock_roomReservation.reservationStart()).thenReturn(TimestampConverter.getDateObject("2015-01-25 09:00:00"));
        when(mock_room.id()).thenReturn(5);
        when(mock_room.floorID()).thenReturn(2);
        when(mock_room.buildingID()).thenReturn(1);
        //Testing
        Assert.assertEquals(new Double(70.00), this.reservationProcessing.cancelReservedRoom(this.token, mock_reservation, mock_roomReservation));
    }

    @Test
    public void getReservation() throws Exception {
        Reservation reservation = reservationProcessing.getReservation(this.token, 1);
        Assert.assertEquals(new Integer(1), reservation.id());
        Assert.assertEquals(new Integer(3), reservation.customerID());
        Assert.assertEquals(new Integer(3), reservation.discount().id());
        Assert.assertEquals(3, reservation.rooms().size());
    }

    @Test
    public void getReservations() throws Exception {
        Customer mock_customer = mock(Customer.class);
        when(mock_customer.customerID()).thenReturn(3);
        List<Reservation> reservations = reservationProcessing.getReservations(this.token, mock_customer);
        Assert.assertEquals(2, reservations.size());
    }

    @Test
    public void getRoomCategory() throws Exception {
        RoomCategory roomCategory = this.reservationProcessing.getRoomCategory(this.token, 1);
        Assert.assertEquals(new RoomCategory(1, 10, 10), roomCategory);
    }
}