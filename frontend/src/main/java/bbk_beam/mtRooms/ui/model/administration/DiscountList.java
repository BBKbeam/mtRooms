package bbk_beam.mtRooms.ui.model.administration;

import bbk_beam.mtRooms.reservation.dto.Discount;
import bbk_beam.mtRooms.ui.model.SessionManager;
import bbk_beam.mtRooms.ui.model.common.GenericModelTable;

import java.util.Collection;

public class DiscountList extends GenericModelTable<Discount, DiscountItem> {
    /**
     * Constructor
     *
     * @param sessionManager SessionManager instance
     */
    public DiscountList(SessionManager sessionManager) {
        super(sessionManager);
    }

    @Override
    public void loadData(Collection<Discount> collection) {
        if (!this.observableList.isEmpty())
            this.observableList.clear();
        for (Discount discount : collection) {
            this.observableList.add(new DiscountItem(discount));
        }
    }

    @Override
    public void appendData(Collection<Discount> collection) {
        for (Discount discount : collection) {
            this.observableList.add(new DiscountItem(discount));
        }
    }

    /**
     * Add a Discount DTO to the list
     *
     * @param discount Discount DTO
     */
    public void addData(Discount discount) {
        this.observableList.add(new DiscountItem(discount));
    }
}
