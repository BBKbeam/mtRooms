package bbk_beam.mtRooms.reservation.processing;

import bbk_beam.mtRooms.admin.authentication.Token;
import bbk_beam.mtRooms.db.DbSystemBootstrap;
import bbk_beam.mtRooms.db.IReservationDbAccess;
import bbk_beam.mtRooms.db.IUserAccDbAccess;
import bbk_beam.mtRooms.db.exception.DbQueryException;
import bbk_beam.mtRooms.db.session.SessionType;
import bbk_beam.mtRooms.reservation.delegate.ReservationDbDelegate;
import bbk_beam.mtRooms.reservation.dto.Customer;
import bbk_beam.mtRooms.reservation.exception.FailedDbWrite;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

public class CustomerAccountAccessTest {
    private DbSystemBootstrap db_bootstrapper = new DbSystemBootstrap();
    private IReservationDbAccess reservationDbAccess;
    private IUserAccDbAccess userAccDbAccess;
    Token token = new Token("00001", new Date(), Date.from(Instant.now().plus(1, ChronoUnit.DAYS)));
    private CustomerAccountAccess customerAccountAccess;

    @Before
    public void setUp() throws Exception {
        Files.deleteIfExists(Paths.get("customer_account_access_test.db"));
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
        Assert.assertTrue(false);
        //TODO
    }

    @Test(expected = DbQueryException.class)
    public void getCustomerAccount_fail() throws Exception {
        this.customerAccountAccess.getCustomerAccount(this.token, 1);
    }

    @Test
    public void getCustomerAccount1() throws Exception {
        Assert.assertTrue(false);
        //TODO
    }

    @Test
    public void findCustomer() throws Exception {
        Assert.assertTrue(false);
        //TODO
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
        Customer record_DTO = this.customerAccountAccess.getCustomerAccount(this.token, 1);
        Assert.assertEquals((Integer) 1, post_commit_DTO.customerID());
        Assert.assertTrue(post_commit_DTO.equals(record_DTO));
    }

    @Test(expected = FailedDbWrite.class)
    public void createNewCustomer_fail() throws Exception {

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
                1, //Does not exist
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