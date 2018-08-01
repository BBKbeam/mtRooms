package bbk_beam.mtRooms.ui.model.frontdesk;

import bbk_beam.mtRooms.reservation.dto.Reservation;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.text.SimpleDateFormat;
import java.util.Objects;

public class ReservationModel {
    Reservation reservation;
    private IntegerProperty reservationId = new SimpleIntegerProperty();
    private StringProperty created = new SimpleStringProperty("");
    private StringProperty discount_rate = new SimpleStringProperty();

    /**
     * Constructor
     *
     * @param reservation Reservation DTO
     */
    public ReservationModel(Reservation reservation) {
        this.reservation = reservation;
        this.reservationId.set(reservation.id());
        SimpleDateFormat df = new SimpleDateFormat("YYYY/MM/dd - HH:mm:ss");
        this.created.set(df.format(reservation.createdTimestamp()));
        this.discount_rate.set(reservation.discount().rate() + "%");
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

    /**
     * Gets the discount rate applied to the reservation
     *
     * @return Discount rate string
     */
    public String discountRate() {
        return this.discount_rate.get();
    }

    /**
     * Gets the discount rate property of the discount applied to the reservation
     *
     * @return Discount rate property
     */
    public StringProperty discountRateProperty() {
        return this.discount_rate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReservationModel that = (ReservationModel) o;
        return Objects.equals(reservationId, that.reservationId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(reservationId);
    }
}
