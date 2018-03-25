package bbk_beam.mtRooms.reservation.dto;

import java.util.Objects;

/**
 * RoomProperty DTO
 * <p>
 * Container class for the properties of a room for evaluation purposes
 * Boolean flags that are set to false on the instance used for evaluation are non-required
 * Values set to negative (-1) on the instance used for evaluation are non-required
 * Other values mean that properties are required and need to be evaluated as such during comparisons
 */
public class RoomProperty implements Comparable<RoomProperty> {
    private boolean has_fixed_chairs;
    private boolean has_catering_space;
    private boolean has_whiteboard;
    private boolean has_projector;
    private Integer capacity;
    private Integer dimension;

    /**
     * Constructor
     *
     * @param has_fixed_chairs   Fixed chairs flag
     * @param has_catering_space Catering space flag
     * @param has_whiteboard     Whiteboard flag
     * @param has_projector      Projector flag
     * @param capacity           Number of people room can hold
     * @param dimension          Dimension of room in square metres
     */
    public RoomProperty(
            boolean has_fixed_chairs,
            boolean has_catering_space,
            boolean has_whiteboard,
            boolean has_projector,
            Integer capacity,
            Integer dimension) {
        this.has_fixed_chairs = has_fixed_chairs;
        this.has_catering_space = has_catering_space;
        this.has_whiteboard = has_whiteboard;
        this.has_projector = has_projector;
        this.capacity = capacity;
        this.dimension = dimension;
    }

    /**
     * Constructor
     *
     * @param has_fixed_chairs   Fixed chairs flag
     * @param has_catering_space Catering space flag
     * @param has_whiteboard     Whiteboard flag
     * @param has_projector      Projector flag
     * @param capacity           Number of people room can hold
     */
    public RoomProperty(
            boolean has_fixed_chairs,
            boolean has_catering_space,
            boolean has_whiteboard,
            boolean has_projector,
            Integer capacity) {
        this.has_fixed_chairs = has_fixed_chairs;
        this.has_catering_space = has_catering_space;
        this.has_whiteboard = has_whiteboard;
        this.has_projector = has_projector;
        this.capacity = capacity;
        this.dimension = -1;
    }

    /**
     * Gets fixed chair the flag
     *
     * @return Fixed chair flag
     */
    public boolean hasFixedChairs() {
        return this.has_fixed_chairs;
    }

    /**
     * Gets the catering space flag
     *
     * @return Catering space flag
     */
    public boolean hasCateringSpace() {
        return this.has_catering_space;
    }

    /**
     * Gets the whiteboard flag
     *
     * @return Whiteboard flag
     */
    public boolean hasWhiteboard() {
        return this.has_whiteboard;
    }

    /**
     * Gets the projector flag
     *
     * @return Projector flag
     */
    public boolean hasProjector() {
        return this.has_projector;
    }

    /**
     * Gets the capacity
     *
     * @return Capacity (-1 when does not matter)
     */
    public Integer capacity() {
        return this.capacity;
    }

    /**
     * Gets the dimension
     *
     * @return Dimension in sq metres (-1 when does not matter)
     */
    public Integer dimension() {
        return this.dimension;
    }

    /**
     * Compares a flag requirement with a given flag
     *
     * @param required   Required flag
     * @param other_flag Flag to compare to
     * @return (- 1) when requirement is not met, (0) when equal, (+1) when not required but given anyway
     */
    private int compareFlags(boolean required, boolean other_flag) {
        if (required)
            return other_flag ? 0 : -1;
        else
            return other_flag ? 1 : 0;
    }

    @Override
    public int compareTo(RoomProperty that) {
        int tally = 0, result;
        if ((result = compareFlags(this.hasFixedChairs(), that.hasFixedChairs())) < 0)
            return -1;
        else
            tally += result;
        if ((result = compareFlags(this.hasCateringSpace(), that.hasCateringSpace())) < 0)
            return -1;
        else
            tally += result;
        if ((result = compareFlags(this.hasWhiteboard(), that.hasWhiteboard())) < 0)
            return -1;
        else
            tally += result;
        if ((result = compareFlags(this.hasProjector(), that.hasProjector())) < 0)
            return -1;
        else
            tally += result;
        if (this.capacity() > 0) //i.e.: is capacity needed for the evaluation?
            if ((result = that.capacity().compareTo(this.capacity())) < 0)
                return -1;
            else
                tally += result;
        if (this.dimension() > 0) //i.e.: is dimension needed for the evaluation?
            if ((result = that.dimension().compareTo(this.dimension())) < 0)
                return -1;
            else
                tally += result;
        return tally;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RoomProperty that = (RoomProperty) o;
        return has_fixed_chairs == that.has_fixed_chairs &&
                has_catering_space == that.has_catering_space &&
                has_whiteboard == that.has_whiteboard &&
                has_projector == that.has_projector &&
                capacity == that.capacity &&
                dimension == that.dimension;
    }

    @Override
    public int hashCode() {
        return Objects.hash(has_fixed_chairs, has_catering_space, has_whiteboard, has_projector, capacity, dimension);
    }
}
