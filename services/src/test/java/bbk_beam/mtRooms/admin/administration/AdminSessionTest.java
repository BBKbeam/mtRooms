package bbk_beam.mtRooms.admin.administration;

import bbk_beam.mtRooms.admin.authentication.IAuthenticationSystem;
import bbk_beam.mtRooms.admin.authentication.Token;
import bbk_beam.mtRooms.admin.exception.AccountExistenceException;
import bbk_beam.mtRooms.admin.exception.RecordUpdateException;
import bbk_beam.mtRooms.db.exception.SessionCorruptedException;
import bbk_beam.mtRooms.db.exception.SessionExpiredException;
import bbk_beam.mtRooms.db.exception.SessionInvalidException;
import bbk_beam.mtRooms.db.session.SessionType;
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
    public void setUp() throws Exception {
        mock_administration_module = mock(UserAccAdministration.class);
        mock_maintenance_module = mock(ReservationDbMaintenance.class);
        mock_authentication_module = mock(IAuthenticationSystem.class);
        adminSession = new AdminSession(mock_administration_module, mock_maintenance_module, mock_authentication_module);
        session_expiry = Date.from(Instant.now().plus(1, ChronoUnit.DAYS));
        admin_token = new Token("00001", new Date(), session_expiry);
    }

    @After
    public void tearDown() throws Exception {
        mock_administration_module = null;
        mock_maintenance_module = null;
        mock_authentication_module = null;
        adminSession = null;
        session_expiry = null;
        admin_token = null;
    }

    @Test
    public void createNewAccount() throws Exception {
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
        when(mock_authentication_module.hasValidAccessRights(admin_token, SessionType.ADMIN)).thenReturn(true);
        doThrow(AccountExistenceException.class).when(mock_administration_module).createNewAccount(SessionType.USER, "user001", "password");
        adminSession.createNewAccount(this.admin_token, SessionType.USER, "user001", "password");
    }

    @Test(expected = RuntimeException.class)
    public void createNewAccount_runtime_fail() throws Exception {
        when(mock_authentication_module.hasValidAccessRights(admin_token, SessionType.ADMIN)).thenReturn(true);
        doThrow(RecordUpdateException.class).when(mock_administration_module).createNewAccount(SessionType.USER, "user001", "password");
        adminSession.createNewAccount(this.admin_token, SessionType.USER, "user001", "password");
    }

    @Test
    public void updateAccountPassword() throws Exception {
        //TODO
        Assert.assertTrue(false);
    }

    @Test
    public void activateAccount() throws Exception {
        //TODO
        Assert.assertTrue(false);
    }

    @Test
    public void deactivateAccount() throws Exception {
        //TODO
        Assert.assertTrue(false);
    }

    @Test
    public void deleteAccount() throws Exception {
        //TODO
        Assert.assertTrue(false);
    }

    @Test
    public void getAccounts() throws Exception {
        //TODO
        Assert.assertTrue(false);
    }

    @Test
    public void getAccount_by_id() throws Exception {
        //TODO
        Assert.assertTrue(false);
    }

    @Test
    public void getAccount_by_username() throws Exception {
        //TODO
        Assert.assertTrue(false);
    }

    @Test
    public void optimiseReservationDatabase() throws Exception {
        //TODO
        Assert.assertTrue(false);
    }

    @Test
    public void optimiseUserAccountDatabase() throws Exception {
        //TODO
        Assert.assertTrue(false);
    }

}