package bbk_beam.mtRooms.admin.administration;

import bbk_beam.mtRooms.admin.authentication.IAuthenticationSystem;
import bbk_beam.mtRooms.admin.authentication.Token;
import bbk_beam.mtRooms.admin.exception.AccountExistenceException;
import bbk_beam.mtRooms.admin.exception.AccountOverrideException;
import bbk_beam.mtRooms.admin.exception.RecordUpdateException;
import bbk_beam.mtRooms.db.exception.DbQueryException;
import bbk_beam.mtRooms.db.exception.SessionCorruptedException;
import bbk_beam.mtRooms.db.exception.SessionExpiredException;
import bbk_beam.mtRooms.db.exception.SessionInvalidException;
import bbk_beam.mtRooms.db.session.SessionType;
import eadjlib.datastructure.ObjectTable;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import static org.mockito.Mockito.*;

public class AdminSessionTest {
    private UserAccAdministration mock_administration_module;
    private ReservationDbMaintenance mock_maintenance_module;
    private IAuthenticationSystem mock_authentication_module;
    private AdminSession adminSession;
    private Date session_expiry;
    private Token admin_token;

    @Before
    public void setUp() {
        mock_administration_module = mock(UserAccAdministration.class);
        mock_maintenance_module = mock(ReservationDbMaintenance.class);
        mock_authentication_module = mock(IAuthenticationSystem.class);
        adminSession = new AdminSession(mock_administration_module, mock_maintenance_module, mock_authentication_module);
        session_expiry = Date.from(Instant.now().plus(1, ChronoUnit.DAYS));
        admin_token = new Token("00001", new Date(), session_expiry);
    }

    @After
    public void tearDown() {
        mock_administration_module = null;
        mock_maintenance_module = null;
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
        doThrow(RecordUpdateException.class).when(mock_administration_module).createNewAccount(SessionType.USER, "user001", "password");
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
        doNothing().when(mock_administration_module).checkValidity(this.admin_token);
        when(mock_authentication_module.hasValidAccessRights(this.admin_token, SessionType.ADMIN)).thenReturn(true);
        when(mock_administration_module.getAccounts()).thenReturn(account_table);
        Assert.assertEquals(account_table, this.adminSession.getAccounts(this.admin_token));
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
        doNothing().when(mock_administration_module).checkValidity(this.admin_token);
        when(mock_authentication_module.hasValidAccessRights(this.admin_token, SessionType.ADMIN)).thenReturn(true);
        when(mock_administration_module.getAccount(1)).thenReturn(account_table);
        Assert.assertEquals(account_table, this.adminSession.getAccount(this.admin_token, 1));
        verify(mock_administration_module, times(1)).getAccount(1);
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
        doNothing().when(mock_administration_module).checkValidity(this.admin_token);
        when(mock_authentication_module.hasValidAccessRights(this.admin_token, SessionType.ADMIN)).thenReturn(true);
        when(mock_administration_module.getAccount("00001")).thenReturn(account_table);
        Assert.assertEquals(account_table, this.adminSession.getAccount(this.admin_token, "00001"));
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
}