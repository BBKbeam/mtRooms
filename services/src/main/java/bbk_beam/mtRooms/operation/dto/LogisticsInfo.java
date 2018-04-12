package bbk_beam.mtRooms.operation.dto;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Logistics information container class
 */
public class LogisticsInfo {
    private List<LogisticsEntry> entries;

    /**
     * Constructor
     */
    public LogisticsInfo() {
        this.entries = new ArrayList<>();
    }

    /**
     * Add a logistics entry to the report
     *
     * @param entry Entry to add
     */
    public void addEntry(LogisticsEntry entry) {
        this.entries.add(entry);
    }

    /**
     * Get all the entries from the report
     *
     * @return List of entries
     */
    public List<LogisticsEntry> getEntries() {
        return Collections.unmodifiableList(this.entries);
    }

    /**
     * Gets the number of logistic entries
     *
     * @return Entry count
     */
    public int entryCount() {
        return this.entries.size();
    }
}
