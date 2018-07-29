package bbk_beam.mtRooms.uaa;

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
    synchronized public void createNewAccount(Token admin_token, SessionType account_type, String username, String password) throws SessionInvalidException, SessionExpiredException, SessionCorruptedException, AccountExistenceException, RuntimeException {
        this.delegate.createNewAccount(admin_token, account_type, username, password);
    }

    @Override
    synchronized public void updateAccountPassword(Token admin_token, Integer account_id, String password) throws SessionInvalidException, SessionExpiredException, SessionCorruptedException, AccountExistenceException, AccountOverrideException, RuntimeException {
        this.delegate.updateAccountPassword(admin_token, account_id, password);
    }

    @Override
    synchronized public void activateAccount(Token admin_token, Integer account_id) throws SessionInvalidException, SessionExpiredException, SessionCorruptedException, AccountExistenceException, AccountOverrideException, RuntimeException {
        this.delegate.activateAccount(admin_token, account_id);
    }

    @Override
    synchronized public void deactivateAccount(Token admin_token, Integer account_id) throws SessionInvalidException, SessionExpiredException, SessionCorruptedException, AccountExistenceException, AccountOverrideException, RuntimeException {
        this.delegate.deactivateAccount(admin_token, account_id);
    }

    @Override
    synchronized public void deleteAccount(Token admin_token, Integer account_id) throws SessionInvalidException, SessionExpiredException, SessionCorruptedException, AccountExistenceException, AccountOverrideException, RuntimeException {
        this.delegate.deleteAccount(admin_token, account_id);
    }

    @Override
    synchronized public List<Account> getAccounts(Token admin_token) throws SessionInvalidException, SessionExpiredException, SessionCorruptedException, RuntimeException {
        return this.delegate.getAccounts(admin_token);
    }

    @Override
    synchronized public Account getAccount(Token admin_token, Integer account_id) throws AccountExistenceException, SessionInvalidException, SessionExpiredException, SessionCorruptedException, RuntimeException {
        return this.delegate.getAccount(admin_token, account_id);
    }

    @Override
    synchronized public Account getAccount(Token admin_token, String account_username) throws AccountExistenceException, SessionInvalidException, SessionExpiredException, SessionCorruptedException, RuntimeException {
        return this.delegate.getAccount(admin_token, account_username);
    }

    @Override
    public List<AccountType> getAccountTypes(Token admin_token) throws SessionInvalidException, SessionExpiredException, SessionCorruptedException, RuntimeException {
        return this.delegate.getAccountTypes(admin_token);
    }

    @Override
    public List<Building> getBuildings(Token admin_token) throws FailedRecordFetch, SessionExpiredException, SessionInvalidException, SessionCorruptedException {
        return this.delegate.getBuildings(admin_token);
    }

    @Override
    public List<Floor> getFloors(Token admin_token, Building building) throws FailedRecordFetch, SessionExpiredException, SessionInvalidException, SessionCorruptedException {
        return this.delegate.getFloors(admin_token, building);
    }

    @Override
    public List<Room> getRooms(Token admin_token, Floor floor) throws FailedRecordFetch, SessionExpiredException, SessionInvalidException, SessionCorruptedException {
        return this.delegate.getRooms(admin_token, floor);
    }

    @Override
    public List<Usage<RoomPrice, Integer>> getRoomPrices(Token admin_token, Room room) throws FailedRecordFetch, SessionExpiredException, SessionInvalidException, SessionCorruptedException {
        return this.delegate.getRoomPrices(admin_token, room);
    }

    @Override
    public Usage<RoomPrice, Integer> getMostRecentRoomPrice(Token admin_token, Room room) throws IncompleteRecord, FailedRecordFetch, SessionExpiredException, SessionInvalidException, SessionCorruptedException {
        return this.delegate.getMostRecentRoomPrice(admin_token, room);
    }

    @Override
    public List<Usage<RoomCategory, Room>> getRoomCategories(Token admin_token) throws FailedRecordFetch, SessionExpiredException, SessionInvalidException, SessionCorruptedException {
        return this.delegate.getRoomCategories(admin_token);
    }

    @Override
    public DetailedRoom getRoomDetails(Token admin_token, Room room) throws FailedRecordFetch, SessionExpiredException, SessionInvalidException, SessionCorruptedException {
        return this.delegate.getRoomDetails(admin_token, room);
    }

    @Override
    public void add(Token admin_token, Building building) throws FailedRecordWrite, SessionExpiredException, SessionInvalidException, SessionCorruptedException {
        this.delegate.add(admin_token, building);
    }

    @Override
    public void add(Token admin_token, Floor floor) throws FailedRecordWrite, SessionExpiredException, SessionInvalidException, SessionCorruptedException {
        this.delegate.add(admin_token, floor);
    }

    @Override
    public void add(Token admin_token, Room room, RoomPrice price, RoomFixtures fixtures) throws FailedRecordWrite, SessionExpiredException, SessionInvalidException, SessionCorruptedException {
        this.delegate.add(admin_token, room, price, fixtures);
    }

    @Override
    public RoomPrice add(Token admin_token, RoomPrice roomPrice) throws FailedRecordWrite, FailedRecordFetch, SessionExpiredException, SessionInvalidException, SessionCorruptedException {
        return this.delegate.add(admin_token, roomPrice);
    }

    @Override
    public RoomCategory add(Token admin_token, RoomCategory roomCategory) throws FailedRecordWrite, FailedRecordFetch, SessionExpiredException, SessionInvalidException, SessionCorruptedException {
        return this.delegate.add(admin_token, roomCategory);
    }

    @Override
    public void update(Token admin_token, Building building) throws FailedRecordUpdate, SessionExpiredException, SessionInvalidException, SessionCorruptedException {
        this.delegate.update(admin_token, building);
    }

    @Override
    public void update(Token admin_token, Floor floor) throws FailedRecordUpdate, SessionExpiredException, SessionInvalidException, SessionCorruptedException {
        this.delegate.update(admin_token, floor);
    }

    @Override
    public void update(Token admin_token, Room room, RoomPrice price) throws FailedRecordUpdate, SessionExpiredException, SessionInvalidException, SessionCorruptedException {
        this.delegate.update(admin_token, room, price);
    }

    @Override
    public boolean remove(Token admin_token, Building building) throws FailedRecordWrite, SessionExpiredException, SessionInvalidException, SessionCorruptedException {
        return this.delegate.remove(admin_token, building);
    }

    @Override
    public boolean remove(Token admin_token, Floor floor) throws FailedRecordWrite, SessionExpiredException, SessionInvalidException, SessionCorruptedException {
        return this.delegate.remove(admin_token, floor);
    }

    @Override
    public boolean remove(Token admin_token, Room room) throws FailedRecordWrite, SessionExpiredException, SessionInvalidException, SessionCorruptedException {
        return this.delegate.remove(admin_token, room);
    }

    @Override
    public boolean remove(Token admin_token, RoomPrice roomPrice) throws FailedRecordWrite, SessionExpiredException, SessionInvalidException, SessionCorruptedException {
        return this.delegate.remove(admin_token, roomPrice);
    }

    @Override
    public boolean remove(Token admin_token, RoomCategory roomCategory) throws FailedRecordWrite, SessionExpiredException, SessionInvalidException, SessionCorruptedException {
        return this.delegate.remove(admin_token, roomCategory);
    }

    @Override
    public List<Usage<Membership, Integer>> getMemberships(Token admin_token) throws FailedRecordFetch, SessionExpiredException, SessionInvalidException, SessionCorruptedException {
        return this.delegate.getMemberships(admin_token);
    }

    @Override
    public List<Discount> getDiscounts(Token admin_token) throws FailedRecordFetch, SessionExpiredException, SessionInvalidException, SessionCorruptedException {
        return this.delegate.getDiscounts(admin_token);
    }

    @Override
    public Membership add(Token admin_token, Membership membership) throws FailedRecordWrite, SessionExpiredException, SessionInvalidException, SessionCorruptedException {
        return this.delegate.add(admin_token, membership);
    }

    @Override
    public boolean remove(Token admin_token, Membership membership) throws FailedRecordWrite, SessionExpiredException, SessionInvalidException, SessionCorruptedException {
        return this.delegate.remove(admin_token, membership);
    }

    @Override
    synchronized public boolean optimiseReservationDatabase(Token admin_token) throws SessionInvalidException, SessionExpiredException, SessionCorruptedException {
        return this.delegate.optimiseReservationDatabase(admin_token);
    }

    @Override
    synchronized public boolean optimiseUserAccountDatabase(Token admin_token) throws SessionInvalidException, SessionExpiredException, SessionCorruptedException {
        return this.delegate.optimiseUserAccountDatabase(admin_token);
    }
}
