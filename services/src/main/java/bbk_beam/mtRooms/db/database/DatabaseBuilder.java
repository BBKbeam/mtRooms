package bbk_beam.mtRooms.db.database;

import bbk_beam.mtRooms.admin.authentication.PasswordHash;
import bbk_beam.mtRooms.admin.exception.AuthenticationHasherException;
import bbk_beam.mtRooms.db.TimestampConverter;
import bbk_beam.mtRooms.db.exception.DbQueryException;
import eadjlib.logger.Logger;

import java.util.Date;

class DatabaseBuilder {
    private final Logger log = Logger.getLoggerInstance(DatabaseBuilder.class.getName());
    private final int reservation_table_count = 17;
    private final int user_table_count = 2;

    /**
     * Builds the Reservation database
     *
     * @param db Database instance
     * @return Success
     */
    boolean buildReservationDB(IDatabase db) {
        log.log("Building reservation tables...");
        int build_count = 0;
        if (buildTable_Building(db)) build_count++;
        if (buildTable_Floor(db)) build_count++;
        if (buildTable_RoomCategory(db)) build_count++;
        if (buildTable_RoomPrice(db)) build_count++;
        if (buildTable_RoomFixtures(db)) build_count++;
        if (buildTable_Room(db)) build_count++;
        if (buildTable_Room_has_RoomPrice(db)) build_count++;
        if (buildTable_Room_has_RoomFixtures(db)) build_count++;
        if (buildTable_PaymentMethod(db)) build_count++;
        if (buildTable_Payment(db)) build_count++;
        if (buildTable_DiscountCategory(db)) build_count++;
        if (buildTable_Discount(db)) build_count++;
        if (buildTable_MembershipType(db)) build_count++;
        if (buildTable_Customer(db)) build_count++;
        if (buildTable_Reservation(db)) build_count++;
        if (buildTable_Reservation_has_Payment(db)) build_count++;
        if (buildTable_Room_has_Reservation(db)) build_count++;
        return reservation_table_count == build_count;
    }

    /**
     * Builds the User Accounts database
     *
     * @param db Database instance
     * @return Success
     */
    boolean buildUserAccDB(IDatabase db) {
        log.log("Building user account tables...");
        int build_count = 0;
        if (buildTable_AccountType(db)) build_count++;
        if (buildTable_UserAccount(db)) build_count++;
        return user_table_count == build_count;
    }

    /**
     * Pushes a query to the database
     *
     * @param db    Database instance
     * @param query Query to pass
     * @return Success of query
     */
    private boolean pushQuery(IDatabase db, String query) {
        try {
            return db.push(query);
        } catch (DbQueryException e) {
            log.log_Error("Issue encountered processing query: ", query);
            log.log_Exception(e);
            return false;
        }
    }

    //=========================================Reservation Tables ======================================================
    private boolean buildTable_Building(IDatabase db) {
        String query = "CREATE TABLE Building( "
                + "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
                + "name TEXT NOT NULL, "
                + "address1 VARCHAR(255) NOT NULL, "
                + "address2 VARCHAR(255), "
                + "city VARCHAR(255) NOT NULL, "
                + "postcode VARCHAR(145) NOT NULL, "
                + "country VARCHAR(145) NOT NULL, "
                + "telephone VARCHAR(50) NOT NULL "
                + ")";
        return pushQuery(db, query);
    }

    private boolean buildTable_Floor(IDatabase db) {
        String query = "CREATE TABLE Floor( "
                + "id INTEGER NOT NULL, "
                + "building_id INTEGER NOT NULL, "
                + "description VARCHAR(50) NOT NULL, "
                + "FOREIGN KEY(building_id) REFERENCES Building(id) ON DELETE CASCADE, "
                + "UNIQUE( id, building_id ), "
                + "PRIMARY KEY( id, building_id ) "
                + ")";
        return pushQuery(db, query);
    }

    private boolean buildTable_RoomCategory(IDatabase db) {
        String query = "CREATE TABLE RoomCategory( "
                + "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
                + "capacity INTEGER NOT NULL, "
                + "dimension INTEGER, "
                + "UNIQUE( capacity, dimension ) "
                + ")";
        return pushQuery(db, query);
    }

    private boolean buildTable_RoomPrice(IDatabase db) {
        String query = "CREATE TABLE RoomPrice( "
                + "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
                + "price DOUBLE NOT NULL, "
                + "year INTEGER NOT NULL, "
                + "UNIQUE( price, year ) "
                + ")";
        return pushQuery(db, query);
    }

    private boolean buildTable_RoomFixtures(IDatabase db) {
        String query = "CREATE TABLE RoomFixtures( "
                + "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
                + "fixed_chairs BOOLEAN NOT NULL, "
                + "catering_space BOOLEAN NOT NULL, "
                + "whiteboard BOOLEAN NOT NULL, "
                + "projector BOOLEAN NOT NULL "
                + ")";
        return pushQuery(db, query);
    }

