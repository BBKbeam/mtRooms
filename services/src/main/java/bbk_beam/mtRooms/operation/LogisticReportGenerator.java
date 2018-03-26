package bbk_beam.mtRooms.operation;

import java.util.Collection;
import java.util.Date;

public class LogisticReportGenerator implements ILogisticReportGenerator {
    private LogisticAggregator aggregator;

    /**
     * Constructor
     *
     * @param logisticAggregator LogisticAggregator instance
     */
    public LogisticReportGenerator(LogisticAggregator logisticAggregator) {
        this.aggregator = logisticAggregator;
    }

    @Override
    public Collection getInfo(Date date) {
        return null;
    }

    @Override
    public Collection getInfo(Date fromDate, Date toDate) {
        return null;
    }
}
