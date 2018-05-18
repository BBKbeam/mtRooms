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

    public Integer getId() {
        return id.get();
    }

    public IntegerProperty idProperty() {
        return id;
    }

    public String getUsername() {
        return username.get();
    }

    public StringProperty usernameProperty() {
        return username;
    }

    public Date getCreated() {
        return created.get();
    }

    public ObjectProperty<Date> createdProperty() {
        return created;
    }

    public void setCreated(Date created) {
        this.created.set(created);
    }

    public Date getLastLogin() {
        return last_login.get();
    }

    public ObjectProperty<Date> LastLoginProperty() {
        return last_login;
    }

    public void setLastLogin(Date last_login) {
        this.last_login.set(last_login);
    }

    public Date getLastPwdChange() {
        return last_pwd_change.get();
    }

    public ObjectProperty<Date> lastPwdChangeProperty() {
        return last_pwd_change;
    }

    public void setLastPwdChange(Date last_pwd_change) {
        this.last_pwd_change.set(last_pwd_change);
    }

    public AccountType getAccountType() {
        return this.accountType.get();
    }

    public Integer getAccountTypeID() {
        return this.accountType.get().id();
    }

    public String getAccountTypeDesc() {
        return accountType.get().description();
    }

    public ObjectProperty<String> accountTypeDescProperty() {
        return new SimpleObjectProperty<>(this.accountType.get().description());
    }

    public void setAccountType(AccountType type) {
        this.accountType.set(type);
    }

    public boolean isActive() {
        return is_active.get();
    }

    public BooleanProperty isActiveProperty() {
        return is_active;
    }

    public void setActiveFlag(boolean is_active) {
        this.is_active.set(is_active);
    }
}
