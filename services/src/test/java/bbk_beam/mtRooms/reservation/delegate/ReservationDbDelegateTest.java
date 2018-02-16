package bbk_beam.mtRooms.reservation.delegate;

import bbk_beam.mtRooms.admin.authentication.Token;
import bbk_beam.mtRooms.db.DbSystemBootstrap;
import bbk_beam.mtRooms.db.IReservationDbAccess;
import bbk_beam.mtRooms.db.IUserAccDbAccess;
import bbk_beam.mtRooms.db.TimestampConverter;
import bbk_beam.mtRooms.db.session.SessionType;
import bbk_beam.mtRooms.reservation.dto.*;
import bbk_beam.mtRooms.reservation.exception.FailedDbWrite;
import bbk_beam.mtRooms.reservation.exception.InvalidReservation;
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
        this.userAccDbAccess.openSession(this.token.getSessionId(), this.token.getExpiry(), SessionType.ADMIN, 1);
        this.reservationDbDelegate = new ReservationDbDelegate(reservationDbAccess);
    }

    @After
    public void tearDown() throws Exception {
        this.userAccDbAccess.closeSession(this.token.getSessionId());
        this.userAccDbAccess = null;
        this.reservationDbAccess = null;
        this.reservationDbDelegate = null;
        Files.deleteIfExists(Paths.get("reservation_db_delegate_test.db"));
    }

    @Test
    public void getCustomerAccount_via_ID() throws Exception {
        ObjectTable table = this.reservationDbDelegate.getCustomerAccount(this.token, 1);
        Assert.assertEquals(1, table.rowCount());
        HashMap<String, Object> row = table.getRow(1);
        Assert.assertEquals(1, row.get("id"));
        Assert.assertEquals(1, row.get("membership_type_id"));
        Assert.assertEquals("2015-10-15 16:15:12", row.get("customer_since"));
        Assert.assertEquals("Mrs", row.get("title"));
        Assert.assertEquals("Joanne", row.get("name"));
        Assert.assertEquals("Bouvier", row.get("surname"));
        Assert.assertEquals("Flat 4", row.get("address_1"));
        Assert.assertEquals("21 big road", row.get("address_2"));
        Assert.assertEquals("London", row.get("city"));
        Assert.assertEquals("London", row.get("county"));
        Assert.assertEquals("UK", row.get("country"));
        Assert.assertEquals("W1 4AQ", row.get("postcode"));
        Assert.assertEquals("+44 9876 532 123", row.get("telephone_1"));
        Assert.assertEquals(null, row.get("telephone_2"));
        Assert.assertEquals("jbouvier@mail.com", row.get("email"));
    }

    @Test
    public void getCustomerAccount_via_DTO_ID() throws Exception {
        Customer mock_customer = mock(Customer.class);
        when(mock_customer.customerID()).thenReturn(1);
        ObjectTable table = this.reservationDbDelegate.getCustomerAccount(this.token, mock_customer);
        Assert.assertEquals(1, table.rowCount());
        HashMap<String, Object> row = table.getRow(1);
        Assert.assertEquals(1, row.get("id"));
        Assert.assertEquals(1, row.get("membership_type_id"));
        Assert.assertEquals("2015-10-15 16:15:12", row.get("customer_since"));
        Assert.assertEquals("Mrs", row.get("title"));
        Assert.assertEquals("Joanne", row.get("name"));
        Assert.assertEquals("Bouvier", row.get("surname"));
        Assert.assertEquals("Flat 4", row.get("address_1"));
        Assert.assertEquals("21 big road", row.get("address_2"));
        Assert.assertEquals("London", row.get("city"));
        Assert.assertEquals("London", row.get("county"));
        Assert.assertEquals("UK", row.get("country"));
        Assert.assertEquals("W1 4AQ", row.get("postcode"));
        Assert.assertEquals("+44 9876 532 123", row.get("telephone_1"));
        Assert.assertEquals(null, row.get("telephone_2"));
        Assert.assertEquals("jbouvier@mail.com", row.get("email"));
    }

    @Test
    public void getCustomerAccount_via_DTO_details() throws Exception {
        Customer mock_customer = mock(Customer.class);
        when(mock_customer.customerID()).thenReturn(-1);
        when(mock_customer.membershipTypeID()).thenReturn(1);
        when(mock_customer.accountCreationDate()).thenReturn(TimestampConverter.getDateObject("2015-10-15 16:15:12"));
        when(mock_customer.title()).thenReturn("Mrs");
        when(mock_customer.name()).thenReturn("Joanne");
        when(mock_customer.surname()).thenReturn("Bouvier");
        when(mock_customer.address1()).thenReturn("Flat 4");
        when(mock_customer.address2()).thenReturn("21 big road");
        when(mock_customer.city()).thenReturn("London");
        when(mock_customer.county()).thenReturn("London");
        when(mock_customer.country()).thenReturn("UK");
        when(mock_customer.postCode()).thenReturn("W1 4AQ");
        when(mock_customer.phone1()).thenReturn("+44 9876 532 123");
        when(mock_customer.phone2()).thenReturn(null);
        when(mock_customer.email()).thenReturn("jbouvier@mail.com");
        //Testing
        ObjectTable table = this.reservationDbDelegate.getCustomerAccount(this.token, mock_customer);
        Assert.assertEquals(1, table.rowCount());
        HashMap<String, Object> row = table.getRow(1);
        Assert.assertEquals(1, row.get("id"));
        Assert.assertEquals(1, row.get("membership_type_id"));
        Assert.assertEquals("2015-10-15 16:15:12", row.get("customer_since"));
        Assert.assertEquals("Mrs", row.get("title"));
        Assert.assertEquals("Joanne", row.get("name"));
        Assert.assertEquals("Bouvier", row.get("surname"));
        Assert.assertEquals("Flat 4", row.get("address_1"));
        Assert.assertEquals("21 big road", row.get("address_2"));
        Assert.assertEquals("London", row.get("city"));
        Assert.assertEquals("London", row.get("county"));
        Assert.assertEquals("UK", row.get("country"));
        Assert.assertEquals("W1 4AQ", row.get("postcode"));
        Assert.assertEquals("+44 9876 532 123", row.get("telephone_1"));
        Assert.assertEquals(null, row.get("telephone_2"));
        Assert.assertEquals("jbouvier@mail.com", row.get("email"));
    }

    @Test
    public void findCustomer() throws Exception {
        ObjectTable table = this.reservationDbDelegate.findCustomer(this.token, "Bouvier");
        Assert.assertEquals(1, table.rowCount());
        HashMap<String, Object> row = table.getRow(1);
        Assert.assertEquals(1, row.get("id"));
        Assert.assertEquals("Mrs", row.get("title"));
        Assert.assertEquals("Joanne", row.get("name"));
        Assert.assertEquals("Bouvier", row.get("surname"));
    }

    @Test
    public void createNewCustomer() throws Exception {
        Customer customer = new Customer(
                1, new Date(),
                "Mr", "Joe", "Smith",
                "1 Coder Way", null, "HCK0R", "Belleview", null, "Hell",
                "+666 012 345 678", null, "jsmith@9nthcircle.com");
        String query = "SELECT * " +
                "FROM Customer " +
                "WHERE membership_type_id = " + customer.membershipTypeID() +
                " AND customer_since = \"" + TimestampConverter.getUTCTimestampString(customer.accountCreationDate()) + "\"" +
                " AND title = \"" + customer.title() + "\"" +
                " AND name = \"" + customer.name() + "\"" +
                " AND surname = \"" + customer.surname() + "\"" +
                " AND address_1 = \"" + customer.address1() + "\"" +
                " AND address_2 " + (customer.address2() == null ? "isnull" : "= \"" + customer.address2() + "\"") + "" +
                " AND city = \"" + customer.city() + "\"" +
                " AND county " + (customer.county() == null ? "isnull" : "= \"" + customer.county() + "\"") + "" +
                " AND country = \"" + customer.country() + "\"" +
                " AND postcode = \"" + customer.postCode() + "\"" +
                " AND telephone_1 = \"" + customer.phone1() + "\"" +
                " AND telephone_2 " + (customer.phone2() == null ? "isnull" : "= \"" + customer.phone2() + "\"") + "" +
                " AND email = \"" + customer.email() + "\"";
        //Testing
        Assert.assertEquals(0, this.reservationDbAccess.pullFromDB(this.token.getSessionId(), query).rowCount());
        this.reservationDbDelegate.createNewCustomer(this.token, customer);
        Assert.assertEquals(1, this.reservationDbAccess.pullFromDB(this.token.getSessionId(), query).rowCount());
    }

    @Test
    public void saveCustomerChangesToDB() throws Exception {
        String query = "SELECT * FROM Customer WHERE id = 1";
        Customer original = new Customer(
                1, 1, TimestampConverter.getDateObject("2015-10-15 16:15:12"),
                "Mrs", "Joanne", "Bouvier",
                "Flat 4", "21 big road", "W1 4AQ", "London", "London", "UK",
                "+44 9876 532 123", null, "jbouvier@mail.com");
        Customer update = new Customer(
                1, 2, new Date(),
                "Mr", "John", "Bouviere",
                "Flat 10", "21 huge road", "W1 4AQ", "Birmingham", "Birminghamshire", "UK",
                "+44 9876 111 111", "+44 6666 666 666", "jbouvier05@mail.com");
        //Pre-Update testing
        ObjectTable pre_update_table = this.reservationDbAccess.pullFromDB(this.token.getSessionId(), query);
        Assert.assertEquals(1, pre_update_table.rowCount());
        HashMap<String, Object> pre_update_row = pre_update_table.getRow(1);
        Assert.assertEquals(original.customerID(), pre_update_row.get("id"));
        Assert.assertEquals(original.membershipTypeID(), pre_update_row.get("membership_type_id"));
        Assert.assertEquals(TimestampConverter.getUTCTimestampString(original.accountCreationDate()), pre_update_row.get("customer_since"));
        Assert.assertEquals(original.title(), pre_update_row.get("title"));
        Assert.assertEquals(original.name(), pre_update_row.get("name"));
        Assert.assertEquals(original.surname(), pre_update_row.get("surname"));
        Assert.assertEquals(original.address1(), pre_update_row.get("address_1"));
        Assert.assertEquals(original.address2(), pre_update_row.get("address_2"));
        Assert.assertEquals(original.city(), pre_update_row.get("city"));
        Assert.assertEquals(original.county(), pre_update_row.get("county"));
        Assert.assertEquals(original.country(), pre_update_row.get("country"));
        Assert.assertEquals(original.postCode(), pre_update_row.get("postcode"));
        Assert.assertEquals(original.phone1(), pre_update_row.get("telephone_1"));
        Assert.assertEquals(original.phone2(), pre_update_row.get("telephone_2"));
        Assert.assertEquals(original.email(), pre_update_row.get("email"));
        //Update
        this.reservationDbDelegate.saveCustomerChangesToDB(this.token, update);
        //Post-Update testing
        ObjectTable post_update_table = this.reservationDbAccess.pullFromDB(this.token.getSessionId(), query);
        Assert.assertEquals(1, post_update_table.rowCount());
        HashMap<String, Object> post_update_row = post_update_table.getRow(1);
        Assert.assertEquals(update.customerID(), post_update_row.get("id"));
        Assert.assertEquals(update.membershipTypeID(), post_update_row.get("membership_type_id"));
        Assert.assertEquals(TimestampConverter.getUTCTimestampString(update.accountCreationDate()), post_update_row.get("customer_since"));
        Assert.assertEquals(update.title(), post_update_row.get("title"));
        Assert.assertEquals(update.name(), post_update_row.get("name"));
        Assert.assertEquals(update.surname(), post_update_row.get("surname"));
        Assert.assertEquals(update.address1(), post_update_row.get("address_1"));
        Assert.assertEquals(update.address2(), post_update_row.get("address_2"));
        Assert.assertEquals(update.city(), post_update_row.get("city"));
        Assert.assertEquals(update.county(), post_update_row.get("county"));
        Assert.assertEquals(update.country(), post_update_row.get("country"));
        Assert.assertEquals(update.postCode(), post_update_row.get("postcode"));
        Assert.assertEquals(update.phone1(), post_update_row.get("telephone_1"));
        Assert.assertEquals(update.phone2(), post_update_row.get("telephone_2"));
        Assert.assertEquals(update.email(), post_update_row.get("email"));
    }

    @Test(expected = FailedDbWrite.class)
    public void saveCustomerChangesToDB_fail() throws Exception {
        Customer update = new Customer(
                9999999, 2, new Date(),
                "Mr", "John", "Bouviere",
                "Flat 10", "21 huge road", "W1 4AQ", "Birmingham", "Birminghamshire", "UK",
                "+44 9876 111 111", "+44 6666 666 666", "jbouvier05@mail.com");
        this.reservationDbDelegate.saveCustomerChangesToDB(this.token, update);
    }

    @Test
    public void pay() throws Exception {
        Reservation mock_reservation = mock(Reservation.class);
        when(mock_reservation.id()).thenReturn(3);
        PaymentMethod paymentMethod = new PaymentMethod(1, "Cash");
        Date date = new Date();
        String note = "Payment note...";
        Payment payment = new Payment(mock_reservation, 999, date, note, paymentMethod);
        //Testing
        String check_query = "SELECT * FROM Reservation_has_Payment LEFT OUTER JOIN Payment ON payment_id = id WHERE reservation_id = 3";
        Assert.assertTrue(this.reservationDbAccess.pullFromDB(this.token.getSessionId(), check_query).isEmpty());
        this.reservationDbDelegate.pay(this.token, mock_reservation, payment);
        ObjectTable table = this.reservationDbAccess.pullFromDB(this.token.getSessionId(), check_query);
        Assert.assertEquals(1, table.rowCount());
        HashMap<String, Object> row = table.getRow(1);
        Assert.assertEquals(3, row.get("reservation_id"));
        Assert.assertNotNull(row.get("hash_id"));
        Assert.assertEquals(999, row.get("amount"));
        Assert.assertEquals(paymentMethod.id(), row.get("payment_method"));
        Assert.assertEquals(TimestampConverter.getUTCTimestampString(date), row.get("timestamp"));
        Assert.assertEquals(note, row.get("note"));
    }

    @Test
    public void getPayments() throws Exception {
        Reservation mock_reservation = mock(Reservation.class);
        when(mock_reservation.id()).thenReturn(1);
        ObjectTable table = this.reservationDbDelegate.getPayments(this.token, mock_reservation);
        Assert.assertEquals(1, table.rowCount());
        HashMap<String, Object> row = table.getRow(1);
        Assert.assertEquals(1, row.get("id"));
        Assert.assertEquals("TestHashID0000001", row.get("hash_id"));
        Assert.assertEquals(7700, row.get("amount"));
        Assert.assertEquals(2, row.get("method_id"));
        Assert.assertEquals("Debit Card", row.get("method_description"));
        Assert.assertEquals("2018-02-02 19:00:00", row.get("timestamp"));
        Assert.assertEquals("85*.90 credit c. Room L2", row.get("note"));
    }

    @Test
    public void getPaymentTypes() throws Exception {
        ObjectTable table = this.reservationDbDelegate.getPaymentMethods(this.token);
        Assert.assertEquals(3, table.rowCount());
    }

    @Test
    public void createReservation() throws Exception {
        Room room = new Room(8, 3, 1, 6);
        Discount discount = new Discount(1, .0, 1, "None");
        Date created_date = new Date();
        Reservation reservation = new Reservation(-1, created_date, 1, discount);
        //Testing
        this.reservationDbDelegate.createReservation(this.token, reservation);
        ObjectTable table = this.reservationDbAccess.pullFromDB(
                this.token.getSessionId(),
                "SELECT * FROM Reservation " +
                        "WHERE created_timestamp = \"" + TimestampConverter.getUTCTimestampString(created_date) + "\" " +
                        "AND customer_id = 1"
        );
        Assert.assertEquals(1, table.rowCount());
        HashMap<String, Object> row = table.getRow(1);
        Assert.assertEquals(6, row.get("id"));
        Assert.assertEquals(TimestampConverter.getUTCTimestampString(created_date), row.get("created_timestamp"));
        Assert.assertEquals(1, row.get("customer_id"));
        Assert.assertEquals(1, row.get("discount_id"));
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
        Assert.assertEquals(new Integer(7700), this.reservationDbDelegate.cancelReservation(this.token, mock_reservation));
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
        Assert.assertEquals(new Integer(8500), this.reservationDbDelegate.cancelReservedRoom(this.token, 1, mock_roomReservation));
        Assert.assertTrue(this.reservationDbAccess.pullFromDB(this.token.getSessionId(), check_query).rowCount() == 1);
    }

    @Test
    public void deleteReservation() throws Exception {
        String test_query1 = "SELECT * FROM Reservation WHERE id = 1";
        String test_query2 = "SELECT * FROM Room_has_Reservation WHERE reservation_id = 1";
        //Pre-Delete test
        Assert.assertEquals(1, this.reservationDbAccess.pullFromDB(this.token.getSessionId(), test_query1).rowCount());
        Assert.assertEquals(3, this.reservationDbAccess.pullFromDB(this.token.getSessionId(), test_query2).rowCount());
        //Deletion
        this.reservationDbDelegate.deleteReservation(this.token, 1);
        //Post-Delete test
        Assert.assertEquals(0, this.reservationDbAccess.pullFromDB(this.token.getSessionId(), test_query1).rowCount());
        Assert.assertEquals(0, this.reservationDbAccess.pullFromDB(this.token.getSessionId(), test_query2).rowCount());
    }

    @Test(expected = InvalidReservation.class)
    public void deleteReservation_with_invalid_reservation() throws Exception {
        this.reservationDbDelegate.deleteReservation(this.token, 99999);
    }

    @Test
    public void getReservation() throws Exception {
        ObjectTable table = this.reservationDbDelegate.getReservation(this.token, 1);
        HashMap<String, Object> row = table.getRow(1);
        Assert.assertEquals(1, row.get("id"));
        Assert.assertEquals("2018-02-09 10:00:00", row.get("created_timestamp"));
        Assert.assertEquals(3, row.get("customer_id"));
        Assert.assertEquals(3, row.get("discount_id"));
        Assert.assertEquals(10.0, row.get("discount_rate"));
        Assert.assertEquals(3, row.get("discount_category_id"));
        Assert.assertEquals("Member", row.get("discount_category_description"));
    }

    @Test
    public void getReservations() throws Exception {
        Customer mock_customer = mock(Customer.class);
        when(mock_customer.customerID()).thenReturn(3);
        ObjectTable table = this.reservationDbDelegate.getReservations(this.token, mock_customer);
        Assert.assertEquals(2, table.rowCount());
        for (int i = 1; i <= table.rowCount(); i++) {
            HashMap<String, Object> row = table.getRow(i);
            Assert.assertEquals(3, row.get("customer_id"));
            Assert.assertEquals(3, row.get("discount_id"));
            Assert.assertEquals(10.0, row.get("discount_rate"));
            Assert.assertEquals(3, row.get("discount_category_id"));
            Assert.assertEquals("Member", row.get("discount_category_description"));
            if ((Integer) row.get("id") == 1) {
                Assert.assertEquals("2018-02-09 10:00:00", row.get("created_timestamp"));
            } else if ((Integer) row.get("id") == 3) {
                Assert.assertEquals("2018-06-13 15:00:00", row.get("created_timestamp"));
            } else {
                Assert.assertTrue("Found an unexpected reservation ID.", false);
            }
        }
    }

    @Test
    public void getReservedRooms() throws Exception {
        Reservation mock_reservation = mock(Reservation.class);
        when(mock_reservation.id()).thenReturn(1);
        ObjectTable table = this.reservationDbDelegate.getReservedRooms(this.token, mock_reservation);
        Assert.assertEquals(3, table.rowCount());
        for (int i = 1; i <= table.rowCount(); i++) {
            HashMap<String, Object> row = table.getRow(i);
            Integer r = (Integer) row.get("room_id");
            Integer f = (Integer) row.get("floor_id");
            Integer b = (Integer) row.get("building_id");
            if (r == 7 && f == 3 && b == 1) {
                Assert.assertEquals("2018-02-09 10:05:00", row.get("timestamp_in"));
                Assert.assertEquals("2018-02-09 11:00:00", row.get("timestamp_out"));
                Assert.assertEquals("nothing to note", row.get("notes"));
                Assert.assertEquals(0, row.get("cancelled_flag"));
                Assert.assertEquals("Large room 2", row.get("description"));
                Assert.assertEquals(5, row.get("room_category_id"));
                Assert.assertEquals(11, row.get("price_id"));
                Assert.assertEquals(8500, row.get("price"));
                Assert.assertEquals(2008, row.get("price_year"));
            } else if (r == 4 && f == 2 && b == 1) {
                Assert.assertEquals("2018-02-09 11:00:00", row.get("timestamp_in"));
                Assert.assertEquals("2018-02-09 12:00:00", row.get("timestamp_out"));
                Assert.assertEquals("nothing to note", row.get("notes"));
                Assert.assertEquals(0, row.get("cancelled_flag"));
                Assert.assertEquals("Medium room 2", row.get("description"));
                Assert.assertEquals(3, row.get("room_category_id"));
                Assert.assertEquals(9, row.get("price_id"));
                Assert.assertEquals(6500, row.get("price"));
                Assert.assertEquals(2008, row.get("price_year"));
            } else if (r == 1 && f == 1 && b == 1) {
                Assert.assertEquals("2018-02-09 11:00:00", row.get("timestamp_in"));
                Assert.assertEquals("2018-02-09 12:00:00", row.get("timestamp_out"));
                Assert.assertEquals("nothing to note", row.get("notes"));
                Assert.assertEquals(1, row.get("cancelled_flag"));
                Assert.assertEquals("Small room 1", row.get("description"));
                Assert.assertEquals(1, row.get("room_category_id"));
                Assert.assertEquals(7, row.get("price_id"));
                Assert.assertEquals(4500, row.get("price"));
                Assert.assertEquals(2008, row.get("price_year"));
            } else {
                Assert.assertTrue("Found an unexpected ReservedRoom (r=" + r + ", f=" + f + ", b=" + b + ").", false);
            }
        }
    }

    @Test
    public void getDiscount() throws Exception {
        ObjectTable table = this.reservationDbDelegate.getDiscount(this.token, 1);
        HashMap<String, Object> row = table.getRow(1);
        Assert.assertEquals(1, row.get("id"));
        Assert.assertEquals(0.0, row.get("discount_rate"));
        Assert.assertEquals(1, row.get("category_id"));
        Assert.assertEquals("None", row.get("category_description"));
    }

    @Test
    public void getRoomCategory() throws Exception {
        ObjectTable table = this.reservationDbDelegate.getRoomCategory(this.token, 1);
        HashMap<String, Object> row = table.getRow(1);
        Assert.assertEquals(1, row.get("id"));
        Assert.assertEquals(10, row.get("capacity"));
        Assert.assertEquals(10, row.get("dimension"));
    }


}