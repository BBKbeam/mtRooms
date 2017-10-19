package bbk_beam.mtRooms.db.database;

import bbk_beam.mtRooms.db.exception.DbEmptyException;
import bbk_beam.mtRooms.db.exception.DbQueryException;
import eadjlib.datastructure.ObjectTable;
import eadjlib.logger.Logger;

import java.util.HashMap;

class DatabaseChecker {
    private final Logger log = Logger.getLoggerInstance(DatabaseChecker.class.getName());
    //Update when adding/removing tables from schema
    private final int reservation_table_count = 11;
    private final int user_table_count = 2;
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
        if (checkTable_RoomCategory(db)) check_count++;
        if (checkTable_RoomPrice(db)) check_count++;
        if (checkTable_RoomFixtures(db)) check_count++;
        if (checkTable_Room(db)) check_count++;
        if (checkTable_Room_has_RoomPrice(db)) check_count++;
        if (checkTable_Room_had_RoomFixtures(db)) check_count++;
        if (checkTable_PaymentMethod(db)) check_count++;
        if (checkTable_DiscountCategory(db)) check_count++;
        if (checkTable_Discount(db)) check_count++;
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
        if (checkTable_AccountType(db)) check_count++;
        if (checkTable_UserAccount(db)) check_count++;
        return user_table_count == check_count;
    }

    /**
     * Check column against expected properties
     *
     * @param colProperty    Column properties expected
     * @param columnMetaData Column meta-data from database
     * @return Validity
     */
    private boolean checkColumn(ColProperty colProperty,
                                HashMap<String, Object> columnMetaData) {
        boolean ok_flag = true;
        if (!columnMetaData.get("type").equals(colProperty.getType())) {
            log.log_Error(colProperty, " (type): expected '", colProperty.getType(), "', got '", columnMetaData.get("type"), "'");
            ok_flag = false;
        }
        if (!columnMetaData.get("notnull").equals(colProperty.isNotNull())) {
            log.log_Error(colProperty, " (notnull): expected '", colProperty.isNotNull(), "', got '", columnMetaData.get("notnull"), "'.");
            ok_flag = false;
        }
        if (columnMetaData.get("dflt_value") != null && !columnMetaData.get("dflt_value").equals(colProperty.getDefaultValue())) {
            log.log_Error(colProperty, " (dflt_value): expected '", colProperty.getDefaultValue(), "', got '", columnMetaData.get("dflt_value"), "'.");
            ok_flag = false;
        }
        if (!columnMetaData.get("pk").equals(colProperty.getPk())) {
            log.log_Error(colProperty, " (pk): expected '", colProperty.getPk(), "', got '", columnMetaData.get("pk"), "'.");
            ok_flag = false;
        }
        return ok_flag;
    }

    //=========================================Reservation Tables ======================================================
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
                    ok_flag = checkColumn(
                            new ColProperty("Building", "id", "INTEGER", true, null, 1),
                            row
                    );
                }
                if (row.get("name").equals("name")) {
                    checked++;
                    ok_flag = checkColumn(
                            new ColProperty("Building", "name", "TEXT", true, null, 0),
                            row
                    );
                }
                if (row.get("name").equals("address1")) {
                    checked++;
                    ok_flag = checkColumn(
                            new ColProperty("Building", "address1", "VARCHAR(255)", true, null, 0),
                            row
                    );
                }
                if (row.get("name").equals("address2")) {
                    checked++;
                    ok_flag = checkColumn(
                            new ColProperty("Building", "address2", "VARCHAR(255)", false, null, 0),
                            row
                    );
                }
                if (row.get("name").equals("postcode")) {
                    checked++;
                    ok_flag = checkColumn(
                            new ColProperty("Building", "postcode", "VARCHAR(145)", false, null, 0),
                            row
                    );
                }
                if (row.get("name").equals("country")) {
                    checked++;
                    ok_flag = checkColumn(
                            new ColProperty("Building", "country", "VARCHAR(145)", false, null, 0),
                            row
                    );
                }
                if (row.get("name").equals("telephone")) {
                    checked++;
                    ok_flag = checkColumn(
                            new ColProperty("Building", "telephone", "VARCHAR(50)", false, null, 0),
                            row
                    );
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
        final int column_count = 3;
        String query = "PRAGMA table_info( Floor )";
        try {
            boolean ok_flag = true;
            int checked = 0;
            ObjectTable table = db.pull(query);
            for (int i = 1; i <= table.rowCount(); i++) {
                HashMap<String, Object> row = table.getRow(i);
                if (row.get("name").equals("id")) {
                    checked++;
                    ok_flag = checkColumn(
                            new ColProperty("Floor", "id", "INTEGER", true, null, 1),
                            row
                    );
                }
                if (row.get("name").equals("building_id")) {
                    checked++;
                    ok_flag = checkColumn(
                            new ColProperty("Floor", "building_id", "INTEGER", true, null, 2),
                            row
                    );
                }
                if (row.get("name").equals("description")) {
                    checked++;
                    ok_flag = checkColumn(
                            new ColProperty("Floor", "description", "VARCHAR(50)", true, null, 0),
                            row
                    );
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

    private boolean checkTable_RoomCategory(IDatabase db) {
        final int column_count = 3;
        String query = "PRAGMA table_info( RoomCategory )";
        try {
            boolean ok_flag = true;
            int checked = 0;
            ObjectTable table = db.pull(query);
            for (int i = 1; i <= table.rowCount(); i++) {
                HashMap<String, Object> row = table.getRow(i);
                if (row.get("name").equals("id")) {
                    checked++;
                    ok_flag = checkColumn(
                            new ColProperty("RoomCategory", "id", "INTEGER", true, null, 1),
                            row
                    );
                }
                if (row.get("name").equals("capacity")) {
                    checked++;
                    ok_flag = checkColumn(
                            new ColProperty("RoomCategory", "capacity", "INTEGER", true, null, 0),
                            row
                    );
                }
                if (row.get("name").equals("dimension")) {
                    checked++;
                    ok_flag = checkColumn(
                            new ColProperty("RoomCategory", "dimension", "INTEGER", false, null, 0),
                            row
                    );
                }
            }
            if (checked != column_count) {
                log.log_Error("'RoomCategory' table does not have the required columns (", checked, "/", column_count, ").'");
                ok_flag = false;
            }
            return ok_flag;
        } catch (DbQueryException e) {
            log.log_Error("Issue encountered processing query: ", query);
            log.log_Exception(e);
            return false;
        }
    }

    private boolean checkTable_RoomPrice(IDatabase db) {
        final int column_count = 3;
        String query = "PRAGMA table_info( RoomPrice )";
        try {
            boolean ok_flag = true;
            int checked = 0;
            ObjectTable table = db.pull(query);
            for (int i = 1; i <= table.rowCount(); i++) {
                HashMap<String, Object> row = table.getRow(i);
                if (row.get("name").equals("id")) {
                    checked++;
                    ok_flag = checkColumn(
                            new ColProperty("RoomPrice", "id", "INTEGER", true, null, 1),
                            row
                    );
                }
                if (row.get("name").equals("price")) {
                    checked++;
                    ok_flag = checkColumn(
                            new ColProperty("RoomPrice", "price", "INTEGER", true, null, 0),
                            row
                    );
                }
                if( row.get("name").equals("year")) {
                    checked++;
                    ok_flag = checkColumn(
                            new ColProperty("RoomPrice", "year", "INTEGER", true, null, 0),
                            row
                    );
                }
            }
            if (checked != column_count) {
                log.log_Error("'RoomPrice' table does not have the required columns (", checked, "/", column_count, ").'");
                ok_flag = false;
            }
            return ok_flag;
        } catch (DbQueryException e) {
            log.log_Error("Issue encountered processing query: ", query);
            log.log_Exception(e);
            return false;
        }
    }

    private boolean checkTable_RoomFixtures(IDatabase db) {
        final int column_count = 5;
        String query = "PRAGMA table_info( RoomFixtures )";
        try {
            boolean ok_flag = true;
            int checked = 0;
            ObjectTable table = db.pull(query);
            for (int i = 1; i <= table.rowCount(); i++) {
                HashMap<String, Object> row = table.getRow(i);
                if (row.get("name").equals("id")) {
                    checked++;
                    ok_flag = checkColumn(
                            new ColProperty("RoomFixtures", "id", "INTEGER", true, null, 1),
                            row
                    );
                }
                if (row.get("name").equals("fixed_chairs")) {
                    checked++;
                    ok_flag = checkColumn(
                            new ColProperty("RoomFixtures", "fixed_chairs", "BOOLEAN", true, null, 0),
                            row
                    );
                }
                if (row.get("name").equals("catering_space")) {
                    checked++;
                    ok_flag = checkColumn(
                            new ColProperty("RoomFixtures", "catering_space", "BOOLEAN", true, null, 0),
                            row
                    );
                }
                if (row.get("name").equals("whiteboard")) {
                    checked++;
                    ok_flag = checkColumn(
                            new ColProperty("RoomFixtures", "whiteboard", "BOOLEAN", true, null, 0),
                            row
                    );
                }
                if (row.get("name").equals("projector")) {
                    checked++;
                    ok_flag = checkColumn(
                            new ColProperty("RoomFixtures", "projector", "BOOLEAN", true, null, 0),
                            row
                    );
                }
            }
            if (checked != column_count) {
                log.log_Error("'RoomFixtures' table does not have the required columns (", checked, "/", column_count, ").'");
                ok_flag = false;
            }
            return ok_flag;
        } catch (DbQueryException e) {
            log.log_Error("Issue encountered processing query: ", query);
            log.log_Exception(e);
            return false;
        }
    }

    private boolean checkTable_Room(IDatabase db) {
        final int column_count = 5;
        String query = "PRAGMA table_info( Room )";
        try {
            boolean ok_flag = true;
            int checked = 0;
            ObjectTable table = db.pull(query);
            for (int i = 1; i <= table.rowCount(); i++) {
                HashMap<String, Object> row = table.getRow(i);
                if (row.get("name").equals("id")) {
                    checked++;
                    ok_flag = checkColumn(
                            new ColProperty("Room", "id", "INTEGER", true, null, 1),
                            row
                    );
                }
                if (row.get("name").equals("floor_id")) {
                    checked++;
                    ok_flag = checkColumn(
                            new ColProperty("Room", "floor_id", "INTEGER", true, null, 2),
                            row
                    );
                }
                if (row.get("name").equals("building_id")) {
                    checked++;
                    ok_flag = checkColumn(
                            new ColProperty("Room", "building_id", "INTEGER", true, null, 3),
                            row
                    );
                }
                if (row.get("name").equals("room_category_id")) {
                    checked++;
                    ok_flag = checkColumn(
                            new ColProperty("Room", "room_category_id", "INTEGER", true, null, 0),
                            row
                    );
                }
                if (row.get("name").equals("description")) {
                    checked++;
                    ok_flag = checkColumn(
                            new ColProperty("Room", "description", "VARCHAR(255)", true, null, 0),
                            row
                    );
                }
            }
            if (checked != column_count) {
                log.log_Error("'Room' table does not have the required columns (", checked, "/", column_count, ").'");
                ok_flag = false;
            }
            return ok_flag;
        } catch (DbQueryException e) {
            log.log_Error("Issue encountered processing query: ", query);
            log.log_Exception(e);
            return false;
        }
    }

    private boolean checkTable_Room_has_RoomPrice(IDatabase db) {
        final int column_count = 3;
        String query = "PRAGMA table_info( Room_has_RoomPrice )";
        try {
            boolean ok_flag = true;
            int checked = 0;
            ObjectTable table = db.pull(query);
            for (int i = 1; i <= table.rowCount(); i++) {
                HashMap<String, Object> row = table.getRow(i);
                if (row.get("name").equals("id")) {
                    checked++;
                    ok_flag = checkColumn(
                            new ColProperty("Room_has_RoomPrice", "id", "INTEGER", true, null, 1),
                            row
                    );
                }
                if (row.get("name").equals("room_id")) {
                    checked++;
                    ok_flag = checkColumn(
                            new ColProperty("Room_has_RoomPrice", "room_id", "INTEGER", true, null, 0),
                            row
                    );
                }
                if (row.get("name").equals("price_id")) {
                    checked++;
                    ok_flag = checkColumn(
                            new ColProperty("Room_has_RoomPrice", "price_id", "INTEGER", true, null, 0),
                            row
                    );
                }
            }
            if (checked != column_count) {
                log.log_Error("'Room_has_RoomPrice' table does not have the required columns (", checked, "/", column_count, ").'");
                ok_flag = false;
            }
            return ok_flag;
        } catch (DbQueryException e) {
            log.log_Error("Issue encountered processing query: ", query);
            log.log_Exception(e);
            return false;
        }
    }

    private boolean checkTable_Room_had_RoomFixtures(IDatabase db) {
        final int column_count = 2;
        String query = "PRAGMA table_info( Room_has_RoomFixtures )";
        try {
            boolean ok_flag = true;
            int checked = 0;
            ObjectTable table = db.pull(query);
            for (int i = 1; i <= table.rowCount(); i++) {
                HashMap<String, Object> row = table.getRow(i);
                if (row.get("name").equals("room_id")) {
                    checked++;
                    ok_flag = checkColumn(
                            new ColProperty("Room_has_RoomFixtures", "room_id", "INTEGER", true, null, 1),
                            row
                    );
                }
                if (row.get("name").equals("room_fixture_id")) {
                    checked++;
                    ok_flag = checkColumn(
                            new ColProperty("Room_has_RoomFixtures", "room_fixture_id", "INTEGER", true, null, 2),
                            row
                    );
                }
            }
            if (checked != column_count) {
                log.log_Error("'Room_has_RoomFixtures' table does not have the required columns (", checked, "/", column_count, ").'");
                ok_flag = false;
            }
            return ok_flag;
        } catch (DbQueryException e) {
            log.log_Error("Issue encountered processing query: ", query);
            log.log_Exception(e);
            return false;
        }
    }

    private boolean checkTable_PaymentMethod(IDatabase db) {
        final int column_count = 2;
        String query = "PRAGMA table_info( PaymentMethod )";
        try {
            boolean ok_flag = true;
            int checked = 0;
            ObjectTable table = db.pull(query);
            for (int i = 1; i <= table.rowCount(); i++) {
                HashMap<String, Object> row = table.getRow(i);
                if (row.get("name").equals("id")) {
                    checked++;
                    ok_flag = checkColumn(
                            new ColProperty("PaymentMethod", "id", "INTEGER", true, null, 1),
                            row
                    );
                }
                if (row.get("name").equals("description")) {
                    checked++;
                    ok_flag = checkColumn(
                            new ColProperty("PaymentMethod", "description", "VARCHAR(255)", true, null, 0),
                            row
                    );
                }
            }
            if (checked != column_count) {
                log.log_Error("'PaymentMethod' table does not have the required columns (", checked, "/", column_count, ").'");
                ok_flag = false;
            }
            return ok_flag;
        } catch (DbQueryException e) {
            log.log_Error("Issue encountered processing query: ", query);
            log.log_Exception(e);
            return false;
        }
    }

    private boolean checkTable_DiscountCategory(IDatabase db) {
        final int column_count = 2;
        String query = "PRAGMA table_info( DiscountCategory )";
        try {
            boolean ok_flag = true;
            int checked = 0;
            ObjectTable table = db.pull(query);
            for (int i = 1; i <= table.rowCount(); i++) {
                HashMap<String, Object> row = table.getRow(i);
                if (row.get("name").equals("id")) {
                    checked++;
                    ok_flag = checkColumn(
                            new ColProperty("DiscountCategory", "id", "INTEGER", true, null, 1),
                            row
                    );
                }
                if (row.get("name").equals("description")) {
                    checked++;
                    ok_flag = checkColumn(
                            new ColProperty("DiscountCategory", "description", "VARCHAR(255)", true, null, 0),
                            row
                    );
                }
            }
            if (checked != column_count) {
                log.log_Error("'DiscountCategory' table does not have the required columns (", checked, "/", column_count, ").'");
                ok_flag = false;
            }
            return ok_flag;
        } catch (DbQueryException e) {
            log.log_Error("Issue encountered processing query: ", query);
            log.log_Exception(e);
            return false;
        }
    }

    private boolean checkTable_Discount(IDatabase db) {
        final int column_count = 3;
        String query = "PRAGMA table_info( Discount)";
        try {
            boolean ok_flag = true;
            int checked = 0;
            ObjectTable table = db.pull(query);
            for (int i = 1; i <= table.rowCount(); i++) {
                HashMap<String, Object> row = table.getRow(i);
                if (row.get("name").equals("id")) {
                    checked++;
                    ok_flag = checkColumn(
                            new ColProperty("Discount", "id", "INTEGER", true, null, 1),
                            row
                    );
                }

                if (row.get("name").equals("discount_rate")) {
                    checked++;
                    ok_flag = checkColumn(
                            new ColProperty("Discount", "discount_rate", "DOUBLE", true, null, 0),
                            row
                    );
                }

                if (row.get("name").equals("discount_category_id")) {
                    checked++;
                    ok_flag = checkColumn(
                            new ColProperty("Discount", "discount_category_id", "INTEGER", true, null, 0),
                            row
                    );
                }
            }
            if (checked != column_count) {
                log.log_Error("'Discount' table does not have the required columns (", checked, "/", column_count, ").'");
                ok_flag = false;
            }
            return ok_flag;
        } catch (DbQueryException e) {
            log.log_Error("Issue encountered processing query: ", query);
            log.log_Exception(e);
            return false;
        }
    }

    //========================================User Account Tables ======================================================
    private boolean checkTable_AccountType(IDatabase db) {
        final int column_count = 2;
        String query = "PRAGMA table_info( AccountType )";
        try {
            boolean ok_flag = true;
            int checked = 0;
            ObjectTable table = db.pull(query);
            for (int i = 1; i <= table.rowCount(); i++) {
                HashMap<String, Object> row = table.getRow(i);
                if (row.get("name").equals("id")) {
                    checked++;
                    ok_flag = checkColumn(
                            new ColProperty("AccountType", "id", "INTEGER", true, null, 1),
                            row
                    );
                }
                if (row.get("name").equals("description")) {
                    checked++;
                    ok_flag = checkColumn(
                            new ColProperty("AccountType", "description", "VARCHAR(255)", true, null, 0),
                            row
                    );
                }
            }
            if (checked != column_count) {
                log.log_Error("'AccountType' table does not have the required columns (", checked, "/", column_count, ").'");
                ok_flag = false;
            }
            //TODO check there is admin and user types
            return ok_flag;
        } catch (DbQueryException e) {
            log.log_Error("Issue encountered processing query: ", query);
            log.log_Exception(e);
            return false;
        }
    }

    private boolean checkTable_UserAccount(IDatabase db) {
        final int column_count = 7;
        String query = "PRAGMA table_info( UserAccount )";
        try {
            boolean ok_flag = true;
            int checked = 0;
            ObjectTable table = db.pull(query);
            for (int i = 1; i <= table.rowCount(); i++) {
                HashMap<String, Object> row = table.getRow(i);
                if (row.get("name").equals("id")) {
                    checked++;
                    ok_flag = checkColumn(
                            new ColProperty("UserAccount", "id", "INTEGER", true, null, 1),
                            row
                    );
                }
                if (row.get("name").equals("username")) {
                    checked++;
                    ok_flag = checkColumn(
                            new ColProperty("UserAccount", "username", "TEXT", true, null, 0),
                            row
                    );
                }
                if (row.get("name").equals("pwd_hash")) {
                    checked++;
                    ok_flag = checkColumn(
                            new ColProperty("UserAccount", "pwd_hash", "TEXT", true, null, 0),
                            row
                    );
                }
                if (row.get("name").equals("pwd_salt")) {
                    checked++;
                    ok_flag = checkColumn(
                            new ColProperty("UserAccount", "pwd_salt", "TEXT", true, null, 0),
                            row
                    );
                }
                if (row.get("name").equals("created")) {
                    checked++;
                    ok_flag = checkColumn(
                            new ColProperty("UserAccount", "created", "VARCHAR(255)", true, null, 0),
                            row
                    );
                }
                if (row.get("name").equals("last_pwd_change")) {
                    checked++;
                    ok_flag = checkColumn(
                            new ColProperty("UserAccount", "last_pwd_change", "VARCHAR(255)", true, null, 0),
                            row
                    );
                }
                if (row.get("name").equals("account_type_id")) {
                    checked++;
                    ok_flag = checkColumn(
                            new ColProperty("UserAccount", "account_type_id", "INTEGER", true, null, 0),
                            row
                    );
                }
                if (row.get("name").equals("active_state")) {
                    checked++;
                    ok_flag = checkColumn(
                            new ColProperty("UserAccount", "active_state", "BOOLEAN", true, null, 0),
                            row
                    );
                }
            }
            if (checked != column_count) {
                log.log_Error("'UserAccount' table does not have the required columns (", checked, "/", column_count, ").'");
                ok_flag = false;
            }
            //TODO check there is at least 1 admin root account
            return ok_flag;
        } catch (DbQueryException e) {
            log.log_Error("Issue encountered processing query: ", query);
            log.log_Exception(e);
            return false;
        }
    }
}
