package bbk_beam.mtRooms.admin.administration.maintenance;

import bbk_beam.mtRooms.admin.authentication.Token;
import bbk_beam.mtRooms.db.DbSystemBootstrap;
import bbk_beam.mtRooms.db.IReservationDbAccess;
import bbk_beam.mtRooms.db.IUserAccDbAccess;
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

public class RealEstateAdministrationTest {
    private DbSystemBootstrap db_bootstrapper = new DbSystemBootstrap();
    private IUserAccDbAccess user_db_access;
    private IReservationDbAccess reservation_db_access;
    private RealEstateAdministration realEstateAdministration;
    private final String session_user_id = "00001";
    private final Date session_expiry = Date.from(Instant.now().plus(1, ChronoUnit.DAYS));
    private Token token = new Token("00001", new Date(), Date.from(Instant.now().plus(1, ChronoUnit.DAYS)));

    @Before
    public void setUp() throws Exception {
        TestDBGenerator testDBGenerator = new TestDBGenerator();
        testDBGenerator.createTestDB("real_estate_maintenance_test.db");
        this.db_bootstrapper.init("real_estate_maintenance_test.db");
        this.user_db_access = this.db_bootstrapper.getUserAccDbAccess();
        this.reservation_db_access = this.db_bootstrapper.getReservationDbAccess();
        this.user_db_access.openSession(this.session_user_id, this.session_expiry, SessionType.ADMIN, 1);
        this.realEstateAdministration = new RealEstateAdministration(this.reservation_db_access);
    }

    @After
    public void tearDown() throws Exception {
        this.user_db_access.closeSession(this.session_user_id);
        this.user_db_access = null;
        this.realEstateAdministration = null;
        Files.deleteIfExists(Paths.get("real_estate_maintenance_test.db"));
    }

    @Test
    public void getBuildings() throws Exception {
        ObjectTable table = this.realEstateAdministration.getBuildings(this.token);
        Assert.assertEquals("Wrong number of rows", 1, table.rowCount());
        Assert.assertEquals("Wrong number of columns", 8, table.columnCount());
        Assert.assertEquals(1, table.getInteger(1, 1)); //id
        Assert.assertEquals("Test Building", table.getString(2, 1)); //name
    }

