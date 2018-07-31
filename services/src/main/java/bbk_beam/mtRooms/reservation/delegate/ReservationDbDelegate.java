package bbk_beam.mtRooms.reservation.delegate;

import bbk_beam.mtRooms.admin.authentication.Token;
import bbk_beam.mtRooms.db.IReservationDbAccess;
import bbk_beam.mtRooms.db.TimestampConverter;
import bbk_beam.mtRooms.db.exception.DbQueryException;
import bbk_beam.mtRooms.db.exception.SessionExpiredException;
import bbk_beam.mtRooms.db.exception.SessionInvalidException;
import bbk_beam.mtRooms.reservation.dto.*;
import bbk_beam.mtRooms.reservation.exception.*;
import eadjlib.datastructure.ObjectTable;
import eadjlib.logger.Logger;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;

public class ReservationDbDelegate implements ICustomerAccount, IPay, IReserve, ISearch {
    private final Logger log = Logger.getLoggerInstance(ReservationDbDelegate.class.getName());
    private IReservationDbAccess db_access;

    /**
     * RoomProperty filter query builder
     *
     * @param property RoomProperty DTO
     * @return Filter query built from the RoomProperty object
     */
    private String buildFilterQueryString(RoomProperty property) {
        LinkedList<String> filters = new LinkedList<>();
        //Fixed chairs
        if (property.hasFixedChairs() == RoomProperty.Trilean.TRUE)
            filters.addLast("RoomFixtures.fixed_chairs == 1");
        if (property.hasFixedChairs() == RoomProperty.Trilean.FALSE)
            filters.addLast("RoomFixtures.fixed_chairs == 0");
        //Catering space
        if (property.hasCateringSpace() == RoomProperty.Trilean.TRUE)
            filters.addLast("RoomFixtures.catering_space == 1");
        if (property.hasCateringSpace() == RoomProperty.Trilean.FALSE)
            filters.addLast("RoomFixtures.catering_space == 0");
        //Whiteboard
        if (property.hasWhiteboard() == RoomProperty.Trilean.TRUE)
            filters.addLast("RoomFixtures.whiteboard == 1");
        if (property.hasWhiteboard() == RoomProperty.Trilean.FALSE)
            filters.addLast("RoomFixtures.whiteboard == 0");
        //Projector
        if (property.hasProjector() == RoomProperty.Trilean.TRUE)
            filters.addLast("RoomFixtures.projector == 1");
        if (property.hasProjector() == RoomProperty.Trilean.FALSE)
            filters.addLast("RoomFixtures.projector == 0");
        //Capacity
        if (property.capacity() > 0)
            filters.addLast("RoomCategory.capacity >= " + property.capacity());
        //Dimension
        if (property.dimension() > 0)
            filters.addLast("RoomCategory.dimension >= " + property.dimension());

        StringBuilder sb = new StringBuilder();
        int i = filters.size();
        for (String filter : filters) {
            sb.append(filter);
            if ((--i) > 0)
                sb.append(" AND ");
        }
        return sb.toString();
    }

    /**
     * Constructor
     *
     * @param reservationDbAccess ReservationDbAccess instance
     */
    public ReservationDbDelegate(IReservationDbAccess reservationDbAccess) {
        this.db_access = reservationDbAccess;
    }

    @Override
    public ObjectTable getCustomerAccount(Token session_token, Integer customerID) throws DbQueryException, SessionExpiredException, SessionInvalidException {
        String query = "SELECT "
                + "id, "
                + "membership_type_id, "
                + "customer_since, "
                + "title, "
                + "name, "
                + "surname, "
                + "address_1, "
                + "address_2, "
                + "city, "
                + "county, "
                + "country, "
                + "postcode, "
                + "telephone_1, "
                + "telephone_2, "
                + "email "
                + "FROM Customer WHERE id = " + customerID;
        return this.db_access.pullFromDB(session_token.getSessionId(), query);
    }

    @Override
    public ObjectTable getCustomerAccount(Token session_token, Customer customer) throws DbQueryException, SessionExpiredException, SessionInvalidException {
        if (customer.customerID() < 1) {
            String query = "SELECT " +
                    "id, " +
                    "membership_type_id, " +
                    "customer_since, " +
                    "title, " +
                    "name, " +
                    "surname, " +
                    "address_1, " +
                    "address_2, " +
                    "city, " +
                    "county, " +
                    "country, " +
                    "postcode, " +
                    "telephone_1, " +
                    "telephone_2, " +
                    "email " +
                    "FROM Customer " +
                    "WHERE membership_type_id = " + customer.membershipTypeID() +
                    " AND customer_since = \"" + TimestampConverter.getUTCTimestampString(customer.accountCreationDate()) + "\"" +
                    " AND title = \"" + customer.title() + "\"" +
                    " AND name = \"" + customer.name() + "\"" +
                    " AND surname = \"" + customer.surname() + "\"" +
                    " AND address_1 = \"" + customer.address1() + "\"" +
                    " AND address_2 " + (customer.address2() == null ? "isnull" : "= \"" + customer.address2() + "\"") + "" +
                    " AND city = \"" + customer.city() + "\"" +
                    " AND county " + (customer.county() == null ? "isnull" : "= \"" + customer.county() + "\"") + "" +
                    " AND country = \"" + customer.country() + "\"" +
                    " AND postcode = \"" + customer.postCode() + "\"" +
                    " AND telephone_1 = \"" + customer.phone1() + "\"" +
                    " AND telephone_2 " + (customer.phone2() == null ? "isnull" : "= \"" + customer.phone2() + "\"") + "" +
                    " AND email = \"" + customer.email() + "\"";
            return this.db_access.pullFromDB(session_token.getSessionId(), query);
        } else {
            return getCustomerAccount(session_token, customer.customerID());
        }
    }

    @Override
    public ObjectTable findCustomer(Token session_token, String surname) throws DbQueryException, SessionExpiredException, SessionInvalidException {
        String query = "SELECT "
                + "id, title, name, surname "
                + "FROM Customer "
                + "WHERE surname = \"" + surname + "\" COLLATE NOCASE";
        return this.db_access.pullFromDB(session_token.getSessionId(), query);
    }

