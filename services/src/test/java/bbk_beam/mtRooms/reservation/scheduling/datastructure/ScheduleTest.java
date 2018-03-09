package bbk_beam.mtRooms.reservation.scheduling.datastructure;

import bbk_beam.mtRooms.admin.authentication.Token;
import bbk_beam.mtRooms.db.TimestampConverter;
import bbk_beam.mtRooms.reservation.dto.Room;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Date;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;


public class ScheduleTest {
    private Schedule schedule;
    private Room room1 = new Room(1, 1, 1, 1);
    private Room room2 = new Room(2, 1, 1, 1);
    private Token token1 = new Token("00001", new Date(), Date.from(Instant.now().plus(1, ChronoUnit.DAYS)));
    private Token token2 = new Token("00002", new Date(), Date.from(Instant.now().plus(1, ChronoUnit.DAYS)));
    private Token token3 = new Token("00003", new Date(), Date.from(Instant.now().plus(1, ChronoUnit.DAYS)));

    @Before
    public void setUp() throws Exception {
        schedule = new Schedule();
    }

    @After
    public void tearDown() throws Exception {
        schedule = null;
    }

    @Test
    public void addSlot() throws Exception {
        Date date1from = new Date();
        Date date2from = Date.from(Instant.now().minus(30, ChronoUnit.MINUTES));
        Date date1to = Date.from(Instant.now().plus(43, ChronoUnit.MINUTES));
        Date date2to = Date.from(Instant.now().plus(90, ChronoUnit.MINUTES));
        this.schedule.addSlot(token1, room1, date1from, date1to);
        this.schedule.addSlot(token2, room1, date2from, date2to);
        Collection<Token> watchers = this.schedule.getWatchers(room1, date1from, date2to);
        Assert.assertEquals(2, watchers.size());
    }

