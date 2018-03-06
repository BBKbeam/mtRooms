package bbk_beam.mtRooms.reservation.dto;

import bbk_beam.mtRooms.db.TimestampConverter;
import bbk_beam.mtRooms.reservation.exception.InvalidOperation;
import eadjlib.logger.Logger;

import java.util.Date;

public class Customer {
    private final Logger log = Logger.getLoggerInstance(Customer.class.getName());
    private Integer id;
    private Integer membership_type_id;
    private Date account_creation;
    private String title;
    private String name;
    private String surname;
    private String address1;
    private String address2;
    private String postcode;
    private String city;
    private String county;
    private String country;
    private String phone1;
    private String phone2;
    private String email;

    /**
     * Constructor
     *
     * @param id                 Customer ID
     * @param membership_type_id Membership type ID
     * @param account_creation   Customer account creation date
     * @param title              Customer title
     * @param name               Customer name
     * @param surname            Customer  surname
     * @param address1           Customer address line 1
     * @param address2           Customer address line 2
     * @param postcode           Customer postcode
     * @param city               Customer city
     * @param county             Customer county
     * @param country            Customer country
     * @param phone1             Customer phone 1
     * @param phone2             Customer phone 2
     * @param email              Customer email
     */
    public Customer(Integer id,
                    Integer membership_type_id,
                    Date account_creation,
                    String title,
                    String name,
                    String surname,
                    String address1,
                    String address2,
                    String postcode,
                    String city,
                    String county,
                    String country,
                    String phone1,
                    String phone2,
                    String email) {
        this.id = id;
        this.membership_type_id = membership_type_id;
        this.account_creation = account_creation;
        this.title = title;
        this.name = name;
        this.surname = surname;
        this.address1 = address1;
        this.address2 = address2;
        this.postcode = postcode;
        this.city = city;
        this.county = county;
        this.country = country;
        this.phone1 = phone1;
        this.phone2 = phone2;
        this.email = email;
    }

    /**
     * Constructor
     *
     * @param membership_type_id Membership type ID
     * @param account_creation   Customer account creation date
     * @param title              Customer title
     * @param name               Customer name
     * @param surname            Customer  surname
     * @param address1           Customer address line 1
     * @param address2           Customer address line 2
     * @param postcode           Customer postcode
     * @param city               Customer city
     * @param county             Customer county
     * @param country            Customer country
     * @param phone1             Customer phone 1
     * @param phone2             Customer phone 2
     * @param email              Customer email
     */
    public Customer(Integer membership_type_id,
                    Date account_creation,
                    String title,
                    String name,
                    String surname,
                    String address1,
                    String address2,
                    String postcode,
                    String city,
                    String county,
                    String country,
                    String phone1,
                    String phone2,
                    String email) {
        this.id = -1;
        this.membership_type_id = membership_type_id;
        this.account_creation = account_creation;
        this.title = title;
        this.name = name;
        this.surname = surname;
        this.address1 = address1;
        this.address2 = address2;
        this.postcode = postcode;
        this.city = city;
        this.county = county;
        this.country = country;
        this.phone1 = phone1;
        this.phone2 = phone2;
        this.email = email;
    }

    /**
     * Copy-Constructor
     *
     * @param customer Customer object to copy
     */
    Customer(Customer customer) {
        this.id = customer.id;
        this.membership_type_id = customer.membership_type_id;
        this.account_creation = customer.account_creation;
        this.title = customer.title;
        this.name = customer.name;
        this.surname = customer.surname;
        this.address1 = customer.address1;
        this.address2 = customer.address2;
        this.postcode = customer.postcode;
        this.city = customer.city;
        this.county = customer.county;
        this.country = customer.country;
        this.phone1 = customer.phone1;
        this.phone2 = customer.phone2;
        this.email = customer.email;
    }

    /**
     * Sets the Customer's ID
     *
     * @param id ID
     * @throws InvalidOperation when trying to override a valid ID (>0)
     */
    public void setID(Integer id) throws InvalidOperation {
        if (this.id < 1) {
            this.id = id;
        } else {
            log.log_Error("Trying to override valid Customer ID [", this.id, "] with [", id, "]");
            throw new InvalidOperation("Trying to override valid Customer ID [" + this.id + "] with [" + id + "]");
        }
    }

    /**
     * Gets the customer's ID
     *
     * @return Customer ID
     */
    public Integer customerID() {
        return this.id;
    }

    /**
     * Gets the customer's membership type ID
     *
     * @return Membership type ID
     */
    public Integer membershipTypeID() {
        return this.membership_type_id;
    }

    /**
     * Sets the customer's membership type ID
     *
     * @param type_id Membership type ID
     */
    public void setMembershipTypeID(Integer type_id) {
        this.membership_type_id = type_id;
    }

    /**
     * Gets the Date when the account was created
     *
     * @return Creation date
     */
    public Date accountCreationDate() {
        return this.account_creation;
    }

    /**
     * Gets the customer's title
     *
     * @return Customer title
     */
    public String title() {
        return this.title;
    }

