package bbk_beam.mtRooms.ui.controller.frontdesk;

import bbk_beam.mtRooms.MtRoomsGUI;
import bbk_beam.mtRooms.exception.LoginException;
import bbk_beam.mtRooms.network.IRmiServices;
import bbk_beam.mtRooms.network.exception.Unauthorised;
import bbk_beam.mtRooms.reservation.dto.Customer;
import bbk_beam.mtRooms.reservation.dto.Membership;
import bbk_beam.mtRooms.reservation.exception.FailedDbFetch;
import bbk_beam.mtRooms.reservation.exception.InvalidMembership;
import bbk_beam.mtRooms.ui.AlertDialog;
import bbk_beam.mtRooms.ui.controller.MainWindowController;
import bbk_beam.mtRooms.ui.model.SessionManager;
import eadjlib.logger.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.ResourceBundle;

public class CustomerAccountController implements Initializable {
    private final Logger log = Logger.getLoggerInstance(CustomerAccountController.class.getName());
    private SessionManager sessionManager;
    private MainWindowController mainWindowController;
    private ResourceBundle resourceBundle;
    private Customer customer;

    public Button closeAccount_Button;
    public Label customer_field;
    public Text id_field;
    public Text address1_field;
    public Text address2_field;
    public Text city_field;
    public Text county_field;
    public Text country_field;
    public Text phone1_field;
    public Text phone2_field;
    public Text email_field;
    public Text creationDate_field;
    public Text membershipType_field;
    public Text discountRate_field;

    /**
     * Shows the customer edit dialog
     */
    private void showEditCustomerDialog(Customer customer) {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(MtRoomsGUI.class.getResource("/view/frontdesk/CustomerCreationView.fxml"));
        loader.setResources(resourceBundle);
        try {
            AnchorPane pane = loader.load();
            CustomerCreationController customerCreationController = loader.getController();
            customerCreationController.setSessionManager(sessionManager);
            customerCreationController.setMainWindowController(this.mainWindowController);
            customerCreationController.loadCustomer(customer);
            Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setOnHiding(event -> {
                if (customerCreationController.getCustomer().isPresent()) {
                    try {
                        loadCustomer(customerCreationController.getCustomer().get());
                    } catch (FailedDbFetch e) {
                        AlertDialog.showExceptionAlert(
                                this.resourceBundle.getString("ErrorDialogTitle_UserAccount"),
                                this.resourceBundle.getString("ErrorMsg_FailedBackendFetch"),
                                e
                        );
                    } catch (InvalidMembership e) {
                        AlertDialog.showAlert(
                                Alert.AlertType.ERROR,
                                this.resourceBundle.getString("ErrorDialogTitle_UserAccount"),
                                this.resourceBundle.getString("ErrorMsg_InvalidMembership")
                        );
                    } catch (LoginException e) {
                        AlertDialog.showAlert(
                                Alert.AlertType.ERROR,
                                this.resourceBundle.getString("ErrorDialogTitle_Generic"),
                                this.resourceBundle.getString("ErrorMsg_LoggedOut")
                        );
                    } catch (RemoteException e) {
                        AlertDialog.showExceptionAlert(
                                this.resourceBundle.getString("ErrorDialogTitle_Generic"),
                                this.resourceBundle.getString("ErrorMsg_RemoteIssue"),
                                e
                        );
                    } catch (Unauthorised e) {
                        AlertDialog.showAlert(
                                Alert.AlertType.ERROR,
                                this.resourceBundle.getString("ErrorDialogTitle_Generic"),
                                this.resourceBundle.getString("ErrorMsg_Unauthorized")
                        );
                    }
                }
            });
            Scene scene = new Scene(pane);
            dialog.setScene(scene);
            dialog.show();
        } catch (IOException e) {
            log.log_Error("Could not load the 'Edit customer' dialog.");
            log.log_Exception(e);
            AlertDialog.showExceptionAlert(
                    this.resourceBundle.getString("ErrorDialogTitle_Generic"),
                    this.resourceBundle.getString("ErrorMsg_UiResourceIO"),
                    e
            );
        } catch (LoginException e) {
            AlertDialog.showAlert(
                    Alert.AlertType.ERROR,
                    this.resourceBundle.getString("ErrorDialogTitle_Generic"),
                    this.resourceBundle.getString("ErrorMsg_LoggedOut")
            );
        } catch (Unauthorised e) {
            AlertDialog.showAlert(
                    Alert.AlertType.ERROR,
                    this.resourceBundle.getString("ErrorDialogTitle_Generic"),
                    this.resourceBundle.getString("ErrorMsg_Unauthorized")
            );
        } catch (FailedDbFetch e) {
            AlertDialog.showExceptionAlert(
                    this.resourceBundle.getString("ErrorDialogTitle_UserAccount"),
                    this.resourceBundle.getString("ErrorMsg_FailedBackendFetch"),
                    e
            );
        } catch (InvalidMembership e) {
            AlertDialog.showAlert(
                    Alert.AlertType.ERROR,
                    this.resourceBundle.getString("ErrorDialogTitle_UserAccount"),
                    this.resourceBundle.getString("ErrorMsg_InvalidMembership")
            );
        }
    }

    /**
     * Load a Customer DTO info into the fields
     *
     * @param customer Customer DTO
     * @throws LoginException    when there is not a current session
     * @throws Unauthorised      when this client session is not authorised to access the resource
     * @throws RemoteException   when network issues occur during the remote call
     * @throws InvalidMembership when the customer has an invalid membership ID (doesn't exist in records)
     * @throws FailedDbFetch     when membership or records or customer could not be fetched
     */
    void loadCustomer(Customer customer) throws LoginException, Unauthorised, RemoteException, InvalidMembership, FailedDbFetch {
        IRmiServices services = this.sessionManager.getServices();
        Membership membership = services.getMembership(sessionManager.getToken(), customer.membershipTypeID());

        this.customer = customer;
        this.id_field.setText(String.valueOf(customer.customerID()));
        this.customer_field.setText(customer.title() + " " + customer.name() + " " + customer.surname().toUpperCase());
        this.address1_field.setText(customer.address1());
        this.address2_field.setText(customer.address2());
        this.city_field.setText(customer.city());
        this.county_field.setText(customer.county());
        this.country_field.setText(customer.country());
        this.phone1_field.setText(customer.phone1());
        this.phone2_field.setText(customer.phone2());
        this.email_field.setText(customer.email());
        this.creationDate_field.setText(customer.accountCreationDate().toString());
        this.membershipType_field.setText(membership.description());
        this.discountRate_field.setText(String.valueOf(membership.discount().rate()));
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.resourceBundle = resources;
        closeAccount_Button.setMinSize(64, 64);
        closeAccount_Button.setMaxSize(64, 64);
    }

    /**
     * Sets the session manager for the controller
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

    @FXML
    public void handleEditAction(ActionEvent actionEvent) {
        showEditCustomerDialog(this.customer);
    }

    @FXML
    public void handleCloseAccountAction(ActionEvent actionEvent) {
        this.mainWindowController.showCustomerSearchPane();
    }
}
