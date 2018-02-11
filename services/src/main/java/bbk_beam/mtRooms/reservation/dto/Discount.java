package bbk_beam.mtRooms.reservation.dto;

import java.util.Objects;

public class Discount {
    private Integer id;
    private Double rate;
    private Integer category_id;
    private String category_description;

    /**
     * Constructor
     *
     * @param id                   Discount ID
     * @param rate                 Discount rate percentage
     * @param category_id          Discount category ID
     * @param category_description Discount category description
     */
    public Discount(Integer id, Double rate, Integer category_id, String category_description) {
        this.id = id;
        this.rate = rate;
        this.category_id = category_id;
        this.category_description = category_description;
    }

    /**
     * Copy Constructor
     *
     * @param discount Discount object to copy
     */
    public Discount(Discount discount) {
        this.id = discount.id;
        this.rate = discount.rate;
        this.category_id = discount.category_id;
        this.category_description = discount.category_description;
    }

    /**
     * Gets the discount ID
     *
     * @return Discount ID
     */
    public Integer id() {
        return id;
    }

    /**
     * Gets the discount rate percentage
     *
     * @return Percentage discount rate
     */
    public Double rate() {
        return rate;
    }

    /**
     * Gets the discount's category ID
     *
     * @return Discount category ID
     */
    public Integer categoryID() {
        return category_id;
    }

    /**
     * Gets the discount's category description
     *
     * @return Discount category description
     */
    public String categoryDescription() {
        return category_description;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Discount discount = (Discount) o;
        return Objects.equals(id, discount.id) &&
                Objects.equals(rate, discount.rate) &&
                Objects.equals(category_id, discount.category_id) &&
                Objects.equals(category_description, discount.category_description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, rate, category_id, category_description);
    }

    @Override
    public String toString() {
        return "[" + id + "]={ "
                + "rate: " + rate
                + ", category_id: " + category_id
                + ", category_description: " + category_description
                + " }";
    }
}
