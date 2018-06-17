package bbk_beam.mtRooms.admin.administration;

import bbk_beam.mtRooms.admin.administration.maintenance.MembershipAdministration;
import bbk_beam.mtRooms.admin.administration.maintenance.RealEstateAdministration;
import bbk_beam.mtRooms.admin.administration.maintenance.ReservationDbMaintenance;
import bbk_beam.mtRooms.admin.administration.maintenance.UserAccAdministration;
import bbk_beam.mtRooms.admin.authentication.IAuthenticationSystem;
import bbk_beam.mtRooms.admin.authentication.Token;
import bbk_beam.mtRooms.admin.dto.Account;
import bbk_beam.mtRooms.admin.dto.AccountType;
import bbk_beam.mtRooms.admin.dto.Usage;
import bbk_beam.mtRooms.admin.exception.*;
import bbk_beam.mtRooms.db.IReservationDbAccess;
import bbk_beam.mtRooms.db.IUserAccDbAccess;
import bbk_beam.mtRooms.db.TimestampConverter;
import bbk_beam.mtRooms.db.exception.DbQueryException;
import bbk_beam.mtRooms.db.exception.SessionCorruptedException;
import bbk_beam.mtRooms.db.exception.SessionExpiredException;
import bbk_beam.mtRooms.db.exception.SessionInvalidException;
import bbk_beam.mtRooms.db.session.SessionType;
import bbk_beam.mtRooms.reservation.dto.*;
import eadjlib.datastructure.ObjectTable;
import eadjlib.logger.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.function.UnaryOperator;

public class AdminSession implements IAdminSession {
    private final Logger log = Logger.getLoggerInstance(AdminSession.class.getName());
    private UserAccAdministration administration;
    private ReservationDbMaintenance db_maintenance;
    private MembershipAdministration memberships;
    private RealEstateAdministration real_estate;
    private IAuthenticationSystem authenticator;

    /**
     * Checks the credential used for the admin session
     *
     * @param token Session token
     * @throws SessionExpiredException   when the session is expired
     * @throws SessionCorruptedException when tracked and token expiry timestamps do not match for the token's ID
     * @throws SessionInvalidException   when the session is invalid (not logged-in or not admin)
     */
    private void checkTokenValidity(Token token) throws SessionExpiredException, SessionCorruptedException, SessionInvalidException {
        try {
            this.administration.checkValidity(token);
            if (!this.authenticator.hasValidAccessRights(token, SessionType.ADMIN)) {
                log.log_Error("Token [", token.getSessionId(), "] not valid for administrative access.");
                throw new SessionInvalidException("Token [" + token.getSessionId() + "] not valid for administrative access.");
            }
        } catch (SessionExpiredException e) {
            log.log_Error("Token [", token.getSessionId(), "] could not be validated: EXPIRED.");
            throw new SessionExpiredException("Token [" + token.getSessionId() + "] expired.", e);
        } catch (SessionCorruptedException e) {
            log.log_Error("Token [", token.getSessionId(), "] could not be validated: CORRUPTED.");
            throw new SessionCorruptedException("Token [" + token.getSessionId() + "] corrupted.", e);
        } catch (SessionInvalidException e) {
            log.log_Error("Token [", token.getSessionId(), "] could not be validated: INVALID.");
            throw new SessionInvalidException("Token [" + token.getSessionId() + "] invalid.", e);
        }
    }

    /**
     * Constructor
     *
     * @param reservationDbAccess  IReservationDbAccess instance
     * @param userAccDbAccess      IUserAccDbAccess instance
     * @param authenticationSystem IAuthenticationSystem instance
     */
    public AdminSession(
            IReservationDbAccess reservationDbAccess,
            IUserAccDbAccess userAccDbAccess,
            IAuthenticationSystem authenticationSystem) {
        this.administration = new UserAccAdministration(userAccDbAccess);
        this.db_maintenance = new ReservationDbMaintenance(reservationDbAccess);
        this.memberships = new MembershipAdministration(reservationDbAccess);
        this.real_estate = new RealEstateAdministration(reservationDbAccess);
        this.authenticator = authenticationSystem;
    }

