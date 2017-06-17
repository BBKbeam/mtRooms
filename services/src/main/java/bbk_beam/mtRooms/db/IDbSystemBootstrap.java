package bbk_beam.mtRooms.db;

import bbk_beam.mtRooms.db.exception.DbBootstrapException;

/**
 * Bootstrap interface for initiating the Db system
 */
public interface IDbSystemBootstrap {
    /**
     * Initialises the reservation database system
     *
     * @param database File name for the database
     * @throws DbBootstrapException when error occurs during bootstrapping
     */
    public void init(String database) throws DbBootstrapException;

    /**
     * Gets the running instance of the UserAccDbAccess
     *
     * @return UserAccDbAccess instance
     * @throws DbBootstrapException when bootstrap was not initiated
     */
    public IUserAccDbAccess getUserAccDbAccess() throws DbBootstrapException;

    /**
     * Gets the running instance of the ReservationDbAccess
     *
     * @return ReservationDbAccess instance
     * @throws DbBootstrapException when bootstrap was not initiated
     */
    public IQueryDB getReservationDbAccess() throws DbBootstrapException;
}
