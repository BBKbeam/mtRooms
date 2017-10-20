package bbk_beam.mtRooms.db.database;

import bbk_beam.mtRooms.db.exception.DbEmptyException;

/**
 * User Accounts database interface
 */
public interface IUserAccDb extends IDatabase {
    /**
     * Builds the tables for the user accounts database
     *
     * @return Success
     */
    public boolean setupUserAccDB();

    /**
     * Checks the structure of the user accounts database
     *
     * @return Success
     * @throws DbEmptyException when non of the user account tables exist
     */
    public boolean checkUserAccDB() throws DbEmptyException;
}
