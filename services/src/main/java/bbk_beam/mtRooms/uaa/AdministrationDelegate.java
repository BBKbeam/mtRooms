package bbk_beam.mtRooms.uaa;

import bbk_beam.mtRooms.admin.administration.IAdminSession;
import bbk_beam.mtRooms.admin.authentication.Token;
import bbk_beam.mtRooms.admin.dto.Account;
import bbk_beam.mtRooms.admin.dto.AccountType;
import bbk_beam.mtRooms.admin.dto.Usage;
import bbk_beam.mtRooms.admin.exception.*;
import bbk_beam.mtRooms.db.exception.SessionCorruptedException;
import bbk_beam.mtRooms.db.exception.SessionExpiredException;
import bbk_beam.mtRooms.db.exception.SessionInvalidException;
import bbk_beam.mtRooms.db.session.SessionType;
import bbk_beam.mtRooms.reservation.dto.*;

import java.util.List;

public class AdministrationDelegate implements IAdminSession {
    private IAdminSession admin_session;

    /**
     * Constructor
     *
     * @param admin_session IAdminSession instance
     */
    AdministrationDelegate(IAdminSession admin_session) {
        this.admin_session = admin_session;
    }

    /**
     * Creates a new account
     *
     * @param admin_token  Administrator session token
     * @param account_type Account type to create
     * @param username     Username of the account to create
     * @param password     Password of the account to create
     * @throws SessionInvalidException   when administrator session is not valid
     * @throws SessionExpiredException   when current administrator session has expired
     * @throws SessionCorruptedException when tracked and token expiry timestamps do not match for the token's ID
     * @throws AccountExistenceException when account with same name exists already
     * @throws RuntimeException          when non-standard failure occurred during account creation in records
     */
    public void createNewAccount(Token admin_token, SessionType account_type, String username, String password) throws SessionInvalidException, SessionExpiredException, SessionCorruptedException, AccountExistenceException, RuntimeException {
        this.admin_session.createNewAccount(admin_token, account_type, username, password);
    }

    /**
     * Updates existing account's password
     *
     * @param admin_token Administrator or User session token
     * @param account_id  ID of account to update
     * @param password    New password
     * @throws SessionInvalidException   when session token is not valid
     * @throws SessionExpiredException   when current session has expired
     * @throws SessionCorruptedException when tracked and token expiry timestamps do not match for the token's ID
     * @throws AccountExistenceException when account does not exist in the records
     * @throws AccountOverrideException  when new password is the same as old one
     * @throws RuntimeException          when non-standard failure occurred during account creation in records
     */
    public void updateAccountPassword(Token admin_token, Integer account_id, String password) throws SessionInvalidException, SessionExpiredException, SessionCorruptedException, AccountExistenceException, AccountOverrideException, RuntimeException {
        this.admin_session.updateAccountPassword(admin_token, account_id, password);
    }

    /**
     * Activates an existing account
     *
     * @param admin_token Administrator session token
     * @param account_id  ID of account to activate
     * @throws SessionInvalidException   when administrator session is not valid
     * @throws SessionExpiredException   when current administrator session has expired
     * @throws SessionCorruptedException when tracked and token expiry timestamps do not match for the token's ID
     * @throws AccountExistenceException when account does not exist in the records
     * @throws AccountOverrideException  when account to activate is the current logged-in administrator's
     * @throws RuntimeException          when non-standard failure occurred during account activation
     */
    public void activateAccount(Token admin_token, Integer account_id) throws SessionInvalidException, SessionExpiredException, SessionCorruptedException, AccountExistenceException, AccountOverrideException, RuntimeException {
        this.admin_session.activateAccount(admin_token, account_id);
    }

    /**
     * Deactivates an existing account
     *
     * @param admin_token Administrator session token
     * @param account_id  ID of account to deactivate
     * @throws SessionInvalidException   when administrator session is not valid
     * @throws SessionExpiredException   when current administrator session has expired
     * @throws SessionCorruptedException when tracked and token expiry timestamps do not match for the token's ID
     * @throws AccountExistenceException when account does not exist in the records
     * @throws AccountOverrideException  when account to deactivates is the current logged-in administrator's
     * @throws RuntimeException          when non-standard failure occurred during account deactivation
     */
    public void deactivateAccount(Token admin_token, Integer account_id) throws SessionInvalidException, SessionExpiredException, SessionCorruptedException, AccountExistenceException, AccountOverrideException, RuntimeException {
        this.admin_session.deactivateAccount(admin_token, account_id);
    }

    /**
     * Deletes an existing  account record
     *
     * @param admin_token Administrator session token
     * @param account_id  ID of account to delete
     * @throws SessionInvalidException   when administrator session is not valid
     * @throws SessionExpiredException   when current administrator session has expired
     * @throws AccountExistenceException when account to delete does not exist in the records
     * @throws SessionCorruptedException when tracked and token expiry timestamps do not match for the token's ID
     * @throws AccountOverrideException  when account to delete is the current logged-in administrator's
     * @throws RuntimeException          when non-standard failure occurred during account removal from records
     */
    public void deleteAccount(Token admin_token, Integer account_id) throws SessionInvalidException, SessionExpiredException, SessionCorruptedException, AccountExistenceException, AccountOverrideException, RuntimeException {
        this.admin_session.deleteAccount(admin_token, account_id);
    }

