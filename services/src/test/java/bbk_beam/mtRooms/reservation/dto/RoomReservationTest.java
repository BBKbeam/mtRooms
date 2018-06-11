package bbk_beam.mtRooms.reservation.dto;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

public class RoomReservationTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void serialization() throws Exception {
        RoomReservation before = new RoomReservation(
                new Room(8, 3, 1, 6, "Theatre"),
                new Date(),
                Date.from(Instant.now().plus(2, ChronoUnit.HOURS)),
                10,
                true,
                "Note 1",
                new RoomPrice(12, 110., 2008),
                false
        );
        //Out
        Files.deleteIfExists(Paths.get("RoomReservation_Serializable_Test.ser"));
        FileOutputStream fos = new FileOutputStream("RoomReservation_Serializable_Test.ser");
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(before);
        oos.flush();
        oos.close();
        //In
        FileInputStream fis = new FileInputStream("RoomReservation_Serializable_Test.ser");
        ObjectInputStream ois = new ObjectInputStream(fis);
        RoomReservation after = (RoomReservation) ois.readObject();
        ois.close();
        //Test
        Assert.assertEquals(before, after);
        Files.deleteIfExists(Paths.get("RoomReservation_Serializable_Test.ser"));
    }
}