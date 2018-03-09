package bbk_beam.mtRooms.reservation.processing;

import bbk_beam.mtRooms.reservation.delegate.ISearch;
import eadjlib.logger.Logger;

public class OptimisedSearch {
    private final Logger log = Logger.getLoggerInstance(OptimisedSearch.class.getName());
    private ISearch db_delegate;

    /**
     * Constructor
     *
     * @param search_delegate ISearch instance
     */
    public OptimisedSearch(ISearch search_delegate) {
        this.db_delegate = search_delegate;
    }

    //TODO
}
