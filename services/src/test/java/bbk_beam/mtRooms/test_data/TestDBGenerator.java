package bbk_beam.mtRooms.test_data;

import bbk_beam.mtRooms.admin.authentication.Token;
import bbk_beam.mtRooms.db.DbSystemBootstrap;
import bbk_beam.mtRooms.db.IReservationDbAccess;
import bbk_beam.mtRooms.db.IUserAccDbAccess;
import bbk_beam.mtRooms.db.session.SessionType;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

public class TestDBGenerator {
    @Test
    public void createTestDB() throws Exception {
        DbSystemBootstrap DbBootstrapper = new DbSystemBootstrap();
        Files.deleteIfExists(Paths.get("mtRoomsTestData.db"));
        DbBootstrapper.init("mtRoomsTestData.db");
        //Open access
        IUserAccDbAccess userAccDbAccess = DbBootstrapper.getUserAccDbAccess();
        ;
        Token token = new Token("TestGenerator", new Date(), Date.from(Instant.now().plus(1, ChronoUnit.DAYS)));
        userAccDbAccess.openSession(token.getSessionId(), token.getExpiry(), SessionType.ADMIN, 1);
        //Create test data entries
        IReservationDbAccess reservationDbAccess = DbBootstrapper.getReservationDbAccess();
        reservationDbAccess.pushToDB(token.getSessionId(), TestDataDBQueries.building);
        reservationDbAccess.pushToDB(token.getSessionId(), TestDataDBQueries.floors);

        userAccDbAccess.closeSession(token.getSessionId());
    }
}
