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
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.rmi.RemoteException;
import java.util.List;

public class UserAccountController {
    private final Logger log = Logger.getLoggerInstance(UserAccountController.class.getName());

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
     */
    private void saveNewAccount() {
        log.log("New account creation required...");
        //TODO check fields (username 3+char, pwd 6+char)
        String username = this.username_field.getText();
        String password = this.pwd_field.getText();
        AccountType account_type = this.accountType_choiceBox.getValue();
        IRmiServices services = this.sessionManager.getServices();
        try {
            services.createNewAccount(
                    this.sessionManager.getToken(),
                    account_type.getSessionType(),
                    username,
                    password
            );
        } catch (Unauthorised unauthorised) {
            unauthorised.printStackTrace();
        } catch (AccountExistenceException e) {
            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (LoginException e) {
            e.printStackTrace();
        }
    }

    /**
     * Saves to records account modifications
     */
    private void saveModifiedAccount() {
        IRmiServices services = this.sessionManager.getServices();
        try {
            if (!this.pwd_field.getText().isEmpty()) {
                log.log("Password change required...");
                services.updateAccountPassword(
                        this.sessionManager.getToken(),
                        userAccount.getId(),
                        this.pwd_field.getText()
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
        } catch (AccountExistenceException e) {
            e.printStackTrace();
        } catch (AccountOverrideException e) {
            e.printStackTrace();
        } catch (LoginException e) {
            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (Unauthorised unauthorised) {
            unauthorised.printStackTrace();
        }
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
        switch (this.scenarioType) {
            case NEW_ACCOUNT:
                saveNewAccount();
                break;
            case EDIT_ACCOUNT:
                saveModifiedAccount();
                break;
        }
        Stage stage = (Stage) this.saveButton.getScene().getWindow();
        stage.close();
    }
}
