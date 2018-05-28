package bbk_beam.mtRooms.ui.controller.frontdesk;

import bbk_beam.mtRooms.exception.LoginException;
import bbk_beam.mtRooms.network.IRmiServices;
import bbk_beam.mtRooms.network.exception.Unauthorised;
import bbk_beam.mtRooms.reservation.dto.Customer;
import bbk_beam.mtRooms.reservation.dto.Membership;
import bbk_beam.mtRooms.reservation.exception.FailedDbFetch;
import bbk_beam.mtRooms.reservation.exception.InvalidMembership;
import bbk_beam.mtRooms.ui.model.SessionManager;
import eadjlib.logger.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.text.Text;

import java.net.URL;
import java.rmi.RemoteException;
import java.util.ResourceBundle;

public class CustomerAccountController implements Initializable {
    private final Logger log = Logger.getLoggerInstance(CustomerAccountController.class.getName());
    private SessionManager sessionManager;
    private ResourceBundle resourceBundle;

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

    private void loadCustomer(Customer customer) throws LoginException, Unauthorised, RemoteException, InvalidMembership, FailedDbFetch {
        IRmiServices services = this.sessionManager.getServices();
        Membership membership = services.getMembership(sessionManager.getToken(), customer.membershipTypeID());

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
    }

    /**
     * Sets the session manager for the controller
     *
     * @param sessionManager SessionManager instance
     */
    public void setSessionManager(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    @FXML
    public void handleEditAction(ActionEvent actionEvent) {
        //TODO
    }
}
