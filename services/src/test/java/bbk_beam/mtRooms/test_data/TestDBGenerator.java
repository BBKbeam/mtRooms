package bbk_beam.mtRooms.test_data;

import bbk_beam.mtRooms.admin.authentication.Token;
import bbk_beam.mtRooms.db.DbSystemBootstrap;
import bbk_beam.mtRooms.db.IReservationDbAccess;
import bbk_beam.mtRooms.db.IUserAccDbAccess;
import bbk_beam.mtRooms.db.session.SessionType;
import eadjlib.logger.Logger;
import org.junit.After;
import org.junit.Ignore;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

public class TestDBGenerator {
    private final Logger log = Logger.getLoggerInstance(TestDBGenerator.class.getName());

    @After
    public void tearDown() throws Exception {
//        Files.deleteIfExists(Paths.get("mtRoomsTestData.db"));
    }

    /**
     * Run this test to create a database with test data
     *
     * @throws Exception when something goes wrong...
     */
    @Ignore("Used to manually create a basic DB template.")
    @Test
    public void createTestDB() throws Exception {
        createTestDB("mtRoomsTestData.db");
    }

    /**
     * Test data generation method that can be called by unit tests creating a database with some basic data
     *
     * @param file_name Database file name
     * @throws Exception when something goes wrong...
     */
    public void createTestDB(String file_name) throws Exception {
        DbSystemBootstrap DbBootstrapper = new DbSystemBootstrap();
        Files.deleteIfExists(Paths.get(file_name));
        DbBootstrapper.init(file_name);
        //Open access
        IUserAccDbAccess userAccDbAccess = DbBootstrapper.getUserAccDbAccess();
        Token token = new Token("TestGenerator", new Date(), Date.from(Instant.now().plus(1, ChronoUnit.DAYS)));
        userAccDbAccess.openSession(token.getSessionId(), token.getExpiry(), SessionType.ADMIN, 1);
        //Create test data entries in all tables
        IReservationDbAccess reservationDbAccess = DbBootstrapper.getReservationDbAccess();
        log.log("Adding dummy test data in '", file_name, "'.");
        reservationDbAccess.pushToDB(token.getSessionId(), TestDataDBQueries.building);
        reservationDbAccess.pushToDB(token.getSessionId(), TestDataDBQueries.floors);
        reservationDbAccess.pushToDB(token.getSessionId(), TestDataDBQueries.roomCategories);
        reservationDbAccess.pushToDB(token.getSessionId(), TestDataDBQueries.rooms);
        reservationDbAccess.pushToDB(token.getSessionId(), TestDataDBQueries.roomFixtures);
        reservationDbAccess.pushToDB(token.getSessionId(), TestDataDBQueries.room_has_RoomFixtures);
        reservationDbAccess.pushToDB(token.getSessionId(), TestDataDBQueries.roomPrices);
        reservationDbAccess.pushToDB(token.getSessionId(), TestDataDBQueries.room_has_RoomPrice);
        reservationDbAccess.pushToDB(token.getSessionId(), TestDataDBQueries.discountCategories);
        reservationDbAccess.pushToDB(token.getSessionId(), TestDataDBQueries.membershipTypes);
        reservationDbAccess.pushToDB(token.getSessionId(), TestDataDBQueries.discounts);
        reservationDbAccess.pushToDB(token.getSessionId(), TestDataDBQueries.customers);
        reservationDbAccess.pushToDB(token.getSessionId(), TestDataDBQueries.reservations);
        reservationDbAccess.pushToDB(token.getSessionId(), TestDataDBQueries.room_has_reservation);
        reservationDbAccess.pushToDB(token.getSessionId(), TestDataDBQueries.payments);
        reservationDbAccess.pushToDB(token.getSessionId(), TestDataDBQueries.reservation_has_payments);
        //Close access session
        userAccDbAccess.closeSession(token.getSessionId());
    }
}
