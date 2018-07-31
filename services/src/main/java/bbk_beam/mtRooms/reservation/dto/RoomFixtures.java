package bbk_beam.mtRooms.reservation.dto;

import java.io.Serializable;
import java.util.Objects;
import java.util.Stack;

public class RoomFixtures implements Serializable {
    private Integer id;
    private boolean fixed_chairs;
    private boolean catering_space;
    private boolean whiteboard;
    private boolean projector;

    /**
     * Constructor
     *
     * @param id             ID
     * @param fixed_chairs   Fixed chairs flag
     * @param catering_space Catering space flag
     * @param whiteboard     Whiteboard flag
     * @param projector      Projector flag
     */
    public RoomFixtures(
            Integer id,
            boolean fixed_chairs,
            boolean catering_space,
            boolean whiteboard,
            boolean projector) {
        this.id = id;
        this.fixed_chairs = fixed_chairs;
        this.catering_space = catering_space;
        this.whiteboard = whiteboard;
        this.projector = projector;
    }

    /**
     * Gets the RoomFixtures's ID
     *
     * @return ID
     */
    public Integer id() {
        return this.id;
    }

    /**
     * Gets the fixed chairs flag
     *
     * @return Fixed chairs state
     */
    public boolean hasFixedChairs() {
        return this.fixed_chairs;
    }

    /**
     * Gets the catering space flag
     *
     * @return Catering space state
     */
    public boolean hasCateringSpace() {
        return this.catering_space;
    }

    /**
     * Gets the whiteboard flag
     *
     * @return Whiteboard state
     */
    public boolean hasWhiteboard() {
        return this.whiteboard;
    }

    /**
     * Gets the projector flag
     *
     * @return Projector state
     */
    public boolean hasProjector() {
        return this.projector;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RoomFixtures that = (RoomFixtures) o;
        return id == that.id &&
                fixed_chairs == that.fixed_chairs &&
                catering_space == that.catering_space &&
                whiteboard == that.whiteboard &&
                projector == that.projector;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, fixed_chairs, catering_space, whiteboard, projector);
    }

    @Override
    public String toString() {
        Stack<String> flags = new Stack<>();
        if (fixed_chairs)
            flags.push("fixed chairs ");
        if (catering_space)
            flags.push("catering space ");
        if (whiteboard)
            flags.push("whiteboard ");
        if (projector)
            flags.push("projector ");
        StringBuilder sb = new StringBuilder("RoomFixture[").append(id).append("]={ ");
        while (!flags.empty()) {
            sb.append(flags.pop());
            if (!flags.empty())
                sb.append(", ");
        }
        sb.append("}");
        return sb.toString();
    }
}