    @Test
    public void getSlotOverlapCount() {
        Date start1 = TimestampConverter.getDateObject("2018-01-01 00:00:00");
        Date end1 = TimestampConverter.getDateObject("2018-01-01 01:00:00");
        Date start2 = TimestampConverter.getDateObject("2018-01-01 00:30:00");
        Date end2 = TimestampConverter.getDateObject("2018-01-01 01:30:00");
        Date start3 = TimestampConverter.getDateObject("2018-01-01 00:10:00");
        Date end3 = TimestampConverter.getDateObject("2018-01-01 00:40:00");
        Date start4 = TimestampConverter.getDateObject("2018-01-01 01:00:00");
        Date end4 = TimestampConverter.getDateObject("2018-01-01 01:45:00");
        //Room1
        this.schedule.addSlot(token1, room1, start1, end1); //00:00:00 - 01:00:00
        this.schedule.addSlot(token2, room1, start2, end2); //00:30:00 - 01:30:00
        this.schedule.addSlot(token3, room1, start3, end3); //00:10:00 - 00:40:00
        //Room2
        this.schedule.addSlot(token2, room2, start2, end2); //00:30:00 - 01:30:00
        this.schedule.addSlot(token1, room2, start4, end4); //01:00:00 - 01:45:00
        //Tests
        Assert.assertEquals(
                "Room1, 00:00:00 - 01:00:00 overlap count fail.",
                new Integer(3),
                this.schedule.getSlotOverlapCount(room1, start1, end1)
        );
        Assert.assertEquals(
                "Room2, 00:30:00 - 01:30:00 overlap count fail.",
                new Integer(2),
                this.schedule.getSlotOverlapCount(room2, start2, end2)
        );
        Assert.assertEquals(
                "Room2, 01:00:00-01:45:00 overlap count fail.",
                new Integer(2),
                this.schedule.getSlotOverlapCount(room2, start4, end4)
        );
        //Individual slots - Room1
        Assert.assertEquals(
                "Room1, 00:00:00-00:30:00 single slot count fail.",
                new Integer(2),
                this.schedule.getSlotOverlapCount(
                        room1,
                        TimestampConverter.getDateObject("2018-01-01 00:00:00"),
                        TimestampConverter.getDateObject("2018-01-01 00:30:00")
                )
        );
        Assert.assertEquals(
                "Room1, 00:30:00-01:00:00 single slot count fail.",
                new Integer(3),
                this.schedule.getSlotOverlapCount(
                        room1,
                        TimestampConverter.getDateObject("2018-01-01 00:30:00"),
                        TimestampConverter.getDateObject("2018-01-01 01:00:00")
                )
        );
        Assert.assertEquals(
                "Room1, 01:00:00-01:30:00 single slot count fail.",
                new Integer(1),
                this.schedule.getSlotOverlapCount(
                        room1,
                        TimestampConverter.getDateObject("2018-01-01 01:00:00"),
                        TimestampConverter.getDateObject("2018-01-01 01:30:00")
                )
        );
        Assert.assertEquals(
                "Room1, 01:30:00-02:00:00 single slot count fail.",
                new Integer(0),
                this.schedule.getSlotOverlapCount(
                        room1,
                        TimestampConverter.getDateObject("2018-01-01 01:30:00"),
                        TimestampConverter.getDateObject("2018-01-01 02:00:00")
                )
        );
        Assert.assertEquals(
                "Room1, 02:00:00-02:30:00 single slot count fail.",
                new Integer(0),
                this.schedule.getSlotOverlapCount(
                        room1,
                        TimestampConverter.getDateObject("2018-01-01 02:00:00"),
                        TimestampConverter.getDateObject("2018-01-01 02:30:00")
                )
        );
        //Individual slots - Room2
        Assert.assertEquals(
                "Room2, 00:00:00-00:30:00 single slot count fail.",
                new Integer(0),
                this.schedule.getSlotOverlapCount(
                        room2,
                        TimestampConverter.getDateObject("2018-01-01 00:00:00"),
                        TimestampConverter.getDateObject("2018-01-01 00:30:00")
                )
        );
        Assert.assertEquals(
                "Room2, 00:30:00-01:00:00 single slot count fail.",
                new Integer(1),
                this.schedule.getSlotOverlapCount(
                        room2,
                        TimestampConverter.getDateObject("2018-01-01 00:30:00"),
                        TimestampConverter.getDateObject("2018-01-01 01:00:00")
                )
        );
        Assert.assertEquals(
                "Room2, 01:00:00-01:30:00 single slot count fail.",
                new Integer(2),
                this.schedule.getSlotOverlapCount(
                        room2,
                        TimestampConverter.getDateObject("2018-01-01 01:00:00"),
                        TimestampConverter.getDateObject("2018-01-01 01:30:00")
                )
        );
        Assert.assertEquals(
                "Room2, 01:30:00-02:00:00 single slot count fail.",
                new Integer(1),
                this.schedule.getSlotOverlapCount(
                        room2,
                        TimestampConverter.getDateObject("2018-01-01 01:30:00"),
                        TimestampConverter.getDateObject("2018-01-01 02:00:00")
                )
        );
        Assert.assertEquals(
                "Room2, 02:00:00-02:30:00 single slot count fail.",
                new Integer(0),
                this.schedule.getSlotOverlapCount(
                        room2,
                        TimestampConverter.getDateObject("2018-01-01 02:00:00"),
                        TimestampConverter.getDateObject("2018-01-01 02:30:00")
                )
        );
    }

    @Test
    public void getWatchers() {
        Date start1 = TimestampConverter.getDateObject("2018-01-01 00:00:00");
        Date end1 = TimestampConverter.getDateObject("2018-01-01 01:00:00");
        //Sanity check
        Assert.assertEquals(0, this.schedule.cachedSlotsCount(room1));
        Assert.assertEquals(0, this.schedule.cachedSlotsCount(room2));
        //Room1
        this.schedule.addSlot(token1, room1, start1, end1); //00:00:00 - 01:00:00
        Assert.assertEquals(2, this.schedule.cachedSlotsCount(room1));
        this.schedule.addSlot(token2, room1, start1, end1); //00:00:00 - 01:00:00
        Assert.assertEquals(2, this.schedule.cachedSlotsCount(room1));
        this.schedule.addSlot(token3, room1, start1, end1); //00:00:00 - 01:00:00
        Assert.assertEquals(2, this.schedule.cachedSlotsCount(room1));
        //Room2
        Assert.assertEquals(0, this.schedule.cachedSlotsCount(room2));
        this.schedule.addSlot(token1, room2, start1, end1); //00:00:00 - 01:00:00
        Assert.assertEquals(2, this.schedule.cachedSlotsCount(room2));
        this.schedule.addSlot(token2, room2, start1, end1); //00:00:00 - 01:00:00
        Assert.assertEquals(2, this.schedule.cachedSlotsCount(room2));
        //Testing
        assertThat(this.schedule.getWatchers(room1, start1, end1), containsInAnyOrder(token1, token2, token3));
        assertThat(this.schedule.getWatchers(room2, start1, end1), containsInAnyOrder(token1, token2));
    }

