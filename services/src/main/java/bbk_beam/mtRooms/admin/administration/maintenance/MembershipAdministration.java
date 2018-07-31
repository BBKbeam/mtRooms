package bbk_beam.mtRooms.admin.administration.maintenance;

import bbk_beam.mtRooms.admin.authentication.Token;
import bbk_beam.mtRooms.db.IReservationDbAccess;
import bbk_beam.mtRooms.db.exception.DbQueryException;
import bbk_beam.mtRooms.db.exception.SessionExpiredException;
import bbk_beam.mtRooms.db.exception.SessionInvalidException;
import bbk_beam.mtRooms.reservation.dto.Discount;
import bbk_beam.mtRooms.reservation.dto.DiscountCategory;
import bbk_beam.mtRooms.reservation.dto.Membership;
import eadjlib.datastructure.ObjectTable;
import eadjlib.logger.Logger;

import java.util.HashMap;

public class MembershipAdministration {
    private final Logger log = Logger.getLoggerInstance(RealEstateAdministration.class.getName());
    private IReservationDbAccess db_access;

    /**
     * Adds a new discount category to the records
     *
     * @param admin_token      Administration session token
     * @param discountCategory DiscountCategory DTO
     * @return ObjectTable of matching/created record
     * @throws DbQueryException        when query failed
     * @throws SessionExpiredException when current administrator session has expired
     * @throws SessionInvalidException when administrator session is not valid
     */
    private DiscountCategory add(Token admin_token, DiscountCategory discountCategory) throws DbQueryException, SessionExpiredException, SessionInvalidException {
        String check_query = "SELECT * FROM DiscountCategory WHERE description = \"" + discountCategory.description() + "\"";
        String create_query = "INSERT INTO DiscountCategory( description ) VALUES ( \"" + discountCategory.description() + "\" )";
        ObjectTable table = this.db_access.pullFromDB(admin_token.getSessionId(), check_query);
        if (table.isEmpty()) {
            log.log_Trace("Creating: ", discountCategory);
            this.db_access.pushToDB(admin_token.getSessionId(), create_query);
            table = this.db_access.pullFromDB(admin_token.getSessionId(), check_query);
        }
        if (!table.isEmpty()) {
            HashMap<String, Object> row = table.getRow(1);
            return new DiscountCategory(
                    (Integer) row.get("id"),
                    (String) row.get("description")

            );
        } else {
            log.log_Error("Failed fetching and/or creation of record: ", discountCategory);
            throw new DbQueryException("Failed fetching and/or creation of record: " + discountCategory);
        }
    }

    /**
     * Adds a new discount to the records
     *
     * @param admin_token Administration session token
     * @param discount    Discount DTO
     * @return ObjectTable of matching/created record
     * @throws DbQueryException        when query failed
     * @throws SessionExpiredException when current administrator session has expired
     * @throws SessionInvalidException when administrator session is not valid
     */
    private Discount add(Token admin_token, Discount discount) throws DbQueryException, SessionExpiredException, SessionInvalidException {
        DiscountCategory discountCategory = add(admin_token, discount.category());
        String check_query = "SELECT * FROM Discount " +
                "WHERE discount_rate = " + discount.rate() + "" +
                " AND discount_category_id = " + discountCategory.id();
        String create_query = "INSERT INTO Discount( discount_rate, discount_category_id ) VALUES ( " +
                discount.rate() + ", " +
                discountCategory.id() + " )";
        ObjectTable table = this.db_access.pullFromDB(admin_token.getSessionId(), check_query);
        if (table.isEmpty()) {
            log.log_Trace("Creating: ", discount);
            this.db_access.pushToDB(admin_token.getSessionId(), create_query);
            table = this.db_access.pullFromDB(admin_token.getSessionId(), check_query);
        }
        if (!table.isEmpty()) {
            HashMap<String, Object> row = table.getRow(1);
            return new Discount(
                    (Integer) row.get("id"),
                    (Double) row.get("discount_rate"),
                    discountCategory
            );
        } else {
            log.log_Error("Failed fetching and/or creation of record: ", discount);
            throw new DbQueryException("Failed fetching and/or creation of record: " + discount);
        }
    }

