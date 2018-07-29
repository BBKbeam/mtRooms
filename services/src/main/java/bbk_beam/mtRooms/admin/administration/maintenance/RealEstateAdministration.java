package bbk_beam.mtRooms.admin.administration.maintenance;

import bbk_beam.mtRooms.admin.authentication.Token;
import bbk_beam.mtRooms.admin.exception.IncompleteRecord;
import bbk_beam.mtRooms.db.IReservationDbAccess;
import bbk_beam.mtRooms.db.TimestampConverter;
import bbk_beam.mtRooms.db.exception.DbQueryException;
import bbk_beam.mtRooms.db.exception.SessionExpiredException;
import bbk_beam.mtRooms.db.exception.SessionInvalidException;
import bbk_beam.mtRooms.reservation.dto.*;
import eadjlib.datastructure.ObjectTable;
import eadjlib.logger.Logger;

import java.sql.Date;
import java.time.Instant;
import java.util.HashMap;
import java.util.Optional;

public class RealEstateAdministration {
    private final Logger log = Logger.getLoggerInstance(RealEstateAdministration.class.getName());
    private IReservationDbAccess reservation_db_access;

    /**
     * Gets the record ID of a set of property for a room
     *
     * @param admin_token    Administration session token
     * @param fixed_chairs   Fixed chairs flag
     * @param catering_space Catering space flag
     * @param whiteboard     Whiteboard flag
     * @param projector      Projector flag
     * @return ID for set of property
     * @throws DbQueryException        when query failed
     * @throws SessionExpiredException when current administrator session has expired
     * @throws SessionInvalidException when administrator session is not valid
     */
    private Optional<Integer> getRoomFixturesID(Token admin_token, boolean fixed_chairs, boolean catering_space, boolean whiteboard, boolean projector) throws DbQueryException, SessionExpiredException, SessionInvalidException {
        String query = "SELECT RoomFixtures.id FROM RoomFixtures " +
                "WHERE fixed_chairs = " + (fixed_chairs ? 1 : 0) +
                " AND catering_space = " + (catering_space ? 1 : 0) +
                " AND whiteboard = " + (whiteboard ? 1 : 0) +
                " AND projector = " + (projector ? 1 : 0);
        ObjectTable table = this.reservation_db_access.pullFromDB(admin_token.getSessionId(), query);
        return (table.isEmpty() ? Optional.empty() : Optional.of(table.getInteger(1, 1)));
    }

    /**
     * Adds a new RoomFixtures record
     *
     * @param admin_token    Administration session token
     * @param fixed_chairs   Fixed chairs flag
     * @param catering_space Catering space flag
     * @param whiteboard     Whiteboard flag
     * @param projector      Projector flag
     * @return ID for created record
     * @throws DbQueryException        when query failed
     * @throws SessionExpiredException when current administrator session has expired
     * @throws SessionInvalidException when administrator session is not valid
     */
    private Integer addToRoomFixtures(Token admin_token, boolean fixed_chairs, boolean catering_space, boolean whiteboard, boolean projector) throws DbQueryException, SessionExpiredException, SessionInvalidException {
        String query = "INSERT INTO RoomFixtures( fixed_chairs, catering_space, whiteboard, projector ) VALUES ( " +
                (fixed_chairs ? 1 : 0) + ", " +
                (catering_space ? 1 : 0) + ", " +
                (whiteboard ? 1 : 0) + ", " +
                (projector ? 1 : 0) + " " +
                ")";
        this.reservation_db_access.pushToDB(admin_token.getSessionId(), query);
        Optional<Integer> id = getRoomFixturesID(admin_token, fixed_chairs, catering_space, whiteboard, projector);
        if (!id.isPresent()) {
            log.log_Error("Added new RoomFixture (", fixed_chairs, ",", catering_space, ", ", whiteboard, ", ", projector, ") but couldn't get its record ID back.");
            throw new DbQueryException("Problem occurred getting ID of newly created RoomFixture record.");
        } else {
            return id.get();
        }
    }

