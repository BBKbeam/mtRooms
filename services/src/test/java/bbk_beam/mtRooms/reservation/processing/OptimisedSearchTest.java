package bbk_beam.mtRooms.reservation.processing;

import bbk_beam.mtRooms.admin.authentication.Token;
import bbk_beam.mtRooms.db.DbSystemBootstrap;
import bbk_beam.mtRooms.db.IReservationDbAccess;
import bbk_beam.mtRooms.db.IUserAccDbAccess;
import bbk_beam.mtRooms.db.TimestampConverter;
import bbk_beam.mtRooms.db.session.SessionType;
import bbk_beam.mtRooms.reservation.delegate.ReservationDbDelegate;
import bbk_beam.mtRooms.reservation.dto.Room;
import bbk_beam.mtRooms.reservation.dto.RoomProperty;
import bbk_beam.mtRooms.reservation.scheduling.ScheduleCache;
import bbk_beam.mtRooms.reservation.scheduling.datastructure.TimeSpan;
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
import java.util.HashMap;
import java.util.List;

public class OptimisedSearchTest {
    private DbSystemBootstrap db_bootstrapper = new DbSystemBootstrap();
    private IReservationDbAccess reservationDbAccess;
    private IUserAccDbAccess userAccDbAccess;
    private Token token = new Token("00001", new Date(), Date.from(Instant.now().plus(1, ChronoUnit.DAYS)));
    private ReservationDbDelegate reservationDbDelegate;
    private OptimisedSearch optimisedSearch;

    @Before
    public void setUp() throws Exception {
        Files.deleteIfExists(Paths.get("optimised_search_test.db"));
        TestDBGenerator testDBGenerator = new TestDBGenerator();
        testDBGenerator.createTestDB("optimised_search_test.db");
        this.db_bootstrapper.init("optimised_search_test.db");
        this.userAccDbAccess = this.db_bootstrapper.getUserAccDbAccess();
        this.reservationDbAccess = this.db_bootstrapper.getReservationDbAccess();
        this.userAccDbAccess.openSession(this.token.getSessionId(), this.token.getExpiry(), SessionType.ADMIN, 1);
        this.reservationDbDelegate = new ReservationDbDelegate(reservationDbAccess);
        this.optimisedSearch = new OptimisedSearch(this.reservationDbDelegate, new ScheduleCache());
    }

    @After
    public void tearDown() throws Exception {
        this.userAccDbAccess.closeSession(this.token.getSessionId());
        this.userAccDbAccess = null;
        this.reservationDbAccess = null;
        this.reservationDbDelegate = null;
        this.optimisedSearch = null;
        Files.deleteIfExists(Paths.get("optimised_search_test.db"));
    }

    /**
     * Searches for any Rooms that match the properties given
     */
    @Test
    public void search1() throws Exception {
        RoomProperty property = new RoomProperty(
                RoomProperty.Trilean.TRUE, //fixed chairs
                RoomProperty.Trilean.UNDEFINED,
                RoomProperty.Trilean.UNDEFINED,
                RoomProperty.Trilean.UNDEFINED,
                -1,
                -1
        );
        List<Room> list = this.optimisedSearch.search(this.token, property);
        Assert.assertEquals(2, list.size());
        int validator = 0;
        for (Room room : list) {
            if (room.id() == 1 && room.floorID() == 1 && room.buildingID() == 1)
                validator++;
            if (room.id() == 8 && room.floorID() == 3 && room.buildingID() == 1)
                validator++;
        }
        Assert.assertEquals("Found " + validator + "/2 matches in list.", 2, validator);
    }

    /**
     * Searches for Rooms that match the properties given in a building
     */
    @Test
    public void search2() throws Exception {
        RoomProperty property = new RoomProperty(
                RoomProperty.Trilean.UNDEFINED,
                RoomProperty.Trilean.TRUE, //catering space
                RoomProperty.Trilean.UNDEFINED,
                RoomProperty.Trilean.UNDEFINED,
                -1,
                -1
        );
        List<Room> list = this.optimisedSearch.search(this.token, 1, property);
        Assert.assertEquals(4, list.size());
        int validator = 0;
        for (Room room : list) {
            if (room.id() == 4 && room.floorID() == 2 && room.buildingID() == 1)
                validator++;
            if (room.id() == 5 && room.floorID() == 2 && room.buildingID() == 1)
                validator++;
            if (room.id() == 6 && room.floorID() == 2 && room.buildingID() == 1)
                validator++;
            if (room.id() == 7 && room.floorID() == 3 && room.buildingID() == 1)
                validator++;
        }
        Assert.assertEquals("Found " + validator + "/4 matches in list.", 4, validator);
    }

    /**
     * Searches for Rooms that match the properties given in a floor
     */
    @Test
    public void search3() throws Exception {
        RoomProperty property = new RoomProperty(
                RoomProperty.Trilean.UNDEFINED,
                RoomProperty.Trilean.UNDEFINED,
                RoomProperty.Trilean.UNDEFINED,
                RoomProperty.Trilean.TRUE, //projector
                -1,
                -1
        );
        List<Room> list = this.optimisedSearch.search(this.token, 1, 1, property);
        Assert.assertEquals(1, list.size());
        int validator = 0;
        for (Room room : list) {
            if (room.id() == 3 && room.floorID() == 1 && room.buildingID() == 1)
                validator++;
        }
        Assert.assertEquals("Found " + validator + "/1 matches in list.", 1, validator);
    }

