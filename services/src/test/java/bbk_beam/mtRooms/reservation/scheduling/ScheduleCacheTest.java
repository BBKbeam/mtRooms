package bbk_beam.mtRooms.reservation.scheduling;

import bbk_beam.mtRooms.admin.authentication.Token;
import bbk_beam.mtRooms.db.TimestampConverter;
import bbk_beam.mtRooms.reservation.ReservationSession;
import bbk_beam.mtRooms.reservation.dto.Room;
import bbk_beam.mtRooms.reservation.dto.RoomPrice;
import bbk_beam.mtRooms.reservation.dto.RoomReservation;
import bbk_beam.mtRooms.reservation.scheduling.datastructure.TimeSpan;
import bbk_beam.mtRooms.reservation.scheduling.timing.TimestampUTC;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

import static org.mockito.Mockito.*;

public class ScheduleCacheTest {
    private ScheduleCache cache;
    private Token token1 = new Token("00001", new Date(), Date.from(Instant.now().plus(1, ChronoUnit.DAYS)));
    private Token token2 = new Token("00002", new Date(), Date.from(Instant.now().plus(1, ChronoUnit.DAYS)));
    private Token token3 = new Token("00003", new Date(), Date.from(Instant.now().plus(1, ChronoUnit.DAYS)));
    private ReservationSession mock_observer1;
    private ReservationSession mock_observer2;
    private ReservationSession mock_observer3;

    @Before
    public void setUp() throws Exception {
        this.cache = new ScheduleCache();
        this.mock_observer1 = mock(ReservationSession.class);
        when(mock_observer1.getToken()).thenReturn(this.token1);
        doNothing().when(mock_observer1).update(eq(this.cache), any());
        this.mock_observer2 = mock(ReservationSession.class);
        when(mock_observer2.getToken()).thenReturn(this.token2);
        doNothing().when(mock_observer2).update(eq(this.cache), any());
        this.mock_observer3 = mock(ReservationSession.class);
        when(mock_observer3.getToken()).thenReturn(this.token3);
        doNothing().when(mock_observer3).update(eq(this.cache), any());
    }

    @After
    public void tearDown() throws Exception {
        this.cache = null;
        this.mock_observer1 = null;
        this.mock_observer2 = null;
        this.mock_observer3 = null;
    }

    @Test
    public void add() throws Exception {
        Room room = new Room(1, 1, 1, 1);
        Date from = TimestampConverter.getDateObject("2018-01-01 12:00:00");
        Date to = TimestampConverter.getDateObject("2018-01-02 15:00:00");
        List<TimeSpan> list = this.cache.add(token1, room, from, to);
        Assert.assertEquals((48 + 6), list.size());
        TimestampUTC start = new TimestampUTC("2018-01-01 12:00:00");
        for (TimeSpan span : list) {
            Assert.assertEquals(start, span.start());
            start = span.end();
        }
    }

    @Test
    public void broadcastRoomReservation() throws Exception {
        Room room1 = new Room(1, 1, 1, 1);
        Date start1 = TimestampConverter.getDateObject("2018-01-01 00:00:00");
        Date end1 = TimestampConverter.getDateObject("2018-01-01 01:00:00");
        Date start2 = TimestampConverter.getDateObject("2018-01-01 02:00:00");
        Date end2 = TimestampConverter.getDateObject("2018-01-01 03:00:00");

        this.cache.addObserver(mock_observer1);
        this.cache.addObserver(mock_observer2);
        this.cache.addObserver(mock_observer3);

        this.cache.add(token1, room1, start1, end1);
        this.cache.add(token2, room1, start1, end1);
        this.cache.add(token3, room1, start2, end2);

        RoomReservation reservation = new RoomReservation(
                room1,
                start1,
                end1,
                10,
                false,
                "",
                new RoomPrice(1, 100., 2018),
                false
        );
        this.cache.broadcastRoomReservation(token1, reservation);
        verify(mock_observer1, times(0)).update(eq(this.cache), eq(reservation));
        verify(mock_observer2, times(1)).update(eq(this.cache), eq(reservation));
        verify(mock_observer3, times(0)).update(eq(this.cache), eq(reservation));
    }

    @Test
    public void addObserver() throws Exception {
        Assert.assertEquals(0, this.cache.countObservers());
        this.cache.addObserver(mock_observer1);
        Assert.assertEquals(1, this.cache.countObservers());
    }

    @Test
    public void deleteObserver() throws Exception {
        this.cache.addObserver(mock_observer1);
        this.cache.addObserver(mock_observer2);
        this.cache.addObserver(mock_observer3);
        Assert.assertEquals(3, this.cache.countObservers());
        this.cache.deleteObserver(mock_observer1);
        Assert.assertEquals(2, this.cache.countObservers());
        this.cache.deleteObserver(mock_observer2);
        Assert.assertEquals(1, this.cache.countObservers());
        this.cache.deleteObserver(mock_observer3);
        Assert.assertEquals(0, this.cache.countObservers());
    }

    @Test
    public void countObservers() throws Exception {
        Assert.assertEquals(0, this.cache.countObservers());
        this.cache.addObserver(mock_observer1);
        Assert.assertEquals(1, this.cache.countObservers());
        this.cache.addObserver(mock_observer2);
        Assert.assertEquals(2, this.cache.countObservers());
        this.cache.addObserver(mock_observer3);
        Assert.assertEquals(3, this.cache.countObservers());
    }

    @Test
    public void exists() throws Exception {
        Assert.assertFalse(this.cache.exists(mock_observer1));
        this.cache.addObserver(mock_observer1);
        Assert.assertTrue(this.cache.exists(mock_observer1));
    }
}