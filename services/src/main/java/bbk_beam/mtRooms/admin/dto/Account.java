package bbk_beam.mtRooms.admin.dto;

import java.io.Serializable;
import java.util.Date;

public class Account implements Serializable {
    private Integer id;
    private String username;
    private Date created;
    private Date last_login;
    private Date last_pwd_change;
    private AccountType type;
    private boolean is_active;

    /**
     * Constructor
     *
     * @param id              Account ID
     * @param username        Username
     * @param created         Created timestamp
     * @param last_login      Last login timestamp
     * @param last_pwd_change Last password change timestamp
     * @param type            Account type
     * @param is_active       Active state of the account
     */
    public Account(
            Integer id,
            String username,
            Date created,
            Date last_login,
            Date last_pwd_change,
            AccountType type,
            boolean is_active) {
        this.id = id;
        this.username = username;
        this.created = created;
        this.last_login = last_login;
        this.last_pwd_change = last_pwd_change;
        this.type = type;
        this.is_active = is_active;
    }

    /**
     * Gets the account ID
     *
     * @return ID
     */
    public Integer id() {
        return id;
    }

    /**
     * Gets the account username
     *
     * @return Username
     */
    public String username() {
        return username;
    }

    /**
     * Gets the account creation timestamp
     *
     * @return Creation timestamp
     */
    public Date created() {
        return created;
    }

    /**
     * Gets the account's last login timestamp
     *
     * @return Last login timestamp
     */
    public Date lastLogin() {
        return last_login;
    }

    /**
     * Gets the account's last password change timestamp
     *
     * @return Last password change timestamp
     */
    public Date lastPwdChange() {
        return last_pwd_change;
    }

    /**
     * Gets the account type
     *
     * @return Account type
     */
    public AccountType type() {
        return type;
    }

    /**
     * Gets the account active state
     *
     * @return Active state
     */
    public boolean isActive() {
        return is_active;
    }

    @Override
    public String toString() {
        return "Account={ " + "id: " + id +
                ", username: '" + username + '\'' +
                ", created: " + created +
                ", last_login: " + last_login +
                ", last_pwd_change: " + last_pwd_change +
                ", type: " + type +
                ", is_active: " + is_active +
                " }";
    }
}
