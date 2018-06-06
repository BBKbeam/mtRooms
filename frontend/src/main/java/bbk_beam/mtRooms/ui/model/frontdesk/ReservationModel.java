package bbk_beam.mtRooms.ui.model.frontdesk;

import bbk_beam.mtRooms.reservation.dto.Reservation;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ReservationModel {
    Reservation reservation;
    private IntegerProperty reservationId = new SimpleIntegerProperty();
    private StringProperty created = new SimpleStringProperty("");

    /**
     * Constructor
     *
     * @param reservation Reservation DTO
     */
    public ReservationModel(Reservation reservation) {
        this.reservation = reservation;
        this.reservationId.set(reservation.id());
        this.created.set(reservation.createdTimestamp().toString());
    }

    /**
     * Gets the reservation
     *
     * @return Reservation DTO
     */
    public Reservation getReservation() {
        return this.reservation;
    }

    /**
     * Gets the reservation ID
     *
     * @return Reservation ID
     */
    public Integer reservationId() {
        return reservationId.get();
    }

    /**
     * Gets the reservation ID property
     *
     * @return ID property
     */
    public IntegerProperty reservationIdProperty() {
        return this.reservationId;
    }

    /**
     * Gets the creation Date
     *
     * @return Date of reservation creation
     */
    public String created() {
        return created.get();
    }

    /**
     * Gets the creation Date property
     *
     * @return Creation Date property
     */
    public StringProperty createdProperty() {
        return this.created;
    }
}