    /**
     * Adds a new Room record
     *
     * @param admin_token Administration session token
     * @param room        Room DTO
     * @return Room DTO of created record
     * @throws DbQueryException        when query failed
     * @throws SessionExpiredException when current administrator session has expired
     * @throws SessionInvalidException when administrator session is not valid
     */
    private Room add(Token admin_token, Room room) throws DbQueryException, SessionExpiredException, SessionInvalidException {
        String query1 = "INSERT INTO Room( id, floor_id, building_id, room_category_id, description ) VALUES ( " +
                room.id() + ", " +
                room.floorID() + ", " +
                room.buildingID() + ", " +
                room.category() + ", " +
                "\"" + room.description() + "\" " +
                ")";
        this.reservation_db_access.pushToDB(admin_token.getSessionId(), query1);
        String query2 = "SELECT " +
                "Room.id, " +
                "Room.floor_id, " +
                "Room.building_id, " +
                "Room.room_category_id, " +
                "Room.description " +
                "FROM Room " +
                "WHERE id = " + room.id() +
                " AND floor_id = " + room.floorID() +
                " AND building_id = " + room.buildingID();
        ObjectTable table = this.reservation_db_access.pullFromDB(admin_token.getSessionId(), query2);
        if (table.isEmpty()) {
            log.log_Error("Added new room but couldn't get its record ID back: ", room);
            throw new DbQueryException("Problem occurred getting ID of newly created Room record.");
        } else {
            HashMap<String, Object> row = table.getRow(1);
            return new Room(
                    (Integer) row.get("id"),
                    (Integer) row.get("floor_id"),
                    (Integer) row.get("building_id"),
                    (Integer) row.get("room_category_id"),
                    (String) row.get("description")
            );
        }
    }

    /**
     * Binds an existing Room with a new/existing RoomPrice in the Room_has_RoomPrice table
     *
     * @param admin_token Administration session token
     * @param room        Room DTO
     * @param price       Price DTO
     * @throws DbQueryException        when query failed
     * @throws SessionExpiredException when current administrator session has expired
     * @throws SessionInvalidException when administrator session is not valid
     */
    private void bind(Token admin_token, Room room, RoomPrice price) throws DbQueryException, SessionExpiredException, SessionInvalidException {
        Integer price_id = add(admin_token, price).getInteger(1, 1);
        String check_query = "SELECT * FROM Room_has_RoomPrice " +
                "WHERE room_id = " + room.id() +
                " AND floor_id = " + room.floorID() +
                " AND building_id = " + room.buildingID() +
                " AND price_id = " + price_id;
        if (this.reservation_db_access.pullFromDB(admin_token.getSessionId(), check_query).isEmpty()) {
            log.log_Debug("Creating Room-RoomPrice binding: RoomPrice[", price_id, "] to ", room);
            String create_query = "INSERT INTO Room_has_RoomPrice( room_id, floor_id, building_id, price_id ) VALUES ( " +
                    room.id() + ", " +
                    room.floorID() + ", " +
                    room.buildingID() + ", " +
                    price_id + " " +
                    ")";
            this.reservation_db_access.pushToDB(admin_token.getSessionId(), create_query);
        } else {
            log.log_Debug("Room-RoomPrice binding already exists: RoomPrice[", price_id, "] to ", room);
        }
    }

    /**
     * Constructor
     *
     * @param reservation_db_access IReservationDbAccess instance
     */
    public RealEstateAdministration(IReservationDbAccess reservation_db_access) {
        this.reservation_db_access = reservation_db_access;
    }

    /**
     * Gets all buildings in real estate portfolio
     *
     * @param admin_token Administration session token
     * @return ObjectTable of the building records
     * @throws DbQueryException        when query failed
     * @throws SessionExpiredException when current administrator session has expired
     * @throws SessionInvalidException when administrator session is not valid
     */
    public ObjectTable getBuildings(Token admin_token) throws DbQueryException, SessionExpiredException, SessionInvalidException {
        String query = "SELECT * FROM Building";
        return this.reservation_db_access.pullFromDB(admin_token.getSessionId(), query);
    }

    /**
     * Gets all floors in a building
     *
     * @param admin_token Administration session token
     * @param building    Building DTO
     * @return ObjectTable of the floor records for the building
     * @throws DbQueryException        when query failed
     * @throws SessionExpiredException when current administrator session has expired
     * @throws SessionInvalidException when administrator session is not valid
     */
    public ObjectTable getFloors(Token admin_token, Building building) throws DbQueryException, SessionExpiredException, SessionInvalidException {
        String query = "SELECT * FROM Floor WHERE building_id = " + building.id();
        return this.reservation_db_access.pullFromDB(admin_token.getSessionId(), query);
    }

