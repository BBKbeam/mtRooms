package bbk_beam.mtRooms.ui.controller;

import bbk_beam.mtRooms.MtRoomsGUI;
import bbk_beam.mtRooms.admin.exception.AuthenticationFailureException;
import bbk_beam.mtRooms.exception.LoginException;
import bbk_beam.mtRooms.exception.RemoteFailure;
import bbk_beam.mtRooms.network.IRmiAdministrationServices;
import bbk_beam.mtRooms.network.IRmiServices;
import bbk_beam.mtRooms.network.exception.Unauthorised;
import bbk_beam.mtRooms.ui.controller.administration.AdministrationController;
import bbk_beam.mtRooms.ui.controller.common.AlertDialog;
import bbk_beam.mtRooms.ui.controller.frontdesk.CustomerSearchController;
import bbk_beam.mtRooms.ui.model.SessionManager;
import eadjlib.logger.Logger;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.ResourceBundle;

public class MainWindowController implements Initializable {
    private final Logger log = Logger.getLoggerInstance(MainWindowController.class.getName());
    private AlertDialog alertDialog;
    private SessionManager sessionManager;
    private ResourceBundle resourceBundle;
    public Label status_left;
    public Label status_right;
    public ScrollPane main_pane;
    public MenuItem logout;
    public MenuItem quit;
    public MenuItem reservation;
    public MenuItem revenue;
    public MenuItem logistics;
    public MenuItem administration;
    public Menu administration_menu;
    public MenuItem about;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.resourceBundle = resources;
        this.alertDialog = new AlertDialog(resources);

        ImageView logout_menuIcon = new ImageView(new Image("icon/account-logout-8x.png"));
        ImageView quit_menuIcon = new ImageView(new Image("icon/x-8x.png"));
        ImageView admin_menuIcon = new ImageView(new Image("icon/wrench-8x.png"));
        ImageView reservation_menuIcon = new ImageView(new Image("icon/clock-8x.png"));
        ImageView revenue_menuIcon = new ImageView(new Image("icon/pie-chart-8x.png"));
        ImageView logistics_menuIcon = new ImageView(new Image("icon/document-8x.png"));
        ImageView about_menuIcon = new ImageView(new Image("icon/info-8x.png"));

        logout_menuIcon.setFitHeight(20);
        logout_menuIcon.setFitWidth(20);
        quit_menuIcon.setFitHeight(20);
        quit_menuIcon.setFitWidth(20);
        admin_menuIcon.setFitHeight(20);
        admin_menuIcon.setFitWidth(20);
        reservation_menuIcon.setFitHeight(20);
        reservation_menuIcon.setFitWidth(20);
        revenue_menuIcon.setFitHeight(20);
        revenue_menuIcon.setFitWidth(20);
        logistics_menuIcon.setFitHeight(20);
        logistics_menuIcon.setFitWidth(20);
        about_menuIcon.setFitHeight(20);
        about_menuIcon.setFitWidth(20);

        logout.setGraphic(logout_menuIcon);
        quit.setGraphic(quit_menuIcon);
        administration.setGraphic(admin_menuIcon);
        reservation.setGraphic(reservation_menuIcon);
        revenue.setGraphic(revenue_menuIcon);
        logistics.setGraphic(logistics_menuIcon);
        about.setGraphic(about_menuIcon);

