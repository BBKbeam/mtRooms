package bbk_beam.mtRooms.db.database;

import bbk_beam.mtRooms.db.exception.DbEmptyException;
import bbk_beam.mtRooms.db.exception.DbQueryException;
import eadjlib.datastructure.ObjectTable;
import eadjlib.logger.Logger;

import java.util.HashMap;

class DatabaseChecker {
    private final Logger log = Logger.getLoggerInstance(DatabaseChecker.class.getName());
    //Update when adding/removing tables from schema
    private final int reservation_table_count = 2;
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
        if (checkTable_Building(db)) check_count++;
        if (checkTable_Floor(db)) check_count++;

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
        //TODO UserAccDb table checks

        return user_table_count == check_count;
    }

    private boolean checkTable_Building(IDatabase db) {
        final int column_count = 7;
        String query = "PRAGMA table_info( Building )";
        try {
            boolean ok_flag = true;
            int checked = 0;
            ObjectTable table = db.pull(query);
            for (int i = 1; i <= table.rowCount(); i++) {
                HashMap<String, Object> row = table.getRow(i);
                if (row.get("name").equals("id")) {
                    checked++;
                    if (!row.get("type").equals("INTEGER")) {
                        log.log_Error("'Building.id': wrong type (", row.get("type"), ")");
                        ok_flag = false;
                    }
                    if (!row.get("pk").equals(1)) {
                        log.log_Error("'Building.id: not primary key (", row.get("pk"), ")");
                        ok_flag = false;
                    }
                }
                if (row.get("name").equals("name")) {
                    checked++;
                    if (!row.get("type").equals("TEXT")) {
                        log.log_Error("'Building.name': wrong type (", row.get("type"), ")");
                        ok_flag = false;
                    }
                }
                if (row.get("name").equals("address1")) {
                    checked++;
                    if (!row.get("type").equals("VARCHAR(255)")) {
                        log.log_Error("'Building.address1': wrong type (", row.get("type"), ")");
                        ok_flag = false;
                    }
                }
                if (row.get("name").equals("address2")) {
                    checked++;
                    if (!row.get("type").equals("VARCHAR(255)")) {
                        log.log_Error("'Building.address2': wrong type (", row.get("type"), ")");
                        ok_flag = false;
                    }
                }
                if (row.get("name").equals("postcode")) {
                    checked++;
                    if (!row.get("type").equals("VARCHAR(145)")) {
                        log.log_Error("'Building.postcode': wrong type (", row.get("type"), ")");
                        ok_flag = false;
                    }
                }
                if (row.get("name").equals("country")) {
                    checked++;
                    if (!row.get("type").equals("VARCHAR(145)")) {
                        log.log_Error("'Building.country': wrong type (", row.get("type"), ")");
                        ok_flag = false;
                    }
                }
                if (row.get("name").equals("telephone")) {
                    checked++;
                    if (!row.get("type").equals("VARCHAR(50)")) {
                        log.log_Error("'Building.telephone': wrong type (", row.get("type"), ")");
                        ok_flag = false;
                    }
                }
            }
            if (checked != column_count) {
                log.log_Error("'Building' table does not have the required columns (", checked, "/", column_count, ").'");
                ok_flag = false;
            }
            return ok_flag;
        } catch (DbQueryException e) {
            log.log_Error("Issue encountered processing query: ", query);
            log.log_Exception(e);
            return false;
        }
    }

    private boolean checkTable_Floor(IDatabase db) {
        final int column_count = 2;
        String query = "PRAGMA table_info( Floor )";
        try {
            boolean ok_flag = true;
            int checked = 0;
            ObjectTable table = db.pull(query);
            for (int i = 1; i <= table.rowCount(); i++) {
                HashMap<String, Object> row = table.getRow(i);
                if (row.get("name").equals("id")) {
                    checked++;
                    if (!row.get("type").equals("INTEGER")) {
                        log.log_Error("'Floor.id': wrong type (", row.get("type"), ")");
                        ok_flag = false;
                    }
                    if (!row.get("pk").equals(1)) {
                        log.log_Error("'Floor.id: not primary key (", row.get("pk"), ")");
                        ok_flag = false;
                    }
                }
                if (row.get("name").equals("building_id")) {
                    checked++;
                    if (!row.get("type").equals("INTEGER")) {
                        log.log_Error("'Floor.building_id': wrong type (", row.get("type"), ")");
                        ok_flag = false;
                    }
                    if (!row.get("pk").equals(2)) {
                        log.log_Error("'Floor.building_id: not primary key (", row.get("pk"), ")");
                        ok_flag = false;
                    }
                }
            }
            if (checked != column_count) {
                log.log_Error("'Floor' table does not have the required columns (", checked, "/", column_count, ").'");
                ok_flag = false;
            }
            return ok_flag;
        } catch (DbQueryException e) {
            log.log_Error("Issue encountered processing query: ", query);
            log.log_Exception(e);
            return false;
        }
    }
}