    /**
     * Gets all rooms ina floor
     *
     * @param admin_token Administration session token
     * @param floor       Floor DTO
     * @return ObjectTable of the room records on a floor
     * @throws DbQueryException        when query failed
     * @throws SessionExpiredException when current administrator session has expired
     * @throws SessionInvalidException when administrator session is not valid
     */
    public ObjectTable getRooms(Token admin_token, Floor floor) throws DbQueryException, SessionExpiredException, SessionInvalidException {
        String query = "SELECT * FROM Room " +
                "WHERE building_id = " + floor.buildingID() +
                " AND floor_id = " + floor.floorID();
        return this.reservation_db_access.pullFromDB(admin_token.getSessionId(), query);
    }

    /**
     * Gets all RoomPrice records for a Room with, if any, tied reservations
     *
     * @param admin_token Administration session token
     * @param room        Room DTO
     * @return ObjectTable of the RoomPrice records for a room
     * @throws DbQueryException        when query failed
     * @throws SessionExpiredException when current administrator session has expired
     * @throws SessionInvalidException when administrator session is not valid
     */
    public ObjectTable getRoomPrices(Token admin_token, Room room) throws DbQueryException, SessionExpiredException, SessionInvalidException {
        String query = "SELECT " +
                "RoomPrice.id AS room_price_id, " +
                "RoomPrice.price, " +
                "RoomPrice.year, " +
                "Room_has_Reservation.reservation_id " +
                "FROM Room_has_RoomPrice " +
                "LEFT OUTER JOIN RoomPrice " +
                "ON Room_has_RoomPrice.price_id = RoomPrice.id " +
                "LEFT JOIN Room_has_Reservation ON Room_has_Reservation.room_price_id = RoomPrice.id " +
                "WHERE Room_has_RoomPrice.room_id = " + room.id() +
                " AND Room_has_RoomPrice.floor_id = " + room.floorID() +
                " AND Room_has_RoomPrice.building_id = " + room.buildingID() + " " +
                "ORDER BY room_price_id, reservation_id ASC";
        return this.reservation_db_access.pullFromDB(admin_token.getSessionId(), query);
    }

    /**
     * Gets the most recent price for a room with, if any, tied reservations
     *
     * @param admin_token Administration session token
     * @param room        Room DTO
     * @return ObjectTable of the most recent RoomPrice for a room
     * { room_price_id, price, year, reservation_id }
     * @throws IncompleteRecord        when Room is not associated with a RoomPrice
     * @throws DbQueryException        when query failed
     * @throws SessionExpiredException when current administrator session has expired
     * @throws SessionInvalidException when administrator session is not valid
     */
    public ObjectTable getMostRecentRoomPrice(Token admin_token, Room room) throws IncompleteRecord, DbQueryException, SessionExpiredException, SessionInvalidException {
        String recentYear_query = "SELECT " +
                "MAX( RoomPrice.year ) " +
                "FROM RoomPrice " +
                "WHERE RoomPrice.year <= strftime( '%Y', \"" + TimestampConverter.getUTCTimestampString(Date.from(Instant.now())) + "\" )";
        String priceUsage_query = "SELECT " +
                "Room_has_Reservation.room_id, " +
                "Room_has_Reservation.floor_id, " +
                "Room_has_Reservation.building_id, " +
                "Room_has_Reservation.reservation_id " +
                "FROM Room_has_Reservation " +
                "LEFT OUTER JOIN RoomPrice" +
                " ON Room_has_Reservation.room_price_id = RoomPrice.id " +
                "WHERE Room_has_Reservation.room_id = " + room.id() +
                " AND Room_has_Reservation.floor_id = " + room.floorID() +
                " AND Room_has_Reservation.building_id = " + room.buildingID() + " " +
                " AND RoomPrice.year IN ( " + recentYear_query + " ) " +
                "GROUP BY Room_has_Reservation.reservation_id";
        String roomMatch_query = "SELECT " +
                "RoomPrice.id AS room_price_id, " +
                "RoomPrice.price, " +
                "RoomPrice.year, " +
                "PriceUsage.reservation_id " +
                "FROM Room_has_RoomPrice " +
                "LEFT OUTER JOIN RoomPrice" +
                " ON RoomPrice.id = Room_has_RoomPrice.price_id " +
                "LEFT JOIN (" + priceUsage_query + ") PriceUsage" +
                " ON PriceUsage.room_id = Room_has_RoomPrice.room_id" +
                " AND PriceUsage.floor_id = Room_has_RoomPrice.floor_id" +
                " AND PriceUsage.building_id = Room_has_RoomPrice.building_id " +
                "WHERE Room_has_RoomPrice.room_id = " + room.id() +
                " AND Room_has_RoomPrice.floor_id = " + room.floorID() +
                " AND Room_has_RoomPrice.building_id = " + room.buildingID() +
                " AND RoomPrice.year IN ( " + recentYear_query + " ) ";
        ObjectTable table = this.reservation_db_access.pullFromDB(admin_token.getSessionId(), roomMatch_query);
        if (!table.isEmpty()) {
            return table;
        } else {
            log.log_Error("No room price linked to room: ", room);
            throw new IncompleteRecord("No room price linked to room: " + room);
        }
    }

