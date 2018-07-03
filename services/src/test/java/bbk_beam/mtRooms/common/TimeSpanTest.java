package bbk_beam.mtRooms.common;

import org.junit.Assert;
import org.junit.Test;

public class TimeSpanTest {
    private String start1 = "2018-01-01 12:00:00";
    private String end1 = "2018-01-01 13:00:00";
    private String start2 = "2018-01-01 13:00:00";
    private String end2 = "2018-01-01 14:00:00";
    private String start3 = "2018-01-01 12:30:00";
    private String end3 = "2018-01-01 13:30:00";

    @Test
    public void compareTo_1() { //Before
        TimeSpan span1 = new TimeSpan(start1, end1);
        TimeSpan span2 = new TimeSpan(start2, end2);
        Assert.assertEquals(-1, span1.compareTo(span2));
    }

    @Test
    public void compareTo_2() { //After
        TimeSpan span1 = new TimeSpan(start1, end1);
        TimeSpan span2 = new TimeSpan(start2, end2);
        Assert.assertEquals(1, span2.compareTo(span1));
    }

    @Test
    public void compareTo_3() { //Overlap
        TimeSpan span1 = new TimeSpan(start1, end1);
        TimeSpan span2 = new TimeSpan(start1, end1);
        TimeSpan span3 = new TimeSpan(start3, end3);
        Assert.assertEquals(0, span1.compareTo(span2));
        Assert.assertEquals(0, span2.compareTo(span1));
        Assert.assertEquals(0, span1.compareTo(span3));
        Assert.assertEquals(0, span3.compareTo(span1));
    }
}