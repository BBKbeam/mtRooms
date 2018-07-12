package bbk_beam.mtRooms.revenue;


import bbk_beam.mtRooms.admin.authentication.Token;
import bbk_beam.mtRooms.db.IReservationDbAccess;
import bbk_beam.mtRooms.db.TimestampConverter;
import bbk_beam.mtRooms.db.exception.DbQueryException;
import bbk_beam.mtRooms.db.exception.SessionExpiredException;
import bbk_beam.mtRooms.db.exception.SessionInvalidException;
import bbk_beam.mtRooms.reservation.dto.Building;
import bbk_beam.mtRooms.reservation.dto.Customer;
import bbk_beam.mtRooms.reservation.dto.Floor;
import bbk_beam.mtRooms.reservation.dto.Room;
import eadjlib.datastructure.ObjectTable;
import eadjlib.logger.Logger;

import java.util.Date;

/**
 * RevenueAggregator
 * <p>
 * Acts as a DB delegate for getting the appropriate data
 * required from the records in order to generate invoices and revenue reports
 * </p>
 */
public class RevenueAggregator {
    private final Logger log = Logger.getLoggerInstance(RevenueAggregator.class.getName());
    private IReservationDbAccess db_access;

    /**
     * Constructor
     *
     * @param db_access IReservationDbAccess instance
     */
    public RevenueAggregator(IReservationDbAccess db_access) {
        this.db_access = db_access;
    }

    /**
     * Gets all buildings in real estate portfolio
     *
     * @param session_token Session token
     * @return ObjectTable of the building records
     * @throws DbQueryException        when query failed
     * @throws SessionExpiredException when current administrator session has expired
     * @throws SessionInvalidException when administrator session is not valid
     */
    ObjectTable getBuildings(Token session_token) throws DbQueryException, SessionExpiredException, SessionInvalidException {
        String query = "SELECT * FROM Building";
        return this.db_access.pullFromDB(session_token.getSessionId(), query);
    }

    /**
     * Gets all floors in a building
     *
     * @param session_token Session token
     * @param building      Building DTO
     * @return ObjectTable of the floor records for the building
     * @throws DbQueryException        when query failed
     * @throws SessionExpiredException when current administrator session has expired
     * @throws SessionInvalidException when administrator session is not valid
     */
    ObjectTable getFloors(Token session_token, Building building) throws DbQueryException, SessionExpiredException, SessionInvalidException {
        String query = "SELECT * FROM Floor WHERE building_id = " + building.id();
        return this.db_access.pullFromDB(session_token.getSessionId(), query);
    }

    /**
     * Gets all rooms in a floor
     *
     * @param session_token Session token
     * @param floor         Floor DTO
     * @return ObjectTable of the room records on a floor
     * @throws DbQueryException        when query failed
     * @throws SessionExpiredException when current administrator session has expired
     * @throws SessionInvalidException when administrator session is not valid
     */
    ObjectTable getRooms(Token session_token, Floor floor) throws DbQueryException, SessionExpiredException, SessionInvalidException {
        String query = "SELECT * FROM Room " +
                "WHERE building_id = " + floor.buildingID() +
                " AND floor_id = " + floor.floorID();
        return this.db_access.pullFromDB(session_token.getSessionId(), query);
    }

    /**
     * Gets customer information
     *
     * @param session_token Session token
     * @param customer_id   Customer ID
     * @return ObjectTable {}
     * @throws DbQueryException        when query failed
     * @throws SessionExpiredException when current administrator session has expired
     * @throws SessionInvalidException when administrator session is not valid
     */
    ObjectTable getCustomerAccount(Token session_token, Integer customer_id) throws DbQueryException, SessionExpiredException, SessionInvalidException {
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
                + "FROM Customer WHERE id = " + customer_id;
        return this.db_access.pullFromDB(session_token.getSessionId(), query);
    }


    /**
     * Gets all reservation IDs linked to a customer
     *
     * @param session_token Session token
     * @param customer      Customer DTO
     * @return ObjectTable {id}
     * @throws DbQueryException        when query failed
     * @throws SessionExpiredException when current administrator session has expired
     * @throws SessionInvalidException when administrator session is not valid
     */
    ObjectTable getCustomerReservationIDs(Token session_token, Customer customer) throws DbQueryException, SessionExpiredException, SessionInvalidException {
        String query = "SELECT id FROM Reservation WHERE Reservation.customer_id = " + customer.customerID();
        return this.db_access.pullFromDB(session_token.getSessionId(), query);
    }

