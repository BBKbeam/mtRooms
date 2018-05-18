package bbk_beam.mtRooms.ui.controller;

import bbk_beam.mtRooms.MtRoomsGUI;
import bbk_beam.mtRooms.admin.exception.AuthenticationFailureException;
import bbk_beam.mtRooms.exception.LoginException;
import bbk_beam.mtRooms.exception.RemoteFailure;
import bbk_beam.mtRooms.network.IRmiServices;
import bbk_beam.mtRooms.network.exception.Unauthorised;
import bbk_beam.mtRooms.ui.controller.administration.AdministrationController;
import bbk_beam.mtRooms.ui.model.SessionManager;
import eadjlib.logger.Logger;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;

import java.io.IOException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.ResourceBundle;

public class MainWindowController implements Initializable {
    private final Logger log = Logger.getLoggerInstance(MainWindowController.class.getName());
    private SessionManager sessionManager;
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
            IRmiServices services = this.sessionManager.getServices();
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
        try {
            //TODO keep view panes in cache during a session then null them @ logout
            AnchorPane pane = loader.load();
            LoginController loginController = loader.getController();
            loginController.setSessionManager(sessionManager);
            loginController.setMainWindowController(this);
            main_pane.setFitToWidth(true);
            main_pane.setFitToHeight(true);
            main_pane.setContent(pane);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Displays the administration pane
     */
    public void showAdminPane() {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(MtRoomsGUI.class.getResource("/view/administration/AdminView.fxml"));
        try {
            AnchorPane pane = loader.load();
            AdministrationController adminController = loader.getController();
            adminController.setSessionManager(sessionManager);
            adminController.loadAccountTable();
            main_pane.setFitToWidth(true);
            main_pane.setFitToHeight(true);
            main_pane.setContent(pane);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleLogoutAction(ActionEvent actionEvent) {
        try {
            this.sessionManager.logout();
            this.logout.setVisible(false);
            this.disableAllViewMenuOptions();
            this.showLoginPane();
        } catch (RemoteFailure remoteFailure) {
            remoteFailure.printStackTrace();
        } catch (AuthenticationFailureException e) {
            e.printStackTrace();
        }
        //TODO
    }

    @FXML
    public void handleExitAction(ActionEvent actionEvent) {
        Platform.exit();
    }

    @FXML
    public void handleAboutAction(ActionEvent actionEvent) {
        System.out.println("About action");
        //TODO
    }

    @FXML
    public void handleViewReservationAction(ActionEvent actionEvent) {
        this.administration_menu.setVisible(false);
    }

    @FXML
    public void handleViewRevenueAction(ActionEvent actionEvent) {
        this.administration_menu.setVisible(false);
    }

    @FXML
    public void handleViewLogisticsAction(ActionEvent actionEvent) {
        this.administration_menu.setVisible(false);
    }

    @FXML
    public void handleViewAdministrationAction(ActionEvent actionEvent) {
        this.administration_menu.setVisible(true);
        this.showAdminPane();
    }

    @FXML
    public void handleOptimiseReservationDbAction(ActionEvent actionEvent) {
        IRmiServices services = this.sessionManager.getServices();
        if (services != null) {
            try {
                services.optimiseReservationDatabase(this.sessionManager.getToken());
            } catch (Unauthorised unauthorised) {
                unauthorised.printStackTrace();
            } catch (RemoteException e) {
                e.printStackTrace();
            } catch (LoginException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    public void handleOptimiseUserAccDbAction(ActionEvent actionEvent) {
        IRmiServices services = this.sessionManager.getServices();
        if (services != null) {
            try {
                services.optimiseUserAccountDatabase(this.sessionManager.getToken());
            } catch (Unauthorised unauthorised) {
                unauthorised.printStackTrace();
            } catch (RemoteException e) {
                e.printStackTrace();
            } catch (LoginException e) {
                e.printStackTrace();
            }
        }
    }
}