    @Test
    public void clearWatcherCache() {
        Date start1 = TimestampConverter.getDateObject("2018-01-01 00:00:00");
        Date end1 = TimestampConverter.getDateObject("2018-01-01 01:00:00");
        //Sanity check
        Assert.assertEquals(0, this.schedule.cachedSlotsCount(room1));
        Assert.assertEquals(0, this.schedule.cachedSlotsCount(room2));
        //Room1
        this.schedule.addSlot(token1, room1, start1, end1); //00:00:00 - 01:00:00
        Assert.assertEquals(2, this.schedule.cachedSlotsCount(room1));
        this.schedule.addSlot(token2, room1, start1, end1); //00:00:00 - 01:00:00
        Assert.assertEquals(2, this.schedule.cachedSlotsCount(room1));
        this.schedule.addSlot(token3, room1, start1, end1); //00:00:00 - 01:00:00
        Assert.assertEquals(2, this.schedule.cachedSlotsCount(room1));
        //Room2
        Assert.assertEquals(0, this.schedule.cachedSlotsCount(room2));
        this.schedule.addSlot(token1, room2, start1, end1); //00:00:00 - 01:00:00
        Assert.assertEquals(2, this.schedule.cachedSlotsCount(room2));
        this.schedule.addSlot(token2, room2, start1, end1); //00:00:00 - 01:00:00
        Assert.assertEquals(2, this.schedule.cachedSlotsCount(room2));
        //Deletion
        Assert.assertFalse(this.schedule.cacheIsEmpty());
        this.schedule.clearWatcherCache(token1);
        Assert.assertEquals(2, this.schedule.cachedSlotsCount(room1));
        Assert.assertEquals(2, this.schedule.cachedSlotsCount(room2));
        assertThat(this.schedule.getWatchers(room1, start1, end1), containsInAnyOrder(token2, token3));
        assertThat(this.schedule.getWatchers(room2, start1, end1), containsInAnyOrder(token2));
        this.schedule.clearWatcherCache(token2);
        Assert.assertEquals(2, this.schedule.cachedSlotsCount(room1));
        Assert.assertEquals(0, this.schedule.cachedSlotsCount(room2));
        assertThat(this.schedule.getWatchers(room1, start1, end1), containsInAnyOrder(token3));
        Assert.assertTrue(this.schedule.getWatchers(room2, start1, end1).isEmpty());
        this.schedule.clearWatcherCache(token3);
        Assert.assertTrue(this.schedule.cacheIsEmpty());
    }

    @Test
    public void clearCache() {
        Date start1 = TimestampConverter.getDateObject("2018-01-01 00:00:00");
        Date end1 = TimestampConverter.getDateObject("2018-01-01 01:00:00");
        //Sanity check
        Assert.assertEquals(0, this.schedule.cachedSlotsCount(room1));
        Assert.assertEquals(0, this.schedule.cachedSlotsCount(room2));
        //Room1
        this.schedule.addSlot(token1, room1, start1, end1); //00:00:00 - 01:00:00
        Assert.assertEquals(2, this.schedule.cachedSlotsCount(room1));
        this.schedule.addSlot(token2, room1, start1, end1); //00:00:00 - 01:00:00
        Assert.assertEquals(2, this.schedule.cachedSlotsCount(room1));
        this.schedule.addSlot(token3, room1, start1, end1); //00:00:00 - 01:00:00
        Assert.assertEquals(2, this.schedule.cachedSlotsCount(room1));
        //Room2
        Assert.assertEquals(0, this.schedule.cachedSlotsCount(room2));
        this.schedule.addSlot(token1, room2, start1, end1); //00:00:00 - 01:00:00
        Assert.assertEquals(2, this.schedule.cachedSlotsCount(room2));
        this.schedule.addSlot(token2, room2, start1, end1); //00:00:00 - 01:00:00
        Assert.assertEquals(2, this.schedule.cachedSlotsCount(room2));
        //Testing
        Assert.assertFalse(this.schedule.cacheIsEmpty());
        this.schedule.clearCache();
        Assert.assertTrue(this.schedule.cacheIsEmpty());
    }
}