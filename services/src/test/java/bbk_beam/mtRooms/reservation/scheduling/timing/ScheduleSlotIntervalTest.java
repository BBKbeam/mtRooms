package bbk_beam.mtRooms.reservation.scheduling.timing;

import bbk_beam.mtRooms.db.TimestampConverter;
import org.junit.Assert;
import org.junit.Test;

import java.util.Date;

public class ScheduleSlotIntervalTest {
    private ScheduleSlotInterval interval15 = ScheduleSlotInterval.QUARTER_HOUR;
    private ScheduleSlotInterval interval30 = ScheduleSlotInterval.HALF_HOUR;
    private ScheduleSlotInterval interval60 = ScheduleSlotInterval.HOUR;
    //Test dates
    private Date same = TimestampConverter.getDateObject("2018-03-07 14:00:00");
    private Date lower1 = TimestampConverter.getDateObject("2018-03-07 14:01:30");
    private Date midlower1 = TimestampConverter.getDateObject("2018-03-07 14:07:29");
    private Date midupper1 = TimestampConverter.getDateObject("2018-03-07 14:07:31");
    private Date upper1 = TimestampConverter.getDateObject("2018-03-07 14:14:30");
    private Date lower2 = TimestampConverter.getDateObject("2018-03-07 14:16:30");
    private Date upper2 = TimestampConverter.getDateObject("2018-03-07 14:29:30");
    private Date lower3 = TimestampConverter.getDateObject("2018-03-07 14:31:30");
    private Date upper3 = TimestampConverter.getDateObject("2018-03-07 14:44:30");
    private Date lower4 = TimestampConverter.getDateObject("2018-03-07 14:45:30");
    private Date upper4 = TimestampConverter.getDateObject("2018-03-07 14:59:30");

    @Test
    public void mns_15() {
        Assert.assertEquals(new Integer(15), interval15.mns());
    }

    @Test
    public void mns_30() {
        Assert.assertEquals(new Integer(30), interval30.mns());
    }

    @Test
    public void mns_60() {
        Assert.assertEquals(new Integer(60), interval60.mns());
    }

    @Test
    public void ceilToInterval_15() {
        Assert.assertEquals(
                "same failed",
                TimestampConverter.getDateObject("2018-03-07 14:00:00"),
                interval15.ceilToInterval(same)
        );
        Assert.assertEquals(
                "lower1 failed",
                TimestampConverter.getDateObject("2018-03-07 14:15:00"),
                interval15.ceilToInterval(lower1)
        );
        Assert.assertEquals(
                "midlower1 failed",
                TimestampConverter.getDateObject("2018-03-07 14:15:00"),
                interval15.ceilToInterval(midlower1)
        );
        Assert.assertEquals(
                "midupper1 failed",
                TimestampConverter.getDateObject("2018-03-07 14:15:00"),
                interval15.ceilToInterval(midupper1)
        );
        Assert.assertEquals(
                "upper1 failed",
                TimestampConverter.getDateObject("2018-03-07 14:15:00"),
                interval15.ceilToInterval(upper1)
        );
        Assert.assertEquals(
                "lower2 failed",
                TimestampConverter.getDateObject("2018-03-07 14:30:00"),
                interval15.ceilToInterval(lower2)
        );
        Assert.assertEquals(
                "upper2 failed",
                TimestampConverter.getDateObject("2018-03-07 14:30:00"),
                interval15.ceilToInterval(upper2)
        );
        Assert.assertEquals(
                "lower3 failed",
                TimestampConverter.getDateObject("2018-03-07 14:45:00"),
                interval15.ceilToInterval(lower3)
        );
        Assert.assertEquals(
                "upper3 failed",
                TimestampConverter.getDateObject("2018-03-07 14:45:00"),
                interval15.ceilToInterval(upper3)
        );
        Assert.assertEquals(
                "lower4 failed",
                TimestampConverter.getDateObject("2018-03-07 15:00:00"),
                interval15.ceilToInterval(lower4)
        );
        Assert.assertEquals(
                "upper4 failed",
                TimestampConverter.getDateObject("2018-03-07 15:00:00"),
                interval15.ceilToInterval(upper4)
        );
    }

    @Test
    public void ceilToInterval_30() {
        Assert.assertEquals(
                "same failed",
                TimestampConverter.getDateObject("2018-03-07 14:00:00"),
                interval30.ceilToInterval(same)
        );
        Assert.assertEquals(
                "lower1 failed",
                TimestampConverter.getDateObject("2018-03-07 14:30:00"),
                interval30.ceilToInterval(lower1)
        );
        Assert.assertEquals(
                "midupper1 failed",
                TimestampConverter.getDateObject("2018-03-07 14:30:00"),
                interval30.ceilToInterval(midupper1)
        );
        Assert.assertEquals(
                "upper2 failed",
                TimestampConverter.getDateObject("2018-03-07 14:30:00"),
                interval30.ceilToInterval(upper2)
        );
        Assert.assertEquals(
                "lower3 failed",
                TimestampConverter.getDateObject("2018-03-07 15:00:00"),
                interval30.ceilToInterval(lower3)
        );
        Assert.assertEquals(
                "upper4 failed",
                TimestampConverter.getDateObject("2018-03-07 15:00:00"),
                interval30.ceilToInterval(upper4)
        );
    }

