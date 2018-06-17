package bbk_beam.mtRooms.admin.administration;

import bbk_beam.mtRooms.admin.administration.maintenance.MembershipAdministration;
import bbk_beam.mtRooms.admin.administration.maintenance.RealEstateAdministration;
import bbk_beam.mtRooms.admin.administration.maintenance.ReservationDbMaintenance;
import bbk_beam.mtRooms.admin.administration.maintenance.UserAccAdministration;
import bbk_beam.mtRooms.admin.authentication.IAuthenticationSystem;
import bbk_beam.mtRooms.admin.authentication.Token;
import bbk_beam.mtRooms.admin.authentication.UserAccountChecker;
import bbk_beam.mtRooms.admin.dto.Account;
import bbk_beam.mtRooms.admin.dto.AccountType;
import bbk_beam.mtRooms.admin.dto.Usage;
import bbk_beam.mtRooms.admin.exception.AccountExistenceException;
import bbk_beam.mtRooms.admin.exception.AccountOverrideException;
import bbk_beam.mtRooms.admin.exception.FailedRecordUpdate;
import bbk_beam.mtRooms.db.DbSystemBootstrap;
import bbk_beam.mtRooms.db.IReservationDbAccess;
import bbk_beam.mtRooms.db.IUserAccDbAccess;
import bbk_beam.mtRooms.db.TimestampConverter;
import bbk_beam.mtRooms.db.exception.DbQueryException;
import bbk_beam.mtRooms.db.exception.SessionCorruptedException;
import bbk_beam.mtRooms.db.exception.SessionExpiredException;
import bbk_beam.mtRooms.db.exception.SessionInvalidException;
import bbk_beam.mtRooms.db.session.SessionType;
import bbk_beam.mtRooms.reservation.dto.*;
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
import java.util.HashMap;
import java.util.List;

import static org.mockito.Mockito.*;

public class AdminSessionTest {
    private DbSystemBootstrap db_bootstrapper = new DbSystemBootstrap();
    private IUserAccDbAccess user_db_access;
    private IReservationDbAccess reservation_db_access;
    private UserAccAdministration mock_administration_module;
    private ReservationDbMaintenance mock_maintenance_module;
    private MembershipAdministration mock_memberships_module;
    private RealEstateAdministration mock_real_estate_module;
    private IAuthenticationSystem mock_authentication_module;
    private AdminSession adminSession;
    private AdminSession realAdminSession;
    private Date session_expiry;
    private Token admin_token;

    @Before
    public void setUp() throws Exception {
        session_expiry = Date.from(Instant.now().plus(1, ChronoUnit.DAYS));
        admin_token = new Token("00001", new Date(), session_expiry);

        TestDBGenerator testDBGenerator = new TestDBGenerator();
        testDBGenerator.createTestDB("admin_session_test.db");
        this.db_bootstrapper.init("admin_session_test.db");
        this.user_db_access = this.db_bootstrapper.getUserAccDbAccess();
        this.reservation_db_access = this.db_bootstrapper.getReservationDbAccess();
        this.user_db_access.openSession(admin_token.getSessionId(), admin_token.getExpiry(), SessionType.ADMIN, 1);
        this.realAdminSession = new AdminSession(
                this.reservation_db_access,
                this.user_db_access,
                new UserAccountChecker(this.user_db_access)
        );

        mock_administration_module = mock(UserAccAdministration.class);
        mock_maintenance_module = mock(ReservationDbMaintenance.class);
        mock_memberships_module = mock(MembershipAdministration.class);
        mock_real_estate_module = mock(RealEstateAdministration.class);
        mock_authentication_module = mock(IAuthenticationSystem.class);
        adminSession = new AdminSession(
                mock_administration_module,
                mock_maintenance_module,
                mock_memberships_module,
                mock_real_estate_module,
                mock_authentication_module
        );
    }

    @After
    public void tearDown() throws Exception {
        this.user_db_access.closeSession(this.admin_token.getSessionId());
        Files.deleteIfExists(Paths.get("admin_session_test.db"));

        this.user_db_access = null;
        mock_administration_module = null;
        mock_maintenance_module = null;
        mock_memberships_module = null;
        mock_real_estate_module = null;
        mock_authentication_module = null;
        adminSession = null;
        session_expiry = null;
        admin_token = null;
    }

    @Test
    public void createNewAccount() throws Exception {
        doNothing().when(mock_administration_module).checkValidity(this.admin_token);
        when(mock_authentication_module.hasValidAccessRights(admin_token, SessionType.ADMIN)).thenReturn(true);
        adminSession.createNewAccount(this.admin_token, SessionType.USER, "user001", "password");
        verify(mock_administration_module, times(1)).createNewAccount(SessionType.USER, "user001", "password");
    }

    @Test(expected = SessionInvalidException.class)
    public void createNewAccount_session_invalid_fail() throws Exception {
        doThrow(SessionInvalidException.class).when(mock_administration_module).checkValidity(this.admin_token);
        adminSession.createNewAccount(this.admin_token, SessionType.USER, "user001", "password");
    }

    @Test(expected = SessionInvalidException.class)
    public void createNewAccount_session_invalid_fail_incorrect_session_type() throws Exception {
        when(mock_authentication_module.hasValidAccessRights(admin_token, SessionType.ADMIN)).thenReturn(false);
        adminSession.createNewAccount(this.admin_token, SessionType.USER, "user001", "password");
    }