    @Test
    public void getFloors() throws Exception {
        Building mock_building = mock(Building.class);
        when(mock_building.id()).thenReturn(1);
        ObjectTable table = this.realEstateAdministration.getFloors(this.token, mock_building);
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
        ObjectTable table = this.realEstateAdministration.getRooms(this.token, mock_floor);
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
    public void getRoomPrices() throws Exception {
        Room room = new Room(1, 1, 1, 1, "Small room 1");
        ObjectTable table = this.realEstateAdministration.getRoomPrices(this.token, room);
        Assert.assertEquals("Wrong number of rows", 6, table.rowCount());
        Assert.assertEquals("Wrong number of columns", 4, table.columnCount());
        Assert.assertEquals(1, table.getInteger(1, 1)); //id
        Assert.assertEquals(new Double(40.), table.getDouble(2, 1)); //price
        Assert.assertEquals(2007, table.getInteger(3, 1)); //year
        Assert.assertNull(table.getObject(4, 1)); //reservation_id
        Assert.assertEquals(7, table.getInteger(1, 2)); //id
        Assert.assertEquals(new Double(45.), table.getDouble(2, 2)); //price
        Assert.assertEquals(2008, table.getInteger(3, 2)); //year
        Assert.assertEquals(1, table.getInteger(4, 2)); //reservation_id
    }

    @Test
    public void getRoomCategories() throws Exception {
        ObjectTable table = this.realEstateAdministration.getRoomCategories(this.token);
        Assert.assertEquals("Wrong number of rows", 9, table.rowCount());
        Assert.assertEquals("Wrong number of columns", 7, table.columnCount());
        Assert.assertEquals(1, table.getInteger(1, 1)); //id
        Assert.assertEquals(10, table.getInteger(2, 1)); //capacity
        Assert.assertEquals(10, table.getInteger(3, 1)); //dimension
        Assert.assertEquals(6, table.getInteger(1, 8)); //id
        Assert.assertEquals(100, table.getInteger(2, 8)); //capacity
        Assert.assertEquals(100, table.getInteger(3, 8)); //dimension
    }

    @Test
    public void getRoomDetails() throws Exception {
        Room room = new Room(1, 1, 1, 1, "Small room 1");
        ObjectTable table = this.realEstateAdministration.getRoomDetails(this.token, room);
        Assert.assertEquals("Wrong number of rows", 1, table.rowCount());
        Assert.assertEquals("Wrong number of columns", 20, table.columnCount());
        Assert.assertEquals(1, table.getInteger(1, 1)); //building_id
        Assert.assertEquals(1, table.getInteger(9, 1)); //floor_id
        Assert.assertEquals(1, table.getInteger(11, 1)); //room_id
    }

    @Test
    public void addBuilding() throws Exception {
        Building building = new Building(
                "The Shard",
                "32 London Bridge Street",
                "",
                "SE1 9SG",
                "London",
                "UK",
                "+44 1230 456 789"
        );
        this.realEstateAdministration.add(this.token, building);
        //Check building was added to records
        ObjectTable table = this.reservation_db_access.pullFromDB(
                this.token.getSessionId(),
                "SELECT * FROM Building WHERE name = \"The Shard\""
        );
        Assert.assertEquals(1, table.rowCount());
        Assert.assertEquals(2, table.getInteger(1, 1)); //ID
    }

    @Test
    public void addFloor() throws Exception {
        Floor floor = new Floor(
                1, 4, "4th floor"
        );
        this.realEstateAdministration.add(this.token, floor);
        //Check floor was added to building[1]'s records
        ObjectTable table = this.reservation_db_access.pullFromDB(
                this.token.getSessionId(),
                "SELECT * FROM Floor WHERE description = \"4th floor\""
        );
        Assert.assertEquals(1, table.rowCount());
    }

    @Test
    public void addRoom() throws Exception {
        Room room = new Room(666, 3, 1, 1, "Doom room");
        RoomFixtures fixtures = new RoomFixtures(-1, true, true, true, true);
        this.realEstateAdministration.add(this.token, room, fixtures);
        //Check room and fixtures were added to records
        ObjectTable table1 = this.reservation_db_access.pullFromDB(
                this.token.getSessionId(),
                "SELECT * FROM RoomFixtures WHERE id = 7"
        );
        Assert.assertEquals(1, table1.rowCount());
        ObjectTable table2 = this.reservation_db_access.pullFromDB(
                this.token.getSessionId(),
                "SELECT * FROM Room WHERE id = 666"
        );
        Assert.assertEquals(1, table2.rowCount());
    }

    @Test
    public void addRoomPrice() throws Exception {
        RoomPrice mock_roomPrice = mock(RoomPrice.class);
        //Already existing price/year combo
        when(mock_roomPrice.price()).thenReturn(100.);
        when(mock_roomPrice.year()).thenReturn(2007);
        ObjectTable table1 = this.realEstateAdministration.add(this.token, mock_roomPrice);
        Assert.assertFalse(table1.isEmpty());
        Assert.assertEquals(6, table1.getInteger(1, 1)); //id
        //Non-existing price/year combo
        when(mock_roomPrice.price()).thenReturn(100.);
        when(mock_roomPrice.year()).thenReturn(2015);
        ObjectTable table2 = this.realEstateAdministration.add(this.token, mock_roomPrice);
        Assert.assertFalse(table2.isEmpty());
        System.out.println(table2);
        Assert.assertEquals(13, table2.getInteger(1, 1)); //id
    }

    @Test
    public void addRoomCategory() throws Exception {
        RoomCategory mock_roomCategory = mock(RoomCategory.class);
        //Already existing capacity/dimension
        when(mock_roomCategory.capacity()).thenReturn(10);
        when(mock_roomCategory.dimension()).thenReturn(10);
        ObjectTable table1 = this.realEstateAdministration.add(this.token, mock_roomCategory);
        Assert.assertFalse(table1.isEmpty());
        Assert.assertEquals(1, table1.getInteger(1, 1)); //id
        //Non-existing capacity/dimension
        when(mock_roomCategory.capacity()).thenReturn(10);
        when(mock_roomCategory.dimension()).thenReturn(20);
        ObjectTable table2 = this.realEstateAdministration.add(this.token, mock_roomCategory);
        Assert.assertFalse(table2.isEmpty());
        Assert.assertEquals(8, table2.getInteger(1, 1)); //id
    }

    @Test
    public void updateBuilding() throws Exception {
        Building building = new Building(
                1,
                "The Shard",
                "32 London Bridge Street",
                "-",
                "SE1 9SG",
                "London",
                "UK",
                "+44 1230 456 789"
        );
        this.realEstateAdministration.update(this.token, building);
        ObjectTable table = this.realEstateAdministration.getBuildings(this.token);
        Assert.assertFalse(table.isEmpty());
        Assert.assertEquals(1, table.getInteger(1, 1)); //id
        Assert.assertEquals("The Shard", table.getString(2, 1)); //name
        Assert.assertEquals("32 London Bridge Street", table.getString(3, 1)); //address1
        Assert.assertEquals("-", table.getString(4, 1)); //address2
        Assert.assertEquals("London", table.getString(5, 1)); //city
        Assert.assertEquals("SE1 9SG", table.getString(6, 1)); //postcode
        Assert.assertEquals("UK", table.getString(7, 1)); //country
        Assert.assertEquals("+44 1230 456 789", table.getString(8, 1)); //telephone
    }

    @Test
    public void updateFloor() throws Exception {
        Floor floor = new Floor(1, 1, "Updated description");
        this.realEstateAdministration.update(this.token, floor);
        Building mock_building = mock(Building.class);
        when(mock_building.id()).thenReturn(1);
        ObjectTable table = this.realEstateAdministration.getFloors(this.token, mock_building);
        Assert.assertFalse(table.isEmpty());
        Assert.assertEquals(1, table.getInteger(1, 1));
        Assert.assertEquals(1, table.getInteger(2, 1));
        Assert.assertEquals("Updated description", table.getString(3, 1));
    }

    @Test
    public void updateRoom() throws Exception {
        Room room = new Room(1, 1, 1, 1, "Updated description");
        this.realEstateAdministration.update(this.token, room);
        Floor mock_floor = mock(Floor.class);
        when(mock_floor.buildingID()).thenReturn(1);
        when(mock_floor.floorID()).thenReturn(1);
        ObjectTable table = this.realEstateAdministration.getRooms(this.token, mock_floor);
        Assert.assertFalse(table.isEmpty());
        Assert.assertEquals(1, table.getInteger(1, 1));
        Assert.assertEquals(1, table.getInteger(2, 1));
        Assert.assertEquals(1, table.getInteger(3, 1));
        Assert.assertEquals(1, table.getInteger(4, 1));
        Assert.assertEquals("Updated description", table.getString(5, 1));
    }

    @Test
    public void removeBuilding() throws Exception {
        Assert.assertEquals(1, this.realEstateAdministration.getBuildings(this.token).rowCount());
        Building building = new Building(
                2,
                "The Shard",
                "32 London Bridge Street",
                "-",
                "SE1 9SG",
                "London",
                "UK",
                "+44 1230 456 789"
        );
        this.realEstateAdministration.add(this.token, building);
        Assert.assertEquals("Shard building not created", 2, this.realEstateAdministration.getBuildings(this.token).rowCount());
        this.realEstateAdministration.remove(this.token, building);
        ObjectTable table = this.realEstateAdministration.getBuildings(this.token);
        Assert.assertEquals(1, table.rowCount());
        Assert.assertEquals("Test Building", table.getString(2, 1)); //name of old building
    }

    @Test
    public void removeBuilding_TiedToReservationFail() throws Exception {
        Building building = mock(Building.class);
        when(building.id()).thenReturn(1);
        Assert.assertFalse(this.realEstateAdministration.remove(this.token, building));
    }

    @Test
    public void removeBuilding_TiedToFloorFail() throws Exception {
        Building building = new Building(
                2,
                "The Shard",
                "32 London Bridge Street",
                "-",
                "SE1 9SG",
                "London",
                "UK",
                "+44 1230 456 789"
        );
        Floor floor = new Floor(2, 1, "Ground floor of the Shard");
        this.realEstateAdministration.add(this.token, building);
        this.realEstateAdministration.add(this.token, floor);
        Assert.assertEquals(2, this.realEstateAdministration.getBuildings(this.token).rowCount());
        Assert.assertEquals(1, this.realEstateAdministration.getFloors(this.token, building).rowCount());
        Assert.assertFalse(this.realEstateAdministration.remove(this.token, building));
    }

    @Test
    public void removeFloor() throws Exception {
        Building building = mock(Building.class);
        when(building.id()).thenReturn(1);
        Floor floor = new Floor(1, 10, "Tester floor");
        Assert.assertEquals(3, this.realEstateAdministration.getFloors(this.token, building).rowCount());
        this.realEstateAdministration.add(this.token, floor);
        Assert.assertEquals(4, this.realEstateAdministration.getFloors(this.token, building).rowCount());
        this.realEstateAdministration.remove(this.token, floor);
        Assert.assertEquals(3, this.realEstateAdministration.getFloors(this.token, building).rowCount());
    }

    @Test
    public void removeFloor_TiedToReservationFail() throws Exception {
        Floor mock_floor = mock(Floor.class);
        when(mock_floor.buildingID()).thenReturn(1);
        when(mock_floor.floorID()).thenReturn(1);
        Assert.assertFalse(this.realEstateAdministration.remove(this.token, mock_floor));
    }

    @Test
    public void removeFloor_TiedToRoomFail() throws Exception {
        Floor floor = new Floor(1, 10, "Tester floor");
        Room room = new Room(1, 10, 1, 1, "Tester floor room");
        this.realEstateAdministration.add(this.token, floor);
        this.realEstateAdministration.add(
                this.token,
                room,
                new RoomFixtures(-1, true, true, true, false)
        );
        //Testing
        Assert.assertFalse(this.realEstateAdministration.remove(this.token, floor));
    }

    @Test
    public void removeRoom() throws Exception {
        Room mock_room = mock(Room.class);
        when(mock_room.buildingID()).thenReturn(1);
        when(mock_room.floorID()).thenReturn(1);
        when(mock_room.id()).thenReturn(2);
        Assert.assertTrue(this.realEstateAdministration.remove(this.token, mock_room));
    }

    @Test
    public void removeRoom_TiedToReservationFail() throws Exception {
        Room mock_room = mock(Room.class);
        when(mock_room.buildingID()).thenReturn(1);
        when(mock_room.floorID()).thenReturn(1);
        when(mock_room.id()).thenReturn(1);
        Assert.assertFalse(this.realEstateAdministration.remove(this.token, mock_room));
    }

    @Test
    public void removeRoomPrice() throws Exception {
        RoomPrice mock_roomPrice = mock(RoomPrice.class);
        //Used RoomPriceUsage
        when(mock_roomPrice.id()).thenReturn(7);
        Assert.assertFalse(this.realEstateAdministration.remove(this.token, mock_roomPrice));
        //Unused RoomPriceUsage
        when(mock_roomPrice.id()).thenReturn(1);
        Assert.assertTrue(this.realEstateAdministration.remove(this.token, mock_roomPrice));
    }

    @Test
    public void removeRoomCategory() throws Exception {
        RoomCategory mock_category = mock(RoomCategory.class);
        when(mock_category.id()).thenReturn(7);
        Assert.assertEquals(9, this.realEstateAdministration.getRoomCategories(this.token).rowCount());
        this.realEstateAdministration.remove(this.token, mock_category);
        Assert.assertEquals(8, this.realEstateAdministration.getRoomCategories(this.token).rowCount());
    }

    @Test
    public void removeRoomCategory_TiedToRoomFail() throws Exception {
        RoomCategory mock_category = mock(RoomCategory.class);
        when(mock_category.id()).thenReturn(1);
        Assert.assertFalse(this.realEstateAdministration.remove(this.token, mock_category));
    }
}