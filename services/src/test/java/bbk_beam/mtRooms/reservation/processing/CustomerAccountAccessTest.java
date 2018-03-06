package bbk_beam.mtRooms.reservation.processing;

import bbk_beam.mtRooms.admin.authentication.Token;
import bbk_beam.mtRooms.db.DbSystemBootstrap;
import bbk_beam.mtRooms.db.IReservationDbAccess;
import bbk_beam.mtRooms.db.IUserAccDbAccess;
import bbk_beam.mtRooms.db.TimestampConverter;
import bbk_beam.mtRooms.db.session.SessionType;
import bbk_beam.mtRooms.reservation.delegate.ReservationDbDelegate;
import bbk_beam.mtRooms.reservation.dto.Customer;
import bbk_beam.mtRooms.reservation.exception.FailedDbWrite;
import bbk_beam.mtRooms.reservation.exception.InvalidCustomer;
import bbk_beam.mtRooms.test_data.TestDBGenerator;
import javafx.util.Pair;
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

public class CustomerAccountAccessTest {
    private DbSystemBootstrap db_bootstrapper = new DbSystemBootstrap();
    private IReservationDbAccess reservationDbAccess;
    private IUserAccDbAccess userAccDbAccess;
    Token token = new Token("00001", new Date(), Date.from(Instant.now().plus(1, ChronoUnit.DAYS)));
    private CustomerAccountAccess customerAccountAccess;

    @Before
    public void setUp() throws Exception {
        Files.deleteIfExists(Paths.get("customer_account_access_test.db"));
        TestDBGenerator testDBGenerator = new TestDBGenerator();
        testDBGenerator.createTestDB("customer_account_access_test.db");
        this.db_bootstrapper.init("customer_account_access_test.db");
        this.userAccDbAccess = this.db_bootstrapper.getUserAccDbAccess();
        this.reservationDbAccess = this.db_bootstrapper.getReservationDbAccess();
        this.userAccDbAccess.openSession(this.token.getSessionId(), this.token.getExpiry(), SessionType.ADMIN, 1);
        this.customerAccountAccess = new CustomerAccountAccess(new ReservationDbDelegate(reservationDbAccess));
    }

    @After
    public void tearDown() throws Exception {
        this.userAccDbAccess.closeSession(this.token.getSessionId());
        this.userAccDbAccess = null;
        this.reservationDbAccess = null;
        this.customerAccountAccess = null;
        Files.deleteIfExists(Paths.get("customer_account_access_test.db"));
    }

    @Test
    public void getCustomerAccount() throws Exception {
        Customer customer = this.customerAccountAccess.getCustomerAccount(this.token, 1);
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
        Assert.assertEquals(null, customer.phone2());
        Assert.assertEquals("jbouvier@mail.com", customer.email());
    }

    @Test(expected = InvalidCustomer.class)
    public void getCustomerAccount_fail() throws Exception {
        this.customerAccountAccess.getCustomerAccount(this.token, 999999);
    }

    @Test
    public void findCustomer() throws Exception {
        List<Pair<Integer, String>> search_results = this.customerAccountAccess.findCustomer(this.token, "Cage");
        Assert.assertEquals(1, search_results.size());
        Assert.assertEquals(new Integer(4), search_results.get(0).getKey());
        Assert.assertEquals("Mr Nicholas Cage", search_results.get(0).getValue());
    }

    @Test
    public void createNewCustomer() throws Exception {
        Customer pre_commit_DTO = new Customer(
                -1,
                1,
                Date.from(Instant.now()),
                "Mr",
                "John",
                "Smith",
                "101 Main road",
                null,
                "AB16 X00",
                "Bigcity",
                null,
                "UK",
                "01234 567 891",
                null,
                "jsmith@mail.com"
        );
        Customer post_commit_DTO = this.customerAccountAccess.createNewCustomer(this.token, pre_commit_DTO);
        Customer record_DTO = this.customerAccountAccess.getCustomerAccount(this.token, 5);
        Assert.assertEquals((Integer) 5, post_commit_DTO.customerID());
        Assert.assertTrue(post_commit_DTO.equals(record_DTO));
    }

    @Test
    public void saveCustomerChangesToDB() throws Exception {
        Customer customer = new Customer(
                -1,
                1,
                Date.from(Instant.now()),
                "Mr",
                "John",
                "Smith",
                "101 Main road",
                null,
                "AB16 X00",
                "Bigcity",
                null,
                "UK",
                "01234 567 891",
                null,
                "jsmith@mail.com"
        );
        customer = this.customerAccountAccess.createNewCustomer(this.token, customer);
        Customer update = new Customer(
                customer.customerID(),
                1,
                Date.from(Instant.now()),
                "Miss",
                "Jane",
                "Smith",
                "101 Main road",
                null,
                "AB12 C34",
                "Big city",
                "Some county",
                "UK",
                "01234 567 891",
                null,
                "jsmith@mail.com"
        );
        this.customerAccountAccess.saveCustomerChangesToDB(this.token, update);
        Customer check = this.customerAccountAccess.getCustomerAccount(this.token, customer.customerID());
        Assert.assertTrue(update.equals(check));
    }

    @Test(expected = FailedDbWrite.class)
    public void saveCustomerChangesToDB_fail() throws Exception {
        Customer update = new Customer(
                9999, //Does not exist
                1,
                Date.from(Instant.now()),
                "Miss",
                "Jane",
                "Smith",
                "101 Main road",
                null,
                "AB12 C34",
                "Big city",
                "Some county",
                "UK",
                "01234 567 891",
                null,
                "jsmith@mail.com"
        );
        this.customerAccountAccess.saveCustomerChangesToDB(this.token, update);
    }
}