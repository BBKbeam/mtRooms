package bbk_beam.mtRooms.ui.controller.administration;

import bbk_beam.mtRooms.admin.exception.FailedRecordUpdate;
import bbk_beam.mtRooms.admin.exception.FailedRecordWrite;
import bbk_beam.mtRooms.exception.LoginException;
import bbk_beam.mtRooms.network.IRmiAdministrationServices;
import bbk_beam.mtRooms.network.exception.Unauthorised;
import bbk_beam.mtRooms.reservation.dto.Floor;
import bbk_beam.mtRooms.ui.controller.MainWindowController;
import bbk_beam.mtRooms.ui.controller.common.AlertDialog;
import bbk_beam.mtRooms.ui.controller.common.FieldValidator;
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
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicBoolean;

public class FloorController implements Initializable {
    private enum ControllerRole {NEW_FLOOR, EDIT_FLOOR}

    private final Logger log = Logger.getLoggerInstance(FloorController.class.getName());
    private AlertDialog alertDialog;
    private SessionManager sessionManager;
    private MainWindowController mainWindowController;
    private ResourceBundle resourceBundle;
    private FieldValidator fieldValidator;
    private Floor floor = null;
    private ControllerRole controllerRole;

    public TextField buildingId_TextField;
    public TextField floorDescription_TextField;
    public Button cancelButton;
    public Button saveButton;

    /**
     * Updates a floor in the records
     *
     * @return Success
     */
    private boolean updateFloor() {
        IRmiAdministrationServices services = this.sessionManager.getAdministrationServices();
        AtomicBoolean success_flag = new AtomicBoolean(false);
        Optional<ButtonType> result = AlertDialog.showConfirmation(
                resourceBundle.getString("ConfirmationDialogTitle_Generic"),
                resourceBundle.getString("ConfirmationDialog_GenericUpdate"),
                ""
        );
        result.ifPresent(response -> {
            try {
                this.floor = new Floor(
                        this.floor.buildingID(),
                        this.floor.floorID(),
                        floorDescription_TextField.getText()
                );
                if (response == ButtonType.OK) {
                    services.update(this.sessionManager.getToken(), this.floor);
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
     * Adds a floor to the records
     *
     * @return Success
     */
    private boolean addFloor() {
        AtomicBoolean success_flag = new AtomicBoolean(false);
        IRmiAdministrationServices services = this.sessionManager.getAdministrationServices();
        try {
            this.floor = new Floor(
                    Integer.parseInt(buildingId_TextField.getText()),
                    -1,
                    floorDescription_TextField.getText()
            );
            services.add(this.sessionManager.getToken(), this.floor);
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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.resourceBundle = resources;
        this.alertDialog = new AlertDialog(resources);
        this.fieldValidator = new FieldValidator();

        fieldValidator.set(floorDescription_TextField, false);

        floorDescription_TextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() < 3) {
                floorDescription_TextField.setStyle("-fx-control-inner-background: red;");
                fieldValidator.set(floorDescription_TextField, false);
            } else {
                floorDescription_TextField.setStyle("-fx-control-inner-background: white;");
                fieldValidator.set(floorDescription_TextField, true);
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

    /**
     * Loads field for floor
     *
     * @param floor Floor DTO
     */
    void loadFloor(Floor floor) {
        this.floor = floor;
        this.controllerRole = ControllerRole.EDIT_FLOOR;
        this.floorDescription_TextField.setText(floor.description());
    }

    /**
     * Prepare fields for new floor for a building
     *
     * @param building_id Building ID
     */
    void loadFloor(Integer building_id) {
        this.floor = null;
        this.controllerRole = ControllerRole.NEW_FLOOR;
        this.buildingId_TextField.setText(String.valueOf(building_id));
    }

    /**
     * Gets the created/updated floor
     *
     * @return Floor if created/updated
     */
    Optional<Floor> getFloor() {
        return Optional.ofNullable(this.floor);
    }

    public void handleCancelAction(ActionEvent actionEvent) {
        this.floor = null;
        cancelButton.getScene().getWindow().hide();
    }

    public void handleSaveAction(ActionEvent actionEvent) {
        if (this.fieldValidator.check()) {
            switch (this.controllerRole) {
                case NEW_FLOOR:
                    if (addFloor()) {
                        log.log("Floor added: ", this.floor);
                        mainWindowController.status_left.setText(this.resourceBundle.getString("Status_FloorCreated"));
                    }
                    break;
                case EDIT_FLOOR:
                    if (updateFloor()) {
                        log.log("Floor updated: ", this.floor);
                        mainWindowController.status_left.setText(this.resourceBundle.getString("Status_FloorUpdated"));
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
