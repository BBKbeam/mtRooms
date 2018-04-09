package bbk_beam.mtRooms.uaa;

import bbk_beam.mtRooms.admin.authentication.Token;
import bbk_beam.mtRooms.reservation.ReservationSession;
import bbk_beam.mtRooms.reservation.dto.Customer;
import bbk_beam.mtRooms.test_data.TestDBGenerator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Paths;

public class SessionDriverTest {
    private SessionDriver session_driver;
    private String test_db_file = "SessionDriver_test.db";

    @Before
    public void setUp() throws Exception {
        Files.deleteIfExists(Paths.get(test_db_file));
        TestDBGenerator dbGenerator = new TestDBGenerator();
        dbGenerator.createTestDB(test_db_file);
        this.session_driver = new SessionDriver();
    }

    @After
    public void tearDown() throws Exception {
        this.session_driver = null;
        Files.deleteIfExists(Paths.get(test_db_file));
    }

    @Test
    public void reservation1() throws Exception {
        //create dummy data file for test
        this.session_driver.init(this.test_db_file);
        //login admin
        Token token = this.session_driver.login("root", "letmein");
        IAuthenticatedAdministration administration = this.session_driver.getAdminInstance(token);


        IAuthenticatedFrontDesk front_desk = this.session_driver.getFrontDeskInstance(token);
        ReservationSession session = front_desk.openReservationSession(token);
        Customer customer = session.getCustomerAccount(token, 1);
        System.out.println(customer);
        front_desk.closeReservationSession(session);
        this.session_driver.logout(token);
        this.session_driver.reset();
        this.session_driver.init(this.test_db_file);
    }

    @Test
    public void init() {
        //TODO
    }

    @Test
    public void reset() {
        //TODO
    }

    @Test
    public void getFrontDeskInstance() throws Exception {
        this.session_driver.init(this.test_db_file);
        Token token = this.session_driver.login("root", "letmein");
        IAuthenticatedFrontDesk front_desk = this.session_driver.getFrontDeskInstance(token);
        ReservationSession session = front_desk.openReservationSession(token);
        Customer customer = session.getCustomerAccount(token, 1);
        System.out.println(customer);
        front_desk.closeReservationSession(session);
        this.session_driver.logout(token);
        this.session_driver.reset();
        this.session_driver.init(this.test_db_file);
    }

    @Test
    public void getAdminInstance() throws Exception {
        //TODO
    }

    @Test
    public void getRevenuePersonnelInstance() throws Exception {
        //TODO
    }

    @Test
    public void getLogisticsPersonnelInstance() throws Exception {
        //TODO
    }

    @Test
    public void login() throws Exception {
        //TODO
    }

    @Test
    public void logout() throws Exception {
        //TODO
    }
}