package bbk_beam.mtRooms.reservation.dto;

import java.io.Serializable;
import java.util.Objects;

public class Building implements Serializable {
    private Integer id;
    private String name;
    private String address1;
    private String address2;
    private String postcode;
    private String city;
    private String country;
    private String phone;

    /**
     * Constructor
     *
     * @param id       Building ID
     * @param name     Building name
     * @param address1 Address line 1
     * @param address2 Address line 2
     * @param postcode Postcode
     * @param city     City
     * @param country  Country
     * @param phone    Phone number
     */
    public Building(Integer id, String name, String address1, String address2, String postcode, String city, String country, String phone) {
        this.id = id;
        this.name = name;
        this.address1 = address1;
        this.address2 = address2;
        this.postcode = postcode;
        this.city = city;
        this.country = country;
        this.phone = phone;
    }

    /**
     * Constructor
     *
     * @param name     Building name
     * @param address1 Address line 1
     * @param address2 Address line 2
     * @param postcode Postcode
     * @param city     City
     * @param country  Country
     * @param phone    Phone number
     */
    public Building(String name, String address1, String address2, String postcode, String city, String country, String phone) {
        this.id = -1;
        this.name = name;
        this.address1 = address1;
        this.address2 = address2;
        this.postcode = postcode;
        this.city = city;
        this.country = country;
        this.phone = phone;
    }

    /**
     * Gets the building ID
     *
     * @return Building ID
     */
    public Integer id() {
        return this.id;
    }

    /**
     * Gets the building name
     *
     * @return Building name
     */
    public String name() {
        return this.name;
    }

    /**
     * Gets the address line 1
     *
     * @return Address line 1
     */
    public String address1() {
        return this.address1;
    }

    /**
     * Gets the address line 2
     *
     * @return Address line 2
     */
    public String address2() {
        return this.address2;
    }

    /**
     * Gets the postcode
     *
     * @return Postcode
     */
    public String postcode() {
        return this.postcode;
    }

    /**
     * Gets the city
     *
     * @return City
     */
    public String city() {
        return this.city;
    }

    public String country() {
        return this.country;
    }

    /**
     * Gets the phone number
     *
     * @return Phone number
     */
    public String phone() {
        return this.phone;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Building building = (Building) o;
        return Objects.equals(id, building.id) &&
                Objects.equals(name, building.name) &&
                Objects.equals(address1, building.address1) &&
                Objects.equals(address2, building.address2) &&
                Objects.equals(postcode, building.postcode) &&
                Objects.equals(city, building.city) &&
                Objects.equals(country, building.country) &&
                Objects.equals(phone, building.phone);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, address1, address2, postcode, city, country, phone);
    }

    @Override
    public String toString() {
        return "Building[" + id + "]={ " +
                "name='" + name + '\'' +
                ", address1='" + address1 + '\'' +
                ", address2='" + address2 + '\'' +
                ", postcode='" + postcode + '\'' +
                ", city='" + city + '\'' +
                ", country='" + country + '\'' +
                ", phone='" + phone + '\'' +
                " }";
    }
}