    /**
     * Constructor (injector used for testing)
     *
     * @param administration_module UserAccAdministration instance
     * @param maintenance_module    ReservationDbMaintenance instance
     * @param memberships_module    MembershipAdministration instance
     * @param real_estate_module    RealEstateAdministration instance
     * @param authenticationSystem  IAuthenticationSystem instance
     */
    public AdminSession(
            UserAccAdministration administration_module,
            ReservationDbMaintenance maintenance_module,
            MembershipAdministration memberships_module,
            RealEstateAdministration real_estate_module,
            IAuthenticationSystem authenticationSystem) {
        this.administration = administration_module;
        this.db_maintenance = maintenance_module;
        this.memberships = memberships_module;
        this.real_estate = real_estate_module;
        this.authenticator = authenticationSystem;
    }

    @Override
    public void createNewAccount(Token admin_token, SessionType account_type, String username, String password) throws SessionInvalidException, SessionExpiredException, SessionCorruptedException, AccountExistenceException, RuntimeException {
        try {
            checkTokenValidity(admin_token);
            this.administration.createNewAccount(account_type, username, password);
        } catch (FailedRecordUpdate e) {
            log.log_Fatal("Could not addUsage user account [id: ", username, "] to records.");
            throw new RuntimeException("Could not addUsage user account to records.", e);
        }
    }

    @Override
    public void updateAccountPassword(Token admin_token, Integer account_id, String password) throws SessionInvalidException, SessionExpiredException, SessionCorruptedException, AccountExistenceException, AccountOverrideException, RuntimeException {
        try {
            checkTokenValidity(admin_token);
            this.administration.updateAccountPassword(account_id, password);
        } catch (FailedRecordUpdate e) {
            log.log_Fatal("Could not update user account [id: ", account_id, "] in records.");
            throw new RuntimeException("Could not update user account in records.", e);
        }
    }

    @Override
    public void activateAccount(Token admin_token, Integer account_id) throws SessionInvalidException, SessionExpiredException, SessionCorruptedException, AccountExistenceException, AccountOverrideException, RuntimeException {
        try {
            checkTokenValidity(admin_token);
            if (this.administration.isSameAccount(admin_token, account_id)) {
                log.log_Warning("Detected attempt to re-activate current session account used for access!");
                throw new AccountOverrideException("Attempted to re-activate current session account.");
            } else {
                this.administration.activateAccount(account_id);
            }
        } catch (DbQueryException e) {
            log.log_Fatal("Could not activate user account [id: ", account_id, "] in records.");
            throw new RuntimeException("Could not activate user account in records.", e);
        }
    }

    @Override
    public void deactivateAccount(Token admin_token, Integer account_id) throws SessionInvalidException, SessionExpiredException, SessionCorruptedException, AccountExistenceException, AccountOverrideException, RuntimeException {
        try {
            checkTokenValidity(admin_token);
            if (this.administration.isSameAccount(admin_token, account_id)) {
                log.log_Error("Detected attempt to deactivate current session account used for access!");
                throw new AccountOverrideException("Attempted to deactivate current session account.");
            } else {
                this.administration.deactivateAccount(account_id);
            }
        } catch (DbQueryException e) {
            log.log_Fatal("Could not deactivate user account [id: ", account_id, "] in records.");
            throw new RuntimeException("Could not deactivate user account in records.", e);
        }
    }

    @Override
    public void deleteAccount(Token admin_token, Integer account_id) throws SessionInvalidException, SessionExpiredException, SessionCorruptedException, AccountExistenceException, AccountOverrideException, RuntimeException {
        try {
            checkTokenValidity(admin_token);
            if (this.administration.isSameAccount(admin_token, account_id)) {
                log.log_Error("Detected attempt to delete current session account used for access!");
                throw new AccountOverrideException("Attempted to delete current session account.");
            } else {
                this.administration.deleteAccount(account_id);
            }
        } catch (DbQueryException e) {
            log.log_Fatal("Could not delete user account [id: ", account_id, "] from records.");
            throw new RuntimeException("Could not delete user account from records.", e);
        }
    }

