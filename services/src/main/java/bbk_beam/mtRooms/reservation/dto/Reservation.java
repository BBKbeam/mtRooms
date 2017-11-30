package bbk_beam.mtRooms.reservation.dto;

import java.util.*;

public class Reservation {
    private Integer id;
    private Date created_timestamp;
    private Integer customer_id;
    private Discount discount;
    private List<RoomReservation> rooms_reserved;

    /**
     * Constructor
     *
     * @param id                Reservation ID
     * @param created_timestamp Creation timestamp
     * @param customer_id       Customer ID
     * @param discount          Discount DTO
     */
    public Reservation(Integer id, Date created_timestamp, Integer customer_id, Discount discount) {
        this.id = id;
        this.created_timestamp = created_timestamp;
        this.customer_id = customer_id;
        this.discount = discount;
        this.rooms_reserved = new ArrayList<>();
    }

    /**
     * Constructor
     *
     * @param id                Reservation ID
     * @param created_timestamp Creation timestamp
     * @param customer_id       Customer ID
     * @param discount          Discount DTO
     * @param roomReservations  List of reserved rooms
     */
    public Reservation(Integer id, Date created_timestamp, Integer customer_id, Discount discount, List<RoomReservation> roomReservations) {
        this.id = id;
        this.created_timestamp = created_timestamp;
        this.customer_id = customer_id;
        this.discount = discount;
        this.rooms_reserved = roomReservations;
    }

    /**
     * Gets the customer ID
     *
     * @return Customer's ID
     */
    public Integer customerID() {
        return this.customer_id;
    }

    /**
     * Gets the reservation's ID
     *
     * @return Reservation ID
     */
    public Integer id() {
        return this.id;
    }

    /**
     * Gets the reservation's created timestamp
     *
     * @return Timestamp
     */
    public Date createdTimestamp() {
        return this.created_timestamp;
    }

    /**
     * Gets the discount DTO
     *
     * @return Copy of the Discount DTO
     */
    public Discount discount() {
        return new Discount(this.discount);
    }

    /**
     * Gets the rooms in the reservation
     *
     * @return list of RoomReservation DTOs
     */
    public Collection<RoomReservation> rooms() {
        return Collections.unmodifiableCollection(this.rooms_reserved);
    }

    /**
     * Adds a room to the reservation
     *
     * @param roomReservation RoomReservation DTO to add
     */
    public void addRoom(RoomReservation roomReservation) {
        this.rooms_reserved.add(roomReservation);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Reservation that = (Reservation) o;

        if (!id.equals(that.id)) return false;
        if (!created_timestamp.equals(that.created_timestamp)) return false;
        if (!customer_id.equals(that.customer_id)) return false;
        if (!discount.equals(that.discount)) return false;
        return rooms_reserved.equals(that.rooms_reserved);
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + created_timestamp.hashCode();
        result = 31 * result + customer_id.hashCode();
        result = 31 * result + discount.hashCode();
        result = 31 * result + rooms_reserved.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "[" + id + "]={ "

                + "created_timestamp: " + created_timestamp
                + ", customer_id: " + customer_id
                + ", discount: " + discount
                + ", rooms_reserved: " + rooms_reserved
                + " }";
    }
}
