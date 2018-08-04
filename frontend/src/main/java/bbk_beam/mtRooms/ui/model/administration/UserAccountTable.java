package bbk_beam.mtRooms.ui.model.administration;

import bbk_beam.mtRooms.admin.dto.Account;
import bbk_beam.mtRooms.ui.model.SessionManager;
import bbk_beam.mtRooms.ui.model.common.GenericModelTable;

import java.util.Collection;

public class UserAccountTable extends GenericModelTable<Account, UserAccountItem> {

    /**
     * Constructor
     *
     * @param sessionManager SessionManager instance
     */
    public UserAccountTable(SessionManager sessionManager) {
        super(sessionManager);
    }

    @Override
    public void loadData(Collection<Account> accounts) {
        if (!this.observableList.isEmpty())
            this.observableList.clear();
        for (Account account : accounts) {
            this.observableList.add(new UserAccountItem(account));
        }
    }

    @Override
    public void appendData(Collection<Account> accounts) {
        for (Account account : accounts) {
            this.observableList.add(new UserAccountItem(account));
        }
    }
}
