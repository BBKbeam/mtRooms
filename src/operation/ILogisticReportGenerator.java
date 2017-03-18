package bbk_beam.mtRooms.operation;

public interface ILogisticReportGenerator {
    public Collection getInfo( Date date );
    public Collection getInfo( Date fromDate, Date toDate );
}
