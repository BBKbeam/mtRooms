package bbk_beam.mtRooms.revenue;

import bbk_beam.mtRooms.db.DbSystemBootstrap;
import bbk_beam.mtRooms.db.IReservationDbAccess;
import bbk_beam.mtRooms.revenue.exception.InvalidPeriodException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.CallableStatement;

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

    @Test
    CallableStatement prepareCall(String query) {
        this.query =
                "SELECT Room_has_Reservation.reservation_id," +
                        "Room_has_Reservation.room_id," +
                        "Room_has_Reservation.floor_id," +
                        "Room_has_Reservation.building_id," +
                        "Reservation_has_Payment.payment_id," +
                        "Payment.amount," +
                        " strftime('%m', Payment.timestamp) AS month," +
                        " strftime('%Y', Payment.timestamp) AS year" +
                        "FROM Room_has_Reservation" +
                        "LEFT OUTER JOIN Reservation_has_Payment" +
                        "ON Room_has_Reservation.reservation_id = Reservation_has_Payment.reservation_id"
        "LEFT OUTER JOIN Payment  ON Reservation_has_Payment.payment_id = Payment.id" +
                "WHERE Room_has_Reservation.cancelled_flag = 0" +
                "AND  Payment.amount notnull";
    }


    @Test(expected = InvalidPeriodException.class)
    public void getRevenueReport() {

    }
}