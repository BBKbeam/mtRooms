package bbk_beam.mtRooms.reservation.dto;

import org.junit.Assert;
import org.junit.Test;

public class RoomPropertyTest {

    @Test
    public void compareTo_1() { //All crap requirements/don't care
        RoomProperty a = new RoomProperty(
                false,
                false,
                false,
                false,
                -1,
                -1
        );
        RoomProperty b = new RoomProperty(
                false,
                false,
                false,
                false,
                -1,
                -1
        );
        Assert.assertEquals(0, a.compareTo(b));
    }

    @Test
    public void compareTo_2() { //All requirements met exactly
        RoomProperty a = new RoomProperty(
                false,
                false,
                false,
                false,
                3,
                5
        );
        RoomProperty b = new RoomProperty(
                false,
                false,
                false,
                false,
                3,
                5
        );
        Assert.assertEquals(0, a.compareTo(b));
    }

    @Test
    public void compareTo_3() { //All requirements are better than needed
        RoomProperty a = new RoomProperty(
                false,
                false,
                false,
                false,
                3,
                5
        );
        RoomProperty b = new RoomProperty(
                true,
                true,
                true,
                true,
                10,
                10
        );
        Assert.assertEquals(6, a.compareTo(b));
    }

    @Test
    public void compareTo_4() { //Don't care apart from 1 requirement (dimension)
        RoomProperty a = new RoomProperty(
                false,
                false,
                false,
                false,
                -1,
                5
        );
        RoomProperty b = new RoomProperty(
                false,
                false,
                false,
                false,
                10,
                10
        );
        Assert.assertEquals(1, a.compareTo(b));
    }

    @Test
    public void compareTo_5() { //Dimension requirement fails
        RoomProperty a = new RoomProperty(
                false,
                false,
                false,
                false,
                3,
                5
        );
        RoomProperty b = new RoomProperty(
                false,
                false,
                false,
                false,
                3,
                4
        );
        Assert.assertEquals(-1, a.compareTo(b));
    }

    @Test
    public void compareTo_6() { //Capacity requirement fails
        RoomProperty a = new RoomProperty(
                false,
                false,
                false,
                false,
                3,
                5
        );
        RoomProperty b = new RoomProperty(
                false,
                false,
                false,
                false,
                2,
                5
        );
        Assert.assertEquals(-1, a.compareTo(b));
    }

    @Test
    public void compareTo_7() { //Projector requirement fails
        RoomProperty a = new RoomProperty(
                true,
                true,
                true,
                true,
                3,
                5
        );
        RoomProperty b = new RoomProperty(
                true,
                true,
                true,
                false,
                3,
                5
        );
        Assert.assertEquals(-1, a.compareTo(b));
    }

    @Test
    public void compareTo_8() { //Whiteboard requirement fails
        RoomProperty a = new RoomProperty(
                true,
                true,
                true,
                true,
                3,
                5
        );
        RoomProperty b = new RoomProperty(
                true,
                true,
                false,
                true,
                3,
                5
        );
        Assert.assertEquals(-1, a.compareTo(b));
    }

    @Test
    public void compareTo_9() { //Catering space requirement fails
        RoomProperty a = new RoomProperty(
                true,
                true,
                true,
                true,
                3,
                5
        );
        RoomProperty b = new RoomProperty(
                true,
                false,
                true,
                true,
                3,
                5
        );
        Assert.assertEquals(-1, a.compareTo(b));
    }

    @Test
    public void compareTo_10() { //Fixed chairs requirement fails
        RoomProperty a = new RoomProperty(
                true,
                true,
                true,
                true,
                3,
                5
        );
        RoomProperty b = new RoomProperty(
                false,
                true,
                true,
                true,
                3,
                5
        );
        Assert.assertEquals(-1, a.compareTo(b));
    }
}