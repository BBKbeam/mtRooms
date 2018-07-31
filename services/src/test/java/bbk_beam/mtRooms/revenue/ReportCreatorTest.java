package bbk_beam.mtRooms.revenue;

import bbk_beam.mtRooms.admin.authentication.Token;
import bbk_beam.mtRooms.db.DbSystemBootstrap;
import bbk_beam.mtRooms.db.TimestampConverter;
import bbk_beam.mtRooms.db.session.SessionType;
import bbk_beam.mtRooms.reservation.dto.*;
import bbk_beam.mtRooms.revenue.dto.CustomerBalance;
import bbk_beam.mtRooms.revenue.dto.Invoice;
import bbk_beam.mtRooms.revenue.dto.RoomOccupancy;
import bbk_beam.mtRooms.revenue.dto.SimpleCustomerBalance;
import bbk_beam.mtRooms.test_data.TestDBGenerator;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ReportCreatorTest {
    private DbSystemBootstrap db_bootstrapper = new DbSystemBootstrap();
    private ReportCreator report_creator;
    private Token token = new Token("00001", new Date(), Date.from(Instant.now().plus(1, ChronoUnit.DAYS)));

    @Before
    public void setUp() throws Exception {
        Files.deleteIfExists(Paths.get("report_creator_test.db"));
        TestDBGenerator testDBGenerator = new TestDBGenerator();
        testDBGenerator.createTestDB("report_creator_test.db");
        this.db_bootstrapper.init("report_creator_test.db");
        this.db_bootstrapper.getUserAccDbAccess().openSession(this.token.getSessionId(), this.token.getExpiry(), SessionType.ADMIN, 1);
        this.report_creator = new ReportCreator(this.db_bootstrapper.getReservationDbAccess());
    }

    @After
    public void tearDown() throws Exception {
        Files.deleteIfExists(Paths.get("report_creator_test.db"));
        this.db_bootstrapper = null;
        this.report_creator = null;
    }

    @Test
    public void getBuildings() throws Exception {
        List<Building> buildings = this.report_creator.getBuildings(this.token);
        Assert.assertEquals(1, buildings.size());
        Assert.assertEquals(new Integer(1), buildings.get(0).id());
        Assert.assertEquals("Test Building", buildings.get(0).name());
    }

    @Test
    public void getFloors() throws Exception {
        Building building = mock(Building.class);
        when(building.id()).thenReturn(1);
        List<Floor> floors = this.report_creator.getFloors(this.token, building);
//        for(Floor floor : floors)
//            System.out.println(floor);
        Assert.assertEquals(3, floors.size());
        Assert.assertEquals("Ground level", floors.get(0).description());
        Assert.assertEquals("First floor", floors.get(1).description());
        Assert.assertEquals("Second floor", floors.get(2).description());
    }

    @Test
    public void getRooms() throws Exception {
        Floor floor = mock(Floor.class);
        when(floor.floorID()).thenReturn(1);
        when(floor.buildingID()).thenReturn(1);
        List<Room> rooms = this.report_creator.getRooms(this.token, floor);
//        for (Room room : rooms)
//            System.out.println(room);
        Assert.assertEquals(3, rooms.size());
        Assert.assertEquals(new Integer(1), rooms.get(0).id());
        Assert.assertEquals(new Integer(2), rooms.get(1).id());
        Assert.assertEquals(new Integer(3), rooms.get(2).id());
    }

    @Test
    public void getCustomerBalance_for_all_customers() throws Exception {
        List<SimpleCustomerBalance> list = this.report_creator.getCustomerBalance(this.token);
//        for (SimpleCustomerBalance balance : list)
//            System.out.println(balance);
        Assert.assertEquals(5, list.size());
        int check_count = 0;
        for (SimpleCustomerBalance balance : list) {
            if (balance.getCustomerID().equals(1)) {
                Assert.assertEquals("Wrong reservation count for customer[1]", 1, balance.getReservationCount());
                Assert.assertEquals("Wrong total cost for customer [1]", (Double) 75.9375, balance.getTotalCost());
                Assert.assertEquals("Wrong total paid for customer [1]", (Double) 34., balance.getTotalPaid());
                Assert.assertEquals("Wrong account balance for customer [1]", (Double) (-41.9375), balance.getBalance());
                check_count++;
            }
            if (balance.getCustomerID().equals(2)) {
                Assert.assertEquals("Wrong reservation count for customer[2]", 1, balance.getReservationCount());
                Assert.assertEquals("Wrong total cost for customer [2]", (Double) 0., balance.getTotalCost());
                Assert.assertEquals("Wrong total paid for customer [2]", (Double) 63., balance.getTotalPaid());
                Assert.assertEquals("Wrong account balance for customer [2]", (Double) 63., balance.getBalance());
                check_count++;
            }
            if (balance.getCustomerID().equals(3)) {
                Assert.assertEquals("Wrong reservation count for customer[3]", 2, balance.getReservationCount());
                Assert.assertEquals("Wrong total cost for customer [3]", 191.625, balance.getTotalCost(), 0.0001);
                Assert.assertEquals("Wrong total paid for customer [3]", (Double) 77., balance.getTotalPaid());
                Assert.assertEquals("Wrong account balance for customer [3]", -114.625, balance.getBalance(), 0.0001);
                check_count++;
            }
            if (balance.getCustomerID().equals(4)) {
                Assert.assertEquals("Wrong reservation count for customer[4]", 1, balance.getReservationCount());
                Assert.assertEquals("Wrong total cost for customer [4]", (Double) 63., balance.getTotalCost());
                Assert.assertEquals("Wrong total paid for customer [4]", (Double) 0., balance.getTotalPaid());
                Assert.assertEquals("Wrong account balance for customer [4]", (Double) (-63.), balance.getBalance());
                check_count++;
            }
            if (balance.getCustomerID().equals(5)) {
                Assert.assertEquals("Wrong reservation count for customer[5]", 1, balance.getReservationCount());
                Assert.assertEquals("Wrong total cost for customer [5]", (Double) 2035., balance.getTotalCost());
                Assert.assertEquals("Wrong total paid for customer [5]", (Double) 0., balance.getTotalPaid());
                Assert.assertEquals("Wrong account balance for customer [5]", (Double) (-2035.), balance.getBalance());
                check_count++;
            }
        }
        Assert.assertEquals("Number of items checked not equal to list content.", list.size(), check_count);
    }

    @Test
    public void getCustomerBalance_for_single_customer() throws Exception {
        Customer customer = mock(Customer.class);
        when(customer.customerID()).thenReturn(3);
        CustomerBalance customerBalance = this.report_creator.getCustomerBalance(this.token, customer);
//        for (ReservationBalance balance : customerBalance.getReservationBalances())
//            System.out.println(balance);
        Assert.assertEquals("Wrong number of reservations", 2, customerBalance.getReservationBalances().size());
        Assert.assertEquals("Wrong reservation count", 2, customerBalance.getReservationCount());
        Assert.assertEquals("Wrong total cost", 191.625, customerBalance.getTotalCost(), 0.0001);
        Assert.assertEquals("Wrong total paid", (Double) 77., customerBalance.getTotalPaid());
        Assert.assertEquals("Wrong account balance", -114.625, customerBalance.getBalance(), 0.0001);
    }

    @Test
    public void getSimpleCustomerBalance_for_single_customer() throws Exception {
        Customer customer = mock(Customer.class);
        when(customer.customerID()).thenReturn(3);
        SimpleCustomerBalance customerBalance = this.report_creator.getSimpleCustomerBalance(this.token, customer);
//        System.out.println(customerBalance);
        Assert.assertEquals("Wrong reservation count", 2, customerBalance.getReservationCount());
        Assert.assertEquals("Wrong total cost", 191.625, customerBalance.getTotalCost(), 0.0001);
        Assert.assertEquals("Wrong total paid", (Double) 77., customerBalance.getTotalPaid());
        Assert.assertEquals("Wrong account balance", -114.625, customerBalance.getBalance(), 0.0001);
    }

    @Test
    public void getOccupancy() throws Exception { //TODO
        Date from = TimestampConverter.getDateObject("2000-03-10 00:00:00");
        Date to = TimestampConverter.getDateObject("2018-03-10 23:59:59");
        List<RoomOccupancy> occupancies = this.report_creator.getOccupancy(this.token, from, to).getOccupancies();
//        for (RoomOccupancy occupancy : occupancies)
//            System.out.println(occupancy);
        Assert.assertEquals(6, occupancies.size());
        Assert.assertEquals(12, occupancies.get(0).getOccupancy().size());
        Assert.assertEquals(8, occupancies.get(1).getOccupancy().size());
        Assert.assertEquals(14, occupancies.get(2).getOccupancy().size());
        Assert.assertEquals(16, occupancies.get(3).getOccupancy().size());
        Assert.assertEquals(9, occupancies.get(4).getOccupancy().size());
        Assert.assertEquals(8, occupancies.get(5).getOccupancy().size());
    }

    @Test
    public void getOccupancy_for_Building() throws Exception { //TODO
        Date from = TimestampConverter.getDateObject("2000-03-10 00:00:00");
        Date to = TimestampConverter.getDateObject("2018-03-10 23:59:59");
        Building building = mock(Building.class);
        when(building.id()).thenReturn(1);
        List<RoomOccupancy> occupancies = this.report_creator.getOccupancy(this.token, building, from, to).getOccupancies();
//        for (RoomOccupancy occupancy : occupancies)
//            System.out.println(occupancy);
        Assert.assertEquals(6, occupancies.size());
        Assert.assertEquals(12, occupancies.get(0).getOccupancy().size());
        Assert.assertEquals(8, occupancies.get(1).getOccupancy().size());
        Assert.assertEquals(14, occupancies.get(2).getOccupancy().size());
        Assert.assertEquals(16, occupancies.get(3).getOccupancy().size());
        Assert.assertEquals(9, occupancies.get(4).getOccupancy().size());
        Assert.assertEquals(8, occupancies.get(5).getOccupancy().size());
    }

    @Test
    public void getOccupancy_for_Floor() throws Exception {
        Date from = TimestampConverter.getDateObject("2000-03-10 00:00:00");
        Date to = TimestampConverter.getDateObject("2018-03-10 23:59:59");
        Floor floor = mock(Floor.class);
        when(floor.buildingID()).thenReturn(1);
        when(floor.floorID()).thenReturn(1);
        List<RoomOccupancy> occupancies = this.report_creator.getOccupancy(this.token, floor, from, to).getOccupancies();
//        for (RoomOccupancy ro : occupancies)
//            System.out.println(ro);
        Assert.assertEquals(2, occupancies.size());
        Assert.assertEquals(12, occupancies.get(0).getOccupancy().size());
        Assert.assertEquals(8, occupancies.get(1).getOccupancy().size());
    }

    @Test
    public void getOccupancy_for_Room() throws Exception {
        Date from = TimestampConverter.getDateObject("2000-03-10 00:00:00");
        Date to = TimestampConverter.getDateObject("2018-03-10 23:59:59");
        Room room = mock(Room.class);
        when(room.buildingID()).thenReturn(1);
        when(room.floorID()).thenReturn(1);
        when(room.id()).thenReturn(1);
        List<RoomOccupancy> occupancies = this.report_creator.getOccupancy(this.token, room, from, to).getOccupancies();
//        for (RoomOccupancy ro : occupancies)
//            System.out.println(ro);
        Assert.assertEquals(1, occupancies.size());
        RoomOccupancy occupancy = occupancies.get(0);
        Assert.assertEquals(12, occupancy.getOccupancy().size());
    }

    @Test
    public void createInvoice() throws Exception {
        Customer expected_customer = new Customer(
                1, 1,
                TimestampConverter.getDateObject("2015-10-15 16:15:12"),
                "Mrs", "Joanne", "Bouvier",
                "Flat 4", "21 big road", "W1 4AQ", "London", "London", "UK",
                "+44 9876 532 123", null, "jbouvier@mail.com"
        );

        List<RoomReservation> expected_room_reservations = new ArrayList<>();
        expected_room_reservations.add(new RoomReservation(
                new Room(1, 1, 1, 1, "Small room 1"),
                TimestampConverter.getDateObject("2018-02-09 10:15:00"),
                TimestampConverter.getDateObject("2018-02-09 12:30:00"),
                10, false, "",
                new RoomPrice(7, 45., 2008),
                false

        ));
        Reservation expected_reservation = new Reservation(
                2, TimestampConverter.getDateObject("2018-02-09 10:00:00"), 1,
                new Discount(2, 25., new DiscountCategory(2, "Student")),
                expected_room_reservations
        );

        //Testing
        Invoice invoice = this.report_creator.createInvoice(this.token, 2);
        Assert.assertEquals(expected_customer, invoice.customer());
        Assert.assertEquals(expected_reservation, invoice.reservation());
        Assert.assertEquals(1, invoice.reservationBalance().getPayments().size());
        Assert.assertEquals((Double) 101.25, invoice.reservationBalance().getRawCost());
        Assert.assertEquals((Double) 25., invoice.reservationBalance().getDiscount().rate());
        Assert.assertEquals((Double) 75.9375, invoice.reservationBalance().getDiscountedCost());
        Assert.assertEquals((Double) 34., invoice.reservationBalance().getPaymentsTotal());
        Assert.assertEquals((Double) (-41.9375), invoice.reservationBalance().getBalance());
        Assert.assertEquals((Integer) 1, invoice.customerBalance().getCustomerID());
        Assert.assertEquals(1, invoice.customerBalance().getReservationCount());
        Assert.assertEquals((Double) 75.9375, invoice.customerBalance().getTotalCost());
        Assert.assertEquals((Double) 34., invoice.customerBalance().getTotalPaid());
        Assert.assertEquals((Double) (-41.9375), invoice.customerBalance().getBalance());
    }
}