package bbk_beam.mtRooms.ui.controller.administration;

import bbk_beam.mtRooms.MtRoomsGUI;
import bbk_beam.mtRooms.ui.model.SessionManager;
import bbk_beam.mtRooms.ui.model.administration.UserAccount;
import bbk_beam.mtRooms.ui.model.administration.UserAccountTable;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class AdministrationController implements Initializable {
    public TableView<UserAccount> account_table; //UI Table
    public TableColumn<UserAccount, Integer> id_col;
    public TableColumn<UserAccount, String> username_col;
    public TableColumn<UserAccount, String> created_col;
    public TableColumn<UserAccount, String> login_col;
    public TableColumn<UserAccount, String> pwd_change_col;
    public TableColumn<UserAccount, String> type_col;
    public TableColumn<UserAccount, Boolean> active_col;
    private UserAccountTable userAccountTable; //Model
    private SessionManager sessionManager;
    //add/edit user should overlay admin view. cancel/add/ok should go back to admin view on those

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        id_col.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
        username_col.setCellValueFactory(cellData -> cellData.getValue().usernameProperty());
        created_col.setCellValueFactory(cellData -> cellData.getValue().createdProperty().asString());
        login_col.setCellValueFactory(cellData -> cellData.getValue().LastLoginProperty().asString());
        pwd_change_col.setCellValueFactory(cellData -> cellData.getValue().lastPwdChangeProperty().asString());
        created_col.setCellValueFactory(cellData -> cellData.getValue().createdProperty().asString());
        type_col.setCellValueFactory(cellData -> cellData.getValue().accountTypeDescProperty().asString());
        active_col.setCellValueFactory(cellData -> cellData.getValue().isActiveProperty().asObject());
    }

    /**
     * Sets the session manager for the controller
     *
     * @param sessionManager SessionManager instance
     */
    public void setSessionManager(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
        this.userAccountTable = new UserAccountTable(this.sessionManager);
    }

    /**
     * Loads the model into the UI table
     */
    public void loadAccountTable() {
        account_table.setItems(this.userAccountTable.getUserData());
    }

    /**
     * Opens the edit dialog window for a user account
     *
     * @param event       ActionEvent
     * @param userAccount UserAccount DTO to edit
     */
    public void openEditDialog(ActionEvent event, UserAccount userAccount) {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(MtRoomsGUI.class.getResource("/view/administration/UserAccountView.fxml"));
        try {
            Stage stage = new Stage();
            stage.setTitle("Edit user account");
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(((Node) event.getTarget()).getScene().getWindow());
            stage.setScene(new Scene(loader.load()));
            UserAccountController userAccountController = loader.getController();
            userAccountController.setSessionManager(this.sessionManager);
            if( userAccount != null )
                userAccountController.setEditAccountFields(userAccount);
            else
                userAccountController.setNewAccountFields();
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleEditAction(ActionEvent actionEvent) {
        UserAccount userAccount = this.account_table.getSelectionModel().getSelectedItem();
        openEditDialog(actionEvent, userAccount);
    }

    @FXML
    public void handleNewAction(ActionEvent actionEvent) {
        openEditDialog(actionEvent, null);
    }
}
