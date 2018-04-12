package bbk_beam.mtRooms.reservation.dto;

import org.junit.Assert;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

public class RoomPropertyTest {

    @Test
    public void compareTo_1() { //All crap requirements/don't care
        RoomProperty a = new RoomProperty(
                RoomProperty.Trilean.UNDEFINED,
                RoomProperty.Trilean.UNDEFINED,
                RoomProperty.Trilean.UNDEFINED,
                RoomProperty.Trilean.UNDEFINED,
                -1,
                -1
        );
        RoomProperty b = new RoomProperty(
                RoomProperty.Trilean.FALSE,
                RoomProperty.Trilean.FALSE,
                RoomProperty.Trilean.FALSE,
                RoomProperty.Trilean.FALSE,
                -1,
                -1
        );
        Assert.assertEquals(0, a.compareTo(b));
    }

    @Test
    public void compareTo_2() { //All requirements met exactly
        RoomProperty a = new RoomProperty(
                RoomProperty.Trilean.FALSE,
                RoomProperty.Trilean.FALSE,
                RoomProperty.Trilean.FALSE,
                RoomProperty.Trilean.FALSE,
                3,
                5
        );
        RoomProperty b = new RoomProperty(
                RoomProperty.Trilean.FALSE,
                RoomProperty.Trilean.FALSE,
                RoomProperty.Trilean.FALSE,
                RoomProperty.Trilean.FALSE,
                3,
                5
        );
        Assert.assertEquals(0, a.compareTo(b));
    }

    @Test
    public void compareTo_3() { //All requirements are better than needed
        RoomProperty a = new RoomProperty(
                RoomProperty.Trilean.UNDEFINED,
                RoomProperty.Trilean.UNDEFINED,
                RoomProperty.Trilean.UNDEFINED,
                RoomProperty.Trilean.UNDEFINED,
                3,
                5
        );
        RoomProperty b = new RoomProperty(
                RoomProperty.Trilean.TRUE,
                RoomProperty.Trilean.TRUE,
                RoomProperty.Trilean.TRUE,
                RoomProperty.Trilean.TRUE,
                10,
                10
        );
        Assert.assertEquals(6, a.compareTo(b));
    }

    @Test
    public void compareTo_4() { //Don't care apart from 1 requirement (dimension)
        RoomProperty a = new RoomProperty(
                RoomProperty.Trilean.UNDEFINED,
                RoomProperty.Trilean.UNDEFINED,
                RoomProperty.Trilean.UNDEFINED,
                RoomProperty.Trilean.UNDEFINED,
                -1,
                5
        );
        RoomProperty b = new RoomProperty(
                RoomProperty.Trilean.FALSE,
                RoomProperty.Trilean.FALSE,
                RoomProperty.Trilean.FALSE,
                RoomProperty.Trilean.FALSE,
                10,
                10
        );
        Assert.assertEquals(1, a.compareTo(b));
    }

    @Test
    public void compareTo_5() { //Dimension requirement fails
        RoomProperty a = new RoomProperty(
                RoomProperty.Trilean.UNDEFINED,
                RoomProperty.Trilean.UNDEFINED,
                RoomProperty.Trilean.UNDEFINED,
                RoomProperty.Trilean.UNDEFINED,
                3,
                5
        );
        RoomProperty b = new RoomProperty(
                RoomProperty.Trilean.FALSE,
                RoomProperty.Trilean.FALSE,
                RoomProperty.Trilean.FALSE,
                RoomProperty.Trilean.FALSE,
                3,
                4
        );
        Assert.assertEquals(-1, a.compareTo(b));
    }

    @Test
    public void compareTo_6() { //Capacity requirement fails
        RoomProperty a = new RoomProperty(
                RoomProperty.Trilean.UNDEFINED,
                RoomProperty.Trilean.UNDEFINED,
                RoomProperty.Trilean.UNDEFINED,
                RoomProperty.Trilean.UNDEFINED,
                3,
                5
        );
        RoomProperty b = new RoomProperty(
                RoomProperty.Trilean.FALSE,
                RoomProperty.Trilean.FALSE,
                RoomProperty.Trilean.FALSE,
                RoomProperty.Trilean.FALSE,
                2,
                5
        );
        Assert.assertEquals(-1, a.compareTo(b));
    }

