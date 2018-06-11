package bbk_beam.mtRooms.uaa;

import bbk_beam.mtRooms.admin.authentication.Token;
import bbk_beam.mtRooms.admin.dto.Account;
import bbk_beam.mtRooms.admin.exception.AuthenticationFailureException;
import bbk_beam.mtRooms.db.TimestampConverter;
import bbk_beam.mtRooms.db.session.SessionType;
import bbk_beam.mtRooms.operation.dto.LogisticsEntry;
import bbk_beam.mtRooms.operation.dto.LogisticsInfo;
import bbk_beam.mtRooms.reservation.IReservationSession;
import bbk_beam.mtRooms.reservation.dto.Customer;
import bbk_beam.mtRooms.reservation.dto.Room;
import bbk_beam.mtRooms.reservation.dto.RoomProperty;
import bbk_beam.mtRooms.test_data.TestDBGenerator;
import bbk_beam.mtRooms.uaa.exception.SessionActive;
import bbk_beam.mtRooms.uaa.exception.SessionLocked;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

public class SessionDriverTest {
    private SessionDriver session_driver;
    private String test_db_file = "SessionDriver_test.db";
    private Token bad_token = new Token("BADTOKEN", new Date(), Date.from(Instant.now().plus(1, ChronoUnit.HOURS)));

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
    public void scenario_test1() throws Exception {
        //create dummy data file for test
        this.session_driver.init(this.test_db_file);
        //login admin
        Token admin_token = this.session_driver.login("root", "letmein");
        IAuthenticatedAdministration administration = this.session_driver.getAdministrationInstance(admin_token);
        //create a user
        administration.createNewAccount(admin_token, SessionType.USER, "user01", "userpassword");
        //logout from admin session
        this.session_driver.logout(admin_token);
        //login with new user
        Token user_token = this.session_driver.login("user01", "userpassword");
        //access reservation system
        IAuthenticatedFrontDesk front_desk = this.session_driver.getFrontDeskInstance(user_token);
        IReservationSession session = front_desk.openReservationSession(user_token);
        //test customer details fetching
        Customer customer = session.getCustomerAccount(user_token, 1);
        Assert.assertEquals(new Integer(1), customer.customerID());
        Assert.assertEquals(new Integer(1), customer.membershipTypeID());
        Assert.assertEquals("2015-10-15 16:15:12", TimestampConverter.getUTCTimestampString(customer.accountCreationDate()));
        Assert.assertEquals("Mrs", customer.title());
        Assert.assertEquals("Joanne", customer.name());
        Assert.assertEquals("Bouvier", customer.surname());
        Assert.assertEquals("Flat 4", customer.address1());
        Assert.assertEquals("21 big road", customer.address2());
        Assert.assertEquals("London", customer.city());
        Assert.assertEquals("London", customer.county());
        Assert.assertEquals("UK", customer.country());
        Assert.assertEquals("W1 4AQ", customer.postCode());
        Assert.assertEquals("+44 9876 532 123", customer.phone1());
        Assert.assertNull(customer.phone2());
        Assert.assertEquals("jbouvier@mail.com", customer.email());
        //test search of room with capacity 90+
        List<Room> list = session.search(user_token, 1, new RoomProperty(
                RoomProperty.Trilean.UNDEFINED,
                RoomProperty.Trilean.UNDEFINED,
                RoomProperty.Trilean.UNDEFINED,
                RoomProperty.Trilean.UNDEFINED,
                -1,
                90)
        );
        Assert.assertEquals(1, list.size());
        Assert.assertEquals(new Room(8, 3, 1, 6, "Theatre"), list.get(0));
        //closing reservation session
        front_desk.closeReservationSession(session);
        //logout user
        this.session_driver.logout(user_token);
        //reset driver
        this.session_driver.reset();
    }

    @Test
    public void init() throws Exception {
        //Bran new file
        this.session_driver.init("new_db_init.db");
        Assert.assertTrue(Files.deleteIfExists(Paths.get("new_db_init.db")));
        this.session_driver.reset();
        //Pre-existing file (uses the dummy test file)
        this.session_driver.init(this.test_db_file);
    }

