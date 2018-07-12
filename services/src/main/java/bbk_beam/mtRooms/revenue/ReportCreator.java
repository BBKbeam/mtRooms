package bbk_beam.mtRooms.revenue;

import bbk_beam.mtRooms.admin.authentication.Token;
import bbk_beam.mtRooms.common.TimeSpan;
import bbk_beam.mtRooms.db.IReservationDbAccess;
import bbk_beam.mtRooms.db.TimestampConverter;
import bbk_beam.mtRooms.db.exception.DbQueryException;
import bbk_beam.mtRooms.db.exception.SessionExpiredException;
import bbk_beam.mtRooms.db.exception.SessionInvalidException;
import bbk_beam.mtRooms.reservation.dto.*;
import bbk_beam.mtRooms.reservation.exception.FailedDbFetch;
import bbk_beam.mtRooms.revenue.dto.*;
import bbk_beam.mtRooms.revenue.exception.InvalidPeriodException;
import eadjlib.datastructure.ObjectTable;
import eadjlib.logger.Logger;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ReportCreator implements IRevenueReporter {
    private final Logger log = Logger.getLoggerInstance(ReportCreator.class.getName());
    private RevenueAggregator aggregator;

    /**
     * Gets the number of hours between 2 dates
     *
     * @param start Start timestamp
     * @param end   End timestamp
     * @return Number of hours
     */
    private Double hourCount(Date start, Date end) {
        return TimeUnit.MILLISECONDS.toMinutes(Math.abs(end.getTime() - start.getTime())) / 60.;
    }

    /**
     * Checks start and end dates are chronologically set
     *
     * @param from Start timestamp
     * @param to   End timestamp
     * @throws InvalidPeriodException when Start timestamp is after the End timestamp
     */
    private void checkDate(Date from, Date to) throws InvalidPeriodException {
        if (!to.after(from)) {
            log.log_Error("Negative period (from > to): ", from, " > ", to);
            throw new InvalidPeriodException("Start date is after end date for period.");
        }
    }

    /**
     * Gets the discount associated with a reservation
     *
     * @param session_token  Session token
     * @param reservation_id Reservation ID
     * @return Discount DTO
     * @throws FailedDbFetch           when error occurred during fetching of data from DB
     * @throws SessionExpiredException when the session for the id provided has expired
     * @throws SessionInvalidException when the session for the id provided does not exist in the tracker
     */
    private Discount getDiscount(Token session_token, Integer reservation_id) throws FailedDbFetch, SessionExpiredException, SessionInvalidException {
        try {
            ObjectTable discount_table = this.aggregator.getReservationDiscount(session_token, reservation_id);
            HashMap<String, Object> row = discount_table.getRow(1);
            return new Discount(
                    (Integer) row.get("discount_id"),
                    (Double) row.get("discount_rate"),
                    new DiscountCategory(
                            (Integer) row.get("discount_category_id"),
                            (String) row.get("category_description")
                    )
            );
        } catch (DbQueryException e) {
            log.log_Error("Could not fetch Discount from records for Reservation[", reservation_id, "].");
            throw new FailedDbFetch("Could not fetch Discount from records for Reservation[" + reservation_id + "].", e);
        }
    }

    /**
     * Gets the total cost of a reservation's booked rooms
     *
     * @param session_token  Session token
     * @param reservation_id Reservation ID
     * @return ReservationCost DTO
     * @throws FailedDbFetch           when error occurred during fetching of data from DB
     * @throws SessionExpiredException when the session for the id provided has expired
     * @throws SessionInvalidException when the session for the id provided does not exist in the tracker
     */
    private ReservationCost getTotalReservationCost(Token session_token, Integer reservation_id) throws FailedDbFetch, SessionExpiredException, SessionInvalidException {
        try {
            ObjectTable room_reservations = this.aggregator.getItemisedReservation(session_token, reservation_id);
            ReservationCost reservationCost = new ReservationCost();
            for (int j = 1; j <= room_reservations.rowCount(); j++) {
                HashMap<String, Object> row = room_reservations.getRow(j);
                if ((Integer) row.get("cancelled_flag") == 0) {
                    reservationCost.incrementRoomCount();
                    Double price = (Double) row.get("room_price");
                    Double slots = hourCount(
                            TimestampConverter.getDateObject((String) row.get("timestamp_in")),
                            TimestampConverter.getDateObject((String) row.get("timestamp_out"))
                    );
                    reservationCost.addToCost(price * slots);
                    log.log_Trace("room ", reservationCost.getRoomCount(), ": ", price, " x ", slots, ", { ",
                            row.get("timestamp_in"), " -> ", row.get("timestamp_out"), " } = ",
                            (price * slots), "; running balance = ", reservationCost.getTotalCost()
                    );
                }
            }
            return reservationCost;
        } catch (DbQueryException e) {
            log.log_Error("Could not fetch itemised reservation data from records for Reservation[", reservation_id, "].");
            throw new FailedDbFetch("Could not fetch itemised reservation data from records for Reservation[" + reservation_id + "].", e);
        }
    }

    /**
     * Gets all payments associated with a reservation
     *
     * @param session_token  Session token
     * @param reservation_id Reservation ID
     * @return List of Payment DTOs
     * @throws FailedDbFetch           when error occurred during fetching of data from DB
     * @throws SessionExpiredException when the session for the id provided has expired
     * @throws SessionInvalidException when the session for the id provided does not exist in the tracker
     */
    private List<Payment> getPayments(Token session_token, Integer reservation_id) throws FailedDbFetch, SessionExpiredException, SessionInvalidException {
        try {
            ObjectTable reservation_payments = this.aggregator.getPayments(session_token, reservation_id);
            List<Payment> payments = new ArrayList<>();
            for (int j = 1; j <= reservation_payments.rowCount(); j++) {
                HashMap<String, Object> row = reservation_payments.getRow(j);
                payments.add(
                        new Payment(
                                (Integer) row.get("payment_id"),
                                (String) row.get("hash_id"),
                                (Double) row.get("amount"),
                                TimestampConverter.getDateObject((String) row.get("timestamp")),
                                (String) row.get("note"),
                                new PaymentMethod(
                                        (Integer) row.get("method_id"),
                                        (String) row.get("method_description")
                                )
                        )
                );
            }
            return payments;
        } catch (DbQueryException e) {
            log.log_Error("");
            throw new FailedDbFetch("", e);
        }
    }

    /**
     * Creates and fills an Occupancy object with data from an ObjectTable
     *
     * @param table ObjectTable with fields {building_id, floor_id, room_id, room_category_id, room_description, timestamp_in, timestamp_out}
     * @return Occupancy object
     */
    private Occupancy getOccupancy(ObjectTable table) {
        Occupancy occupancy = new Occupancy();
        for (int i = 1; i <= table.rowCount(); i++) {
            HashMap<String, Object> row = table.getRow(i);
            Room room = new Room(
                    (Integer) row.get("room_id"),
                    (Integer) row.get("floor_id"),
                    (Integer) row.get("building_id"),
                    (Integer) row.get("room_category_id"),
                    (String) row.get("room_description")
            );
            occupancy.add(
                    room,
                    new TimeSpan(
                            (String) row.get("timestamp_in"),
                            (String) row.get("timestamp_out")
                    )
            );
        }
        return occupancy;
    }

    /**
     * Constructor
     *
     * @param db_access IReservationDbAccess instance
     */
    public ReportCreator(IReservationDbAccess db_access) {
        this.aggregator = new RevenueAggregator(db_access);
    }

    /**
     * Constructor
     *
     * @param aggregator RevenueAggregator instance
     */
    public ReportCreator(RevenueAggregator aggregator) {
        this.aggregator = aggregator;
    }

    @Override
    public List<Building> getBuildings(Token admin_token) throws FailedDbFetch, SessionExpiredException, SessionInvalidException {
        try {
            List<Building> list = new ArrayList<>();
            ObjectTable table = this.aggregator.getBuildings(admin_token);
            if (!table.isEmpty()) {
                for (int i = 1; i <= table.rowCount(); i++) {
                    HashMap<String, Object> row = table.getRow(i);
                    list.add(
                            new Building(
                                    (Integer) row.get("id"),
                                    (String) row.get("name"),
                                    (String) row.get("address1"),
                                    (String) row.get("address2"),
                                    (String) row.get("postcode"),
                                    (String) row.get("city"),
                                    (String) row.get("country"),
                                    (String) row.get("telephone")
                            )
                    );
                }
            }
            return list;
        } catch (DbQueryException e) {
            log.log_Fatal("Could not fetch buildings from records.");
            throw new FailedDbFetch("Could not fetch buildings from records.", e);
        }
    }

    @Override
    public List<Floor> getFloors(Token admin_token, Building building) throws FailedDbFetch, SessionExpiredException, SessionInvalidException {
        try {
            List<Floor> list = new ArrayList<>();
            ObjectTable table = this.aggregator.getFloors(admin_token, building);
            if (!table.isEmpty()) {
                for (int i = 1; i <= table.rowCount(); i++) {
                    HashMap<String, Object> row = table.getRow(i);
                    list.add(
                            new Floor(
                                    (Integer) row.get("building_id"),
                                    (Integer) row.get("id"),
                                    (String) row.get("description")
                            )
                    );
                }
            }
            return list;
        } catch (DbQueryException e) {
            log.log_Fatal("Could not fetch floors for Building[", building.id(), "] from records.");
            throw new FailedDbFetch("Could not fetch floors for Buildings[" + building.id() + "] from records.", e);
        }
    }

    @Override
    public List<Room> getRooms(Token admin_token, Floor floor) throws FailedDbFetch, SessionExpiredException, SessionInvalidException {
        try {
            List<Room> list = new ArrayList<>();
            ObjectTable table = this.aggregator.getRooms(admin_token, floor);
            if (!table.isEmpty()) {
                for (int i = 1; i <= table.rowCount(); i++) {
                    HashMap<String, Object> row = table.getRow(i);
                    list.add(
                            new Room(
                                    (Integer) row.get("id"),
                                    (Integer) row.get("floor_id"),
                                    (Integer) row.get("building_id"),
                                    (Integer) row.get("room_category_id"),
                                    (String) row.get("description")
                            )
                    );
                }
            }
            return list;
        } catch (DbQueryException e) {
            log.log_Fatal("Could not fetch rooms from records for floor: ", floor);
            throw new FailedDbFetch("Could not fetch rooms from records for floor: " + floor, e);
        }
    }

    @Override
    public List<SimpleCustomerBalance> getCustomerBalance(Token session_token) throws FailedDbFetch, SessionExpiredException, SessionInvalidException {
        try {
            ObjectTable table = this.aggregator.getBalance(session_token);
            List<SimpleCustomerBalance> list = new ArrayList<>();
            for (int i = 1; i <= table.rowCount(); i++) {
                HashMap<String, Object> row = table.getRow(i);
                list.add(
                        new SimpleCustomerBalance(
                                (Integer) row.get("customer_id"),
                                (Integer) row.get("reservation_count"),
                                (Double) row.get("total_cost"),
                                (Double) row.get("total_paid"),
                                (Double) row.get("final_balance")
                        )
                );
            }
            return list;
        } catch (DbQueryException e) {
            log.log_Error("Could not fetch balance records for customers.");
            throw new FailedDbFetch("Could not fetch balance records for customers.", e);
        }
    }

    @Override
    public CustomerBalance getCustomerBalance(Token session_token, Customer customer) throws FailedDbFetch, SessionExpiredException, SessionInvalidException {
        try {
            ObjectTable reservations = this.aggregator.getCustomerReservationIDs(session_token, customer);

            CustomerBalance customerBalance = new CustomerBalance();
            for (int i = 1; i <= reservations.rowCount(); i++) {
                Integer reservation_id = reservations.getInteger(1, i);

                ReservationCost reservationCost = this.getTotalReservationCost(session_token, reservation_id);
                Discount discount = this.getDiscount(session_token, reservation_id);

                customerBalance.addReservationBalance(
                        new ReservationBalance(
                                reservation_id,
                                reservationCost.getRoomCount(),
                                reservationCost.getTotalCost(),
                                discount,
                                this.getPayments(session_token, reservation_id)
                        )
                );
            }
            return customerBalance;
        } catch (DbQueryException e) {
            log.log_Error("Could not fetch data from records for Customer[", customer.customerID(), "]");
            throw new FailedDbFetch("Could not fetch data from records.", e);
        } catch (FailedDbFetch e) {
            log.log_Error("Could not fetch some data from records.");
            throw new FailedDbFetch("Could not fetch some data from records.", e);
        }
    }

    @Override
    public Occupancy getOccupancy(Token session_token, Date from, Date to) throws InvalidPeriodException, FailedDbFetch, SessionExpiredException, SessionInvalidException {
        try {
            checkDate(from, to);
            ObjectTable table = this.aggregator.getReservationScheduleData(session_token, from, to);
            return getOccupancy(table);
        } catch (DbQueryException e) {
            log.log_Error(""); //TODO
            throw new FailedDbFetch("", e);
        }
    }

    @Override
    public Occupancy getOccupancy(Token session_token, Building building, Date from, Date to) throws InvalidPeriodException, FailedDbFetch, SessionExpiredException, SessionInvalidException {
        try {
            checkDate(from, to);
            ObjectTable table = this.aggregator.getReservationScheduleData(session_token, building, from, to);
            return getOccupancy(table);
        } catch (DbQueryException e) {
            log.log_Error(""); //TODO
            throw new FailedDbFetch("", e);
        }
    }

    @Override
    public Occupancy getOccupancy(Token session_token, Floor floor, Date from, Date to) throws InvalidPeriodException, FailedDbFetch, SessionExpiredException, SessionInvalidException {
        try {
            checkDate(from, to);
            ObjectTable table = this.aggregator.getReservationScheduleData(session_token, floor, from, to);
            return getOccupancy(table);
        } catch (DbQueryException e) {
            log.log_Error(""); //TODO
            throw new FailedDbFetch("", e);
        }
    }

    @Override
    public Occupancy getOccupancy(Token session_token, Room room, Date from, Date to) throws InvalidPeriodException, FailedDbFetch, SessionExpiredException, SessionInvalidException {
        try {
            checkDate(from, to);
            ObjectTable table = this.aggregator.getReservationScheduleData(session_token, room, from, to);
            return getOccupancy(table);
        } catch (DbQueryException e) {
            log.log_Error(""); //TODO
            throw new FailedDbFetch("", e);
        }
    }

    @Override
    public RevenueReport getRevenueReport(Token session_token, Date from, Date to) throws InvalidPeriodException, FailedDbFetch, SessionExpiredException, SessionInvalidException {
        checkDate(from, to);
        return null;
    }

    @Override
    public RevenueReport getRevenueReport(Token session_token, Building building, Date from, Date to) throws InvalidPeriodException, FailedDbFetch, SessionExpiredException, SessionInvalidException {
        checkDate(from, to);
        return null;
    }

    @Override
    public RevenueReport getRevenueReport(Token session_token, Floor floor, Date from, Date to) throws InvalidPeriodException, FailedDbFetch, SessionExpiredException, SessionInvalidException {
        checkDate(from, to);
        return null;
    }

    @Override
    public RevenueReport getRevenueReport(Token session_token, Room room, Date from, Date to) throws InvalidPeriodException, FailedDbFetch, SessionExpiredException, SessionInvalidException {
        checkDate(from, to);
        return null;
    }
}
