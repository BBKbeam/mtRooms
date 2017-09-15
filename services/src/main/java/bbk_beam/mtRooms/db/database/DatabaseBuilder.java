package bbk_beam.mtRooms.db.database;

import bbk_beam.mtRooms.db.exception.DbQueryException;
import eadjlib.logger.Logger;

public class DatabaseBuilder {
    private final Logger log = Logger.getLoggerInstance(DatabaseBuilder.class.getName());
    private final int reservation_table_count = 1;
    private final int user_table_count = 0;
    //TODO DB setup tool
    //i.e.: create all the tables and structures required for a new blank db

    /**
     * Builds the Reservation database
     *
     * @param db Database instance
     * @return Success
     */
    public boolean buildReservationDB(IDatabase db) {
        int build_count = 0;
        //TODO
        if( buildTable_Building( db ) ) build_count++;

        return reservation_table_count == build_count;
    }

    /**
     * Builds the User Accounts database
     *
     * @param db Database instance
     * @return Success
     */
    public boolean buildUserAccDB(IDatabase db) {
        //TODO
        return false;
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
            db.queryDB( query );
            return true;
        } catch (DbQueryException e) {
            log.log_Error( "Issue encountered processing query: ", query );
            log.log_Exception( e );
            return false;
        }
    }
}
