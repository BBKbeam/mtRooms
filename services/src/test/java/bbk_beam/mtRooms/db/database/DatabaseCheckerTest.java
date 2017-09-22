package bbk_beam.mtRooms.db.database;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.*;

public class DatabaseCheckerTest {
    private Database db;
    private DatabaseChecker db_checker = new DatabaseChecker();

    @Before
    public void setUp() throws Exception {
        db = new Database( "test.db" );
        db.connect();
    }

    @After
    public void tearDown() throws Exception {
        db.disconnect();
        Files.deleteIfExists(Paths.get("test.db"));
    }

    @Test
    public void checkReservationDB() throws Exception {
        Assert.assertTrue( db_checker.checkReservationDB( this.db ) );
    }

    @Test
    public void checkUserAccDB() throws Exception {
        Assert.assertTrue( db_checker.checkUserAccDB( this.db ) );
    }

}