    @Override
    public void createNewCustomer(Token session_token, Customer customer) throws FailedDbWrite, DbQueryException, SessionExpiredException, SessionInvalidException {
        String query = "INSERT INTO Customer( "
                + "membership_type_id, "
                + "customer_since, "
                + "title, "
                + "name, "
                + "surname, "
                + "address_1, "
                + "address_2, "
                + "city, "
                + "county, "
                + "country, "
                + "postcode, "
                + "telephone_1, "
                + "telephone_2, "
                + "email "
                + ") VALUES ( "
                + customer.membershipTypeID() + ", "
                + "\"" + TimestampConverter.getUTCTimestampString(customer.accountCreationDate()) + "\", "
                + "\"" + customer.title() + "\", "
                + "\"" + customer.name() + "\", "
                + "\"" + customer.surname() + "\", "
                + "\"" + customer.address1() + "\", "
                + (customer.address2() == null ? null : "\"" + customer.address2() + "\"") + ", "
                + "\"" + customer.city() + "\", "
                + (customer.county() == null ? null : "\"" + customer.county() + "\"") + ", "
                + "\"" + customer.country() + "\", "
                + "\"" + customer.postCode() + "\", "
                + "\"" + customer.phone1() + "\", "
                + (customer.phone2() == null ? null : "\"" + customer.phone2() + "\"") + ", "
                + "\"" + customer.email() + "\" "
                + ")";
        if (!this.db_access.pushToDB(session_token.getSessionId(), query)) {
            log.log_Error("Could not add customer: ", customer);
            throw new DbQueryException("Could not add customer to record.");
        }
        //Check if update was made
        ObjectTable table = this.db_access.pullFromDB(session_token.getSessionId(), "SELECT CHANGES()");
        if (table.getInteger(1, 1) == 0) {
            log.log_Error("Customer was not added: ", customer);
            throw new FailedDbWrite("Customer was not added to record.");
        }
    }

    @Override
    public void saveCustomerChangesToDB(Token session_token, Customer customer) throws FailedDbWrite, DbQueryException, SessionExpiredException, SessionInvalidException {
        String query = "UPDATE Customer SET "
                + "membership_type_id = " + customer.membershipTypeID() + ", "
                + "customer_since = \"" + TimestampConverter.getUTCTimestampString(customer.accountCreationDate()) + "\", "
                + "title = \"" + customer.title() + "\", "
                + "name = \"" + customer.name() + "\", "
                + "surname = \"" + customer.surname() + "\", "
                + "address_1 = \"" + customer.address1() + "\", "
                + "address_2 = " + (customer.address2() == null ? null : "\"" + customer.address2() + "\"") + ", "
                + "city = \"" + customer.city() + "\", "
                + "county = " + (customer.county() == null ? null : "\"" + customer.county() + "\"") + ", "
                + "country = \"" + customer.country() + "\", "
                + "postcode = \"" + customer.postCode() + "\", "
                + "telephone_1 = \"" + customer.phone1() + "\", "
                + "telephone_2 = " + (customer.phone2() == null ? null : "\"" + customer.phone2() + "\"") + ", "
                + "email = \"" + customer.email() + "\" "
                + "WHERE id = " + customer.customerID();
        this.db_access.pushToDB(session_token.getSessionId(), query);
        //Check if update was made
        ObjectTable table = this.db_access.pullFromDB(session_token.getSessionId(), "SELECT CHANGES()");
        if (table.getInteger(1, 1) == 0) {
            log.log_Error("Could not update customer [", customer.customerID(), "]'s record.");
            throw new FailedDbWrite("Could not update customer [" + customer.customerID() + "]'s record.");
        }
    }

    @Override
    public ObjectTable getMembership(Token session_token, Integer membership_id) throws DbQueryException, SessionExpiredException, SessionInvalidException {
        String query = "SELECT " +
                "MembershipType.id, " +
                "MembershipType.description, " +
                "DiscountCategory.id AS discount_category_id, " +
                "DiscountCategory.description AS discount_category_desc, " +
                "Discount.id AS discount_id, " +
                "Discount.discount_rate AS discount_rate " +
                "FROM MembershipType " +
                "LEFT OUTER JOIN DiscountCategory ON MembershipType.discount_category_id = DiscountCategory.id " +
                "LEFT OUTER JOIN Discount ON DiscountCategory.id = Discount.discount_category_id " +
                "WHERE MembershipType.id = " + membership_id;
        return this.db_access.pullFromDB(session_token.getSessionId(), query);
    }

    @Override
    public ObjectTable getMemberships(Token session_token) throws DbQueryException, SessionExpiredException, SessionInvalidException {
        String query = "SELECT " +
                "MembershipType.id, " +
                "MembershipType.description, " +
                "DiscountCategory.id AS discount_category_id, " +
                "DiscountCategory.description AS discount_category_desc, " +
                "Discount.id AS discount_id, " +
                "Discount.discount_rate AS discount_rate " +
                "FROM MembershipType " +
                "LEFT OUTER JOIN DiscountCategory ON MembershipType.discount_category_id = DiscountCategory.id " +
                "LEFT OUTER JOIN Discount ON DiscountCategory.id = Discount.discount_category_id";
        return this.db_access.pullFromDB(session_token.getSessionId(), query);
    }

