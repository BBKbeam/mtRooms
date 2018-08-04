package bbk_beam.mtRooms.ui.model.logistics;

import bbk_beam.mtRooms.operation.dto.LogisticsEntry;
import bbk_beam.mtRooms.ui.model.SessionManager;
import bbk_beam.mtRooms.ui.model.common.GenericModelTable;

import java.util.Collection;

public class LogisticsTable extends GenericModelTable<LogisticsEntry, LogisticsModel> {
    /**
     * Constructor
     *
     * @param sessionManager SessionManager instance
     */
    public LogisticsTable(SessionManager sessionManager) {
        super(sessionManager);
    }

    @Override
    public void loadData(Collection<LogisticsEntry> collection) {
        if (!this.observableList.isEmpty())
            this.observableList.clear();
        for (LogisticsEntry entry : collection) {
            this.observableList.add(new LogisticsModel(entry));
        }
    }

    @Override
    public void appendData(Collection<LogisticsEntry> collection) {
        for (LogisticsEntry entry : collection) {
            this.observableList.add(new LogisticsModel(entry));
        }
    }
}