    /**
     * Gets all room categories on record with, if any, tied rooms
     *
     * @param admin_token Administration session token
     * @return ObjectTable of the room categories
     * @throws DbQueryException        when query failed
     * @throws SessionExpiredException when current administrator session has expired
     * @throws SessionInvalidException when administrator session is not valid
     */
    public ObjectTable getRoomCategories(Token admin_token) throws DbQueryException, SessionExpiredException, SessionInvalidException {
        String query = "SELECT " +
                "RoomCategory.id AS room_category_id, " +
                "RoomCategory.capacity, " +
                "RoomCategory.dimension, " +
                "Room.id AS room_id, " +
                "Room.floor_id, " +
                "Room.building_id, " +
                "Room.description AS room_description " +
                "FROM RoomCategory " +
                "LEFT JOIN Room " +
                "ON Room.room_category_id = RoomCategory.id";
        return this.reservation_db_access.pullFromDB(admin_token.getSessionId(), query);
    }

    /**
     * Gets a room's detailed information
     *
     * @param session_token Administration session token
     * @param room          Room DTO
     * @return ObjectTable of the room's detailed records
     * @throws DbQueryException        when query failed
     * @throws SessionExpiredException when current administrator session has expired
     * @throws SessionInvalidException when administrator session is not valid
     */
    public ObjectTable getRoomDetails(Token session_token, Room room) throws DbQueryException, SessionExpiredException, SessionInvalidException {
        String query = "SELECT " +
                "Building.id AS building_id, " +
                "Building.name AS building_name, " +
                "Building.address1 , " +
                "Building.address2 , " +
                "Building.city , " +
                "Building.postcode , " +
                "Building.country , " +
                "Building.telephone , " +
                "Floor.id AS floor_id, " +
                "Floor.description AS floor_description, " +
                "Room.id AS room_id, " +
                "Room.description AS room_description, " +
                "RoomCategory.id AS category_id, " +
                "RoomCategory.capacity, " +
                "RoomCategory.dimension, " +
                "RoomFixtures.id AS room_fixture_id, " +
                "RoomFixtures.fixed_chairs, " +
                "RoomFixtures.catering_space, " +
                "RoomFixtures.whiteboard, " +
                "RoomFixtures.projector " +
                "FROM Room " +
                "LEFT OUTER JOIN Floor " +
                "ON Room.floor_id = Floor.id" +
                " AND Room.building_id = Floor.building_id " +
                "LEFT OUTER JOIN Building " +
                "ON Floor.building_id = Building.id " +
                "LEFT OUTER JOIN RoomCategory " +
                "ON Room.room_category_id = RoomCategory.id " +
                "LEFT OUTER JOIN Room_has_RoomFixtures " +
                "ON Room.id = Room_has_RoomFixtures.room_id" +
                " AND Room.floor_id = Room_has_RoomFixtures.floor_id" +
                " AND Room.building_id = Room_has_RoomFixtures.building_id " +
                "LEFT OUTER JOIN RoomFixtures " +
                "ON Room_has_RoomFixtures.room_fixture_id = RoomFixtures.id " +
                "WHERE Room.id = " + room.id() +
                " AND Room.floor_id = " + room.floorID() +
                " AND Room.building_id = " + room.buildingID();
        ObjectTable table = this.reservation_db_access.pullFromDB(session_token.getSessionId(), query);
        if (table.isEmpty()) {
            log.log_Error("Got no results for detail request on room: ", room);
            throw new DbQueryException("Got no results for detail request on room: " + room);
        }
        return table;
    }

