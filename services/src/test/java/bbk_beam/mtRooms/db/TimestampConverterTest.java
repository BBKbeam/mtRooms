package bbk_beam.mtRooms.db;

import org.junit.Assert;
import org.junit.Test;

import java.time.Instant;
import java.util.Date;

public class TimestampConverterTest {
    @Test
    public void getUTCTimestampString() throws Exception {
        Assert.assertEquals("1970-01-01 00:00:00", TimestampConverter.getUTCTimestampString(Date.from(Instant.EPOCH)));
    }

    @Test
    public void getDateObject() throws Exception {
        Date expected = Date.from(Instant.parse("2000-01-30T23:59:40Z"));
        Date returned = TimestampConverter.getDateObject("2000-01-30 23:59:40");
        Assert.assertEquals(expected, returned);
    }
}