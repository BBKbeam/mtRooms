package bbk_beam.mtRooms.admin.administration;

import bbk_beam.mtRooms.admin.authentication.PasswordHash;
import bbk_beam.mtRooms.admin.exception.AccountExistenceException;
import bbk_beam.mtRooms.admin.exception.RecordUpdateException;
import bbk_beam.mtRooms.db.DbSystemBootstrap;
import bbk_beam.mtRooms.db.IUserAccDbAccess;
import bbk_beam.mtRooms.db.exception.DbQueryException;
import bbk_beam.mtRooms.db.session.SessionType;
import eadjlib.datastructure.ObjectTable;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserAccAdministrationTest {
    private DbSystemBootstrap db_bootstrapper = new DbSystemBootstrap();
    private IUserAccDbAccess user_db_access;
    private IUserAccDbAccess mock_user_db_access;
    private final String session_user_id = "00001";
    private final Date session_expiry = Date.from(Instant.now().plus(1, ChronoUnit.DAYS));

    @Before
    public void setUp() throws Exception {
        Files.deleteIfExists(Paths.get("user_acc_test.db"));
        this.db_bootstrapper.init("user_acc_test.db");
        this.user_db_access = this.db_bootstrapper.getUserAccDbAccess();
        this.user_db_access.openSession(this.session_user_id, this.session_expiry, SessionType.ADMIN);
        this.mock_user_db_access = mock(IUserAccDbAccess.class);
    }

    @After
    public void tearDown() throws Exception {
        this.user_db_access.closeSession(this.session_user_id);
        this.user_db_access = null;
        this.mock_user_db_access = null;
        Files.deleteIfExists(Paths.get("user_acc_test.db"));
    }

    @Test
    public void createNewAccount() throws Exception {
        UserAccAdministration userAccAdministration = new UserAccAdministration(this.user_db_access);
        userAccAdministration.createNewAccount(SessionType.USER, "mtRoomUser", "user_password_0000");
        ObjectTable result = userAccAdministration.getAccount("mtRoomUser");
        HashMap<String, Object> account_row = result.getRow(1);
        Assert.assertEquals("mtRoomUser", account_row.get("username"));
        Assert.assertTrue(PasswordHash.validateHash(
                "user_password_0000",
                (String) account_row.get("pwd_salt"),
                (String) account_row.get("pwd_hash"))
        );
        Assert.assertEquals(SessionType.USER, SessionType.valueOf((String) account_row.get("description")));
    }

    @Test(expected = AccountExistenceException.class)
    public void createNewAccount_fail_overwrite() throws Exception {
        UserAccAdministration userAccAdministration = new UserAccAdministration(this.user_db_access);
        userAccAdministration.createNewAccount(SessionType.USER, "root", "password");
    }

    @Test(expected = RecordUpdateException.class)
    public void createNewAccount_fail_get_account_type_id() throws Exception {
        UserAccAdministration userAccAdministration = new UserAccAdministration(this.mock_user_db_access);
        String query_type_id = "SELECT id FROM AccountType WHERE description = \"USER\"";
        ObjectTable mocked_table = mock(ObjectTable.class);
        //Type ID fetching
        when(mock_user_db_access.pullFromDB(query_type_id)).thenReturn(mocked_table);
        when(mocked_table.isEmpty()).thenReturn(true);
        //Method call
        userAccAdministration.createNewAccount(SessionType.USER, "mtRoomUser", "user_password_0000");
    }

    @Test(expected = RecordUpdateException.class)
    public void createNewAccount_fail_no_change_in_record() throws Exception {
        UserAccAdministration userAccAdministration = new UserAccAdministration(this.mock_user_db_access);
        String query_type_id = "SELECT id FROM AccountType WHERE description = \"USER\"";
        String query_accounts = "SELECT UserAccount.id, UserAccount.username, UserAccount.pwd_hash, UserAccount.pwd_salt, UserAccount.created, UserAccount.last_pwd_change, UserAccount.last_login, UserAccount.active_state, AccountType.id AS type_id, AccountType.description FROM UserAccount LEFT OUTER JOIN AccountType ON UserAccount.account_type_id = AccountType.id WHERE UserAccount.username = \"mtRoomUser\"";
        String query_changes = "SELECT changes()";
        ObjectTable mocked_type_id_table = mock(ObjectTable.class);
        ObjectTable mocked_account_table = mock(ObjectTable.class);
        ObjectTable mocked_changes_table = mock(ObjectTable.class);
        //Type Id fetching
        when(this.mock_user_db_access.pullFromDB(query_type_id)).thenReturn(mocked_type_id_table);
        when(mocked_type_id_table.isEmpty()).thenReturn(false);
        when(mocked_type_id_table.getInteger(1, 1)).thenReturn(2);
        //New account query
        when(this.mock_user_db_access.pullFromDB(query_accounts)).thenReturn(mocked_account_table);
        when(mocked_account_table.isEmpty()).thenReturn(true);
        when(this.mock_user_db_access.pushToDB(anyString())).then((InvocationOnMock invocation) -> {
            Object[] args = invocation.getArguments();
            return this.user_db_access.pushToDB((String) args[0]);
        });
        //Changes
        when(mock_user_db_access.pullFromDB(query_changes)).thenReturn(mocked_changes_table);
        when(mocked_changes_table.getInteger(1, 1)).thenReturn(0); //No changes
        //Method call
        userAccAdministration.createNewAccount(SessionType.USER, "mtRoomUser", "user_password_0000");
    }

    @Test(expected = RecordUpdateException.class)
    public void createNewAccount_fail_db_query_type_id() throws Exception {
        UserAccAdministration userAccAdministration = new UserAccAdministration(this.mock_user_db_access);
        String query_type_id = "SELECT id FROM AccountType WHERE description = \"USER\"";
        //Type Id fetching
        when(this.mock_user_db_access.pullFromDB(query_type_id)).thenThrow(RecordUpdateException.class);
        //Method call
        userAccAdministration.createNewAccount(SessionType.USER, "mtRoomUser", "user_password_0000");
    }

    @Test(expected = RecordUpdateException.class)
    public void createNewAccount_fail_db_query_accounts() throws Exception {
        UserAccAdministration userAccAdministration = new UserAccAdministration(this.mock_user_db_access);
        String query_type_id = "SELECT id FROM AccountType WHERE description = \"USER\"";
        String query_accounts = "SELECT UserAccount.id, UserAccount.username, UserAccount.pwd_hash, UserAccount.pwd_salt, UserAccount.created, UserAccount.last_pwd_change, UserAccount.last_login, UserAccount.active_state, AccountType.id AS type_id, AccountType.description FROM UserAccount LEFT OUTER JOIN AccountType ON UserAccount.account_type_id = AccountType.id WHERE UserAccount.username = \"mtRoomUser\"";
        ObjectTable mocked_type_id_table = mock(ObjectTable.class);
        //Type Id fetching
        when(this.mock_user_db_access.pullFromDB(query_type_id)).thenReturn(mocked_type_id_table);
        when(mocked_type_id_table.isEmpty()).thenReturn(false);
        when(mocked_type_id_table.getInteger(1, 1)).thenReturn(2);
        //New account query
        when(this.mock_user_db_access.pullFromDB(query_accounts)).thenThrow(RecordUpdateException.class);
        //Method call
        userAccAdministration.createNewAccount(SessionType.USER, "mtRoomUser", "user_password_0000");
    }

    @Test(expected = RecordUpdateException.class)
    public void createNewAccount_fail_db_query_changes() throws Exception {
        UserAccAdministration userAccAdministration = new UserAccAdministration(this.mock_user_db_access);
        String query_type_id = "SELECT id FROM AccountType WHERE description = \"USER\"";
        String query_accounts = "SELECT UserAccount.id, UserAccount.username, UserAccount.pwd_hash, UserAccount.pwd_salt, UserAccount.created, UserAccount.last_pwd_change, UserAccount.last_login, UserAccount.active_state, AccountType.id AS type_id, AccountType.description FROM UserAccount LEFT OUTER JOIN AccountType ON UserAccount.account_type_id = AccountType.id WHERE UserAccount.username = \"mtRoomUser\"";
        String query_changes = "SELECT changes()";
        ObjectTable mocked_type_id_table = mock(ObjectTable.class);
        ObjectTable mocked_account_table = mock(ObjectTable.class);
        //Type Id fetching
        when(this.mock_user_db_access.pullFromDB(query_type_id)).thenReturn(mocked_type_id_table);
        when(mocked_type_id_table.isEmpty()).thenReturn(false);
        when(mocked_type_id_table.getInteger(1, 1)).thenReturn(2);
        //New account query
        when(this.mock_user_db_access.pullFromDB(query_accounts)).thenReturn(mocked_account_table);
        when(mocked_account_table.isEmpty()).thenReturn(true);
        when(this.mock_user_db_access.pushToDB(anyString())).then((InvocationOnMock invocation) -> {
            Object[] args = invocation.getArguments();
            return this.user_db_access.pushToDB((String) args[0]);
        });
        //Changes
        when(mock_user_db_access.pullFromDB(query_changes)).thenThrow(RecordUpdateException.class);
        //Method call
        userAccAdministration.createNewAccount(SessionType.USER, "mtRoomUser", "user_password_0000");
    }

    @Test
    public void updateAccountPassword() throws Exception {
        UserAccAdministration userAccAdministration = new UserAccAdministration(this.user_db_access);
        String query_accounts = "SELECT UserAccount.id, UserAccount.username, UserAccount.pwd_hash, UserAccount.pwd_salt, UserAccount.created, UserAccount.last_pwd_change, UserAccount.last_login, UserAccount.active_state, AccountType.id AS type_id, AccountType.description FROM UserAccount LEFT OUTER JOIN AccountType ON UserAccount.account_type_id = AccountType.id WHERE UserAccount.id = 1";
        userAccAdministration.updateAccountPassword(1, "n3w_pa55w0rd");
        ObjectTable result = userAccAdministration.getAccount(1);
        HashMap<String, Object> account_row = result.getRow(1);
        Assert.assertTrue(PasswordHash.validateHash(
                "n3w_pa55w0rd",
                (String) account_row.get("pwd_salt"),
                (String) account_row.get("pwd_hash"))
        );
    }

    @Test(expected = AccountExistenceException.class)
    public void updateAccountPassword_fail_get_account() throws Exception {
        UserAccAdministration userAccAdministration = new UserAccAdministration(this.mock_user_db_access);
        ObjectTable account_table = mock(ObjectTable.class);
        String query_accounts = "SELECT UserAccount.id, UserAccount.username, UserAccount.pwd_hash, UserAccount.pwd_salt, UserAccount.created, UserAccount.last_pwd_change, UserAccount.last_login, UserAccount.active_state, AccountType.id AS type_id, AccountType.description FROM UserAccount LEFT OUTER JOIN AccountType ON UserAccount.account_type_id = AccountType.id WHERE UserAccount.id = 1";
        when(mock_user_db_access.pullFromDB(query_accounts)).thenReturn(account_table);
        when(account_table.isEmpty()).thenReturn(true);
        userAccAdministration.updateAccountPassword(1, "n3w_pa55w0rd");
    }

    @Test(expected = RecordUpdateException.class)
    public void updateAccountPassword_fail_db_query_changes() throws Exception {
        UserAccAdministration userAccAdministration = new UserAccAdministration(this.mock_user_db_access);
        ObjectTable account_table = mock(ObjectTable.class);
        ObjectTable changes_table = mock(ObjectTable.class);
        //Account fetching
        String query_accounts = "SELECT UserAccount.id, UserAccount.username, UserAccount.pwd_hash, UserAccount.pwd_salt, UserAccount.created, UserAccount.last_pwd_change, UserAccount.last_login, UserAccount.active_state, AccountType.id AS type_id, AccountType.description FROM UserAccount LEFT OUTER JOIN AccountType ON UserAccount.account_type_id = AccountType.id WHERE UserAccount.id = 1";
        when(mock_user_db_access.pullFromDB(query_accounts)).thenReturn(account_table);
        when(account_table.isEmpty()).thenReturn(false);
        //Changes fetching
        String query_changes = "SELECT changes()";
        when(mock_user_db_access.pullFromDB(query_changes)).thenReturn(changes_table);
        when(changes_table.getInteger(1, 1)).thenReturn(0);
        userAccAdministration.updateAccountPassword(1, "n3w_pa55w0rd");
    }

    @Test(expected = RecordUpdateException.class)
    public void updateAccountPassword_fail_get_account_query() throws Exception {
        UserAccAdministration userAccAdministration = new UserAccAdministration(this.mock_user_db_access);
        String query_accounts = "SELECT UserAccount.id, UserAccount.username, UserAccount.pwd_hash, UserAccount.pwd_salt, UserAccount.created, UserAccount.last_pwd_change, UserAccount.last_login, UserAccount.active_state, AccountType.id AS type_id, AccountType.description FROM UserAccount LEFT OUTER JOIN AccountType ON UserAccount.account_type_id = AccountType.id WHERE UserAccount.id = 1";
        when(mock_user_db_access.pullFromDB(query_accounts)).thenThrow(DbQueryException.class);
        userAccAdministration.updateAccountPassword(1, "n3w_pa55w0rd");
    }

    @Test
    public void deactivateAccount_and_activateAccount() throws Exception {
        UserAccAdministration userAccAdministration = new UserAccAdministration(this.user_db_access);
        //Create new user to test against
        userAccAdministration.createNewAccount(SessionType.USER, "NewUser001", "password");
        ObjectTable account_table = userAccAdministration.getAccount("NewUser001");
        HashMap<String, Object> account_row = account_table.getRow(1);
        Assert.assertEquals("NewUser001", account_row.get("username"));
        Integer account_id = (Integer) account_row.get("id");
        //Deactivate user
        userAccAdministration.deactivateAccount(account_id);
        ObjectTable active_flag_table = this.user_db_access.pullFromDB("SELECT active_state FROM UserAccount WHERE id = " + account_id);
        Assert.assertEquals(0, active_flag_table.getInteger(1, 1));
        //Activate user
        userAccAdministration.activateAccount(account_id);
        active_flag_table = this.user_db_access.pullFromDB("SELECT active_state FROM UserAccount WHERE id = " + account_id);
        Assert.assertEquals(1, active_flag_table.getInteger(1, 1));
    }

    @Test(expected = AccountExistenceException.class)
    public void activateAccount_exists_fail() throws Exception {
        UserAccAdministration userAccAdministration = new UserAccAdministration(this.user_db_access);
        userAccAdministration.activateAccount(2);
    }

    @Test(expected = DbQueryException.class)
    public void activateAccount_query_fail() throws Exception {
        UserAccAdministration userAccAdministration = new UserAccAdministration(this.mock_user_db_access);
        when(mock_user_db_access.pullFromDB(anyString())).thenThrow(DbQueryException.class);
        userAccAdministration.activateAccount(1);
    }

    @Test(expected = AccountExistenceException.class)
    public void deactivateAccount_exists_fail() throws Exception {
        UserAccAdministration userAccAdministration = new UserAccAdministration(this.user_db_access);
        userAccAdministration.deactivateAccount(2);
    }

    @Test(expected = DbQueryException.class)
    public void deactivateAccount_query_fail() throws Exception {
        UserAccAdministration userAccAdministration = new UserAccAdministration(this.mock_user_db_access);
        when(mock_user_db_access.pullFromDB(anyString())).thenThrow(DbQueryException.class);
        userAccAdministration.deactivateAccount(1);
    }

    @Test
    public void deleteAccount() throws Exception {
        UserAccAdministration userAccAdministration = new UserAccAdministration(this.user_db_access);
        userAccAdministration.deleteAccount(1);
        ObjectTable account_table = user_db_access.pullFromDB("SELECT * FROM UserAccount WHERE id = 1");
        Assert.assertTrue(account_table.isEmpty());
    }

    @Test(expected = AccountExistenceException.class)
    public void deleteAccount_exists_fail() throws Exception {
        UserAccAdministration userAccAdministration = new UserAccAdministration(this.user_db_access);
        userAccAdministration.deleteAccount(2);
    }

    @Test(expected = DbQueryException.class)
    public void deleteAccount_query_fail() throws Exception {
        UserAccAdministration userAccAdministration = new UserAccAdministration(this.mock_user_db_access);
        when(mock_user_db_access.pullFromDB(anyString())).thenThrow(DbQueryException.class);
        userAccAdministration.deleteAccount(1);
    }


    @Test
    public void getAccounts() throws Exception {
        UserAccAdministration userAccAdministration = new UserAccAdministration(this.user_db_access);
        ObjectTable table = userAccAdministration.getAccounts();
        System.out.println(table);
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

}