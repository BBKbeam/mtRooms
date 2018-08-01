package bbk_beam.mtRooms.reservation.processing;

import bbk_beam.mtRooms.admin.authentication.Token;
import bbk_beam.mtRooms.db.TimestampConverter;
import bbk_beam.mtRooms.db.exception.DbQueryException;
import bbk_beam.mtRooms.db.exception.SessionExpiredException;
import bbk_beam.mtRooms.db.exception.SessionInvalidException;
import bbk_beam.mtRooms.reservation.delegate.IReserve;
import bbk_beam.mtRooms.reservation.dto.*;
import bbk_beam.mtRooms.reservation.exception.*;
import bbk_beam.mtRooms.reservation.scheduling.ScheduleCache;
import eadjlib.datastructure.ObjectTable;
import eadjlib.logger.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ReservationProcessing {
    private final Logger log = Logger.getLoggerInstance(ReservationProcessing.class.getName());
    private IReserve db_delegate;
    private ScheduleCache schedule_cache;

    /**
     * Constructor
     *
     * @param reserve_delegate IReserve instance
     * @param schedule_cache   ScheduleCache instance
     */
    public ReservationProcessing(IReserve reserve_delegate, ScheduleCache schedule_cache) {
        this.db_delegate = reserve_delegate;
        this.schedule_cache = schedule_cache;
    }

    /**
     * Creates a reservation in the records
     *
     * @param session_token Session's token
     * @param reservation   Reservation DTO
     * @return Reservation object with the ID of the record created
     * @throws FailedDbWrite           when a problem was encountered whilst processing the query
     * @throws FailedDbFetch           when a problem was encountered whilst processing the query
     * @throws SessionExpiredException When the session for the id provided has expired
     * @throws SessionInvalidException When the session for the id provided does not exist in the tracker
     */
    public Reservation createReservation(Token session_token, Reservation reservation) throws FailedDbWrite, FailedDbFetch, SessionExpiredException, SessionInvalidException {
        try {
            Integer reservation_id = this.db_delegate.createReservation(session_token, reservation);
            log.log_Debug("Created reservation in records. Given Reservation.id: ", reservation_id);
            for (RoomReservation reserved_room : reservation.rooms()) {
                this.schedule_cache.add(session_token, reserved_room); //adds to the cache if not already there
                this.db_delegate.createRoomReservation(session_token, reservation_id, reserved_room);
            }
            return getReservation(session_token, reservation_id);
        } catch (DbQueryException e) {
            log.log_Error("Failed to save reservation in records: ", reservation);
            throw new FailedDbWrite("Failed to save reservation in records: " + reservation, e);
        } catch (InvalidReservation e) {
            log.log_Error("Could not fetch back created Reservation from records: ", reservation);
            throw new FailedDbFetch("Could not fetch back created Reservation from records: " + reservation, e);
        }
    }

    /**
     * Creates a RoomReservation in the records
     *
     * @param session_token Session's token
     * @param reservation   Reservation DTO
     * @param reserved_room RoomReservation DTO
     * @throws InvalidReservation      when Reservation cannot be validated with the records
     * @throws FailedDbWrite           when a problem was encountered whilst processing the query
     * @throws FailedDbFetch           when a problem was encountered whilst processing the query
     * @throws SessionExpiredException When the session for the id provided has expired
     * @throws SessionInvalidException When the session for the id provided does not exist in the tracker
     */
    public void createRoomReservation(Token session_token, Reservation reservation, RoomReservation reserved_room) throws InvalidReservation, FailedDbWrite, FailedDbFetch, SessionExpiredException, SessionInvalidException {
        try {
            Reservation temp_reservation = getReservation(session_token, reservation.id());
            this.db_delegate.createRoomReservation(session_token, temp_reservation.id(), reserved_room);
            this.schedule_cache.broadcastRoomReservation(session_token, reserved_room);
        } catch (InvalidReservation e) {
            log.log_Error("Trying to create a RoomReservation in the records before Reservation.");
            throw new InvalidReservation("Trying to create a RoomReservation in the records before Reservation.", e);
        } catch (DbQueryException e) {
            log.log_Error("Could not create record of RoomReservation: ", reserved_room);
            throw new FailedDbWrite("Could not create record of RoomReservation: " + reserved_room, e);
        }
    }

    /**
     * Mark the record of a reservation as cancelled
     *
     * @param session_token Session's token
     * @param reservation   Reservation DTO
     * @return Balance to reimburse on reservation
     * @throws InvalidReservation      when Reservation cannot be validated with the records
     * @throws FailedDbWrite           when a problem was encountered whilst processing the query
     * @throws SessionExpiredException when the session for the id provided has expired
     * @throws SessionInvalidException when the session for the id provided does not exist in the tracker
     */
    public Double cancelReservation(Token session_token, Reservation reservation) throws InvalidReservation, FailedDbWrite, SessionExpiredException, SessionInvalidException {
        try {
            return this.db_delegate.cancelReservation(session_token, reservation);
        } catch (DbQueryException e) {
            log.log_Error("Could not cancel reservation: ", reservation);
            throw new FailedDbWrite("Could not cancel reservation: " + reservation, e);
        }
    }

    /**
     * Mark the record of a room inside a reservation as cancelled
     *
     * @param session_token    Session's token
     * @param reservation      Reservation DTO
     * @param room_reservation RoomReservation DTO
     * @return Pre-discount balance to reimburse on reservation
     * @throws InvalidReservation      when Reservation cannot be validated with the records
     * @throws FailedDbWrite           when a problem was encountered whilst processing the query
     * @throws SessionExpiredException when the session for the id provided has expired
     * @throws SessionInvalidException when the session for the id provided does not exist in the tracker
     */
    public Double cancelReservedRoom(Token session_token, Reservation reservation, RoomReservation room_reservation) throws InvalidReservation, FailedDbWrite, SessionExpiredException, SessionInvalidException {
        try {
            return this.db_delegate.cancelReservedRoom(session_token, reservation.id(), room_reservation);
        } catch (DbQueryException e) {
            log.log_Error("Could not cancel RoomReservation: ", room_reservation);
            throw new FailedDbWrite("Could not cancel RoomReservation: " + room_reservation);
        }
    }

    /**
     * Updates the note on a RoomReservation
     *
     * @param session_token    Session's token
     * @param reservation      Reservation DTO
     * @param room_reservation RoomReservation DTO
     * @throws InvalidReservation      when Reservation cannot be validated with the records
     * @throws FailedDbWrite           when a problem was encountered whilst processing the query
     * @throws SessionExpiredException when the session for the id provided has expired
     * @throws SessionInvalidException when the session for the id provided does not exist in the tracker
     */
    public void updateReservedRoomNote(Token session_token, Reservation reservation, RoomReservation room_reservation) throws InvalidReservation, FailedDbWrite, SessionExpiredException, SessionInvalidException {
        try {
            this.db_delegate.updateReservedRoomNote(session_token, reservation.id(), room_reservation);
        } catch (DbQueryException e) {
            log.log_Error("Could not update RoomReservation's note: ", room_reservation);
            throw new FailedDbWrite("Could not update RoomReservation's note: " + room_reservation);
        }
    }

    /**
     * Gets a reservation's details
     *
     * @param session_token  Session's token
     * @param reservation_id Reservation ID
     * @return Reservation DTO
     * @throws InvalidReservation      when the reservation ID does not match any within the records
     * @throws FailedDbFetch           when a problem was encountered whilst processing the query
     * @throws SessionExpiredException when the session for the id provided has expired
     * @throws SessionInvalidException when the session for the id provided does not exist in the tracker
     */
    public Reservation getReservation(Token session_token, Integer reservation_id) throws InvalidReservation, FailedDbFetch, SessionExpiredException, SessionInvalidException {
        try {
            ObjectTable reservation_table = this.db_delegate.getReservation(session_token, reservation_id);
            HashMap<String, Object> reservation_row = reservation_table.getRow(1);

            Discount discount = new Discount(
                    (Integer) reservation_row.get("discount_id"),
                    (Double) reservation_row.get("discount_rate"),
                    new DiscountCategory(
                            (Integer) reservation_row.get("discount_category_id"),
                            (String) reservation_row.get("discount_category_description")
                    )
            );
            Reservation reservation = new Reservation(
                    (Integer) reservation_row.get("id"),
                    TimestampConverter.getDateObject((String) reservation_row.get("created_timestamp")),
                    (Integer) reservation_row.get("customer_id"),
                    discount
            );

            ObjectTable rooms_table = this.db_delegate.getReservedRooms(session_token, reservation);
            for (int i = 1; i <= rooms_table.rowCount(); i++) {
                HashMap<String, Object> reserved_room_row = rooms_table.getRow(i);
                reservation.addRoomReservation(new RoomReservation(
                        new Room(
                                (Integer) reserved_room_row.get("room_id"),
                                (Integer) reserved_room_row.get("floor_id"),
                                (Integer) reserved_room_row.get("building_id"),
                                (Integer) reserved_room_row.get("room_category_id"),
                                (String) reserved_room_row.get("room_description")
                        ),
                        TimestampConverter.getDateObject((String) reserved_room_row.get("timestamp_in")),
                        TimestampConverter.getDateObject((String) reserved_room_row.get("timestamp_out")),
                        (Integer) reserved_room_row.get("seated_count"),
                        ((Integer) reserved_room_row.get("catering") != 0),
                        (String) reserved_room_row.get("notes"),
                        new RoomPrice(
                                (Integer) reserved_room_row.get("price_id"),
                                (Double) reserved_room_row.get("price"),
                                (Integer) reserved_room_row.get("price_year")
                        ),
                        ((Integer) reserved_room_row.get("cancelled_flag") != 0)
                ));
            }
            return reservation;
        } catch (DbQueryException e) {
            log.log_Error("Fetching of Reservation [", reservation_id, "]'s details unsuccessful: SQL Query issue.", e);
            throw new FailedDbFetch("Fetching of Reservation [" + reservation_id + "]'s details unsuccessful: SQL Query issue.", e);
        }
    }

    /**
     * Gets the reservations associated with a customer
     *
     * @param session_token Session's token
     * @param customer      Customer
     * @return List of customer's reservation
     * @throws FailedDbFetch           when a problem was encountered whilst processing the query
     * @throws SessionExpiredException when the session for the id provided has expired
     * @throws SessionInvalidException when the session for the id provided does not exist in the tracker
     */
    public List<Reservation> getReservations(Token session_token, Customer customer) throws FailedDbFetch, SessionExpiredException, SessionInvalidException {
        try {
            ObjectTable table = this.db_delegate.getReservations(session_token, customer);
            List<Reservation> reservations = new ArrayList<>();
            for (int i = 1; i <= table.rowCount(); i++) {
                Integer reservation_id = table.getInteger(1, i);
                reservations.add(getReservation(session_token, reservation_id));
            }
            return reservations;
        } catch (DbQueryException e) {
            log.log_Error("Fetching of reservations for customer \"", customer.name(), " ", customer.surname(), "\" [" + customer.customerID() + "]'s details unsuccessful: SQL Query issue.");
            throw new FailedDbFetch("Fetching of reservations for customer \"" + customer.name() + " " + customer.surname() + "\" [" + customer.customerID() + "]'s details unsuccessful: SQL Query issue.", e);
        } catch (InvalidReservation e) {
            log.log_Error("Fetching of reservations for customer \"", customer.name(), " ", customer.surname(), "\" [" + customer.customerID() + "]'s details unsuccessful: Invalid reservation ID.");
            throw new FailedDbFetch("Fetching of reservations for customer \"" + customer.name() + " " + customer.surname() + "\" [" + customer.customerID() + "]'s details unsuccessful: Invalid reservation ID.", e);
        }
    }

    /**
     * Gets the RoomCategory DTO from an ID
     *
     * @param session_token Session's token
     * @param category_id   RoomCategory ID
     * @return RoomCategory DTO
     * @throws InvalidRoomCategory     when the ID does not match any RoomCategory in the records
     * @throws FailedDbFetch           when a problem was encountered whilst processing the query
     * @throws SessionExpiredException when the session for the id provided has expired
     * @throws SessionInvalidException when the session for the id provided does not exist in the tracker
     */
    public RoomCategory getRoomCategory(Token session_token, Integer category_id) throws InvalidRoomCategory, FailedDbFetch, SessionExpiredException, SessionInvalidException {
        try {
            ObjectTable table = this.db_delegate.getRoomCategory(session_token, category_id);
            if (table.isEmpty()) {
                log.log_Error("RoomCategory [", category_id, "] does not exists in records.");
                throw new InvalidRoomCategory("RoomCategory [" + category_id + "] does not exists in records.");
            }
            HashMap<String, Object> row = table.getRow(1);
            return new RoomCategory(
                    (Integer) row.get("id"),
                    (Integer) row.get("capacity"),
                    (Integer) row.get("dimension")
            );
        } catch (DbQueryException e) {
            log.log_Error("Could not get RoomCategory [", category_id, "]");
            throw new FailedDbFetch("Could not get RoomCategory [" + category_id + "]", e);
        }
    }

    /**
     * Gets details for a Room
     *
     * @param session_token Session's token
     * @param room          Room DTO
     * @return DetailedRoom DTO
     * @throws InvalidRoom             when the room does not match any in the records
     * @throws FailedDbFetch           when a problem was encountered whilst processing the query
     * @throws SessionExpiredException when the session for the id provided has expired
     * @throws SessionInvalidException when the session for the id provided does not exist in the tracker
     */
    public DetailedRoom getRoomDetails(Token session_token, Room room) throws InvalidRoom, FailedDbFetch, SessionExpiredException, SessionInvalidException {
        try {
            ObjectTable table = this.db_delegate.getRoomDetails(session_token, room);
            HashMap<String, Object> row = table.getRow(1);
            return new DetailedRoom(
                    new Building(
                            (Integer) row.get("building_id"),
                            (String) row.get("building_name"),
                            (String) row.get("address1"),
                            (String) row.get("address2"),
                            (String) row.get("city"),
                            (String) row.get("postcode"),
                            (String) row.get("country"),
                            (String) row.get("telephone")
                    ),
                    new Floor(
                            (Integer) row.get("building_id"),
                            (Integer) row.get("floor_id"),
                            (String) row.get("floor_description")
                    ),
                    new Room(
                            (Integer) row.get("room_id"),
                            (Integer) row.get("floor_id"),
                            (Integer) row.get("building_id"),
                            (Integer) row.get("category_id"),
                            (String) row.get("room_description")
                    ),
                    new RoomCategory(
                            (Integer) row.get("category_id"),
                            (Integer) row.get("capacity"),
                            (Integer) row.get("dimension")
                    ),
                    new RoomFixtures(
                            (Integer) row.get("room_fixture_id"),
                            ((Integer) row.get("fixed_chairs") != 0),
                            ((Integer) row.get("catering_space") != 0),
                            ((Integer) row.get("whiteboard") != 0),
                            ((Integer) row.get("projector") != 0)
                    )
            );
        } catch (DbQueryException e) {
            log.log_Error("");
            throw new FailedDbFetch("");
        }
    }

    /**
     * Gets all the prices on records for a room
     *
     * @param session_token Session's token
     * @param room          Room DTO
     * @return List of RoomPrice DTOs for the Room
     * @throws FailedDbFetch           when a problem was encountered whilst processing the query
     * @throws SessionExpiredException when the session for the id provided has expired
     * @throws SessionInvalidException when the session for the id provided does not exist in the tracker
     */
    public List<RoomPrice> getRoomPrices(Token session_token, Room room) throws FailedDbFetch, SessionExpiredException, SessionInvalidException {
        try {
            List<RoomPrice> list = new ArrayList<>();
            ObjectTable table = this.db_delegate.getRoomPrices(session_token, room);
            for (int i = 1; i <= table.rowCount(); i++) {
                HashMap<String, Object> row = table.getRow(i);
                list.add(new RoomPrice(
                        (Integer) row.get("id"),
                        (Double) row.get("price"),
                        (Integer) row.get("year")
                ));
            }
            return list;
        } catch (DbQueryException e) {
            log.log_Error("Could not get RoomPrice(s) for Room: ", room);
            throw new FailedDbFetch("Could not get RoomPrices for Room: " + room, e);
        }
    }
}
