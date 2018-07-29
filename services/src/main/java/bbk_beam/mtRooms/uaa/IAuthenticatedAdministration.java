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

public interface IAuthenticatedAdministration {
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
    void createNewAccount(Token admin_token, SessionType account_type, String username, String password) throws SessionInvalidException, SessionExpiredException, SessionCorruptedException, AccountExistenceException, RuntimeException;

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
    void updateAccountPassword(Token admin_token, Integer account_id, String password) throws SessionInvalidException, SessionExpiredException, SessionCorruptedException, AccountExistenceException, AccountOverrideException, RuntimeException;

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
    void activateAccount(Token admin_token, Integer account_id) throws SessionInvalidException, SessionExpiredException, SessionCorruptedException, AccountExistenceException, AccountOverrideException, RuntimeException;

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
    void deactivateAccount(Token admin_token, Integer account_id) throws SessionInvalidException, SessionExpiredException, SessionCorruptedException, AccountExistenceException, AccountOverrideException, RuntimeException;

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
    void deleteAccount(Token admin_token, Integer account_id) throws SessionInvalidException, SessionExpiredException, SessionCorruptedException, AccountExistenceException, AccountOverrideException, RuntimeException;

    /**
     * @param admin_token Administrator session token
     * @return List of Account DTOs
     * @throws SessionInvalidException   when administrator session is not valid
     * @throws SessionExpiredException   when current administrator session has expired
     * @throws SessionCorruptedException when tracked and token expiry timestamps do not match for the token's ID
     * @throws RuntimeException          when non-standard failure occurred during account fetching from records
     */
    List<Account> getAccounts(Token admin_token) throws SessionInvalidException, SessionExpiredException, SessionCorruptedException, RuntimeException;

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
    Account getAccount(Token admin_token, Integer account_id) throws AccountExistenceException, SessionInvalidException, SessionExpiredException, SessionCorruptedException, RuntimeException;

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
    Account getAccount(Token admin_token, String account_username) throws AccountExistenceException, SessionInvalidException, SessionExpiredException, SessionCorruptedException, RuntimeException;

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
    List<AccountType> getAccountTypes(Token admin_token) throws SessionInvalidException, SessionExpiredException, SessionCorruptedException, RuntimeException;

    //--------------------------------------------------[ Real Estate ]-------------------------------------------------

    /**
     * Gets all buildings in real estate portfolio
     *
     * @param admin_token Administration session token
     * @return List of Building DTOs
     * @throws FailedRecordFetch         when error occurred during fetching of data from DB
     * @throws SessionExpiredException   when current administrator session has expired
     * @throws SessionInvalidException   when administrator session is not valid
     * @throws SessionCorruptedException when tracked and token expiry timestamps do not match for the token's ID
     */
    public List<Building> getBuildings(Token admin_token) throws FailedRecordFetch, SessionExpiredException, SessionInvalidException, SessionCorruptedException;

    /**
     * Gets all floors in a building
     *
     * @param admin_token Administration session token
     * @param building    Building DTO
     * @return List of Floor DTOs
     * @throws FailedRecordFetch         when error occurred during fetching of data from DB
     * @throws SessionExpiredException   when current administrator session has expired
     * @throws SessionInvalidException   when administrator session is not valid
     * @throws SessionCorruptedException when tracked and token expiry timestamps do not match for the token's ID
     */
    public List<Floor> getFloors(Token admin_token, Building building) throws FailedRecordFetch, SessionExpiredException, SessionInvalidException, SessionCorruptedException;

    /**
     * Gets all rooms ina floor
     *
     * @param admin_token Administration session token
     * @param floor       Floor DTO
     * @return List of Room DTOs
     * @throws FailedRecordFetch         when error occurred during fetching of data from DB
     * @throws SessionExpiredException   when current administrator session has expired
     * @throws SessionInvalidException   when administrator session is not valid
     * @throws SessionCorruptedException when tracked and token expiry timestamps do not match for the token's ID
     */
    public List<Room> getRooms(Token admin_token, Floor floor) throws FailedRecordFetch, SessionExpiredException, SessionInvalidException, SessionCorruptedException;

    /**
     * Gets all RoomPrice records for a Room with, if any, tied reservations
     *
     * @param admin_token Administration session token
     * @param room        Room DTO
     * @return List of RoomPrice DTOs with Usage
     * @throws FailedRecordFetch         when error occurred during fetching of data from DB
     * @throws SessionExpiredException   when current administrator session has expired
     * @throws SessionInvalidException   when administrator session is not valid
     * @throws SessionCorruptedException when tracked and token expiry timestamps do not match for the token's ID
     */
    public List<Usage<RoomPrice, Integer>> getRoomPrices(Token admin_token, Room room) throws FailedRecordFetch, SessionExpiredException, SessionInvalidException, SessionCorruptedException;

