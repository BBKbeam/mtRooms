package bbk_beam.mtRooms.revenue.dto;

import bbk_beam.mtRooms.common.TimeSpan;
import bbk_beam.mtRooms.common.TimeSpanAllocation;
import bbk_beam.mtRooms.common.TimeSpanInterval;
import bbk_beam.mtRooms.reservation.dto.Room;
import eadjlib.datastructure.AVLTree;
import eadjlib.exception.UndefinedException;
import eadjlib.logger.Logger;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Occupancy extends TimeSpanAllocation<Room, ReservationSlot> implements Serializable {
    transient private final Logger log = Logger.getLoggerInstance(Occupancy.class.getName());

    /**
     * Constructor
     */
    public Occupancy() {
        super();
    }

    /**
     * Constructor
     *
     * @param interval Slot interval granularity
     */
    public Occupancy(TimeSpanInterval interval) {
        super(interval);
    }


    /**
     * Adds the occupancy of room with a time span
     *
     * @param room Room DTO
     * @param span Span of time the room is booked for
     */
    public void add(Room room, TimeSpan span) {
        try {
            for (TimeSpan time_span : convertToUTCSlotIntervals(span)) {
                boolean room_exists = this.allocations.containsKey(room);
                if (!room_exists)
                    this.allocations.putIfAbsent(room, new AVLTree<>());
                if (this.allocations.get(room).search(time_span.start())) {
                    log.log_Debug("Incrementing reservation count for ", room, ": ", time_span);
                    ReservationSlot new_slot = this.allocations.get(room).apply(time_span.start(), (ReservationSlot s) -> {
                        s.incrementReservationCount();
                        return s;
                    });
                } else {
                    this.allocations.get(room).add(
                            time_span.start(),
                            new ReservationSlot(time_span.start(), time_span.end(), 1)
                    );
                }
            }
        } catch (UndefinedException e) {
            log.log_Fatal("Error detected in the occupancy data-structure in time-slot allocation of Room: ", room);
            log.log_Exception(e);
        }
    }

    /**
     * Gets the occupancy
     *
     * @return List of all room occupancies
     */
    public List<RoomOccupancy> getOccupancies() {
        List<RoomOccupancy> occupancies = new ArrayList<>();
        List<Room> keySet = new ArrayList<>(this.allocations.keySet());
        java.util.Collections.sort(keySet);
        for (Room room : keySet) {
            RoomOccupancy occupancy = new RoomOccupancy(room);
            AVLTree.AVLTreeIterator it = (AVLTree.AVLTreeIterator) this.allocations.get(room).iterator();
            while (it.hasNext()) {
                occupancy.add((ReservationSlot) it.next().value());
            }
            occupancies.add(occupancy);
        }
        return occupancies;
    }
}
