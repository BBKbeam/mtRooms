package bbk_beam.mtRooms.uaa;

import bbk_beam.mtRooms.ServiceDriver;
import bbk_beam.mtRooms.admin.administration.IAdminSession;
import bbk_beam.mtRooms.admin.authentication.Token;
import bbk_beam.mtRooms.admin.exception.AccountExistenceException;
import bbk_beam.mtRooms.admin.exception.AccountOverrideException;
import bbk_beam.mtRooms.db.exception.SessionCorruptedException;
import bbk_beam.mtRooms.db.exception.SessionExpiredException;
import bbk_beam.mtRooms.db.exception.SessionInvalidException;
import bbk_beam.mtRooms.db.session.SessionType;
import eadjlib.datastructure.ObjectTable;

public class AdminDelegate implements IAdminSession {
    private IAdminSession admin_session;

    /**
     * Constructor
     *
     * @param driver Service layer driver
     */
    AdminDelegate(ServiceDriver driver) {
        this.admin_session = driver.getAdminSession();
    }

    @Override
    public void createNewAccount(Token admin_token, SessionType account_type, String username, String password) throws SessionInvalidException, SessionExpiredException, SessionCorruptedException, AccountExistenceException, RuntimeException {
        this.admin_session.createNewAccount(admin_token, account_type, username, password);
    }

    @Override
    public void updateAccountPassword(Token admin_token, Integer account_id, String password) throws SessionInvalidException, SessionExpiredException, SessionCorruptedException, AccountExistenceException, AccountOverrideException, RuntimeException {
        this.admin_session.updateAccountPassword(admin_token, account_id, password);
    }

    @Override
    public void activateAccount(Token admin_token, Integer account_id) throws SessionInvalidException, SessionExpiredException, SessionCorruptedException, AccountExistenceException, AccountOverrideException, RuntimeException {
        this.admin_session.activateAccount(admin_token, account_id);
    }

    @Override
    public void deactivateAccount(Token admin_token, Integer account_id) throws SessionInvalidException, SessionExpiredException, SessionCorruptedException, AccountExistenceException, AccountOverrideException, RuntimeException {
        this.admin_session.deactivateAccount(admin_token, account_id);
    }

    @Override
    public void deleteAccount(Token admin_token, Integer account_id) throws SessionInvalidException, SessionExpiredException, SessionCorruptedException, AccountExistenceException, AccountOverrideException, RuntimeException {
        this.admin_session.deleteAccount(admin_token, account_id);
    }

    @Override
    public ObjectTable getAccounts(Token admin_token) throws SessionInvalidException, SessionExpiredException, SessionCorruptedException, RuntimeException {
        return this.admin_session.getAccounts(admin_token);
    }

    @Override
    public ObjectTable getAccount(Token admin_token, Integer account_id) throws SessionInvalidException, SessionExpiredException, SessionCorruptedException, RuntimeException {
        return this.admin_session.getAccount(admin_token, account_id);
    }

    @Override
    public ObjectTable getAccount(Token admin_token, String account_username) throws SessionInvalidException, SessionExpiredException, SessionCorruptedException, RuntimeException {
        return this.admin_session.getAccount(admin_token, account_username);
    }

    @Override
    public boolean optimiseReservationDatabase(Token admin_token) throws SessionInvalidException, SessionExpiredException, SessionCorruptedException {
        return this.admin_session.optimiseReservationDatabase(admin_token);
    }

    @Override
    public boolean optimiseUserAccountDatabase(Token admin_token) throws SessionInvalidException, SessionExpiredException, SessionCorruptedException {
        return this.admin_session.optimiseUserAccountDatabase(admin_token);
    }
}
