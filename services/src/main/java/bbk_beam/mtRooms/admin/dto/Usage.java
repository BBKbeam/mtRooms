package bbk_beam.mtRooms.admin.dto;

import java.io.Serializable;
import java.util.*;

public class Usage<DTO, UsageType> implements Serializable {
    private DTO dto;
    private Set<UsageType> usage_list;

    /**
     * Constructor
     *
     * @param dto DTO type
     */
    public Usage(DTO dto) {
        this.dto = dto;
        this.usage_list = new HashSet<>();
    }

    /**
     * Gets the DTO
     *
     * @return DTO
     */
    public DTO dto() {
        return this.dto;
    }

    /**
     * Gets the collection of users of the DTO
     *
     * @return User list
     */
    public Collection<UsageType> usage() {
        return Collections.unmodifiableCollection(this.usage_list);
    }

    /**
     * Adds a DTO user to the set
     *
     * @param user User to addUsage
     */
    public void addUsage(UsageType user) {
        this.usage_list.add(user);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Usage<?, ?> usage = (Usage<?, ?>) o;
        return Objects.equals(dto, usage.dto) &&
                Objects.equals(usage_list, usage.usage_list);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dto, usage_list);
    }

    @Override
    public String toString() {
        return "Usage{ " + dto + ", usage: " + usage_list.toString() + " }";
    }
}