    /**
     * Adds a new building to the records
     *
     * @param admin_token Administration session token
     * @param building    Building DTO
     * @throws DbQueryException        when query failed
     * @throws SessionExpiredException when current administrator session has expired
     * @throws SessionInvalidException when administrator session is not valid
     */
    public void add(Token admin_token, Building building) throws DbQueryException, SessionExpiredException, SessionInvalidException {
        String query = "INSERT INTO Building( name, address1, address2, city, postcode, country, telephone ) VALUES( " +
                "\"" + building.name() + "\", " +
                "\"" + building.address1() + "\", " +
                (building.address2() == null ? null : "\"" + building.address2() + "\"") + ", " +
                "\"" + building.city() + "\", " +
                "\"" + building.postcode() + "\", " +
                "\"" + building.country() + "\", " +
                "\"" + building.phone() + "\" " +
                ")";
        this.reservation_db_access.pushToDB(admin_token.getSessionId(), query);
    }

    /**
     * Adds a new floor to the records
     *
     * @param admin_token Administration session token
     * @param floor       Floor DTO
     * @throws DbQueryException        when query failed
     * @throws SessionExpiredException when current administrator session has expired
     * @throws SessionInvalidException when administrator session is not valid
     */
    public void add(Token admin_token, Floor floor) throws DbQueryException, SessionExpiredException, SessionInvalidException {
        String query = "INSERT INTO Floor( id, building_id, description ) VALUES ( " +
                floor.floorID() + ", " +
                floor.buildingID() + ", " +
                "\"" + floor.description() + "\" " +
                ")";
        this.reservation_db_access.pushToDB(admin_token.getSessionId(), query);
    }

    /**
     * Adds a new room to the records
     * <p>Create if needed and links up the corresponding fixture and price entries</p>
     *
     * @param admin_token Administration session token
     * @param room        Room DTO
     * @param price       RoomPrice DTO
     * @param fixtures    RoomFixtures DTO
     * @throws DbQueryException        when query failed
     * @throws SessionExpiredException when current administrator session has expired
     * @throws SessionInvalidException when administrator session is not valid
     */
    public void add(Token admin_token, Room room, RoomPrice price, RoomFixtures fixtures) throws DbQueryException, SessionExpiredException, SessionInvalidException {
        Integer room_fixture_id = getRoomFixturesID(
                admin_token,
                fixtures.hasFixedChairs(),
                fixtures.hasCateringSpace(),
                fixtures.hasWhiteboard(),
                fixtures.hasProjector()
        ).orElse(addToRoomFixtures(
                admin_token,
                fixtures.hasFixedChairs(),
                fixtures.hasCateringSpace(),
                fixtures.hasWhiteboard(),
                fixtures.hasProjector())
        );

        Room created_room = add(admin_token, room);

        bind(admin_token, room, price);

        String query = "INSERT INTO Room_has_RoomFixtures( room_id, floor_id, building_id, room_fixture_id ) VALUES ( " +
                created_room.id() + ", " +
                created_room.floorID() + ", " +
                created_room.buildingID() + ", " +
                room_fixture_id + " " +
                ")";
        this.reservation_db_access.pushToDB(admin_token.getSessionId(), query);
    }

    /**
     * Adds a new room price to the records
     *
     * @param admin_token Administration session token
     * @param roomPrice   RoomPrice DTO
     * @return ObjectTable of matching/created record
     * { id, price, year }
     * @throws DbQueryException        when query failed
     * @throws SessionExpiredException when current administrator session has expired
     * @throws SessionInvalidException when administrator session is not valid
     */
    public ObjectTable add(Token admin_token, RoomPrice roomPrice) throws DbQueryException, SessionExpiredException, SessionInvalidException {
        String check_query = "SELECT * FROM RoomPrice " +
                "WHERE price = " + roomPrice.price() +
                " AND year = " + roomPrice.year();
        String create_query = "INSERT INTO RoomPrice( price, year ) VALUES ( " +
                roomPrice.price() + ", " +
                roomPrice.year() + " )";
        ObjectTable table = this.reservation_db_access.pullFromDB(admin_token.getSessionId(), check_query);
        if (table.isEmpty()) {
            this.reservation_db_access.pushToDB(admin_token.getSessionId(), create_query);
            log.log_Debug("Adding new RoomPrice entry: " + roomPrice.price() + " for ", roomPrice.year());
            table = this.reservation_db_access.pullFromDB(admin_token.getSessionId(), check_query);
        }
        return table;
    }