        clearStatusBar();
    }

    /**
     * Clears the status bar of messages
     */
    private void clearStatusBar() {
        this.status_left.setText("");
        this.status_right.setText("");
    }

    /**
     * Disables all menu entries in "View" menu and any view specific menu
     */
    private void disableAllViewMenuOptions() {
        this.reservation.setDisable(true);
        this.revenue.setDisable(true);
        this.logistics.setDisable(true);
        this.administration.setDisable(true);
        this.administration_menu.setVisible(false);
    }

    /**
     * Sets the SessionManager model in the controller
     *
     * @param sessionManager SessionManager instance
     */
    public void setSessionManager(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    /**
     * Enable/Disable the "View" menu entries based on user access rights
     */
    public void setViewMenuAccessibility() {
        if (this.sessionManager == null) {
            disableAllViewMenuOptions();
        } else {
            IRmiServices services = this.sessionManager.getMainServices();
            try {
                this.reservation.setDisable(!services.hasFrontDeskAccess(this.sessionManager.getToken()));
                this.revenue.setDisable(!services.hasFrontDeskAccess(this.sessionManager.getToken()));
                this.logistics.setDisable(!services.hasLogisticsAccess(this.sessionManager.getToken()));
                this.administration.setDisable(!services.hasAdministrativeAccess(this.sessionManager.getToken()));
            } catch (RemoteException e) {
                log.log_Error("Could not assess service access rights: connection problem.");
                disableAllViewMenuOptions();
            } catch (LoginException e) {
                disableAllViewMenuOptions();
            }
        }
    }

    /**
     * Displays the login pane
     */
    public void showLoginPane() {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(MtRoomsGUI.class.getResource("/view/LoginView.fxml"));
        loader.setResources(resourceBundle);
        try {
            AnchorPane pane = loader.load();
            LoginController loginController = loader.getController();
            loginController.setSessionManager(sessionManager);
            loginController.setMainWindowController(this);
            main_pane.setFitToWidth(true);
            main_pane.setFitToHeight(true);
            main_pane.setContent(pane);
        } catch (IOException e) {
            this.alertDialog.showGenericError(e);
        }
    }

    /**
     * Displays the administration pane
     */
    public void showAdminPane() {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(MtRoomsGUI.class.getResource("/view/administration/AdminView.fxml"));
        loader.setResources(resourceBundle);
        try {
            AnchorPane pane = loader.load();
            AdministrationController adminController = loader.getController();
            adminController.setSessionManager(sessionManager);
            adminController.setMainWindowController(this);
            adminController.populateAccountTable();
            main_pane.setFitToWidth(true);
            main_pane.setFitToHeight(true);
            main_pane.setContent(pane);
        } catch (IOException e) {
            this.alertDialog.showGenericError(e);
        }
    }

    /**
     * Shows the customer search pane
     */
    public void showCustomerSearchPane() {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(MtRoomsGUI.class.getResource("/view/frontdesk/CustomerSearchView.fxml"));
        loader.setResources(resourceBundle);
        try {
            TabPane pane = loader.load();
            CustomerSearchController customerSearchController = loader.getController();
            customerSearchController.setSessionManager(sessionManager);
            customerSearchController.setMainWindowController(this);
            main_pane.setFitToWidth(true);
            main_pane.setFitToHeight(true);
            main_pane.setContent(pane);
        } catch (IOException e) {
            this.alertDialog.showGenericError(e);
        }
    }

    @FXML
    public void handleLogoutAction(ActionEvent actionEvent) {
        try {
            this.sessionManager.logout();
            this.logout.setVisible(false);
            this.disableAllViewMenuOptions();
            this.showLoginPane();
            this.status_left.setText("Logged out of user session...");
        } catch (RemoteFailure e) {
            this.alertDialog.showGenericError(e);
        } catch (AuthenticationFailureException e) {
            log.log_Error("Token was found invalid by server.");
            log.log_Exception(e);
            AlertDialog.showAlert(
                    Alert.AlertType.ERROR,
                    this.resourceBundle.getString("ErrorDialogTitle_Generic"),
                    this.resourceBundle.getString("ErrorMsg_InvalidSessionToken")
            );
            this.logout.setVisible(false);
            this.disableAllViewMenuOptions();
            this.showLoginPane();
            this.status_left.setText("Invalid user session terminated.");
        }
    }

    @FXML
    public void handleExitAction(ActionEvent actionEvent) {
        Platform.exit();
    }

    @FXML
    public void handleAboutAction(ActionEvent actionEvent) {
        this.status_left.setText("");
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(MtRoomsGUI.class.getResource("/view/about/AboutView.fxml"));
        loader.setResources(resourceBundle);
        try {
            Stage dialog = new Stage();
            dialog.initStyle(StageStyle.UTILITY);
            Scene scene = new Scene(loader.load());
            dialog.setScene(scene);
            dialog.show();
        } catch (IOException e) {
            log.log_Error("Could not load the 'about' dialog.");
            log.log_Exception(e);
            this.alertDialog.showGenericError(e);
        }
    }

    @FXML
    public void handleViewReservationAction(ActionEvent actionEvent) {
        this.administration_menu.setVisible(false);
        this.status_left.setText("");
        showCustomerSearchPane();
    }

    @FXML
    public void handleViewRevenueAction(ActionEvent actionEvent) {
        this.administration_menu.setVisible(false);
        this.status_left.setText("");
    }

    @FXML
    public void handleViewLogisticsAction(ActionEvent actionEvent) {
        this.administration_menu.setVisible(false);
        this.status_left.setText("");
    }

    @FXML
    public void handleViewAdministrationAction(ActionEvent actionEvent) {
        this.administration_menu.setVisible(true);
        this.status_left.setText("");
        this.showAdminPane();
    }

    @FXML
    public void handleOptimiseReservationDbAction(ActionEvent actionEvent) {
        IRmiAdministrationServices services = this.sessionManager.getAdministrationServices();
        if (services != null) {
            try {
                services.optimiseReservationDatabase(this.sessionManager.getToken());
                this.status_left.setText("Reservation DB optimised.");
            } catch (Unauthorised e) {
                this.alertDialog.showGenericError(e);
            } catch (RemoteException e) {
                this.alertDialog.showGenericError(e);
            } catch (LoginException e) {
                this.alertDialog.showGenericError(e);
            }
        }
    }

    @FXML
    public void handleOptimiseUserAccDbAction(ActionEvent actionEvent) {
        IRmiAdministrationServices services = this.sessionManager.getAdministrationServices();
        if (services != null) {
            try {
                services.optimiseUserAccountDatabase(this.sessionManager.getToken());
                this.status_left.setText("User account DB optimised.");
            } catch (Unauthorised e) {
                this.alertDialog.showGenericError(e);
            } catch (RemoteException e) {
                this.alertDialog.showGenericError(e);
            } catch (LoginException e) {
                this.alertDialog.showGenericError(e);
            }
        }
    }
}
