package bbk_beam.mtRooms.admin.authentication;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

public class Token implements Serializable {
    private String session_id;
    private Date created;
    private Date expiry;

    /**
     * Constructor
     *
     * @param id       Session ID
     * @param creation Token creation timestamp
     * @param expiry   Token expiry timestamp
     */
    public Token(String id, Date creation, Date expiry) {
        this.session_id = id;
        this.created = creation;
        this.expiry = expiry;
    }

    /**
     * Gets the session id for the token
     *
     * @return Session ID
     */
    public String getSessionId() {
        return session_id;
    }

    /**
     * Gets the creation timestamp of the token
     *
     * @return Creation timestamp
     */
    public Date getCreated() {
        return created;
    }

    /**
     * Gets the expiry timestamp of the token
     *
     * @return Expiry timestamp
     */
    public Date getExpiry() {
        return expiry;
    }

    @Override
    public String toString() {
        return this.session_id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Token token = (Token) o;
        return Objects.equals(session_id, token.session_id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(session_id);
    }
}
