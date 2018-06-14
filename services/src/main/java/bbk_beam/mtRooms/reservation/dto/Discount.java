package bbk_beam.mtRooms.reservation.dto;

import java.io.Serializable;
import java.util.Objects;

public class Discount implements Serializable {
    private Integer id;
    private Double rate;
    private DiscountCategory category;

    /**
     * Constructor
     *
     * @param id       Discount ID
     * @param rate     Discount rate percentage
     * @param category DiscountCategory DTO
     */
    public Discount(Integer id, Double rate, DiscountCategory category) {
        this.id = id;
        this.rate = rate;
        this.category = category;
    }

    /**
     * Copy Constructor
     *
     * @param discount Discount object to copy
     */
    public Discount(Discount discount) {
        this.id = discount.id;
        this.rate = discount.rate;
        this.category = discount.category;
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
     * Gets the discount's category
     *
     * @return DiscountCategory DTO
     */
    public DiscountCategory category() {
        return this.category;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Discount discount = (Discount) o;
        return Objects.equals(id, discount.id) &&
                Objects.equals(rate, discount.rate) &&
                Objects.equals(category, discount.category);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, rate, category);
    }

    @Override
    public String toString() {
        return "Discount[" + id + "]={ "
                + "rate: " + rate
                + ", " + category
                + " }";
    }
}
