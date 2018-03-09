package bbk_beam.mtRooms.reservation.scheduling;

import java.util.Observable;
import java.util.Observer;

//Any view on the timetable should be told if changes occur from another source and updated with the latest
//-> potential slot locking when adding to reservation cart?
//-> reservation cart's timetable slots should probably be packaged into a container object

public class ScheduleCache extends Observable {

    //TODO


    @Override
    public synchronized void addObserver(Observer o) {
        super.addObserver(o);
    }

    @Override
    public synchronized void deleteObserver(Observer o) {
        super.deleteObserver(o);
    }

    @Override
    public void notifyObservers() {
        super.notifyObservers();
    }

    @Override
    public void notifyObservers(Object arg) {
        super.notifyObservers(arg);
    }

    @Override
    public synchronized void deleteObservers() {
        super.deleteObservers();
    }

    @Override
    protected synchronized void setChanged() {
        super.setChanged();
    }

    @Override
    protected synchronized void clearChanged() {
        super.clearChanged();
    }

    @Override
    public synchronized boolean hasChanged() {
        return super.hasChanged();
    }

    @Override
    public synchronized int countObservers() {
        return super.countObservers();
    }
}
