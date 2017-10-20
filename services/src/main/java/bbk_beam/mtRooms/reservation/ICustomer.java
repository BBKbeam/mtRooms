package bbk_beam.mtRooms.reservation;

import java.util.Date;

public interface ICustomer {
    /**
     * Gets the customer's ID
     *
     * @return Customer ID
     */
    public String getCustomerID();

    /**
     * Gets the Date when the account was created
     *
     * @return Creation date
     */
    public Date getAccountCreationDate();

    /**
     * Gets the customer's title
     *
     * @return Customer title
     */
    public String getTitle();

    /**
     * Gets the customer's name
     *
     * @return Customer name
     */
    public String getName();

    /**
     * Gets the customer's surname
     *
     * @return Customer's surname
     */
    public String getSurname();

    /**
     * Gets the customer's 1st address line
     *
     * @return Customer's address line #1
     */
    public String getAddress1();

    /**
     * Sets the customer's 1st address line
     *
     * @param address1 Address line #1
     */
    public void setAddress1(String address1);

    /**
     * Gets the customer's 2nd address line
     *
     * @return Customer's address line #2
     */
    public String getAddress2();

    /**
     * Sets the customer's 2nd address line
     *
     * @param address2 Address line #2
     */
    public void setAddress2(String address2);

    /**
     * Gets the customer's postcode
     *
     * @return Customer's postcode
     */
    public String getPostCode();

    /**
     * Sets the customer's postcode
     *
     * @param postCode Postcode
     */
    public void setPostCode(String postCode);

    /**
     * Gets the customer's city
     *
     * @return Customer's city
     */
    public String getCity();

    /**
     * Sets the customer's city
     *
     * @param city City
     */
    public void setCity(String city);

    /**
     * Gets the customer's county
     *
     * @return Customer's county
     */
    public String getCounty();

    /**
     * Sets the customer's county
     *
     * @param county County
     */
    public void setCounty(String county);

    /**
     * Gets the customer's country
     *
     * @return Customer's country
     */
    public String getCountry();

    /**
     * Gets the customer's 1st phone number
     *
     * @return Customer's 1st phone number
     */
    public String getPhone1();

    /**
     * Sets the customer's phone #1
     *
     * @param phone1 Phone number 1
     */
    public void setPhone1(String phone1);

    /**
     * Gets the customer's 2nd phone number
     *
     * @return Customer's 2nd phone number
     */
    public String getPhone2();

    /**
     * Sets the customer's phone #2
     *
     * @param phone2 Phone number 2
     */
    public void setPhone2(String phone2);

    /**
     * Gets the customer's email address
     *
     * @return Customer's email
     */
    public String getEmail();

    /**
     * Sets the customer's email
     *
     * @param email Email
     */
    public void setEmail(String email);
}
