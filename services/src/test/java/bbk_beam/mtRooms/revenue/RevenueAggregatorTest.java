package bbk_beam.mtRooms.revenue;

import bbk_beam.mtRooms.db.DbSystemBootstrap;
import bbk_beam.mtRooms.db.IReservationDbAccess;
import org.junit.After;
import org.junit.Before;

import java.nio.file.Files;
import java.nio.file.Paths;

import static org.mockito.Mockito.mock;

public class RevenueAggregatorTest {
    private DbSystemBootstrap db_bootstrap = new DbSystemBootstrap();
    private IReservationDbAccess reservation_db_access;
    private IReservationDbAccess _mock_reservation_db_access;
    private String query;

    @Before
    public void setUp() throws Exception {
        this.db_bootstrap.init("mtRoomsTestData.db");
        this.reservation_db_access = this.db_bootstrap.getReservationDbAccess();
        this._mock_reservation_db_access = mock(IReservationDbAccess.class);
    }

    @After
    public void tearDown() throws Exception {
        Files.deleteIfExists(Paths.get("mtRoomsTestData.db"));
        this.reservation_db_access = null;
        this._mock_reservation_db_access = null;
    }


    //   @Test(expected = InvalidPeriodException.class)
    // public void getRevenueReport() {

    //}


    // @Test
    // public void checkDate() {
    //}
}