    @Override
    public List<Account> getAccounts(Token admin_token) throws SessionInvalidException, SessionExpiredException, SessionCorruptedException, RuntimeException {
        try {
            checkTokenValidity(admin_token);
            ObjectTable table = this.administration.getAccounts();
            List<Account> account_list = new ArrayList<>();
            for (int i = 1; i <= table.rowCount(); i++) {
                HashMap<String, Object> row = table.getRow(i);
                account_list.add(
                        new Account(
                                (Integer) row.get("id"),
                                (String) row.get("username"),
                                TimestampConverter.getDateObject((String) row.get("created")),
                                TimestampConverter.getDateObject((String) row.get("last_login")),
                                TimestampConverter.getDateObject((String) row.get("last_pwd_change")),
                                new AccountType(
                                        (Integer) row.get("type_id"),
                                        (String) row.get("description")
                                ),
                                ((Integer) row.get("active_state") != 0)
                        ));
            }
            return account_list;
        } catch (DbQueryException e) {
            log.log_Fatal("Could not fetch user accounts from records.");
            throw new RuntimeException("Could not fetch user accounts from records.", e);
        }
    }

    @Override
    public Account getAccount(Token admin_token, Integer account_id) throws AccountExistenceException, SessionInvalidException, SessionExpiredException, SessionCorruptedException, RuntimeException {
        try {
            checkTokenValidity(admin_token);
            ObjectTable table = this.administration.getAccount(account_id);
            if (!table.isEmpty()) {
                HashMap<String, Object> row = table.getRow(1);
                return new Account(
                        (Integer) row.get("id"),
                        (String) row.get("username"),
                        TimestampConverter.getDateObject((String) row.get("created")),
                        TimestampConverter.getDateObject((String) row.get("last_login")),
                        TimestampConverter.getDateObject((String) row.get("last_pwd_change")),
                        new AccountType(
                                (Integer) row.get("type_id"),
                                (String) row.get("description")
                        ),
                        ((Integer) row.get("active_state") != 0)
                );
            } else {
                log.log_Error("Account [", account_id, "] does not exist in records.");
                throw new AccountExistenceException("Account [" + account_id + "] does not exist in records.");
            }
        } catch (DbQueryException e) {
            log.log_Fatal("Could not fetch user account [id: ", account_id, "] from records.");
            throw new RuntimeException("Could not fetch user account from records.", e);
        }
    }

    @Override
    public Account getAccount(Token admin_token, String account_username) throws AccountExistenceException, SessionInvalidException, SessionExpiredException, SessionCorruptedException, RuntimeException {
        try {
            checkTokenValidity(admin_token);
            ObjectTable table = this.administration.getAccount(account_username);
            if (!table.isEmpty()) {
                HashMap<String, Object> row = table.getRow(1);
                return new Account(
                        (Integer) row.get("id"),
                        (String) row.get("username"),
                        TimestampConverter.getDateObject((String) row.get("created")),
                        TimestampConverter.getDateObject((String) row.get("last_login")),
                        TimestampConverter.getDateObject((String) row.get("last_pwd_change")),
                        new AccountType(
                                (Integer) row.get("type_id"),
                                (String) row.get("description")
                        ),
                        ((Integer) row.get("active_state") != 0)
                );
            } else {
                log.log_Error("No account with username '", account_username, "' exists in records.");
                throw new AccountExistenceException("No account with username '" + account_username + "' exists in records.");
            }
        } catch (DbQueryException e) {
            log.log_Fatal("Could not fetch user account [u/n: ", account_username, "] from records.");
            throw new RuntimeException("Could not fetch user account from records.", e);
        }
    }