    @Test
    public void ceilToInterval_60() {
        Assert.assertEquals(
                "same failed",
                TimestampConverter.getDateObject("2018-03-07 14:00:00"),
                interval60.ceilToInterval(same)
        );
        Assert.assertEquals(
                "lower1 failed",
                TimestampConverter.getDateObject("2018-03-07 15:00:00"),
                interval60.ceilToInterval(lower1)
        );
        Assert.assertEquals(
                "midupper1 failed",
                TimestampConverter.getDateObject("2018-03-07 15:00:00"),
                interval60.ceilToInterval(midupper1)
        );
        Assert.assertEquals(
                "upper2 failed",
                TimestampConverter.getDateObject("2018-03-07 15:00:00"),
                interval60.ceilToInterval(upper2)
        );
        Assert.assertEquals(
                "lower3 failed",
                TimestampConverter.getDateObject("2018-03-07 15:00:00"),
                interval60.ceilToInterval(lower3)
        );
        Assert.assertEquals(
                "upper4 failed",
                TimestampConverter.getDateObject("2018-03-07 15:00:00"),
                interval60.ceilToInterval(upper4)
        );
    }

    @Test
    public void floorToInterval_15() {
        Assert.assertEquals(
                "same failed",
                TimestampConverter.getDateObject("2018-03-07 14:00:00"),
                interval15.floorToInterval(same)
        );
        Assert.assertEquals(
                "lower1 failed",
                TimestampConverter.getDateObject("2018-03-07 14:00:00"),
                interval15.floorToInterval(lower1)
        );
        Assert.assertEquals(
                "midlower1 failed",
                TimestampConverter.getDateObject("2018-03-07 14:00:00"),
                interval15.floorToInterval(midlower1)
        );
        Assert.assertEquals(
                "midupper1 failed",
                TimestampConverter.getDateObject("2018-03-07 14:00:00"),
                interval15.floorToInterval(midupper1)
        );
        Assert.assertEquals(
                "upper1 failed",
                TimestampConverter.getDateObject("2018-03-07 14:00:00"),
                interval15.floorToInterval(upper1)
        );
        Assert.assertEquals(
                "lower2 failed",
                TimestampConverter.getDateObject("2018-03-07 14:15:00"),
                interval15.floorToInterval(lower2)
        );
        Assert.assertEquals(
                "upper2 failed",
                TimestampConverter.getDateObject("2018-03-07 14:15:00"),
                interval15.floorToInterval(upper2)
        );
        Assert.assertEquals(
                "lower3 failed",
                TimestampConverter.getDateObject("2018-03-07 14:30:00"),
                interval15.floorToInterval(lower3)
        );
        Assert.assertEquals(
                "upper3 failed",
                TimestampConverter.getDateObject("2018-03-07 14:30:00"),
                interval15.floorToInterval(upper3)
        );
        Assert.assertEquals(
                "lower4 failed",
                TimestampConverter.getDateObject("2018-03-07 14:45:00"),
                interval15.floorToInterval(lower4)
        );
        Assert.assertEquals(
                "upper4 failed",
                TimestampConverter.getDateObject("2018-03-07 14:45:00"),
                interval15.floorToInterval(upper4)
        );
    }

    @Test
    public void floorToInterval_30() {
        Assert.assertEquals(
                "same failed",
                TimestampConverter.getDateObject("2018-03-07 14:00:00"),
                interval30.floorToInterval(same)
        );
        Assert.assertEquals(
                "lower1 failed",
                TimestampConverter.getDateObject("2018-03-07 14:00:00"),
                interval30.floorToInterval(lower1)
        );
        Assert.assertEquals(
                "midupper1 failed",
                TimestampConverter.getDateObject("2018-03-07 14:00:00"),
                interval30.floorToInterval(midupper1)
        );
        Assert.assertEquals(
                "upper2 failed",
                TimestampConverter.getDateObject("2018-03-07 14:00:00"),
                interval30.floorToInterval(upper2)
        );
        Assert.assertEquals(
                "lower3 failed",
                TimestampConverter.getDateObject("2018-03-07 14:30:00"),
                interval30.floorToInterval(lower3)
        );
        Assert.assertEquals(
                "upper4 failed",
                TimestampConverter.getDateObject("2018-03-07 14:30:00"),
                interval30.floorToInterval(upper4)
        );
    }

