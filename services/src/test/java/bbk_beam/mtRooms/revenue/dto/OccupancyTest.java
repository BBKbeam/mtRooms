package bbk_beam.mtRooms.revenue.dto;

import bbk_beam.mtRooms.common.TimeSpan;
import bbk_beam.mtRooms.common.TimestampUTC;
import bbk_beam.mtRooms.reservation.dto.Room;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class OccupancyTest {
    Occupancy occupancy;

    @Before
    public void setUp() throws Exception {
        this.occupancy = new Occupancy();
    }

    @After
    public void tearDown() throws Exception {
        this.occupancy = null;
    }

    @Test
    public void add() { //TODO
        Room room1 = new Room(1, 1, 1, 1, "");
        Room room2 = new Room(2, 1, 1, 1, "");
        TimeSpan span1 = new TimeSpan(
                new TimestampUTC("2000-01-01 10:00:00"),
                new TimestampUTC("2000-01-01 12:00:00")
        );
        TimeSpan span2 = new TimeSpan(
                new TimestampUTC("2000-01-01 11:00:00"),
                new TimestampUTC("2000-01-01 13:00:00")
        );


        this.occupancy.add(room2, span2);
        this.occupancy.add(room1, span1);
        this.occupancy.add(room1, span1);

        for (RoomOccupancy occupancy : this.occupancy.getOccupancies()) {
            System.out.println(occupancy.getRoom());
            for (ReservationSlot slot : occupancy.getOccupancy())
                System.out.println("\t" + slot);
        }
        Assert.fail();
    }

    @Test
    public void getOccupancies() { //TODO
        Assert.fail();
    }
}