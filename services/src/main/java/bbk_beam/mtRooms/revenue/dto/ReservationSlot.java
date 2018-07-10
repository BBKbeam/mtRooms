package bbk_beam.mtRooms.revenue.dto;

import bbk_beam.mtRooms.common.TimeSpan;
import bbk_beam.mtRooms.common.TimestampUTC;

import java.io.Serializable;

/**
 * Representation of a reservation slot with a start and an end
 * <p>
 * Keeps track of the number of reservations made in a slot.
 * </p>
 */
public class ReservationSlot extends TimeSpan implements Serializable {
    private Integer reservation_count;

    /**
     * Constructor
     *
     * @param start Start of slot in the day
     * @param end   End of slot in the day
     */
    public ReservationSlot(TimestampUTC start, TimestampUTC end) {
        super(start, end);
        this.reservation_count = 0;
    }

    /**
     * Constructor
     *
     * @param start      Start of slot in the day
     * @param end        End of slot in the day
     * @param base_count Starting count for the number of reservations on that slot
     */
    public ReservationSlot(TimestampUTC start, TimestampUTC end, Integer base_count) {
        super(start, end);
        this.reservation_count = base_count;
    }

    /**
     * Increment the reservation count on the slot
     *
     * @return Post-increment count
     */
    public Integer incrementReservationCount() {
        return (++this.reservation_count);
    }

    /**
     * Gets the reservation count for the slot
     *
     * @return Reservation count
     */
    public Integer reservationCount() {
        return this.reservation_count;
    }

    @Override
    public String toString() {
        return this.start() + " -> " + this.end() + ": " + reservation_count;
    }
}