    /**
     * Gets the most recent price for a room with, if any, tied reservations
     *
     * @param admin_token Administration session token
     * @param room        Room DTO
     * @return RoomPrice DTO with Usage
     * @throws IncompleteRecord        when Room is not associated with a RoomPrice
     * @throws FailedRecordFetch       when error occurred during fetching of data from DB
     * @throws SessionExpiredException when current administrator session has expired
     * @throws SessionInvalidException when administrator session is not valid
     */
    public Usage<RoomPrice, Integer> getMostRecentRoomPrice(Token admin_token, Room room) throws IncompleteRecord, FailedRecordFetch, SessionExpiredException, SessionInvalidException, SessionCorruptedException;

    /**
     * Gets all room categories on record
     *
     * @param admin_token Administration session token
     * @return List of RoomCategory DTOs with Usage
     * @throws FailedRecordFetch         when error occurred during fetching of data from DB
     * @throws SessionExpiredException   when current administrator session has expired
     * @throws SessionInvalidException   when administrator session is not valid
     * @throws SessionCorruptedException when tracked and token expiry timestamps do not match for the token's ID
     */
    public List<Usage<RoomCategory, Room>> getRoomCategories(Token admin_token) throws FailedRecordFetch, SessionExpiredException, SessionInvalidException, SessionCorruptedException;

    /**
     * Gets a room's detailed information
     *
     * @param admin_token Administration session token
     * @param room        Room DTO
     * @return DetailedRoom DTO
     * @throws FailedRecordFetch         when error occurred during fetching of data from DB
     * @throws SessionExpiredException   when current administrator session has expired
     * @throws SessionInvalidException   when administrator session is not valid
     * @throws SessionCorruptedException when tracked and token expiry timestamps do not match for the token's ID
     */
    public DetailedRoom getRoomDetails(Token admin_token, Room room) throws FailedRecordFetch, SessionExpiredException, SessionInvalidException, SessionCorruptedException;

    /**
     * Adds a new building to the records
     *
     * @param admin_token Administration session token
     * @param building    Building DTO
     * @throws FailedRecordWrite         when error occurred during writing of data to DB
     * @throws SessionExpiredException   when current administrator session has expired
     * @throws SessionInvalidException   when administrator session is not valid
     * @throws SessionCorruptedException when tracked and token expiry timestamps do not match for the token's ID
     */
    void add(Token admin_token, Building building) throws FailedRecordWrite, SessionExpiredException, SessionInvalidException, SessionCorruptedException;

    /**
     * Adds a new floor to the records
     *
     * @param admin_token Administration session token
     * @param floor       Floor DTO
     * @throws FailedRecordWrite         when error occurred during writing of data to DB
     * @throws SessionExpiredException   when current administrator session has expired
     * @throws SessionInvalidException   when administrator session is not valid
     * @throws SessionCorruptedException when tracked and token expiry timestamps do not match for the token's ID
     */
    void add(Token admin_token, Floor floor) throws FailedRecordWrite, SessionExpiredException, SessionInvalidException, SessionCorruptedException;

    /**
     * Adds a new room to the records
     *
     * @param admin_token Administration session token
     * @param room        Room DTO
     * @param fixtures    RoomFixtures DTO
     * @throws FailedRecordWrite         when error occurred during writing of data to DB
     * @throws SessionExpiredException   when current administrator session has expired
     * @throws SessionInvalidException   when administrator session is not valid
     * @throws SessionCorruptedException when tracked and token expiry timestamps do not match for the token's ID
     */
    void add(Token admin_token, Room room, RoomFixtures fixtures) throws FailedRecordWrite, SessionExpiredException, SessionInvalidException, SessionCorruptedException;

    /**
     * Adds a new room price to the records
     *
     * @param admin_token Administration session token
     * @param roomPrice   RoomPrice DTO
     * @return RoomPrice DTO of created/matching record
     * @throws FailedRecordWrite         when error occurred during writing of data to DB
     * @throws FailedRecordFetch         when created record could not be retrieved from DB
     * @throws SessionExpiredException   when current administrator session has expired
     * @throws SessionInvalidException   when administrator session is not valid
     * @throws SessionCorruptedException when tracked and token expiry timestamps do not match for the token's ID
     */
    RoomPrice add(Token admin_token, RoomPrice roomPrice) throws FailedRecordWrite, FailedRecordFetch, SessionExpiredException, SessionInvalidException, SessionCorruptedException;

