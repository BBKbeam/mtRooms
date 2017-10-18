package bbk_beam.mtRooms.db.session;

/**
 * SessionType container
 * The type value dictates the access level
 * An access level has the rights of all higher denominations as well as its own.
 * i.e.: The lowest value has all access whereas the highest only has it's own access.
 */
public enum SessionType {
    ADMIN(0),
    USER(1);

    private final int level;

    /**
     * Constructor
     *
     * @param level Access level
     */
    SessionType(int level) {
        this.level = level;
    }

    /**
     * Gets the access level
     *
     * @return Access level value
     */
    public int level() {
        return this.level;
    }
}
