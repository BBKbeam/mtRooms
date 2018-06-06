package bbk_beam.mtRooms.ui.controller.frontdesk;

import bbk_beam.mtRooms.exception.LoginException;
import bbk_beam.mtRooms.network.IRmiServices;
import bbk_beam.mtRooms.network.exception.Unauthorised;
import bbk_beam.mtRooms.reservation.dto.Customer;
import bbk_beam.mtRooms.reservation.dto.Membership;
import bbk_beam.mtRooms.reservation.exception.FailedDbFetch;
import bbk_beam.mtRooms.reservation.exception.FailedDbWrite;
import bbk_beam.mtRooms.reservation.exception.InvalidMembership;
import bbk_beam.mtRooms.ui.AlertDialog;
import bbk_beam.mtRooms.ui.controller.MainWindowController;
import bbk_beam.mtRooms.ui.model.SessionManager;
import eadjlib.logger.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

import java.net.URL;
import java.rmi.RemoteException;
import java.time.Instant;
import java.util.*;

public class CustomerCreationController implements Initializable {
    private enum ControllerRole {NEW_CUSTOMER, EDIT_CUSTOMER}

    private final Logger log = Logger.getLoggerInstance(CustomerCreationController.class.getName());
    private AlertDialog alertDialog;
    private SessionManager sessionManager;
    private MainWindowController mainWindowController;
    private ResourceBundle resourceBundle;
    private HashMap<TextField, Boolean> field_validation = new HashMap<>();
    private Customer customer = null;
    private ControllerRole controllerRole;

    public Pane customerIdLabel_Pane;
    public Label id_Label;
    public ButtonBar button_bar;
    public Button cancel_Button;
    public Button save_Button;
    public Accordion accordion_section;
    public TitledPane personalDetails_TitlePane;
    public TitledPane address_TitlePane;
    public TitledPane contact_TitlePane;
    public TitledPane membership_TitlePane;
    public TextField title_field;
    public TextField name_field;
    public TextField surname_field;
    public TextField address1_field;
    public TextField address2_field; //Optional field
    public TextField city_field;
    public TextField postcode_field;
    public TextField county_field; //Optional field
    public TextField country_field;
    public TextField phone1_field;
    public TextField phone2_field; //Optional field
    public TextField email_field;
    public ChoiceBox<Membership> membership_ChoiceBox;
    public Text discountRate_field;

    /**
     * Loads the Membership ChoiceBox content
     *
     * @throws LoginException  when there is not a current session
     * @throws FailedDbFetch   when membership records could not be fetched
     * @throws Unauthorised    when this client session is not authorised to access the resource
     * @throws RemoteException when network issues occur during the remote call
     */
    private void loadMembershipChoiceBox() throws LoginException, FailedDbFetch, Unauthorised, RemoteException {
        IRmiServices services = this.sessionManager.getServices();
        List<Membership> membershipList = services.getMemberships(sessionManager.getToken());
        ObservableList<Membership> membershipObservableList = FXCollections.observableList(membershipList);
        membership_ChoiceBox.setItems(membershipObservableList);
        membership_ChoiceBox.getSelectionModel().selectFirst();
    }

    /**
     * Checks the field validation HashMap
     *
     * @return Field validation state (true = all valid, false = 1+ invalid)
     */
    private boolean checkValidationFlags() {
        boolean valid_flag = true;
        for (Map.Entry<TextField, Boolean> entry : this.field_validation.entrySet()) {
            if (!entry.getValue()) {
                entry.getKey().setStyle("-fx-control-inner-background: red;");
                valid_flag = false;
            }
        }
        return valid_flag;
    }

    /**
     * Saves a new customer to records
     *
     * @throws FailedDbWrite   when customer could not be saved to records
     * @throws LoginException  when there is not a current session
     * @throws FailedDbFetch   when membership or customer records could not be fetched
     * @throws Unauthorised    when this client session is not authorised to access the resource
     * @throws RemoteException when network issues occur during the remote call
     */
    private void saveNewCustomer() throws LoginException, Unauthorised, RemoteException, FailedDbWrite, FailedDbFetch {
        Customer new_customer = new Customer(
                membership_ChoiceBox.getSelectionModel().getSelectedItem().id(),
                Date.from(Instant.now()),
                title_field.getText(),
                name_field.getText(),
                surname_field.getText(),
                address1_field.getText(),
                (address2_field.getText().isEmpty() ? null : address2_field.getText()),
                postcode_field.getText(),
                city_field.getText(),
                (county_field.getText().isEmpty() ? null : county_field.getText()),
                country_field.getText(),
                phone1_field.getText(),
                (phone2_field.getText().isEmpty() ? null : phone2_field.getText()),
                email_field.getText()
        );

        IRmiServices services = this.sessionManager.getServices();
        try {
            this.customer = services.createNewCustomer(this.sessionManager.getToken(), new_customer);
            log.log("New customer created: [", this.customer.customerID(), "].");
        } catch (FailedDbWrite e) {
            log.log_Error("Failed to save customer: ", new_customer);
            throw e;
        } catch (FailedDbFetch e) {
            log.log_Error("Failed to fetch back saved customer from records.");
            throw e;
        }
    }