    @Test(expected = SessionExpiredException.class)
    public void createNewAccount_session_expired_fail() throws Exception {
        doThrow(SessionExpiredException.class).when(mock_administration_module).checkValidity(this.admin_token);
        adminSession.createNewAccount(this.admin_token, SessionType.USER, "user001", "password");
    }

    @Test(expected = SessionCorruptedException.class)
    public void createNewAccount_session_corrupted_fail() throws Exception {
        doThrow(SessionCorruptedException.class).when(mock_administration_module).checkValidity(this.admin_token);
        adminSession.createNewAccount(this.admin_token, SessionType.USER, "user001", "password");
    }

    @Test(expected = AccountExistenceException.class)
    public void createNewAccount_account_fetch_fail() throws Exception {
        doNothing().when(mock_administration_module).checkValidity(this.admin_token);
        when(mock_authentication_module.hasValidAccessRights(admin_token, SessionType.ADMIN)).thenReturn(true);
        doThrow(AccountExistenceException.class).when(mock_administration_module).createNewAccount(SessionType.USER, "user001", "password");
        adminSession.createNewAccount(this.admin_token, SessionType.USER, "user001", "password");
    }

    @Test(expected = RuntimeException.class)
    public void createNewAccount_runtime_fail() throws Exception {
        doNothing().when(mock_administration_module).checkValidity(this.admin_token);
        when(mock_authentication_module.hasValidAccessRights(admin_token, SessionType.ADMIN)).thenReturn(true);
        doThrow(FailedRecordUpdate.class).when(mock_administration_module).createNewAccount(SessionType.USER, "user001", "password");
        adminSession.createNewAccount(this.admin_token, SessionType.USER, "user001", "password");
    }

    @Test
    public void updateAccountPassword() throws Exception {
        doNothing().when(mock_administration_module).checkValidity(this.admin_token);
        when(mock_authentication_module.hasValidAccessRights(admin_token, SessionType.ADMIN)).thenReturn(true);
        adminSession.updateAccountPassword(this.admin_token, 1, "new_password");
        verify(mock_administration_module, times(1)).updateAccountPassword(1, "new_password");
    }

    @Test(expected = SessionInvalidException.class)
    public void updateAccountPassword_invalid_session_fail() throws Exception {
        doThrow(SessionInvalidException.class).when(mock_administration_module).checkValidity(this.admin_token);
        adminSession.updateAccountPassword(this.admin_token, 1, "new_password");
    }

    @Test(expected = SessionExpiredException.class)
    public void updateAccountPassword_expired_session_fail() throws Exception {
        doThrow(SessionExpiredException.class).when(mock_administration_module).checkValidity(this.admin_token);
        adminSession.updateAccountPassword(this.admin_token, 1, "new_password");
    }

    @Test(expected = SessionCorruptedException.class)
    public void updateAccountPassword_corrupted_session_fail() throws Exception {
        doThrow(SessionCorruptedException.class).when(mock_administration_module).checkValidity(this.admin_token);
        adminSession.updateAccountPassword(this.admin_token, 1, "new_password");
    }

    @Test(expected = AccountExistenceException.class)
    public void updateAccountPassword_account_fetch_fail() throws Exception {
        doNothing().when(mock_administration_module).checkValidity(this.admin_token);
        when(mock_authentication_module.hasValidAccessRights(this.admin_token, SessionType.ADMIN)).thenReturn(true);
        doThrow(AccountExistenceException.class).when(mock_administration_module).updateAccountPassword(1, "new_password");
        adminSession.updateAccountPassword(this.admin_token, 1, "new_password");
    }

    @Test(expected = AccountOverrideException.class)
    public void updateAccountPassword_account_override_fail() throws Exception {
        doNothing().when(mock_administration_module).checkValidity(this.admin_token);
        when(mock_authentication_module.hasValidAccessRights(this.admin_token, SessionType.ADMIN)).thenReturn(true);
        doThrow(AccountOverrideException.class).when(mock_administration_module).updateAccountPassword(1, "new_password");
        adminSession.updateAccountPassword(this.admin_token, 1, "new_password");
    }

    @Test(expected = RuntimeException.class)
    public void updateAccountPassword_runtime_fail() throws Exception {
        doNothing().when(mock_administration_module).checkValidity(this.admin_token);
        when(mock_authentication_module.hasValidAccessRights(this.admin_token, SessionType.ADMIN)).thenReturn(true);
        doThrow(RuntimeException.class).when(mock_administration_module).updateAccountPassword(1, "new_password");
        adminSession.updateAccountPassword(this.admin_token, 1, "new_password");
    }

    @Test
    public void activateAccount() throws Exception {
        doNothing().when(mock_administration_module).checkValidity(this.admin_token);
        when(mock_authentication_module.hasValidAccessRights(this.admin_token, SessionType.ADMIN)).thenReturn(true);
        adminSession.activateAccount(this.admin_token, 1);
        verify(mock_administration_module, times(1)).activateAccount(1);
    }

    @Test(expected = SessionInvalidException.class)
    public void activateAccount_invalid_session_fail() throws Exception {
        doThrow(SessionInvalidException.class).when(mock_administration_module).checkValidity(this.admin_token);
        adminSession.activateAccount(this.admin_token, 1);
    }

    @Test(expected = SessionExpiredException.class)
    public void activateAccount_expired_session_fail() throws Exception {
        doThrow(SessionExpiredException.class).when(mock_administration_module).checkValidity(this.admin_token);
        adminSession.activateAccount(this.admin_token, 1);
    }

