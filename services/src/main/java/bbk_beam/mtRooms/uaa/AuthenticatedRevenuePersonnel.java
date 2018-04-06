package bbk_beam.mtRooms.uaa;

public class AuthenticatedRevenuePersonnel implements IAuthenticatedRevenuePersonnel {
    private RevenuePersonelDelegate delegate;

    /**
     * Constructor
     *
     * @param delegate RevenuePersonelDelegate instance
     */
    public AuthenticatedRevenuePersonnel(RevenuePersonelDelegate delegate) {
        this.delegate = delegate;
    }

    //TODO implement interface methods once that's defined
}
