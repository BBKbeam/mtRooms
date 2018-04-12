package bbk_beam.mtRooms.reservation.dto;

import java.io.Serializable;

public class PaymentMethod implements Serializable {
    private Integer id;
    private String description;

    /**
     * Constructor
     *
     * @param id          ID
     * @param description Description
     */
    public PaymentMethod(Integer id, String description) {
        this.id = id;
        this.description = description;
    }

    /**
     * Gets the payment method ID
     *
     * @return ID
     */
    public Integer id() {
        return this.id;
    }

    /**
     * Gets the payment method description
     *
     * @return Description
     */
    public String description() {
        return this.description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PaymentMethod that = (PaymentMethod) o;

        if (!id.equals(that.id)) return false;
        return description.equals(that.description);
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + description.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "[" + id + "]={ " + description + " }";
    }
}
