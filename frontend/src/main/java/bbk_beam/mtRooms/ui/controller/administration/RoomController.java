package bbk_beam.mtRooms.ui.controller.administration;

import bbk_beam.mtRooms.admin.dto.Usage;
import bbk_beam.mtRooms.admin.exception.FailedRecordFetch;
import bbk_beam.mtRooms.admin.exception.FailedRecordUpdate;
import bbk_beam.mtRooms.admin.exception.FailedRecordWrite;
import bbk_beam.mtRooms.admin.exception.IncompleteRecord;
import bbk_beam.mtRooms.common.TimestampUTC;
import bbk_beam.mtRooms.db.TimestampConverter;
import bbk_beam.mtRooms.exception.LoginException;
import bbk_beam.mtRooms.network.IRmiAdministrationServices;
import bbk_beam.mtRooms.network.exception.Unauthorised;
import bbk_beam.mtRooms.reservation.dto.*;
import bbk_beam.mtRooms.ui.AlertDialog;
import bbk_beam.mtRooms.ui.controller.MainWindowController;
import bbk_beam.mtRooms.ui.model.SessionManager;
import eadjlib.logger.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicBoolean;

public class RoomController implements Initializable {
    private enum ControllerRole {NEW_ROOM, EDIT_ROOM}

    private final Logger log = Logger.getLoggerInstance(RoomController.class.getName());
    private AlertDialog alertDialog;
    private SessionManager sessionManager;
    private MainWindowController mainWindowController;
    private ResourceBundle resourceBundle;
    private HashMap<TextField, Boolean> field_validation = new HashMap<>();
    private Room room = null;
    private ControllerRole controllerRole;

    public TextField buildingId_TextField;
    public TextField floorId_TextField;
    public Label roomDescriptionField_Text;
    public TextField roomDescription_TextField;
    public TextField roomCapacity_TextField;
    public TextField roomDimension_TextField;
    public Label price_Label;
    public TextField price_TextField;
    public CheckBox fixedChairs_CheckBox;
    public CheckBox cateringSpace_CheckBox;
    public CheckBox whiteboard_CheckBox;
    public CheckBox projector_CheckBox;
    public Button cancelButton;
    public Button saveButton;

    /**
     * Updates a room in the records
     *
     * @return Success
     */
    private boolean updateRoom() {
        IRmiAdministrationServices services = this.sessionManager.getAdministrationServices();
        AtomicBoolean success_flag = new AtomicBoolean(false);
        Optional<ButtonType> result = AlertDialog.showConfirmation(
                resourceBundle.getString("ConfirmationDialogTitle_Generic"),
                resourceBundle.getString("ConfirmationDialog_GenericUpdate"),
                ""
        );
        result.ifPresent(response -> {
            try {
                RoomCategory category = new RoomCategory(
                        -1,
                        Integer.parseInt(roomCapacity_TextField.getText()),
                        Integer.parseInt(roomDimension_TextField.getText())
                );

                category = services.add(this.sessionManager.getToken(), category);

                RoomPrice price = new RoomPrice(
                        -1,
                        Double.parseDouble(price_TextField.getText()),
                        new TimestampUTC(TimestampConverter.nowUTC()).year()
                );

                RoomFixtures fixtures = new RoomFixtures(
                        -1,
                        fixedChairs_CheckBox.isSelected(),
                        cateringSpace_CheckBox.isSelected(),
                        whiteboard_CheckBox.isSelected(),
                        projector_CheckBox.isSelected()
                );

                this.room = new Room(
                        this.room.id(),
                        this.room.floorID(),
                        this.room.buildingID(),
                        category.id(),
                        roomDescription_TextField.getText()

                );

                if (response == ButtonType.OK) {
                    services.update(this.sessionManager.getToken(), this.room, price, fixtures);
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
            } catch (FailedRecordFetch e) {
                AlertDialog.showExceptionAlert(
                        resourceBundle.getString("ErrorDialogTitle_Deletion"),
                        resourceBundle.getString("ErrorMsg_FailedBackendFetch"),
                        e
                );
            } catch (FailedRecordWrite failedRecordWrite) {
                failedRecordWrite.printStackTrace();
            }
        });
        return success_flag.get();
    }

    /**
     * Adds a room to the records
     *
     * @return Success
     */
    private boolean addRoom() {
        AtomicBoolean success_flag = new AtomicBoolean(false);
        IRmiAdministrationServices services = this.sessionManager.getAdministrationServices();
        try {
            RoomCategory category = new RoomCategory(
                    -1,
                    Integer.parseInt(roomCapacity_TextField.getText()),
                    Integer.parseInt(roomDimension_TextField.getText())
            );

            category = services.add(this.sessionManager.getToken(), category);

            RoomPrice price = new RoomPrice(
                    -1,
                    Double.parseDouble(price_TextField.getText()),
                    new TimestampUTC(TimestampConverter.nowUTC()).year()
            );

            RoomFixtures fixtures = new RoomFixtures(
                    -1,
                    fixedChairs_CheckBox.isSelected(),
                    cateringSpace_CheckBox.isSelected(),
                    whiteboard_CheckBox.isSelected(),
                    projector_CheckBox.isSelected()
            );

            this.room = new Room(
                    this.room.id(),
                    this.room.floorID(),
                    this.room.buildingID(),
                    category.id(),
                    roomDescription_TextField.getText()

            );

            services.add(this.sessionManager.getToken(), this.room, price, fixtures);
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
        } catch (FailedRecordFetch e) {
            AlertDialog.showExceptionAlert(
                    resourceBundle.getString("ErrorDialogTitle_Deletion"),
                    resourceBundle.getString("ErrorMsg_FailedBackendFetch"),
                    e
            );
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

        field_validation.put(roomDescription_TextField, false);
        field_validation.put(roomCapacity_TextField, false);
        field_validation.put(roomDimension_TextField, false);
        field_validation.put(price_TextField, false);

        roomDescription_TextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() < 3) {
                roomDescription_TextField.setStyle("-fx-control-inner-background: red;");
                field_validation.put(roomDescription_TextField, false);
            } else {
                roomDescription_TextField.setStyle("-fx-control-inner-background: white;");
                field_validation.put(roomDescription_TextField, true);
            }
        });