    /**
     * Gets all IDs of reservations made in a time frame
     *
     * @param session_token Session token
     * @param from          Start of date range
     * @param to            End of date range
     * @return ObjectTable { id }
     * @throws DbQueryException        when query failed
     * @throws SessionExpiredException when current administrator session has expired
     * @throws SessionInvalidException when administrator session is not valid
     */
    ObjectTable getReservationIDs(Token session_token, Date from, Date to) throws DbQueryException, SessionExpiredException, SessionInvalidException {
        String query = "SELECT id " +
                "FROM Reservation " +
                "WHERE created_timestamp >= \"" + TimestampConverter.getUTCTimestampString(from) + "\"" +
                " AND created_timestamp <= \"" + TimestampConverter.getUTCTimestampString(to) + "\" " +
                "ORDER BY id ASC";
        return this.db_access.pullFromDB(session_token.getSessionId(), query);
    }

    /**
     * Gets the price and reservation time for all rooms in a reservation
     *
     * @param session_token  Session token
     * @param reservation_id Reservation ID
     * @return ObjectTable
     * {building_id, floor_id, room_id, timestamp_in, timestamp_out, room_price, cancelled_flag}
     * @throws DbQueryException        when a problem was encountered whilst processing the query
     * @throws SessionExpiredException when the session for the id provided has expired
     * @throws SessionInvalidException when the session for the id provided does not exist in the tracker
     */
    ObjectTable getItemisedReservation(Token session_token, Integer reservation_id) throws DbQueryException, SessionExpiredException, SessionInvalidException {
        String query = "SELECT " +
                "Room_has_Reservation.building_id, " +
                "Room_has_Reservation.floor_id, " +
                "Room_has_Reservation.room_id, " +
                "Room_has_Reservation.timestamp_in, " +
                "Room_has_Reservation.timestamp_out, " +
                "Room_has_Reservation.cancelled_flag, " +
                "RoomPrice.price AS room_price " +
                "FROM Room_has_Reservation " +
                "LEFT OUTER JOIN RoomPrice" +
                " ON RoomPrice.id = Room_has_Reservation.room_price_id " +
                "WHERE Room_has_Reservation.reservation_id = " + reservation_id;
        return this.db_access.pullFromDB(session_token.getSessionId(), query);
    }

    /**
     * Gets the discount associated with a reservation
     *
     * @param session_token  Session token
     * @param reservation_id Reservation ID
     * @return ObjectTable
     * {discount_id, discount_rate, discount_category_id, category_description}
     * @throws DbQueryException        when a problem was encountered whilst processing the query
     * @throws SessionExpiredException when the session for the id provided has expired
     * @throws SessionInvalidException when the session for the id provided does not exist in the tracker
     */
    ObjectTable getReservationDiscount(Token session_token, Integer reservation_id) throws DbQueryException, SessionExpiredException, SessionInvalidException {
        String query = "SELECT " +
                "Discount.id AS discount_id, " +
                "Discount.discount_rate AS discount_rate, " +
                "Discount.discount_category_id AS discount_category_id, " +
                "DiscountCategory.description AS category_description " +
                "FROM Reservation " +
                "LEFT OUTER JOIN Discount" +
                " ON Discount.id = Reservation.discount_id " +
                "LEFT OUTER JOIN DiscountCategory" +
                " ON DiscountCategory.id = Discount.discount_category_id " +
                "WHERE Reservation.id = " + reservation_id;
        return this.db_access.pullFromDB(session_token.getSessionId(), query);
    }