    @Test
    public void floorToInterval_60() {
        Assert.assertEquals(
                "same failed",
                TimestampConverter.getDateObject("2018-03-07 14:00:00"),
                interval60.floorToInterval(same)
        );
        Assert.assertEquals(
                "lower1 failed",
                TimestampConverter.getDateObject("2018-03-07 14:00:00"),
                interval60.floorToInterval(lower1)
        );
        Assert.assertEquals(
                "midupper1 failed",
                TimestampConverter.getDateObject("2018-03-07 14:00:00"),
                interval60.floorToInterval(midupper1)
        );
        Assert.assertEquals(
                "upper2 failed",
                TimestampConverter.getDateObject("2018-03-07 14:00:00"),
                interval60.floorToInterval(upper2)
        );
        Assert.assertEquals(
                "lower3 failed",
                TimestampConverter.getDateObject("2018-03-07 14:00:00"),
                interval60.floorToInterval(lower3)
        );
        Assert.assertEquals(
                "upper4 failed",
                TimestampConverter.getDateObject("2018-03-07 14:00:00"),
                interval60.floorToInterval(upper4)
        );
    }

    @Test
    public void roundToNearestInterval_15() {
        Assert.assertEquals(
                "same failed",
                TimestampConverter.getDateObject("2018-03-07 14:00:00"),
                interval15.roundToNearestInterval(same)
        );
        Assert.assertEquals(
                "lower1 failed",
                TimestampConverter.getDateObject("2018-03-07 14:00:00"),
                interval15.roundToNearestInterval(lower1)
        );
        Assert.assertEquals(
                "midlower1 failed",
                TimestampConverter.getDateObject("2018-03-07 14:00:00"),
                interval15.roundToNearestInterval(midlower1)
        );
        Assert.assertEquals(
                "midupper1 failed",
                TimestampConverter.getDateObject("2018-03-07 14:15:00"),
                interval15.roundToNearestInterval(midupper1)
        );
        Assert.assertEquals(
                "upper1 failed",
                TimestampConverter.getDateObject("2018-03-07 14:15:00"),
                interval15.roundToNearestInterval(upper1)
        );
        Assert.assertEquals(
                "lower2 failed",
                TimestampConverter.getDateObject("2018-03-07 14:15:00"),
                interval15.roundToNearestInterval(lower2)
        );
        Assert.assertEquals(
                "upper2 failed",
                TimestampConverter.getDateObject("2018-03-07 14:30:00"),
                interval15.roundToNearestInterval(upper2)
        );
        Assert.assertEquals(
                "lower3 failed",
                TimestampConverter.getDateObject("2018-03-07 14:30:00"),
                interval15.roundToNearestInterval(lower3)
        );
        Assert.assertEquals(
                "upper3 failed",
                TimestampConverter.getDateObject("2018-03-07 14:45:00"),
                interval15.roundToNearestInterval(upper3)
        );
        Assert.assertEquals(
                "lower4 failed",
                TimestampConverter.getDateObject("2018-03-07 14:45:00"),
                interval15.roundToNearestInterval(lower4)
        );
        Assert.assertEquals(
                "upper4 failed",
                TimestampConverter.getDateObject("2018-03-07 15:00:00"),
                interval15.roundToNearestInterval(upper4)
        );
    }

    @Test
    public void roundToNearestInterval_30() {
        Assert.assertEquals(
                "same failed",
                TimestampConverter.getDateObject("2018-03-07 14:00:00"),
                interval30.roundToNearestInterval(same)
        );
        Assert.assertEquals(
                "lower1 failed",
                TimestampConverter.getDateObject("2018-03-07 14:00:00"),
                interval30.roundToNearestInterval(lower1)
        );
        Assert.assertEquals(
                "upper2 failed",
                TimestampConverter.getDateObject("2018-03-07 14:30:00"),
                interval30.roundToNearestInterval(upper2)
        );
        Assert.assertEquals(
                "lower3 failed",
                TimestampConverter.getDateObject("2018-03-07 14:30:00"),
                interval30.roundToNearestInterval(lower3)
        );
        Assert.assertEquals(
                "upper4 failed",
                TimestampConverter.getDateObject("2018-03-07 15:00:00"),
                interval30.roundToNearestInterval(upper4)
        );
    }

    @Test
    public void roundToNearestInterval_60() {
        Assert.assertEquals(
                "same failed",
                TimestampConverter.getDateObject("2018-03-07 14:00:00"),
                interval60.roundToNearestInterval(same)
        );
        Assert.assertEquals(
                "lower1 failed",
                TimestampConverter.getDateObject("2018-03-07 14:00:00"),
                interval60.roundToNearestInterval(lower1)
        );
        Assert.assertEquals(
                "upper2 failed",
                TimestampConverter.getDateObject("2018-03-07 14:00:00"),
                interval60.roundToNearestInterval(upper2)
        );
        Assert.assertEquals(
                "lower3 failed",
                TimestampConverter.getDateObject("2018-03-07 15:00:00"),
                interval60.roundToNearestInterval(lower3)
        );
        Assert.assertEquals(
                "upper4 failed",
                TimestampConverter.getDateObject("2018-03-07 15:00:00"),
                interval60.roundToNearestInterval(upper4)
        );
    }
}