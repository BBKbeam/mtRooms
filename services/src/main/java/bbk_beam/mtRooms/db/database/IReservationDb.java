package bbk_beam.mtRooms.db.database;

import bbk_beam.mtRooms.db.exception.DbEmptyException;

/**
 * Reservation database interface
 */
public interface IReservationDb extends IDatabase {
    /**
     * Builds the tables for the reservation database
     *
     * @return Success
     */
    boolean setupReservationDB();

    /**
     * Checks the structure of the reservation database
     *
     * @return Success
     * @throws DbEmptyException when non of the required reservation tables exist
     */
    boolean checkReservationDB() throws DbEmptyException;
}
