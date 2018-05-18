package bbk_beam.mtRooms.ui.controller.administration;

import bbk_beam.mtRooms.admin.dto.AccountType;
import bbk_beam.mtRooms.exception.LoginException;
import bbk_beam.mtRooms.network.IRmiServices;
import bbk_beam.mtRooms.network.exception.Unauthorised;
import bbk_beam.mtRooms.ui.model.SessionManager;
import bbk_beam.mtRooms.ui.model.administration.UserAccount;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.rmi.RemoteException;
import java.util.List;

public class UserAccountController {
    private SessionManager sessionManager;
    public TextField username_field;
    public TextField pwd_field;
    public ChoiceBox<AccountType> accountType_choiceBox;
    public CheckBox active_field;
    public Button cancelButton;
    public Button saveButton;

    public void setSessionManager(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    private void loadAccoutTypeChoiceBox() {
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

    public void setEditAccountFields(UserAccount userAccount) {
        this.username_field.setText(userAccount.getUsername());
        this.username_field.setDisable(true);
        this.pwd_field.setPromptText("type to modify password.");
        this.active_field.setSelected(userAccount.isActive());
        loadAccoutTypeChoiceBox();
        this.accountType_choiceBox.getSelectionModel().select(userAccount.getAccountType());
    }

    public void setNewAccountFields() {
        this.username_field.setDisable(false);
        this.pwd_field.clear();
        this.pwd_field.setPromptText("");
        loadAccoutTypeChoiceBox();
        this.active_field.setSelected(true);
    }

    public void handleCancelAction(ActionEvent actionEvent) {
        Stage stage = (Stage) this.cancelButton.getScene().getWindow();
        stage.close();
    }

    public void handleSaveAction(ActionEvent actionEvent) {
        //TODO save
        Stage stage = (Stage) this.saveButton.getScene().getWindow();
        stage.close();
    }
}
