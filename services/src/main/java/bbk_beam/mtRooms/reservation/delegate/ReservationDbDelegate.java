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
    public CustomerDTO createNewCustomer(Token session_token, String title, String name, String surname) throws DbQueryException, SessionExpiredException, SessionInvalidException {
        return null;
    }

    @Override
    public void saveCustomerChangesToDB(Token session_token, CustomerDTO customer) throws DbQueryException, SessionExpiredException, SessionInvalidException {

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
}