    /**
     * Updates a customer's records
     *
     * @throws FailedDbWrite   when customer could not be saved to records
     * @throws LoginException  when there is not a current session
     * @throws Unauthorised    when this client session is not authorised to access the resource
     * @throws RemoteException when network issues occur during the remote call
     */
    private void saveEditedCustomer() throws LoginException, Unauthorised, RemoteException, FailedDbWrite {
        this.customer.setTitle(title_field.getText());
        this.customer.setName(name_field.getText());
        this.customer.setSurname(surname_field.getText());
        this.customer.setAddress1(address1_field.getText());
        this.customer.setAddress2((address2_field.getText().isEmpty() ? null : address2_field.getText()));
        this.customer.setPostCode(postcode_field.getText());
        this.customer.setCity(city_field.getText());
        this.customer.setCounty((county_field.getText().isEmpty() ? null : county_field.getText()));
        this.customer.setCountry(country_field.getText());
        this.customer.setPhone1(phone1_field.getText());
        this.customer.setPhone2((phone2_field.getText().isEmpty() ? null : phone2_field.getText()));
        this.customer.setEmail(email_field.getText());
        this.customer.setMembershipTypeID(membership_ChoiceBox.getSelectionModel().getSelectedItem().id());

        IRmiServices services = this.sessionManager.getServices();
        try {
            services.saveCustomerChangesToDB(this.sessionManager.getToken(), this.customer);
        } catch (FailedDbWrite e) {
            log.log_Error("Failed to save customer changes to records.");
            throw e;
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //TODO add regex for special fields (email, phone..)
        this.resourceBundle = resources;
        this.alertDialog = new AlertDialog(resources);

        accordion_section.setExpandedPane(personalDetails_TitlePane);

        field_validation.put(title_field, false);
        field_validation.put(name_field, false);
        field_validation.put(surname_field, false);
        field_validation.put(address1_field, false);
        field_validation.put(city_field, false);
        field_validation.put(postcode_field, false);
        field_validation.put(country_field, false);
        field_validation.put(phone1_field, false);
        field_validation.put(phone2_field, true); //empty at start is valid
        field_validation.put(email_field, false);

        title_field.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() < 2) {
                title_field.setStyle("-fx-control-inner-background: red;");
                field_validation.put(title_field, false);
            } else {
                title_field.setStyle("-fx-control-inner-background: white;");
                field_validation.put(title_field, true);
            }
        });

        name_field.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() < 2) {
                name_field.setStyle("-fx-control-inner-background: red;");
                field_validation.put(name_field, false);
            } else {
                name_field.setStyle("-fx-control-inner-background: white;");
                field_validation.put(name_field, true);
            }
        });

        surname_field.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() < 2) {
                surname_field.setStyle("-fx-control-inner-background: red;");
                field_validation.put(surname_field, false);
            } else {
                surname_field.setStyle("-fx-control-inner-background: white;");
                field_validation.put(surname_field, true);
            }
        });

        address1_field.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() < 3) {
                address1_field.setStyle("-fx-control-inner-background: red;");
                field_validation.put(address1_field, false);
            } else {
                address1_field.setStyle("-fx-control-inner-background: white;");
                field_validation.put(address1_field, true);
            }
        });

        city_field.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() < 3) {
                city_field.setStyle("-fx-control-inner-background: red;");
                field_validation.put(city_field, false);
            } else {
                city_field.setStyle("-fx-control-inner-background: white;");
                field_validation.put(city_field, true);
            }
        });

        postcode_field.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() < 3) {
                postcode_field.setStyle("-fx-control-inner-background: red;");
                field_validation.put(postcode_field, false);
            } else {
                postcode_field.setStyle("-fx-control-inner-background: white;");
                field_validation.put(postcode_field, true);
            }
        });

        country_field.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() < 3) {
                country_field.setStyle("-fx-control-inner-background: red;");
                field_validation.put(country_field, false);
            } else {
                country_field.setStyle("-fx-control-inner-background: white;");
                field_validation.put(country_field, true);
            }
        });

        phone1_field.textProperty().addListener((observable, oldValue, newValue) -> { //TODO regex
            if (newValue.length() < 3) {
                phone1_field.setStyle("-fx-control-inner-background: red;");
                field_validation.put(phone1_field, false);
            } else {
                phone1_field.setStyle("-fx-control-inner-background: white;");
                field_validation.put(phone1_field, true);
            }
        });

        phone2_field.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty()) {
                //TODO regex
//                field_validation.put(phone2_field, true);//or false
            } else {
                phone2_field.setStyle("-fx-control-inner-background: white;");
                field_validation.put(phone2_field, true);
            }
        });

        email_field.textProperty().addListener((observable, oldValue, newValue) -> { //TODO regex
            if (newValue.length() < 3) {
                email_field.setStyle("-fx-control-inner-background: red;");
                field_validation.put(email_field, false);
            } else {
                email_field.setStyle("-fx-control-inner-background: white;");
                field_validation.put(email_field, true);
            }
        });

        membership_ChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            discountRate_field.setText(String.valueOf(newValue.discount().rate()));
        });

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

    /**
     * Loads a Customer into the fields
     *
     * @param customer Customer DTO
     * @throws InvalidMembership when the customer has an invalid membership ID (doesn't exist in records)
     * @throws LoginException    when there is not a current session
     * @throws FailedDbFetch     when membership or records or customer could not be fetched
     * @throws Unauthorised      when this client session is not authorised to access the resource
     * @throws RemoteException   when network issues occur during the remote call
     */
    void loadCustomer(Customer customer) throws LoginException, Unauthorised, RemoteException, FailedDbFetch, InvalidMembership {
        this.customer = customer;

        try {
            loadMembershipChoiceBox();
        } catch (FailedDbFetch e) {
            log.log_Error("Failed to fetch memberships from records.");
            throw e;
        }

        if (this.customer == null) {
            controllerRole = ControllerRole.NEW_CUSTOMER;
            customerIdLabel_Pane.setVisible(false);
        } else {
            controllerRole = ControllerRole.EDIT_CUSTOMER;
            customerIdLabel_Pane.setVisible(true);
            IRmiServices services = this.sessionManager.getServices();

            try {
                Membership membership = services.getMembership(sessionManager.getToken(), customer.membershipTypeID());
                this.id_Label.setText(this.resourceBundle.getString("Label_Customer") + " #" + customer.customerID());
                this.title_field.setText(customer.title());
                this.name_field.setText(customer.name());
                this.surname_field.setText(customer.surname());
                this.address1_field.setText(customer.address1());
                this.address2_field.setText((customer.address2() == null ? "" : customer.address2()));
                this.city_field.setText(customer.city());
                this.postcode_field.setText(customer.postCode());
                this.county_field.setText((customer.county() == null ? "" : customer.county()));
                this.country_field.setText(customer.country());
                this.phone1_field.setText(customer.phone1());
                this.phone2_field.setText((customer.phone2() == null ? "" : customer.phone2()));
                this.email_field.setText(customer.email());
                this.membership_ChoiceBox.getSelectionModel().select(membership);
            } catch (InvalidMembership e) {
                log.log_Error("Customer [", customer.customerID(), "] has an invalid membership [", customer.membershipTypeID(), "].");
                throw e;
            } catch (FailedDbFetch e) {
                log.log_Error("Failed to fetch Membership type [", customer.membershipTypeID(), "] from records. ");
                throw e;
            }
        }
    }

    /**
     * Gets the created customer
     *
     * @return Customer if created
     */
    Optional<Customer> getCustomer() {
        return Optional.ofNullable(this.customer);
    }

    @FXML
    public void handleCloseAction(ActionEvent actionEvent) {
        this.customer = null;
        cancel_Button.getScene().getWindow().hide();
    }

    @FXML
    public void handleSaveAction(ActionEvent actionEvent) {
        try {
            if (checkValidationFlags()) {
                switch (this.controllerRole) {
                    case NEW_CUSTOMER:
                        saveNewCustomer();
                        log.log("Customer [", this.customer.customerID(), "] added.");
                        mainWindowController.status_left.setText(this.resourceBundle.getString("Status_CustomerCreated"));
                        break;
                    case EDIT_CUSTOMER:
                        saveEditedCustomer();
                        log.log("Customer [", this.customer.customerID(), "] updated.");
                        mainWindowController.status_left.setText(this.resourceBundle.getString("Status_CustomerUpdated"));
                        break;
                }
                save_Button.getScene().getWindow().hide();
            } else {
                AlertDialog.showAlert(
                        Alert.AlertType.ERROR,
                        this.resourceBundle.getString("ErrorDialogTitle_Generic"),
                        this.resourceBundle.getString("ErrorMsg_RequiredFieldsUnfilled")
                );
            }
        } catch (FailedDbWrite e) {
            this.alertDialog.showGenericError(e);
        } catch (FailedDbFetch e) {
            this.alertDialog.showGenericError(e);
        } catch (Unauthorised e) {
            this.alertDialog.showGenericError(e);
        } catch (RemoteException e) {
            this.alertDialog.showGenericError(e);
        } catch (LoginException e) {
            this.alertDialog.showGenericError(e);
        }
    }
}