    @Test(expected = SessionCorruptedException.class)
    public void activateAccount_corrupted_session_fail() throws Exception {
        doThrow(SessionCorruptedException.class).when(mock_administration_module).checkValidity(this.admin_token);
        adminSession.activateAccount(this.admin_token, 1);
    }

    @Test(expected = AccountExistenceException.class)
    public void activateAccount_account_fetch_fail() throws Exception {
        doNothing().when(mock_administration_module).checkValidity(this.admin_token);
        when(mock_authentication_module.hasValidAccessRights(this.admin_token, SessionType.ADMIN)).thenReturn(true);
        doThrow(AccountExistenceException.class).when(mock_administration_module).activateAccount(1);
        adminSession.activateAccount(this.admin_token, 1);
    }

    @Test(expected = AccountOverrideException.class)
    public void activateAccount_override_fail() throws Exception {
        doNothing().when(mock_administration_module).checkValidity(this.admin_token);
        when(mock_authentication_module.hasValidAccessRights(this.admin_token, SessionType.ADMIN)).thenReturn(true);
        when(mock_administration_module.isSameAccount(this.admin_token, 1)).thenReturn(true);
        adminSession.activateAccount(this.admin_token, 1);
    }

    @Test(expected = RuntimeException.class)
    public void activateAccount_runtime_fail() throws Exception {
        doNothing().when(mock_administration_module).checkValidity(this.admin_token);
        when(mock_authentication_module.hasValidAccessRights(this.admin_token, SessionType.ADMIN)).thenReturn(true);
        doThrow(DbQueryException.class).when(mock_administration_module).activateAccount(1);
        adminSession.activateAccount(this.admin_token, 1);
    }

    @Test
    public void deactivateAccount() throws Exception {
        doNothing().when(mock_administration_module).checkValidity(this.admin_token);
        when(mock_authentication_module.hasValidAccessRights(this.admin_token, SessionType.ADMIN)).thenReturn(true);
        adminSession.deactivateAccount(this.admin_token, 1);
        verify(mock_administration_module, times(1)).deactivateAccount(1);
    }

    @Test(expected = SessionInvalidException.class)
    public void deactivateAccount_invalid_session_fail() throws Exception {
        doThrow(SessionInvalidException.class).when(mock_administration_module).checkValidity(this.admin_token);
        adminSession.deactivateAccount(this.admin_token, 1);
    }

    @Test(expected = SessionExpiredException.class)
    public void deactivateAccount_expired_session_fail() throws Exception {
        doThrow(SessionExpiredException.class).when(mock_administration_module).checkValidity(this.admin_token);
        adminSession.deactivateAccount(this.admin_token, 1);
    }

    @Test(expected = SessionCorruptedException.class)
    public void deactivateAccount_corrupted_session_fail() throws Exception {
        doThrow(SessionCorruptedException.class).when(mock_administration_module).checkValidity(this.admin_token);
        adminSession.deactivateAccount(this.admin_token, 1);
    }

    @Test(expected = AccountExistenceException.class)
    public void deactivateAccount_account_fetch_fail() throws Exception {
        doNothing().when(mock_administration_module).checkValidity(this.admin_token);
        when(mock_authentication_module.hasValidAccessRights(this.admin_token, SessionType.ADMIN)).thenReturn(true);
        doThrow(AccountExistenceException.class).when(mock_administration_module).deactivateAccount(1);
        adminSession.deactivateAccount(this.admin_token, 1);
    }

    @Test(expected = AccountOverrideException.class)
    public void deactivateAccount_override_fail() throws Exception {
        doNothing().when(mock_administration_module).checkValidity(this.admin_token);
        when(mock_authentication_module.hasValidAccessRights(this.admin_token, SessionType.ADMIN)).thenReturn(true);
        when(mock_administration_module.isSameAccount(this.admin_token, 1)).thenReturn(true);
        adminSession.deactivateAccount(this.admin_token, 1);
    }

    @Test(expected = RuntimeException.class)
    public void deactivateAccount_runtime_fail() throws Exception {
        doNothing().when(mock_administration_module).checkValidity(this.admin_token);
        when(mock_authentication_module.hasValidAccessRights(this.admin_token, SessionType.ADMIN)).thenReturn(true);
        doThrow(DbQueryException.class).when(mock_administration_module).deactivateAccount(1);
        adminSession.deactivateAccount(this.admin_token, 1);
    }

    @Test
    public void deleteAccount() throws Exception {
        doNothing().when(mock_administration_module).checkValidity(this.admin_token);
        when(mock_authentication_module.hasValidAccessRights(this.admin_token, SessionType.ADMIN)).thenReturn(true);
        adminSession.deleteAccount(this.admin_token, 1);
        verify(mock_administration_module, times(1)).deleteAccount(1);
    }

    @Test(expected = SessionInvalidException.class)
    public void deleteAccount_invalid_session_fail() throws Exception {
        doThrow(SessionInvalidException.class).when(mock_administration_module).checkValidity(this.admin_token);
        adminSession.deleteAccount(this.admin_token, 1);
    }

    @Test(expected = SessionExpiredException.class)
    public void deleteAccount_expired_session_fail() throws Exception {
        doThrow(SessionExpiredException.class).when(mock_administration_module).checkValidity(this.admin_token);
        adminSession.deleteAccount(this.admin_token, 1);
    }

    @Test(expected = SessionCorruptedException.class)
    public void deleteAccount_corrupted_session_fail() throws Exception {
        doThrow(SessionCorruptedException.class).when(mock_administration_module).checkValidity(this.admin_token);
        adminSession.deleteAccount(this.admin_token, 1);
    }

