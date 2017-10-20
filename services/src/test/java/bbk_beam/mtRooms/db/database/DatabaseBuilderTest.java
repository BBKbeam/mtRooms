package bbk_beam.mtRooms.db.database;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Paths;

public class DatabaseBuilderTest {
    private Database db;
    private DatabaseBuilder db_builder = new DatabaseBuilder();
    private DatabaseChecker db_checker = new DatabaseChecker();

    @Before
    public void setUp() throws Exception {
        Files.deleteIfExists(Paths.get("test.db"));
        db = new Database("test.db");
        db.connect();
    }

    @After
    public void tearDown() throws Exception {
        db.disconnect();
        Files.deleteIfExists(Paths.get("test.db"));
    }

    @Test
    public void buildReservationDB() throws Exception {
        Assert.assertTrue(db_builder.buildReservationDB(this.db));
        Assert.assertTrue(db_checker.checkReservationDB(this.db));
    }

    @Test
    public void buildUserAccDB() throws Exception {
        Assert.assertTrue(db_builder.buildUserAccDB(this.db));
        Assert.assertTrue(db_checker.checkUserAccDB(this.db));
    }

}