    /**
     * Adds a new room category to the records
     *
     * @param admin_token  Administration session token
     * @param roomCategory RoomCategory DTO
     * @return RoomCategory DTO of created/matching record
     * @throws FailedRecordWrite         when error occurred during writing of data to DB
     * @throws FailedRecordFetch         when created record could not be retrieved from DB
     * @throws SessionExpiredException   when current administrator session has expired
     * @throws SessionInvalidException   when administrator session is not valid
     * @throws SessionCorruptedException when tracked and token expiry timestamps do not match for the token's ID
     */
    RoomCategory add(Token admin_token, RoomCategory roomCategory) throws FailedRecordWrite, FailedRecordFetch, SessionExpiredException, SessionInvalidException, SessionCorruptedException;

    /**
     * Updates a building's records
     *
     * @param admin_token Administration session token
     * @param building    Building DTO
     * @throws FailedRecordUpdate        when error occurred during update on data in DB
     * @throws SessionExpiredException   when current administrator session has expired
     * @throws SessionInvalidException   when administrator session is not valid
     * @throws SessionCorruptedException when tracked and token expiry timestamps do not match for the token's ID
     */
    void update(Token admin_token, Building building) throws FailedRecordUpdate, SessionExpiredException, SessionInvalidException, SessionCorruptedException;

    /**
     * Updates a floor's records
     * <p>Updated: Floor.description</p>
     *
     * @param admin_token Administration session token
     * @param floor       Floor DTO
     * @throws FailedRecordUpdate        when error occurred during update on data in DB
     * @throws SessionExpiredException   when current administrator session has expired
     * @throws SessionInvalidException   when administrator session is not valid
     * @throws SessionCorruptedException when tracked and token expiry timestamps do not match for the token's ID
     */
    void update(Token admin_token, Floor floor) throws FailedRecordUpdate, SessionExpiredException, SessionInvalidException, SessionCorruptedException;

    /**
     * Update a room in the records
     * <p>Updated: Room.description</p>
     *
     * @param admin_token Administration session token
     * @param room        Room DTO
     * @throws FailedRecordUpdate        when error occurred during update on data in DB
     * @throws SessionExpiredException   when current administrator session has expired
     * @throws SessionInvalidException   when administrator session is not valid
     * @throws SessionCorruptedException when tracked and token expiry timestamps do not match for the token's ID
     */
    void update(Token admin_token, Room room) throws FailedRecordUpdate, SessionExpiredException, SessionInvalidException, SessionCorruptedException;

    /**
     * Remove a building from the records
     *
     * @param admin_token Administration session token
     * @param building    Building DTO
     * @return Success
     * @throws FailedRecordWrite         when error occurred during removal of data in DB
     * @throws SessionExpiredException   when current administrator session has expired
     * @throws SessionInvalidException   when administrator session is not valid
     * @throws SessionCorruptedException when tracked and token expiry timestamps do not match for the token's ID
     */
    boolean remove(Token admin_token, Building building) throws FailedRecordWrite, SessionExpiredException, SessionInvalidException, SessionCorruptedException;

    /**
     * Removes a floor from the records
     *
     * @param admin_token Administration session token
     * @param floor       Floor DTO
     * @return Success
     * @throws FailedRecordWrite         when error occurred during removal of data in DB
     * @throws SessionExpiredException   when current administrator session has expired
     * @throws SessionInvalidException   when administrator session is not valid
     * @throws SessionCorruptedException when tracked and token expiry timestamps do not match for the token's ID
     */
    boolean remove(Token admin_token, Floor floor) throws FailedRecordWrite, SessionExpiredException, SessionInvalidException, SessionCorruptedException;

    /**
     * Removes a room from the records
     *
     * @param admin_token Administration session token
     * @param room        Room DTO
     * @return Success
     * @throws FailedRecordWrite         when error occurred during removal of data in DB
     * @throws SessionExpiredException   when current administrator session has expired
     * @throws SessionInvalidException   when administrator session is not valid
     * @throws SessionCorruptedException when tracked and token expiry timestamps do not match for the token's ID
     */
    boolean remove(Token admin_token, Room room) throws FailedRecordWrite, SessionExpiredException, SessionInvalidException, SessionCorruptedException;

