package bbk_beam.mtRooms.reservation.dto;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class Reservation {
    private Customer customer;
    private Integer id;
    private Integer payment_method_id;
    private Discount discount;
    private List<Room> rooms;

    /**
     * Constructor
     *
     * @param customer          Customer DTO
     * @param id                Reservation ID
     * @param payment_method_id Payment method ID
     * @param discount          Discount DTO
     */
    public Reservation(Customer customer, Integer id, Integer payment_method_id, Discount discount) {
        this.customer = customer;
        this.id = id;
        this.payment_method_id = payment_method_id;
        this.discount = discount;
        this.rooms = new ArrayList<>();
    }

    /**
     * Constructor
     *
     * @param customer          Customer DTO
     * @param id                Reservation ID
     * @param payment_method_id Payment method ID
     * @param discount          Discount DTO
     * @param rooms             List of reserved rooms
     */
    public Reservation(Customer customer, Integer id, Integer payment_method_id, Discount discount, List<Room> rooms) {
        this.customer = customer;
        this.id = id;
        this.payment_method_id = payment_method_id;
        this.discount = discount;
        this.rooms = rooms;
    }

    /**
     * Gets the customer DTO
     *
     * @return Copy of the Customer DTO
     */
    public Customer customer() {
        return new Customer(this.customer);
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
     * Gets the reservation's payment method ID
     *
     * @return Payment method ID
     */
    public Integer paymentMethodID() {
        return this.payment_method_id;
    }

    /**
     * Sets the payment method ID
     *
     * @param payment_method_id Payment method ID
     */
    public void setPaymentMethod(Integer payment_method_id) {
        this.payment_method_id = payment_method_id;
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
     * @return list of Room DTOs
     */
    public Collection<Room> rooms() {
        return Collections.unmodifiableCollection(this.rooms);
    }

    /**
     * Adds a room to the reservation
     *
     * @param room Room DTO to add
     */
    public void addRoom(Room room) {
        this.rooms.add(room);
    }
}
