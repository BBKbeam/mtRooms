package bbk_beam.mtRooms.uaa;

import bbk_beam.mtRooms.admin.authentication.Token;
import bbk_beam.mtRooms.admin.dto.Account;
import bbk_beam.mtRooms.admin.exception.AccountExistenceException;
import bbk_beam.mtRooms.admin.exception.AccountOverrideException;
import bbk_beam.mtRooms.db.exception.SessionCorruptedException;
import bbk_beam.mtRooms.db.exception.SessionExpiredException;
import bbk_beam.mtRooms.db.exception.SessionInvalidException;
import bbk_beam.mtRooms.db.session.SessionType;
import eadjlib.datastructure.ObjectTable;

import java.util.List;

public class AuthenticatedAdministration implements IAuthenticatedAdministration {
    private AdministrationDelegate delegate;

    /**
     * Constructor
     *
     * @param delegate AdministrationDelegate instance
     */
    public AuthenticatedAdministration(AdministrationDelegate delegate) {
        this.delegate = delegate;
    }

    @Override
    public void createNewAccount(Token admin_token, SessionType account_type, String username, String password) throws SessionInvalidException, SessionExpiredException, SessionCorruptedException, AccountExistenceException, RuntimeException {
        this.delegate.createNewAccount(admin_token, account_type, username, password);
    }

    @Override
    public void updateAccountPassword(Token admin_token, Integer account_id, String password) throws SessionInvalidException, SessionExpiredException, SessionCorruptedException, AccountExistenceException, AccountOverrideException, RuntimeException {
        this.delegate.updateAccountPassword(admin_token, account_id, password);
    }

    @Override
    public void activateAccount(Token admin_token, Integer account_id) throws SessionInvalidException, SessionExpiredException, SessionCorruptedException, AccountExistenceException, AccountOverrideException, RuntimeException {
        this.delegate.activateAccount(admin_token, account_id);
    }

    @Override
    public void deactivateAccount(Token admin_token, Integer account_id) throws SessionInvalidException, SessionExpiredException, SessionCorruptedException, AccountExistenceException, AccountOverrideException, RuntimeException {
        this.delegate.deactivateAccount(admin_token, account_id);
    }

    @Override
    public void deleteAccount(Token admin_token, Integer account_id) throws SessionInvalidException, SessionExpiredException, SessionCorruptedException, AccountExistenceException, AccountOverrideException, RuntimeException {
        this.delegate.deleteAccount(admin_token, account_id);
    }

    @Override
    public List<Account> getAccounts(Token admin_token) throws SessionInvalidException, SessionExpiredException, SessionCorruptedException, RuntimeException {
        return this.delegate.getAccounts(admin_token);
    }

    @Override
    public Account getAccount(Token admin_token, Integer account_id) throws AccountExistenceException, SessionInvalidException, SessionExpiredException, SessionCorruptedException, RuntimeException {
        return this.delegate.getAccount(admin_token, account_id);
    }

    @Override
    public Account getAccount(Token admin_token, String account_username) throws AccountExistenceException, SessionInvalidException, SessionExpiredException, SessionCorruptedException, RuntimeException {
        return this.delegate.getAccount(admin_token, account_username);
    }

    @Override
    public boolean optimiseReservationDatabase(Token admin_token) throws SessionInvalidException, SessionExpiredException, SessionCorruptedException {
        return this.delegate.optimiseReservationDatabase(admin_token);
    }

    @Override
    public boolean optimiseUserAccountDatabase(Token admin_token) throws SessionInvalidException, SessionExpiredException, SessionCorruptedException {
        return this.delegate.optimiseUserAccountDatabase(admin_token);
    }
}