    /**
     * Removes an entry from the Discount table
     * <p>Note: checks if the entry is tied to a Reservation but not by a MembershipType via DiscountCategory</p>
     *
     * @param admin_token Administration session token
     * @param discount    Discount DTO
     * @return Success
     * @throws DbQueryException        when query failed
     * @throws SessionExpiredException when current administrator session has expired
     * @throws SessionInvalidException when administrator session is not valid
     */
    private boolean remove(Token admin_token, Discount discount) throws DbQueryException, SessionExpiredException, SessionInvalidException {
        String check_query = "SELECT id FROM Reservation WHERE discount_id = " + discount.id();
        ObjectTable table = this.db_access.pullFromDB(admin_token.getSessionId(), check_query);
        if (!table.isEmpty()) {
            log.log_Debug("Discount tied to Reservation(s). Cannot delete entry: ", discount);
            return false;
        } else {
            String remove_query = "DELETE FROM Discount WHERE id = " + discount.id();
            this.db_access.pushToDB(admin_token.getSessionId(), remove_query);
            log.log_Trace("Removed: ", discount);
            return true;
        }
    }

    /**
     * Removes an entry from the DiscountCategory table
     * <p>Checks if referenced by either the MembershipType or Discount tables prior to deletion.</p>
     *
     * @param admin_token      Administration session token
     * @param discountCategory DiscountCategory DTO
     * @return Success
     * @throws DbQueryException        when query failed
     * @throws SessionExpiredException when current administrator session has expired
     * @throws SessionInvalidException when administrator session is not valid
     */
    private boolean remove(Token admin_token, DiscountCategory discountCategory) throws DbQueryException, SessionExpiredException, SessionInvalidException {
        String check_query1 = "SELECT id FROM MembershipType WHERE discount_category_id = " + discountCategory.id();
        String check_query2 = "SELECT id FROM Discount WHERE discount_category_id = " + discountCategory.id();
        ObjectTable table1 = this.db_access.pullFromDB(admin_token.getSessionId(), check_query1);
        ObjectTable table2 = this.db_access.pullFromDB(admin_token.getSessionId(), check_query2);
        if (table1.isEmpty() && table2.isEmpty()) { //DiscountCategory entry unused
            String remove_query = "DELETE FROM DiscountCategory WHERE id = " + discountCategory.id();
            this.db_access.pushToDB(admin_token.getSessionId(), remove_query);
            log.log_Trace("Removed: ", discountCategory);
            return true;
        } else { //DiscountCategory tied to MembershipType and/or Discount entries
            log.log_Debug("DiscountCategory entry is tied to table(s) {",
                    (!table1.isEmpty() ? " MembershipType" : ""),
                    (!table2.isEmpty() ? " Discount" : ""),
                    "}. Cannot delete entry: ", discountCategory
            );
            return false;
        }
    }

    /**
     * Constructor
     *
     * @param db_access IReservationDbAccess instance
     */
    public MembershipAdministration(IReservationDbAccess db_access) {
        this.db_access = db_access;
    }

    /**
     * Gets the Membership records and associated DiscountCategory and Discount entries
     *
     * @param admin_token Administration session token
     * @return ObjectTable of the Membership records
     * @throws DbQueryException        when query failed
     * @throws SessionExpiredException when current administrator session has expired
     * @throws SessionInvalidException when administrator session is not valid
     */
    public ObjectTable getMemberships(Token admin_token) throws DbQueryException, SessionExpiredException, SessionInvalidException {
        String query = "SELECT " +
                "MembershipType.id AS membership_id, " +
                "MembershipType.description AS membership_description, " +
                "MembershipType.discount_category_id, " +
                "DiscountCategory.description AS discount_category_description, " +
                "Discount.id AS discount_id, " +
                "Discount.discount_rate, " +
                "Customer.id AS customer_id " +
                "FROM MembershipType " +
                "LEFT OUTER JOIN DiscountCategory " +
                "ON MembershipType.discount_category_id = DiscountCategory.id " +
                "LEFT OUTER JOIN Discount " +
                "ON DiscountCategory.id = Discount.discount_category_id " +
                "LEFT JOIN Customer " +
                "ON Customer.membership_type_id = MembershipType.id";
        return this.db_access.pullFromDB(admin_token.getSessionId(), query);
    }

