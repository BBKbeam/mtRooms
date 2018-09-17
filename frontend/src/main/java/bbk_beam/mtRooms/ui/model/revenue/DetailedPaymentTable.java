package bbk_beam.mtRooms.ui.model.revenue;

import bbk_beam.mtRooms.revenue.dto.DetailedPayment;
import bbk_beam.mtRooms.ui.model.SessionManager;
import bbk_beam.mtRooms.ui.model.common.GenericModelTable;

import java.util.Collection;

public class DetailedPaymentTable extends GenericModelTable<DetailedPayment, DetailedPaymentModel> {
    private Double running_total;

    /**
     * Constructor
     *
     * @param sessionManager SessionManager instance
     */
    public DetailedPaymentTable(SessionManager sessionManager) {
        super(sessionManager);
        this.running_total = 0d;
    }

    @Override
    public void loadData(Collection<DetailedPayment> collection) {
        if (!this.observableList.isEmpty())
            this.observableList.clear();
        for (DetailedPayment entry : collection) {
            this.observableList.add(new DetailedPaymentModel(entry));
            this.running_total += entry.amount();
        }
    }

    @Override
    public void appendData(Collection<DetailedPayment> collection) {
        for (DetailedPayment entry : collection) {
            this.observableList.add(new DetailedPaymentModel(entry));
            this.running_total += entry.amount();
        }
    }

    /**
     * Gets the running total of the payments
     *
     * @return Running total
     */
    public Double getPaymentTotal() {
        return this.running_total;
    }
}
