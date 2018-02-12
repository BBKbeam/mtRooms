package bbk_beam.mtRooms.reservation.processing;

import bbk_beam.mtRooms.admin.authentication.Token;
import bbk_beam.mtRooms.db.DbSystemBootstrap;
import bbk_beam.mtRooms.db.IReservationDbAccess;
import bbk_beam.mtRooms.db.IUserAccDbAccess;
import bbk_beam.mtRooms.db.session.SessionType;
import bbk_beam.mtRooms.reservation.delegate.ReservationDbDelegate;
import bbk_beam.mtRooms.reservation.dto.Payment;
import bbk_beam.mtRooms.reservation.dto.PaymentType;
import bbk_beam.mtRooms.reservation.dto.Reservation;
import bbk_beam.mtRooms.test_data.TestDBGenerator;
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
import static org.mockito.Mockito.when;

public class PaymentProcessingTest {
    private DbSystemBootstrap db_bootstrapper = new DbSystemBootstrap();
    private IReservationDbAccess reservationDbAccess;
    private IUserAccDbAccess userAccDbAccess;
    Token token = new Token("00001", new Date(), Date.from(Instant.now().plus(1, ChronoUnit.DAYS)));
    private ReservationDbDelegate reservationDbDelegate;
    private PaymentProcessing paymentProcessing;

    @Before
    public void setUp() throws Exception {
        Files.deleteIfExists(Paths.get("payment_processing_test.db"));
        TestDBGenerator testDBGenerator = new TestDBGenerator();
        testDBGenerator.createTestDB("payment_processing_test.db");
        this.db_bootstrapper.init("payment_processing_test.db");
        this.userAccDbAccess = this.db_bootstrapper.getUserAccDbAccess();
        this.reservationDbAccess = this.db_bootstrapper.getReservationDbAccess();
        this.userAccDbAccess.openSession(this.token.getSessionId(), this.token.getExpiry(), SessionType.ADMIN, 1);
        this.reservationDbDelegate = new ReservationDbDelegate(reservationDbAccess);
        this.paymentProcessing = new PaymentProcessing(this.reservationDbDelegate);
    }

    @After
    public void tearDown() throws Exception {
        this.userAccDbAccess.closeSession(this.token.getSessionId());
        this.userAccDbAccess = null;
        this.reservationDbAccess = null;
        this.reservationDbDelegate = null;
        this.paymentProcessing = null;
        //Files.deleteIfExists(Paths.get("payment_processing_test.db"));
    }

    @Test
    public void getPayments() throws Exception {
        Reservation mock_reservation = mock(Reservation.class);
        when(mock_reservation.id()).thenReturn(1);
        List<Payment> payments = paymentProcessing.getPayments(this.token, mock_reservation);
        for (Payment p : payments) {
            System.out.println(p);
        }
        Assert.assertTrue(false);
        //TODO
    }

    @Test
    public void getPaymentTypes() throws Exception {
        List<PaymentType> paymentTypes = paymentProcessing.getPaymentTypes(this.token);
        for (PaymentType t : paymentTypes) {
            System.out.println(t);
        }
        Assert.assertTrue(false);
        //TODO
    }
}