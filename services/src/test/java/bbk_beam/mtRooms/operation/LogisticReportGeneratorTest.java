package bbk_beam.mtRooms.operation;

import bbk_beam.mtRooms.admin.authentication.Token;
import bbk_beam.mtRooms.db.DbSystemBootstrap;
import bbk_beam.mtRooms.db.IReservationDbAccess;
import bbk_beam.mtRooms.db.IUserAccDbAccess;
import bbk_beam.mtRooms.db.TimestampConverter;
import bbk_beam.mtRooms.db.session.SessionType;
import bbk_beam.mtRooms.operation.dto.LogisticsInfo;
import bbk_beam.mtRooms.reservation.dto.Building;
import bbk_beam.mtRooms.reservation.dto.Floor;
import bbk_beam.mtRooms.reservation.dto.Room;
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

public class LogisticReportGeneratorTest {
    private DbSystemBootstrap db_bootstrapper = new DbSystemBootstrap();
    private IReservationDbAccess reservationDbAccess;
    private IUserAccDbAccess userAccDbAccess;
    private Token token = new Token("00001", new Date(), Date.from(Instant.now().plus(1, ChronoUnit.DAYS)));
    private LogisticAggregator logisticAggregator;
    private LogisticReportGenerator logisticReportGenerator;

    @Before
    public void setUp() throws Exception {
        Files.deleteIfExists(Paths.get("logistic_report_generator_test.db"));
        TestDBGenerator testDBGenerator = new TestDBGenerator();
        testDBGenerator.createTestDB("logistic_report_generator_test.db");
        this.db_bootstrapper.init("logistic_report_generator_test.db");
        this.userAccDbAccess = this.db_bootstrapper.getUserAccDbAccess();
        this.reservationDbAccess = this.db_bootstrapper.getReservationDbAccess();
        this.userAccDbAccess.openSession(this.token.getSessionId(), this.token.getExpiry(), SessionType.ADMIN, 1);
        this.logisticAggregator = new LogisticAggregator(reservationDbAccess);
        this.logisticReportGenerator = new LogisticReportGenerator(this.logisticAggregator);
    }

    @After
    public void tearDown() throws Exception {
        this.userAccDbAccess.closeSession(this.token.getSessionId());
        this.userAccDbAccess = null;
        this.reservationDbAccess = null;
        this.logisticAggregator = null;
        this.logisticReportGenerator = null;
        Files.deleteIfExists(Paths.get("logistic_report_generator_test.db"));
    }

    @Test
    public void getBuildings() throws Exception {
        List<Building> buildings = this.logisticReportGenerator.getBuildings(this.token);
        Assert.assertEquals(1, buildings.size());
        Assert.assertEquals(new Integer(1), buildings.get(0).id());
        Assert.assertEquals("Test Building", buildings.get(0).name());
    }

    @Test
    public void getFloors() throws Exception {
        Building building = mock(Building.class);
        when(building.id()).thenReturn(1);
        List<Floor> floors = this.logisticReportGenerator.getFloors(this.token, building);
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
        List<Room> rooms = this.logisticReportGenerator.getRooms(this.token, floor);
//        for (Room room : rooms)
//            System.out.println(room);
        Assert.assertEquals(3, rooms.size());
        Assert.assertEquals(new Integer(1), rooms.get(0).id());
        Assert.assertEquals(new Integer(2), rooms.get(1).id());
        Assert.assertEquals(new Integer(3), rooms.get(2).id());
    }

    @Test
    public void getInfo_by_building() throws Exception {
        Date from = TimestampConverter.getDateObject("2018-03-10 00:00:00");
        Date to = TimestampConverter.getDateObject("2018-03-10 23:59:59");
        LogisticsInfo report = this.logisticReportGenerator.getInfo(this.token, 1, from, to);
        Assert.assertEquals(13, report.entryCount());
    }

    @Test
    public void getInfo_by_floor() throws Exception {
        Date from = TimestampConverter.getDateObject("2018-03-10 00:00:00");
        Date to = TimestampConverter.getDateObject("2018-03-10 23:59:59");
        LogisticsInfo report = this.logisticReportGenerator.getInfo(this.token, 1, 3, from, to);
        Assert.assertEquals(2, report.entryCount());
    }

    @Test
    public void getInfo_by_room() throws Exception {
        Room mock_room = mock(Room.class);
        when(mock_room.id()).thenReturn(5);
        when(mock_room.floorID()).thenReturn(2);
        when(mock_room.buildingID()).thenReturn(1);
        Date from = TimestampConverter.getDateObject("2018-03-10 00:00:00");
        Date to = TimestampConverter.getDateObject("2018-03-10 23:59:59");
        LogisticsInfo report = this.logisticReportGenerator.getInfo(this.token, mock_room, from, to);
        Assert.assertEquals(5, report.entryCount());
    }
}