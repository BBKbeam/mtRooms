package bbk_beam.mtRooms.ui.model.administration;

import bbk_beam.mtRooms.admin.dto.Usage;
import bbk_beam.mtRooms.reservation.dto.Membership;
import bbk_beam.mtRooms.ui.model.SessionManager;
import bbk_beam.mtRooms.ui.model.common.GenericModelTable;

import java.util.Collection;

public class MembershipList extends GenericModelTable<Usage<Membership, Integer>, CustomerMembershipItem> {
    /**
     * Constructor
     *
     * @param sessionManager SessionManager instance
     */
    public MembershipList(SessionManager sessionManager) {
        super(sessionManager);
    }

    @Override
    public void loadData(Collection<Usage<Membership, Integer>> collection) {
        if (!this.observableList.isEmpty())
            this.observableList.clear();
        for (Usage<Membership, Integer> usage : collection) {
            this.observableList.add(new CustomerMembershipItem(usage));
        }
    }

    @Override
    public void appendData(Collection<Usage<Membership, Integer>> collection) {
        for (Usage<Membership, Integer> usage : collection) {
            this.observableList.add(new CustomerMembershipItem(usage));
        }
    }
}