    /**
     * Searches for available times for a Room
     */
    @Test
    public void search4() throws Exception {
        Room room = new Room(5, 2, 1, 3, "Medium room 3");
        Date start1 = TimestampConverter.getDateObject("2018-03-10 09:00:00");
        Date end1 = TimestampConverter.getDateObject("2018-03-10 21:00:00"); //24x30mns
        List<TimeSpan> free_slots = this.optimisedSearch.search(this.token, room, start1, end1);

        Assert.assertEquals(10, free_slots.size()); //24-(2+1+1+4+2)
    }

    /**
     * Searches for available rooms in a building within a time frame
     */
    @Test
    public void search5() throws Exception {
        RoomProperty property = new RoomProperty(
                RoomProperty.Trilean.UNDEFINED,
                RoomProperty.Trilean.UNDEFINED,
                RoomProperty.Trilean.UNDEFINED,
                RoomProperty.Trilean.UNDEFINED,
                -1,
                -1
        );
        Date start1 = TimestampConverter.getDateObject("2018-03-10 16:00:00");
        Date end1 = TimestampConverter.getDateObject("2018-03-10 17:00:00"); //2x30mns
        HashMap<Room, List<TimeSpan>> free_slots = this.optimisedSearch.search(this.token, 1, start1, end1, property);

        Assert.assertEquals("Wrong # of rooms for the property.", 3, free_slots.size());
        Room room2 = new Room(2, 1, 1, 1, "Small room 2");
        Room room5 = new Room(5, 2, 1, 4, "Medium room 3");
        Room room6 = new Room(6, 2, 1, 4, "Large room 1");
        Assert.assertTrue("Room2 missing", free_slots.containsKey(room5));
        Assert.assertTrue("Room5 missing", free_slots.containsKey(room5));
        Assert.assertTrue("Room6 missing", free_slots.containsKey(room6));

        //Room2
        Assert.assertEquals(2, free_slots.get(room2).size()); //2
        //Room5
        Assert.assertEquals(2, free_slots.get(room5).size()); //2
        //Room6
        Assert.assertEquals(2, free_slots.get(room6).size()); //2
    }

    /**
     * Searches for available rooms on a floor within a time frame
     */
    @Test
    public void search6() throws Exception {
        RoomProperty property = new RoomProperty(
                RoomProperty.Trilean.UNDEFINED,
                RoomProperty.Trilean.UNDEFINED,
                RoomProperty.Trilean.UNDEFINED,
                RoomProperty.Trilean.UNDEFINED,
                -1,
                -1
        );
        Date start1 = TimestampConverter.getDateObject("2018-03-10 09:00:00");
        Date end1 = TimestampConverter.getDateObject("2018-03-10 11:00:00"); //4x30mns
        HashMap<Room, List<TimeSpan>> free_slots = this.optimisedSearch.search(this.token, 1, 1, start1, end1, property);

        Assert.assertEquals("Wrong # of rooms for the property.", 3, free_slots.size());
        Room room1 = new Room(1, 1, 1, 1, "Small room 1");
        Room room2 = new Room(2, 1, 1, 1, "Small room 2");
        Room room3 = new Room(3, 1, 1, 2, "Medium room 1");
        Assert.assertTrue("Room1 missing", free_slots.containsKey(room1));
        Assert.assertTrue("Room2 missing", free_slots.containsKey(room2));
        Assert.assertTrue("Room3 missing", free_slots.containsKey(room3));

        //Room1
        Assert.assertEquals(2, free_slots.get(room1).size()); //4-(2)
        //Room2
        Assert.assertEquals(4, free_slots.get(room2).size()); //4-(0)
        //Room3
        Assert.assertEquals(2, free_slots.get(room3).size()); //4-(0)
    }

    /**
     * Searches for available rooms anywhere
     */
    @Test
    public void search7() throws Exception {
        RoomProperty property = new RoomProperty(
                RoomProperty.Trilean.FALSE,
                RoomProperty.Trilean.UNDEFINED,
                RoomProperty.Trilean.UNDEFINED,
                RoomProperty.Trilean.TRUE,
                30,
                -1
        );
        Date start1 = TimestampConverter.getDateObject("2018-03-10 00:00:00");
        Date end1 = TimestampConverter.getDateObject("2018-03-11 00:00:00");
        HashMap<Room, List<TimeSpan>> free_slots = this.optimisedSearch.search(this.token, start1, end1, property);

        Assert.assertEquals("Wrong # of rooms for the property.", 4, free_slots.size());
        Room room4 = new Room(4, 2, 1, 3, "Medium room 2");
        Room room5 = new Room(5, 2, 1, 4, "Medium room 3");
        Room room6 = new Room(6, 2, 1, 4, "Large room 1");
        Room room7 = new Room(7, 3, 1, 5, "Large room 2");
        Assert.assertTrue("Room4 missing", free_slots.containsKey(room4));
        Assert.assertTrue("Room5 missing", free_slots.containsKey(room5));
        Assert.assertTrue("Room6 missing", free_slots.containsKey(room6));
        Assert.assertTrue("Room7 missing", free_slots.containsKey(room7));

        //Room4
        Assert.assertEquals(36, free_slots.get(room4).size()); //48-(6+6)
        //Room5
        Assert.assertEquals(34, free_slots.get(room5).size()); //48-(2+2+3+3+4)
        //Room6
        Assert.assertEquals(48, free_slots.get(room6).size()); //48-(0)
        //Room7
        Assert.assertEquals(40, free_slots.get(room7).size()); //48-(8)

//        for (Map.Entry<Room, List<TimeSpan>> entry : free_slots.entrySet()) {
//            System.out.println("Room: " + entry.getKey());
//            for (TimeSpan span : entry.getValue()) {
//                System.out.println(span);
//            }
//        }
    }
}