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
    public TextField id_field;
    public TextField username_field;
    public TextField created_field;
    public TextField lastLogin_field;
    public TextField lastPwdChange_field;
    public ChoiceBox<AccountType> accountType_comboBox;
    public CheckBox active_field;
    public Button cancelButton;
    public Button saveButton;

    public void setSessionManager(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    public void setFields(UserAccount userAccount) {
        this.id_field.setText(userAccount.getId().toString());
        this.username_field.setText(userAccount.getUsername());
        this.created_field.setText(userAccount.getCreated().toString());
        this.lastLogin_field.setText(userAccount.getLastLogin().toString());
        this.lastPwdChange_field.setText(userAccount.getLastPwdChange().toString());
        this.active_field.setSelected(userAccount.isActive());
        IRmiServices services = this.sessionManager.getServices();
        try {
            List<AccountType> accountTypeList = services.getAccountTypes(this.sessionManager.getToken());
            ObservableList<AccountType> accountTypeObservableList = FXCollections.observableList(accountTypeList);
            this.accountType_comboBox.setItems(accountTypeObservableList);
            this.accountType_comboBox.getSelectionModel().select(userAccount.getAccountType());

        } catch (Unauthorised unauthorised) {
            unauthorised.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (LoginException e) {
            e.printStackTrace();
        }

    }

    public void clearFields() {
        this.id_field.clear();
        this.username_field.clear();
        this.created_field.clear();
        this.lastLogin_field.clear();
        this.lastPwdChange_field.clear();
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

    public void handleChangePwdAction(ActionEvent actionEvent) {
    }
}