    @Test(expected = AccountExistenceException.class)
    public void deleteAccount_account_fetch_fail() throws Exception {
        doNothing().when(mock_administration_module).checkValidity(this.admin_token);
        when(mock_authentication_module.hasValidAccessRights(this.admin_token, SessionType.ADMIN)).thenReturn(true);
        doThrow(AccountExistenceException.class).when(mock_administration_module).deleteAccount(1);
        adminSession.deleteAccount(this.admin_token, 1);
    }

    @Test(expected = AccountOverrideException.class)
    public void deleteAccount_account_override_fail() throws Exception {
        doNothing().when(mock_administration_module).checkValidity(this.admin_token);
        when(mock_authentication_module.hasValidAccessRights(this.admin_token, SessionType.ADMIN)).thenReturn(true);
        when(mock_administration_module.isSameAccount(this.admin_token, 1)).thenReturn(true);
        adminSession.deleteAccount(this.admin_token, 1);
    }

    @Test(expected = RuntimeException.class)
    public void deleteAccount_runtime_fail() throws Exception {
        doNothing().when(mock_administration_module).checkValidity(this.admin_token);
        when(mock_authentication_module.hasValidAccessRights(this.admin_token, SessionType.ADMIN)).thenReturn(true);
        doThrow(DbQueryException.class).when(mock_administration_module).deleteAccount(1);
        adminSession.deleteAccount(this.admin_token, 1);
    }

    @Test
    public void getAccounts() throws Exception {
        ObjectTable account_table = mock(ObjectTable.class);
        HashMap<String, Object> row = new HashMap<>();
        row.put("id", 10);
        row.put("username", "jsmith01");
        row.put("created", "2010-01-03 15:02:54");
        row.put("last_login", "2018-02-25 09:05:21");
        row.put("last_pwd_change", "2012-11-12 17:01:54");
        row.put("type_id", 1);
        row.put("description", "ADMIN");
        row.put("active_state", 1);
        when(account_table.isEmpty()).thenReturn(false);
        when(account_table.rowCount()).thenReturn(1);
        when(account_table.getRow(1)).thenReturn(row);
        doNothing().when(mock_administration_module).checkValidity(this.admin_token);
        when(mock_authentication_module.hasValidAccessRights(this.admin_token, SessionType.ADMIN)).thenReturn(true);
        when(mock_administration_module.getAccounts()).thenReturn(account_table);
        //Testing
        List<Account> accounts = this.adminSession.getAccounts(this.admin_token);
        Assert.assertEquals(1, accounts.size());
        Account account = accounts.get(0);
        Assert.assertEquals(new Integer(10), account.id());
        Assert.assertEquals("jsmith01", account.username());
        Assert.assertEquals(TimestampConverter.getDateObject("2010-01-03 15:02:54"), account.created());
        Assert.assertEquals(TimestampConverter.getDateObject("2018-02-25 09:05:21"), account.lastLogin());
        Assert.assertEquals(TimestampConverter.getDateObject("2012-11-12 17:01:54"), account.lastPwdChange());
        Assert.assertEquals(new AccountType(1, "ADMIN"), account.type());
        Assert.assertTrue(account.isActive());
        verify(mock_administration_module, times(1)).getAccounts();
    }

    @Test(expected = SessionInvalidException.class)
    public void getAccounts_invalid_session_fail() throws Exception {
        doThrow(SessionInvalidException.class).when(mock_administration_module).checkValidity(this.admin_token);
        adminSession.getAccounts(this.admin_token);
    }

    @Test(expected = SessionExpiredException.class)
    public void getAccounts_expired_session_fail() throws Exception {
        doThrow(SessionExpiredException.class).when(mock_administration_module).checkValidity(this.admin_token);
        adminSession.getAccounts(this.admin_token);
    }

    @Test(expected = SessionCorruptedException.class)
    public void getAccounts_corrupted_session_fail() throws Exception {
        doThrow(SessionCorruptedException.class).when(mock_administration_module).checkValidity(this.admin_token);
        adminSession.getAccounts(this.admin_token);
    }

    @Test(expected = RuntimeException.class)
    public void getAccounts_runtime_fail() throws Exception {
        doThrow(DbQueryException.class).when(mock_administration_module).checkValidity(this.admin_token);
        adminSession.getAccounts(this.admin_token);
    }

    @Test
    public void getAccount_by_id() throws Exception {
        ObjectTable account_table = mock(ObjectTable.class);
        HashMap<String, Object> row = new HashMap<>();
        row.put("id", 10);
        row.put("username", "jsmith01");
        row.put("created", "2010-01-03 15:02:54");
        row.put("last_login", "2018-02-25 09:05:21");
        row.put("last_pwd_change", "2012-11-12 17:01:54");
        row.put("type_id", 1);
        row.put("description", "ADMIN");
        row.put("active_state", 1);
        when(account_table.isEmpty()).thenReturn(false);
        when(account_table.getRow(1)).thenReturn(row);
        doNothing().when(mock_administration_module).checkValidity(this.admin_token);
        when(mock_authentication_module.hasValidAccessRights(this.admin_token, SessionType.ADMIN)).thenReturn(true);
        when(mock_administration_module.getAccount(10)).thenReturn(account_table);
        //Testing
        Account account = this.adminSession.getAccount(this.admin_token, 10);
        Assert.assertEquals(new Integer(10), account.id());
        Assert.assertEquals("jsmith01", account.username());
        Assert.assertEquals(TimestampConverter.getDateObject("2010-01-03 15:02:54"), account.created());
        Assert.assertEquals(TimestampConverter.getDateObject("2018-02-25 09:05:21"), account.lastLogin());
        Assert.assertEquals(TimestampConverter.getDateObject("2012-11-12 17:01:54"), account.lastPwdChange());
        Assert.assertEquals(new AccountType(1, "ADMIN"), account.type());
        Assert.assertTrue(account.isActive());
        verify(mock_administration_module, times(1)).getAccount(10);
    }