    @Override
    public List<AccountType> getAccountTypes(Token admin_token) throws SessionInvalidException, SessionExpiredException, SessionCorruptedException, RuntimeException {
        try {
            checkTokenValidity(admin_token);
            List<AccountType> list = new ArrayList<>();
            ObjectTable table = this.administration.getAccountTypes();
            if (!table.isEmpty()) {
                for (int i = 1; i <= table.rowCount(); i++) {
                    HashMap<String, Object> row = table.getRow(i);
                    list.add(
                            new AccountType(
                                    (Integer) row.get("id"),
                                    (String) row.get("description")
                            )
                    );
                }
            }
            return list;
        } catch (DbQueryException e) {
            log.log_Fatal("Could not fetch user account types from records.");
            throw new RuntimeException("Could not fetch user account types from records.", e);
        }
    }

    @Override
    public List<Building> getBuildings(Token admin_token) throws FailedRecordFetch, SessionExpiredException, SessionInvalidException, SessionCorruptedException {
        try {
            checkTokenValidity(admin_token);
            List<Building> list = new ArrayList<>();
            ObjectTable table = this.real_estate.getBuildings(admin_token);
            if (!table.isEmpty()) {
                for (int i = 1; i <= table.rowCount(); i++) {
                    HashMap<String, Object> row = table.getRow(i);
                    list.add(
                            new Building(
                                    (Integer) row.get("id"),
                                    (String) row.get("name"),
                                    (String) row.get("address1"),
                                    (String) row.get("address2"),
                                    (String) row.get("postcode"),
                                    (String) row.get("city"),
                                    (String) row.get("country"),
                                    (String) row.get("telephone")
                            )
                    );
                }
            }
            return list;
        } catch (DbQueryException e) {
            log.log_Fatal("Could not fetch buildings from records.");
            throw new FailedRecordFetch("Could not fetch buildings from records.", e);
        }
    }

    @Override
    public List<Floor> getFloors(Token admin_token, Building building) throws FailedRecordFetch, SessionExpiredException, SessionInvalidException, SessionCorruptedException {
        try {
            checkTokenValidity(admin_token);
            List<Floor> list = new ArrayList<>();
            ObjectTable table = this.real_estate.getFloors(admin_token, building);
            if (!table.isEmpty()) {
                for (int i = 1; i <= table.rowCount(); i++) {
                    HashMap<String, Object> row = table.getRow(i);
                    list.add(
                            new Floor(
                                    (Integer) row.get("building_id"),
                                    (Integer) row.get("id"),
                                    (String) row.get("description")
                            )
                    );
                }
            }
            return list;
        } catch (DbQueryException e) {
            log.log_Fatal("Could not fetch floors for Building[", building.id(), "] from records.");
            throw new FailedRecordFetch("Could not fetch floors for Buildings[" + building.id() + "] from records.", e);
        }
    }

    @Override
    public List<Room> getRooms(Token admin_token, Floor floor) throws FailedRecordFetch, SessionExpiredException, SessionInvalidException, SessionCorruptedException {
        try {
            checkTokenValidity(admin_token);
            List<Room> list = new ArrayList<>();
            ObjectTable table = this.real_estate.getRooms(admin_token, floor);
            if (!table.isEmpty()) {
                for (int i = 1; i <= table.rowCount(); i++) {
                    HashMap<String, Object> row = table.getRow(i);
                    list.add(
                            new Room(
                                    (Integer) row.get("id"),
                                    (Integer) row.get("floor_id"),
                                    (Integer) row.get("building_id"),
                                    (Integer) row.get("room_category_id"),
                                    (String) row.get("description")
                            )
                    );
                }
            }
            return list;
        } catch (DbQueryException e) {
            log.log_Fatal("Could not fetch rooms from records for floor: ", floor);
            throw new FailedRecordFetch("Could not fetch rooms from records for floor: " + floor, e);
        }
    }