    @Override
    public void pay(Token session_token, Reservation reservation, Payment payment) throws DbQueryException, FailedDbFetch, SessionExpiredException, SessionInvalidException {
        //Adding Payment entry
        String query1 = "INSERT INTO Payment( hash_id, amount, payment_method, timestamp, note ) VALUES ( " +
                "\"" + payment.hashID() + "\", " +
                payment.amount() + ", " +
                payment.paymentMethod().id() + ", " +
                "\"" + TimestampConverter.getUTCTimestampString(payment.timestamp()) + "\", " +
                (payment.note().isEmpty() ? null : "\"" + payment.note() + "\" ") +
                ")";
        if (!this.db_access.pushToDB(session_token.getSessionId(), query1)) {
            log.log_Error("Could not add payment (method=", payment.paymentMethod(), ", amount=", payment.amount(), ") for Reservation: ", reservation);
            throw new DbQueryException("Could not add payment (method=" + payment.paymentMethod() + ", amount=" + payment.amount() + ") for Reservation: " + reservation);
        }
        //Getting the row_id for Payment added
        String query2 = "SELECT id FROM Payment WHERE hash_id = \"" + payment.hashID() + "\"";
        ObjectTable row_id_table = this.db_access.pullFromDB(session_token.getSessionId(), query2);
        if (row_id_table.isEmpty()) {
            log.log_Fatal("Could not get the ID for the added Payment (hash_id=", payment.hashID(), ") for Reservation [", reservation.id(), "] in records.");
            throw new FailedDbFetch("Could not get the ID for the added Payment (hash_id=" + payment.hashID() + ") for Reservation [" + reservation.id() + "] in records.");
        }
        Integer payment_id = row_id_table.getInteger(1, 1);
        //Adding entry to Reservation_has_Payment for the Payment
        String query3 = "Insert INTO Reservation_has_Payment( reservation_id, payment_id ) VALUES ( " +
                reservation.id() + ", " + payment_id + " )";
        if (!this.db_access.pushToDB(session_token.getSessionId(), query3)) {
            log.log_Error("Could not add link between Payment [", payment_id, "]=(", payment.hashID(), ") and Reservation [", reservation.id(), "]");
            throw new DbQueryException("Could not add link between Payment [" + payment_id + "]=(" + payment.hashID() + ") and Reservation [" + reservation.id() + "]");
        }
    }

    @Override
    public ObjectTable getPayments(Token session_token, Reservation reservation) throws DbQueryException, SessionExpiredException, SessionInvalidException {
        String query = "SELECT " +
                "Payment.id, " +
                "Payment.hash_id, " +
                "Payment.amount, " +
                "Payment.payment_method AS method_id, " +
                "PaymentMethod.description AS method_description, " +
                "Payment.timestamp, " +
                "Payment.note " +
                "FROM Reservation_has_Payment " +
                "LEFT OUTER JOIN Payment ON Reservation_has_Payment.payment_id = Payment.id " +
                "LEFT OUTER JOIN PaymentMethod ON Payment.payment_method = PaymentMethod.id " +
                "WHERE Reservation_has_Payment.reservation_id = " + reservation.id();
        ObjectTable table = this.db_access.pullFromDB(session_token.getSessionId(), query);
        if (table.isEmpty()) {
            log.log_Debug("No payments found for reservation [", reservation.id(), "] in records.");
        }
        return table;
    }

    @Override
    public ObjectTable getPaymentMethods(Token session_token) throws DbQueryException, SessionExpiredException, SessionInvalidException {
        String query = "SELECT * FROM PaymentMethod";
        ObjectTable table = this.db_access.pullFromDB(session_token.getSessionId(), query);
        if (table.isEmpty()) {
            log.log_Debug("No payments methods found in records.");
        }
        return table;
    }

    @Override
    public ObjectTable getFinancialSummary(Token session_token, Reservation reservation) throws DbQueryException, SessionExpiredException, SessionInvalidException {
        String query = "SELECT " +
                "Confirmed_Rooms.room_count AS confirmed_count, " +
                "Confirmed_Rooms.room_subtotal AS confirmed_subtotal, " +
                "Cancelled_Rooms.room_count AS cancelled_count, " +
                "Cancelled_Rooms.room_subtotal AS cancelled_subtotal, " +
                "Payments.pay_count AS payment_count, " +
                "Payments.credit AS payment_total, " +
                "Discount.discount_rate " +
                "FROM Reservation " +
                "LEFT OUTER JOIN Discount ON Reservation.discount_id = Discount.id " +
                "LEFT OUTER JOIN (" +
                " SELECT" +
                " COUNT( * ) AS room_count," +
                " SUM( RoomPrice.price ) as room_subtotal" +
                " FROM Room_has_Reservation" +
                " INNER JOIN RoomPrice" +
                " ON Room_has_Reservation.room_price_id = RoomPrice.id " +
                " WHERE Room_has_Reservation.cancelled_flag = 0 AND Room_has_Reservation.reservation_id = " + reservation.id() + " " +
                ") AS Confirmed_Rooms " +
                "LEFT OUTER JOIN (" +
                " SELECT" +
                " SUM( RoomPrice.price ) AS room_subtotal," +
                " SUM( Room_has_Reservation.cancelled_flag ) AS room_count" +
                " FROM Room_has_Reservation" +
                " INNER JOIN RoomPrice ON Room_has_Reservation.room_price_id = RoomPrice.id" +
                " WHERE Room_has_Reservation.cancelled_flag = 1 AND Room_has_Reservation.reservation_id = " + reservation.id() + " " +
                ") AS Cancelled_Rooms " +
                "LEFT OUTER JOIN ( " +
                " SELECT" +
                " COUNT( * ) AS pay_count," +
                " SUM( Payment.amount ) AS credit" +
                " FROM Reservation_has_Payment" +
                " INNER JOIN Payment ON Reservation_has_Payment.payment_id = Payment.id" +
                " WHERE Reservation_has_Payment.reservation_id = " + reservation.id() + " " +
                ") AS Payments " +
                "WHERE Reservation.id = " + reservation.id();
        ObjectTable table = this.db_access.pullFromDB(session_token.getSessionId(), query);
        if (table.isEmpty()) {
            log.log_Error("Could not fetch financial summary for Reservation [", reservation.id(), "] from records.");
            throw new DbQueryException("Could not fetch financial summary for Reservation [" + reservation.id() + "] from records.");
        }
        return table;
    }

