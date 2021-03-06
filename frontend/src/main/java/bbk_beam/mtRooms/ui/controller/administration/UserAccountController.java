package bbk_beam.mtRooms.ui.controller.administration;

import bbk_beam.mtRooms.admin.dto.AccountType;
import bbk_beam.mtRooms.admin.exception.AccountExistenceException;
import bbk_beam.mtRooms.admin.exception.AccountOverrideException;
import bbk_beam.mtRooms.exception.LoginException;
import bbk_beam.mtRooms.network.IRmiAdministrationServices;
import bbk_beam.mtRooms.network.exception.Unauthorised;
import bbk_beam.mtRooms.ui.controller.common.AlertDialog;
import bbk_beam.mtRooms.ui.model.SessionManager;
import bbk_beam.mtRooms.ui.model.administration.UserAccountItem;
import eadjlib.logger.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.rmi.RemoteException;
import java.util.List;
import java.util.ResourceBundle;

public class UserAccountController implements Initializable {
    private final Logger log = Logger.getLoggerInstance(UserAccountController.class.getName());
    private final int MIN_PWD_LENGTH = 6;
    private final int MIN_USERNAME_LENGTH = 4;

    public enum ScenarioType {EDIT_ACCOUNT, NEW_ACCOUNT}

    private AlertDialog alertDialog;
    private SessionManager sessionManager;
    private ScenarioType scenarioType;
    private UserAccountItem userAccountItem;
    private ResourceBundle resourceBundle;
    public TextField username_field;
    public TextField pwd_field;
    public ChoiceBox<AccountType> accountType_choiceBox;
    public CheckBox active_field;
    public Button cancelButton;
    public Button saveButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.resourceBundle = resources;
        this.alertDialog = new AlertDialog(resources);
    }

    /**
     * Loader helper for account type choice box
     *
     * @throws LoginException  when method is used outside a session
     * @throws Unauthorised    when client is not authorised to access the resource
     * @throws RemoteException when network issues occur during the remote call
     */
    private void loadAccountTypeChoiceBox() throws LoginException, Unauthorised, RemoteException {
        IRmiAdministrationServices services = this.sessionManager.getAdministrationServices();
        List<AccountType> accountTypeList = services.getAccountTypes(this.sessionManager.getToken());
        ObservableList<AccountType> accountTypeObservableList = FXCollections.observableList(accountTypeList);
        this.accountType_choiceBox.setItems(accountTypeObservableList);
    }

    /**
     * Saves to records new account
     *
     * @return Success
     */
    private boolean saveNewAccount() {
        String username = this.username_field.getText();
        String password = this.pwd_field.getText();
        AccountType account_type = this.accountType_choiceBox.getValue();
        if (username.length() < MIN_USERNAME_LENGTH) {
            String msg = this.resourceBundle.getString("ErrorMsg_UsernameTooShort") +
                    "\n" +
                    this.resourceBundle.getString("InfoMsg_UsernameMinLength") +
                    " " +
                    MIN_USERNAME_LENGTH;
            AlertDialog.showAlert(
                    Alert.AlertType.ERROR,
                    this.resourceBundle.getString("ErrorDialogTitle_UserAccount"),
                    msg
            );
            return false;
        }
        if (password.length() < MIN_PWD_LENGTH) {
            String msg = this.resourceBundle.getString("ErrorMsg_PwdTooShort") +
                    "\n" +
                    this.resourceBundle.getString("InfoMsg_PwdMinLength") +
                    " " +
                    MIN_PWD_LENGTH;
            AlertDialog.showAlert(
                    Alert.AlertType.ERROR,
                    this.resourceBundle.getString("ErrorDialogTitle_UserAccount"),
                    msg
            );
            return false;
        }
        IRmiAdministrationServices services = this.sessionManager.getAdministrationServices();
        try {
            services.createNewAccount(
                    this.sessionManager.getToken(),
                    account_type.getSessionType(),
                    username,
                    password
            );
            return true;
        } catch (Unauthorised e) {
            this.alertDialog.showGenericError(e);
        } catch (AccountExistenceException e) {
            AlertDialog.showAlert(
                    Alert.AlertType.ERROR,
                    this.resourceBundle.getString("ErrorDialogTitle_UserAccount"),
                    this.resourceBundle.getString("ErrorMsg_UserAccountOverride")
            );
        } catch (RemoteException e) {
            this.alertDialog.showGenericError(e);
        } catch (LoginException e) {
            this.alertDialog.showGenericError(e);
        }
        return false;
    }

    /**
     * Saves to records account modifications
     *
     * @return Success
     */
    private boolean saveModifiedAccount() {
        IRmiAdministrationServices services = this.sessionManager.getAdministrationServices();
        try {
            if (!this.pwd_field.getText().isEmpty()) {
                String password = this.pwd_field.getText();
                if (password.length() < MIN_PWD_LENGTH) {
                    AlertDialog.showAlert(
                            Alert.AlertType.ERROR,
                            this.resourceBundle.getString("ErrorDialogTitle_UserAccount"),
                            this.resourceBundle.getString("ErrorMsg_PwdTooShort") + "\n" +
                                    this.resourceBundle.getString("InfoMsg_PwdMinLength") + MIN_PWD_LENGTH
                    );
                    return false;
                }
                try {
                    services.updateAccountPassword(
                            this.sessionManager.getToken(),
                            userAccountItem.getId(),
                            password
                    );
                } catch (AccountOverrideException e) {
                    AlertDialog.showAlert(
                            Alert.AlertType.ERROR,
                            this.resourceBundle.getString("ErrorDialogTitle_UserAccount"),
                            this.resourceBundle.getString("ErrorMsg_SamePwdOverride")
                    );
                    return false;
                }
            }
            if (this.userAccountItem.isActive() != this.active_field.isSelected()) {
                try {
                    if (this.active_field.isSelected()) {
                        services.activateAccount(
                                this.sessionManager.getToken(),
                                this.userAccountItem.getId()
                        );
                    } else
                        services.deactivateAccount(
                                this.sessionManager.getToken(),
                                this.userAccountItem.getId()
                        );
                } catch (AccountOverrideException e) {
                    AlertDialog.showExceptionAlert(
                            this.resourceBundle.getString("ErrorDialogTitle_UserAccount"),
                            this.resourceBundle.getString("ErrorMsg_CurrentUserOverride"),
                            e
                    );
                    return false;
                }
            }
            return true;
        } catch (AccountExistenceException e) {
            AlertDialog.showAlert(
                    Alert.AlertType.ERROR,
                    this.resourceBundle.getString("ErrorDialogTitle_UserAccount"),
                    this.resourceBundle.getString("ErrorMsg_InvalidUserAccount")
            );
        } catch (LoginException e) {
            this.alertDialog.showGenericError(e);
        } catch (RemoteException e) {
            this.alertDialog.showGenericError(e);
        } catch (Unauthorised e) {
            this.alertDialog.showGenericError(e);
        }
        return false;
    }

    /**
     * Sets the session manager for the controller
     *
     * @param sessionManager SessionManager instance
     */
    void setSessionManager(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    /**
     * Sets the fields for an edit account scenario
     *
     * @param userAccountItem UserAccountItem DTO
     */
    void setEditAccountFields(UserAccountItem userAccountItem) throws LoginException, Unauthorised, RemoteException {
        this.userAccountItem = userAccountItem;
        this.scenarioType = ScenarioType.EDIT_ACCOUNT;
        this.username_field.setText(userAccountItem.getUsername());
        this.username_field.setDisable(true);
        this.username_field.setFocusTraversable(false);
        this.pwd_field.setPromptText(this.resourceBundle.getString("PromptText_UserAccountPwd"));
        loadAccountTypeChoiceBox();
        this.accountType_choiceBox.getSelectionModel().select(userAccountItem.getAccountType());
        this.accountType_choiceBox.setDisable(true);
        this.accountType_choiceBox.setFocusTraversable(false);
        this.active_field.setDisable(false);
        this.active_field.setSelected(userAccountItem.isActive());
        this.active_field.setFocusTraversable(true);
    }

    /**
     * Sets the fields for a new account scenario
     */
    void setNewAccountFields() throws LoginException, Unauthorised, RemoteException {
        this.userAccountItem = null;
        this.scenarioType = ScenarioType.NEW_ACCOUNT;
        this.username_field.setDisable(false);
        this.username_field.setFocusTraversable(true);
        this.username_field.requestFocus();
        this.pwd_field.clear();
        this.pwd_field.setPromptText("");
        loadAccountTypeChoiceBox();
        this.accountType_choiceBox.getSelectionModel().select(1);
        this.accountType_choiceBox.setDisable(false);
        this.accountType_choiceBox.setFocusTraversable(true);
        this.active_field.setSelected(true);
        this.active_field.setDisable(true);
        this.active_field.setFocusTraversable(false);
    }

    @FXML
    public void handleCancelAction(ActionEvent actionEvent) {
        Stage stage = (Stage) this.cancelButton.getScene().getWindow();
        stage.close();
    }

    @FXML
    public void handleSaveAction(ActionEvent actionEvent) {
        Stage stage = (Stage) this.saveButton.getScene().getWindow();
        switch (this.scenarioType) {
            case NEW_ACCOUNT:
                if (saveNewAccount())
                    stage.close();
                break;
            case EDIT_ACCOUNT:
                if (saveModifiedAccount())
                    stage.close();
                break;
        }
    }
}
