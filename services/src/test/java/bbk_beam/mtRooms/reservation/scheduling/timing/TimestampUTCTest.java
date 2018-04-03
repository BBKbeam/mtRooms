package bbk_beam.mtRooms.reservation.scheduling.timing;

import org.junit.Assert;
import org.junit.Test;

public class TimestampUTCTest {
    @Test
    public void compareTo() {
        TimestampUTC timestampUTC1 = new TimestampUTC("1999-01-05 00:53:50");
        TimestampUTC timestampUTC2 = new TimestampUTC("2010-03-05 12:02:03");
        TimestampUTC timestampUTC3 = new TimestampUTC("2020-12-23 14:53:13");
        Assert.assertEquals(0, timestampUTC2.compareTo(timestampUTC2));
        Assert.assertEquals(1, timestampUTC2.compareTo(timestampUTC1));
        Assert.assertEquals(-1, timestampUTC2.compareTo(timestampUTC3));
    }

    @Test
    public void compareTo_YYYY() {
        TimestampUTC timestampUTC1 = new TimestampUTC("1999-01-01 00:00:00");
        TimestampUTC timestampUTC2 = new TimestampUTC("2010-01-01 00:00:00");
        TimestampUTC timestampUTC3 = new TimestampUTC("2020-01-01 00:00:00");
        Assert.assertEquals(0, timestampUTC2.compareTo(timestampUTC2));
        Assert.assertEquals(1, timestampUTC2.compareTo(timestampUTC1));
        Assert.assertEquals(-1, timestampUTC2.compareTo(timestampUTC3));
    }

    @Test
    public void compareTo_MM() {
        TimestampUTC timestampUTC1 = new TimestampUTC("2000-01-01 00:00:00");
        TimestampUTC timestampUTC2 = new TimestampUTC("2000-10-01 00:00:00");
        TimestampUTC timestampUTC3 = new TimestampUTC("2000-12-01 00:00:00");
        Assert.assertEquals(0, timestampUTC2.compareTo(timestampUTC2));
        Assert.assertEquals(1, timestampUTC2.compareTo(timestampUTC1));
        Assert.assertEquals(-1, timestampUTC2.compareTo(timestampUTC3));
    }

    @Test
    public void compareTo_dd() {
        TimestampUTC timestampUTC1 = new TimestampUTC("2000-01-01 00:00:00");
        TimestampUTC timestampUTC2 = new TimestampUTC("2000-01-11 00:00:00");
        TimestampUTC timestampUTC3 = new TimestampUTC("2000-01-23 00:00:00");
        Assert.assertEquals(0, timestampUTC2.compareTo(timestampUTC2));
        Assert.assertEquals(1, timestampUTC2.compareTo(timestampUTC1));
        Assert.assertEquals(-1, timestampUTC2.compareTo(timestampUTC3));
    }

    @Test
    public void compareTo_hh() {
        TimestampUTC timestampUTC1 = new TimestampUTC("2000-01-01 00:00:00");
        TimestampUTC timestampUTC2 = new TimestampUTC("2000-01-01 12:00:00");
        TimestampUTC timestampUTC3 = new TimestampUTC("2000-01-01 23:00:00");
        Assert.assertEquals(0, timestampUTC2.compareTo(timestampUTC2));
        Assert.assertEquals(1, timestampUTC2.compareTo(timestampUTC1));
        Assert.assertEquals(-1, timestampUTC2.compareTo(timestampUTC3));
    }

    @Test
    public void compareTo_mm() {
        TimestampUTC timestampUTC1 = new TimestampUTC("2000-01-01 00:00:00");
        TimestampUTC timestampUTC2 = new TimestampUTC("2000-01-01 00:11:00");
        TimestampUTC timestampUTC3 = new TimestampUTC("2000-01-01 00:23:00");
        Assert.assertEquals(0, timestampUTC2.compareTo(timestampUTC2));
        Assert.assertEquals(1, timestampUTC2.compareTo(timestampUTC1));
        Assert.assertEquals(-1, timestampUTC2.compareTo(timestampUTC3));
    }

    @Test
    public void compareTo_ss() {
        TimestampUTC timestampUTC1 = new TimestampUTC("2000-01-01 00:00:00");
        TimestampUTC timestampUTC2 = new TimestampUTC("2000-01-01 00:00:09");
        TimestampUTC timestampUTC3 = new TimestampUTC("2000-01-01 00:00:59");
        Assert.assertEquals(0, timestampUTC2.compareTo(timestampUTC2));
        Assert.assertEquals(1, timestampUTC2.compareTo(timestampUTC1));
        Assert.assertEquals(-1, timestampUTC2.compareTo(timestampUTC3));
    }

    @Test
    public void equals() {
        TimestampUTC timestampUTC1 = new TimestampUTC("2000-01-01 00:00:00");
        TimestampUTC timestampUTC2 = new TimestampUTC("2000-01-01 00:00:00");
        TimestampUTC timestampUTC3 = new TimestampUTC("2000-01-01 00:00:01");
        Assert.assertTrue(timestampUTC1.equals(timestampUTC2));
        Assert.assertTrue(timestampUTC2.equals(timestampUTC1));
        Assert.assertFalse(timestampUTC1.equals(timestampUTC3));
    }

    @Test
    public void year() {
        TimestampUTC timestampUTC = new TimestampUTC("2018-12-05 23:17:51");
        Assert.assertEquals(new Integer(2018), timestampUTC.year());
    }

    @Test
    public void month() {
        TimestampUTC timestampUTC = new TimestampUTC("2018-12-05 23:17:51");
        Assert.assertEquals(new Integer(12), timestampUTC.month());
    }

    @Test
    public void day() {
        TimestampUTC timestampUTC = new TimestampUTC("2018-12-05 23:17:51");
        Assert.assertEquals(new Integer(5), timestampUTC.day());
    }

    @Test
    public void hours() {
        TimestampUTC timestampUTC = new TimestampUTC("2018-12-05 23:17:51");
        Assert.assertEquals(new Integer(23), timestampUTC.hours());
    }

    @Test
    public void minutes() {
        TimestampUTC timestampUTC = new TimestampUTC("2018-12-05 23:17:51");
        Assert.assertEquals(new Integer(17), timestampUTC.minutes());
    }

    @Test
    public void seconds() {
        TimestampUTC timestampUTC = new TimestampUTC("2018-12-05 23:17:51");
        Assert.assertEquals(new Integer(51), timestampUTC.seconds());
    }
}