    @Override
    public Integer createReservation(Token session_token, Reservation reservation) throws DbQueryException, SessionExpiredException, SessionInvalidException {
        String query1 = "INSERT INTO Reservation " +
                "( created_timestamp, customer_id, discount_id ) " +
                "VALUES ( " +
                "\"" + TimestampConverter.getUTCTimestampString(reservation.createdTimestamp()) + "\", " +
                reservation.customerID() + ", " +
                reservation.discount().id() + " )";
        if (!this.db_access.pushToDB(session_token.getSessionId(), query1)) {
            log.log_Error("Could not add Reservation to records: ", reservation);
            throw new DbQueryException("Could not add Reservation to records: " + reservation);
        }
        String query2 = "SELECT id FROM Reservation " +
                "WHERE created_timestamp = \"" + TimestampConverter.getUTCTimestampString(reservation.createdTimestamp()) + "\"" +
                " AND customer_id = " + reservation.customerID() +
                " AND discount_id = " + reservation.discount().id();
        ObjectTable table = this.db_access.pullFromDB(session_token.getSessionId(), query2);
        if (table.isEmpty()) {
            log.log_Error("Could not retrieve ID of created Reservation from records: ", reservation);
            throw new DbQueryException("Could not retrieve ID of created Reservation from records: " + reservation);
        }
        return table.getInteger(1, 1);
    }

    @Override
    public void createRoomReservation(Token session_token, Integer reservation_id, RoomReservation room_reservation) throws DbQueryException, SessionExpiredException, SessionInvalidException {
        String query = "INSERT INTO Room_has_Reservation " +
                "( room_id, floor_id, building_id, reservation_id, timestamp_in, timestamp_out, room_price_id, seated_count, catering, notes ) " +
                "VALUES ( " +
                room_reservation.room().id() + ", " +
                room_reservation.room().floorID() + ", " +
                room_reservation.room().buildingID() + ", " +
                reservation_id + ", " +
                "\"" + TimestampConverter.getUTCTimestampString(room_reservation.reservationStart()) + "\", " +
                "\"" + TimestampConverter.getUTCTimestampString(room_reservation.reservationEnd()) + "\", " +
                room_reservation.price().id() + ", " +
                room_reservation.seatedCount() + ", " +
                (room_reservation.hasCateringRequired() ? 1 : 0) + ", " +
                (room_reservation.note().isEmpty() ? null : "\"" + room_reservation.note() + "\" ") +
                ")";
        if (!this.db_access.pushToDB(session_token.getSessionId(), query)) {
            log.log_Error("Could not add RoomReservation to records: ", room_reservation);
            throw new DbQueryException("Could not add RoomReservation to records: " + room_reservation);
        }
    }

    @Override
    public Double cancelReservation(Token session_token, Reservation reservation) throws InvalidReservation, DbQueryException, SessionExpiredException, SessionInvalidException {
        //Checking Reservation exists
        ObjectTable check = this.db_access.pullFromDB(
                session_token.getSessionId(),
                "SELECT COUNT(*) FROM Reservation WHERE id = " + reservation.id()
        );
        if (check.isEmpty())
            throw new InvalidReservation("Reservation [" + reservation.id() + "] does not exists in records.");
        //Setting all Room_has_Reservation entries linked the the Reservation as cancelled in records
        String query1 = "UPDATE " +
                "Room_has_Reservation " +
                "SET cancelled_flag = 1 " +
                "WHERE reservation_id = " + reservation.id();
        if (!this.db_access.pushToDB(session_token.getSessionId(), query1)) {
            log.log_Error("Could not cancel ReservedRooms in Reservation [", reservation.id(), "].");
            throw new DbQueryException("Could not cancel ReservedRooms in Reservation [" + reservation.id() + "].");
        }
        //Getting total of payments linked to Reservation
        String query2 = "SELECT " +
                "SUM( Payment.amount ) AS total_paid " +
                "FROM Reservation_has_Payment " +
                "INNER JOIN Payment ON Reservation_has_Payment.payment_id = Payment.id " +
                "WHERE Reservation_has_Payment.reservation_id = " + reservation.id();
        ObjectTable table = this.db_access.pullFromDB(session_token.getSessionId(), query2);
        if (table.isEmpty()) {
            log.log_Error("Could not get total paid on Reservation [", reservation.id(), "].");
            throw new DbQueryException("Could not get total paid on Reservation [" + reservation.id() + "].");
        }
        return table.getObject(1, 1) == null
                ? 0
                : table.getDouble(1, 1);
    }

    @Override
    public Double cancelReservedRoom(Token session_token, Integer reservation_id, RoomReservation reserved_room) throws InvalidReservation, DbQueryException, SessionExpiredException, SessionInvalidException {
        //Checking Reservation exists
        ObjectTable check = this.db_access.pullFromDB(
                session_token.getSessionId(),
                "SELECT COUNT(*) FROM Reservation WHERE id = " + reservation_id
        );
        if (check.isEmpty())
            throw new InvalidReservation("Reservation [" + reservation_id + "] does not exists in records.");
        //Setting Room_has_Reservation entry as cancelled in records
        String query1 = "UPDATE " +
                "Room_has_Reservation " +
                "SET cancelled_flag = 1 " +
                "WHERE room_id = " + reserved_room.room().id() +
                " AND floor_id = " + reserved_room.room().floorID() +
                " AND building_id = " + reserved_room.room().buildingID() +
                " AND reservation_id = " + reservation_id +
                " AND timestamp_in = \"" + TimestampConverter.getUTCTimestampString(reserved_room.reservationStart()) + "\"";
        if (!this.db_access.pushToDB(session_token.getSessionId(), query1)) {
            log.log_Error("Could not cancel RoomReservation: ", reserved_room);
            throw new DbQueryException("Could not cancel RoomReservation: : " + reserved_room);
        }
        //Getting the cancelled room's price
        String query2 = "SELECT " +
                "RoomPrice.price AS room_price " +
                "FROM Room_has_Reservation " +
                "LEFT OUTER JOIN RoomPrice " +
                "ON Room_has_Reservation.room_price_id = RoomPrice.id " +
                "WHERE Room_has_Reservation.reservation_id = " + reservation_id +
                " AND Room_has_Reservation.room_id = " + reserved_room.room().id() +
                " AND Room_has_Reservation.floor_id = " + reserved_room.room().floorID() +
                " AND Room_has_Reservation.building_id = " + reserved_room.room().buildingID();
        ObjectTable table = this.db_access.pullFromDB(session_token.getSessionId(), query2);
        if (table.isEmpty()) {
            log.log_Error("Could not get cost on RoomReservation:  ", reserved_room);
            throw new DbQueryException("Could not get cost on RoomReservation:  " + reserved_room);
        }
        return table.getDouble(1, 1);
    }