    /**
     * Gets the customer's name
     *
     * @return Customer name
     */
    public String name() {
        return this.name;
    }

    /**
     * Gets the customer's surname
     *
     * @return Customer's surname
     */
    public String surname() {
        return this.surname;
    }

    /**
     * Gets the customer's 1st address line
     *
     * @return Customer's address line #1
     */
    public String address1() {
        return this.address1;
    }

    /**
     * Sets the customer's 1st address line
     *
     * @param address1 Address line #1
     */
    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    /**
     * Gets the customer's 2nd address line
     *
     * @return Customer's address line #2
     */
    public String address2() {
        return this.address2;
    }

    /**
     * Sets the customer's 2nd address line
     *
     * @param address2 Address line #2
     */
    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    /**
     * Gets the customer's postcode
     *
     * @return Customer's postcode
     */
    public String postCode() {
        return this.postcode;
    }

    /**
     * Sets the customer's postcode
     *
     * @param postCode Postcode
     */
    public void setPostCode(String postCode) {
        this.postcode = postCode;
    }

    /**
     * Gets the customer's city
     *
     * @return Customer's city
     */
    public String city() {
        return this.city;
    }

    /**
     * Sets the customer's city
     *
     * @param city City
     */
    public void setCity(String city) {
        this.city = city;
    }

    /**
     * Gets the customer's county
     *
     * @return Customer's county
     */
    public String county() {
        return this.county;
    }

    /**
     * Sets the customer's county
     *
     * @param county County
     */
    public void setCounty(String county) {
        this.county = county;
    }

    /**
     * Gets the customer's country
     *
     * @return Customer's country
     */
    public String country() {
        return this.country;
    }

    /**
     * Sets the customer's country
     *
     * @param country Country
     */
    public void setCountry(String country) {
        this.country = country;
    }

    /**
     * Gets the customer's 1st phone number
     *
     * @return Customer's 1st phone number
     */
    public String phone1() {
        return this.phone1;
    }

    /**
     * Sets the customer's phone #1
     *
     * @param phone1 Phone number 1
     */
    public void setPhone1(String phone1) {
        this.phone1 = phone1;
    }

    /**
     * Gets the customer's 2nd phone number
     *
     * @return Customer's 2nd phone number
     */
    public String phone2() {
        return this.phone2;
    }

    /**
     * Sets the customer's phone #2
     *
     * @param phone2 Phone number 2
     */
    public void setPhone2(String phone2) {
        this.phone2 = phone2;
    }

    /**
     * Gets the customer's email address
     *
     * @return Customer's email
     */
    public String email() {
        return this.email;
    }

    /**
     * Sets the customer's email
     *
     * @param email Email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "[" + this.id + "]={ "
                + this.membership_type_id + ", "
                + this.account_creation + ", "
                + this.title + ", "
                + this.name + ", "
                + this.surname + ", "
                + this.address1 + ", "
                + this.address2 + ", "
                + this.postcode + ", "
                + this.city + ", "
                + this.county + ", "
                + this.country + ", "
                + this.phone1 + ", "
                + this.phone2 + ", "
                + this.email + " }";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Customer that = (Customer) o;

        if (!id.equals(that.id)) return false;
        if (!membership_type_id.equals(that.membership_type_id)) return false;
        String this_account_creation_timestamp = TimestampConverter.getUTCTimestampString(this.account_creation);
        String that_account_creation_timestamp = TimestampConverter.getUTCTimestampString(that.account_creation);
        if (!this_account_creation_timestamp.equals(that_account_creation_timestamp)) return false;
        if (!title.equals(that.title)) return false;
        if (!name.equals(that.name)) return false;
        if (!surname.equals(that.surname)) return false;
        if (!address1.equals(that.address1)) return false;
        if (address2 != null ? !address2.equals(that.address2) : that.address2 != null) return false;
        if (!postcode.equals(that.postcode)) return false;
        if (!city.equals(that.city)) return false;
        if (county != null ? !county.equals(that.county) : that.county != null) return false;
        if (!country.equals(that.country)) return false;
        if (!phone1.equals(that.phone1)) return false;
        if (phone2 != null ? !phone2.equals(that.phone2) : that.phone2 != null) return false;
        return email.equals(that.email);
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + membership_type_id.hashCode();
        result = 31 * result + TimestampConverter.getUTCTimestampString(account_creation).hashCode();
        result = 31 * result + title.hashCode();
        result = 31 * result + name.hashCode();
        result = 31 * result + surname.hashCode();
        result = 31 * result + address1.hashCode();
        result = 31 * result + (address2 != null ? address2.hashCode() : 0);
        result = 31 * result + postcode.hashCode();
        result = 31 * result + city.hashCode();
        result = 31 * result + (county != null ? county.hashCode() : 0);
        result = 31 * result + country.hashCode();
        result = 31 * result + phone1.hashCode();
        result = 31 * result + (phone2 != null ? phone2.hashCode() : 0);
        result = 31 * result + email.hashCode();
        return result;
    }
}
