package bbk_beam.mtRooms.ui.model.administration;

import bbk_beam.mtRooms.admin.dto.Account;
import bbk_beam.mtRooms.admin.dto.AccountType;
import javafx.beans.property.*;

import java.text.SimpleDateFormat;
import java.util.Date;

public class UserAccountItem {
    static private final String CHECK = "\u2713";
    static private final String CROSS = "\u274C";
    static private final SimpleDateFormat dateFormat = new SimpleDateFormat("YYYY/MM/dd - HH:mm:ss");
    private Account account;
    private IntegerProperty id = new SimpleIntegerProperty();
    private StringProperty username = new SimpleStringProperty();
    private StringProperty created = new SimpleStringProperty();
    private StringProperty last_login = new SimpleStringProperty();
    private StringProperty last_pwd_change = new SimpleStringProperty();
    private ObjectProperty<AccountType> accountType = new SimpleObjectProperty<>();
    private StringProperty is_active = new SimpleStringProperty();

    /**
     * Constructor
     *
     * @param account Account DTO from backend services
     */
    public UserAccountItem(Account account) {
        this.account = account;
        this.id.set(account.id());
        this.username.set(account.username());
        this.created.set(dateFormat.format(account.created()));
        this.last_login.set(dateFormat.format(account.lastLogin()));
        this.last_pwd_change.set(dateFormat.format(account.lastPwdChange()));
        this.accountType.set(account.type());
        this.is_active.set(account.isActive() ? CHECK : CROSS);
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
    public UserAccountItem(Integer id, String username, Date created, Date last_login, Date last_pwd_change, AccountType type, boolean is_active) {
        this.account = new Account(id, username, created, last_login, last_pwd_change, type, is_active);
        this.id.set(id);
        this.username.set(username);
        this.created.set(dateFormat.format(created));
        this.last_login.set(dateFormat.format(last_login));
        this.last_pwd_change.set(dateFormat.format(last_pwd_change));
        this.accountType.setValue(type);
        this.is_active.set(is_active ? CHECK : CROSS);
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
    public StringProperty createdProperty() {
        return created;
    }

    /**
     * Gets the account's last login date property
     *
     * @return Last login date property
     */
    public StringProperty LastLoginProperty() {
        return last_login;
    }

    /**
     * Gets the last password change date property
     *
     * @return Last password change date property
     */
    public StringProperty lastPwdChangeProperty() {
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
        return this.account.isActive();
    }

    /**
     * Gets the active status property
     *
     * @return Active status property
     */
    public StringProperty isActiveProperty() {
        return is_active;
    }
}
