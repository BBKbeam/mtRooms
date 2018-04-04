package bbk_beam.mtRooms.reservation.dto;

import bbk_beam.mtRooms.db.TimestampConverter;

import java.util.Date;

public class RoomReservation {
    private Room room;
    private Date timestamp_in;
    private Date timestamp_out;
    private String note;
    private RoomPrice price;
    private Integer seated_count;
    private boolean catering_flag;
    private boolean cancelled_flag;

    /**
     * Constructor
     *
     * @param room           Room
     * @param timestamp_in   Room reservation start timestamp
     * @param timestamp_out  Room reservation end timestamp
     * @param seated_count   Actual seated count
     * @param catering_flag  Catering option flag
     * @param note           Note
     * @param roomPrice      Price for the reservation of the room
     * @param cancelled_flag Cancelled room reservation flag
     */
    public RoomReservation(Room room,
                           Date timestamp_in,
                           Date timestamp_out,
                           Integer seated_count,
                           boolean catering_flag,
                           String note,
                           RoomPrice roomPrice,
                           boolean cancelled_flag) {
        this.room = room;
        this.timestamp_in = timestamp_in;
        this.timestamp_out = timestamp_out;
        this.seated_count = seated_count;
        this.catering_flag = catering_flag;
        this.note = note;
        this.price = roomPrice;
        this.cancelled_flag = cancelled_flag;
    }

    /**
     * Gets the room reserved
     *
     * @return Room
     */
    public Room room() {
        return this.room;
    }

    /**
     * Gets the room's reservation start timestamp
     *
     * @return Reservation start timestamp
     */
    public Date reservationStart() {
        return this.timestamp_in;
    }

    /**
     * Gets the room's reservation end timestamp
     *
     * @return Reservation end timestamp
     */
    public Date reservationEnd() {
        return this.timestamp_out;
    }

    /**
     * Gets the room's reservation seated count
     *
     * @return Seated count
     */
    public Integer seatedCount() {
        return this.seated_count;
    }

    /**
     * Gets the room reservation's catering requirement option
     *
     * @return Catering option selected state
     */
    public boolean hasCateringRequired() {
        return this.catering_flag;
    }

    /**
     * Gets the note entry for the room
     *
     * @return Note
     */
    public String note() {
        return this.note;
    }

    /**
     * Gets the room's set price
     *
     * @return Room price
     */
    public RoomPrice price() {
        return this.price;
    }


    /**
     * Gets the cancelled status of the reserved room
     *
     * @return Cancelled flag
     */
    public boolean isCancelled() {
        return this.cancelled_flag;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RoomReservation that = (RoomReservation) o;

        if (cancelled_flag != that.cancelled_flag) return false;
        if (!room.equals(that.room)) return false;
        String thisTimestampIN = TimestampConverter.getUTCTimestampString(timestamp_in);
        String thatTimestampIN = TimestampConverter.getUTCTimestampString(that.timestamp_in);
        if (thisTimestampIN.compareTo(thatTimestampIN) != 0) return false;
        String thisTimestampOUT = TimestampConverter.getUTCTimestampString(timestamp_out);
        String thatTimestampOUT = TimestampConverter.getUTCTimestampString(that.timestamp_out);
        if (thisTimestampOUT.compareTo(thatTimestampOUT) != 0) return false;
        if (!seated_count.equals(that.seated_count)) return false;
        if (catering_flag != that.catering_flag) return false;
        if (!note.equals(that.note)) return false;
        return price.equals(that.price);
    }

    @Override
    public int hashCode() {
        int result = room.hashCode();
        result = 31 * result + TimestampConverter.getUTCTimestampString(timestamp_in).hashCode();
        result = 31 * result + TimestampConverter.getUTCTimestampString(timestamp_out).hashCode();
        result = 31 * result + seated_count.hashCode();
        result = 31 * result + (catering_flag ? 1 : 0);
        result = 31 * result + note.hashCode();
        result = 31 * result + price.hashCode();
        result = 31 * result + (cancelled_flag ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return "RoomReservation{ " +
                "room: " + room +
                ", timestamp_in: " + timestamp_in +
                ", timestamp_out: " + timestamp_out +
                ", seated_count: " + seated_count +
                ", catering: " + (catering_flag ? "yes" : "no") +
                ", note: '" + note + '\'' +
                ", price: " + price +
                ", cancelled: " + (cancelled_flag ? "true" : "false") +
                " }";
    }
}
