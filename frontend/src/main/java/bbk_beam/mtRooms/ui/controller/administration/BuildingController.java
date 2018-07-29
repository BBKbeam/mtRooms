package bbk_beam.mtRooms.ui.controller.administration;

import bbk_beam.mtRooms.admin.exception.FailedRecordUpdate;
import bbk_beam.mtRooms.admin.exception.FailedRecordWrite;
import bbk_beam.mtRooms.exception.LoginException;
import bbk_beam.mtRooms.network.IRmiAdministrationServices;
import bbk_beam.mtRooms.network.exception.Unauthorised;
import bbk_beam.mtRooms.reservation.dto.Building;
import bbk_beam.mtRooms.ui.AlertDialog;
import bbk_beam.mtRooms.ui.controller.MainWindowController;
import bbk_beam.mtRooms.ui.model.SessionManager;
import eadjlib.logger.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;

import java.net.URL;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicBoolean;

public class BuildingController implements Initializable {
    private enum ControllerRole {NEW_BUILDING, EDIT_BUILDING}

    private final Logger log = Logger.getLoggerInstance(BuildingController.class.getName());
    private AlertDialog alertDialog;
    private SessionManager sessionManager;
    private MainWindowController mainWindowController;
    private ResourceBundle resourceBundle;
    private HashMap<TextField, Boolean> field_validation = new HashMap<>();
    private Building building = null;
    private ControllerRole controllerRole;

    public TextField name_TextField;
    public TextField address1_TextField;
    public TextField address2_TextField;
    public TextField city_TextField;
    public TextField postcode_TextField;
    public TextField country_TextField;
    public TextField telephone_TextField;
    public Button cancelButton;
    public Button saveButton;

    /**
     * Updates a building in the records
     *
     * @return Success
     */
    private boolean updateBuilding() {
        IRmiAdministrationServices services = this.sessionManager.getAdministrationServices();
        AtomicBoolean success_flag = new AtomicBoolean(false);
        Optional<ButtonType> result = AlertDialog.showConfirmation(
                resourceBundle.getString("ConfirmationDialogTitle_Generic"),
                resourceBundle.getString("ConfirmationDialog_GenericUpdate"),
                ""
        );
        result.ifPresent(response -> {
            try {
                this.building = new Building(
                        this.building.id(),
                        name_TextField.getText(),
                        address1_TextField.getText(),
                        address2_TextField.getText(),
                        city_TextField.getText(),
                        postcode_TextField.getText(),
                        country_TextField.getText(),
                        telephone_TextField.getText()
                );
                if (response == ButtonType.OK) {
                    services.update(this.sessionManager.getToken(), this.building);
                    success_flag.set(true);
                }
            } catch (FailedRecordUpdate e) {
                AlertDialog.showExceptionAlert(
                        resourceBundle.getString("ErrorDialogTitle_Deletion"),
                        resourceBundle.getString("ErrorMsg_FailedBackendWrite"),
                        e
                );
            } catch (Unauthorised e) {
                this.alertDialog.showGenericError(e);
            } catch (RemoteException e) {
                this.alertDialog.showGenericError(e);
            } catch (LoginException e) {
                this.alertDialog.showGenericError(e);
            }
        });
        return success_flag.get();
    }

