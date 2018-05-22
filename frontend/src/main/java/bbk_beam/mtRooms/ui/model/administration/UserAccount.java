package bbk_beam.mtRooms.ui.model.administration;

import bbk_beam.mtRooms.admin.dto.Account;
import bbk_beam.mtRooms.admin.dto.AccountType;
import javafx.beans.property.*;

import java.util.Date;

public class UserAccount {
    private IntegerProperty id;
    private StringProperty username;
    private ObjectProperty<Date> created;
    private ObjectProperty<Date> last_login;
    private ObjectProperty<Date> last_pwd_change;
    private ObjectProperty<AccountType> accountType;
    private BooleanProperty is_active;

    /**
     * Constructor
     *
     * @param account Account DTO from backend services
     */
    public UserAccount(Account account) {
        this.id = new SimpleIntegerProperty(account.id());
        this.username = new SimpleStringProperty(account.username());
        this.created = new SimpleObjectProperty<>(account.created());
        this.last_login = new SimpleObjectProperty<>(account.lastLogin());
        this.last_pwd_change = new SimpleObjectProperty<>(account.lastPwdChange());
        this.accountType = new SimpleObjectProperty<>(account.type());
        this.is_active = new SimpleBooleanProperty(account.isActive());
    }

    /**
     * Constructor
     *
     * @param id              Account ID
     * @param username        Username
     * @param created         Created timestamp
     * @param last_login      Last login timestamp
     * @param last_pwd_change Last password change timestamp
     * @param type            Account type_desc DTO
     * @param is_active       Active account flag
     */
    public UserAccount(Integer id, String username, Date created, Date last_login, Date last_pwd_change, AccountType type, boolean is_active) {
        this.id = new SimpleIntegerProperty(id);
        this.username = new SimpleStringProperty(username);
        this.created = new SimpleObjectProperty<>(created);
        this.last_login = new SimpleObjectProperty<>(last_login);
        this.last_pwd_change = new SimpleObjectProperty<>(last_pwd_change);
        this.accountType = new SimpleObjectProperty<>(type);
        this.is_active = new SimpleBooleanProperty(is_active);
    }

    /**
     * Gets the account ID
     *
     * @return ID
     */
    public Integer getId() {
        return id.get();
    }

    /**
     * Gets the account ID property
     *
     * @return ID property
     */
    public IntegerProperty idProperty() {
        return id;
    }

    /**
     * Gets the account's username
     *
     * @return Username
     */
    public String getUsername() {
        return username.get();
    }

    /**
     * Gets the account's username property
     *
     * @return Username property
     */
    public StringProperty usernameProperty() {
        return username;
    }

    /**
     * Gets the account's creation date property
     *
     * @return Creation date property
     */
    public ObjectProperty<Date> createdProperty() {
        return created;
    }

    /**
     * Gets the account's last login date property
     *
     * @return Last login date property
     */
    public ObjectProperty<Date> LastLoginProperty() {
        return last_login;
    }

    /**
     * Gets the last password change date property
     *
     * @return Last password change date property
     */
    public ObjectProperty<Date> lastPwdChangeProperty() {
        return last_pwd_change;
    }

    /**
     * Gets the account's type
     *
     * @return AccountType DTO
     */
    public AccountType getAccountType() {
        return this.accountType.get();
    }

    /**
     * Gets the account's type property
     *
     * @return AccountType DTO property
     */
    public ObjectProperty<String> accountTypeDescProperty() {
        return new SimpleObjectProperty<>(this.accountType.get().description());
    }

    /**
     * Gets the active status of the account
     *
     * @return Active status
     */
    public boolean isActive() {
        return is_active.get();
    }

    /**
     * Gets the active status property
     *
     * @return Active status property
     */
    public BooleanProperty isActiveProperty() {
        return is_active;
    }
}
