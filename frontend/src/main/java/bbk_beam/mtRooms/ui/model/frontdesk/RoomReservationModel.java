package bbk_beam.mtRooms.ui.model.frontdesk;

import bbk_beam.mtRooms.reservation.dto.RoomReservation;
import javafx.beans.property.*;

import java.util.Date;

public class RoomReservationModel {
    static private final String CHECK = "\u2713";
    static private final String CROSS = "\u274C";
    private RoomReservation roomReservation;
    private IntegerProperty building_id = new SimpleIntegerProperty();
    private IntegerProperty floor_id = new SimpleIntegerProperty();
    private IntegerProperty room_id = new SimpleIntegerProperty();
    private ObjectProperty<Date> in = new SimpleObjectProperty<>();
    private ObjectProperty<Date> out = new SimpleObjectProperty<>();
    private IntegerProperty seated = new SimpleIntegerProperty();
    private StringProperty has_catering = new SimpleStringProperty();
    private StringProperty is_cancelled = new SimpleStringProperty();

    /**
     * Constructor
     *
     * @param roomReservation RoomReservation DTO
     */
    public RoomReservationModel(RoomReservation roomReservation) {
        this.roomReservation = roomReservation;
        building_id.set(roomReservation.room().buildingID());
        floor_id.set(roomReservation.room().floorID());
        room_id.set(roomReservation.room().id());
        in.set(roomReservation.reservationStart());
        out.set(roomReservation.reservationEnd());
        seated.set(roomReservation.seatedCount());
        has_catering.set(roomReservation.hasCateringRequired() ? CHECK : "");
        is_cancelled.set(roomReservation.isCancelled() ? CROSS : "");
    }

    /**
     * Gets the RoomReservation DTO
     *
     * @return RoomReservation DTO
     */
    public RoomReservation getRoomReservation() {
        return this.roomReservation;
    }

    /**
     * Gets the building ID
     *
     * @return Building ID
     */
    public Integer buildingId() {
        return building_id.get();
    }

    /**
     * Gets the building ID property
     *
     * @return Building ID property
     */
    public IntegerProperty buildingIdProperty() {
        return building_id;
    }

    /**
     * Gets the floor ID
     *
     * @return Floor ID
     */
    public Integer floorId() {
        return floor_id.get();
    }

    /**
     * Gets the floor ID property
     *
     * @return Floor ID property
     */
    public IntegerProperty floorIdProperty() {
        return floor_id;
    }

    /**
     * Gets the room ID
     *
     * @return Room ID
     */
    public Integer roomId() {
        return room_id.get();
    }

    /**
     * Gets the room ID property
     *
     * @return Room ID property
     */
    public IntegerProperty roomIdProperty() {
        return room_id;
    }

    /**
     * Gets the start timestamp
     *
     * @return Starting timestamp
     */
    public Date inTimestamp() {
        return in.get();
    }

    /**
     * Gets the start timestamp property
     *
     * @return Starting timestamp property
     */
    public ObjectProperty<Date> inProperty() {
        return in;
    }

    /**
     * Gets the ending timestamp
     *
     * @return Ending timestamp
     */
    public Date outTimestamp() {
        return out.get();
    }

    /**
     * Gets the ending timestamp property
     *
     * @return Ending timestamp property
     */
    public ObjectProperty<Date> outProperty() {
        return out;
    }

    /**
     * Gets the number of seats required
     *
     * @return Number of seats
     */
    public Integer seated() {
        return seated.get();
    }

    /**
     * Gets the number of seats required property
     *
     * @return Number of seats property
     */
    public IntegerProperty seatedProperty() {
        return seated;
    }

    /**
     * Gets the catering required flag
     *
     * @return Catering flag
     */
    public String hasCatering() {
        return has_catering.get();
    }

    /**
     * Gets the catering required flag property
     *
     * @return Catering flag property
     */
    public StringProperty cateringProperty() {
        return has_catering;
    }

    /**
     * Gets the cancelled status
     *
     * @return Cancelled status
     */
    public String isCancelled() {
        return is_cancelled.get();
    }

    /**
     * Gets the cancelled status property
     *
     * @return Cancelled status property
     */
    public StringProperty cancelledProperty() {
        return is_cancelled;
    }
}
