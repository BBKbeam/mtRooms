package bbk_beam.mtRooms.ui.model.logistics;

import bbk_beam.mtRooms.operation.dto.LogisticsEntry;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.text.SimpleDateFormat;

public class LogisticsModel {
    static private final String CHECK = "\u2713";
    static private final SimpleDateFormat dateFormat = new SimpleDateFormat("YYYY/MM/dd - HH:mm:ss");
    private IntegerProperty building_id = new SimpleIntegerProperty();
    private IntegerProperty floor_id = new SimpleIntegerProperty();
    private IntegerProperty room_id = new SimpleIntegerProperty();
    private StringProperty in = new SimpleStringProperty();
    private StringProperty out = new SimpleStringProperty();
    private StringProperty seated = new SimpleStringProperty();
    private StringProperty has_catering = new SimpleStringProperty();
    private StringProperty note = new SimpleStringProperty();


    /**
     * Constructor
     *
     * @param entry LogisticsEntry DTO
     */
    public LogisticsModel(LogisticsEntry entry) {
        this.building_id.set(entry.getRoom().buildingID());
        this.floor_id.set(entry.getRoom().floorID());
        this.room_id.set(entry.getRoom().id());
        this.in.set(dateFormat.format(entry.getReservationStart()));
        this.out.set(dateFormat.format(entry.getReservationEnd()));
        this.seated.set(entry.getSeatedCount() + "/" + entry.getRoom().capacity());
        this.has_catering.set((entry.hasCatering() ? CHECK : ""));
        this.note.set(entry.getNotes());
    }

    /**
     * Gets the Building ID property
     *
     * @return Building ID property
     */
    public IntegerProperty buildingIdProperty() {
        return building_id;
    }

    /**
     * Gets the Floor ID property
     *
     * @return Floor ID property
     */
    public IntegerProperty floorIdProperty() {
        return floor_id;
    }

    /**
     * Gets the Room ID property
     *
     * @return Room ID property
     */
    public IntegerProperty roomIdProperty() {
        return room_id;
    }

    /**
     * Gets the Timestamp IN property
     *
     * @return Timestamp IN property
     */
    public StringProperty inProperty() {
        return in;
    }

    /**
     * Gets the Timestamp OUT property
     *
     * @return Timestamp OUT property
     */
    public StringProperty outProperty() {
        return out;
    }

    /**
     * Gets the seating property
     *
     * @return Seating property
     */
    public StringProperty seatedProperty() {
        return seated;
    }

    /**
     * Gets the catering property
     *
     * @return Catering property
     */
    public StringProperty cateringFlagProperty() {
        return has_catering;
    }

    /**
     * Gets the note property
     *
     * @return Note property
     */
    public StringProperty noteProperty() {
        return note;
    }
}
