package bbk_beam.mtRooms.network;

import bbk_beam.mtRooms.admin.authentication.Token;
import bbk_beam.mtRooms.admin.dto.Account;
import bbk_beam.mtRooms.admin.dto.AccountType;
import bbk_beam.mtRooms.admin.dto.Usage;
import bbk_beam.mtRooms.admin.exception.*;
import bbk_beam.mtRooms.db.session.SessionType;
import bbk_beam.mtRooms.network.exception.Unauthorised;
import bbk_beam.mtRooms.reservation.dto.*;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface IRmiAdministrationServices extends Remote {
    /**
     * Creates a new account
     *
     * @param admin_token  Administrator session token
     * @param account_type Account type to create
     * @param username     Username of the account to create
     * @param password     Password of the account to create
     * @throws Unauthorised              when client is not authorised to access the resource
     * @throws AccountExistenceException when account with same name exists already
     * @throws RuntimeException          when non-standard failure occurred during account creation in records
     * @throws RemoteException           when network issues occur during the remote call
     */
    void createNewAccount(Token admin_token, SessionType account_type, String username, String password) throws Unauthorised, AccountExistenceException, RuntimeException, RemoteException;

    /**
     * Updates existing account's password
     *
     * @param admin_token Administrator or User session token
     * @param account_id  ID of account to update
     * @param password    New password
     * @throws Unauthorised              when client is not authorised to access the resource
     * @throws AccountExistenceException when account does not exist in the records
     * @throws AccountOverrideException  when new password is the same as old one
     * @throws RuntimeException          when non-standard failure occurred during account creation in records
     * @throws RemoteException           when network issues occur during the remote call
     */
    void updateAccountPassword(Token admin_token, Integer account_id, String password) throws Unauthorised, AccountExistenceException, AccountOverrideException, RuntimeException, RemoteException;

    /**
     * Activates an existing account
     *
     * @param admin_token Administrator session token
     * @param account_id  ID of account to activate
     * @throws Unauthorised              when client is not authorised to access the resource
     * @throws AccountExistenceException when account does not exist in the records
     * @throws AccountOverrideException  when account to activate is the current logged-in administrator's
     * @throws RuntimeException          when non-standard failure occurred during account activation
     * @throws RemoteException           when network issues occur during the remote call
     */
    void activateAccount(Token admin_token, Integer account_id) throws Unauthorised, AccountExistenceException, AccountOverrideException, RuntimeException, RemoteException;

    /**
     * Deactivates an existing account
     *
     * @param admin_token Administrator session token
     * @param account_id  ID of account to deactivate
     * @throws Unauthorised              when client is not authorised to access the resource
     * @throws AccountExistenceException when account does not exist in the records
     * @throws AccountOverrideException  when account to deactivates is the current logged-in administrator's
     * @throws RuntimeException          when non-standard failure occurred during account deactivation
     * @throws RemoteException           when network issues occur during the remote call
     */
    void deactivateAccount(Token admin_token, Integer account_id) throws Unauthorised, AccountExistenceException, AccountOverrideException, RuntimeException, RemoteException;

    /**
     * Deletes an existing  account record
     *
     * @param admin_token Administrator session token
     * @param account_id  ID of account to delete
     * @throws Unauthorised              when client is not authorised to access the resource
     * @throws AccountExistenceException when account to delete does not exist in the records
     * @throws AccountOverrideException  when account to delete is the current logged-in administrator's
     * @throws RuntimeException          when non-standard failure occurred during account removal from records
     * @throws RemoteException           when network issues occur during the remote call
     */
    void deleteAccount(Token admin_token, Integer account_id) throws Unauthorised, AccountExistenceException, AccountOverrideException, RuntimeException, RemoteException;

    /**
     * @param admin_token Administrator session token
     * @return List of Account DTOs
     * @throws Unauthorised     when client is not authorised to access the resource
     * @throws RuntimeException when non-standard failure occurred during account fetching from records
     * @throws RemoteException  when network issues occur during the remote call
     */
    List<Account> getAccounts(Token admin_token) throws Unauthorised, RuntimeException, RemoteException;

    /**
     * Gets the records for an account
     *
     * @param admin_token Administrator session token
     * @param account_id  Account ID
     * @return Account DTO
     * @throws Unauthorised              when client is not authorised to access the resource
     * @throws AccountExistenceException when account does not exist in records
     * @throws RuntimeException          when non-standard failure occurred during account fetching from records
     * @throws RemoteException           when network issues occur during the remote call
     */
    Account getAccount(Token admin_token, Integer account_id) throws AccountExistenceException, Unauthorised, RuntimeException, RemoteException;

    /**
     * Gets the records for an account
     *
     * @param admin_token      Administrator session token
     * @param account_username Account username
     * @return Account DTO
     * @throws Unauthorised              when client is not authorised to access the resource
     * @throws AccountExistenceException when account does not exist in records
     * @throws RuntimeException          when non-standard failure occurred during account fetching from records
     * @throws RemoteException           when network issues occur during the remote call
     */
    Account getAccount(Token admin_token, String account_username) throws AccountExistenceException, Unauthorised, RuntimeException, RemoteException;

    /**
     * Gets the user account types
     *
     * @param admin_token Administrator session token
     * @return List of AccountType DTOs
     * @throws Unauthorised     when client is not authorised to access the resource
     * @throws RuntimeException when non-standard failure occurred during account fetching from records
     * @throws RemoteException  when network issues occur during the remote call
     */
    List<AccountType> getAccountTypes(Token admin_token) throws Unauthorised, RuntimeException, RemoteException;

    /**
     * Gets all buildings in real estate portfolio
     *
     * @param admin_token Administration session token
     * @return List of Building DTOs
     * @throws FailedRecordFetch when error occurred during fetching of data from DB
     * @throws Unauthorised      when client is not authorised to access the resource
     * @throws RemoteException   when network issues occur during the remote call
     */
    public List<Building> getBuildings(Token admin_token) throws FailedRecordFetch, Unauthorised, RemoteException;

    /**
     * Gets all floors in a building
     *
     * @param admin_token Administration session token
     * @param building    Building DTO
     * @return List of Floor DTOs
     * @throws FailedRecordFetch when error occurred during fetching of data from DB
     * @throws Unauthorised      when client is not authorised to access the resource
     * @throws RemoteException   when network issues occur during the remote call
     */
    public List<Floor> getFloors(Token admin_token, Building building) throws FailedRecordFetch, Unauthorised, RemoteException;

    /**
     * Gets all rooms ina floor
     *
     * @param admin_token Administration session token
     * @param floor       Floor DTO
     * @return List of Room DTOs
     * @throws FailedRecordFetch when error occurred during fetching of data from DB
     * @throws Unauthorised      when client is not authorised to access the resource
     * @throws RemoteException   when network issues occur during the remote call
     */
    public List<Room> getRooms(Token admin_token, Floor floor) throws FailedRecordFetch, Unauthorised, RemoteException;

    /**
     * Gets all RoomPrice records for a Room with, if any, tied reservations
     *
     * @param admin_token Administration session token
     * @param room        Room DTO
     * @return List of RoomPrice DTOs with Usage
     * @throws FailedRecordFetch when error occurred during fetching of data from DB
     * @throws Unauthorised      when client is not authorised to access the resource
     * @throws RemoteException   when network issues occur during the remote call
     */
    public List<Usage<RoomPrice, Integer>> getRoomPrices(Token admin_token, Room room) throws FailedRecordFetch, Unauthorised, RemoteException;

    /**
     * Gets all room categories on record
     *
     * @param admin_token Administration session token
     * @return List of RoomCategory DTOs with Usage
     * @throws FailedRecordFetch when error occurred during fetching of data from DB
     * @throws Unauthorised      when client is not authorised to access the resource
     * @throws RemoteException   when network issues occur during the remote call
     */
    public List<Usage<RoomCategory, Room>> getRoomCategories(Token admin_token) throws FailedRecordFetch, Unauthorised, RemoteException;

    /**
     * Gets a room's detailed information
     *
     * @param admin_token Administration session token
     * @param room        Room DTO
     * @return DetailedRoom DTO
     * @throws FailedRecordFetch when error occurred during fetching of data from DB
     * @throws Unauthorised      when client is not authorised to access the resource
     * @throws RemoteException   when network issues occur during the remote call
     */
    public DetailedRoom getRoomDetails(Token admin_token, Room room) throws FailedRecordFetch, Unauthorised, RemoteException;

    /**
     * Adds a new building to the records
     *
     * @param admin_token Administration session token
     * @param building    Building DTO
     * @throws FailedRecordWrite when error occurred during writing of data to DB
     * @throws Unauthorised      when client is not authorised to access the resource
     * @throws RemoteException   when network issues occur during the remote call
     */
    void add(Token admin_token, Building building) throws FailedRecordWrite, Unauthorised, RemoteException;

    /**
     * Adds a new floor to the records
     *
     * @param admin_token Administration session token
     * @param floor       Floor DTO
     * @throws FailedRecordWrite when error occurred during writing of data to DB
     * @throws Unauthorised      when client is not authorised to access the resource
     * @throws RemoteException   when network issues occur during the remote call
     */
    void add(Token admin_token, Floor floor) throws FailedRecordWrite, Unauthorised, RemoteException;

    /**
     * Adds a new room to the records
     *
     * @param admin_token Administration session token
     * @param room        Room DTO
     * @param fixtures    RoomFixtures DTO
     * @throws FailedRecordWrite when error occurred during writing of data to DB
     * @throws Unauthorised      when client is not authorised to access the resource
     * @throws RemoteException   when network issues occur during the remote call
     */
    void add(Token admin_token, Room room, RoomFixtures fixtures) throws FailedRecordWrite, Unauthorised, RemoteException;

    /**
     * Adds a new room price to the records
     *
     * @param admin_token Administration session token
     * @param roomPrice   RoomPrice DTO
     * @return RoomPrice DTO of created/matching record
     * @throws FailedRecordWrite when error occurred during writing of data to DB
     * @throws FailedRecordFetch when created record could not be retrieved from DB
     * @throws Unauthorised      when client is not authorised to access the resource
     * @throws RemoteException   when network issues occur during the remote call
     */
    RoomPrice add(Token admin_token, RoomPrice roomPrice) throws FailedRecordWrite, FailedRecordFetch, Unauthorised, RemoteException;

    /**
     * Adds a new room category to the records
     *
     * @param admin_token  Administration session token
     * @param roomCategory RoomCategory DTO
     * @return RoomCategory DTO of created/matching record
     * @throws FailedRecordWrite when error occurred during writing of data to DB
     * @throws FailedRecordFetch when created record could not be retrieved from DB
     * @throws Unauthorised      when client is not authorised to access the resource
     * @throws RemoteException   when network issues occur during the remote call
     */
    RoomCategory add(Token admin_token, RoomCategory roomCategory) throws FailedRecordWrite, FailedRecordFetch, Unauthorised, RemoteException;

    /**
     * Updates a building's records
     *
     * @param admin_token Administration session token
     * @param building    Building DTO
     * @throws FailedRecordUpdate when error occurred during update on data in DB
     * @throws Unauthorised       when client is not authorised to access the resource
     * @throws RemoteException    when network issues occur during the remote call
     */
    void update(Token admin_token, Building building) throws FailedRecordUpdate, Unauthorised, RemoteException;

    /**
     * Updates a floor's records
     * <p>Updated: Floor.description</p>
     *
     * @param admin_token Administration session token
     * @param floor       Floor DTO
     * @throws FailedRecordUpdate when error occurred during update on data in DB
     * @throws Unauthorised       when client is not authorised to access the resource
     * @throws RemoteException    when network issues occur during the remote call
     */
    void update(Token admin_token, Floor floor) throws FailedRecordUpdate, Unauthorised, RemoteException;

    /**
     * Update a room in the records
     * <p>Updated: Room.description</p>
     *
     * @param admin_token Administration session token
     * @param room        Room DTO
     * @throws FailedRecordUpdate when error occurred during update on data in DB
     * @throws Unauthorised       when client is not authorised to access the resource
     * @throws RemoteException    when network issues occur during the remote call
     */
    void update(Token admin_token, Room room) throws FailedRecordUpdate, Unauthorised, RemoteException;

    /**
     * Remove a building from the records
     *
     * @param admin_token Administration session token
     * @param building    Building DTO
     * @return Success
     * @throws FailedRecordWrite when error occurred during removal of data in DB
     * @throws Unauthorised      when client is not authorised to access the resource
     * @throws RemoteException   when network issues occur during the remote call
     */
    boolean remove(Token admin_token, Building building) throws FailedRecordWrite, Unauthorised, RemoteException;

    /**
     * Removes a floor from the records
     *
     * @param admin_token Administration session token
     * @param floor       Floor DTO
     * @return Success
     * @throws FailedRecordWrite when error occurred during removal of data in DB
     * @throws Unauthorised      when client is not authorised to access the resource
     * @throws RemoteException   when network issues occur during the remote call
     */
    boolean remove(Token admin_token, Floor floor) throws FailedRecordWrite, Unauthorised, RemoteException;

    /**
     * Removes a room from the records
     *
     * @param admin_token Administration session token
     * @param room        Room DTO
     * @return Success
     * @throws FailedRecordWrite when error occurred during removal of data in DB
     * @throws Unauthorised      when client is not authorised to access the resource
     * @throws RemoteException   when network issues occur during the remote call
     */
    boolean remove(Token admin_token, Room room) throws FailedRecordWrite, Unauthorised, RemoteException;

    /**
     * Remove a room price untied to any reservations from the records
     *
     * @param admin_token Administration session token
     * @param roomPrice   RoomPrice DTO
     * @return Success
     * @throws FailedRecordWrite when error occurred during removal of data in DB
     * @throws Unauthorised      when client is not authorised to access the resource
     * @throws RemoteException   when network issues occur during the remote call
     */
    boolean remove(Token admin_token, RoomPrice roomPrice) throws FailedRecordWrite, Unauthorised, RemoteException;

    /**
     * Remove a room category untied to any rooms from the records
     *
     * @param admin_token  Administration session token
     * @param roomCategory RoomCategory DTO
     * @return Success
     * @throws FailedRecordWrite when error occurred during removal of data in DB
     * @throws Unauthorised      when client is not authorised to access the resource
     * @throws RemoteException   when network issues occur during the remote call
     */
    boolean remove(Token admin_token, RoomCategory roomCategory) throws FailedRecordWrite, Unauthorised, RemoteException;

    /**
     * Gets the Membership records and associated DiscountCategory + Discount entries
     *
     * @param admin_token Administration session token
     * @return List of Membership records with Usage (customer IDs)
     * @throws FailedRecordFetch when error occurred during fetching of data from DB
     * @throws Unauthorised      when client is not authorised to access the resource
     * @throws RemoteException   when network issues occur during the remote call
     */
    List<Usage<Membership, Integer>> getMemberships(Token admin_token) throws FailedRecordFetch, Unauthorised, RemoteException;

    /**
     * Gets the Discount categories and rates records
     *
     * @param admin_token Administration session token
     * @return List of Discount DTOs
     * @throws FailedRecordFetch when error occurred during fetching of data from DB
     * @throws Unauthorised      when client is not authorised to access the resource
     * @throws RemoteException   when network issues occur during the remote call
     */
    List<Discount> getDiscounts(Token admin_token) throws FailedRecordFetch, Unauthorised, RemoteException;

    /**
     * Adds a Membership to the records
     *
     * @param admin_token Administration session token
     * @param membership  Membership DTO
     * @return Membership DTO of the added record
     * @throws FailedRecordWrite when error occurred during writing of data to DB
     * @throws Unauthorised      when client is not authorised to access the resource
     * @throws RemoteException   when network issues occur during the remote call
     */
    Membership add(Token admin_token, Membership membership) throws FailedRecordWrite, Unauthorised, RemoteException;

    /**
     * Removes a Membership from the records
     *
     * @param admin_token Administration session token
     * @param membership  Membership DTO
     * @return Success
     * @throws FailedRecordWrite when error occurred during writing of data to DB
     * @throws Unauthorised      when client is not authorised to access the resource
     * @throws RemoteException   when network issues occur during the remote call
     */
    boolean remove(Token admin_token, Membership membership) throws FailedRecordWrite, Unauthorised, RemoteException;

    /**
     * Optimises the reservation database
     *
     * @param admin_token Administrator session token
     * @return Success
     * @throws Unauthorised    when client is not authorised to access the resource
     * @throws RemoteException when network issues occur during the remote call
     */
    boolean optimiseReservationDatabase(Token admin_token) throws Unauthorised, RemoteException;

    /**
     * Optimises the user account database
     *
     * @param admin_token Administrator session token
     * @return Success
     * @throws Unauthorised    when client is not authorised to access the resource
     * @throws RemoteException when network issues occur during the remote call
     */
    boolean optimiseUserAccountDatabase(Token admin_token) throws Unauthorised, RemoteException;
}
