package bbk_beam.mtRooms.operation;

import java.util.Collection;
import java.util.Date;

public interface ILogisticReportGenerator {
    /**
     * Gets logistical information for a day
     *
     * @param date Date
     * @return Logistics information
     */
    public Collection getInfo(Date date);

    /**
     * Gets logistical information
     *
     * @param fromDate From date
     * @param toDate   To date
     * @return Logistics information
     */
    public Collection getInfo(Date fromDate, Date toDate);
}