    /**
     * Gets all Payment Data
     *
     * @param session_token Session token
     * @param from          Start of the date range for payments
     * @param to            End of the date range for payments
     * @return ObjectTable of payment data
     * {customer_id, reservation_id, payment_id, hash_id, timestamp, amount, note, method_id, method_description}
     * @throws DbQueryException        when a problem was encountered whilst processing the query
     * @throws SessionExpiredException when the session for the id provided has expired
     * @throws SessionInvalidException when the session for the id provided does not exist in the tracker
     */
    ObjectTable getPayments(Token session_token, Date from, Date to) throws DbQueryException, SessionExpiredException, SessionInvalidException {
        String query = "SELECT " +
                "Reservation.customer_id AS customer_id, " +
                "Reservation.id AS reservation_id, " +
                "Payment.id AS payment_id, " +
                "Payment.hash_id, " +
                "Payment.timestamp, " +
                "Payment.amount, " +
                "Payment.note, " +
                "PaymentMethod.id AS method_id, " +
                "PaymentMethod.description AS method_description " +
                "FROM Payment " +
                "LEFT OUTER JOIN PaymentMethod" +
                " ON PaymentMethod.id = Payment.payment_method " +
                "LEFT OUTER JOIN Reservation_has_Payment" +
                " ON Reservation_has_Payment.payment_id = Payment.id " +
                "LEFT OUTER JOIN Reservation" +
                " ON Reservation.id = Reservation_has_Payment.reservation_id " +
                "WHERE Payment.timestamp >= \"" + TimestampConverter.getUTCTimestampString(from) + "\"" +
                " AND Payment.timestamp <= \"" + TimestampConverter.getUTCTimestampString(to) + "\" " +
                "ORDER BY customer_id, reservation_id, timestamp ASC";
        return this.db_access.pullFromDB(session_token.getSessionId(), query);
    }

    /**
     * Gets all Payment data for a reservation
     *
     * @param session_token  Session token
     * @param reservation_id Reservation ID
     * @return ObjectTable of payment data
     * {payment_id, hash_id, timestamp, amount, note, method_id, method_description}
     * @throws DbQueryException        when a problem was encountered whilst processing the query
     * @throws SessionExpiredException when the session for the id provided has expired
     * @throws SessionInvalidException when the session for the id provided does not exist in the tracker
     */
    ObjectTable getPayments(Token session_token, Integer reservation_id) throws DbQueryException, SessionExpiredException, SessionInvalidException {
        String query = "SELECT " +
                "Payment.id AS payment_id, " +
                "Payment.hash_id, " +
                "Payment.timestamp, " +
                "Payment.amount, " +
                "Payment.note, " +
                "PaymentMethod.id AS method_id, " +
                "PaymentMethod.description AS method_description " +
                "FROM Payment " +
                "LEFT OUTER JOIN PaymentMethod" +
                " ON PaymentMethod.id = Payment.payment_method " +
                "LEFT OUTER JOIN Reservation_has_Payment" +
                " ON Reservation_has_Payment.payment_id = Payment.id " +
                "LEFT OUTER JOIN Reservation" +
                " ON Reservation.id = Reservation_has_Payment.reservation_id " +
                "WHERE Reservation.id = " + reservation_id;
        return this.db_access.pullFromDB(session_token.getSessionId(), query);
    }

    /**
     * Gets all payment data for a customer
     *
     * @param session_token Session token
     * @param customer      Customer DTO
     * @return ObjectTable of payment data
     * {payment_id, hash_id, timestamp, amount, note, method_id, method_description}
     * @throws DbQueryException        when a problem was encountered whilst processing the query
     * @throws SessionExpiredException when the session for the id provided has expired
     * @throws SessionInvalidException when the session for the id provided does not exist in the tracker
     */
    ObjectTable getPayments(Token session_token, Customer customer) throws DbQueryException, SessionExpiredException, SessionInvalidException {
        String query = "SELECT " +
                "Payment.id AS payment_id, " +
                "Payment.hash_id, " +
                "Payment.timestamp, " +
                "Payment.amount, " +
                "Payment.note, " +
                "PaymentMethod.id AS method_id, " +
                "PaymentMethod.description AS method_description " +
                "FROM Payment " +
                "LEFT OUTER JOIN PaymentMethod" +
                " ON PaymentMethod.id = Payment.payment_method " +
                "LEFT OUTER JOIN Reservation_has_Payment" +
                " ON Reservation_has_Payment.payment_id = Payment.id " +
                "LEFT OUTER JOIN Reservation" +
                " ON Reservation.id = Reservation_has_Payment.reservation_id " +
                "WHERE Reservation.customer_id = " + customer.customerID();
        return this.db_access.pullFromDB(session_token.getSessionId(), query);
    }

