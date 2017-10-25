package bbk_beam.mtRooms.admin.authentication;

import bbk_beam.mtRooms.admin.exception.AuthenticationFailureException;
import bbk_beam.mtRooms.db.IUserAccDbAccess;
import bbk_beam.mtRooms.db.UserAccDbAccess;
import bbk_beam.mtRooms.db.exception.SessionInvalidException;
import bbk_beam.mtRooms.db.session.SessionType;
import eadjlib.datastructure.ObjectTable;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserAccountCheckerTest {
    private IUserAccDbAccess mocked_user_access;
    private Token mocked_token;

    @Before
    public void setUp() throws Exception {
        mocked_user_access = mock(UserAccDbAccess.class);
        mocked_token = mock(Token.class);
    }

    @After
    public void tearDown() throws Exception {
        mocked_user_access = null;
        mocked_token = null;
    }

    @Test
    public void login() throws Exception {
        ObjectTable mocked_table = mock(ObjectTable.class);
        UserAccountChecker accountChecker = new UserAccountChecker(mocked_user_access);
        HashMap<String, Object> account_row = new HashMap<>();

        String salt = PasswordHash.createSalt();
        String hash = PasswordHash.createHash("password", salt);
        account_row.put("id", 1);
        account_row.put("pwd_hash", hash);
        account_row.put("pwd_salt", salt);

        when(mocked_user_access.pullFromDB(any(String.class))).thenReturn(mocked_table);
        when(mocked_table.getRow(1)).thenReturn(account_row);
        when(mocked_user_access.pushToDB(any(String.class))).thenReturn(true); //TODO check new date is passed (check with returned token)


        accountChecker.login("username", "password");
        //TODO
        Assert.assertFalse(true);
    }

    @Test(expected = AuthenticationFailureException.class)
    public void login_bad_pwd_hash() throws Exception {
        //TODO
        Assert.assertFalse(true);
    }

    @Test(expected = AuthenticationFailureException.class)
    public void login_bad_sql() throws Exception {
        //TODO
        Assert.assertFalse(true);
    }

    @Test(expected = AuthenticationFailureException.class)
    public void login_missing_account_info() throws Exception {
        //TODO
        Assert.assertFalse(true);
    }

    @Test(expected = AuthenticationFailureException.class)
    public void login_hasher_failure() throws Exception {
        //TODO
        Assert.assertFalse(true);
    }

    @Test
    public void logout() throws Exception {
        UserAccountChecker accountChecker = new UserAccountChecker(mocked_user_access);
        //TODO
        Assert.assertFalse(true);
    }

    @Test(expected = SessionInvalidException.class)
    public void logout_bad_session() throws Exception {
        UserAccountChecker accountChecker = new UserAccountChecker(mocked_user_access);
        //TODO
        Assert.assertFalse(true);
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
}