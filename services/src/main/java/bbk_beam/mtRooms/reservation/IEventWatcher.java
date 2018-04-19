package bbk_beam.mtRooms.reservation;

import bbk_beam.mtRooms.reservation.dto.RoomReservation;

public interface IEventWatcher {
    void update(ReservationSession session, RoomReservation reservation);
}
