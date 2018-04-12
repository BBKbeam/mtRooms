package bbk_beam.mtRooms.operation.dto;

import java.util.Date;

/**
 * Container class holding details of a logistic entry for the report
 */
public class LogisticsEntry {
    OpsRoom room;
    private Date timestamp_in;
    private Date timestamp_out;
    private Integer seated_count;
    private boolean catering_flag;
    private String notes;

    /**
     * Constructor
     *
     * @param room          OpsRoom DTO
     * @param timestamp_in  Start timestamp
     * @param timestamp_out End timestamp
     * @param seated_count  Number of chairs required
     * @param catering_flag Catering flag
     * @param notes         Notes
     */
    public LogisticsEntry(
            OpsRoom room,
            Date timestamp_in,
            Date timestamp_out,
            Integer seated_count,
            boolean catering_flag,
            String notes) {
        this.room = room;
        this.timestamp_in = timestamp_in;
        this.timestamp_out = timestamp_out;
        this.seated_count = seated_count;
        this.catering_flag = catering_flag;
        this.notes = notes;
    }

    /**
     * Gets the room for the entry
     *
     * @return OpsRoom DTO
     */
    public OpsRoom getRoom() {
        return this.room;
    }

    /**
     * Gets the start timestamp of the room booking
     *
     * @return Start timestamp
     */
    public Date getReservationStart() {
        return this.timestamp_in;
    }

    /**
     * Gets the end timestamp of the room booking
     *
     * @return End timestamp
     */
    public Date getReservationEnd() {
        return this.timestamp_out;
    }

    /**
     * Gets the number of chairs required
     *
     * @return Seated count
     */
    public Integer getSeatedCount() {
        return this.seated_count;
    }

    /**
     * Gets the catering flag
     *
     * @return catering option requirement state
     */
    public boolean hasCatering() {
        return catering_flag;
    }

    /**
     * Gets the entry's notes
     *
     * @return Notes
     */
    public String getNotes() {
        return this.notes;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        sb.append("[");
        sb.append(room.buildingID()).append('.');
        sb.append(room.floorID()).append('.');
        sb.append(room.id()).append("] { ");
        sb.append(timestamp_in).append(" -> ").append(timestamp_out).append(" } (");
        sb.append(seated_count).append('/').append(room.capacity()).append(") ");
        sb.append(this.hasCatering() ? "Catering" : "No catering");
        if (!notes.isEmpty())
            sb.append(" \"").append(notes).append('\"');
        return sb.toString();
    }
}
