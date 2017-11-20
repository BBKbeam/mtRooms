package bbk_beam.mtRooms.reservation.delegate;

import bbk_beam.mtRooms.admin.authentication.Token;
import bbk_beam.mtRooms.db.IReservationDbAccess;
import bbk_beam.mtRooms.db.TimestampConverter;
import bbk_beam.mtRooms.db.exception.DbQueryException;
import bbk_beam.mtRooms.db.exception.SessionExpiredException;
import bbk_beam.mtRooms.db.exception.SessionInvalidException;
import bbk_beam.mtRooms.reservation.dto.CustomerDTO;
import bbk_beam.mtRooms.reservation.exception.InvalidPaymentType;
import bbk_beam.mtRooms.reservation.exception.InvalidReservation;
import bbk_beam.mtRooms.reservation.processing.Reservation;
import eadjlib.datastructure.ObjectTable;
import eadjlib.logger.Logger;
import javafx.util.Pair;

import java.util.Collection;
import java.util.HashMap;

public class ReservationDbDelegate implements ICustomerAccount, IPay, IReserve, ISearch {
    private final Logger log = Logger.getLoggerInstance(ReservationDbDelegate.class.getName());
    private IReservationDbAccess db_access;

    /**
     * Constructor
     *
     * @param reservationDbAccess ReservationDbAccess instance
     */
    ReservationDbDelegate(IReservationDbAccess reservationDbAccess) {
        this.db_access = reservationDbAccess;
    }

    @Override
    public CustomerDTO getCustomerAccount(Token session_token, Integer customerID) throws DbQueryException, SessionExpiredException, SessionInvalidException {
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
        ObjectTable table = this.db_access.pullFromDB(session_token.getSessionId(), query);
        try {
            HashMap<String, Object> row = table.getRow(1);
            return new CustomerDTO(
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
            throw new DbQueryException("Customer [" + customerID + "] does not exist in records.", e);
        }
    }

    @Override
    public Collection<Pair<Integer, String>> findCustomer(Token session_token, String surname) throws DbQueryException, SessionExpiredException, SessionInvalidException {
        return null;
    }

    @Override
    public CustomerDTO createNewCustomer(Token session_token, CustomerDTO customer) throws DbQueryException, SessionExpiredException, SessionInvalidException {
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
        this.db_access.pushToDB(session_token.getSessionId(), query);
        return getCustomerAccount(session_token, customer); //Get a fully updated DTO that includes the ID of the new customer
    }

    @Override
    public void saveCustomerChangesToDB(Token session_token, CustomerDTO customer) throws DbQueryException, SessionExpiredException, SessionInvalidException {
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
            throw new DbQueryException("Could not update customer [" + customer.customerID() + "]'s record.");
        }
    }

    @Override
    public Integer pay(Token session_token, Reservation reservation, Integer amount, Integer paymentID) throws InvalidReservation, InvalidPaymentType, DbQueryException, SessionExpiredException, SessionInvalidException {
        return null;
    }

    @Override
    public ObjectTable getPaymentTypes(Token session_token) throws DbQueryException, SessionExpiredException, SessionInvalidException {
        return null;
    }

    @Override
    public void createReservation(Token session_token, Reservation reservation) throws DbQueryException, SessionExpiredException, SessionInvalidException {

    }

    @Override
    public Integer cancelReservation(Token session_token, Reservation reservation) throws InvalidReservation, DbQueryException, SessionExpiredException, SessionInvalidException {
        return null;
    }

    /**
     * Gets a customer's ID placed in its DTO from the details within
     *
     * @param session_token Session token
     * @param customer      Customer DTO
     * @return Customer details
     * @throws DbQueryException        when a problem was encountered whilst processing the query
     * @throws SessionExpiredException When the session for the id provided has expired
     * @throws SessionInvalidException When the session for the id provided does not exist in the tracker
     */
    private CustomerDTO getCustomerAccount(Token session_token, CustomerDTO customer) throws DbQueryException, SessionExpiredException, SessionInvalidException {
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
        ObjectTable table = this.db_access.pullFromDB(session_token.getSessionId(), query);
        if (!table.isEmpty()) {
            Integer id = table.getInteger(1, 1);
            return new CustomerDTO(
                    id,
                    customer.membershipTypeID(),
                    customer.accountCreationDate(),
                    customer.title(),
                    customer.name(),
                    customer.surname(),
                    customer.address1(),
                    customer.address2(),
                    customer.postCode(),
                    customer.city(),
                    customer.county(),
                    customer.country(),
                    customer.phone1(),
                    customer.phone2(),
                    customer.email());
        } else
            log.log_Error("Customer DTO details do not match any record set.");
        throw new DbQueryException("Customer DTO details do not match any record set.");
    }
}
