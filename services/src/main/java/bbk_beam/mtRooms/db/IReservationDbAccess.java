package bbk_beam.mtRooms.db;

public interface IReservationDbAccess {
    //DB transactions should atomic and queued FIFO (serialised)
    //transaction provenance and timestamp should be here
    //Any view on the timetable should be told if changes occur from another source and updated with the latest
    //-> potential slot locking when adding to reservation cart?
    //-> reservation cart's timetable slots should probably be packaged into a container object
    //Any view on customer data should hold a lock on that data so that others may not modify it elsewhere (admin) during the locking period.
    //->lock timeout? : crash/errors/etc..
    //IReservationDBAccess and IUserAccDBAccess should be able to deal with threaded queries (see timestamp as possible approach)

}
