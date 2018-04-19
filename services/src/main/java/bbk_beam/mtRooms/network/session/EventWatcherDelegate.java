package bbk_beam.mtRooms.network.session;

import bbk_beam.mtRooms.reservation.IEventWatcher;
import bbk_beam.mtRooms.reservation.ReservationSession;
import bbk_beam.mtRooms.reservation.dto.RoomReservation;
import eadjlib.logger.Logger;

import java.rmi.RemoteException;

/**
 * EventWatcherDelegate
 * <p>
 * Linker for RMI clients and their respective :ReservationSession observers used
 * to chain-link update() calls through ReservationSession -> EventWatcherDelegate -> ClientWrapper
 * </p>
 */
public class EventWatcherDelegate implements IEventWatcher {
    private final Logger log = Logger.getLoggerInstance(EventWatcherDelegate.class.getName());
    private ClientSessions connected_clients;

    /**
     * Constructor
     *
     * @param sessions ClientSessions instance
     */
    public EventWatcherDelegate(ClientSessions sessions) {
        this.connected_clients = sessions;
    }

    @Override
    public void update(ReservationSession session, RoomReservation reservation) {
        try {
            this.connected_clients.notifyClient(session.getToken(), reservation);
        } catch (RemoteException e) {
            log.log_Error("Could not notify client [", session.getToken(), "] about ", reservation);
        }
    }
}