    /**
     * Gets all payment data for a customer
     *
     * @param session_token Session token
     * @param customer      Customer DTO
     * @param from          Start of the date range for payments
     * @param to            End of the date range for payments
     * @return ObjectTable of payment data
     * {payment_id, hash_id, timestamp, amount, note, method_id, method_description}
     * @throws DbQueryException        when a problem was encountered whilst processing the query
     * @throws SessionExpiredException when the session for the id provided has expired
     * @throws SessionInvalidException when the session for the id provided does not exist in the tracker
     */
    ObjectTable getPayments(Token session_token, Customer customer, Date from, Date to) throws DbQueryException, SessionExpiredException, SessionInvalidException {
        String query = "SELECT " +
                "Payment.id AS payment_id, " +
                "Payment.hash_id, " +
                "Payment.timestamp, " +
                "Payment.amount, " +
                "Payment.note, " +
                "PaymentMethod.id AS method_id, " +
                "PaymentMethod.description AS method_description " +
                "FROM Payment " +
                "LEFT OUTER JOIN PaymentMethod" +
                " ON PaymentMethod.id = Payment.payment_method " +
                "LEFT OUTER JOIN Reservation_has_Payment" +
                " ON Reservation_has_Payment.payment_id = Payment.id " +
                "LEFT OUTER JOIN Reservation" +
                " ON Reservation.id = Reservation_has_Payment.reservation_id " +
                "WHERE Reservation.customer_id = " + customer.customerID() +
                " AND Payment.timestamp >= \"" + TimestampConverter.getUTCTimestampString(from) + "\"" +
                " AND Payment.timestamp <= \"" + TimestampConverter.getUTCTimestampString(to) + "\" ";
        return this.db_access.pullFromDB(session_token.getSessionId(), query);
    }

    /**
     * Gets credit balance on all customers
     *
     * @param session_token Session token
     * @return ObjectTable of customers and their respective balances
     * {customer_id, reservation_count, total_cost, total_paid, final_balance}
     * @throws DbQueryException        when a problem was encountered whilst processing the query
     * @throws SessionExpiredException when the session for the id provided has expired
     * @throws SessionInvalidException when the session for the id provided does not exist in the tracker
     */
    ObjectTable getBalance(Token session_token) throws DbQueryException, SessionExpiredException, SessionInvalidException {
        /*===================
         *Sub-Query structure
         *===================
         * customer_query LEFT JOIN {               //Number of Reservations made, total cost, total paid, final balance
         *      reservation_query LEFT JOIN {       //Discount rates for reservation, calculation for discounted total and reservation balance
         *          payment_query,                  //Total of payments made grouped by reservation ID
         *          room_totals_query LEFT JOIN {   //Total time(mns) booked and total room prices grouped by reservation ID
         *              cost_query                  //Cost of each room booking time(mns)/60 * price of room
         *          }
         *      }
         * }
         */
        String payment_query = "SELECT " +
                "Reservation_has_Payment.reservation_id AS reservation_id, " +
                "SUM( IFNULL(Payment.amount, 0.) ) AS total " +
                "FROM Reservation_has_Payment " +
                "LEFT OUTER JOIN Payment" +
                " ON Payment.id = Reservation_has_Payment.payment_id " +
                "GROUP BY Reservation_has_Payment.reservation_id";
        String cost_query = "SELECT " +
                "Room_has_Reservation.reservation_id, " +
                "Room_has_Reservation.building_id, " +
                "Room_has_Reservation.floor_id, " +
                "Room_has_Reservation.room_id, " +
                "Room_has_Reservation.timestamp_in, " +
                "Room_has_Reservation.timestamp_out, " +
                "( strftime( '%s', timestamp_out) - strftime( '%s', timestamp_in) ) / 60 AS range_mns, " +
                "RoomPrice.price AS price, " +
                "( ( strftime( '%s', timestamp_out) - strftime( '%s', timestamp_in) ) / 60 ) / 60. AS unit, " +
                "( RoomPrice.price * ( ( strftime( '%s', timestamp_out) - strftime( '%s', timestamp_in) ) / 60 ) / 60. ) AS total_price " +
                "FROM Room_has_Reservation " +
                "LEFT OUTER JOIN RoomPrice" +
                " ON RoomPrice.id = Room_has_Reservation.room_price_id " +
                "WHERE Room_has_Reservation.cancelled_flag = 0";
        String room_totals_query = "SELECT " +
                "Room_has_Reservation.reservation_id, " +
                "COUNT( * ) AS booking_count, " +
                "SUM( IFNULL( RoomCost.range_mns, 0 ) ) AS total_mns, " +
                "SUM( IFNULL( RoomCost.total_price, 0. ) ) AS total_price " +
                "FROM Room_has_Reservation " +
                "LEFT JOIN ( " + cost_query + " ) RoomCost" +
                " ON Room_has_Reservation.reservation_id = RoomCost.reservation_id" +
                " AND Room_has_Reservation.building_id = RoomCost.building_id" +
                " AND Room_has_Reservation.floor_id = RoomCost.floor_id" +
                " AND Room_has_Reservation.room_id = RoomCost.room_id " +
                " AND Room_has_Reservation.timestamp_in = RoomCost.timestamp_in " +
                " AND Room_has_Reservation.timestamp_out = RoomCost.timestamp_out " +
                "WHERE Room_has_Reservation.cancelled_flag = 0 " +
                "GROUP BY Room_has_Reservation.reservation_id";
        String reservation_query = "SELECT " +
                "Reservation.id AS reservation_id, " +
                "Reservation.customer_id, " +
                "IFNULL( RoomTotals.total_price, 0. ) AS price," +
                "Discount.discount_rate, " +
                "( IFNULL( RoomTotals.total_price, 0. ) * ( 100 - Discount.discount_rate ) / 100 ) AS discounted_price, " +
                "IFNULL( ReservationSubTotals.total, 0. ) AS paid, " +
                "( IFNULL( ReservationSubTotals.total, 0. ) - IFNULL( RoomTotals.total_price, 0 ) * ( 100 - Discount.discount_rate ) / 100 ) AS balance " +
                "FROM Reservation " +
                "LEFT OUTER JOIN Discount" +
                " ON Discount.id = Reservation.discount_id " +
                "LEFT JOIN ( " + payment_query + " ) ReservationSubTotals" +
                " ON ReservationSubTotals.reservation_id = Reservation.id " +
                "LEFT JOIN ( " + room_totals_query + " ) RoomTotals" +
                " ON RoomTotals.reservation_id = Reservation.id ";
        String customer_query = "SELECT " +
                "Customer.id AS customer_id, " +
                "COUNT( ReservationSummary.reservation_id ) AS reservation_count, " +
                "SUM( ReservationSummary.discounted_price ) AS total_cost, " +
                "SUM( ReservationSummary.paid ) AS total_paid, " +
                "SUM( ReservationSummary.balance ) AS final_balance " +
                "FROM Customer LEFT JOIN ( " + reservation_query + " ) ReservationSummary" +
                " ON ReservationSummary.customer_id = Customer.id " +
                "GROUP BY Customer.id";
        return this.db_access.pullFromDB(session_token.getSessionId(), customer_query);
    }