    /**
     * Deletes the record of a RoomReservation
     *
     * @param session_token  Session's token
     * @param reservation_id Reservation ID
     * @param room_id        RoomReservation room ID
     * @param floor_id       RoomReservation floor ID
     * @param building_id    RoomReservation building ID
     * @param timestamp_in   RoomReservation start timestamp
     * @throws DbQueryException        when a problem was encountered whilst processing the query
     * @throws SessionExpiredException when the session for the id provided has expired
     * @throws SessionInvalidException when the session for the id provided does not exist in the tracker
     */
    private void deleteReservedRoom(Token session_token,
                                    Integer reservation_id,
                                    Integer room_id,
                                    Integer floor_id,
                                    Integer building_id,
                                    String timestamp_in) throws DbQueryException, SessionExpiredException, SessionInvalidException {
        String query = "DELETE FROM Room_has_Reservation " +
                "WHERE reservation_id = " + reservation_id +
                " AND room_id = " + room_id +
                " AND floor_id = " + floor_id +
                " AND building_id = " + building_id +
                " AND timestamp_in = \"" + timestamp_in + "\"";
        this.db_access.pushToDB(session_token.getSessionId(), query);
    }

    @Override
    public void deleteReservation(Token session_token, Integer reservation_id) throws DbQueryException, InvalidReservation, SessionExpiredException, SessionInvalidException {
        String query1 = "SELECT id FROM Reservation WHERE id = " + reservation_id;
        if (this.db_access.pullFromDB(session_token.getSessionId(), query1).isEmpty()) {
            log.log_Error("Reservation [", reservation_id, "] to delete does not exist in records.");
            throw new InvalidReservation("Reservation [" + reservation_id + "] to delete does not exist in records.");
        }
        //Delete ReservedRooms in Reservation
        String query2 = "SELECT " +
                "room_id, floor_id, building_id, timestamp_in " +
                "FROM Room_has_Reservation " +
                "WHERE reservation_id = " + reservation_id;
        ObjectTable reserved_rooms = this.db_access.pullFromDB(session_token.getSessionId(), query2);
        if (!reserved_rooms.isEmpty()) {
            for (int i = 1; i <= reserved_rooms.rowCount(); i++) {
                HashMap<String, Object> row = reserved_rooms.getRow(i);
                Integer room_id = (Integer) row.get("room_id");
                Integer floor_id = (Integer) row.get("floor_id");
                Integer building_id = (Integer) row.get("building_id");
                String timestamp_in = (String) row.get("timestamp_in");
                deleteReservedRoom(session_token, reservation_id, room_id, floor_id, building_id, timestamp_in);
            }
        }
        //Delete Reservation entry
        String query3 = "DELETE FROM Reservation WHERE id = " + reservation_id;
        this.db_access.pushToDB(session_token.getSessionId(), query3);
    }

    @Override
    public ObjectTable getReservation(Token session_token, Integer reservation_id) throws InvalidReservation, DbQueryException, SessionExpiredException, SessionInvalidException {
        String query = "SELECT " +
                "Reservation.id, " +
                "Reservation.created_timestamp, " +
                "Reservation.customer_id, " +
                "Discount.id AS discount_id, " +
                "Discount.discount_rate, " +
                "Discount.discount_category_id, " +
                "DiscountCategory.description AS discount_category_description " +
                "FROM Reservation " +
                "LEFT OUTER JOIN Discount ON Reservation.discount_id = Discount.id " +
                "LEFT OUTER JOIN DiscountCategory ON Discount.discount_category_id = DiscountCategory.id " +
                "WHERE Reservation.id = " + reservation_id;
        ObjectTable table = this.db_access.pullFromDB(session_token.getSessionId(), query);
        if (!table.isEmpty()) {
            return table;
        } else {
            log.log_Error("Reservation [", reservation_id, "] does not exist in records.");
            throw new InvalidReservation("Reservation [" + reservation_id + "] does not exist in records.");
        }
    }

    @Override
    public ObjectTable getReservations(Token session_token, Customer customer) throws DbQueryException, SessionExpiredException, SessionInvalidException {
        String query = "SELECT " +
                "Reservation.id, " +
                "Reservation.created_timestamp, " +
                "Reservation.customer_id, " +
                "Discount.id AS discount_id, " +
                "Discount.discount_rate, " +
                "Discount.discount_category_id, " +
                "DiscountCategory.description AS discount_category_description " +
                "FROM Reservation " +
                "LEFT OUTER JOIN Discount ON Reservation.discount_id = Discount.id " +
                "LEFT OUTER JOIN DiscountCategory ON Discount.discount_category_id = DiscountCategory.id " +
                "WHERE Reservation.customer_id = " + customer.customerID();
        ObjectTable table = this.db_access.pullFromDB(session_token.getSessionId(), query);
        if (table.isEmpty()) {
            log.log_Debug("No reservations found for Customer \"", customer.name(), " ", customer.surname(), "\" [", customer.customerID(), "] in records.");
        }
        return table;
    }

