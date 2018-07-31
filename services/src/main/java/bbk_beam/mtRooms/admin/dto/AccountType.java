package bbk_beam.mtRooms.admin.dto;

import bbk_beam.mtRooms.db.session.SessionType;

import java.io.Serializable;
import java.util.Objects;

public class AccountType implements Serializable {
    private Integer id;
    private String description;
    private SessionType sessionType;

    /**
     * Constructor
     *
     * @param id          Type ID
     * @param description Type description
     */
    public AccountType(Integer id, String description) {
        this.id = id;
        this.description = description;
    }

    /**
     * Gets the type ID
     *
     * @return ID of type
     */
    public Integer id() {
        return this.id;
    }

    /**
     * Gets the type description
     *
     * @return Description
     */
    public String description() {
        return this.description;
    }

    /**
     * Gets the SessionType of the AccountType
     *
     * @return SessionType
     */
    public SessionType getSessionType() {
        return SessionType.valueOf(this.description);
    }

    @Override
    public String toString() {
        return description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AccountType that = (AccountType) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, description);
    }
}