    @Test(expected = SessionActive.class)
    public void init_active_session() throws Exception {
        this.session_driver.init(this.test_db_file);
        this.session_driver.init(this.test_db_file);
    }

    @Test(expected = SessionLocked.class)
    public void reset_locked_session() throws Exception {
        this.session_driver.init(this.test_db_file);
        Token admin_token = this.session_driver.login("root", "letmein");
        this.session_driver.reset();
    }

    @Test
    public void getFrontDeskInstance() throws Exception {
        this.session_driver.init(this.test_db_file);
        Token token = this.session_driver.login("root", "letmein");
        IAuthenticatedFrontDesk front_desk = this.session_driver.getFrontDeskInstance(token);
        IReservationSession session = front_desk.openReservationSession(token);
        Customer customer = session.getCustomerAccount(token, 1);
        System.out.println(customer);
        front_desk.closeReservationSession(session);
        this.session_driver.logout(token);
        this.session_driver.reset();
    }

    @Test(expected = AuthenticationFailureException.class)
    public void getFrontDeskInstance_failed_authentication1() throws Exception {
        //create dummy data file for test
        this.session_driver.init(this.test_db_file);
        IAuthenticatedFrontDesk front_desk = this.session_driver.getFrontDeskInstance(bad_token);
    }

    @Test(expected = AuthenticationFailureException.class)
    public void getFrontDeskInstance_failed_authentication2() throws Exception {
        //create dummy data file for test
        this.session_driver.init(this.test_db_file);
        Token token = this.session_driver.login("root", "letmein");
        IAuthenticatedFrontDesk front_desk = this.session_driver.getFrontDeskInstance(token);
        IReservationSession session = front_desk.openReservationSession(bad_token);
    }

    @Test
    public void getAdministrationInstance() throws Exception {
        //create dummy data file for test
        this.session_driver.init(this.test_db_file);
        //Test 3x consecutive time
        for (int i = 0; i < 3; i++) {
            Token token = this.session_driver.login("root", "letmein");
            IAuthenticatedAdministration administration = this.session_driver.getAdministrationInstance(token);
            Account account = administration.getAccount(token, "root");
            System.out.println(account);
            this.session_driver.logout(token);
        }
        this.session_driver.reset();
    }

    @Test(expected = AuthenticationFailureException.class)
    public void getAdministrationInstance_failed_authentication() throws Exception {
        //create dummy data file for test
        this.session_driver.init(this.test_db_file);
        //try getting administration access
        IAuthenticatedAdministration administration = this.session_driver.getAdministrationInstance(bad_token);
    }

    @Test(expected = AuthenticationFailureException.class)
    public void getAdministrationInstance_failed_access_level() throws Exception {
        //create dummy data file for test
        this.session_driver.init(this.test_db_file);
        //login admin
        Token admin_token = this.session_driver.login("root", "letmein");
        IAuthenticatedAdministration administration = this.session_driver.getAdministrationInstance(admin_token);
        //create a user
        administration.createNewAccount(admin_token, SessionType.USER, "user01", "userpassword");
        //logout from admin session
        this.session_driver.logout(admin_token);
        //login with new user
        Token user_token = this.session_driver.login("user01", "userpassword");
        this.session_driver.getAdministrationInstance(user_token);
    }

    @Test
    public void getLogisticsPersonnelInstance() throws Exception {
        this.session_driver.init(this.test_db_file);
        Token token = this.session_driver.login("root", "letmein");
        IAuthenticatedLogisticsPersonnel operations = this.session_driver.getLogisticsPersonnelInstance(token);
        LogisticsInfo info = operations.getInfo(
                token,
                1,
                2,
                TimestampConverter.getDateObject("2018-03-10 00:00:00"),
                TimestampConverter.getDateObject("2018-03-10 23:59:59")
        );
        Assert.assertEquals(7, info.getEntries().size());
        for (LogisticsEntry entry : info.getEntries()) {
            System.out.println(entry);
        }
        this.session_driver.logout(token);
    }

    @Test
    public void getRevenuePersonnelInstance() throws Exception {
        //TODO getRevenuePersonnelInstance() implementation
    }
}