    /**
     * Gets all accounts in records
     *
     * @param admin_token Administrator session token
     * @return List of Account DTOs
     * @throws SessionInvalidException   when administrator session is not valid
     * @throws SessionExpiredException   when current administrator session has expired
     * @throws SessionCorruptedException when tracked and token expiry timestamps do not match for the token's ID
     * @throws RuntimeException          when non-standard failure occurred during account fetching from records
     */
    public List<Account> getAccounts(Token admin_token) throws SessionInvalidException, SessionExpiredException, SessionCorruptedException, RuntimeException {
        return this.admin_session.getAccounts(admin_token);
    }

    /**
     * Gets the records for an account
     *
     * @param admin_token Administrator session token
     * @param account_id  Account ID
     * @return Account DTO
     * @throws AccountExistenceException when account does not exist in records
     * @throws SessionInvalidException   when administrator session is not valid
     * @throws SessionExpiredException   when current administrator session has expired
     * @throws SessionCorruptedException when tracked and token expiry timestamps do not match for the token's ID
     * @throws RuntimeException          when non-standard failure occurred during account fetching from records
     */
    public Account getAccount(Token admin_token, Integer account_id) throws AccountExistenceException, SessionInvalidException, SessionExpiredException, SessionCorruptedException, RuntimeException {
        return this.admin_session.getAccount(admin_token, account_id);
    }

    /**
     * Gets the records for an account
     *
     * @param admin_token      Administrator session token
     * @param account_username Account username
     * @return Account DTO
     * @throws AccountExistenceException when account does not exist in records
     * @throws SessionInvalidException   when administrator session is not valid
     * @throws SessionExpiredException   when current administrator session has expired
     * @throws SessionCorruptedException when tracked and token expiry timestamps do not match for the token's ID
     * @throws RuntimeException          when non-standard failure occurred during account fetching from records
     */
    public Account getAccount(Token admin_token, String account_username) throws AccountExistenceException, SessionInvalidException, SessionExpiredException, SessionCorruptedException, RuntimeException {
        return this.admin_session.getAccount(admin_token, account_username);
    }

    /**
     * Gets the list of account types
     *
     * @param admin_token Administrator session token
     * @return List of AccountType DTOs
     * @throws SessionInvalidException   when administrator session is not valid
     * @throws SessionExpiredException   when current administrator session has expired
     * @throws SessionCorruptedException when tracked and token expiry timestamps do not match for the token's ID
     * @throws RuntimeException          when non-standard failure occurred during account fetching from records
     */
    public List<AccountType> getAccountTypes(Token admin_token) throws SessionInvalidException, SessionExpiredException, SessionCorruptedException, RuntimeException {
        return this.admin_session.getAccountTypes(admin_token);
    }

    @Override
    public List<Building> getBuildings(Token admin_token) throws FailedRecordFetch, SessionExpiredException, SessionInvalidException, SessionCorruptedException {
        return this.admin_session.getBuildings(admin_token);
    }

    @Override
    public List<Floor> getFloors(Token admin_token, Building building) throws FailedRecordFetch, SessionExpiredException, SessionInvalidException, SessionCorruptedException {
        return this.admin_session.getFloors(admin_token, building);
    }

    @Override
    public List<Room> getRooms(Token admin_token, Floor floor) throws FailedRecordFetch, SessionExpiredException, SessionInvalidException, SessionCorruptedException {
        return this.admin_session.getRooms(admin_token, floor);
    }

    @Override
    public List<Usage<RoomPrice, Integer>> getRoomPrices(Token admin_token, Room room) throws FailedRecordFetch, SessionExpiredException, SessionInvalidException, SessionCorruptedException {
        return this.admin_session.getRoomPrices(admin_token, room);
    }

    @Override
    public Usage<RoomPrice, Integer> getMostRecentRoomPrice(Token admin_token, Room room) throws IncompleteRecord, FailedRecordFetch, SessionExpiredException, SessionInvalidException, SessionCorruptedException {
        return this.admin_session.getMostRecentRoomPrice(admin_token, room);
    }

    @Override
    public List<Usage<RoomCategory, Room>> getRoomCategories(Token admin_token) throws FailedRecordFetch, SessionExpiredException, SessionInvalidException, SessionCorruptedException {
        return this.admin_session.getRoomCategories(admin_token);
    }

    @Override
    public DetailedRoom getRoomDetails(Token admin_token, Room room) throws FailedRecordFetch, SessionExpiredException, SessionInvalidException, SessionCorruptedException {
        return this.admin_session.getRoomDetails(admin_token, room);
    }

