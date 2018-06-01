package bbk_beam.mtRooms.ui.controller.frontdesk;

import bbk_beam.mtRooms.exception.LoginException;
import bbk_beam.mtRooms.network.IRmiServices;
import bbk_beam.mtRooms.network.exception.Unauthorised;
import bbk_beam.mtRooms.reservation.dto.Customer;
import bbk_beam.mtRooms.reservation.dto.Membership;
import bbk_beam.mtRooms.reservation.exception.FailedDbFetch;
import bbk_beam.mtRooms.reservation.exception.FailedDbWrite;
import bbk_beam.mtRooms.ui.controller.MainWindowController;
import bbk_beam.mtRooms.ui.model.SessionManager;
import eadjlib.logger.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.text.Text;

import java.net.URL;
import java.rmi.RemoteException;
import java.time.Instant;
import java.util.*;

public class CustomerCreationController implements Initializable {
    private final Logger log = Logger.getLoggerInstance(CustomerCreationController.class.getName());
    private SessionManager sessionManager;
    private MainWindowController mainWindowController;
    private ResourceBundle resourceBundle;
    private HashMap<TextField, Boolean> field_validation = new HashMap<>();
    private Customer customer = null;
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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.resourceBundle = resources;
        accordion_section.setExpandedPane(personalDetails_TitlePane);

        field_validation.put(title_field, false);
        field_validation.put(name_field, false);
        field_validation.put(surname_field, false);
        field_validation.put(address1_field, false);
        field_validation.put(city_field, false);
        field_validation.put(postcode_field, false);
        field_validation.put(country_field, false);
        field_validation.put(phone1_field, false);
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

        phone1_field.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() < 3) {
                phone1_field.setStyle("-fx-control-inner-background: red;");
                field_validation.put(phone1_field, false);
            } else {
                phone1_field.setStyle("-fx-control-inner-background: white;");
                field_validation.put(phone1_field, true);
            }
        });

        email_field.textProperty().addListener((observable, oldValue, newValue) -> {
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
     * Loads the Membership ChoiceBox content
     *
     * @throws LoginException
     * @throws FailedDbFetch
     * @throws Unauthorised
     * @throws RemoteException
     */
    void loadMembershipChoiceBox() throws LoginException, FailedDbFetch, Unauthorised, RemoteException {
        IRmiServices services = this.sessionManager.getServices();
        List<Membership> membershipList = services.getMemberships(sessionManager.getToken());
        ObservableList<Membership> membershipObservableList = FXCollections.observableList(membershipList);
        membership_ChoiceBox.setItems(membershipObservableList);
        membership_ChoiceBox.getSelectionModel().selectFirst();
    }

    /**
     * Gets the created customer
     *
     * @return Customer if created
     */
    Optional<Customer> getCreatedCustomer() {
        return Optional.ofNullable(this.customer);
    }

    @FXML
    public void handleCloseAction(ActionEvent actionEvent) {
        this.customer = null;
        cancel_Button.getScene().getWindow().hide();
    }

    @FXML
    public void handleSaveAction(ActionEvent actionEvent) {
        if (checkValidationFlags()) {
            IRmiServices services = this.sessionManager.getServices();
            try {
                this.customer = services.createNewCustomer(
                        this.sessionManager.getToken(),
                        new Customer(
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
                        )
                );
                log.log("New customer created: ", customer);
                save_Button.getScene().getWindow().hide();
            } catch (FailedDbWrite failedDbWrite) {
                failedDbWrite.printStackTrace();
            } catch (FailedDbFetch failedDbFetch) {
                failedDbFetch.printStackTrace();
            } catch (Unauthorised unauthorised) {
                unauthorised.printStackTrace();
            } catch (RemoteException e) {
                e.printStackTrace();
            } catch (LoginException e) {
                e.printStackTrace();
            }
            //TODO
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(this.resourceBundle.getString("ErrorDialogTitle_Generic"));
            alert.setHeaderText(this.resourceBundle.getString("ErrorMsg_RequiredFieldsUnfilled"));
            alert.showAndWait();
        }

    }
}
