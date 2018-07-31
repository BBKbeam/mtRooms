package bbk_beam.mtRooms.reservation.processing;

import bbk_beam.mtRooms.admin.authentication.Token;
import bbk_beam.mtRooms.db.DbSystemBootstrap;
import bbk_beam.mtRooms.db.IReservationDbAccess;
import bbk_beam.mtRooms.db.IUserAccDbAccess;
import bbk_beam.mtRooms.db.TimestampConverter;
import bbk_beam.mtRooms.db.session.SessionType;
import bbk_beam.mtRooms.reservation.delegate.ReservationDbDelegate;
import bbk_beam.mtRooms.reservation.dto.Payment;
import bbk_beam.mtRooms.reservation.dto.PaymentMethod;
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
    private Token token = new Token("00001", new Date(), Date.from(Instant.now().plus(1, ChronoUnit.DAYS)));
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
        Files.deleteIfExists(Paths.get("payment_processing_test.db"));
    }

    @Test
    public void pay() throws Exception {
        Reservation mock_reservation = mock(Reservation.class);
        when(mock_reservation.id()).thenReturn(1);
        Payment payment = new Payment(
                mock_reservation,
                0.,
                new Date(),
                "Payment note...",
                new PaymentMethod(1, "Cash")
        );
        Assert.assertEquals(new Double(58.), this.paymentProcessing.pay(this.token, mock_reservation, payment));
        Double total_check = 58.;
        Date payment_date = Date.from(Instant.now());
        for (int i = 0; i < 23; i++) {
            Payment incremental_payment = new Payment(
                    mock_reservation,
                    2.5,
                    payment_date,
                    "Payment note...",
                    new PaymentMethod(1, "Cash")
            );
            total_check -= incremental_payment.amount();
            payment_date = Date.from(payment_date.toInstant().plus(1, ChronoUnit.MINUTES));
            Assert.assertEquals(total_check, this.paymentProcessing.pay(this.token, mock_reservation, incremental_payment));
        }
    }

    @Test
    public void getPayments() throws Exception {
        Reservation mock_reservation = mock(Reservation.class);
        when(mock_reservation.id()).thenReturn(4);
        List<Payment> payments = paymentProcessing.getPayments(this.token, mock_reservation);
        Assert.assertEquals(2, payments.size());
        for (Payment p : payments) {
            if (p.id() == 3) {
                Assert.assertEquals("TestHashID0000003", p.hashID());
                Assert.assertEquals(new Double(40.5), p.amount());
                Assert.assertEquals("2018-02-10 01:58:00", TimestampConverter.getUTCTimestampString(p.timestamp()));
                Assert.assertNull(p.note());
                Assert.assertEquals(new Integer(2), p.paymentMethod().id());
                Assert.assertEquals("Debit Card", p.paymentMethod().description());
            } else if (p.id() == 4) {
                Assert.assertEquals("TestHashID0000004", p.hashID());
                Assert.assertEquals(new Double(22.5), p.amount());
                Assert.assertEquals("2018-02-11 13:12:50", TimestampConverter.getUTCTimestampString(p.timestamp()));
                Assert.assertNull(p.note());
                Assert.assertEquals(new Integer(2), p.paymentMethod().id());
                Assert.assertEquals("Debit Card", p.paymentMethod().description());
            } else {
                Assert.fail("Unexpected payment ID found (not 3 or 4).");
            }
        }
    }

    @Test
    public void getPaymentTypes() throws Exception {
        List<PaymentMethod> paymentMethods = paymentProcessing.getPaymentMethods(this.token);
        Assert.assertEquals(3, paymentMethods.size());
        for (PaymentMethod t : paymentMethods) {
            System.out.println(t);
            if (t.id() == 1) {
                Assert.assertEquals(new Integer(1), t.id());
                Assert.assertEquals("Cash", t.description());
            } else if (t.id() == 2) {
                Assert.assertEquals(new Integer(2), t.id());
                Assert.assertEquals("Debit Card", t.description());
            } else if (t.id() == 3) {
                Assert.assertEquals(new Integer(3), t.id());
                Assert.assertEquals("Credit Card", t.description());
            } else {
                Assert.fail("Unexpected PaymentType.id found (>3).");
            }
        }
    }
}