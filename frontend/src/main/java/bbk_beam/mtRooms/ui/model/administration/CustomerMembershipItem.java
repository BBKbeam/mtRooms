package bbk_beam.mtRooms.ui.model.administration;

import bbk_beam.mtRooms.admin.dto.Usage;
import bbk_beam.mtRooms.reservation.dto.Membership;

import java.util.Collection;

public class CustomerMembershipItem {
    private Usage<Membership, Integer> membershipUsage;

    public CustomerMembershipItem(Usage<Membership, Integer> membershipUsage) {
        this.membershipUsage = membershipUsage;
    }

    /**
     * Gets membership usage state
     *
     * @return Membership assigned by customer(s) state
     */
    public boolean isUsed() {
        return !this.membershipUsage.usage().isEmpty();
    }

    /**
     * Gets the Membership DTO
     *
     * @return Membership DTO
     */
    public Membership getMembership() {
        return this.membershipUsage.dto();
    }

    /**
     * Gets the membership usage
     *
     * @return Customer IDs using the membership
     */
    public Collection<Integer> getUsageByCustomerID() {
        return this.membershipUsage.usage();
    }

    @Override
    public String toString() {
        return "[" + membershipUsage.dto().id() + "] " + membershipUsage.dto().description();
    }
}