    /**
     * Gets credit balance for Reservations of a Customer
     *
     * @param session_token Session token
     * @param customer      Customer DTO
     * @return ObjectTable of reservation cost/discount/payments
     * {reservation_id, reservation_timestamp, discount_id, discount_rate, discount_category_id, room_count, room_subtotal, payment_subtotal}
     * @throws DbQueryException        when a problem was encountered whilst processing the query
     * @throws SessionExpiredException when the session for the id provided has expired
     * @throws SessionInvalidException when the session for the id provided does not exist in the tracker
     */
    ObjectTable getBalance(Token session_token, Customer customer) throws DbQueryException, SessionExpiredException, SessionInvalidException {
        /*===================
         *Sub-Query structure
         *===================
         * customer_query LEFT JOIN {               //Number of Reservations made, total cost, total paid, final balance for given customer ID
         *      reservation_query LEFT JOIN {       //Discount rates for reservation, calculation for discounted total and reservation balance
         *          payment_query,                  //Total of payments made grouped by reservation ID
         *          room_totals_query LEFT JOIN {   //Total time(mns) booked and total room prices grouped by reservation ID
         *              cost_query                  //Cost of each room booking time(mns)/60 * price of room
         *          }
         *      }
         * }
         */
        String payment_query = "SELECT " +
                "Reservation_has_Payment.reservation_id AS reservation_id, " +
                "SUM( IFNULL(Payment.amount, 0.) ) AS total " +
                "FROM Reservation_has_Payment " +
                "LEFT OUTER JOIN Payment" +
                " ON Payment.id = Reservation_has_Payment.payment_id " +
                "GROUP BY Reservation_has_Payment.reservation_id";
        String cost_query = "SELECT " +
                "Room_has_Reservation.reservation_id, " +
                "Room_has_Reservation.building_id, " +
                "Room_has_Reservation.floor_id, " +
                "Room_has_Reservation.room_id, " +
                "Room_has_Reservation.timestamp_in, " +
                "Room_has_Reservation.timestamp_out, " +
                "( strftime( '%s', timestamp_out) - strftime( '%s', timestamp_in) ) / 60 AS range_mns, " +
                "RoomPrice.price AS price, " +
                "( ( strftime( '%s', timestamp_out) - strftime( '%s', timestamp_in) ) / 60 ) / 60. AS unit, " +
                "( RoomPrice.price * ( ( strftime( '%s', timestamp_out) - strftime( '%s', timestamp_in) ) / 60 ) / 60. ) AS total_price " +
                "FROM Room_has_Reservation " +
                "LEFT OUTER JOIN RoomPrice" +
                " ON RoomPrice.id = Room_has_Reservation.room_price_id " +
                "WHERE Room_has_Reservation.cancelled_flag = 0";
        String room_totals_query = "SELECT " +
                "Room_has_Reservation.reservation_id, " +
                "COUNT( * ) AS booking_count, " +
                "SUM( IFNULL( RoomCost.range_mns, 0 ) ) AS total_mns, " +
                "SUM( IFNULL( RoomCost.total_price, 0. ) ) AS total_price " +
                "FROM Room_has_Reservation " +
                "LEFT JOIN ( " + cost_query + " ) RoomCost" +
                " ON Room_has_Reservation.reservation_id = RoomCost.reservation_id" +
                " AND Room_has_Reservation.building_id = RoomCost.building_id" +
                " AND Room_has_Reservation.floor_id = RoomCost.floor_id" +
                " AND Room_has_Reservation.room_id = RoomCost.room_id " +
                " AND Room_has_Reservation.timestamp_in = RoomCost.timestamp_in " +
                " AND Room_has_Reservation.timestamp_out = RoomCost.timestamp_out " +
                "WHERE Room_has_Reservation.cancelled_flag = 0 " +
                "GROUP BY Room_has_Reservation.reservation_id";
        String reservation_query = "SELECT " +
                "Reservation.id AS reservation_id, " +
                "Reservation.customer_id, " +
                "IFNULL( RoomTotals.total_price, 0. ) AS price," +
                "Discount.discount_rate, " +
                "( IFNULL( RoomTotals.total_price, 0. ) * ( 100 - Discount.discount_rate ) / 100 ) AS discounted_price, " +
                "IFNULL( ReservationSubTotals.total, 0. ) AS paid, " +
                "( IFNULL( ReservationSubTotals.total, 0. ) - IFNULL( RoomTotals.total_price, 0 ) * ( 100 - Discount.discount_rate ) / 100 ) AS balance " +
                "FROM Reservation " +
                "LEFT OUTER JOIN Discount" +
                " ON Discount.id = Reservation.discount_id " +
                "LEFT JOIN ( " + payment_query + " ) ReservationSubTotals" +
                " ON ReservationSubTotals.reservation_id = Reservation.id " +
                "LEFT JOIN ( " + room_totals_query + " ) RoomTotals" +
                " ON RoomTotals.reservation_id = Reservation.id ";
        String customer_query = "SELECT " +
                "Customer.id AS customer_id, " +
                "COUNT( ReservationSummary.reservation_id ) AS reservation_count, " +
                "SUM( ReservationSummary.discounted_price ) AS total_cost, " +
                "SUM( ReservationSummary.paid ) AS total_paid, " +
                "SUM( ReservationSummary.balance ) AS final_balance " +
                "FROM Customer LEFT JOIN ( " + reservation_query + " ) ReservationSummary" +
                " ON ReservationSummary.customer_id = Customer.id " +
                "WHERE Customer.id = " + customer.customerID() + " " +
                "GROUP BY Customer.id";
        return this.db_access.pullFromDB(session_token.getSessionId(), customer_query);
    }