        roomCapacity_TextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.isEmpty() || !newValue.matches("^\\d*$")) { //REGEX: integer
                roomCapacity_TextField.setStyle("-fx-control-inner-background: red;");
                field_validation.put(roomCapacity_TextField, false);
            } else {
                roomCapacity_TextField.setStyle("-fx-control-inner-background: white;");
                field_validation.put(roomCapacity_TextField, true);
            }
        });

        roomDimension_TextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.isEmpty() || !newValue.matches("^\\d*$")) { //REGEX: integer
                roomDimension_TextField.setStyle("-fx-control-inner-background: red;");
                field_validation.put(roomDimension_TextField, false);
            } else {
                roomDimension_TextField.setStyle("-fx-control-inner-background: white;");
                field_validation.put(roomDimension_TextField, true);
            }
        });

        price_TextField.textProperty().addListener((observable, oldValue, newValue) -> {
            this.price_Label.setText(
                    this.resourceBundle.getString("Label_Price") + " (" + new TimestampUTC(TimestampConverter.nowUTC()).year() + ")"
            );
            if (newValue.isEmpty() || !newValue.matches("\\d+(\\.\\d+)?")) { //REGEX: integer or double
                price_TextField.setStyle("-fx-control-inner-background: red;");
                field_validation.put(price_TextField, false);
            } else {
                price_TextField.setStyle("-fx-control-inner-background: white;");
                field_validation.put(price_TextField, true);
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
     * Loads field for room
     *
     * @param room Room DTO
     * @throws LoginException    when there is not a current session
     * @throws FailedRecordFetch when getting details from the records about the the room fails
     * @throws Unauthorised      when this client session is not authorised to access the resource
     * @throws RemoteException   when network issues occur during the remote call
     */
    void loadRoom(Room room) throws LoginException, FailedRecordFetch, Unauthorised, RemoteException {
        IRmiAdministrationServices services = this.sessionManager.getAdministrationServices();
        DetailedRoom detailedRoom = services.getRoomDetails(this.sessionManager.getToken(), room);
        try {
            Usage<RoomPrice, Integer> price_usage = services.getMostRecentRoomPrice(this.sessionManager.getToken(), room);
            this.price_TextField.setText(String.valueOf(price_usage.dto().price()));
            this.price_Label.setText(this.resourceBundle.getString("Label_Price") + " (" + price_usage.dto().year() + ")");
        } catch (IncompleteRecord e) {
            price_TextField.setStyle("-fx-control-inner-background: red;");
            AlertDialog.showAlert(
                    Alert.AlertType.WARNING,
                    resourceBundle.getString("WarningDialogTitle_Generic"),
                    resourceBundle.getString("WarningMsg_IncompleteRoomPriceRecord")
            );
            log.log_Warning("Backend identified a Room with no RoomPrice: ", detailedRoom.room());
        }
        this.room = room;
        this.controllerRole = ControllerRole.EDIT_ROOM;
        this.buildingId_TextField.setText(String.valueOf(room.buildingID()));
        this.floorId_TextField.setText(String.valueOf(room.floorID()));
        this.roomDescription_TextField.setText(room.description());
        this.roomCapacity_TextField.setText(String.valueOf(detailedRoom.category().capacity()));
        this.roomDimension_TextField.setText(String.valueOf(detailedRoom.category().dimension()));
        this.cateringSpace_CheckBox.setSelected(detailedRoom.fixtures().hasCateringSpace());
        this.fixedChairs_CheckBox.setSelected(detailedRoom.fixtures().hasFixedChairs());
        this.whiteboard_CheckBox.setSelected(detailedRoom.fixtures().hasWhiteboard());
        this.projector_CheckBox.setSelected(detailedRoom.fixtures().hasProjector());
    }

    /**
     * Prepare fields for new room for a floor
     *
     * @param floor Floor DTO
     */
    void loadRoom(Floor floor) {
        this.room = null;
        this.controllerRole = ControllerRole.NEW_ROOM;
        this.buildingId_TextField.setText(String.valueOf(floor.buildingID()));
        this.floorId_TextField.setText(String.valueOf(floor.floorID()));
        this.cateringSpace_CheckBox.setDisable(false);
        this.fixedChairs_CheckBox.setDisable(false);
        this.whiteboard_CheckBox.setDisable(false);
        this.projector_CheckBox.setDisable(false);
    }

    /**
     * Gets the created/updated room
     *
     * @return Room if created/updated
     */
    Optional<Room> getRoom() {
        return Optional.ofNullable(this.room);
    }

    public void handleCancelAction(ActionEvent actionEvent) {
        this.room = null;
        cancelButton.getScene().getWindow().hide();
    }

    public void handleSaveAction(ActionEvent actionEvent) {
        if (checkValidationFlags()) {
            switch (this.controllerRole) {
                case NEW_ROOM:
                    if (addRoom()) {
                        log.log("Room added: ", this.room);
                        mainWindowController.status_left.setText(this.resourceBundle.getString("Status_RoomCreated"));
                    }
                    break;
                case EDIT_ROOM:
                    if (updateRoom()) {
                        log.log("Floor updated: ", this.room);
                        mainWindowController.status_left.setText(this.resourceBundle.getString("Status_RoomUpdated"));
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
