package bbk_beam.mtRooms.reservation.dto;

import java.io.Serializable;

public class RoomPrice implements Serializable {
    private Integer id;
    private Double price;
    private Integer year;

    /**
     * Constructor
     *  @param id    ID
     * @param price Price of room
     * @param year  Year of price
     */
    public RoomPrice(Integer id, Double price, Integer year) {
        this.id = id;
        this.price = price;
        this.year = year;
    }

    /**
     * Gets the RoomPrice ID
     *
     * @return ID
     */
    public Integer id() {
        return this.id;
    }

    /**
     * Gets the RoomPrice price
     *
     * @return Price
     */
    public Double price() {
        return price;
    }

    /**
     * Gets the RoomPrice year
     *
     * @return Year
     */
    public Integer year() {
        return this.year;
    }

    @Override
    public String toString() {
        return "RoomPrice[" + this.id + "]={ "
                + "price: " + price +
                ", year: " + year +
                " }";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RoomPrice roomPrice = (RoomPrice) o;

        if (!id.equals(roomPrice.id)) return false;
        if (!price.equals(roomPrice.price)) return false;
        return year.equals(roomPrice.year);
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + price.hashCode();
        result = 31 * result + year.hashCode();
        return result;
    }
}
