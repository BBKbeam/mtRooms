package bbk_beam.mtRooms.ui.model.frontdesk;

import bbk_beam.mtRooms.reservation.dto.RoomReservation;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

public class RoomReservationModel {
    static private final String CHECK = "\u2713";
    static private final String CROSS = "\u274C";
    static private final SimpleDateFormat dateFormat = new SimpleDateFormat("YYYY/MM/dd - HH:mm:ss");
    private RoomReservation roomReservation;
    private IntegerProperty building_id = new SimpleIntegerProperty();
    private IntegerProperty floor_id = new SimpleIntegerProperty();
    private IntegerProperty room_id = new SimpleIntegerProperty();
    private StringProperty in = new SimpleStringProperty();
    private StringProperty out = new SimpleStringProperty();
    private StringProperty duration = new SimpleStringProperty();
    private IntegerProperty seated = new SimpleIntegerProperty();
    private StringProperty has_catering = new SimpleStringProperty();
    private StringProperty is_cancelled = new SimpleStringProperty();
    private StringProperty has_note = new SimpleStringProperty();

    /**
     * Calculates the difference between 2 Dates and returns the duration
     *
     * @param start Start Date
     * @param end   End Date
     * @return Duration as a String formatted hh:mm
     */
    private static String calcDuration(Date start, Date end) {
        ZonedDateTime from = ZonedDateTime.ofInstant(start.toInstant(), ZoneId.of("UTC"));
        ZonedDateTime to = ZonedDateTime.ofInstant(end.toInstant(), ZoneId.of("UTC"));
        Duration duration = Duration.between(from, to);
        long duration_mns = duration.toMinutes();
        long mns = duration_mns % 60;
        long hours = duration_mns / 60;
        return String.valueOf(hours) + ":" + (mns < 10 ? "0" + String.valueOf(mns) : String.valueOf(mns));
    }

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
        in.set(dateFormat.format(roomReservation.reservationStart()));
        out.set(dateFormat.format(roomReservation.reservationEnd()));
        duration.set(calcDuration(roomReservation.reservationStart(), roomReservation.reservationEnd()));
        seated.set(roomReservation.seatedCount());
        has_catering.set(roomReservation.hasCateringRequired() ? CHECK : "");
        is_cancelled.set(roomReservation.isCancelled() ? CROSS : "");
        has_note.set(roomReservation.note().isEmpty() ? "" : CHECK);
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
        return roomReservation.reservationStart();
    }

    /**
     * Gets the start timestamp property
     *
     * @return Starting timestamp property
     */
    public StringProperty inProperty() {
        return in;
    }

    /**
     * Gets the ending timestamp
     *
     * @return Ending timestamp
     */
    public Date outTimestamp() {
        return roomReservation.reservationEnd();
    }

    /**
     * Gets the ending timestamp property
     *
     * @return Ending timestamp property
     */
    public StringProperty outProperty() {
        return out;
    }

    /**
     * Gets the duration of the booking
     *
     * @return Duration of the booking
     */
    public String duration() {
        return this.duration.get();
    }

    /**
     * Gets the duration property of the booking
     *
     * @return Duration property of the booking
     */
    public StringProperty durationProperty() {
        return this.duration;
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
     * Gets the cancelled status mark
     *
     * @return Cancelled status mark
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

    /**
     * Gets the note status mark
     *
     * @return Note status mark
     */
    public String hasNote() {
        return has_note.get();
    }

    /**
     * Gets the note mark property
     *
     * @return Has note mark property
     */
    public StringProperty hasNoteProperty() {
        return has_note;
    }
}
