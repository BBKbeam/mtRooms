package bbk_beam.mtRooms.reservation.delegate;

import bbk_beam.mtRooms.admin.authentication.Token;
import bbk_beam.mtRooms.db.IReservationDbAccess;
import bbk_beam.mtRooms.db.TimestampConverter;
import bbk_beam.mtRooms.db.exception.DbQueryException;
import bbk_beam.mtRooms.db.exception.SessionExpiredException;
import bbk_beam.mtRooms.db.exception.SessionInvalidException;
import bbk_beam.mtRooms.reservation.dto.Customer;
import bbk_beam.mtRooms.reservation.dto.PaymentType;
import bbk_beam.mtRooms.reservation.dto.Reservation;
import bbk_beam.mtRooms.reservation.exception.*;
import eadjlib.datastructure.ObjectTable;
import eadjlib.logger.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
            String query = "SELECT id FROM Customer WHERE "
                    + "membership_type_id = " + customer.membershipTypeID() + " AND "
                    + "customer_since = \"" + TimestampConverter.getUTCTimestampString(customer.accountCreationDate()) + "\" AND "
                    + "title = \"" + customer.title() + "\" AND "
                    + "name = \"" + customer.name() + "\" AND "
                    + "surname = \"" + customer.surname() + "\" AND "
                    + "address_1 = \"" + customer.address1() + "\" AND "
                    + "address_2 " + (customer.address2() == null ? "isnull" : "= \"" + customer.address2() + "\"") + " AND "
                    + "city = \"" + customer.city() + "\" AND "
                    + "county " + (customer.county() == null ? "isnull" : "= \"" + customer.county() + "\"") + " AND "
                    + "country = \"" + customer.country() + "\" AND "
                    + "postcode = \"" + customer.postCode() + "\" AND "
                    + "telephone_1 = \"" + customer.phone1() + "\" AND "
                    + "telephone_2 " + (customer.phone2() == null ? "isnull" : "= \"" + customer.phone2() + "\"") + " AND "
                    + "email = \"" + customer.email() + "\"";
            return this.db_access.pullFromDB(session_token.getSessionId(), query);
        } else {
            return getCustomerAccount(session_token, customer.customerID());
        }
    }

    @Override
    public ObjectTable findCustomer(Token session_token, String surname) throws DbQueryException, SessionExpiredException, SessionInvalidException {
        String query = "SELECT "
                + "id, name, surname "
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
        //Check if update was made
        ObjectTable table = this.db_access.pullFromDB(session_token.getSessionId(), "SELECT CHANGES()");
        if (table.getInteger(1, 1) == 0) {
            log.log_Error("Could add customer: ", customer);
            throw new FailedDbWrite("Could not add customer to record.");
        }
        this.db_access.pushToDB(session_token.getSessionId(), query);
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
    public Integer pay(Token session_token, Reservation reservation, Integer amount, Integer paymentID) throws InvalidReservation, InvalidPaymentType, DbQueryException, SessionExpiredException, SessionInvalidException {
        //TODO
        return null;
    }

    @Override
    public ObjectTable getPayments(Token session_token, Reservation reservation) throws DbQueryException, SessionExpiredException, SessionInvalidException {
        String query = "SELECT " +
                "Payment.id, " +
                "Payment.amount, " +
                "Payment.payment_method AS method_id, " +
                "PaymentMethod.description AS method_description, " +
                "Payment.timestamp " +
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
    public List<PaymentType> getPaymentTypes(Token session_token) throws DbQueryException, SessionExpiredException, SessionInvalidException {
        String query = "SELECT * FROM PaymentMethod";
        ObjectTable table = this.db_access.pullFromDB(session_token.getSessionId(), query);
        List<PaymentType> paymentTypes = new ArrayList<>();
        for (int i = 1; i <= table.rowCount(); i++) {
            HashMap<String, Object> row = table.getRow(i);
            paymentTypes.add(new PaymentType((Integer) row.get("id"), (String) row.get("description")));
        }
        return paymentTypes;
    }

    @Override
    public void createReservation(Token session_token, Reservation reservation) throws DbQueryException, SessionExpiredException, SessionInvalidException {
        //TODO
    }

    @Override
    public Integer cancelReservation(Token session_token, Reservation reservation) throws InvalidReservation, DbQueryException, SessionExpiredException, SessionInvalidException {
        //TODO
        return null;
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
                "ON Room_has_Reservation.room_id = Room.id " +
                "AND Room_has_Reservation.floor_id = Room.floor_id " +
                "AND Room_has_Reservation.building_id = Room.building_id " +
                "LEFT OUTER JOIN Room_has_RoomPrice " +
                "ON Room_has_Reservation.room_id = Room_has_RoomPrice.room_id " +
                "AND Room_has_Reservation.floor_id = Room_has_RoomPrice.floor_id " +
                "AND Room_has_Reservation.building_id = Room_has_RoomPrice.building_id " +
                "INNER JOIN RoomPrice " +
                "ON Room_has_RoomPrice.price_id = RoomPrice.id AND RoomPrice.year " +
                "IN ( " +
                "SELECT MAX( RoomPrice.year) " +
                "FROM RoomPrice " +
                "WHERE RoomPrice.year <= strftime(\"%Y\", Room_has_Reservation.timestamp_in) " +
                ") " +
                "WHERE reservation_id = 1";
        ObjectTable table = null;
        table = this.db_access.pullFromDB(session_token.getSessionId(), query);
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