    @Test(expected = SessionInvalidException.class)
    public void getAccount_by_id_invalid_session_fail() throws Exception {
        doThrow(SessionInvalidException.class).when(mock_administration_module).checkValidity(this.admin_token);
        adminSession.getAccount(this.admin_token, 1);
    }

    @Test(expected = SessionExpiredException.class)
    public void getAccount_by_id_expired_session_fail() throws Exception {
        doThrow(SessionExpiredException.class).when(mock_administration_module).checkValidity(this.admin_token);
        adminSession.getAccount(this.admin_token, 1);
    }

    @Test(expected = SessionCorruptedException.class)
    public void getAccount_by_id_corrupted_session_fail() throws Exception {
        doThrow(SessionCorruptedException.class).when(mock_administration_module).checkValidity(this.admin_token);
        adminSession.getAccount(this.admin_token, 1);
    }

    @Test(expected = RuntimeException.class)
    public void getAccount_by_id_runtime_fail() throws Exception {
        doThrow(DbQueryException.class).when(mock_administration_module).checkValidity(this.admin_token);
        adminSession.getAccount(this.admin_token, 1);
    }

    @Test
    public void getAccount_by_username() throws Exception {
        ObjectTable account_table = mock(ObjectTable.class);
        HashMap<String, Object> row = new HashMap<>();
        row.put("id", 10);
        row.put("username", "jsmith01");
        row.put("created", "2010-01-03 15:02:54");
        row.put("last_login", "2018-02-25 09:05:21");
        row.put("last_pwd_change", "2012-11-12 17:01:54");
        row.put("type_id", 1);
        row.put("description", "ADMIN");
        row.put("active_state", 1);
        when(account_table.isEmpty()).thenReturn(false);
        when(account_table.getRow(1)).thenReturn(row);
        doNothing().when(mock_administration_module).checkValidity(this.admin_token);
        when(mock_authentication_module.hasValidAccessRights(this.admin_token, SessionType.ADMIN)).thenReturn(true);
        when(mock_administration_module.getAccount("00001")).thenReturn(account_table);
        //Testing
        Account account = this.adminSession.getAccount(this.admin_token, "00001");
        Assert.assertEquals(new Integer(10), account.id());
        Assert.assertEquals("jsmith01", account.username());
        Assert.assertEquals(TimestampConverter.getDateObject("2010-01-03 15:02:54"), account.created());
        Assert.assertEquals(TimestampConverter.getDateObject("2018-02-25 09:05:21"), account.lastLogin());
        Assert.assertEquals(TimestampConverter.getDateObject("2012-11-12 17:01:54"), account.lastPwdChange());
        Assert.assertEquals(new AccountType(1, "ADMIN"), account.type());
        Assert.assertTrue(account.isActive());
        verify(mock_administration_module, times(1)).getAccount("00001");
    }

    @Test(expected = SessionInvalidException.class)
    public void getAccount_by_username_invalid_session_fail() throws Exception {
        doThrow(SessionInvalidException.class).when(mock_administration_module).checkValidity(this.admin_token);
        adminSession.getAccount(this.admin_token, "00001");
    }

    @Test(expected = SessionExpiredException.class)
    public void getAccount_by_username_expired_session_fail() throws Exception {
        doThrow(SessionExpiredException.class).when(mock_administration_module).checkValidity(this.admin_token);
        adminSession.getAccount(this.admin_token, "00001");
    }

    @Test(expected = SessionCorruptedException.class)
    public void getAccount_by_username_corrupted_session_fail() throws Exception {
        doThrow(SessionCorruptedException.class).when(mock_administration_module).checkValidity(this.admin_token);
        adminSession.getAccount(this.admin_token, "00001");
    }

    @Test(expected = RuntimeException.class)
    public void getAccount_by_username_runtime_fail() throws Exception {
        doThrow(DbQueryException.class).when(mock_administration_module).checkValidity(this.admin_token);
        adminSession.getAccount(this.admin_token, "00001");
    }

    @Test
    public void optimiseReservationDatabase() throws Exception {
        doNothing().when(mock_administration_module).checkValidity(this.admin_token);
        when(mock_authentication_module.hasValidAccessRights(this.admin_token, SessionType.ADMIN)).thenReturn(true);
        adminSession.optimiseReservationDatabase(this.admin_token);
        verify(mock_maintenance_module, times(1)).vacuumDatabase(this.admin_token);
    }

    @Test(expected = SessionInvalidException.class)
    public void optimiseReservationDatabase_invalid_session_fail() throws Exception {
        doThrow(SessionInvalidException.class).when(mock_administration_module).checkValidity(this.admin_token);
        adminSession.optimiseReservationDatabase(this.admin_token);
    }

    @Test(expected = SessionExpiredException.class)
    public void optimiseReservationDatabase_expired_session_fail() throws Exception {
        doThrow(SessionExpiredException.class).when(mock_administration_module).checkValidity(this.admin_token);
        adminSession.optimiseReservationDatabase(this.admin_token);
    }

