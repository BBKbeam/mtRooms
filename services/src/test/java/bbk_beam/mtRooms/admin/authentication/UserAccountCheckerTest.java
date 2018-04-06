package bbk_beam.mtRooms.admin.authentication;

import bbk_beam.mtRooms.admin.exception.AuthenticationFailureException;
import bbk_beam.mtRooms.db.IUserAccDbAccess;
import bbk_beam.mtRooms.db.TimestampConverter;
import bbk_beam.mtRooms.db.UserAccDbAccess;
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
import org.mockito.ArgumentCaptor;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class UserAccountCheckerTest {
    private IUserAccDbAccess mocked_user_access;
    private Token mocked_token;

    @Before
    public void setUp() {
        mocked_user_access = mock(UserAccDbAccess.class);
        mocked_token = mock(Token.class);
    }

    @After
    public void tearDown() {
        mocked_user_access = null;
        mocked_token = null;
    }

    @Test
    public void login() throws Exception {
        ObjectTable mocked_table = mock(ObjectTable.class);
        UserAccountChecker accountChecker = new UserAccountChecker(mocked_user_access);
        HashMap<String, Object> account_row = new HashMap<>();
        //Account info required for login
        String salt = PasswordHash.createSalt();
        String hash = PasswordHash.createHash("password", salt);
        account_row.put("id", 1);
        account_row.put("pwd_hash", hash);
        account_row.put("pwd_salt", salt);
        account_row.put("active_state", 1);
        account_row.put("description", "USER");
        //Mock for inner dependency calls
        when(mocked_user_access.pullFromDB(any(String.class))).thenReturn(mocked_table);
        when(mocked_table.getRow(1)).thenReturn(account_row);
        //Argument capture for pushing SQL update with last login timestamp
        ArgumentCaptor<String> argument = ArgumentCaptor.forClass(String.class);
        when(mocked_user_access.pushToDB(any(String.class))).thenReturn(true);
        //Login
        Token token = accountChecker.login("username", "password");
        verify(mocked_user_access).pushToDB(argument.capture());
        //Checking token expiry set to 12 hours after its creation
        Assert.assertEquals(token.getExpiry(), Date.from(token.getCreated().toInstant().plus(12, ChronoUnit.HOURS)));
        //Checking last_login update set to db records is the creation timestamp of the token
        Assert.assertEquals("UPDATE UserAccount SET last_login = \"" + TimestampConverter.getUTCTimestampString(token.getCreated()) + "\" WHERE username = \"username\"", argument.getValue());
    }

    @Test(expected = AuthenticationFailureException.class)
    public void login_bad_pwd_hash() throws Exception {
        ObjectTable mocked_table = mock(ObjectTable.class);
        UserAccountChecker accountChecker = new UserAccountChecker(mocked_user_access);
        HashMap<String, Object> account_row = new HashMap<>();
        //Account info required for login
        String salt = PasswordHash.createSalt();
        String hash = "BADHASH";
        account_row.put("id", 1);
        account_row.put("pwd_hash", hash);
        account_row.put("pwd_salt", salt);
        account_row.put("active_state", 1);
        account_row.put("description", "USER");
        //Mock for inner dependency calls
        when(mocked_user_access.pullFromDB(any(String.class))).thenReturn(mocked_table);
        when(mocked_table.getRow(1)).thenReturn(account_row);
        //Login
        Token token = accountChecker.login("username", "password");
    }

    @Test(expected = AuthenticationFailureException.class)
    public void login_inactive_account() throws Exception {
        ObjectTable mocked_table = mock(ObjectTable.class);
        UserAccountChecker accountChecker = new UserAccountChecker(mocked_user_access);
        HashMap<String, Object> account_row = new HashMap<>();
        //Account info required for login
        String salt = PasswordHash.createSalt();
        String hash = PasswordHash.createHash("password", salt);
        account_row.put("id", 1);
        account_row.put("pwd_hash", hash);
        account_row.put("pwd_salt", salt);
        account_row.put("active_state", 0);
        account_row.put("description", "USER");
        //Mock for inner dependency calls
        when(mocked_user_access.pullFromDB(any(String.class))).thenReturn(mocked_table);
        when(mocked_table.getRow(1)).thenReturn(account_row);
        //Login
        Token token = accountChecker.login("username", "password");
    }

    @Test(expected = AuthenticationFailureException.class)
    public void login_bad_sql() throws Exception {
        ObjectTable mocked_table = mock(ObjectTable.class);
        UserAccountChecker accountChecker = new UserAccountChecker(mocked_user_access);
        HashMap<String, Object> account_row = new HashMap<>();
        //Account info required for login
        String salt = PasswordHash.createSalt();
        String hash = PasswordHash.createHash("password", salt);
        account_row.put("id", 1);
        account_row.put("pwd_hash", hash);
        account_row.put("pwd_salt", salt);
        account_row.put("active_state", 1);
        account_row.put("description", "USER");
        //Mock for inner dependency calls
        when(mocked_user_access.pullFromDB(any(String.class))).thenThrow(new DbQueryException(""));
        //Login
        Token token = accountChecker.login("username", "password");
    }

    @Test(expected = AuthenticationFailureException.class)
    public void login_missing_account_info() throws Exception {
        ObjectTable table = new ObjectTable("id", "pwd_hash", "pwd_salt");
        UserAccountChecker accountChecker = new UserAccountChecker(mocked_user_access);
        //Mock for inner dependency calls
        when(mocked_user_access.pullFromDB(any(String.class))).thenReturn(table);
        //Login
        Token token = accountChecker.login("username", "password");
    }

    @Test
    public void logout() throws Exception {
        UserAccountChecker accountChecker = new UserAccountChecker(mocked_user_access);
        when(mocked_token.getSessionId()).thenReturn("0001");
        when(mocked_token.getExpiry()).thenReturn(Date.from(Instant.now().minus(1, ChronoUnit.HOURS)));
        accountChecker.logout(mocked_token);
        verify(mocked_user_access, times(1)).closeSession("0001");
    }

    @Test(expected = SessionInvalidException.class)
    public void logout_bad_session() throws Exception {
        UserAccountChecker accountChecker = new UserAccountChecker(mocked_user_access);
        doThrow(new SessionInvalidException("")).when(mocked_user_access).closeSession(any(String.class));
        doReturn(Date.from(Instant.EPOCH)).when(mocked_token).getExpiry();
        accountChecker.logout(mocked_token);
    }

    @Test
    public void hasValidAccessRights() throws Exception {
        UserAccountChecker accountChecker = new UserAccountChecker(mocked_user_access);
        //Admin level
        when(mocked_user_access.getSessionType("0001")).thenReturn(SessionType.ADMIN);
        when(mocked_token.getSessionId()).thenReturn("0001");
        Assert.assertTrue(accountChecker.hasValidAccessRights(mocked_token, SessionType.ADMIN));
        Assert.assertTrue(accountChecker.hasValidAccessRights(mocked_token, SessionType.USER));
        //User level
        when(mocked_user_access.getSessionType("0002")).thenReturn(SessionType.USER);
        when(mocked_token.getSessionId()).thenReturn("0002");
        Assert.assertFalse(accountChecker.hasValidAccessRights(mocked_token, SessionType.ADMIN));
        Assert.assertTrue(accountChecker.hasValidAccessRights(mocked_token, SessionType.USER));
        //Invalid token
        when(mocked_user_access.getSessionType("0003")).thenThrow(SessionInvalidException.class);
        when(mocked_token.getSessionId()).thenReturn("0003");
        Assert.assertFalse(accountChecker.hasValidAccessRights(mocked_token, SessionType.ADMIN));
        Assert.assertFalse(accountChecker.hasValidAccessRights(mocked_token, SessionType.USER));
    }

    @Test
    public void isLoggedIn() throws Exception {
        UserAccountChecker accountChecker = new UserAccountChecker(mocked_user_access);
        when(mocked_token.getSessionId()).thenReturn("0001");
        when(mocked_token.getExpiry()).thenReturn(Date.from(Instant.now().plus(1, ChronoUnit.DAYS)));
        Assert.assertTrue(accountChecker.isLoggedIn(mocked_token));
    }

    @Test
    public void isLoggedIn_fail_expired() throws Exception {
        UserAccountChecker accountChecker = new UserAccountChecker(mocked_user_access);
        Date expiry = Date.from(Instant.now().plus(1, ChronoUnit.DAYS));
        doThrow(SessionExpiredException.class).when(mocked_user_access).checkValidity("0001", expiry);
        when(mocked_token.getSessionId()).thenReturn("0001");
        when(mocked_token.getExpiry()).thenReturn(expiry);
        Assert.assertFalse(accountChecker.isLoggedIn(mocked_token));
    }

    @Test
    public void isLoggedIn_fail_invalid() throws Exception {
        UserAccountChecker accountChecker = new UserAccountChecker(mocked_user_access);
        Date expiry = Date.from(Instant.now().plus(1, ChronoUnit.DAYS));
        doThrow(SessionInvalidException.class).when(mocked_user_access).checkValidity("0001", expiry);
        when(mocked_token.getSessionId()).thenReturn("0001");
        when(mocked_token.getExpiry()).thenReturn(expiry);
        Assert.assertFalse(accountChecker.isLoggedIn(mocked_token));
    }

    @Test
    public void isLoggedIn_fail_corrupt() throws Exception {
        UserAccountChecker accountChecker = new UserAccountChecker(mocked_user_access);
        Date expiry = Date.from(Instant.now().plus(1, ChronoUnit.DAYS));
        doThrow(SessionCorruptedException.class).when(mocked_user_access).checkValidity("0001", expiry);
        when(mocked_token.getSessionId()).thenReturn("0001");
        when(mocked_token.getExpiry()).thenReturn(expiry);
        Assert.assertFalse(accountChecker.isLoggedIn(mocked_token));
    }
}