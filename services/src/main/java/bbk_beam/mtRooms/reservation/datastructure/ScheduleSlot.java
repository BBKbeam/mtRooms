package bbk_beam.mtRooms.reservation.datastructure;

/**
 * Schedule-slot holder class
 */
class ScheduleSlot {
    private TimestampUTC start;
    private TimestampUTC end;

    /**
     * Constructor
     *
     * @param start Start timestamp string (UTC)
     * @param end   End timestamp string (UTC)
     */
    ScheduleSlot(String start, String end) {
        this.start = new TimestampUTC(start);
        this.end = new TimestampUTC(end);
    }

    /**
     * Gets the start TimestampUTC of the schedule slot
     *
     * @return Start TimestampUTC
     */
    TimestampUTC start() {
        return this.start;
    }

    /**
     * Gets the end TimestampUTC of the schedule slot
     *
     * @return End TimestampUTC
     */
    TimestampUTC end() {
        return this.end;
    }
}
