package bbk_beam.mtRooms.ui.controller;

import bbk_beam.mtRooms.exception.RemoteFailure;
import bbk_beam.mtRooms.ui.AlertDialog;
import bbk_beam.mtRooms.ui.model.SessionManager;
import eadjlib.logger.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {
    private final Logger log = Logger.getLoggerInstance(LoginController.class.getName());
    private AlertDialog alertDialog;
    private ResourceBundle resourceBundle;
    private SessionManager sessionManager;
    private MainWindowController mainWindowController;
    public Button validate;
    public TextField username;
    public PasswordField password;

    /**
     * Sets the SessionManager model in the controller
     *
     * @param sessionManager SessionManager instance
     */
    public void setSessionManager(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    /**
     * Sets the parent controller
     *
     * @param mainWindowController MainWindowController instance
     */
    public void setMainWindowController(MainWindowController mainWindowController) {
        this.mainWindowController = mainWindowController;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.resourceBundle = resources;
        this.alertDialog = new AlertDialog(resources);
        this.validate.setDefaultButton(true);
    }

    @FXML
    public void handleValidateAction(ActionEvent actionEvent) {
        boolean valid_flag = true;
        if (username.getCharacters().length() < 1) {
            username.setStyle("-fx-control-inner-background: red;");
            valid_flag = false;
        } else {
            username.setStyle("-fx-control-inner-background: white;");
        }
        if (password.getCharacters().length() < 1) {
            password.setStyle("-fx-control-inner-background: red;");
            valid_flag = false;
        } else {
            password.setStyle("-fx-control-inner-background: white;");
        }
        if (valid_flag) {
            try {
                if (this.sessionManager.login(username.getText(), password.getText())) {
                    username.setStyle("-fx-control-inner-background: white;");
                    password.setStyle("-fx-control-inner-background: white;");
                    this.mainWindowController.logout.setVisible(true);
                    this.mainWindowController.setViewMenuAccessibility();
                    this.mainWindowController.showCustomerSearchPane();
                    log.log("Login OK: " + username.getText());
                } else {
                    username.setStyle("-fx-control-inner-background: red;");
                    password.setStyle("-fx-control-inner-background: red;");
                }
            } catch (RemoteFailure e) {
                log.log_Error("A remote failure occurred...");
                log.log_Exception(e);
                this.alertDialog.showGenericError(e);
            }
        }
    }

    @FXML
    public void handleClearAction(ActionEvent actionEvent) {
        username.setStyle("-fx-control-inner-background: white;");
        password.setStyle("-fx-control-inner-background: white;");
        username.clear();
        password.clear();
    }
}
