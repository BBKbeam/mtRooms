package bbk_beam.mtRooms.operation;

import bbk_beam.mtRooms.admin.authentication.Token;
import bbk_beam.mtRooms.db.DbSystemBootstrap;
import bbk_beam.mtRooms.db.IReservationDbAccess;
import bbk_beam.mtRooms.db.IUserAccDbAccess;
import bbk_beam.mtRooms.db.TimestampConverter;
import bbk_beam.mtRooms.db.session.SessionType;
import bbk_beam.mtRooms.reservation.dto.Building;
import bbk_beam.mtRooms.reservation.dto.Floor;
import bbk_beam.mtRooms.reservation.dto.Room;
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

public class LogisticAggregatorTest {
    private DbSystemBootstrap db_bootstrapper = new DbSystemBootstrap();
    private IReservationDbAccess reservationDbAccess;
    private IUserAccDbAccess userAccDbAccess;
    private Token token = new Token("00001", new Date(), Date.from(Instant.now().plus(1, ChronoUnit.DAYS)));
    private LogisticAggregator logisticAggregator;

    @Before
    public void setUp() throws Exception {
        Files.deleteIfExists(Paths.get("logistic_aggregator_test.db"));
        TestDBGenerator testDBGenerator = new TestDBGenerator();
        testDBGenerator.createTestDB("logistic_aggregator_test.db");
        this.db_bootstrapper.init("logistic_aggregator_test.db");
        this.userAccDbAccess = this.db_bootstrapper.getUserAccDbAccess();
        this.reservationDbAccess = this.db_bootstrapper.getReservationDbAccess();
        this.userAccDbAccess.openSession(this.token.getSessionId(), this.token.getExpiry(), SessionType.ADMIN, 1);
        this.logisticAggregator = new LogisticAggregator(reservationDbAccess);
    }

    @After
    public void tearDown() throws Exception {
        this.userAccDbAccess.closeSession(this.token.getSessionId());
        this.userAccDbAccess = null;
        this.reservationDbAccess = null;
        this.logisticAggregator = null;
        Files.deleteIfExists(Paths.get("logistic_aggregator_test.db"));
    }

    @Test
    public void getBuildings() throws Exception {
        ObjectTable table = this.logisticAggregator.getBuildings(this.token);
        Assert.assertEquals("Wrong number of rows", 1, table.rowCount());
        Assert.assertEquals("Wrong number of columns", 8, table.columnCount());
        Assert.assertEquals(1, table.getInteger(1, 1)); //id
        Assert.assertEquals("Test Building", table.getString(2, 1)); //name
    }

    @Test
    public void getFloors() throws Exception {
        Building mock_building = mock(Building.class);
        when(mock_building.id()).thenReturn(1);
        ObjectTable table = this.logisticAggregator.getFloors(this.token, mock_building);
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
        ObjectTable table = this.logisticAggregator.getRooms(this.token, mock_floor);
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
    public void getInfo_by_building() throws Exception {
        Date from = TimestampConverter.getDateObject("2018-03-10 00:00:00");
        Date to = TimestampConverter.getDateObject("2018-03-10 23:59:59");
        ObjectTable table = this.logisticAggregator.getInfo(this.token, 1, from, to);
        Assert.assertEquals(13, table.rowCount());
    }

    @Test
    public void getInfo_by_floor() throws Exception {
        Date from = TimestampConverter.getDateObject("2018-03-10 00:00:00");
        Date to = TimestampConverter.getDateObject("2018-03-10 23:59:59");
        ObjectTable table = this.logisticAggregator.getInfo(this.token, 1, 3, from, to);
        Assert.assertEquals(2, table.rowCount());
    }

    @Test
    public void getInfo_by_room() throws Exception {
        Room mock_room = mock(Room.class);
        when(mock_room.id()).thenReturn(5);
        when(mock_room.floorID()).thenReturn(2);
        when(mock_room.buildingID()).thenReturn(1);
        Date from = TimestampConverter.getDateObject("2018-03-10 00:00:00");
        Date to = TimestampConverter.getDateObject("2018-03-10 23:59:59");
        ObjectTable table = this.logisticAggregator.getInfo(this.token, mock_room, from, to);
        Assert.assertEquals(5, table.rowCount());
    }
}