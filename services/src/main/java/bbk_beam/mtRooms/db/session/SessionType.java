package bbk_beam.mtRooms.db.session;

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
