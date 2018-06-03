package bbk_beam.mtRooms.ui.controller.administration;

import bbk_beam.mtRooms.MtRoomsGUI;
import bbk_beam.mtRooms.exception.LoginException;
import bbk_beam.mtRooms.network.exception.Unauthorised;
import bbk_beam.mtRooms.ui.AlertDialog;
import bbk_beam.mtRooms.ui.model.SessionManager;
import bbk_beam.mtRooms.ui.model.administration.UserAccount;
import bbk_beam.mtRooms.ui.model.administration.UserAccountTable;
import eadjlib.logger.Logger;
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
import java.rmi.RemoteException;
import java.util.ResourceBundle;

public class AdministrationController implements Initializable {
    private final Logger log = Logger.getLoggerInstance(AdministrationController.class.getName());
    public TableView<UserAccount> account_table; //UI Table
    public TableColumn<UserAccount, Integer> id_col;
    public TableColumn<UserAccount, String> username_col;
    public TableColumn<UserAccount, String> created_col;
    public TableColumn<UserAccount, String> login_col;
    public TableColumn<UserAccount, String> pwd_change_col;
    public TableColumn<UserAccount, String> type_col;
    public TableColumn<UserAccount, Boolean> active_col;
    private AlertDialog alertDialog;
    private ResourceBundle resourceBundle;
    private UserAccountTable userAccountTable; //Model
    private SessionManager sessionManager;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.resourceBundle = resources;
        this.alertDialog = new AlertDialog(resources);

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
     * @throws LoginException  when method is used outside a session
     * @throws Unauthorised    when client is not authorised to access the resource
     * @throws RemoteException when network issues occur during the remote call
     */
    public void setSessionManager(SessionManager sessionManager) throws LoginException, Unauthorised, RemoteException {
        this.sessionManager = sessionManager;
        this.userAccountTable = new UserAccountTable(this.sessionManager);
    }

    /**
     * Loads the model into the UI table
     */
    public void loadAccountTable() {
        try {
            this.userAccountTable.reloadData();
            this.account_table.setItems(this.userAccountTable.getUserData());
        } catch (LoginException e) {
            this.alertDialog.showGenericError(e);
        } catch (Unauthorised e) {
            this.alertDialog.showGenericError(e);
        } catch (RemoteException e) {
            this.alertDialog.showGenericError(e);
        }
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
        loader.setResources(this.resourceBundle);
        try {
            Stage stage = new Stage();
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(((Node) event.getTarget()).getScene().getWindow());
            stage.setScene(new Scene(loader.load()));
            stage.setOnHidden(e -> loadAccountTable());
            UserAccountController userAccountController = loader.getController();
            userAccountController.setSessionManager(this.sessionManager);
            if (userAccount != null) {
                stage.setTitle("Edit user account");
                userAccountController.setEditAccountFields(userAccount);
            } else {
                stage.setTitle("Add user account");
                userAccountController.setNewAccountFields();
            }
            stage.show();
        } catch (RemoteException e) {
            this.alertDialog.showGenericError(e);
        } catch (IOException e) {
            log.log_Error("Could not load UI scene.");
            log.log_Exception(e);
            this.alertDialog.showGenericError(e);
        } catch (LoginException e) {
            this.alertDialog.showGenericError(e);
        } catch (Unauthorised e) {
            this.alertDialog.showGenericError(e);
        }
    }

    @FXML
    public void handleEditAction(ActionEvent actionEvent) {
        UserAccount userAccount = this.account_table.getSelectionModel().getSelectedItem();
        if (userAccount != null)
            openEditDialog(actionEvent, userAccount);
    }

    @FXML
    public void handleNewAction(ActionEvent actionEvent) {
        openEditDialog(actionEvent, null);
    }
}