    /**
     * Adds a building to the records
     *
     * @return Success
     */
    private boolean addBuilding() {
        AtomicBoolean success_flag = new AtomicBoolean(false);
        IRmiAdministrationServices services = this.sessionManager.getAdministrationServices();
        try {
            this.building = new Building(
                    name_TextField.getText(),
                    address1_TextField.getText(),
                    (address2_TextField.getText().isEmpty() ? null : address2_TextField.getText()),
                    city_TextField.getText(),
                    postcode_TextField.getText(),
                    country_TextField.getText(),
                    telephone_TextField.getText()
            );
            services.add(this.sessionManager.getToken(), this.building);
            success_flag.set(true);
        } catch (FailedRecordWrite e) {
            AlertDialog.showExceptionAlert(
                    resourceBundle.getString("ErrorDialogTitle_Deletion"),
                    resourceBundle.getString("ErrorMsg_FailedBackendWrite"),
                    e
            );
        } catch (RemoteException e) {
            alertDialog.showGenericError(e);
        } catch (LoginException e) {
            alertDialog.showGenericError(e);
        } catch (Unauthorised e) {
            alertDialog.showGenericError(e);
        }
        return success_flag.get();
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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.resourceBundle = resources;
        this.alertDialog = new AlertDialog(resources);

        field_validation.put(name_TextField, false);
        field_validation.put(address1_TextField, false);
        field_validation.put(address2_TextField, true); //empty at start is valid
        field_validation.put(city_TextField, false);
        field_validation.put(postcode_TextField, false);
        field_validation.put(country_TextField, false);
        field_validation.put(telephone_TextField, false);

        name_TextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() < 3) {
                name_TextField.setStyle("-fx-control-inner-background: red;");
                field_validation.put(name_TextField, false);
            } else {
                name_TextField.setStyle("-fx-control-inner-background: white;");
                field_validation.put(name_TextField, true);
            }
        });

        address1_TextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() < 3) {
                address1_TextField.setStyle("-fx-control-inner-background: red;");
                field_validation.put(address1_TextField, false);
            } else {
                address1_TextField.setStyle("-fx-control-inner-background: white;");
                field_validation.put(address1_TextField, true);
            }
        });

        address2_TextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() > 0 && newValue.length() < 3) {
                address2_TextField.setStyle("-fx-control-inner-background: red;");
                field_validation.put(address2_TextField, false);
            } else {
                address2_TextField.setStyle("-fx-control-inner-background: white;");
                field_validation.put(address2_TextField, true);
            }
        });

        city_TextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() < 3) {
                city_TextField.setStyle("-fx-control-inner-background: red;");
                field_validation.put(city_TextField, false);
            } else {
                city_TextField.setStyle("-fx-control-inner-background: white;");
                field_validation.put(city_TextField, true);
            }
        });

        postcode_TextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() < 3) {
                postcode_TextField.setStyle("-fx-control-inner-background: red;");
                field_validation.put(postcode_TextField, false);
            } else {
                postcode_TextField.setStyle("-fx-control-inner-background: white;");
                field_validation.put(postcode_TextField, true);
            }
        });

        country_TextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() < 2) {
                country_TextField.setStyle("-fx-control-inner-background: red;");
                field_validation.put(country_TextField, false);
            } else {
                country_TextField.setStyle("-fx-control-inner-background: white;");
                field_validation.put(country_TextField, true);
            }
        });

        telephone_TextField.textProperty().addListener((observable, oldValue, newValue) -> {  //TODO regex
            if (newValue.length() < 3) {
                telephone_TextField.setStyle("-fx-control-inner-background: red;");
                field_validation.put(telephone_TextField, false);
            } else {
                telephone_TextField.setStyle("-fx-control-inner-background: white;");
                field_validation.put(telephone_TextField, true);
            }
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
    void setMainWindowController(MainWindowController mainWindowController) {
        this.mainWindowController = mainWindowController;
    }

    void loadBuilding(Building building) {
        this.building = building;
        if (this.building == null) {
            controllerRole = ControllerRole.NEW_BUILDING;
        } else {
            controllerRole = ControllerRole.EDIT_BUILDING;
            name_TextField.setText(building.name());
            address1_TextField.setText(building.address1());
            address2_TextField.setText((building.address2() == null ? "" : building.address2()));
            city_TextField.setText(building.city());
            postcode_TextField.setText(building.postcode());
            country_TextField.setText(building.country());
            telephone_TextField.setText(building.phone());
        }
    }

    /**
     * Gets the created/updated building
     *
     * @return Building if created/updated
     */
    Optional<Building> getBuilding() {
        return Optional.ofNullable(this.building);
    }

    public void handleCancelAction(ActionEvent actionEvent) {
        this.building = null;
        cancelButton.getScene().getWindow().hide();
    }

    public void handleSaveAction(ActionEvent actionEvent) {
        if (checkValidationFlags()) {
            switch (this.controllerRole) {
                case NEW_BUILDING:
                    if (addBuilding()) {
                        log.log("Building added: ", this.building);
                        mainWindowController.status_left.setText(this.resourceBundle.getString("Status_BuildingCreated"));
                    }
                    break;
                case EDIT_BUILDING:
                    if (updateBuilding()) {
                        log.log("Building updated: ", this.building);
                        mainWindowController.status_left.setText(this.resourceBundle.getString("Status_BuildingUpdated"));
                    }
                    break;
            }
            saveButton.getScene().getWindow().hide();
        } else {
            AlertDialog.showAlert(
                    Alert.AlertType.ERROR,
                    this.resourceBundle.getString("ErrorDialogTitle_Generic"),
                    this.resourceBundle.getString("ErrorMsg_RequiredFieldsUnfilled")
            );
        }
    }
}
