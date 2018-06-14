package bbk_beam.mtRooms.reservation.dto;

import java.io.Serializable;
import java.util.Objects;

public class DiscountCategory implements Serializable {
    private Integer id;
    private String description;

    /**
     * Constructor
     *
     * @param id          ID
     * @param description Description
     */
    public DiscountCategory(Integer id, String description) {
        this.id = id;
        this.description = description;
    }

    /**
     * Gets the ID
     *
     * @return ID
     */
    public Integer id() {
        return this.id;
    }

    /**
     * Gets the description
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
        DiscountCategory that = (DiscountCategory) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, description);
    }

    @Override
    public String toString() {
        return "DiscountCategory[" + id + "]={ \'" + description + "\' }";
    }
}
