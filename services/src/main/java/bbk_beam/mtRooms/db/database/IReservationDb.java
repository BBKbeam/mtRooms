package bbk_beam.mtRooms.db.database;

import bbk_beam.mtRooms.db.exception.EmptyDatabaseException;

/**
 * Reservation database interface
 */
public interface IReservationDb extends IDatabase {
    /**
     * Builds the tables for the reservation database
     *
     * @return Success
     */
    public boolean setupReservationDB();

    /**
     * Checks the structure of the reservation database
     *
     * @return Success
     * @throws EmptyDatabaseException when non of the required reservation tables exist
     */
    public boolean checkReservationDB() throws EmptyDatabaseException;
}