    /**
     * Gets day-to-day weekday booking information
     *
     * @param session_token Session token
     * @param from          Start of date range
     * @param to            End of date range
     * @return ObjectTable
     * {building_id, floor_id, room_id, room_category_id, room_description, timestamp_in, timestamp_out}
     * @throws DbQueryException        when a problem was encountered whilst processing the query
     * @throws SessionExpiredException when the session for the id provided has expired
     * @throws SessionInvalidException when the session for the id provided does not exist in the tracker
     */
    ObjectTable getReservationScheduleData(Token session_token, Date from, Date to) throws DbQueryException, SessionExpiredException, SessionInvalidException {
        String query = "SELECT " +
                "Room_has_Reservation.building_id, " +
                "Room_has_Reservation.floor_id, " +
                "Room_has_Reservation.room_id, " +
                "Room.room_category_id AS room_category_id, " +
                "Room.description AS room_description, " +
                "Room_has_Reservation.timestamp_in, " +
                "Room_has_Reservation.timestamp_out " +
                "FROM Room_has_Reservation " +
                "LEFT OUTER JOIN Room" +
                " ON Room.id = Room_has_Reservation.room_id" +
                " AND Room.floor_id = Room_has_Reservation.floor_id" +
                " AND Room.building_id = Room_has_Reservation.building_id " +
                "WHERE cancelled_flag = 0" +
                " AND Room_has_Reservation.timestamp_in >= \"" + TimestampConverter.getUTCTimestampString(from) + "\"" +
                " AND Room_has_Reservation.timestamp_in <= \"" + TimestampConverter.getUTCTimestampString(to) + "\" " +
                "ORDER BY" +
                " Room_has_Reservation.building_id," +
                " Room_has_Reservation.floor_id," +
                " Room_has_Reservation.room_id," +
                " strftime( '%w', Room_has_Reservation.timestamp_in )," +
                " time( Room_has_Reservation.timestamp_in )," +
                " time( Room_has_Reservation.timestamp_out )";
        return this.db_access.pullFromDB(session_token.getSessionId(), query);
    }

