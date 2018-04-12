package bbk_beam.mtRooms.reservation.dto;

import java.io.Serializable;

public class Membership implements Serializable {
    private Integer id;
    private String description;
    private Discount discount;

    /**
     * Constructor
     *
     * @param id          ID
     * @param description Description
     * @param discount    Discount associated with membership
     */
    public Membership(Integer id, String description, Discount discount) {
        this.id = id;
        this.description = description;
        this.discount = discount;
    }

    /**
     * Gets membership's ID
     *
     * @return ID
     */
    public Integer id() {
        return this.id;
    }

    /**
     * Gets membership's description
     *
     * @return Description
     */
    public String description() {
        return this.description;
    }

    /**
     * Gets membership's discount
     *
     * @return Discount
     */
    public Discount discount() {
        return discount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Membership that = (Membership) o;

        if (!id.equals(that.id)) return false;
        if (!description.equals(that.description)) return false;
        return discount.equals(that.discount);
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + description.hashCode();
        result = 31 * result + discount.hashCode();
        return result;
    }
}