    @Test(expected = SessionCorruptedException.class)
    public void optimiseReservationDatabase_corrupted_session_fail() throws Exception {
        doThrow(SessionCorruptedException.class).when(mock_administration_module).checkValidity(this.admin_token);
        adminSession.optimiseReservationDatabase(this.admin_token);
    }

    @Test
    public void optimiseUserAccountDatabase() throws Exception {
        doNothing().when(mock_administration_module).checkValidity(this.admin_token);
        when(mock_authentication_module.hasValidAccessRights(this.admin_token, SessionType.ADMIN)).thenReturn(true);
        adminSession.optimiseUserAccountDatabase(this.admin_token);
        verify(mock_administration_module, times(1)).optimiseDatabase();
    }

    @Test(expected = SessionInvalidException.class)
    public void optimiseUserAccountDatabase_invalid_session_fail() throws Exception {
        doThrow(SessionInvalidException.class).when(mock_administration_module).checkValidity(this.admin_token);
        adminSession.optimiseUserAccountDatabase(this.admin_token);
    }

    @Test(expected = SessionExpiredException.class)
    public void optimiseUserAccountDatabase_expired_session_fail() throws Exception {
        doThrow(SessionExpiredException.class).when(mock_administration_module).checkValidity(this.admin_token);
        adminSession.optimiseUserAccountDatabase(this.admin_token);
    }

    @Test(expected = SessionCorruptedException.class)
    public void optimiseUserAccountDatabase_corrupted_session_fail() throws Exception {
        doThrow(SessionCorruptedException.class).when(mock_administration_module).checkValidity(this.admin_token);
        adminSession.optimiseUserAccountDatabase(this.admin_token);
    }

    @Test
    public void getBuildings() throws Exception {
        List<Building> buildings = this.realAdminSession.getBuildings(this.admin_token);
        Assert.assertEquals(1, buildings.size());
        Assert.assertEquals(new Integer(1), buildings.get(0).id());
        Assert.assertEquals("Test Building", buildings.get(0).name());
    }

    @Test
    public void getFloors() throws Exception {
        Building building = mock(Building.class);
        when(building.id()).thenReturn(1);
        List<Floor> floors = this.realAdminSession.getFloors(this.admin_token, building);
//        for(Floor floor : floors)
//            System.out.println(floor);
        Assert.assertEquals(3, floors.size());
        Assert.assertEquals("Ground level", floors.get(0).description());
        Assert.assertEquals("First floor", floors.get(1).description());
        Assert.assertEquals("Second floor", floors.get(2).description());
    }

    @Test
    public void getRooms() throws Exception {
        Floor floor = mock(Floor.class);
        when(floor.floorID()).thenReturn(1);
        when(floor.buildingID()).thenReturn(1);
        List<Room> rooms = this.realAdminSession.getRooms(this.admin_token, floor);
//        for (Room room : rooms)
//            System.out.println(room);
        Assert.assertEquals(3, rooms.size());
        Assert.assertEquals(new Integer(1), rooms.get(0).id());
        Assert.assertEquals(new Integer(2), rooms.get(1).id());
        Assert.assertEquals(new Integer(3), rooms.get(2).id());
    }

    @Test
    public void getRoomPrices() throws Exception {
        Room room = new Room(1, 1, 1, 1, "Small room 1");
        List<Usage<RoomPrice, Integer>> list = this.realAdminSession.getRoomPrices(this.admin_token, room);
//        for (Usage u : list)
//            System.out.println(u);
        Assert.assertEquals(2, list.size());
        Assert.assertEquals(new Integer(1), list.get(0).dto().id());
        Assert.assertTrue(list.get(0).usage().isEmpty());
        Assert.assertEquals(new Integer(7), list.get(1).dto().id());
        Assert.assertEquals(4, list.get(1).usage().size());
    }

    @Test
    public void getRoomCategories() throws Exception {
        List<Usage<RoomCategory, Room>> list = this.realAdminSession.getRoomCategories(this.admin_token);
//        for (Usage u : list)
//            System.out.println(u);
        Assert.assertEquals(7, list.size());
        Assert.assertEquals(new Integer(1), list.get(0).dto().id());
        Assert.assertEquals(2, list.get(0).usage().size());
        Assert.assertEquals(new Integer(6), list.get(5).dto().id());
        Assert.assertEquals(1, list.get(5).usage().size());
    }

    @Test
    public void getRoomDetails() throws Exception {
        Room room = mock(Room.class);
        when(room.id()).thenReturn(1);
        when(room.floorID()).thenReturn(1);
        when(room.buildingID()).thenReturn(1);
        DetailedRoom detailedRoom = this.realAdminSession.getRoomDetails(this.admin_token, room);
        System.out.println(detailedRoom);
        Assert.assertNotNull(detailedRoom.room());
        Assert.assertNotNull(detailedRoom.building());
        Assert.assertNotNull(detailedRoom.floor());
        Assert.assertNotNull(detailedRoom.category());
        Assert.assertNotNull(detailedRoom.fixtures());
        Assert.assertEquals(new Integer(1), detailedRoom.room().id());
        Assert.assertEquals(new Integer(1), detailedRoom.room().floorID());
        Assert.assertEquals(new Integer(1), detailedRoom.room().buildingID());
    }

