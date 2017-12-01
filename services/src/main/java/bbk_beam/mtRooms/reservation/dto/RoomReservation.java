package bbk_beam.mtRooms.reservation.dto;

import java.util.Date;

public class RoomReservation {
    private Room room;
    private Date timestamp_in;
    private Date timestamp_out;
    private String note;
    private RoomPrice price;
    private boolean cancelled_flag;

    /**
     * Constructor
     *
     * @param room           Room
     * @param timestamp_in   Room reservation start timestamp
     * @param timestamp_out  Room reservation end timestamp
     * @param note           Note
     * @param roomPrice      Price for the reservation of the room
     * @param cancelled_flag Cancelled room reservation flag
     */
    public RoomReservation(Room room,
                           Date timestamp_in,
                           Date timestamp_out,
                           String note,
                           RoomPrice roomPrice,
                           boolean cancelled_flag) {
        this.room = room;
        this.timestamp_in = timestamp_in;
        this.timestamp_out = timestamp_out;
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
        if (!timestamp_in.equals(that.timestamp_in)) return false;
        if (!timestamp_out.equals(that.timestamp_out)) return false;
        if (!note.equals(that.note)) return false;
        return price.equals(that.price);
    }

    @Override
    public int hashCode() {
        int result = room.hashCode();
        result = 31 * result + timestamp_in.hashCode();
        result = 31 * result + timestamp_out.hashCode();
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
                ", note: '" + note + '\'' +
                ", price: " + price +
                ", cancelled: " + (cancelled_flag ? "true" : "false") +
                " }";
    }
}