    @Override
    public ObjectTable getReservedRooms(Token session_token, Reservation reservation) throws DbQueryException, SessionExpiredException, SessionInvalidException {
        String query = "SELECT " +
                "Room_has_Reservation.room_id, " +
                "Room_has_Reservation.floor_id, " +
                "Room_has_Reservation.building_id, " +
                "Room.description AS room_description, " +
                "Room_has_Reservation.timestamp_in, " +
                "Room_has_Reservation.timestamp_out, " +
                "Room_has_Reservation.seated_count, " +
                "Room_has_Reservation.catering, " +
                "Room_has_Reservation.notes, " +
                "Room_has_Reservation.cancelled_flag, " +
                "Room.description, " +
                "Room.room_category_id, " +
                "RoomPrice.id AS price_id, " +
                "RoomPrice.price, " +
                "RoomPrice.year AS price_year " +
                "FROM Room_has_Reservation " +
                "LEFT OUTER JOIN RoomPrice " +
                "ON RoomPrice.id = Room_has_Reservation.room_price_id " +
                "LEFT OUTER JOIN Room " +
                "ON Room_has_Reservation.room_id = Room.id" +
                " AND Room_has_Reservation.floor_id = Room.floor_id" +
                " AND Room_has_Reservation.building_id = Room.building_id " +
                "WHERE reservation_id = " + reservation.id();
        ObjectTable table = this.db_access.pullFromDB(session_token.getSessionId(), query);
        if (table.isEmpty()) {
            log.log_Debug("No rooms were found for Reservation [", reservation.id(), "] in records.");
        }
        return table;
    }

    @Override
    public ObjectTable getDiscount(Token session_token, Integer discount_id) throws InvalidDiscount, DbQueryException, SessionExpiredException, SessionInvalidException {
        String query = "SELECT "
                + "Discount.id, "
                + "Discount.discount_rate, "
                + "DiscountCategory.id AS category_id, "
                + "DiscountCategory.description AS category_description "
                + "FROM Discount "
                + "LEFT OUTER JOIN DiscountCategory "
                + "ON Discount.discount_category_id = DiscountCategory.id "
                + "WHERE Discount.id = " + discount_id;
        ObjectTable table = this.db_access.pullFromDB(session_token.getSessionId(), query);
        if (table.isEmpty()) {
            log.log_Error("Discount [", discount_id, "] does not exist in records.");
            throw new InvalidDiscount("Discount [" + discount_id + "] does not exist in records.");
        }
        return table;
    }

    @Override
    public ObjectTable getRoomCategory(Token session_token, Integer category_id) throws InvalidRoomCategory, DbQueryException, SessionExpiredException, SessionInvalidException {
        String query = "SELECT " +
                "RoomCategory.id, " +
                "RoomCategory.capacity, " +
                "RoomCategory.dimension " +
                "FROM RoomCategory " +
                "WHERE RoomCategory.id = " + category_id;
        ObjectTable table = this.db_access.pullFromDB(session_token.getSessionId(), query);
        if (table.isEmpty()) {
            log.log_Error("RoomCategory [", category_id, "] does not exist in records.");
            throw new InvalidRoomCategory("RoomCategory [" + category_id + "] does not exist in records.");
        }
        return table;
    }

    @Override
    public ObjectTable getRoomDetails(Token session_token, Room room) throws InvalidRoom, DbQueryException, SessionExpiredException, SessionInvalidException {
        String query = "SELECT " +
                "Building.id AS building_id, " +
                "Building.name AS building_name, " +
                "Building.address1 , " +
                "Building.address2 , " +
                "Building.city , " +
                "Building.postcode , " +
                "Building.country , " +
                "Building.telephone , " +
                "Floor.id AS floor_id, " +
                "Floor.description AS floor_description, " +
                "Room.id AS room_id, " +
                "Room.description AS room_description, " +
                "RoomCategory.id AS category_id, " +
                "RoomCategory.capacity, " +
                "RoomCategory.dimension, " +
                "RoomFixtures.id AS room_fixture_id, " +
                "RoomFixtures.fixed_chairs, " +
                "RoomFixtures.catering_space, " +
                "RoomFixtures.whiteboard, " +
                "RoomFixtures.projector " +
                "FROM Room " +
                "LEFT OUTER JOIN Floor " +
                "ON Room.floor_id = Floor.id" +
                " AND Room.building_id = Floor.building_id " +
                "LEFT OUTER JOIN Building " +
                "ON Floor.building_id = Building.id " +
                "LEFT OUTER JOIN RoomCategory " +
                "ON Room.room_category_id = RoomCategory.id " +
                "LEFT OUTER JOIN Room_has_RoomFixtures " +
                "ON Room.id = Room_has_RoomFixtures.room_id" +
                " AND Room.floor_id = Room_has_RoomFixtures.floor_id" +
                " AND Room.building_id = Room_has_RoomFixtures.building_id " +
                "LEFT OUTER JOIN RoomFixtures " +
                "ON Room_has_RoomFixtures.room_fixture_id = RoomFixtures.id " +
                "WHERE Room.id = " + room.id() +
                " AND Room.floor_id = " + room.floorID() +
                " AND Room.building_id = " + room.buildingID();
        ObjectTable table = this.db_access.pullFromDB(session_token.getSessionId(), query);
        if (table.isEmpty()) {
            log.log_Error("Room does not exists in records: ", room);
            throw new InvalidRoom("Room does not exists in records: " + room);
        }
        return table;
    }

    @Override
    public ObjectTable getRoomPrices(Token session_token, Room room) throws DbQueryException, SessionExpiredException, SessionInvalidException {
        String query = "SELECT " +
                "RoomPrice.id, " +
                "RoomPrice.price, " +
                "RoomPrice.year " +
                "FROM Room_has_RoomPrice " +
                "LEFT OUTER JOIN RoomPrice " +
                "ON Room_has_RoomPrice.price_id = RoomPrice.id " +
                "WHERE Room_has_RoomPrice.room_id = " + room.id() +
                " AND Room_has_RoomPrice.floor_id = " + room.floorID() +
                " AND Room_has_RoomPrice.building_id = " + room.buildingID();
        return this.db_access.pullFromDB(session_token.getSessionId(), query);
    }

