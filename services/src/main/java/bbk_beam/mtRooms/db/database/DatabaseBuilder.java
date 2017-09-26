package bbk_beam.mtRooms.db.database;

import bbk_beam.mtRooms.db.exception.DbQueryException;
import eadjlib.logger.Logger;

class DatabaseBuilder {
    private final Logger log = Logger.getLoggerInstance(DatabaseBuilder.class.getName());
    //Update when adding/removing tables from schema
    private final int reservation_table_count = 4;
    private final int user_table_count = -1;
    //TODO DB setup tool
    //i.e.: create all the tables and structures required for a new blank db

    /**
     * Builds the Reservation database
     *
     * @param db Database instance
     * @return Success
     */
    boolean buildReservationDB(IDatabase db) {
        int build_count = 0;
        //TODO
        if (buildTable_Building(db)) build_count++;
        if (buildTable_Floor(db)) build_count++;
        if (buildTable_RoomCategory(db)) build_count++;
        if (buildTable_RoomPrice(db)) build_count++;

        return reservation_table_count == build_count;
    }

    /**
     * Builds the User Accounts database
     *
     * @param db Database instance
     * @return Success
     */
    boolean buildUserAccDB(IDatabase db) {
        int build_count = 0;
        //TODO UserAccDB table builds
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

    private boolean buildTable_Building(IDatabase db) {
        String query = "CREATE TABLE Building( "
                + "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
                + "name TEXT NOT NULL, "
                + "address1 VARCHAR(255) NOT NULL, "
                + "address2 VARCHAR(255), "
                + "postcode VARCHAR(145), "
                + "country VARCHAR(145), "
                + "telephone VARCHAR(50) "
                + ")";
        return pushQuery(db, query);
    }

    private boolean buildTable_Floor(IDatabase db) {
        String query = "CREATE TABLE Floor( "
                + "id INTEGER NOT NULL, "
                + "building_id INTEGER NOT NULL, "
                + "FOREIGN KEY(building_id) REFERENCES Building(id) ON DELETE CASCADE, "
                + "UNIQUE( id, building_id ), "
                + "PRIMARY KEY( id, building_id ) "
                + ")";
        return pushQuery(db, query);
    }

    private boolean buildTable_RoomCategory(IDatabase db) {
        String query = "CREATE TABLE RoomCategory( "
                + "id INTEGER PRIMARY KEY NOT NULL, "
                + "capacity INTEGER NOT NULL, "
                + "dimension INTEGER "
                + ")";
        return pushQuery(db, query);
    }

    private boolean buildTable_RoomPrice(IDatabase db) {
        String query = "CREATE TABLE RoomPrice( "
                + "id INTEGER PRIMARY KEY NOT NULL, "
                + "year INTEGER NOT NULL, "
                + "room_price INTEGER NOT NULL "
                + ")";
        return pushQuery(db, query);
    }
}
