package bbk_beam.mtRooms.reservation.dto;

public class PaymentType {
    private Integer id;
    private String description;

    /**
     * Constructor
     *
     * @param id          ID
     * @param description Description
     */
    public PaymentType(Integer id, String description) {
        this.id = id;
        this.description = description;
    }

    /**
     * Gets the payment type id
     *
     * @return ID
     */
    public Integer id() {
        return this.id;
    }

    /**
     * Gets the payment type description
     *
     * @return Description
     */
    public String description() {
        return this.description;
    }

    @Override
    public String toString() {
        return "[" + this.id + "]={" + this.description + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PaymentType that = (PaymentType) o;

        if (!id.equals(that.id)) return false;
        return description.equals(that.description);
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + description.hashCode();
        return result;
    }
}
