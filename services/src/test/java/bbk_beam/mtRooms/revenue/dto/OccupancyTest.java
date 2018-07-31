package bbk_beam.mtRooms.revenue.dto;

import bbk_beam.mtRooms.common.TimeSpan;
import bbk_beam.mtRooms.common.TimestampUTC;
import bbk_beam.mtRooms.reservation.dto.Room;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

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
        TimeSpan span3 = new TimeSpan(
                new TimestampUTC("2000-01-01 11:00:00"),
                new TimestampUTC("2000-01-01 11:30:00")
        );


        this.occupancy.add(room2, span2);
        this.occupancy.add(room1, span1);
        this.occupancy.add(room1, span2);
        this.occupancy.add(room1, span3);

        int check_count = 0;
        Assert.assertEquals("Incorrect number of rooms found.", 2, this.occupancy.getOccupancies().size());
        for (RoomOccupancy occupancy : this.occupancy.getOccupancies()) {
//            System.out.println(occupancy.getRoom());
//            for (ReservationSlot slot : occupancy.getOccupancy())
//                System.out.println("\t" + slot);

            if (occupancy.getRoom().id() == 1) {
                List<ReservationSlot> slots = occupancy.getOccupancy();
                Assert.assertEquals("Unexpected number of slots found for room [1]", 6, slots.size());
                Assert.assertEquals((Integer) 1, slots.get(0).reservationCount());
                Assert.assertEquals((Integer) 1, slots.get(1).reservationCount());
                Assert.assertEquals((Integer) 3, slots.get(2).reservationCount());
                Assert.assertEquals((Integer) 2, slots.get(3).reservationCount());
                Assert.assertEquals((Integer) 1, slots.get(4).reservationCount());
                Assert.assertEquals((Integer) 1, slots.get(5).reservationCount());
                check_count++;
            }
            if (occupancy.getRoom().id() == 2) {
                List<ReservationSlot> slots = occupancy.getOccupancy();
                Assert.assertEquals("Unexpected number of slots found for room [2]", 4, slots.size());
                Assert.assertEquals((Integer) 1, slots.get(0).reservationCount());
                Assert.assertEquals((Integer) 1, slots.get(1).reservationCount());
                Assert.assertEquals((Integer) 1, slots.get(2).reservationCount());
                Assert.assertEquals((Integer) 1, slots.get(3).reservationCount());
                check_count++;
            }
        }
        Assert.assertEquals("Not all expected rooms were found.", 2, check_count);
    }
}