    @Override
    public List<Usage<RoomPrice, Integer>> getRoomPrices(Token admin_token, Room room) throws FailedRecordFetch, SessionExpiredException, SessionInvalidException, SessionCorruptedException {
        try {
            checkTokenValidity(admin_token);
            List<Usage<RoomPrice, Integer>> list = new ArrayList<>();
            Usage<RoomPrice, Integer> roomPriceUsage = null;
            ObjectTable table = this.real_estate.getRoomPrices(admin_token, room);
            for (int i = 1; i <= table.rowCount(); i++) {
                HashMap<String, Object> row = table.getRow(i);
                Optional<Integer> reservation_id = Optional.ofNullable((Integer) row.get("reservation_id"));

                if (roomPriceUsage != null && roomPriceUsage.dto().id().equals(row.get("room_price_id")) && reservation_id.isPresent()) {
                    roomPriceUsage.addUsage(reservation_id.get());
                } else {
                    roomPriceUsage = new Usage<>(
                            new RoomPrice(
                                    (Integer) row.get("room_price_id"),
                                    (Double) row.get("price"),
                                    (Integer) row.get("year")
                            )
                    );
                    if (reservation_id.isPresent())
                        roomPriceUsage.addUsage(reservation_id.get());
                    list.add(roomPriceUsage);
                }
            }
            return list;
        } catch (DbQueryException e) {
            log.log_Fatal("Could not fetch prices from records for room: ", room);
            throw new FailedRecordFetch("Could not fetch prices from records for room: " + room, e);
        }
    }

    @Override
    public List<Usage<RoomCategory, Room>> getRoomCategories(Token admin_token) throws FailedRecordFetch, SessionExpiredException, SessionInvalidException, SessionCorruptedException {
        final UnaryOperator<Room> validateRoom = room -> {
            if (room.id() != null && room.floorID() != null && room.buildingID() != null && room.category() != null && room.description() != null)
                return room;
            else
                return null;
        };

        try {
            checkTokenValidity(admin_token);
            List<Usage<RoomCategory, Room>> list = new ArrayList<>();
            Usage<RoomCategory, Room> roomCategoryUsage = null;
            ObjectTable table = this.real_estate.getRoomCategories(admin_token);

            for (int i = 1; i <= table.rowCount(); i++) {
                HashMap<String, Object> row = table.getRow(i);

                Optional<Room> room = Optional.ofNullable(validateRoom.apply(
                        new Room(
                                (Integer) row.get("room_id"),
                                (Integer) row.get("floor_id"),
                                (Integer) row.get("building_id"),
                                (Integer) row.get("room_category_id"),
                                (String) row.get("room_description")
                        )
                ));

                Integer category_id = (Integer) row.get("room_category_id");

                if (roomCategoryUsage != null && roomCategoryUsage.dto().id().equals(category_id) && room.isPresent()) {
                    roomCategoryUsage.addUsage(room.get());
                } else {
                    roomCategoryUsage = new Usage<>(
                            new RoomCategory(
                                    category_id,
                                    (Integer) row.get("capacity"),
                                    (Integer) row.get("dimension")
                            )
                    );
                    if (room.isPresent())
                        roomCategoryUsage.addUsage(room.get());
                    list.add(roomCategoryUsage);
                }
            }
            return list;
        } catch (DbQueryException e) {
            log.log_Fatal("Could not fetch room categories from records.");
            throw new FailedRecordFetch("Could not fetch room categories from records.", e);
        }
    }

    @Override
    public DetailedRoom getRoomDetails(Token admin_token, Room room) throws FailedRecordFetch, SessionExpiredException, SessionInvalidException, SessionCorruptedException {
        try {
            checkTokenValidity(admin_token);
            ObjectTable table = this.real_estate.getRoomDetails(admin_token, room);
            HashMap<String, Object> row = table.getRow(1);
            return new DetailedRoom(
                    new Building(
                            (Integer) row.get("building_id"),
                            (String) row.get("building_name"),
                            (String) row.get("address1"),
                            (String) row.get("address2"),
                            (String) row.get("city"),
                            (String) row.get("postcode"),
                            (String) row.get("country"),
                            (String) row.get("telephone")
                    ),
                    new Floor(
                            (Integer) row.get("building_id"),
                            (Integer) row.get("floor_id"),
                            (String) row.get("floor_description")
                    ),
                    new Room(
                            (Integer) row.get("room_id"),
                            (Integer) row.get("floor_id"),
                            (Integer) row.get("building_id"),
                            (Integer) row.get("category_id"),
                            (String) row.get("room_description")
                    ),
                    new RoomCategory(
                            (Integer) row.get("category_id"),
                            (Integer) row.get("capacity"),
                            (Integer) row.get("dimension")
                    ),
                    new RoomFixtures(
                            (Integer) row.get("room_fixture_id"),
                            ((Integer) row.get("fixed_chairs") != 0),
                            ((Integer) row.get("catering_space") != 0),
                            ((Integer) row.get("whiteboard") != 0),
                            ((Integer) row.get("projector") != 0)
                    )
            );
        } catch (DbQueryException e) {
            log.log_Fatal("Could not fetch details for room: ", room);
            throw new FailedRecordFetch("Could not fetch details for room: " + room, e);
        }
    }

