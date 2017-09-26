package bbk_beam.mtRooms.db.database;

import bbk_beam.mtRooms.db.exception.DbQueryException;
import eadjlib.logger.Logger;

class DatabaseBuilder {
    private final Logger log = Logger.getLoggerInstance(DatabaseBuilder.class.getName());
    //Update when adding/removing tables from schema
    private final int reservation_table_count = 2;
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
        //TODO
        return user_table_count == build_count;
    }

    private boolean buildTable_Building(IDatabase db) {
        String query = "CREATE TABLE Building( "
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "name TEXT NOT NULL, "
                + "address1 VARCHAR(255) NOT NULL, "
                + "address2 VARCHAR(255), "
                + "postcode VARCHAR(145), "
                + "country VARCHAR(145), "
                + "telephone VARCHAR(50) "
                + ")";
        try {
            return db.push(query);
        } catch (DbQueryException e) {
            log.log_Error("Issue encountered processing query: ", query);
            log.log_Exception(e);
            return false;
        }
    }

    private boolean buildTable_Floor(IDatabase db) {
        String query = "CREATE TABLE Floor( "
                + "id INTEGER NOT NULL, "
                + "building_id INTEGER NOT NULL, "
                + "FOREIGN KEY(building_id) REFERENCES Building(id) ON DELETE CASCADE, "
                + "UNIQUE( id, building_id ), "
                + "PRIMARY KEY( id, building_id ) "
                + ")";
        try {
            return db.push(query);
        } catch (DbQueryException e) {
            log.log_Error("Issue encountered processing query: ", query);
            log.log_Exception(e);
            return false;
        }
    }
}
