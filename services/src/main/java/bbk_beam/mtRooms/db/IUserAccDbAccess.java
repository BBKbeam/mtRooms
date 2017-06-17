package bbk_beam.mtRooms.db;

public interface IUserAccDbAccess extends IDbAccess {
    /**
     * Checks the validity of a session ID
     *
     * @param session_id Session ID
     * @return Valid state
     */
    public boolean checkValidity(String session_id);
}