    @Override
    public void add(Token admin_token, Building building) throws FailedRecordWrite, SessionExpiredException, SessionInvalidException, SessionCorruptedException {
        try {
            checkTokenValidity(admin_token);
            this.real_estate.add(admin_token, building);
        } catch (DbQueryException e) {
            log.log_Error("Couldn't add building to records: ", building);
            throw new FailedRecordWrite("Couldn't add building to records: " + building, e);
        }
    }

    @Override
    public void add(Token admin_token, Floor floor) throws FailedRecordWrite, SessionExpiredException, SessionInvalidException, SessionCorruptedException {
        try {
            checkTokenValidity(admin_token);
            this.real_estate.add(admin_token, floor);
        } catch (DbQueryException e) {
            log.log_Error("Couldn't add floor to records: ", floor);
            throw new FailedRecordWrite("Couldn't add floor to records: " + floor, e);
        }
    }

    @Override
    public void add(Token admin_token, Room room, RoomFixtures fixtures) throws FailedRecordWrite, SessionExpiredException, SessionInvalidException, SessionCorruptedException {
        try {
            checkTokenValidity(admin_token);
            this.real_estate.add(admin_token, room, fixtures);
        } catch (DbQueryException e) {
            log.log_Error("Couldn't add room to records: ", room, " with ", fixtures);
            throw new FailedRecordWrite("Couldn't add room to records: " + room + " with " + fixtures, e);
        }
    }

    @Override
    public RoomPrice add(Token admin_token, RoomPrice roomPrice) throws FailedRecordWrite, FailedRecordFetch, SessionExpiredException, SessionInvalidException, SessionCorruptedException {
        try {
            checkTokenValidity(admin_token);
            ObjectTable table = this.real_estate.add(admin_token, roomPrice);
            if (!table.isEmpty()) {
                HashMap<String, Object> row = table.getRow(1);
                return new RoomPrice(
                        (Integer) row.get("id"),
                        (Double) row.get("price"),
                        (Integer) row.get("year")
                );
            } else {
                log.log_Error("Could not fetch back created RoomPrice entry: ", roomPrice);
                throw new FailedRecordFetch("Could not fetch back created RoomPrice entry: " + roomPrice);
            }
        } catch (DbQueryException e) {
            log.log_Error("Couldn't add room price to records: ", roomPrice);
            throw new FailedRecordWrite("Couldn't add room price to records: " + roomPrice, e);
        }
    }

    @Override
    public RoomCategory add(Token admin_token, RoomCategory roomCategory) throws FailedRecordWrite, FailedRecordFetch, SessionExpiredException, SessionInvalidException, SessionCorruptedException {
        try {
            checkTokenValidity(admin_token);
            ObjectTable table = this.real_estate.add(admin_token, roomCategory);
            if (!table.isEmpty()) {
                HashMap<String, Object> row = table.getRow(1);
                return new RoomCategory(
                        (Integer) row.get("id"),
                        (Integer) row.get("capacity"),
                        (Integer) row.get("dimension")
                );
            } else {
                log.log_Error("Could not fetch back created RoomCategory entry: ", roomCategory);
                throw new FailedRecordFetch("Could not fetch back created RoomCategory entry: " + roomCategory);
            }
        } catch (DbQueryException e) {
            log.log_Error("Couldn't add room category to records: ", roomCategory);
            throw new FailedRecordWrite("Couldn't add room category to records: " + roomCategory, e);
        }
    }

