package bbk_beam.mtRooms.ui.model.administration;

import bbk_beam.mtRooms.reservation.dto.Discount;

public class DiscountItem {
    private Discount discount;

    /**
     * Constructor
     *
     * @param discount Discount DTO
     */
    public DiscountItem(Discount discount) {
        this.discount = discount;
    }

    /**
     * Gets the Discount DTO
     *
     * @return Wrapped Discount DTO
     */
    public Discount getDiscount() {
        return this.discount;
    }

    @Override
    public String toString() {
        return discount.category().description() + " (" + discount.rate() + "%)";
    }
}