    /**
     * Adds a new room category to the records
     *
     * @param admin_token  Administration session token
     * @param roomCategory RoomCategory DTO
     * @return ObjectTable of matching/created record
     * @throws DbQueryException        when query failed
     * @throws SessionExpiredException when current administrator session has expired
     * @throws SessionInvalidException when administrator session is not valid
     */
    public ObjectTable add(Token admin_token, RoomCategory roomCategory) throws DbQueryException, SessionExpiredException, SessionInvalidException {
        String check_query = "SELECT * FROM RoomCategory " +
                "WHERE capacity = " + roomCategory.capacity() +
                " AND dimension = " + roomCategory.dimension();
        String create_query = "INSERT INTO RoomCategory( capacity, dimension ) VALUES ( " +
                roomCategory.capacity() + ", " +
                roomCategory.dimension() + " )";
        ObjectTable table = this.reservation_db_access.pullFromDB(admin_token.getSessionId(), check_query);
        if (table.isEmpty()) {
            this.reservation_db_access.pushToDB(admin_token.getSessionId(), create_query);
            table = this.reservation_db_access.pullFromDB(admin_token.getSessionId(), check_query);
        }
        return table;
    }

    /**
     * Updates a building's records
     *
     * @param admin_token Administration session token
     * @param building    Building DTO
     * @throws DbQueryException        when query failed
     * @throws SessionExpiredException when current administrator session has expired
     * @throws SessionInvalidException when administrator session is not valid
     */
    public void update(Token admin_token, Building building) throws DbQueryException, SessionExpiredException, SessionInvalidException {
        String query = "UPDATE Building SET" +
                " name = \"" + building.name() + "\"," +
                " address1 = \"" + building.address1() + "\"," +
                " address2 = " + (building.address2().isEmpty() ? null : "\"" + building.address2() + "\" ") + "," +
                " city = \"" + building.city() + "\"," +
                " postcode = \"" + building.postcode() + "\"," +
                " country = \"" + building.country() + "\"," +
                " telephone = \"" + building.phone() + "\" " +
                "WHERE id = " + building.id();
        this.reservation_db_access.pushToDB(admin_token.getSessionId(), query);
    }

    /**
     * Updates a floor's records
     * <p>Updated: Floor.description</p>
     *
     * @param admin_token Administration session token
     * @param floor       Floor DTO
     * @throws DbQueryException        when query failed
     * @throws SessionExpiredException when current administrator session has expired
     * @throws SessionInvalidException when administrator session is not valid
     */
    public void update(Token admin_token, Floor floor) throws DbQueryException, SessionExpiredException, SessionInvalidException {
        String query = "UPDATE Floor SET description = \"" + floor.description() + "\" " +
                "WHERE Floor.id = " + floor.floorID() +
                " AND Floor.building_id = " + floor.buildingID();
        this.reservation_db_access.pushToDB(admin_token.getSessionId(), query);
    }

    /**
     * Update a room in the records
     * <p>Updated: Room.description, Room.room_category_id</p>
     *
     * @param admin_token Administration session token
     * @param room        Room DTO
     * @throws DbQueryException        when query failed
     * @throws SessionExpiredException when current administrator session has expired
     * @throws SessionInvalidException when administrator session is not valid
     */
    public void update(Token admin_token, Room room, RoomPrice price) throws DbQueryException, SessionExpiredException, SessionInvalidException {
        bind(admin_token, room, price);
        String query = "UPDATE Room SET " +
                "description = \"" + room.description() + "\", " +
                "room_category_id = " + room.category() + " " +
                "WHERE id = " + room.id() +
                " AND floor_id = " + room.floorID() +
                " AND building_id = " + room.buildingID();
        this.reservation_db_access.pushToDB(admin_token.getSessionId(), query);
    }

