package bbk_beam.mtRooms.revenue;

import bbk_beam.mtRooms.admin.authentication.Token;
import bbk_beam.mtRooms.db.DbSystemBootstrap;
import bbk_beam.mtRooms.db.TimestampConverter;
import bbk_beam.mtRooms.db.session.SessionType;
import bbk_beam.mtRooms.reservation.dto.Building;
import bbk_beam.mtRooms.reservation.dto.Customer;
import bbk_beam.mtRooms.reservation.dto.Floor;
import bbk_beam.mtRooms.reservation.dto.Room;
import bbk_beam.mtRooms.revenue.dto.CustomerBalance;
import bbk_beam.mtRooms.revenue.dto.ReservationBalance;
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
    public void getCustomerBalance_for_all_customers() throws Exception { //TODO
        List<SimpleCustomerBalance> list = this.report_creator.getCustomerBalance(this.token);
        for (SimpleCustomerBalance balance : list)
            System.out.println(balance);
        Assert.fail();
    }

    @Test
    public void getCustomerBalance_for_single_customer() throws Exception { //TODO
        Customer customer = mock(Customer.class);
        when(customer.customerID()).thenReturn(3);
        CustomerBalance customerBalance = this.report_creator.getCustomerBalance(this.token, customer);
        for (ReservationBalance balance : customerBalance.getReservationBalances())
            System.out.println(balance);
        System.out.println("Balance=" + customerBalance.getBalance());
        Assert.fail();
    }

    @Test
    public void getOccupancy() throws Exception { //TODO
        Date from = TimestampConverter.getDateObject("2000-03-10 00:00:00");
        Date to = TimestampConverter.getDateObject("2018-03-10 23:59:59");
        List<RoomOccupancy> occupancies = this.report_creator.getOccupancy(this.token, from, to).getOccupancies();
        for (RoomOccupancy occupancy : occupancies)
            System.out.println(occupancy);
        Assert.fail();
    }

    @Test
    public void getOccupancy_for_Building() throws Exception { //TODO
        Date from = TimestampConverter.getDateObject("2000-03-10 00:00:00");
        Date to = TimestampConverter.getDateObject("2018-03-10 23:59:59");
        Building building = mock(Building.class);
        when(building.id()).thenReturn(1);
        List<RoomOccupancy> occupancies = this.report_creator.getOccupancy(this.token, building, from, to).getOccupancies();
        for (RoomOccupancy occupancy : occupancies)
            System.out.println(occupancy);
        Assert.fail();
    }

    @Test
    public void getOccupancy_for_Floor() throws Exception { //TODO
        Date from = TimestampConverter.getDateObject("2000-03-10 00:00:00");
        Date to = TimestampConverter.getDateObject("2018-03-10 23:59:59");
        Floor floor = mock(Floor.class);
        when(floor.buildingID()).thenReturn(1);
        when(floor.floorID()).thenReturn(1);
        List<RoomOccupancy> occupancies = this.report_creator.getOccupancy(this.token, floor, from, to).getOccupancies();
        for (RoomOccupancy occupancy : occupancies)
            System.out.println(occupancy);
        Assert.fail();
    }

    @Test
    public void getOccupancy_for_Room() throws Exception { //TODO
        Date from = TimestampConverter.getDateObject("2000-03-10 00:00:00");
        Date to = TimestampConverter.getDateObject("2018-03-10 23:59:59");
        Room room = mock(Room.class);
        when(room.buildingID()).thenReturn(1);
        when(room.floorID()).thenReturn(1);
        when(room.id()).thenReturn(1);
        List<RoomOccupancy> occupancies = this.report_creator.getOccupancy(this.token, room, from, to).getOccupancies();
        for (RoomOccupancy occupancy : occupancies)
            System.out.println(occupancy);
        Assert.fail();
    }

    @Test
    public void getRevenueReport() throws Exception { //TODO
        Assert.fail();
    }

    @Test
    public void getRevenueReport_for_Building() throws Exception { //TODO
        Assert.fail();
    }

    @Test
    public void getRevenueReport_for_Floor() throws Exception { //TODO
        Assert.fail();
    }

    @Test
    public void getRevenueReport_for_Room() throws Exception { //TODO
        Assert.fail();
    }
}