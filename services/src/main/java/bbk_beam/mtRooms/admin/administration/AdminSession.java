package bbk_beam.mtRooms.admin.administration;

import bbk_beam.mtRooms.admin.authentication.Token;
import bbk_beam.mtRooms.admin.exception.AccountExistenceException;
import bbk_beam.mtRooms.admin.exception.AccountOverrideException;
import bbk_beam.mtRooms.db.IReservationDbAccess;
import bbk_beam.mtRooms.db.IUserAccDbAccess;
import bbk_beam.mtRooms.db.exception.SessionExpiredException;
import bbk_beam.mtRooms.db.exception.SessionInvalidException;
import bbk_beam.mtRooms.db.session.SessionType;
import eadjlib.datastructure.ObjectTable;

public class AdminSession implements IAdminSession {
    private UserAccAdministration administration;
    private ReservationDbMaintenance maintenance;

    /**
     * Constructor
     *
     * @param reservationDbAccess IReservationDbAccess instance
     * @param userAccDbAccess     IUserAccDbAccess instance
     */
    public AdminSession(IReservationDbAccess reservationDbAccess, IUserAccDbAccess userAccDbAccess) {
        this.administration = new UserAccAdministration(userAccDbAccess);
        this.maintenance = new ReservationDbMaintenance(reservationDbAccess);
    }

    /**
     * Constructor (injector used for testing)
     *
     * @param administration_module UserAccAdministration instance
     * @param maintenance_module    ReservationDbMaintenance instance
     */
    public AdminSession(UserAccAdministration administration_module, ReservationDbMaintenance maintenance_module) {
        this.administration = administration_module;
        this.maintenance = maintenance_module;
    }

    @Override
    public void createNewAccount(Token admin_token, SessionType account_type, String username, String password) throws SessionInvalidException, SessionExpiredException, AccountExistenceException {
        //TODO
    }

    @Override
    public void updateAccountPassword(Token admin_token, Integer account_id, String password) throws SessionInvalidException, SessionExpiredException, AccountExistenceException, AccountOverrideException {
        //TODO
    }

    @Override
    public void deactivateAccount(Token admin_token, Integer account_id) throws SessionInvalidException, SessionExpiredException, AccountExistenceException, AccountOverrideException {
        //TODO
    }

    @Override
    public void deleteAccount(Token admin_token, Integer account_id) throws SessionInvalidException, SessionExpiredException, AccountExistenceException, AccountOverrideException {
        //TODO
    }

    @Override
    public ObjectTable getAccounts(Token admin_token) throws SessionInvalidException, SessionExpiredException {
        //TODO
        return null;
    }

    @Override
    public ObjectTable getAccount(Token admin_token, Integer account_id) throws SessionInvalidException, SessionExpiredException, AccountExistenceException {
        //TODO
        return null;
    }

    @Override
    public ObjectTable getAccount(Token admin_token, String account_username) throws SessionInvalidException, SessionExpiredException, AccountExistenceException {
        //TODO
        return null;
    }
}