    /**
     * Remove a building from the records
     *
     * @param admin_token Administration session token
     * @param building    Building DTO
     * @return Success
     * @throws DbQueryException        when query failed
     * @throws SessionExpiredException when current administrator session has expired
     * @throws SessionInvalidException when administrator session is not valid
     */
    public boolean remove(Token admin_token, Building building) throws DbQueryException, SessionExpiredException, SessionInvalidException {
        //Check ties to Reservation(s)
        String check_query1 = "SELECT COUNT(*) AS result FROM Room_has_Reservation WHERE building_id = " + building.id();
        ObjectTable table = this.reservation_db_access.pullFromDB(admin_token.getSessionId(), check_query1);
        if (table.getInteger(1, 1) > 0) { //a.k.a. 'result' of COUNT(*)
            log.log_Error("Cannot delete Building record as it is tied to Reservation(s): ", building);
            return false;
        }
        //Check ties to Floor(s)
        String check_query2 = "SELECT COUNT(*) AS result FROM Floor WHERE building_id = " + building.id();
        ObjectTable table2 = this.reservation_db_access.pullFromDB(admin_token.getSessionId(), check_query2);
        if (table2.getInteger(1, 1) > 0) {
            log.log_Error("Cannot delete Building record as it is tied to Floor(s): ", building);
            return false;
        }
        //Remove Building entry
        String remove_query = "DELETE FROM Building WHERE id = " + building.id();
        this.reservation_db_access.pushToDB(admin_token.getSessionId(), remove_query);
        log.log_Trace("Removed: ", building);
        return true;
    }

    /**
     * Removes a floor from the records
     *
     * @param admin_token Administration session token
     * @param floor       Floor DTO
     * @return Success
     * @throws DbQueryException        when query failed
     * @throws SessionExpiredException when current administrator session has expired
     * @throws SessionInvalidException when administrator session is not valid
     */
    public boolean remove(Token admin_token, Floor floor) throws DbQueryException, SessionExpiredException, SessionInvalidException {
        //Check ties to Reservation(s)
        String check_query1 = "SELECT COUNT(*) AS result FROM Room_has_Reservation " +
                "WHERE building_id = " + floor.buildingID() +
                " AND floor_id = " + floor.floorID();
        ObjectTable table1 = this.reservation_db_access.pullFromDB(admin_token.getSessionId(), check_query1);
        if (table1.getInteger(1, 1) > 0) { //a.k.a. 'result' of COUNT(*)
            log.log_Error("Cannot delete Floor record as it is tied to Reservation(s): ", floor);
            return false;
        }
        //Check ties to Room(s)
        String check_query2 = "SELECT COUNT(*) AS result FROM Room " +
                "WHERE floor_id = " + floor.floorID() +
                " AND building_id = " + floor.buildingID();
        ObjectTable table2 = this.reservation_db_access.pullFromDB(admin_token.getSessionId(), check_query2);
        if (table2.getInteger(1, 1) > 0) { //a.k.a. 'result' of COUNT(*)
            log.log_Error("Cannot delete Floor record as it is tied to Room(s): ", floor);
            return false;
        }
        //Remove Floor entry
        String remove_query = "DELETE FROM Floor " +
                "WHERE id = " + floor.floorID() +
                " AND building_id = " + floor.buildingID();
        this.reservation_db_access.pushToDB(admin_token.getSessionId(), remove_query);
        log.log_Trace("Removed: ", floor);
        return true;
    }