    /**
     * Remove a room price untied to any reservations from the records
     *
     * @param admin_token Administration session token
     * @param roomPrice   RoomPrice DTO
     * @return Success
     * @throws FailedRecordWrite         when error occurred during removal of data in DB
     * @throws SessionExpiredException   when current administrator session has expired
     * @throws SessionInvalidException   when administrator session is not valid
     * @throws SessionCorruptedException when tracked and token expiry timestamps do not match for the token's ID
     */
    boolean remove(Token admin_token, RoomPrice roomPrice) throws FailedRecordWrite, SessionExpiredException, SessionInvalidException, SessionCorruptedException;

    /**
     * Remove a room category untied to any rooms from the records
     *
     * @param admin_token  Administration session token
     * @param roomCategory RoomCategory DTO
     * @return Success
     * @throws FailedRecordWrite         when error occurred during removal of data in DB
     * @throws SessionExpiredException   when current administrator session has expired
     * @throws SessionInvalidException   when administrator session is not valid
     * @throws SessionCorruptedException when tracked and token expiry timestamps do not match for the token's ID
     */
    boolean remove(Token admin_token, RoomCategory roomCategory) throws FailedRecordWrite, SessionExpiredException, SessionInvalidException, SessionCorruptedException;

    //--------------------------------------------------[ Memberships ]-------------------------------------------------

    /**
     * Gets the Membership records and associated DiscountCategory + Discount entries
     *
     * @param admin_token Administration session token
     * @return List of Membership records with Usage (customer IDs)
     * @throws FailedRecordFetch         when error occurred during fetching of data from DB
     * @throws SessionExpiredException   when current administrator session has expired
     * @throws SessionInvalidException   when administrator session is not valid
     * @throws SessionCorruptedException when tracked and token expiry timestamps do not match for the token's ID
     */
    List<Usage<Membership, Integer>> getMemberships(Token admin_token) throws FailedRecordFetch, SessionExpiredException, SessionInvalidException, SessionCorruptedException;

    /**
     * Gets the Discount categories and rates records
     *
     * @param admin_token Administration session token
     * @return List of Discount DTOs
     * @throws FailedRecordFetch         when error occurred during fetching of data from DB
     * @throws SessionExpiredException   when current administrator session has expired
     * @throws SessionInvalidException   when administrator session is not valid
     * @throws SessionCorruptedException when tracked and token expiry timestamps do not match for the token's ID
     */
    List<Discount> getDiscounts(Token admin_token) throws FailedRecordFetch, SessionExpiredException, SessionInvalidException, SessionCorruptedException;

    /**
     * Adds a Membership to the records
     *
     * @param admin_token Administration session token
     * @param membership  Membership DTO
     * @return Membership DTO of the added record
     * @throws FailedRecordWrite         when error occurred during writing of data to DB
     * @throws SessionExpiredException   when current administrator session has expired
     * @throws SessionInvalidException   when administrator session is not valid
     * @throws SessionCorruptedException when tracked and token expiry timestamps do not match for the token's ID
     */
    Membership add(Token admin_token, Membership membership) throws FailedRecordWrite, SessionExpiredException, SessionInvalidException, SessionCorruptedException;

    /**
     * Removes a Membership from the records
     *
     * @param admin_token Administration session token
     * @param membership  Membership DTO
     * @return Success
     * @throws FailedRecordWrite         when error occurred during writing of data to DB
     * @throws SessionExpiredException   when current administrator session has expired
     * @throws SessionInvalidException   when administrator session is not valid
     * @throws SessionCorruptedException when tracked and token expiry timestamps do not match for the token's ID
     */
    boolean remove(Token admin_token, Membership membership) throws FailedRecordWrite, SessionExpiredException, SessionInvalidException, SessionCorruptedException;

    //-------------------------------------------------[ Database tools ]-----------------------------------------------

    /**
     * Optimises the reservation database
     *
     * @param admin_token Administrator session token
     * @return Success
     * @throws SessionInvalidException   when administrator session is not valid
     * @throws SessionExpiredException   when current administrator session has expired
     * @throws SessionCorruptedException when tracked and token expiry timestamps do not match for the token's ID
     */
    boolean optimiseReservationDatabase(Token admin_token) throws SessionInvalidException, SessionExpiredException, SessionCorruptedException;

    /**
     * Optimises the user account database
     *
     * @param admin_token Administrator session token
     * @return Success
     * @throws SessionInvalidException   when administrator session is not valid
     * @throws SessionExpiredException   when current administrator session has expired
     * @throws SessionCorruptedException when tracked and token expiry timestamps do not match for the token's ID
     */
    boolean optimiseUserAccountDatabase(Token admin_token) throws SessionInvalidException, SessionExpiredException, SessionCorruptedException;
}
