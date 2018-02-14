package bbk_beam.mtRooms.reservation.delegate;

import bbk_beam.mtRooms.admin.authentication.Token;
import bbk_beam.mtRooms.db.*;
import bbk_beam.mtRooms.db.exception.DbQueryException;
import bbk_beam.mtRooms.db.session.SessionType;
import bbk_beam.mtRooms.reservation.dto.*;
import bbk_beam.mtRooms.test_data.TestDBGenerator;
import eadjlib.datastructure.ObjectTable;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ReservationDbDelegateTest {
    private DbSystemBootstrap db_bootstrapper = new DbSystemBootstrap();
    private IReservationDbAccess reservationDbAccess;
    private IReservationDbAccess mock_reservationDbAccess;
    private IUserAccDbAccess userAccDbAccess;
    private Token token = new Token("00001", new Date(), Date.from(Instant.now().plus(1, ChronoUnit.DAYS)));
    private ReservationDbDelegate reservationDbDelegate;

    @Before
    public void setUp() throws Exception {
        Files.deleteIfExists(Paths.get("reservation_db_delegate_test.db"));
        TestDBGenerator testDBGenerator = new TestDBGenerator();
        testDBGenerator.createTestDB("reservation_db_delegate_test.db");
        this.db_bootstrapper.init("reservation_db_delegate_test.db");
        this.userAccDbAccess = this.db_bootstrapper.getUserAccDbAccess();
        this.reservationDbAccess = this.db_bootstrapper.getReservationDbAccess();
        this.mock_reservationDbAccess = mock(ReservationDbAccess.class);
        this.userAccDbAccess.openSession(this.token.getSessionId(), this.token.getExpiry(), SessionType.ADMIN, 1);
        this.reservationDbDelegate = new ReservationDbDelegate(reservationDbAccess);
    }

    @After
    public void tearDown() throws Exception {
        this.userAccDbAccess.closeSession(this.token.getSessionId());
        this.userAccDbAccess = null;
        this.reservationDbAccess = null;
        this.mock_reservationDbAccess = null;
        this.reservationDbDelegate = null;
        //Files.deleteIfExists(Paths.get("reservation_db_delegate_test.db"));
    }

    @Test
    public void getCustomerAccount() throws Exception {

        Assert.assertTrue(false);
        //TODO
    }

    @Test(expected = DbQueryException.class)
    public void getCustomerAccount_fail() throws Exception {
        this.reservationDbDelegate.getCustomerAccount(this.token, 1);
    }

    @Test
    public void findCustomer() throws Exception {
        Assert.assertTrue(false);
        //TODO
    }

    @Test
    public void createNewCustomer() throws Exception {
        Assert.assertTrue(false);
        //TODO
    }

    @Test
    public void saveCustomerChangesToDB() throws Exception {
        Assert.assertTrue(false);
        //TODO
    }

    @Test(expected = DbQueryException.class)
    public void saveCustomerChangesToDB_fail() throws Exception {
        Assert.assertTrue(false);
        //TODO
    }

    @Test
    public void pay() throws Exception {
        Assert.assertTrue(false);
        //TODO
    }

    @Test
    public void getPayments() throws Exception {
        Reservation mock_reservation = mock(Reservation.class);
        when(mock_reservation.id()).thenReturn(1);
        ObjectTable table = this.reservationDbDelegate.getPayments(this.token, mock_reservation);
        System.out.println(table);
    }

    @Test
    public void getPaymentTypes() throws Exception {
        ObjectTable table = this.reservationDbDelegate.getPaymentTypes(this.token);
        Assert.assertEquals(3, table.rowCount());
    }

    @Test
    public void createReservation() throws Exception {
        Room room = new Room(8, 3, 1, 6);
        Discount discount = new Discount(1, .0, 1, "None");
        Date reservation_start = new Date();
        Date reservation_end = Date.from(Instant.now().plus(2, ChronoUnit.HOURS));
        String note = "Note 1";
        RoomPrice mock_price = mock(RoomPrice.class);
        RoomReservation roomReservation = new RoomReservation(
                room,
                reservation_start,
                reservation_end,
                note,
                mock_price,
                false
        );
        Reservation reservation = new Reservation(
                -1,
                reservation_start,
                1,
                discount
        );
        reservation.addRoomReservation(roomReservation);
        //Testing
        Assert.assertTrue(reservation.id() < 1);
        this.reservationDbDelegate.createReservation(this.token, reservation);
        Assert.assertTrue(reservation.id() > 0);

        ObjectTable table = this.reservationDbAccess.pullFromDB(
                this.token.getSessionId(),
                "SELECT * FROM Reservation " +
                        "LEFT OUTER JOIN Room_has_Reservation ON Reservation.id = Room_has_Reservation.reservation_id " +
                        "WHERE created_timestamp = \"" + TimestampConverter.getUTCTimestampString(reservation_start) + "\" " +
                        "AND customer_id = 1"
        );

        Assert.assertEquals(1, table.rowCount());
        HashMap<String, Object> row = table.getRow(1);
        Assert.assertEquals(5, row.get("id"));
        Assert.assertEquals(TimestampConverter.getUTCTimestampString(reservation_start), row.get("created_timestamp"));
        Assert.assertEquals(1, row.get("customer_id"));
        Assert.assertEquals(1, row.get("discount_id"));
        Assert.assertEquals(8, row.get("room_id"));
        Assert.assertEquals(3, row.get("floor_id"));
        Assert.assertEquals(1, row.get("building_id"));
        Assert.assertEquals(TimestampConverter.getUTCTimestampString(reservation_start), row.get("timestamp_in"));
        Assert.assertEquals(TimestampConverter.getUTCTimestampString(reservation_end), row.get("timestamp_out"));
        Assert.assertEquals("Note 1", row.get("notes"));
        Assert.assertEquals(0, row.get("cancelled_flag"));
    }

    @Test
    public void createRoomReservation() throws Exception {
        String check_query = "SELECT * FROM Room_has_Reservation WHERE room_id = 8 AND floor_id = 3 AND building_id = 1 AND reservation_id = 4";
        Assert.assertFalse(this.reservationDbAccess.pullFromDB(this.token.getSessionId(), check_query).rowCount() == 1);

        Room room = new Room(8, 3, 1, 6);
        RoomReservation mock_roomReservation = mock(RoomReservation.class);
        Date reservation_start = new Date();
        Date reservation_end = Date.from(Instant.now().plus(1, ChronoUnit.HOURS));
        String note = "New booking of a theatre room.";

        when(mock_roomReservation.room()).thenReturn(room);
        when(mock_roomReservation.reservationStart()).thenReturn(reservation_start);
        when(mock_roomReservation.reservationEnd()).thenReturn(reservation_end);
        when(mock_roomReservation.note()).thenReturn(note);

        this.reservationDbDelegate.createRoomReservation(this.token, 4, mock_roomReservation);
        Assert.assertTrue(this.reservationDbAccess.pullFromDB(this.token.getSessionId(), check_query).rowCount() == 1);
    }

    @Test
    public void cancelReservation() throws Exception {
        Reservation mock_reservation = mock(Reservation.class);
        when(mock_reservation.id()).thenReturn(1);
        //Test
        Assert.assertEquals(new Integer(77), this.reservationDbDelegate.cancelReservation(this.token, mock_reservation));
        //Check cancelled_flag has been set for all room reserved
        String query = "SELECT cancelled_flag FROM Room_has_Reservation WHERE reservation_id = 1";
        ObjectTable table = this.reservationDbAccess.pullFromDB(this.token.getSessionId(), query);
        Assert.assertFalse(table.isEmpty());
        for (int i = 1; i <= table.rowCount(); i++) {
            HashMap<String, Object> row = table.getRow(i);
            Assert.assertEquals(1, row.get("cancelled_flag"));
        }
    }

    @Test
    public void cancelReservedRoom() throws Exception {
        RoomReservation mock_roomReservation = mock(RoomReservation.class);
        Room mock_room = mock(Room.class);
        when(mock_roomReservation.room()).thenReturn(mock_room);
        when(mock_roomReservation.reservationStart()).thenReturn(TimestampConverter.getDateObject("2018-02-09 10:05:00"));
        when(mock_room.id()).thenReturn(7);
        when(mock_room.floorID()).thenReturn(3);
        when(mock_room.buildingID()).thenReturn(1);

        String check_query = "SELECT cancelled_flag FROM Room_has_Reservation " +
                "WHERE reservation_id = 1" +
                " AND room_id = 7" +
                " AND floor_id = 3" +
                " AND building_id = 1" +
                " AND timestamp_in = \"2018-02-09 10:05:00\"" +
                " AND cancelled_flag = 1";
        //Test
        Assert.assertFalse(this.reservationDbAccess.pullFromDB(this.token.getSessionId(), check_query).rowCount() == 1);
        Assert.assertEquals(new Integer(85), this.reservationDbDelegate.cancelReservedRoom(this.token, 1, mock_roomReservation));
        Assert.assertTrue(this.reservationDbAccess.pullFromDB(this.token.getSessionId(), check_query).rowCount() == 1);
    }

    @Test
    public void getReservation() throws Exception {
        ObjectTable table = this.reservationDbDelegate.getReservation(this.token, 1);
        System.out.println(table);
        Assert.assertTrue(false);
        //TODO
    }

    @Test
    public void getReservations() throws Exception {
        Customer mock_customer = mock(Customer.class);
        when(mock_customer.customerID()).thenReturn(3);
        ObjectTable table = this.reservationDbDelegate.getReservations(this.token, mock_customer);
        System.out.println(table);
        Assert.assertTrue(false);
        //TODO
    }

    @Test
    public void getReservedRooms() throws Exception {
        Reservation reservation = new Reservation(1, Date.from(Instant.now()), 1, new Discount(1, 20., 1, ""));
        ObjectTable table = this.reservationDbDelegate.getReservedRooms(this.token, reservation);
        System.out.println(table);
        Assert.assertTrue(false);
        //TODO
    }

    @Test
    public void getDiscount() throws Exception {
        ObjectTable table = this.reservationDbDelegate.getDiscount(this.token, 1);
        System.out.println(table);
        Assert.assertTrue(false);
        //TODO
    }

    @Test
    public void getRoomCategory() throws Exception {
        ObjectTable table = this.reservationDbDelegate.getRoomCategory(this.token, 1);
        System.out.println(table);
        Assert.assertTrue(false);
        //TODO
    }
}