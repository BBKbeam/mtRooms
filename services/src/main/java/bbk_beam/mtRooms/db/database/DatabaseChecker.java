package bbk_beam.mtRooms.db.database;

import bbk_beam.mtRooms.db.exception.EmptyDatabaseException;

public class DatabaseChecker {
    //TODO DB Checker tool
    //i.e.: make sure the db connected to has all the correct tables and columns
    // ("PRAGMA table_info( table_name )") with the correct types and key restrictions

    /**
     * Runs all the checks for the Reservation database
     *
     * @param db Database instance
     * @return Success
     * @throws EmptyDatabaseException when non of the tables required exists in the database
     */
    public boolean checkReservationDB(IDatabase db) throws EmptyDatabaseException {
        //TODO
        return false;
    }

    /**
     * Runs all the checks for the User Accounts database
     *
     * @param db Database instance
     * @return Success
     * @throws EmptyDatabaseException when non of the tables required exists in the database
     */
    public boolean checkUserAccDB(IDatabase db) throws EmptyDatabaseException {
        //TODO
        return false;
    }
}