    /**
     * Gets day-to-day weekday booking information for a building
     *
     * @param session_token Session token
     * @param building      Building DTO
     * @param from          Start of date range
     * @param to            End of date range
     * @return ObjectTable
     * {building_id, floor_id, room_id, room_category_id, room_description, timestamp_in, timestamp_out}
     * @throws DbQueryException        when a problem was encountered whilst processing the query
     * @throws SessionExpiredException when the session for the id provided has expired
     * @throws SessionInvalidException when the session for the id provided does not exist in the tracker
     */
    ObjectTable getReservationScheduleData(Token session_token, Building building, Date from, Date to) throws DbQueryException, SessionExpiredException, SessionInvalidException {
        String query = "SELECT " +
                "Room_has_Reservation.building_id, " +
                "Room_has_Reservation.floor_id, " +
                "Room_has_Reservation.room_id, " +
                "Room.room_category_id AS room_category_id, " +
                "Room.description AS room_description, " +
                "Room_has_Reservation.timestamp_in, " +
                "Room_has_Reservation.timestamp_out " +
                "FROM Room_has_Reservation " +
                "LEFT OUTER JOIN Room" +
                " ON Room.id = Room_has_Reservation.room_id" +
                " AND Room.floor_id = Room_has_Reservation.floor_id" +
                " AND Room.building_id = Room_has_Reservation.building_id " +
                "WHERE Room_has_Reservation.cancelled_flag = 0" +
                " AND Room_has_Reservation.timestamp_in >= \"" + TimestampConverter.getUTCTimestampString(from) + "\"" +
                " AND Room_has_Reservation.timestamp_in <= \"" + TimestampConverter.getUTCTimestampString(to) + "\"" +
                " AND Room_has_Reservation.building_id = " + building.id() + " " +
                "ORDER BY" +
                " Room_has_Reservation.building_id," +
                " Room_has_Reservation.floor_id," +
                " Room_has_Reservation.room_id," +
                " strftime( '%w', Room_has_Reservation.timestamp_in )," +
                " time( Room_has_Reservation.timestamp_in )," +
                " time( Room_has_Reservation.timestamp_out )";
        return this.db_access.pullFromDB(session_token.getSessionId(), query);
    }

