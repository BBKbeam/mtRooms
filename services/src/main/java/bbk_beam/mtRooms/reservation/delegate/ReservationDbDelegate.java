package bbk_beam.mtRooms.reservation.delegate;

import bbk_beam.mtRooms.admin.authentication.Token;
import bbk_beam.mtRooms.db.IReservationDbAccess;
import bbk_beam.mtRooms.db.TimestampConverter;
import bbk_beam.mtRooms.db.exception.DbQueryException;
import bbk_beam.mtRooms.db.exception.SessionExpiredException;
import bbk_beam.mtRooms.db.exception.SessionInvalidException;
import bbk_beam.mtRooms.reservation.dto.Customer;
import bbk_beam.mtRooms.reservation.dto.Payment;
import bbk_beam.mtRooms.reservation.dto.Reservation;
import bbk_beam.mtRooms.reservation.dto.RoomReservation;
import bbk_beam.mtRooms.reservation.exception.FailedDbWrite;
import bbk_beam.mtRooms.reservation.exception.InvalidDiscount;
import bbk_beam.mtRooms.reservation.exception.InvalidReservation;
import bbk_beam.mtRooms.reservation.exception.InvalidRoomCategory;
import eadjlib.datastructure.ObjectTable;
import eadjlib.logger.Logger;

import java.util.HashMap;

public class ReservationDbDelegate implements ICustomerAccount, IPay, IReserve, ISearch {
    private final Logger log = Logger.getLoggerInstance(ReservationDbDelegate.class.getName());
    private IReservationDbAccess db_access;

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
                + "WHERE surname = \"" + surname + "\"";
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
    public void pay(Token session_token, Reservation reservation, Payment payment) throws DbQueryException, SessionExpiredException, SessionInvalidException {
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
            throw new DbQueryException("Could not get the ID for the added Payment (hash_id=" + payment.hashID() + ") for Reservation [" + reservation.id() + "] in records.");
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
                "( room_id, floor_id, building_id, reservation_id, timestamp_in, timestamp_out, notes ) " +
                "VALUES ( " +
                room_reservation.room().id() + ", " +
                room_reservation.room().floorID() + ", " +
                room_reservation.room().buildingID() + ", " +
                reservation_id + ", " +
                "\"" + TimestampConverter.getUTCTimestampString(room_reservation.reservationStart()) + "\", " +
                "\"" + TimestampConverter.getUTCTimestampString(room_reservation.reservationEnd()) + "\", " +
                (room_reservation.note().isEmpty() ? null : "\"" + room_reservation.note() + "\" ") +
                ")";
        if (!this.db_access.pushToDB(session_token.getSessionId(), query)) {
            log.log_Error("Could not add RoomReservation to records: ", room_reservation);
            throw new DbQueryException("Could not add RoomReservation to records: " + room_reservation);
        }
    }

    @Override
    public Integer cancelReservation(Token session_token, Reservation reservation) throws DbQueryException, SessionExpiredException, SessionInvalidException {
        String query1 = "UPDATE " +
                "Room_has_Reservation " +
                "SET cancelled_flag = 1 " +
                "WHERE reservation_id = " + reservation.id();
        if (!this.db_access.pushToDB(session_token.getSessionId(), query1)) {
            log.log_Error("Could not cancel ReservedRooms in Reservation [", reservation.id(), "].");
            throw new DbQueryException("Could not cancel ReservedRooms in Reservation [" + reservation.id() + "].");
        }
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
        return table.getInteger(1, 1);
    }

    @Override
    public Integer cancelReservedRoom(Token session_token, Integer reservation_id, RoomReservation reserved_room) throws DbQueryException, SessionExpiredException, SessionInvalidException {
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
        String query2 = "SELECT " +
                "RoomPrice.price AS room_price " +
                "FROM Room_has_Reservation " +
                "LEFT OUTER JOIN Room " +
                "ON Room_has_Reservation.room_id = Room.id" +
                " AND Room_has_Reservation.floor_id = Room.floor_id" +
                " AND Room_has_Reservation.building_id = Room.building_id " +
                "LEFT OUTER JOIN Room_has_RoomPrice " +
                "ON Room_has_Reservation.room_id = Room_has_RoomPrice.room_id" +
                " AND Room_has_Reservation.floor_id = Room_has_RoomPrice.floor_id" +
                " AND Room_has_Reservation.building_id = Room_has_RoomPrice.building_id " +
                "INNER JOIN RoomPrice " +
                "ON Room_has_RoomPrice.price_id = RoomPrice.id AND RoomPrice.year " +
                "IN (" +
                " SELECT MAX( RoomPrice.year)" +
                " FROM RoomPrice" +
                " WHERE RoomPrice.year <= strftime(\"%Y\", Room_has_Reservation.timestamp_in) " +
                ") " +
                "WHERE reservation_id = " + reservation_id +
                " AND Room.id = " + reserved_room.room().id() +
                " AND Room.floor_id = " + reserved_room.room().floorID() +
                " AND Room.building_id = " + reserved_room.room().buildingID();
        ObjectTable table = this.db_access.pullFromDB(session_token.getSessionId(), query2);
        if (table.isEmpty()) {
            log.log_Error("Could not get cost on RoomReservation:  ", reserved_room);
            throw new DbQueryException("Could not get cost on RoomReservation:  " + reserved_room);
        }
        return table.getInteger(1, 1);
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
                "Room_has_Reservation.timestamp_in, " +
                "Room_has_Reservation.timestamp_out, " +
                "Room_has_Reservation.notes, " +
                "Room_has_Reservation.cancelled_flag, " +
                "Room.description, " +
                "Room.room_category_id, " +
                "RoomPrice.id AS price_id, " +
                "RoomPrice.price, " +
                "RoomPrice.year AS price_year " +
                "FROM Room_has_Reservation " +
                "LEFT OUTER JOIN Room " +
                "ON Room_has_Reservation.room_id = Room.id" +
                " AND Room_has_Reservation.floor_id = Room.floor_id" +
                " AND Room_has_Reservation.building_id = Room.building_id " +
                "LEFT OUTER JOIN Room_has_RoomPrice " +
                "ON Room_has_Reservation.room_id = Room_has_RoomPrice.room_id" +
                " AND Room_has_Reservation.floor_id = Room_has_RoomPrice.floor_id" +
                " AND Room_has_Reservation.building_id = Room_has_RoomPrice.building_id " +
                "INNER JOIN RoomPrice " +
                "ON Room_has_RoomPrice.price_id = RoomPrice.id AND RoomPrice.year " +
                "IN ( " +
                " SELECT MAX( RoomPrice.year)" +
                " FROM RoomPrice" +
                " WHERE RoomPrice.year <= strftime(\"%Y\", Room_has_Reservation.timestamp_in)" +
                ") " +
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
}