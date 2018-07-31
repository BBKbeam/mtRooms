package bbk_beam.mtRooms.revenue.dto;

import java.io.Serializable;

public class ReservationCost implements Serializable {
    private Integer room_count;
    private Double cost;

    /**
     * Constructor
     */
    public ReservationCost() {
        this.room_count = 0;
        this.cost = 0d;
    }

    /**
     * Constructor
     *
     * @param room_count Number of rooms in reservation
     * @param total_cost Total cost of reserved rooms
     */
    public ReservationCost(
            Integer room_count,
            Double total_cost) {
        this.room_count = room_count;
        this.cost = total_cost;
    }

    /**
     * Gets the room count in reservation
     *
     * @return Number of booked rooms
     */
    public Integer getRoomCount() {
        return this.room_count;
    }

    /**
     * Gets the total raw cost for the reservation
     *
     * @return Raw cost
     */
    public Double getTotalCost() {
        return this.cost;
    }

    /**
     * Adds 1 to the number of rooms in reservation
     *
     * @return Incremented room count
     */
    public Integer incrementRoomCount() {
        return ++this.room_count;
    }

    /**
     * Adds a cost to the current room cost of the reservation
     *
     * @param cost Cost to add
     * @return New total cost
     */
    public Double addToCost(Double cost) {
        return (this.cost += cost);
    }

    @Override
    public String toString() {
        return "ReservationCost{ " +
                "room_count: " + room_count +
                ", cost: " + cost +
                " }";
    }
}