    private boolean buildTable_Room(IDatabase db) {
        String query = "CREATE TABLE Room( "
                + "id INTEGER NOT NULL, "
                + "floor_id INTEGER NOT NULL, "
                + "building_id INTEGER NOT NULL, "
                + "room_category_id INTEGER NOT NULL, "
                + "description VARCHAR(255) NOT NULL, "
                + "FOREIGN KEY ( floor_id, building_id ) REFERENCES Floor( id, building_id ), "
                + "FOREIGN KEY( room_category_id ) REFERENCES RoomCategory( id ), "
                + "PRIMARY KEY( id, floor_id, building_id ) "
                + ")";
        return pushQuery(db, query);
    }

    private boolean buildTable_Room_has_RoomPrice(IDatabase db) {
        String query = "CREATE TABLE Room_has_RoomPrice( "
                + "room_id INTEGER NOT NULL, "
                + "floor_id INTEGER NOT NULL, "
                + "building_id INTEGER NOT NULL, "
                + "price_id INTEGER NOT NULL, "
                + "FOREIGN KEY( room_id, floor_id, building_id ) REFERENCES Room( id, floor_id, building_id ), "
                + "FOREIGN KEY( price_id ) REFERENCES RoomPrice( id ), "
                + "PRIMARY KEY( room_id, floor_id, building_id, price_id ) "
                + ")";
        return pushQuery(db, query);
    }

    private boolean buildTable_Room_has_RoomFixtures(IDatabase db) {
        String query = "CREATE TABLE Room_has_RoomFixtures( "
                + "room_id INTEGER NOT NULL, "
                + "floor_id INTEGER NOT NULL, "
                + "building_id INTEGER NOT NULL, "
                + "room_fixture_id INTEGER NOT NULL, "
                + "FOREIGN KEY( room_id, floor_id, building_id ) REFERENCES Room( id, floor_id, building_id ), "
                + "FOREIGN KEY( room_fixture_id ) REFERENCES RoomFixtures( id ), "
                + "PRIMARY KEY( room_id, floor_id, building_id, room_fixture_id ) "
                + ")";
        return pushQuery(db, query);
    }

    private boolean buildTable_PaymentMethod(IDatabase db) {
        String query1 = "CREATE TABLE PaymentMethod( "
                + "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
                + "description VARCHAR(255) NOT NULL "
                + ")";
        String query2 = "INSERT INTO PaymentMethod( description ) VALUES "
                + "( \"Cash\" ), "
                + "( \"Debit Card\" ), "
                + "( \"Credit Card\" )";
        return pushQuery(db, query1) && pushQuery(db, query2);
    }

    private boolean buildTable_Payment(IDatabase db) {
        String query = "CREATE TABLE Payment( "
                + "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
                + "hash_id TEXT NOT NULL UNIQUE, "
                + "amount DOUBLE NOT NULL, "
                + "payment_method INTEGER NOT NULL, "
                + "timestamp TIMESTAMP NOT NULL, "
                + "note TEXT, "
                + "FOREIGN KEY( payment_method ) REFERENCES PaymentMethod( id ) "
                + ")";
        return pushQuery(db, query);
    }

    private boolean buildTable_DiscountCategory(IDatabase db) {
        String query1 = "CREATE TABLE DiscountCategory( "
                + "id INTEGER PRIMARY KEY NOT NULL, "
                + "description VARCHAR(255) NOT NULL "
                + ")";
        String query2 = "INSERT INTO DiscountCategory( description ) VALUES "
                + "( \"None\" )";
        return pushQuery(db, query1) && pushQuery(db, query2);
    }

    private boolean buildTable_Discount(IDatabase db) {
        String query1 = "CREATE TABLE Discount( "
                + "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
                + "discount_rate DOUBLE NOT NULL, "
                + "discount_category_id INTEGER NOT NULL, "
                + "FOREIGN KEY( discount_category_id ) REFERENCES DiscountCategory( id ) "
                + ")";
        String query2 = "INSERT INTO Discount( discount_rate, discount_category_id ) VALUES "
                + "( 0., 1 )";
        return pushQuery(db, query1) && pushQuery(db, query2);
    }

    private boolean buildTable_MembershipType(IDatabase db) {
        String query1 = " CREATE TABLE MembershipType( "
                + "id INTEGER PRIMARY KEY NOT NULL, "
                + "discount_category_id INTEGER NOT NULL, "
                + "description TEXT NOT NULL, "
                + "FOREIGN KEY( discount_category_id ) REFERENCES DiscountCategory( id ) "
                + ")";
        String query2 = "INSERT INTO MembershipType( description, discount_category_id ) VALUES "
                + "( \"None\", 1 )";
        return pushQuery(db, query1) && pushQuery(db, query2);
    }

