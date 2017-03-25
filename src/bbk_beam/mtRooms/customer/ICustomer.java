package bbk_beam.mtRooms.customer;

public interface ICustomer {
    String getCustomerID();

    Date getCustomerSince();

    String getTitle();

    String getName();

    String getSurname();

    String getAddress();

    String getPostCode();

    String getCity();

    String getCounty();

    String getCountry();

    String getPhone1();

    String getPhone2();

    String getEmail();

    void setAddress1(String address1);

    void setAddress2(String address2);

    void setPostCode(String postCode);

    void setCity(String city);

    void setCounty(String county);

    void setPhone1(String phone1);

    void setPhone2(String phone2);

    void setEmail(String email);
}