    /**
     * Gets the DiscountCategory records and associated Discount entries
     *
     * @param admin_token Administration session token
     * @return ObjectTable of the discount categories and associated discounts
     * @throws DbQueryException        when query failed
     * @throws SessionExpiredException when current administrator session has expired
     * @throws SessionInvalidException when administrator session is not valid
     */
    public ObjectTable getDiscountCategories(Token admin_token) throws DbQueryException, SessionExpiredException, SessionInvalidException {
        String query = "SELECT " +
                "DiscountCategory.id AS discount_category_id, " +
                "DiscountCategory.description AS discount_category_description, " +
                "Discount.id AS discount_id, " +
                "Discount.discount_rate " +
                "FROM DiscountCategory " +
                "LEFT OUTER JOIN Discount " +
                "ON DiscountCategory.id = Discount.discount_category_id";
        return this.db_access.pullFromDB(admin_token.getSessionId(), query);
    }

    /**
     * Adds a new membership to the records
     *
     * @param admin_token Administration session token
     * @param membership  Membership DTO
     * @return ObjectTable of matching/created record
     * @throws DbQueryException        when query failed
     * @throws SessionExpiredException when current administrator session has expired
     * @throws SessionInvalidException when administrator session is not valid
     */
    public Membership add(Token admin_token, Membership membership) throws DbQueryException, SessionExpiredException, SessionInvalidException {
        Discount discount = add(admin_token, membership.discount());
        String check_query = "SELECT * FROM MembershipType " +
                "WHERE description = \"" + membership.description() + "\"" +
                " AND discount_category_id = " + discount.category().id();
        String create_query = "INSERT INTO MembershipType( description, discount_category_id ) VALUES ( " +
                "\"" + membership.description() + "\", " +
                discount.category().id() + " )";
        ObjectTable table = this.db_access.pullFromDB(admin_token.getSessionId(), check_query);
        if (table.isEmpty()) {
            log.log_Trace("Creating: ", membership);
            this.db_access.pushToDB(admin_token.getSessionId(), create_query);
            table = this.db_access.pullFromDB(admin_token.getSessionId(), check_query);
        }
        if (!table.isEmpty()) {
            HashMap<String, Object> row = table.getRow(1);
            return new Membership(
                    (Integer) row.get("id"),
                    (String) row.get("description"),
                    discount
            );
        } else {
            log.log_Error("Failed fetching and/or creation of record: ", membership);
            throw new DbQueryException("Failed fetching and/or creation of record: " + membership);
        }
    }

    /**
     * Removes a membership from the records
     *
     * @param admin_token Administration session token
     * @param membership  Membership DTO
     * @return Success
     * @throws DbQueryException        when query failed
     * @throws SessionExpiredException when current administrator session has expired
     * @throws SessionInvalidException when administrator session is not valid
     */
    public boolean remove(Token admin_token, Membership membership) throws DbQueryException, SessionExpiredException, SessionInvalidException {
        //Check ties with Customer(s)
        String check_query = "SELECT COUNT(*) AS result FROM Customer WHERE membership_type_id = " + membership.id();
        ObjectTable table1 = this.db_access.pullFromDB(admin_token.getSessionId(), check_query);
        if (table1.getInteger(1, 1) > 0) { //a.k.a. 'result' of COUNT(*)
            log.log_Error("Membership is tied to Customer(s). Cannot delete entry: ", membership);
            return false;
        }
        //Remove MembershipType entry
        String remove_query = "DELETE FROM MembershipType WHERE id = " + membership.id();
        this.db_access.pushToDB(admin_token.getSessionId(), remove_query);
        log.log_Trace("Removed: ", membership);
        remove(admin_token, membership.discount());
        remove(admin_token, membership.discount().category());
        return true;
    }
}
