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
import bbk_beam.mtRooms.reservation.exception.InvalidCustomer;
import bbk_beam.mtRooms.reservation.exception.InvalidReservation;
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
     * Gets the Customer account details
     *
     * @param session_token Session token
     * @param customerID    Customer ID
     * @return Customer details
     * @throws DbQueryException        when a problem was encountered whilst processing the query
     * @throws SessionExpiredException When the session for the id provided has expired
     * @throws SessionInvalidException When the session for the id provided does not exist in the tracker
     */
    private Customer getCustomerAccount(Token session_token, Integer customerID) throws InvalidCustomer, DbQueryException, SessionExpiredException, SessionInvalidException {
        ObjectTable table = this.aggregator.getCustomerAccount(session_token, customerID);
        try {
            HashMap<String, Object> row = table.getRow(1);
            return new Customer(
                    (Integer) row.get("id"),
                    (Integer) row.get("membership_type_id"),
                    TimestampConverter.getDateObject((String) row.get("customer_since")),
                    (String) row.get("title"),
                    (String) row.get("name"),
                    (String) row.get("surname"),
                    (String) row.get("address_1"),
                    (String) row.get("address_2"),
                    (String) row.get("postcode"),
                    (String) row.get("city"),
                    (String) row.get("county"),
                    (String) row.get("country"),
                    (String) row.get("telephone_1"),
                    (String) row.get("telephone_2"),
                    (String) row.get("email"));
        } catch (IndexOutOfBoundsException e) {
            log.log_Error("Customer [", customerID, "] does not exist in records.");
            throw new InvalidCustomer("Customer [" + customerID + "] does not exist in records.", e);
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
    private Reservation getReservation(Token session_token, Integer reservation_id) throws InvalidReservation, FailedDbFetch, SessionExpiredException, SessionInvalidException {
        try {
            ObjectTable reservation_table = this.aggregator.getReservation(session_token, reservation_id);
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

            ObjectTable rooms_table = this.aggregator.getReservedRooms(session_token, reservation);
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
            log.log_Error("Could not fetch payments for Reservation[", reservation_id, "].");
            throw new FailedDbFetch("Could not fetch payments for Reservation[" + reservation_id + "].", e);
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
    ReportCreator(IReservationDbAccess db_access) {
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
    public SimpleCustomerBalance getSimpleCustomerBalance(Token session_token, Customer customer) throws FailedDbFetch, SessionExpiredException, SessionInvalidException {
        try {
            ObjectTable table = this.aggregator.getBalance(session_token, customer);
            if (!table.isEmpty()) {
                HashMap<String, Object> row = table.getRow(1);
                return new SimpleCustomerBalance(
                        (Integer) row.get("customer_id"),
                        (Integer) row.get("reservation_count"),
                        (Double) row.get("total_cost"),
                        (Double) row.get("total_paid"),
                        (Double) row.get("final_balance")
                );
            } else {
                log.log_Error("No balance records found for customer [", customer.customerID(), "].");
                throw new FailedDbFetch("No balance records found for customer [" + customer.customerID() + "].");
            }
        } catch (DbQueryException e) {
            log.log_Error("Could not fetch balance records for customers.");
            throw new FailedDbFetch("Could not fetch balance records for customers.", e);
        }
    }

    @Override
    public Occupancy getOccupancy(Token session_token, Date from, Date to) throws InvalidPeriodException, FailedDbFetch, SessionExpiredException, SessionInvalidException {
        try {
            checkDate(from, to);
            ObjectTable table = this.aggregator.getReservationScheduleData(session_token, from, to);
            return getOccupancy(table);
        } catch (DbQueryException e) {
            log.log_Error("Could not fetch occupancy data from records: ", from, " -> ", to);
            throw new FailedDbFetch("Could not fetch occupancy data from records: " + from + " -> " + to, e);
        }
    }

    @Override
    public Occupancy getOccupancy(Token session_token, Building building, Date from, Date to) throws InvalidPeriodException, FailedDbFetch, SessionExpiredException, SessionInvalidException {
        try {
            checkDate(from, to);
            ObjectTable table = this.aggregator.getReservationScheduleData(session_token, building, from, to);
            return getOccupancy(table);
        } catch (DbQueryException e) {
            log.log_Error("Could not fetch building [", building.id(), "] occupancy data from records: ", from, " -> ", to);
            throw new FailedDbFetch("Could not fetch building [" + building.id() + "] occupancy data from records: " + from + " -> " + to, e);
        }
    }

    @Override
    public Occupancy getOccupancy(Token session_token, Floor floor, Date from, Date to) throws InvalidPeriodException, FailedDbFetch, SessionExpiredException, SessionInvalidException {
        try {
            checkDate(from, to);
            ObjectTable table = this.aggregator.getReservationScheduleData(session_token, floor, from, to);
            return getOccupancy(table);
        } catch (DbQueryException e) {
            log.log_Error("Could not fetch floor [", floor.buildingID(), ".", floor.floorID(), "] occupancy data from records: ", from, " -> ", to);
            throw new FailedDbFetch("Could not fetch floor [" + floor.buildingID() + "." + floor.floorID() + "] occupancy data from records: " + from + " -> " + to, e);
        }
    }

    @Override
    public Occupancy getOccupancy(Token session_token, Room room, Date from, Date to) throws InvalidPeriodException, FailedDbFetch, SessionExpiredException, SessionInvalidException {
        try {
            checkDate(from, to);
            ObjectTable table = this.aggregator.getReservationScheduleData(session_token, room, from, to);
            return getOccupancy(table);
        } catch (DbQueryException e) {
            log.log_Error("Could not fetch room [", room.buildingID(), ".", room.floorID(), ".", room.id(), "] occupancy data from records: ", from, " -> ", to);
            throw new FailedDbFetch("Could not fetch room [" + room.buildingID() + "." + room.floorID() + "." + room.id() + "] occupancy data from records: " + from + " -> " + to, e);
        }
    }

    @Override
    public Invoice createInvoice(Token session_token, Integer reservation_id) throws InvalidReservation, InvalidCustomer, FailedDbFetch, SessionExpiredException, SessionInvalidException {
        try {
            ObjectTable customerID_table = this.aggregator.getCustomerID(session_token, reservation_id);
            if (!customerID_table.isEmpty()) {
                Customer customer = this.getCustomerAccount(session_token, customerID_table.getInteger(1, 1));

                //Reservation details
                Reservation reservation = this.getReservation(session_token, reservation_id);

                //Reservation balance
                ReservationCost reservation_cost = this.getTotalReservationCost(session_token, reservation_id);
                Discount discount = this.getDiscount(session_token, reservation_id);
                ReservationBalance reservation_balance = new ReservationBalance(
                        reservation_id,
                        reservation_cost.getRoomCount(),
                        reservation_cost.getTotalCost(),
                        discount,
                        this.getPayments(session_token, reservation_id)
                );

                //Customer account balance
                SimpleCustomerBalance customer_balance = this.getSimpleCustomerBalance(session_token, customer);

                return new Invoice(
                        customer,
                        reservation,
                        reservation_balance,
                        customer_balance
                );
            } else {
                log.log_Fatal("Customer in Reservation [", reservation_id, "] does not exist in records.");
                throw new FailedDbFetch("Customer in Reservation [" + reservation_id + "] does not exist in records.");
            }
        } catch (DbQueryException e) {
            log.log_Error("Could not fetch information to create Invoice for Reservation [", reservation_id, "]");
            throw new FailedDbFetch("Could not fetch information to create Invoice for Reservation [" + reservation_id + "]", e);
        }
    }

    @Override
    public List<DetailedPayment> getPayments(Token session_token, Date from, Date to) throws InvalidPeriodException, FailedDbFetch, SessionExpiredException, SessionInvalidException {
        try {
            ObjectTable table = this.aggregator.getPayments(session_token, from, to);
            List<DetailedPayment> list = new ArrayList<>();
            for (int i = 1; i <= table.rowCount(); i++) {
                HashMap<String, Object> row = table.getRow(i);
                list.add(
                        new DetailedPayment(
                                (Integer) row.get("customer_id"),
                                (Integer) row.get("reservation_id"),
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
            return list;
        } catch (DbQueryException e) {
            log.log_Error("Could not fetch payment information for ", TimestampConverter.getUTCTimestampString(from), " -> ", TimestampConverter.getUTCTimestampString(to));
            throw new FailedDbFetch("Could not fetch payment information for " + from + " to " + to, e);
        }
    }
}
