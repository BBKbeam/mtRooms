package bbk_beam.mtRooms.revenue.revenue;

import bbk_beam.mtRooms.admin.authentication.Token;
import bbk_beam.mtRooms.db.DbSystemBootstrap;
import bbk_beam.mtRooms.db.TimestampConverter;
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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RevenueAggregatorTest {
    private DbSystemBootstrap db_bootstrapper = new DbSystemBootstrap();
    private RevenueAggregator revenue_aggregator;
    private Token token = new Token("00001", new Date(), Date.from(Instant.now().plus(1, ChronoUnit.DAYS)));

    @Before
    public void setUp() throws Exception {
        Files.deleteIfExists(Paths.get("revenue_aggregator_test.db"));
        TestDBGenerator testDBGenerator = new TestDBGenerator();
        testDBGenerator.createTestDB("revenue_aggregator_test.db");
        this.db_bootstrapper.init("revenue_aggregator_test.db");
        this.db_bootstrapper.getUserAccDbAccess().openSession(this.token.getSessionId(), this.token.getExpiry(), SessionType.ADMIN, 1);
        this.revenue_aggregator = new RevenueAggregator(this.db_bootstrapper.getReservationDbAccess());
    }

    @After
    public void tearDown() throws Exception {
        Files.deleteIfExists(Paths.get("revenue_aggregator_test.db"));
        this.db_bootstrapper = null;
        this.revenue_aggregator = null;
    }

    @Test
    public void getBuildings() throws Exception {
        ObjectTable table = this.revenue_aggregator.getBuildings(this.token);
        Assert.assertEquals("Wrong number of rows", 1, table.rowCount());
        Assert.assertEquals("Wrong number of columns", 8, table.columnCount());
        Assert.assertEquals(1, table.getInteger(1, 1)); //id
        Assert.assertEquals("Test Building", table.getString(2, 1)); //name
    }

    @Test
    public void getFloors() throws Exception {
        Building mock_building = mock(Building.class);
        when(mock_building.id()).thenReturn(1);
        ObjectTable table = this.revenue_aggregator.getFloors(this.token, mock_building);
        Assert.assertEquals("Wrong number of rows", 3, table.rowCount());
        Assert.assertEquals("Wrong number of columns", 3, table.columnCount());
        Assert.assertEquals(1, table.getInteger(1, 1)); //id
        Assert.assertEquals(1, table.getInteger(2, 1)); //building_id
        Assert.assertEquals("Ground level", table.getString(3, 1)); //description
        Assert.assertEquals(2, table.getInteger(1, 2)); //id
        Assert.assertEquals(1, table.getInteger(2, 2)); //building_id
        Assert.assertEquals("First floor", table.getString(3, 2)); //description
        Assert.assertEquals(3, table.getInteger(1, 3)); //id
        Assert.assertEquals(1, table.getInteger(2, 3)); //building_id
        Assert.assertEquals("Second floor", table.getString(3, 3)); //description
    }

    @Test
    public void getRooms() throws Exception {
        Floor mock_floor = mock(Floor.class);
        when(mock_floor.buildingID()).thenReturn(1);
        when(mock_floor.floorID()).thenReturn(2);
        ObjectTable table = this.revenue_aggregator.getRooms(this.token, mock_floor);
        Assert.assertEquals("Wrong number of rows", 3, table.rowCount());
        Assert.assertEquals("Wrong number of columns", 5, table.columnCount());
        Assert.assertEquals(4, table.getInteger(1, 1)); //id
        Assert.assertEquals("Medium room 2", table.getString(5, 1)); //description
        Assert.assertEquals(5, table.getInteger(1, 2)); //id
        Assert.assertEquals("Medium room 3", table.getString(5, 2)); //description
        Assert.assertEquals(6, table.getInteger(1, 3)); //id
        Assert.assertEquals("Large room 1", table.getString(5, 3)); //description
    }

    @Test
    public void getCustomerReservationIDs() throws Exception {
        Customer customer = mock(Customer.class);
        when(customer.customerID()).thenReturn(3);
        ObjectTable table = this.revenue_aggregator.getCustomerReservationIDs(this.token, customer);
        Assert.assertEquals(2, table.rowCount());
        Assert.assertEquals(1, table.columnCount());
        Assert.assertEquals(1, table.getInteger(1, 1));
        Assert.assertEquals(3, table.getInteger(1, 2));
    }

    @Test
    public void getItemisedReservation() throws Exception {
        ObjectTable table = this.revenue_aggregator.getItemisedReservation(this.token, 1);
        Assert.assertEquals(3, table.rowCount());
        Assert.assertEquals(7, table.columnCount());
        Assert.assertEquals(1, table.getInteger(1, 1)); //building
        Assert.assertEquals(3, table.getInteger(2, 1)); //floor
        Assert.assertEquals(7, table.getInteger(3, 1)); //room
        Assert.assertEquals(new Double(85.), table.getDouble(7, 1)); //price

        Assert.assertEquals(1, table.getInteger(1, 2)); //building
        Assert.assertEquals(2, table.getInteger(2, 2)); //floor
        Assert.assertEquals(4, table.getInteger(3, 2)); //room
        Assert.assertEquals(new Double(65.), table.getDouble(7, 2)); //price

        Assert.assertEquals(1, table.getInteger(1, 3)); //building
        Assert.assertEquals(1, table.getInteger(2, 3)); //floor
        Assert.assertEquals(1, table.getInteger(3, 3)); //room
        Assert.assertEquals(new Double(45.), table.getDouble(7, 3)); //price
    }

    @Test
    public void getReservationDiscount() throws Exception {
        ObjectTable table = this.revenue_aggregator.getReservationDiscount(this.token, 3);
        Assert.assertEquals(1, table.rowCount());
        Assert.assertEquals(4, table.columnCount());
        Assert.assertEquals(3, table.getInteger(1, 1)); //discount_id
        Assert.assertEquals(3, table.getInteger(3, 1)); //category_id
    }

    @Test
    public void getPayments() throws Exception {
        Date from = TimestampConverter.getDateObject("2018-02-01 00:00:00");
        Date to = TimestampConverter.getDateObject("2018-02-29 23:59:59");
        ObjectTable table = this.revenue_aggregator.getPayments(this.token, from, to);
        Assert.assertEquals(4, table.rowCount());
        Assert.assertEquals(9, table.columnCount());

        Assert.assertEquals(1, table.getInteger(1, 1)); //customer_id
        Assert.assertEquals(2, table.getInteger(2, 1)); //reservation_id
        Assert.assertEquals(new Double(34.0), table.getDouble(6, 1)); //amount

        Assert.assertEquals(2, table.getInteger(1, 2)); //customer_id
        Assert.assertEquals(4, table.getInteger(2, 2)); //reservation_id
        Assert.assertEquals(new Double(40.5), table.getDouble(6, 2)); //amount

        Assert.assertEquals(2, table.getInteger(1, 3)); //customer_id
        Assert.assertEquals(4, table.getInteger(2, 3)); //reservation_id
        Assert.assertEquals(new Double(22.5), table.getDouble(6, 3)); //amount

        Assert.assertEquals(3, table.getInteger(1, 4)); //customer_id
        Assert.assertEquals(1, table.getInteger(2, 4)); //reservation_id
        Assert.assertEquals(new Double(77.0), table.getDouble(6, 4)); //amount
    }

    @Test
    public void getPayments_for_Reservation() throws Exception {
        Reservation reservation = mock(Reservation.class);
        when(reservation.id()).thenReturn(4);
        ObjectTable table = this.revenue_aggregator.getPayments(this.token, reservation.id());
        Assert.assertEquals(2, table.rowCount());
        Assert.assertEquals(7, table.columnCount());

        Assert.assertEquals(3, table.getInteger(1, 1)); //payment_id
        Assert.assertEquals(new Double(40.5), table.getDouble(4, 1)); //amount

        Assert.assertEquals(4, table.getInteger(1, 2)); //payment_id
        Assert.assertEquals(new Double(22.5), table.getDouble(4, 2)); //amount
    }

    @Test
    public void getPayments_for_Customer() throws Exception {
        Customer customer = mock(Customer.class);
        when(customer.customerID()).thenReturn(2);
        ObjectTable table = this.revenue_aggregator.getPayments(this.token, customer);
        Assert.assertEquals(2, table.rowCount());
        Assert.assertEquals(7, table.columnCount());

        Assert.assertEquals(3, table.getInteger(1, 1)); //payment_id
        Assert.assertEquals(new Double(40.5), table.getDouble(4, 1)); //amount

        Assert.assertEquals(4, table.getInteger(1, 2)); //payment_id
        Assert.assertEquals(new Double(22.5), table.getDouble(4, 2)); //amount
    }

    @Test
    public void getPayments_for_Customer_on_Date_range() throws Exception {
        Customer customer = mock(Customer.class);
        when(customer.customerID()).thenReturn(2);
        Date from = TimestampConverter.getDateObject("2018-02-10 00:00:00");
        Date to = TimestampConverter.getDateObject("2018-02-10 23:59:59");
        ObjectTable table = this.revenue_aggregator.getPayments(this.token, customer, from, to);
        Assert.assertEquals(1, table.rowCount());
        Assert.assertEquals(7, table.columnCount());

        Assert.assertEquals(3, table.getInteger(1, 1)); //payment_id
        Assert.assertEquals(new Double(40.5), table.getDouble(4, 1)); //amount
    }

    @Test
    public void getBalance() throws Exception {
        ObjectTable table = this.revenue_aggregator.getBalance(this.token);
        Assert.assertEquals(5, table.columnCount());
        Assert.assertEquals(5, table.rowCount());

        Assert.assertEquals(1, table.getInteger(1, 1)); //customer_id
        Assert.assertEquals(1, table.getInteger(2, 1)); //reservation_count
        Assert.assertEquals(new Double(75.9375), table.getDouble(3, 1)); //total_cost
        Assert.assertEquals(new Double(34.), table.getDouble(4, 1)); //total_paid
        Assert.assertEquals(new Double(-41.9375), table.getDouble(5, 1)); //final_balance

        Assert.assertEquals(2, table.getInteger(1, 2)); //customer_id
        Assert.assertEquals(1, table.getInteger(2, 2)); //reservation_count
        Assert.assertEquals(new Double(0.), table.getDouble(3, 2)); //total_cost
        Assert.assertEquals(new Double(63.), table.getDouble(4, 2)); //total_paid
        Assert.assertEquals(new Double(63.), table.getDouble(5, 2)); //final_balance

        Assert.assertEquals(3, table.getInteger(1, 3)); //customer_id
        Assert.assertEquals(2, table.getInteger(2, 3)); //reservation_count
        Assert.assertEquals(191.625, table.getDouble(3, 3), 0.0001); //total_cost
        Assert.assertEquals(new Double(77.), table.getDouble(4, 3)); //total_paid
        Assert.assertEquals(-114.625, table.getDouble(5, 3), 0.0001); //final_balance

        Assert.assertEquals(4, table.getInteger(1, 4)); //customer_id
        Assert.assertEquals(1, table.getInteger(2, 4)); //reservation_count
        Assert.assertEquals(new Double(63.), table.getDouble(3, 4)); //total_cost
        Assert.assertEquals(new Double(0.), table.getDouble(4, 4)); //total_paid
        Assert.assertEquals(new Double(-63.), table.getDouble(5, 4)); //final_balance

        Assert.assertEquals(5, table.getInteger(1, 5)); //customer_id
        Assert.assertEquals(1, table.getInteger(2, 5)); //reservation_count
        Assert.assertEquals(new Double(2035.), table.getDouble(3, 5)); //total_cost
        Assert.assertEquals(new Double(0.), table.getDouble(4, 5)); //total_paid
        Assert.assertEquals(new Double(-2035.), table.getDouble(5, 5)); //final_balance
    }

    @Test
    public void getBalance_for_Customer() throws Exception {
        Customer customer = mock(Customer.class);
        when(customer.customerID()).thenReturn(3);
        ObjectTable table = this.revenue_aggregator.getBalance(this.token, customer);
        Assert.assertEquals(5, table.columnCount());
        Assert.assertEquals(1, table.rowCount());

        Assert.assertEquals(3, table.getInteger(1, 1)); //customer_id
        Assert.assertEquals(2, table.getInteger(2, 1)); //reservation_count
        Assert.assertEquals(191.625, table.getDouble(3, 1), 0.0001); //total_cost
        Assert.assertEquals(new Double(77.), table.getDouble(4, 1)); //total_paid
        Assert.assertEquals(-114.625, table.getDouble(5, 1), 0.0001); //final_balance
    }

    @Test
    public void getReservationScheduleData() throws Exception { //TODO
        Date from = TimestampConverter.getDateObject("2000-03-10 00:00:00");
        Date to = TimestampConverter.getDateObject("2018-03-10 23:59:59");
        ObjectTable table = this.revenue_aggregator.getReservationScheduleData(this.token, from, to);
        System.out.println(table);
        Assert.fail();
    }

    @Test
    public void getReservationScheduleData_for_Building() throws Exception { //TODO
        Building building = mock(Building.class);
        when(building.id()).thenReturn(1);
        Date from = TimestampConverter.getDateObject("2000-03-10 00:00:00");
        Date to = TimestampConverter.getDateObject("2018-03-10 23:59:59");
        ObjectTable table = this.revenue_aggregator.getReservationScheduleData(this.token, building, from, to);
        System.out.println(table);
        Assert.fail();
    }

    @Test
    public void getReservationScheduleData_for_Floor() throws Exception { //TODO
        Floor floor = mock(Floor.class);
        when(floor.buildingID()).thenReturn(1);
        when(floor.floorID()).thenReturn(1);
        Date from = TimestampConverter.getDateObject("2000-03-10 00:00:00");
        Date to = TimestampConverter.getDateObject("2018-03-10 23:59:59");
        ObjectTable table = this.revenue_aggregator.getReservationScheduleData(this.token, floor, from, to);
        System.out.println(table);
        Assert.fail();
    }

    @Test
    public void getReservationScheduleData_for_Room() throws Exception { //TODO
        Room room = mock(Room.class);
        when(room.buildingID()).thenReturn(1);
        when(room.floorID()).thenReturn(1);
        when(room.id()).thenReturn(1);
        Date from = TimestampConverter.getDateObject("2000-03-10 00:00:00");
        Date to = TimestampConverter.getDateObject("2018-03-10 23:59:59");
        ObjectTable table = this.revenue_aggregator.getReservationScheduleData(this.token, room, from, to);
        System.out.println(table);
        Assert.fail();
    }

    @Test
    public void getReservationData() throws Exception { //TODO
        Date from = TimestampConverter.getDateObject("2000-03-10 00:00:00");
        Date to = TimestampConverter.getDateObject("2018-03-10 23:59:59");
        ObjectTable table = this.revenue_aggregator.getReservationData(this.token, from, to);
        System.out.println(table);
        Assert.fail();
    }

    @Test
    public void getReservationData_for_Building() throws Exception { //TODO
        Date from = TimestampConverter.getDateObject("2018-03-10 00:00:00");
        Date to = TimestampConverter.getDateObject("2018-03-10 23:59:59");
        Building building = mock(Building.class);
        when(building.id()).thenReturn(1);
        ObjectTable table = this.revenue_aggregator.getReservationData(this.token, building, from, to);
        System.out.println(table);
        Assert.fail();
    }

    @Test
    public void getReservationData_for_Floor() throws Exception { //TODO
        Date from = TimestampConverter.getDateObject("2018-03-10 00:00:00");
        Date to = TimestampConverter.getDateObject("2018-03-10 23:59:59");
        Floor floor = mock(Floor.class);
        when(floor.buildingID()).thenReturn(1);
        when(floor.floorID()).thenReturn(1);
        ObjectTable table = this.revenue_aggregator.getReservationData(this.token, floor, from, to);
        System.out.println(table);
        Assert.fail();
    }

    @Test
    public void getReservationData_for_Room() throws Exception { //TODO
        Date from = TimestampConverter.getDateObject("2018-03-10 00:00:00");
        Date to = TimestampConverter.getDateObject("2018-03-10 23:59:59");
        Room room = mock(Room.class);
        when(room.buildingID()).thenReturn(1);
        when(room.floorID()).thenReturn(1);
        when(room.floorID()).thenReturn(1);
        ObjectTable table = this.revenue_aggregator.getReservationData(this.token, room, from, to);
        System.out.println(table);
        Assert.fail();
    }
}
