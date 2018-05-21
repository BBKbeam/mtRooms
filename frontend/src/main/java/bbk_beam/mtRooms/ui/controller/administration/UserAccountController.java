package bbk_beam.mtRooms.ui.controller.administration;

import bbk_beam.mtRooms.admin.dto.AccountType;
import bbk_beam.mtRooms.admin.exception.AccountExistenceException;
import bbk_beam.mtRooms.admin.exception.AccountOverrideException;
import bbk_beam.mtRooms.exception.LoginException;
import bbk_beam.mtRooms.network.IRmiServices;
import bbk_beam.mtRooms.network.exception.Unauthorised;
import bbk_beam.mtRooms.ui.model.SessionManager;
import bbk_beam.mtRooms.ui.model.administration.UserAccount;
import eadjlib.logger.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.rmi.RemoteException;
import java.util.List;

//TODO handle exceptions with error box
//TODO refresh main view after save
public class UserAccountController {
    private final Logger log = Logger.getLoggerInstance(UserAccountController.class.getName());
    private final int MIN_PWD_LENGTH = 6;
    private final int MIN_USERNAME_LENGTH = 4;

    public enum ScenarioType {EDIT_ACCOUNT, NEW_ACCOUNT}

    private SessionManager sessionManager;
    private ScenarioType scenarioType;
    private UserAccount userAccount;
    public TextField username_field;
    public TextField pwd_field;
    public ChoiceBox<AccountType> accountType_choiceBox;
    public CheckBox active_field;
    public Button cancelButton;
    public Button saveButton;

    /**
     * Helper method for showing an alert dialog
     *
     * @param msg Message to print in the dialog
     */
    private void showErrorAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("User account error");
        alert.setHeaderText(msg);
        alert.showAndWait();
    }

    /**
     * Loader helper for account type choice box
     */
    private void loadAccountTypeChoiceBox() {
        IRmiServices services = this.sessionManager.getServices();
        try {
            List<AccountType> accountTypeList = services.getAccountTypes(this.sessionManager.getToken());
            ObservableList<AccountType> accountTypeObservableList = FXCollections.observableList(accountTypeList);
            this.accountType_choiceBox.setItems(accountTypeObservableList);
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (LoginException e) {
            e.printStackTrace();
        } catch (Unauthorised unauthorised) {
            unauthorised.printStackTrace();
        }
    }

    /**
     * Saves to records new account
     *
     * @return Success
     */
    private boolean saveNewAccount() {
        log.log("New account creation required...");
        String username = this.username_field.getText();
        String password = this.pwd_field.getText();
        AccountType account_type = this.accountType_choiceBox.getValue();
        if( username.length() < MIN_USERNAME_LENGTH ) {
            showErrorAlert("Username too short.\nMinimum char length required: " + MIN_USERNAME_LENGTH);
            return false;
        }
        if( password.length() < MIN_PWD_LENGTH ) {
            showErrorAlert("Password too short.\nMinimum number of characters required: " + MIN_PWD_LENGTH);
            return false;
        }
        IRmiServices services = this.sessionManager.getServices();
        try {
            services.createNewAccount(
                    this.sessionManager.getToken(),
                    account_type.getSessionType(),
                    username,
                    password
            );
            return true;
        } catch (Unauthorised unauthorised) {
            showErrorAlert("Current user session is not authorised to execute this action.");
        } catch (AccountExistenceException e) {
            showErrorAlert("An account with the same username ('" + username + "') already exists in the records.");
        } catch (RemoteException e) {
            showErrorAlert("A connection/remote issue has occurred.");
        } catch (LoginException e) {
            showErrorAlert("Currently not logged-in. Log-in again.");
        }
        return false;
    }

    /**
     * Saves to records account modifications
     *
     * @return Success
     */
    private boolean saveModifiedAccount() {
        IRmiServices services = this.sessionManager.getServices();
        try {
            if (!this.pwd_field.getText().isEmpty()) {
                String password = this.pwd_field.getText();
                if(password.length() < MIN_PWD_LENGTH) {
                    showErrorAlert("Password too short.\nMinimum number of characters required: " + MIN_PWD_LENGTH);
                    return false;
                }
                services.updateAccountPassword(
                        this.sessionManager.getToken(),
                        userAccount.getId(),
                        password
                );
            }
            if (this.userAccount.isActive() != this.active_field.isSelected()) {
                log.log("Active status change required...");
                if (this.active_field.isSelected())
                    services.activateAccount(
                            this.sessionManager.getToken(),
                            this.userAccount.getId()
                    );
                else
                    services.deactivateAccount(
                            this.sessionManager.getToken(),
                            this.userAccount.getId()
                    );
            }
            return true;
        } catch (AccountExistenceException e) {
            showErrorAlert("This account (u/n: '" + userAccount.getUsername() + "') does not exist in the records.");
        } catch (AccountOverrideException e) {
            showErrorAlert("Password is the same as the old one.");
        } catch (LoginException e) {
            showErrorAlert("Currently not logged-in. Log-in again.");
        } catch (RemoteException e) {
            showErrorAlert("A connection/remote issue has occurred.");
        } catch (Unauthorised unauthorised) {
            showErrorAlert("Current user session is not authorised to execute this action.");
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
     * @param userAccount UserAccount DTO
     */
    void setEditAccountFields(UserAccount userAccount) {
        this.userAccount = userAccount;
        this.scenarioType = ScenarioType.EDIT_ACCOUNT;
        this.username_field.setText(userAccount.getUsername());
        this.username_field.setDisable(true);
        this.pwd_field.setPromptText("type to modify password.");
        this.active_field.setDisable(false);
        this.active_field.setSelected(userAccount.isActive());
        loadAccountTypeChoiceBox();
        this.accountType_choiceBox.getSelectionModel().select(userAccount.getAccountType());
        this.accountType_choiceBox.setDisable(true);
    }

    /**
     * Sets the fields for a new account scenario
     */
    void setNewAccountFields() {
        this.userAccount = null;
        this.scenarioType = ScenarioType.NEW_ACCOUNT;
        this.username_field.setDisable(false);
        this.pwd_field.clear();
        this.pwd_field.setPromptText("");
        loadAccountTypeChoiceBox();
        this.accountType_choiceBox.getSelectionModel().select(1);
        this.accountType_choiceBox.setDisable(false);
        this.active_field.setSelected(true);
        this.active_field.setDisable(true);
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