    /**
     * Removes a room from the records
     *
     * @param admin_token Administration session token
     * @param room        Room DTO
     * @return Success
     * @throws DbQueryException        when query failed
     * @throws SessionExpiredException when current administrator session has expired
     * @throws SessionInvalidException when administrator session is not valid
     */
    public boolean remove(Token admin_token, Room room) throws DbQueryException, SessionExpiredException, SessionInvalidException {
        //Check ties to Reservation(s)
        String check_query = "SELECT COUNT(*) AS result FROM Room_has_Reservation " +
                "WHERE room_id = " + room.id() +
                " AND floor_id = " + room.floorID() +
                " AND building_id = " + room.buildingID();
        ObjectTable table = this.reservation_db_access.pullFromDB(admin_token.getSessionId(), check_query);
        if (table.getInteger(1, 1) > 0) { //a.k.a. 'result' of COUNT(*)
            log.log_Error("Room is tied to Reservation(s). Cannot delete entry: ", room);
            return false;
        }
        //Remove any linkage between Room and RoomPrice in Room_has_RoomPrice table
        String remove_query1 = "DELETE FROM Room_has_RoomPrice " +
                "WHERE room_id = " + room.id() +
                " AND floor_id = " + room.floorID() +
                " AND building_id = " + room.buildingID();
        this.reservation_db_access.pushToDB(admin_token.getSessionId(), remove_query1);
        log.log_Trace("Removed all existing Room_has_RoomPrice entries for: ", room);
        //Remove any linkage between Room and RoomFixtures in Room_has_RoomFixtures
        String remove_query2 = "DELETE FROM Room_has_RoomFixtures " +
                "WHERE room_id = " + room.id() +
                " AND floor_id = " + room.floorID() +
                " AND building_id = " + room.buildingID();
        this.reservation_db_access.pushToDB(admin_token.getSessionId(), remove_query2);
        log.log_Trace("Removed all existing Room_has_RoomFixtures entries for: ", room);
        //Remove Room entry
        String remove_query3 = "DELETE FROM Room " +
                "WHERE id = " + room.id() +
                " AND floor_id = " + room.floorID() +
                " AND building_id = " + room.buildingID();
        this.reservation_db_access.pushToDB(admin_token.getSessionId(), remove_query3);
        log.log_Trace("Removed: ", room);
        return true;
    }

    /**
     * Remove a room price untied to any reservations from the records
     *
     * @param admin_token Administration session token
     * @param roomPrice   RoomPrice DTO
     * @return Success
     * @throws DbQueryException        when query failed
     * @throws SessionExpiredException when current administrator session has expired
     * @throws SessionInvalidException when administrator session is not valid
     */
    public boolean remove(Token admin_token, RoomPrice roomPrice) throws DbQueryException, SessionExpiredException, SessionInvalidException {
        //Check ties to Reservation(s)
        String check_query = "SELECT room_id, floor_id, building_id " +
                "FROM Room_has_Reservation " +
                "WHERE room_price_id =" + roomPrice.id();
        ObjectTable table = this.reservation_db_access.pullFromDB(admin_token.getSessionId(), check_query);
        if (!table.isEmpty()) {
            log.log_Error("RoomPrice is tied to Reservation(s). Cannot delete entry: ", roomPrice);
            return false;
        }
        //Remove any linkage between Room and RoomPrice in Room_has_RoomPrice table
        String remove_query1 = "DELETE FROM Room_has_RoomPrice WHERE price_id = " + roomPrice.id();
        this.reservation_db_access.pushToDB(admin_token.getSessionId(), remove_query1);
        log.log_Trace("Removed all Room_has_RoomPrice entries for: ", roomPrice);
        //Remove RoomPrice entry
        String remove_query2 = "DELETE FROM RoomPrice WHERE id = " + roomPrice.id();
        this.reservation_db_access.pushToDB(admin_token.getSessionId(), remove_query2);
        log.log_Trace("Removed: ", roomPrice);
        return true;
    }

    /**
     * Remove a room category untied to any rooms from the records
     *
     * @param admin_token  Administration session token
     * @param roomCategory RoomCategory DTO
     * @return Success
     * @throws DbQueryException        when query failed
     * @throws SessionExpiredException when current administrator session has expired
     * @throws SessionInvalidException when administrator session is not valid
     */
    public boolean remove(Token admin_token, RoomCategory roomCategory) throws DbQueryException, SessionExpiredException, SessionInvalidException {
        //Check ties to Room(s)
        String check_query = "SELECT Room.id, Room.floor_id, Room.building_id FROM Room " +
                "LEFT OUTER JOIN RoomCategory ON Room.room_category_id = RoomCategory.id " +
                "WHERE RoomCategory.id = " + roomCategory.id();
        ObjectTable table = this.reservation_db_access.pullFromDB(admin_token.getSessionId(), check_query);
        if (!table.isEmpty()) {
            log.log_Error("RoomCategory is tied to Room(s). Cannot delete entry: ", roomCategory);
            return false;
        }
        //Remove RoomCategory entry
        String remove_query = "DELETE FROM RoomCategory WHERE id = " + roomCategory.id();
        this.reservation_db_access.pushToDB(admin_token.getSessionId(), remove_query);
        log.log_Trace("Removed: ", roomCategory);
        return true;
    }

}
