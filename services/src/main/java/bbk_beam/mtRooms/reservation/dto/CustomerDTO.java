package bbk_beam.mtRooms.reservation.dto;

import java.util.Date;

public class CustomerDTO {
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
    public CustomerDTO(Integer id,
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
     * Copy-Constructor
     *
     * @param customerDTO CustomerDTO object to copy
     */
    CustomerDTO(CustomerDTO customerDTO) {
        this.id = customerDTO.id;
        this.membership_type_id = customerDTO.membership_type_id;
        this.account_creation = customerDTO.account_creation;
        this.title = customerDTO.title;
        this.name = customerDTO.name;
        this.surname = customerDTO.surname;
        this.address1 = customerDTO.address1;
        this.address2 = customerDTO.address2;
        this.postcode = customerDTO.postcode;
        this.city = customerDTO.city;
        this.county = customerDTO.county;
        this.country = customerDTO.country;
        this.phone1 = customerDTO.phone1;
        this.phone2 = customerDTO.phone2;
        this.email = customerDTO.email;
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
}