    @Test
    public void addBuilding() throws Exception {
        Building building = new Building(
                "The Shard",
                "32 London Bridge Street",
                "",
                "SE1 9SG",
                "London",
                "UK",
                "+44 1230 456 789"
        );
        this.realAdminSession.add(this.admin_token, building);
        List<Building> buildings = this.realAdminSession.getBuildings(this.admin_token);
        Assert.assertEquals(2, buildings.size());
        Assert.assertEquals(new Integer(2), buildings.get(1).id());
        Assert.assertEquals("The Shard", buildings.get(1).name());
    }

    @Test
    public void addFloor() throws Exception {
        Building building = mock(Building.class);
        when(building.id()).thenReturn(1);
        Floor floor = new Floor(
                1, 4, "4th floor"
        );
        this.realAdminSession.add(admin_token, floor);
        List<Floor> floors = this.realAdminSession.getFloors(this.admin_token, building);
        Assert.assertEquals(4, floors.size());
        Assert.assertEquals(new Integer(4), floors.get(3).floorID());
        Assert.assertEquals("4th floor", floors.get(3).description());
    }

    @Test
    public void addRoomWithRoomFixture() throws Exception {
        Room room = new Room(666, 3, 1, 1, "Doom room");
        RoomFixtures fixtures = new RoomFixtures(-1, true, true, true, true);
        this.realAdminSession.add(this.admin_token, room, fixtures);
        Floor floor = mock(Floor.class);
        when(floor.buildingID()).thenReturn(1);
        when(floor.floorID()).thenReturn(3);
        List<Room> rooms = this.realAdminSession.getRooms(this.admin_token, floor);
        Assert.assertEquals(3, rooms.size());
        Assert.assertEquals(new Integer(666), rooms.get(2).id());
        Assert.assertEquals(new Integer(1), rooms.get(2).category());
        Assert.assertEquals("Doom room", rooms.get(2).description());
        DetailedRoom detailedRoom = this.realAdminSession.getRoomDetails(this.admin_token, rooms.get(2));
        Assert.assertTrue(detailedRoom.fixtures().hasFixedChairs());
        Assert.assertTrue(detailedRoom.fixtures().hasCateringSpace());
        Assert.assertTrue(detailedRoom.fixtures().hasWhiteboard());
        Assert.assertTrue(detailedRoom.fixtures().hasProjector());
    }

    @Test
    public void addRoomPrice() throws Exception {
        RoomPrice mock_roomPrice = mock(RoomPrice.class);
        //Already existing price/year combo
        when(mock_roomPrice.price()).thenReturn(100.);
        when(mock_roomPrice.year()).thenReturn(2007);
        RoomPrice roomPrice1 = this.realAdminSession.add(this.admin_token, mock_roomPrice);
        Assert.assertEquals(new Integer(6), roomPrice1.id());
        //Non-existing price/year combo
        when(mock_roomPrice.price()).thenReturn(100.);
        when(mock_roomPrice.year()).thenReturn(2015);
        RoomPrice roomPrice2 = this.realAdminSession.add(this.admin_token, mock_roomPrice);
        Assert.assertEquals(new Integer(13), roomPrice2.id());
    }

    @Test
    public void addRoomCategory() throws Exception {
        RoomCategory mock_roomCategory = mock(RoomCategory.class);
        //Already existing capacity/dimension
        when(mock_roomCategory.capacity()).thenReturn(10);
        when(mock_roomCategory.dimension()).thenReturn(10);
        RoomCategory roomCategory1 = this.realAdminSession.add(this.admin_token, mock_roomCategory);
        Assert.assertEquals(new Integer(1), roomCategory1.id());
        //Non-existing capacity/dimension
        when(mock_roomCategory.capacity()).thenReturn(10);
        when(mock_roomCategory.dimension()).thenReturn(20);
        RoomCategory roomCategory2 = this.realAdminSession.add(this.admin_token, mock_roomCategory);
        Assert.assertEquals(new Integer(8), roomCategory2.id());
    }

    @Test
    public void updateBuilding() throws Exception {
        Building building = new Building(
                1,
                "The Shard",
                "32 London Bridge Street",
                "-",
                "SE1 9SG",
                "London",
                "UK",
                "+44 1230 456 789"
        );
        this.realAdminSession.update(this.admin_token, building);
        List<Building> buildings = this.realAdminSession.getBuildings(this.admin_token);
        Assert.assertEquals(1, buildings.size());
        Assert.assertEquals(new Integer(1), buildings.get(0).id());
        Assert.assertEquals("The Shard", buildings.get(0).name());
        Assert.assertEquals("32 London Bridge Street", buildings.get(0).address1());
        Assert.assertEquals("-", buildings.get(0).address2());
        Assert.assertEquals("London", buildings.get(0).city());
        Assert.assertEquals("SE1 9SG", buildings.get(0).postcode());
        Assert.assertEquals("UK", buildings.get(0).country());
        Assert.assertEquals("+44 1230 456 789", buildings.get(0).phone());
    }

    @Test
    public void updateFloor() throws Exception {
        Floor floor = new Floor(1, 1, "Updated description");
        this.realAdminSession.update(this.admin_token, floor);
        Building mock_building = mock(Building.class);
        when(mock_building.id()).thenReturn(1);
        List<Floor> floors = this.realAdminSession.getFloors(this.admin_token, mock_building);
        Assert.assertEquals(3, floors.size());
        Assert.assertEquals(new Integer(1), floors.get(0).floorID());
        Assert.assertEquals(new Integer(1), floors.get(0).buildingID());
        Assert.assertEquals("Updated description", floors.get(0).description());
    }

