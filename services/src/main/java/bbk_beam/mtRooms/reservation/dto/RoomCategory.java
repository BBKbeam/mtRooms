package bbk_beam.mtRooms.reservation.dto;

public class RoomCategory {
    private Integer id;
    private Integer capacity;
    private Integer dimension;

    /**
     * Constructor
     *
     * @param id        ID
     * @param capacity  Capacity (people)
     * @param dimension Dimension in square meters
     */
    public RoomCategory(Integer id, Integer capacity, Integer dimension) {
        this.id = id;
        this.capacity = capacity;
        this.dimension = dimension;
    }

    /**
     * Gets the category ID
     *
     * @return ID
     */
    public Integer id() {
        return this.id;
    }

    /**
     * Gets the people capacity
     *
     * @return Capacity
     */
    public Integer capacity() {
        return this.capacity;
    }

    /**
     * Gets the dimension in square meters
     *
     * @return Dimension
     */
    public Integer dimension() {
        return this.dimension;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RoomCategory that = (RoomCategory) o;

        if (!id.equals(that.id)) return false;
        if (!capacity.equals(that.capacity)) return false;
        return dimension.equals(that.dimension);
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + capacity.hashCode();
        result = 31 * result + dimension.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "[" + id + "]={ "
                + "capacity: " + capacity +
                ", dimension: " + dimension +
                " }";
    }
}
