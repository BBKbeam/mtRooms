package bbk_beam.mtRooms.db;

import org.junit.Test;

import static org.junit.Assert.*;

public class DatabaseTest {
    @Test
    public void connect() throws Exception {
        Database db = new Database("test.db");
        assertTrue(db.connect());
        assertTrue(db.disconnect());
    }

    @Test
    public void disconnect() throws Exception {
    }

    @Test
    public void queryDB() throws Exception {
    }

}