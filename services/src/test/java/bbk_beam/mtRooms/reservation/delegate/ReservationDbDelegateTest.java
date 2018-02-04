package bbk_beam.mtRooms.reservation.delegate;

import bbk_beam.mtRooms.admin.authentication.Token;
import bbk_beam.mtRooms.db.DbSystemBootstrap;
import bbk_beam.mtRooms.db.IReservationDbAccess;
import bbk_beam.mtRooms.db.IUserAccDbAccess;
import bbk_beam.mtRooms.db.ReservationDbAccess;
import bbk_beam.mtRooms.db.exception.DbQueryException;
import bbk_beam.mtRooms.db.session.SessionType;
import bbk_beam.mtRooms.reservation.dto.Discount;
import bbk_beam.mtRooms.reservation.dto.PaymentType;
import bbk_beam.mtRooms.reservation.dto.Reservation;
import bbk_beam.mtRooms.test_data.TestDBGenerator;
import eadjlib.datastructure.ObjectTable;
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

import static org.mockito.Mockito.mock;

public class ReservationDbDelegateTest {
    private DbSystemBootstrap db_bootstrapper = new DbSystemBootstrap();
    private IReservationDbAccess reservationDbAccess;
    private IReservationDbAccess mock_reservationDbAccess;
    private IUserAccDbAccess userAccDbAccess;
    Token token = new Token("00001", new Date(), Date.from(Instant.now().plus(1, ChronoUnit.DAYS)));
    private ReservationDbDelegate reservationDbDelegate;

    @Before
    public void setUp() throws Exception {
        Files.deleteIfExists(Paths.get("reservation_db_delegate_test.db"));
        TestDBGenerator testDBGenerator = new TestDBGenerator();
        testDBGenerator.createTestDB("reservation_db_delegate_test.db");
        this.db_bootstrapper.init("reservation_db_delegate_test.db");
        this.userAccDbAccess = this.db_bootstrapper.getUserAccDbAccess();
        this.reservationDbAccess = this.db_bootstrapper.getReservationDbAccess();
        this.mock_reservationDbAccess = mock(ReservationDbAccess.class);
        this.userAccDbAccess.openSession(this.token.getSessionId(), this.token.getExpiry(), SessionType.ADMIN, 1);
        this.reservationDbDelegate = new ReservationDbDelegate(reservationDbAccess);
    }

    @After
    public void tearDown() throws Exception {
        this.userAccDbAccess.closeSession(this.token.getSessionId());
        this.userAccDbAccess = null;
        this.reservationDbAccess = null;
        this.mock_reservationDbAccess = null;
        this.reservationDbDelegate = null;
        //Files.deleteIfExists(Paths.get("reservation_db_delegate_test.db"));
    }

    @Test
    public void getCustomerAccount() throws Exception {

        Assert.assertTrue(false);
        //TODO
    }

    @Test(expected = DbQueryException.class)
    public void getCustomerAccount_fail() throws Exception {
        this.reservationDbDelegate.getCustomerAccount(this.token, 1);
    }

    @Test
    public void findCustomer() throws Exception {
        Assert.assertTrue(false);
        //TODO
    }

    @Test
    public void createNewCustomer() throws Exception {
        Assert.assertTrue(false);
        //TODO
    }

    @Test
    public void saveCustomerChangesToDB() throws Exception {
        Assert.assertTrue(false);
        //TODO
    }

    @Test(expected = DbQueryException.class)
    public void saveCustomerChangesToDB_fail() throws Exception {
        Assert.assertTrue(false);
        //TODO
    }

    @Test
    public void pay() throws Exception {
        Assert.assertTrue(false);
        //TODO
    }

    @Test
    public void getPaymentTypes() throws Exception {
        List<PaymentType> list = this.reservationDbDelegate.getPaymentTypes(this.token);
        Assert.assertEquals(3, list.size());
    }

    @Test
    public void createReservation() throws Exception {
        Assert.assertTrue(false);
        //TODO
    }

    @Test
    public void cancelReservation() throws Exception {
        Assert.assertTrue(false);
        //TODO
    }

    @Test
    public void getReservation() throws Exception {
        ObjectTable table = this.reservationDbDelegate.getReservation(this.token, 1);
        System.out.println(table);
        Assert.assertTrue(false);
        //TODO
    }

    @Test
    public void getReservations() throws Exception {
        Assert.assertTrue(false);
        //TODO
    }

    @Test
    public void getReservedRooms() throws Exception {
        Reservation reservation = new Reservation(1, Date.from(Instant.now()), 1, new Discount(1, 20., 1, ""));
        ObjectTable table = this.reservationDbDelegate.getReservedRooms(this.token, reservation);
        System.out.println(table);
        Assert.assertTrue(false);
        //TODO
    }

    @Test
    public void getDiscount() throws Exception {
        Assert.assertTrue(false);
        //TODO
    }
}