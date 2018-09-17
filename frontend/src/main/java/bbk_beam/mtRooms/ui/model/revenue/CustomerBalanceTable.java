package bbk_beam.mtRooms.ui.model.revenue;

import bbk_beam.mtRooms.revenue.dto.SimpleCustomerBalance;
import bbk_beam.mtRooms.ui.model.SessionManager;
import bbk_beam.mtRooms.ui.model.common.GenericModelTable;

import java.util.Collection;

public class CustomerBalanceTable extends GenericModelTable<SimpleCustomerBalance, SimpleCustomerBalanceModel> {
    /**
     * Constructor
     *
     * @param sessionManager SessionManager instance
     */
    public CustomerBalanceTable(SessionManager sessionManager) {
        super(sessionManager);
    }

    @Override
    public void loadData(Collection<SimpleCustomerBalance> collection) {
        if (!this.observableList.isEmpty())
            this.observableList.clear();
        for (SimpleCustomerBalance entry : collection) {
            this.observableList.add(new SimpleCustomerBalanceModel(entry));
        }
    }

    @Override
    public void appendData(Collection<SimpleCustomerBalance> collection) {
        for (SimpleCustomerBalance entry : collection) {
            this.observableList.add(new SimpleCustomerBalanceModel(entry));
        }
    }
}