    @Override
    public void update(Token admin_token, Building building) throws FailedRecordUpdate, SessionExpiredException, SessionInvalidException, SessionCorruptedException {
        try {
            checkTokenValidity(admin_token);
            this.real_estate.update(admin_token, building);
        } catch (DbQueryException e) {
            log.log_Error("Failed to update record entry: ", building);
            throw new FailedRecordUpdate("Failed to update record entry: " + building, e);
        }
    }

    @Override
    public void update(Token admin_token, Floor floor) throws FailedRecordUpdate, SessionExpiredException, SessionInvalidException, SessionCorruptedException {
        try {
            checkTokenValidity(admin_token);
            this.real_estate.update(admin_token, floor);
        } catch (DbQueryException e) {
            log.log_Error("Failed to update record entry: ", floor);
            throw new FailedRecordUpdate("Failed to update record entry: " + floor, e);
        }
    }

    @Override
    public void update(Token admin_token, Room room) throws FailedRecordUpdate, SessionExpiredException, SessionInvalidException, SessionCorruptedException {
        try {
            checkTokenValidity(admin_token);
            this.real_estate.update(admin_token, room);
        } catch (DbQueryException e) {
            log.log_Error("Failed to update record entry: ", room);
            throw new FailedRecordUpdate("Failed to update record entry: " + room, e);
        }
    }

    @Override
    public boolean remove(Token admin_token, Building building) throws FailedRecordWrite, SessionExpiredException, SessionInvalidException, SessionCorruptedException {
        try {
            checkTokenValidity(admin_token);
            return this.real_estate.remove(admin_token, building);
        } catch (DbQueryException e) {
            log.log_Error("Failed to remove record entry: ", building);
            throw new FailedRecordWrite("Failed to remove record entry: " + building, e);
        }
    }

    @Override
    public boolean remove(Token admin_token, Floor floor) throws FailedRecordWrite, SessionExpiredException, SessionInvalidException, SessionCorruptedException {
        try {
            checkTokenValidity(admin_token);
            return this.real_estate.remove(admin_token, floor);
        } catch (DbQueryException e) {
            log.log_Error("Failed to remove record entry: ", floor);
            throw new FailedRecordWrite("Failed to remove record entry: " + floor, e);
        }
    }

    @Override
    public boolean remove(Token admin_token, Room room) throws FailedRecordWrite, SessionExpiredException, SessionInvalidException, SessionCorruptedException {
        try {
            checkTokenValidity(admin_token);
            return this.real_estate.remove(admin_token, room);
        } catch (DbQueryException e) {
            log.log_Error("Failed to remove record entry: ", room);
            throw new FailedRecordWrite("Failed to remove record entry: " + room, e);
        }
    }

    @Override
    public boolean remove(Token admin_token, RoomPrice roomPrice) throws FailedRecordWrite, SessionExpiredException, SessionInvalidException, SessionCorruptedException {
        try {
            checkTokenValidity(admin_token);
            return this.real_estate.remove(admin_token, roomPrice);
        } catch (DbQueryException e) {
            log.log_Error("Failed to remove record entry: ", roomPrice);
            throw new FailedRecordWrite("Failed to remove record entry: " + roomPrice, e);
        }
    }

    @Override
    public boolean remove(Token admin_token, RoomCategory roomCategory) throws FailedRecordWrite, SessionExpiredException, SessionInvalidException, SessionCorruptedException {
        try {
            checkTokenValidity(admin_token);
            return this.real_estate.remove(admin_token, roomCategory);
        } catch (DbQueryException e) {
            log.log_Error("Failed to remove record entry: ", roomCategory);
            throw new FailedRecordWrite("Failed to remove record entry: " + roomCategory, e);
        }
    }