    @Override
    public ObjectTable search(Token session_token, Room room, Date from, Date to) throws DbQueryException, SessionExpiredException, SessionInvalidException {
        String query = "SELECT " +
                "Room_has_Reservation.timestamp_in, " +
                "Room_has_Reservation.timestamp_out, " +
                "RoomFixtures.fixed_chairs, " +
                "RoomFixtures.catering_space, " +
                "RoomFixtures.whiteboard, " +
                "RoomFixtures.projector, " +
                "RoomCategory.capacity, " +
                "RoomCategory.dimension " +
                "FROM Room " +
                "INNER JOIN Room_has_Reservation " +
                "ON Room.id = Room_has_Reservation.room_id" +
                " AND Room.floor_id = Room_has_Reservation.floor_id" +
                " AND Room.building_id = Room_has_Reservation.building_id " +
                "INNER JOIN Room_has_RoomFixtures " +
                "ON Room.id = Room_has_RoomFixtures.room_id" +
                " AND Room.floor_id = Room_has_RoomFixtures.floor_id" +
                " AND Room.building_id = Room_has_RoomFixtures.building_id " +
                "INNER JOIN RoomFixtures " +
                "ON Room_has_RoomFixtures.room_fixture_id = RoomFixtures.id " +
                "INNER JOIN RoomCategory " +
                "ON Room.room_category_id = RoomCategory.id " +
                "WHERE Room_has_Reservation.timestamp_in < \"" + TimestampConverter.getUTCTimestampString(to) + "\"" +
                " AND Room_has_Reservation.timestamp_out > \"" + TimestampConverter.getUTCTimestampString(from) + "\"" +
                " AND Room.id = " + room.id() +
                " AND Room.floor_id = " + room.floorID() +
                " AND Room.building_id = " + room.buildingID() +
                " AND Room_has_Reservation.cancelled_flag = 0";
        return this.db_access.pullFromDB(session_token.getSessionId(), query);
    }

    @Override
    public ObjectTable search(Token session_token, Integer building_id, Integer floor_id, Date from, Date to) throws DbQueryException, SessionExpiredException, SessionInvalidException {
        String query = "SELECT " +
                "Room.building_id, " +
                "Room.floor_id, " +
                "Room.id AS room_id, " +
                "Room.description AS room_description, " +
                "Room_has_Reservation.timestamp_in, " +
                "Room_has_Reservation.timestamp_out, " +
                "RoomFixtures.fixed_chairs, " +
                "RoomFixtures.catering_space, " +
                "RoomFixtures.whiteboard, " +
                "RoomFixtures.projector, " +
                "RoomCategory.capacity, " +
                "RoomCategory.dimension " +
                "FROM Room " +
                "INNER JOIN Room_has_Reservation " +
                "ON Room.id = Room_has_Reservation.room_id" +
                " AND Room.floor_id = Room_has_Reservation.floor_id" +
                " AND Room.building_id = Room_has_Reservation.building_id " +
                "INNER JOIN Room_has_RoomFixtures " +
                "ON Room.id = Room_has_RoomFixtures.room_id" +
                " AND Room.floor_id = Room_has_RoomFixtures.floor_id" +
                " AND Room.building_id = Room_has_RoomFixtures.building_id " +
                "INNER JOIN RoomFixtures " +
                "ON Room_has_RoomFixtures.room_fixture_id = RoomFixtures.id " +
                "INNER JOIN RoomCategory " +
                "ON Room.room_category_id = RoomCategory.id " +
                "WHERE Room_has_Reservation.timestamp_in < \"" + TimestampConverter.getUTCTimestampString(to) + "\"" +
                " AND Room_has_Reservation.timestamp_out > \"" + TimestampConverter.getUTCTimestampString(from) + "\"" +
                " AND Room.floor_id = " + floor_id +
                " AND Room.building_id = " + building_id +
                " AND Room_has_Reservation.cancelled_flag = 0";
        return this.db_access.pullFromDB(session_token.getSessionId(), query);
    }

    @Override
    public ObjectTable search(Token session_token, Integer building_id, Date from, Date to) throws DbQueryException, SessionExpiredException, SessionInvalidException {
        String query = "SELECT " +
                "Room.building_id, " +
                "Room.floor_id, " +
                "Room.id AS room_id, " +
                "Room.description AS room_description, " +
                "Room_has_Reservation.timestamp_in, " +
                "Room_has_Reservation.timestamp_out, " +
                "RoomFixtures.fixed_chairs, " +
                "RoomFixtures.catering_space, " +
                "RoomFixtures.whiteboard, " +
                "RoomFixtures.projector, " +
                "RoomCategory.capacity, " +
                "RoomCategory.dimension " +
                "FROM Room " +
                "INNER JOIN Room_has_Reservation " +
                "ON Room.id = Room_has_Reservation.room_id" +
                " AND Room.floor_id = Room_has_Reservation.floor_id" +
                " AND Room.building_id = Room_has_Reservation.building_id " +
                "INNER JOIN Room_has_RoomFixtures " +
                "ON Room.id = Room_has_RoomFixtures.room_id" +
                " AND Room.floor_id = Room_has_RoomFixtures.floor_id" +
                " AND Room.building_id = Room_has_RoomFixtures.building_id " +
                "INNER JOIN RoomFixtures " +
                "ON Room_has_RoomFixtures.room_fixture_id = RoomFixtures.id " +
                "INNER JOIN RoomCategory " +
                "ON Room.room_category_id = RoomCategory.id " +
                "WHERE Room_has_Reservation.timestamp_in < \"" + TimestampConverter.getUTCTimestampString(to) + "\"" +
                " AND Room_has_Reservation.timestamp_out > \"" + TimestampConverter.getUTCTimestampString(from) + "\"" +
                " AND Room.building_id = " + building_id +
                " AND Room_has_Reservation.cancelled_flag = 0";
        return this.db_access.pullFromDB(session_token.getSessionId(), query);
    }

