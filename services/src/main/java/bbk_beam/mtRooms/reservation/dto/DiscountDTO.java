package bbk_beam.mtRooms.reservation.dto;

public class DiscountDTO {
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
    public DiscountDTO(Integer id, Double rate, Integer category_id, String category_description) {
        this.id = id;
        this.rate = rate;
        this.category_id = category_id;
        this.category_description = category_description;
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
}
