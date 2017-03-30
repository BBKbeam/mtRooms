package bbk_beam.mtRooms.customer;

public interface ICustomer {
    /**
     * Gets the customer's ID
     *
     * @return Customer ID
     */
    String getCustomerID();

    /**
     * Gets the Date when the account was created
     *
     * @return Creation date
     */
    Date getAccountCreationDate();

    /**
     * Gets the customer's title
     *
     * @return Customer title
     */
    String getTitle();

    /**
     * Gets the customer's name
     *
     * @return Customer name
     */
    String getName();

    /**
     * Gets the customer's surname
     *
     * @return Customer's surname
     */
    String getSurname();

    /**
     * Gets the customer's 1st address line
     *
     * @return Customer's address line #1
     */
    String getAddress1();

    /**
     * Gets the customer's 2nd address line
     *
     * @return Customer's address line #2
     */
    String getAddress2();

    /**
     * Gets the customer's postcode
     *
     * @return Customer's postcode
     */
    String getPostCode();

    /**
     * Gets the customer's city
     *
     * @return Customer's city
     */
    String getCity();

    /**
     * Gets the customer's county
     *
     * @return Customer's county
     */
    String getCounty();

    /**
     * Gets the customer's country
     *
     * @return Customer's country
     */
    String getCountry();

    /**
     * Gets the customer's 1st phone number
     *
     * @return Customer's 1st phone number
     */
    String getPhone1();

    /**
     * Gets the customer's 2nd phone number
     *
     * @return Customer's 2nd phone number
     */
    String getPhone2();

    /**
     * Gets the customer's email address
     *
     * @return Customer's email
     */
    String getEmail();

    /**
     * Sets the customer's 1st address line
     *
     * @param address1 Address line #1
     */
    void setAddress1(String address1);

    /**
     * Sets the customer's 2nd address line
     *
     * @param address2 Address line #2
     */
    void setAddress2(String address2);

    /**
     * Sets the customer's postcode
     *
     * @param postCode Postcode
     */
    void setPostCode(String postCode);

    /**
     * Sets the customer's city
     *
     * @param city City
     */
    void setCity(String city);

    /**
     * Sets the customer's county
     *
     * @param county County
     */
    void setCounty(String county);

    /**
     * Sets the customer's phone #1
     *
     * @param phone1 Phone number 1
     */
    void setPhone1(String phone1);

    /**
     * Sets the customer's phone #2
     *
     * @param phone2 Phone number 2
     */
    void setPhone2(String phone2);

    /**
     * Sets the customer's email
     *
     * @param email Email
     */
    void setEmail(String email);
}
