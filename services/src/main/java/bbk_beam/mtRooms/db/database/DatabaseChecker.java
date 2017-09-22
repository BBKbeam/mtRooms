package bbk_beam.mtRooms.db.database;

import bbk_beam.mtRooms.db.exception.DbEmptyException;
import bbk_beam.mtRooms.db.exception.DbQueryException;
import eadjlib.datastructure.ObjectTable;
import eadjlib.logger.Logger;

class DatabaseChecker {
    private final Logger log = Logger.getLoggerInstance(DatabaseChecker.class.getName());
    //Update when adding/removing tables from schema
    private final int reservation_table_count = 1;
    private final int user_table_count = -1;
    //TODO DB Checker tool

    /**
     * Runs all the checks for the Reservation database
     *
     * @param db Database instance
     * @return Success
     * @throws DbEmptyException when non of the tables required exists in the database
     */
    boolean checkReservationDB(IDatabase db) throws DbEmptyException {
        int check_count = 0;
        //TODO
        if( checkTable_Building( db ) ) check_count++;

        return reservation_table_count == check_count;
    }

    /**
     * Runs all the checks for the User Accounts database
     *
     * @param db Database instance
     * @return Success
     * @throws DbEmptyException when non of the tables required exists in the database
     */
    boolean checkUserAccDB(IDatabase db) throws DbEmptyException {
        int check_count = 0;
        //TODO

        return user_table_count == check_count;
    }

    private boolean checkTable_Building(IDatabase db) {
        String query = "PRAGMA table_info( Building )";
        try {
            ObjectTable table = db.pull(query );
            if( table.rowCount() == 7 ) {
                boolean ok_flag = true;
                if( !table.containsInColumn("id","name"))
                    ok_flag = false;
                if( !table.containsInColumn("name", "name"))
                    ok_flag = false;
                if( !table.containsInColumn("address1", "name"))
                    ok_flag = false;
                if( !table.containsInColumn("address2", "name"))
                    ok_flag = false;
                if( !table.containsInColumn("postcode", "name"))
                    ok_flag = false;
                if( !table.containsInColumn("country", "name"))
                    ok_flag = false;
                if( !table.containsInColumn("telephone", "name"))
                    ok_flag = false;
                return ok_flag;
            }
            return false;
        } catch (DbQueryException e) {
            log.log_Error("Issue encountered processing query: ", query);
            log.log_Exception(e);
            return false;
        }
    }
}