    /**
     * Gets day-to-day weekday booking information for a floor
     *
     * @param session_token Session token
     * @param floor         Floor DTO
     * @param from          Start of date range
     * @param to            End of date range
     * @return ObjectTable
     * {building_id, floor_id, room_id, room_category_id, room_description, timestamp_in, timestamp_out}
     * @throws DbQueryException        when a problem was encountered whilst processing the query
     * @throws SessionExpiredException when the session for the id provided has expired
     * @throws SessionInvalidException when the session for the id provided does not exist in the tracker
     */
    ObjectTable getReservationScheduleData(Token session_token, Floor floor, Date from, Date to) throws DbQueryException, SessionExpiredException, SessionInvalidException {
        String query = "SELECT " +
                "Room_has_Reservation.building_id, " +
                "Room_has_Reservation.floor_id, " +
                "Room_has_Reservation.room_id, " +
                "Room.room_category_id AS room_category_id, " +
                "Room.description AS room_description, " +
                "Room_has_Reservation.timestamp_in, " +
                "Room_has_Reservation.timestamp_out " +
                "FROM Room_has_Reservation " +
                "LEFT OUTER JOIN Room" +
                " ON Room.id = Room_has_Reservation.room_id" +
                " AND Room.floor_id = Room_has_Reservation.floor_id" +
                " AND Room.building_id = Room_has_Reservation.building_id " +
                "WHERE Room_has_Reservation.cancelled_flag = 0" +
                " AND Room_has_Reservation.timestamp_in >= \"" + TimestampConverter.getUTCTimestampString(from) + "\"" +
                " AND Room_has_Reservation.timestamp_in <= \"" + TimestampConverter.getUTCTimestampString(to) + "\"" +
                " AND Room_has_Reservation.building_id = " + floor.buildingID() +
                " AND Room_has_Reservation.floor_id = " + floor.floorID() + " " +
                "ORDER BY" +
                " Room_has_Reservation.building_id," +
                " Room_has_Reservation.floor_id," +
                " Room_has_Reservation.room_id," +
                " strftime( '%w', Room_has_Reservation.timestamp_in )," +
                " time( Room_has_Reservation.timestamp_in )," +
                " time( Room_has_Reservation.timestamp_out )";
        return this.db_access.pullFromDB(session_token.getSessionId(), query);
    }

    /**
     * Gets day-to-day weekday booking information for a room
     *
     * @param session_token Session token
     * @param room          Room DTO
     * @param from          Start of date range
     * @param to            End of date range
     * @return ObjectTable
     * {building_id, floor_id, room_id, room_category_id, room_description, timestamp_in, timestamp_out}
     * @throws DbQueryException        when a problem was encountered whilst processing the query
     * @throws SessionExpiredException when the session for the id provided has expired
     * @throws SessionInvalidException when the session for the id provided does not exist in the tracker
     */
    ObjectTable getReservationScheduleData(Token session_token, Room room, Date from, Date to) throws DbQueryException, SessionExpiredException, SessionInvalidException {
        String query = "SELECT " +
                "Room_has_Reservation.building_id, " +
                "Room_has_Reservation.floor_id, " +
                "Room_has_Reservation.room_id, " +
                "Room.room_category_id AS room_category_id, " +
                "Room.description AS room_description, " +
                "Room_has_Reservation.timestamp_in, " +
                "Room_has_Reservation.timestamp_out " +
                "FROM Room_has_Reservation " +
                "LEFT OUTER JOIN Room" +
                " ON Room.id = Room_has_Reservation.room_id" +
                " AND Room.floor_id = Room_has_Reservation.floor_id" +
                " AND Room.building_id = Room_has_Reservation.building_id " +
                "WHERE Room_has_Reservation.cancelled_flag = 0" +
                " AND Room_has_Reservation.timestamp_in >= \"" + TimestampConverter.getUTCTimestampString(from) + "\"" +
                " AND Room_has_Reservation.timestamp_in <= \"" + TimestampConverter.getUTCTimestampString(to) + "\"" +
                " AND Room_has_Reservation.building_id = " + room.buildingID() +
                " AND Room_has_Reservation.floor_id = " + room.floorID() +
                " AND Room_has_Reservation.room_id = " + room.id() + " " +
                "ORDER BY" +
                " Room_has_Reservation.building_id," +
                " Room_has_Reservation.floor_id," +
                " Room_has_Reservation.room_id," +
                " strftime( '%w', Room_has_Reservation.timestamp_in )," +
                " time( Room_has_Reservation.timestamp_in )," +
                " time( Room_has_Reservation.timestamp_out )";
        return this.db_access.pullFromDB(session_token.getSessionId(), query);
    }
}