    private boolean buildTable_Customer(IDatabase db) {
        String query = " CREATE TABLE Customer( "
                + "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
                + "membership_type_id INTEGER NOT NULL, "
                + "customer_since TIMESTAMP NOT NULL, "
                + "title VARCHAR(10) NOT NULL, "
                + "name VARCHAR(255) NOT NULL, "
                + "surname VARCHAR(255) NOT NULL, "
                + "address_1 VARCHAR(255) NOT NULL, "
                + "address_2 VARCHAR(255), "
                + "city VARCHAR(145) NOT NULL, "
                + "county VARCHAR(145), "
                + "country VARCHAR(145) NOT NULL, "
                + "postcode VARCHAR(15) NOT NULL, "
                + "telephone_1 VARCHAR(45) NOT NULL, "
                + "telephone_2 VARCHAR(45), "
                + "email VARCHAR(145) NOT NULL, "
                + "FOREIGN KEY(membership_type_id) REFERENCES MembershipType(id) "
                + ")";
        return pushQuery(db, query);
    }

    private boolean buildTable_Reservation(IDatabase db) {
        String query = "CREATE TABLE Reservation( "
                + "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
                + "created_timestamp TIMESTAMP NOT NULL, "
                + "customer_id INTEGER NOT NULL, "
                + "discount_id INTEGER NOT NULL, "
                + "FOREIGN KEY( discount_id ) REFERENCES Discount( id ) "
                + ")";
        return pushQuery(db, query);
    }

    private boolean buildTable_Reservation_has_Payment(IDatabase db) {
        String query = "CREATE TABLE Reservation_has_Payment( "
                + "reservation_id INTEGER NOT NULL, "
                + "payment_id INTEGER NOT NULL, "
                + "PRIMARY KEY( reservation_id, payment_id ), "
                + "FOREIGN KEY( reservation_id ) REFERENCES Reservation( id ) ON DELETE CASCADE, "
                + "FOREIGN KEY( payment_id ) REFERENCES Payment( id ) "
                + ")";
        return pushQuery(db, query);
    }

    private boolean buildTable_Room_has_Reservation(IDatabase db) {
        String query = "CREATE TABLE Room_has_Reservation( "
                + "room_id INTEGER NOT NULL, "
                + "floor_id INTEGER NOT NULL, "
                + "building_id INTEGER NOT NULL, "
                + "reservation_id INTEGER NOT NULL, "
                + "timestamp_in TIMESTAMP NOT NULL, "
                + "timestamp_out TIMESTAMP NOT NULL, "
                + "room_price_id INTEGER NOT NULL, "
                + "seated_count INTEGER NOT NULL, "
                + "catering BOOLEAN NOT NULL, "
                + "notes TEXT, "
                + "cancelled_flag BOOLEAN NOT NULL DEFAULT 0, "
                + "PRIMARY KEY( room_id, floor_id, building_id, reservation_id, timestamp_in ), "
                + "FOREIGN KEY( room_id, floor_id, building_id ) REFERENCES Room( id, floor_id, building_id ), "
                + "FOREIGN KEY( reservation_id ) REFERENCES Reservation( id ), "
                + "FOREIGN KEY( room_price_id ) REFERENCES RoomPrice( id ) "
                + ")";
        return pushQuery(db, query);
    }

    //========================================User Account Tables ======================================================
    private boolean buildTable_AccountType(IDatabase db) {
        String query1 = "CREATE TABLE AccountType( "
                + "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
                + "description VARCHAR(255) NOT NULL "
                + ")";
        String query2 = "INSERT INTO AccountType( description ) VALUES"
                + "( \"ADMIN\" ), "
                + "( \"USER\" )";
        return pushQuery(db, query1) && pushQuery(db, query2);
    }

    private boolean buildTable_UserAccount(IDatabase db) {
        try {
            String query1 = "CREATE TABLE UserAccount( "
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
                    + "username TEXT NOT NULL, "
                    + "pwd_hash TEXT NOT NULL, "
                    + "pwd_salt TEXT NOT NULL, "
                    + "created VARCHAR(255) NOT NULL, "
                    + "last_pwd_change VARCHAR(255) NOT NULL, "
                    + "last_login VARCHAR(255) DEFAULT NULL, "
                    + "account_type_id INTEGER NOT NULL, "
                    + "active_state BOOLEAN NOT NULL, "
                    + "FOREIGN KEY( account_type_id ) REFERENCES AccountType( id ) "
                    + ")";
            String salt = PasswordHash.createSalt();
            String hash = PasswordHash.createHash("letmein", salt);
            String query2 = "INSERT INTO UserAccount( "
                    + "username, pwd_hash, pwd_salt, created, last_pwd_change, account_type_id, active_state "
                    + ") VALUES( "
                    + "\"root\", "
                    + "\"" + hash + "\", "
                    + "\"" + salt + "\", "
                    + "\"" + TimestampConverter.getUTCTimestampString(new Date()) + "\", "
                    + "\"" + TimestampConverter.getUTCTimestampString(new Date()) + "\", "
                    + "1, "
                    + "1 "
                    + ")";
            return pushQuery(db, query1) && pushQuery(db, query2);
        } catch (AuthenticationHasherException e) {
            log.log_Fatal("Could not create hash for default root account.");
            log.log_Exception(e);
        }
        return false;
    }
}
