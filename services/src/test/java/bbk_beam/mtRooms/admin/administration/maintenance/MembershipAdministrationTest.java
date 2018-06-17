package bbk_beam.mtRooms.admin.administration.maintenance;

import bbk_beam.mtRooms.admin.authentication.Token;
import bbk_beam.mtRooms.db.DbSystemBootstrap;
import bbk_beam.mtRooms.db.IReservationDbAccess;
import bbk_beam.mtRooms.db.IUserAccDbAccess;
import bbk_beam.mtRooms.db.session.SessionType;
import bbk_beam.mtRooms.reservation.dto.Discount;
import bbk_beam.mtRooms.reservation.dto.DiscountCategory;
import bbk_beam.mtRooms.reservation.dto.Membership;
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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MembershipAdministrationTest {
    private DbSystemBootstrap db_bootstrapper = new DbSystemBootstrap();
    private IUserAccDbAccess user_db_access;
    private IReservationDbAccess reservation_db_access;
    private MembershipAdministration membershipAdministration;
    private final String session_user_id = "00001";
    private final Date session_expiry = Date.from(Instant.now().plus(1, ChronoUnit.DAYS));
    private Token token = new Token("00001", new Date(), Date.from(Instant.now().plus(1, ChronoUnit.DAYS)));

    @Before
    public void setUp() throws Exception {
        TestDBGenerator testDBGenerator = new TestDBGenerator();
        testDBGenerator.createTestDB("reservation_maintenance_test.db");
        this.db_bootstrapper.init("reservation_maintenance_test.db");
        this.user_db_access = this.db_bootstrapper.getUserAccDbAccess();
        this.reservation_db_access = this.db_bootstrapper.getReservationDbAccess();
        this.user_db_access.openSession(this.session_user_id, this.session_expiry, SessionType.ADMIN, 1);
        this.membershipAdministration = new MembershipAdministration(this.reservation_db_access);
    }

    @After
    public void tearDown() throws Exception {
        this.user_db_access.closeSession(this.session_user_id);
        this.user_db_access = null;
        this.membershipAdministration = null;
        Files.deleteIfExists(Paths.get("reservation_maintenance_test.db"));
    }

    @Test
    public void getMemberships() throws Exception {
        ObjectTable table = this.membershipAdministration.getMemberships(this.token);
        Assert.assertEquals(5, table.rowCount());
        Assert.assertEquals(7, table.columnCount());
        System.out.println(table);
    }

    @Test
    public void getDiscountCategories() throws Exception {
        ObjectTable table = this.membershipAdministration.getDiscountCategories(this.token);
        Assert.assertEquals(3, table.rowCount());
        Assert.assertEquals(4, table.columnCount());
    }

    @Test
    public void addMembership() throws Exception {
        Discount discount = new Discount(3, 10., new DiscountCategory(3, "Member"));
        Membership mock_membership = mock(Membership.class);
        when(mock_membership.discount()).thenReturn(discount);
        when(mock_membership.id()).thenReturn(-1);
        //Existing membership description
        when(mock_membership.description()).thenReturn("Full Member");
        Membership membership1 = this.membershipAdministration.add(this.token, mock_membership);
        Assert.assertEquals(new Integer(3), membership1.id());
        //Non-existing membership
        when(mock_membership.description()).thenReturn("Platinum");
        Membership membership2 = this.membershipAdministration.add(this.token, mock_membership);
        Assert.assertEquals(new Integer(4), membership2.id());
    }

    @Test
    public void removeMembership() throws Exception {
        DiscountCategory discountCategory = new DiscountCategory(-1, "TestCategory");
        Discount discount = new Discount(-1, 50., discountCategory);
        Membership membership = new Membership(-1, "TestMembership", discount);
        //Adding test membership
        Membership new_membership = this.membershipAdministration.add(this.token, membership);
        Assert.assertEquals(new Integer(4), new_membership.id());
        Assert.assertEquals(new Integer(4), new_membership.discount().id());
        Assert.assertEquals(new Integer(4), new_membership.discount().category().id());
        //Testing removal
        Assert.assertTrue(this.membershipAdministration.remove(this.token, new_membership));
    }

    @Test
    public void removeMembership_Fail() throws Exception {
        Discount discount = mock(Discount.class);
        DiscountCategory discountCategory = mock(DiscountCategory.class);
        Membership membership = mock(Membership.class);
        when(discount.category()).thenReturn(discountCategory);
        when(membership.discount()).thenReturn(discount);
        when(discount.id()).thenReturn(1);
        when(discountCategory.id()).thenReturn(1);
        when(membership.id()).thenReturn(1);
        Assert.assertFalse(this.membershipAdministration.remove(this.token, membership));
    }
}