    @Override
    public void add(Token admin_token, Building building) throws FailedRecordWrite, SessionExpiredException, SessionInvalidException, SessionCorruptedException {
        this.admin_session.add(admin_token, building);
    }

    @Override
    public void add(Token admin_token, Floor floor) throws FailedRecordWrite, SessionExpiredException, SessionInvalidException, SessionCorruptedException {
        this.admin_session.add(admin_token, floor);
    }

    @Override
    public void add(Token admin_token, Room room, RoomFixtures fixtures) throws FailedRecordWrite, SessionExpiredException, SessionInvalidException, SessionCorruptedException {
        this.admin_session.add(admin_token, room, fixtures);
    }

    @Override
    public RoomPrice add(Token admin_token, RoomPrice roomPrice) throws FailedRecordWrite, FailedRecordFetch, SessionExpiredException, SessionInvalidException, SessionCorruptedException {
        return this.admin_session.add(admin_token, roomPrice);
    }

    @Override
    public RoomCategory add(Token admin_token, RoomCategory roomCategory) throws FailedRecordWrite, FailedRecordFetch, SessionExpiredException, SessionInvalidException, SessionCorruptedException {
        return this.admin_session.add(admin_token, roomCategory);
    }

    @Override
    public void update(Token admin_token, Building building) throws FailedRecordUpdate, SessionExpiredException, SessionInvalidException, SessionCorruptedException {
        this.admin_session.update(admin_token, building);
    }

    @Override
    public void update(Token admin_token, Floor floor) throws FailedRecordUpdate, SessionExpiredException, SessionInvalidException, SessionCorruptedException {
        this.admin_session.update(admin_token, floor);
    }

    @Override
    public void update(Token admin_token, Room room) throws FailedRecordUpdate, SessionExpiredException, SessionInvalidException, SessionCorruptedException {
        this.admin_session.update(admin_token, room);
    }

    @Override
    public boolean remove(Token admin_token, Building building) throws FailedRecordWrite, SessionExpiredException, SessionInvalidException, SessionCorruptedException {
        return this.admin_session.remove(admin_token, building);
    }

    @Override
    public boolean remove(Token admin_token, Floor floor) throws FailedRecordWrite, SessionExpiredException, SessionInvalidException, SessionCorruptedException {
        return this.admin_session.remove(admin_token, floor);
    }

    @Override
    public boolean remove(Token admin_token, Room room) throws FailedRecordWrite, SessionExpiredException, SessionInvalidException, SessionCorruptedException {
        return this.admin_session.remove(admin_token, room);
    }

    @Override
    public boolean remove(Token admin_token, RoomPrice roomPrice) throws FailedRecordWrite, SessionExpiredException, SessionInvalidException, SessionCorruptedException {
        return this.admin_session.remove(admin_token, roomPrice);
    }

    @Override
    public boolean remove(Token admin_token, RoomCategory roomCategory) throws FailedRecordWrite, SessionExpiredException, SessionInvalidException, SessionCorruptedException {
        return this.admin_session.remove(admin_token, roomCategory);
    }

    @Override
    public List<Usage<Membership, Integer>> getMemberships(Token admin_token) throws FailedRecordFetch, SessionExpiredException, SessionInvalidException, SessionCorruptedException {
        return this.admin_session.getMemberships(admin_token);
    }

    @Override
    public List<Discount> getDiscounts(Token admin_token) throws FailedRecordFetch, SessionExpiredException, SessionInvalidException, SessionCorruptedException {
        return this.admin_session.getDiscounts(admin_token);
    }

    @Override
    public Membership add(Token admin_token, Membership membership) throws FailedRecordWrite, SessionExpiredException, SessionInvalidException, SessionCorruptedException {
        return this.admin_session.add(admin_token, membership);
    }

    @Override
    public boolean remove(Token admin_token, Membership membership) throws FailedRecordWrite, SessionExpiredException, SessionInvalidException, SessionCorruptedException {
        return this.admin_session.remove(admin_token, membership);
    }

    /**
     * Optimises the reservation database
     *
     * @param admin_token Administrator session token
     * @return Success
     * @throws SessionInvalidException   when administrator session is not valid
     * @throws SessionExpiredException   when current administrator session has expired
     * @throws SessionCorruptedException when tracked and token expiry timestamps do not match for the token's ID
     */
    public boolean optimiseReservationDatabase(Token admin_token) throws SessionInvalidException, SessionExpiredException, SessionCorruptedException {
        return this.admin_session.optimiseReservationDatabase(admin_token);
    }

    /**
     * Optimises the user account database
     *
     * @param admin_token Administrator session token
     * @return Success
     * @throws SessionInvalidException   when administrator session is not valid
     * @throws SessionExpiredException   when current administrator session has expired
     * @throws SessionCorruptedException when tracked and token expiry timestamps do not match for the token's ID
     */
    public boolean optimiseUserAccountDatabase(Token admin_token) throws SessionInvalidException, SessionExpiredException, SessionCorruptedException {
        return this.admin_session.optimiseUserAccountDatabase(admin_token);
    }
}