    @Override
    public List<Usage<Membership, Integer>> getMemberships(Token admin_token) throws FailedRecordFetch, SessionExpiredException, SessionInvalidException, SessionCorruptedException {
        try {
            checkTokenValidity(admin_token);
            List<Usage<Membership, Integer>> list = new ArrayList<>();
            Usage<Membership, Integer> membershipUsage = null;
            ObjectTable table = this.memberships.getMemberships(admin_token);
            for (int i = 1; i <= table.rowCount(); i++) {
                HashMap<String, Object> row = table.getRow(i);
                Optional<Integer> reservation_id = Optional.ofNullable((Integer) row.get("customer_id"));

                if (membershipUsage != null && membershipUsage.dto().id().equals(row.get("membership_id")) && reservation_id.isPresent()) {
                    membershipUsage.addUsage(reservation_id.get());
                } else {
                    membershipUsage = new Usage<>(
                            new Membership(
                                    (Integer) row.get("membership_id"),
                                    (String) row.get("membership_description"),
                                    new Discount(
                                            (Integer) row.get("discount_id"),
                                            (Double) row.get("discount_rate"),
                                            new DiscountCategory(
                                                    (Integer) row.get("discount_category_id"),
                                                    (String) row.get("discount_category_description")
                                            )
                                    )
                            )
                    );
                    if (reservation_id.isPresent())
                        membershipUsage.addUsage(reservation_id.get());
                    list.add(membershipUsage);
                }
            }
            return list;
        } catch (DbQueryException e) {
            log.log_Fatal("Could not fetch Memberships from records.");
            throw new FailedRecordFetch("Could not fetch Memberships from records.", e);
        }
    }

    @Override
    public List<Discount> getDiscounts(Token admin_token) throws FailedRecordFetch, SessionExpiredException, SessionInvalidException, SessionCorruptedException {
        try {
            checkTokenValidity(admin_token);
            ObjectTable table = this.memberships.getDiscountCategories(admin_token);
            List<Discount> list = new ArrayList<>();
            for (int i = 1; i <= table.rowCount(); i++) {
                HashMap<String, Object> row = table.getRow(i);
                list.add(
                        new Discount(
                                (Integer) row.get("discount_id"),
                                (Double) row.get("discount_rate"),
                                new DiscountCategory(
                                        (Integer) row.get("discount_category_id"),
                                        (String) row.get("discount_category_description")
                                )
                        )
                );
            }
            return list;
        } catch (DbQueryException e) {
            log.log_Error("Could not fetch Discounts from records.");
            throw new FailedRecordFetch("Could not fetch Discounts from records.", e);
        }
    }

    @Override
    public Membership add(Token admin_token, Membership membership) throws FailedRecordWrite, SessionExpiredException, SessionInvalidException, SessionCorruptedException {
        try {
            return this.memberships.add(admin_token, membership);
        } catch (DbQueryException e) {
            log.log_Error("Couldn't add membership to records: ", membership);
            throw new FailedRecordWrite("Couldn't add membership to records: " + membership, e);
        }
    }

    @Override
    public boolean remove(Token admin_token, Membership membership) throws FailedRecordWrite, SessionExpiredException, SessionInvalidException, SessionCorruptedException {
        try {
            return this.memberships.remove(admin_token, membership);
        } catch (DbQueryException e) {
            log.log_Error("Failed to remove record entry: ", membership);
            throw new FailedRecordWrite("Failed to remove record entry: " + membership, e);
        }
    }

    @Override
    public boolean optimiseReservationDatabase(Token admin_token) throws SessionInvalidException, SessionExpiredException, SessionCorruptedException {
        try {
            checkTokenValidity(admin_token);
            this.db_maintenance.vacuumDatabase(admin_token);
            return true;
        } catch (DbQueryException e) {
            log.log_Error("Problem encountered when trying to optimise the reservation database.");
        }
        return false;
    }

    @Override
    public boolean optimiseUserAccountDatabase(Token admin_token) throws SessionInvalidException, SessionExpiredException, SessionCorruptedException {
        try {
            checkTokenValidity(admin_token);
            this.administration.optimiseDatabase();
            return true;
        } catch (DbQueryException e) {
            log.log_Error("Problem encountered when trying to optimise the user account database.");
        }
        return false;
    }
}