    @Test
    public void updateRoom() throws Exception {
        Room room = new Room(1, 1, 1, 1, "Updated description");
        this.realAdminSession.update(this.admin_token, room);
        Floor mock_floor = mock(Floor.class);
        when(mock_floor.buildingID()).thenReturn(1);
        when(mock_floor.floorID()).thenReturn(1);
        List<Room> rooms = this.realAdminSession.getRooms(this.admin_token, mock_floor);
        Assert.assertEquals(3, rooms.size());
        Assert.assertEquals(new Integer(1), rooms.get(0).id());
        Assert.assertEquals(new Integer(1), rooms.get(0).floorID());
        Assert.assertEquals(new Integer(1), rooms.get(0).buildingID());
        Assert.assertEquals(new Integer(1), rooms.get(0).category());
        Assert.assertEquals("Updated description", rooms.get(0).description());
    }

    @Test
    public void removeBuilding() throws Exception {
        Assert.assertEquals(1, this.realAdminSession.getBuildings(this.admin_token).size());
        Building building = new Building(
                2,
                "The Shard",
                "32 London Bridge Street",
                "-",
                "SE1 9SG",
                "London",
                "UK",
                "+44 1230 456 789"
        );
        this.realAdminSession.add(this.admin_token, building);
        Assert.assertEquals("Shard building not created", 2, this.realAdminSession.getBuildings(this.admin_token).size());
        this.realAdminSession.remove(this.admin_token, building);
        List<Building> buildings = this.realAdminSession.getBuildings(this.admin_token);
        Assert.assertEquals(1, buildings.size());
        Assert.assertEquals("Test Building", buildings.get(0).name());
    }

    @Test
    public void removeFloor() throws Exception {
        Building building = mock(Building.class);
        when(building.id()).thenReturn(1);
        Floor floor = new Floor(1, 10, "Tester floor");
        Assert.assertEquals(3, this.realAdminSession.getFloors(this.admin_token, building).size());
        this.realAdminSession.add(this.admin_token, floor);
        Assert.assertEquals(4, this.realAdminSession.getFloors(this.admin_token, building).size());
        Assert.assertTrue(this.realAdminSession.remove(this.admin_token, floor));
        Assert.assertEquals(3, this.realAdminSession.getFloors(this.admin_token, building).size());
    }

    @Test
    public void removeRoom() throws Exception {
        Floor floor = mock(Floor.class);
        when(floor.buildingID()).thenReturn(1);
        when(floor.floorID()).thenReturn(1);
        Room mock_room = mock(Room.class);
        when(mock_room.buildingID()).thenReturn(1);
        when(mock_room.floorID()).thenReturn(1);
        when(mock_room.id()).thenReturn(2);
        Assert.assertEquals(3, this.realAdminSession.getRooms(this.admin_token, floor).size());
        Assert.assertTrue(this.realAdminSession.remove(this.admin_token, mock_room));
        Assert.assertEquals(2, this.realAdminSession.getRooms(this.admin_token, floor).size());
    }

    @Test
    public void removeRoomPrice() throws Exception {
        RoomPrice mock_roomPrice = mock(RoomPrice.class);
        //Used RoomPriceUsage
        when(mock_roomPrice.id()).thenReturn(7);
        Assert.assertFalse(this.realAdminSession.remove(this.admin_token, mock_roomPrice));
        //Unused RoomPriceUsage
        when(mock_roomPrice.id()).thenReturn(1);
        Assert.assertTrue(this.realAdminSession.remove(this.admin_token, mock_roomPrice));
    }

    @Test
    public void removeRoomCategory() throws Exception {
        RoomCategory mock_category = mock(RoomCategory.class);
        when(mock_category.id()).thenReturn(7);
        Assert.assertEquals(7, this.realAdminSession.getRoomCategories(this.admin_token).size());
        this.realAdminSession.remove(this.admin_token, mock_category);
        Assert.assertEquals(6, this.realAdminSession.getRoomCategories(this.admin_token).size());
    }

    @Test
    public void getMemberships() throws Exception {
        List<Usage<Membership, Integer>> memberships = this.realAdminSession.getMemberships(this.admin_token);
//        for (Usage membership : memberships)
//            System.out.println(membership);
        Assert.assertEquals(3, memberships.size());
        Assert.assertEquals(new Integer(1), memberships.get(0).dto().id());
        Assert.assertEquals(3, memberships.get(0).usage().size());
        Assert.assertEquals(new Integer(2), memberships.get(1).dto().id());
        Assert.assertEquals(1, memberships.get(1).usage().size());
        Assert.assertEquals(new Integer(3), memberships.get(2).dto().id());
        Assert.assertEquals(1, memberships.get(2).usage().size());
    }

    @Test
    public void getDiscounts() throws Exception {
        List<Discount> discounts = this.realAdminSession.getDiscounts(this.admin_token);
//        for (Discount discount : discounts)
//            System.out.println(discount);
        Assert.assertEquals(3, discounts.size());
        Assert.assertEquals(new Double(0.), discounts.get(0).rate());
        Assert.assertEquals(new Double(25.), discounts.get(1).rate());
        Assert.assertEquals(new Double(10.), discounts.get(2).rate());
    }

    @Test
    public void addMembership() throws Exception {
        Assert.assertTrue(true);
        //method is just a plain forward from MembershipAdministration. See test from there.
    }

    @Test
    public void removeMembership() throws Exception {
        Assert.assertTrue(true);
        //method is just a plain forward from MembershipAdministration. See test from there.
    }
}