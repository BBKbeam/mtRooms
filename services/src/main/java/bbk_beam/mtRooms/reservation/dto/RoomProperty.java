package bbk_beam.mtRooms.reservation.dto;

import java.util.Objects;

/**
 * RoomProperty DTO
 * <p>
 * Container class for the properties of a room for evaluation purposes
 * Values set to negative (-1) on the instance used for evaluation are non-required,
 * positives will be evaluated as a "minimum requirement"
 * </p>
 */
public class RoomProperty implements Comparable<RoomProperty> {
    /**
     * Trilean  (3-state logic)
     * <p>
     * Requirement levels that model the state (TRUE/FALSE) or the needed requirements (TRUE/UNDEFINED/FALSE)
     * </p>
     */
    enum Trilean {
        TRUE,
        UNDEFINED,
        FALSE
    }

    private Trilean has_fixed_chairs;
    private Trilean has_catering_space;
    private Trilean has_whiteboard;
    private Trilean has_projector;
    private Integer capacity;
    private Integer dimension;

    /**
     * Constructor
     *
     * @param has_fixed_chairs   Fixed chairs state or requirement level
     * @param has_catering_space Catering space state or requirement level
     * @param has_whiteboard     Whiteboard state or requirement level
     * @param has_projector      Projector state or requirement level
     * @param capacity           Number of people room can hold
     * @param dimension          Dimension of room in square metres
     */
    public RoomProperty(
            Trilean has_fixed_chairs,
            Trilean has_catering_space,
            Trilean has_whiteboard,
            Trilean has_projector,
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
     * @param has_fixed_chairs   Fixed chairs state or requirement level
     * @param has_catering_space Catering space state or requirement level
     * @param has_whiteboard     Whiteboard state or requirement level
     * @param has_projector      Projector state or requirement level
     * @param capacity           Number of people room can hold
     */
    public RoomProperty(
            Trilean has_fixed_chairs,
            Trilean has_catering_space,
            Trilean has_whiteboard,
            Trilean has_projector,
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
     * @return Fixed chair state or requirement level
     */
    public Trilean hasFixedChairs() {
        return this.has_fixed_chairs;
    }

    /**
     * Gets the catering space flag
     *
     * @return Catering space state or requirement level
     */
    public Trilean hasCateringSpace() {
        return this.has_catering_space;
    }

    /**
     * Gets the whiteboard flag
     *
     * @return Whiteboard  state or requirement level
     */
    public Trilean hasWhiteboard() {
        return this.has_whiteboard;
    }

    /**
     * Gets the projector flag
     *
     * @return Projector state or requirement level
     */
    public Trilean hasProjector() {
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
     * Compares requirement levels with a given state
     *
     * @param requirement Requirement
     * @param given       Given state
     * @return (- 1) when requirement is not met, (0) when requirement is met, (1) when requirement is exceeded
     */
    private int compareRequirementState(Trilean requirement, Trilean given) {
        switch (requirement) {
            case TRUE:
                return given == Trilean.TRUE ? 0 : -1;
            case FALSE:
                return given == Trilean.FALSE ? 0 : -1;
            case UNDEFINED:
                return given == Trilean.TRUE ? 1 : 0;
        }
        throw new RuntimeException("RoomProperty.compareRequirementState( " + requirement + ", " + given + " ) encountered a state not catered for in the switch statement.");
    }

    @Override
    public int compareTo(RoomProperty that) {
        int tally = 0, result;
        if ((result = compareRequirementState(this.hasFixedChairs(), that.hasFixedChairs())) < 0)
            return -1;
        else
            tally += result;
        if ((result = compareRequirementState(this.hasCateringSpace(), that.hasCateringSpace())) < 0)
            return -1;
        else
            tally += result;
        if ((result = compareRequirementState(this.hasWhiteboard(), that.hasWhiteboard())) < 0)
            return -1;
        else
            tally += result;
        if ((result = compareRequirementState(this.hasProjector(), that.hasProjector())) < 0)
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
                capacity.equals(that.capacity) &&
                dimension.equals(that.dimension);
    }

    @Override
    public int hashCode() {
        return Objects.hash(has_fixed_chairs, has_catering_space, has_whiteboard, has_projector, capacity, dimension);
    }
}