    @Override
    public ObjectTable search(Token session_token, Date from, Date to) throws DbQueryException, SessionExpiredException, SessionInvalidException {
        String query = "SELECT " +
                "Room.building_id, " +
                "Room.floor_id, " +
                "Room.id AS room_id, " +
                "Room.description AS room_description, " +
                "Room_has_Reservation.timestamp_in, " +
                "Room_has_Reservation.timestamp_out, " +
                "RoomFixtures.fixed_chairs, " +
                "RoomFixtures.catering_space, " +
                "RoomFixtures.whiteboard, " +
                "RoomFixtures.projector, " +
                "RoomCategory.capacity, " +
                "RoomCategory.dimension " +
                "FROM Room " +
                "INNER JOIN Room_has_Reservation " +
                "ON Room.id = Room_has_Reservation.room_id" +
                " AND Room.floor_id = Room_has_Reservation.floor_id" +
                " AND Room.building_id = Room_has_Reservation.building_id " +
                "INNER JOIN Room_has_RoomFixtures " +
                "ON Room.id = Room_has_RoomFixtures.room_id" +
                " AND Room.floor_id = Room_has_RoomFixtures.floor_id" +
                " AND Room.building_id = Room_has_RoomFixtures.building_id " +
                "INNER JOIN RoomFixtures " +
                "ON Room_has_RoomFixtures.room_fixture_id = RoomFixtures.id " +
                "INNER JOIN RoomCategory " +
                "ON Room.room_category_id = RoomCategory.id " +
                "WHERE Room_has_Reservation.timestamp_in < \"" + TimestampConverter.getUTCTimestampString(to) + "\"" +
                " AND Room_has_Reservation.timestamp_out > \"" + TimestampConverter.getUTCTimestampString(from) + "\"" +
                " AND Room_has_Reservation.cancelled_flag = 0";
        return this.db_access.pullFromDB(session_token.getSessionId(), query);
    }

    @Override
    public ObjectTable search(Token session_token, RoomProperty properties) throws DbQueryException, SessionExpiredException, SessionInvalidException {
        String filter = buildFilterQueryString(properties);
        String query = " SELECT " +
                "Room.building_id, " +
                "Room.floor_id, " +
                "Room.id AS room_id, " +
                "Room.description AS room_description, " +
                "Room.room_category_id, " +
                "RoomFixtures.fixed_chairs, " +
                "RoomFixtures.catering_space, " +
                "RoomFixtures.whiteboard, " +
                "RoomFixtures.projector, " +
                "RoomCategory.capacity, " +
                "RoomCategory.dimension " +
                "FROM Room " +
                "INNER JOIN Room_has_RoomFixtures " +
                "ON Room.id = Room_has_RoomFixtures.room_id" +
                " AND Room.floor_id = Room_has_RoomFixtures.floor_id" +
                " AND Room.building_id = Room_has_RoomFixtures.building_id " +
                "INNER JOIN RoomFixtures " +
                "ON Room_has_RoomFixtures.room_fixture_id = RoomFixtures.id " +
                "INNER JOIN RoomCategory " +
                "ON Room.room_category_id = RoomCategory.id " +
                (!filter.isEmpty() ? "WHERE " + filter : "");
        return this.db_access.pullFromDB(session_token.getSessionId(), query);
    }

    @Override
    public ObjectTable search(Token session_token, Integer building_id, RoomProperty properties) throws DbQueryException, SessionExpiredException, SessionInvalidException {
        String filter = buildFilterQueryString(properties);
        String query = " SELECT " +
                "Room.building_id, " +
                "Room.floor_id, " +
                "Room.id AS room_id, " +
                "Room.description AS room_description, " +
                "Room.room_category_id, " +
                "RoomFixtures.fixed_chairs, " +
                "RoomFixtures.catering_space, " +
                "RoomFixtures.whiteboard, " +
                "RoomFixtures.projector, " +
                "RoomCategory.capacity, " +
                "RoomCategory.dimension " +
                "FROM Room " +
                "INNER JOIN Room_has_RoomFixtures " +
                "ON Room.id = Room_has_RoomFixtures.room_id" +
                " AND Room.floor_id = Room_has_RoomFixtures.floor_id" +
                " AND Room.building_id = Room_has_RoomFixtures.building_id " +
                "INNER JOIN RoomFixtures " +
                "ON Room_has_RoomFixtures.room_fixture_id = RoomFixtures.id " +
                "INNER JOIN RoomCategory " +
                "ON Room.room_category_id = RoomCategory.id " +
                "WHERE Room.building_id = " + building_id +
                (!filter.isEmpty() ? " AND " + filter : "");
        return this.db_access.pullFromDB(session_token.getSessionId(), query);
    }

    @Override
    public ObjectTable search(Token session_token, Integer building_id, Integer floor_id, RoomProperty properties) throws DbQueryException, SessionExpiredException, SessionInvalidException {
        String filter = buildFilterQueryString(properties);
        String query = " SELECT " +
                "Room.building_id, " +
                "Room.floor_id, " +
                "Room.id AS room_id, " +
                "Room.description AS room_description, " +
                "Room.room_category_id, " +
                "RoomFixtures.fixed_chairs, " +
                "RoomFixtures.catering_space, " +
                "RoomFixtures.whiteboard, " +
                "RoomFixtures.projector, " +
                "RoomCategory.capacity, " +
                "RoomCategory.dimension " +
                "FROM Room " +
                "INNER JOIN Room_has_RoomFixtures " +
                "ON Room.id = Room_has_RoomFixtures.room_id" +
                " AND Room.floor_id = Room_has_RoomFixtures.floor_id" +
                " AND Room.building_id = Room_has_RoomFixtures.building_id " +
                "INNER JOIN RoomFixtures " +
                "ON Room_has_RoomFixtures.room_fixture_id = RoomFixtures.id " +
                "INNER JOIN RoomCategory " +
                "ON Room.room_category_id = RoomCategory.id " +
                "WHERE Room.building_id = " + building_id +
                " AND Room.floor_id = " + floor_id +
                (!filter.isEmpty() ? " AND " + filter : "");
        return this.db_access.pullFromDB(session_token.getSessionId(), query);
    }
}