    @Test
    public void compareTo_7() { //Projector requirement fails
        RoomProperty a = new RoomProperty(
                RoomProperty.Trilean.TRUE,
                RoomProperty.Trilean.TRUE,
                RoomProperty.Trilean.TRUE,
                RoomProperty.Trilean.TRUE,
                3,
                5
        );
        RoomProperty b = new RoomProperty(
                RoomProperty.Trilean.TRUE,
                RoomProperty.Trilean.TRUE,
                RoomProperty.Trilean.TRUE,
                RoomProperty.Trilean.FALSE,
                3,
                5
        );
        Assert.assertEquals(-1, a.compareTo(b));
    }

    @Test
    public void compareTo_8() { //Whiteboard requirement fails
        RoomProperty a = new RoomProperty(
                RoomProperty.Trilean.TRUE,
                RoomProperty.Trilean.TRUE,
                RoomProperty.Trilean.TRUE,
                RoomProperty.Trilean.TRUE,
                3,
                5
        );
        RoomProperty b = new RoomProperty(
                RoomProperty.Trilean.TRUE,
                RoomProperty.Trilean.TRUE,
                RoomProperty.Trilean.FALSE,
                RoomProperty.Trilean.TRUE,
                3,
                5
        );
        Assert.assertEquals(-1, a.compareTo(b));
    }

    @Test
    public void compareTo_9() { //Catering space requirement fails
        RoomProperty a = new RoomProperty(
                RoomProperty.Trilean.TRUE,
                RoomProperty.Trilean.TRUE,
                RoomProperty.Trilean.TRUE,
                RoomProperty.Trilean.TRUE,
                3,
                5
        );
        RoomProperty b = new RoomProperty(
                RoomProperty.Trilean.TRUE,
                RoomProperty.Trilean.FALSE,
                RoomProperty.Trilean.TRUE,
                RoomProperty.Trilean.TRUE,
                3,
                5
        );
        Assert.assertEquals(-1, a.compareTo(b));
    }

    @Test
    public void compareTo_10() { //Fixed chairs requirement fails
        RoomProperty a = new RoomProperty(
                RoomProperty.Trilean.TRUE,
                RoomProperty.Trilean.TRUE,
                RoomProperty.Trilean.TRUE,
                RoomProperty.Trilean.TRUE,
                3,
                5
        );
        RoomProperty b = new RoomProperty(
                RoomProperty.Trilean.FALSE,
                RoomProperty.Trilean.TRUE,
                RoomProperty.Trilean.TRUE,
                RoomProperty.Trilean.TRUE,
                3,
                5
        );
        Assert.assertEquals(-1, a.compareTo(b));
    }

    @Test
    public void serialization() throws Exception {
        RoomProperty before = new RoomProperty(
                RoomProperty.Trilean.TRUE,
                RoomProperty.Trilean.FALSE,
                RoomProperty.Trilean.UNDEFINED,
                RoomProperty.Trilean.TRUE,
                10,
                50
        );
        //Out
        Files.deleteIfExists(Paths.get("RoomProperty_Serializable_Test.ser"));
        FileOutputStream fos = new FileOutputStream("RoomProperty_Serializable_Test.ser");
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(before);
        oos.flush();
        oos.close();
        //In
        FileInputStream fis = new FileInputStream("RoomProperty_Serializable_Test.ser");
        ObjectInputStream ois = new ObjectInputStream(fis);
        RoomProperty after = (RoomProperty) ois.readObject();
        ois.close();
        //Test
        Assert.assertEquals(before, after);
        Files.deleteIfExists(Paths.get("RoomProperty_Serializable_Test.ser"));
    }
}