package bbk_beam.mtRooms.ui.controller.frontdesk;

import bbk_beam.mtRooms.exception.LoginException;
import bbk_beam.mtRooms.network.IRmiReservationServices;
import bbk_beam.mtRooms.network.exception.Unauthorised;
import bbk_beam.mtRooms.reservation.dto.Reservation;
import bbk_beam.mtRooms.reservation.dto.RoomReservation;
import bbk_beam.mtRooms.reservation.exception.FailedDbWrite;
import bbk_beam.mtRooms.reservation.exception.InvalidReservation;
import bbk_beam.mtRooms.ui.controller.MainWindowController;
import bbk_beam.mtRooms.ui.controller.common.AlertDialog;
import bbk_beam.mtRooms.ui.model.SessionManager;
import eadjlib.logger.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.text.Text;

import java.net.URL;
import java.rmi.RemoteException;
import java.util.Optional;
import java.util.ResourceBundle;

public class RoomReservationDetailsController implements Initializable {
    private final Logger log = Logger.getLoggerInstance(RoomReservationDetailsController.class.getName());
    private AlertDialog alertDialog;
    private SessionManager sessionManager;
    private MainWindowController mainWindowController;
    private ResourceBundle resourceBundle;
    private Reservation reservation = null;
    private RoomReservation room_reservation = null;
    private boolean updated_flag;

    public Label price_Label;
    public Text price_Text;
    public TextArea note_TextArea;
    public Button closeButton;
    public Button saveButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.resourceBundle = resources;
        this.alertDialog = new AlertDialog(resources);
        this.updated_flag = false;

        this.note_TextArea.textProperty().addListener((observable, oldValue, newValue) -> {
            if (this.room_reservation != null) {
                if (!newValue.equals(this.room_reservation.note())) {
                    this.saveButton.setDisable(false);
                    this.updated_flag = true;
                } else {
                    this.saveButton.setDisable(true);
                }
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
     * Load information into fields
     *
     * @param room_reservation RoomReservation DTO
     */
    void load(Reservation reservation, RoomReservation room_reservation) {
        this.reservation = reservation;
        this.room_reservation = room_reservation;
        this.updated_flag = false;
        this.saveButton.setDisable(true);

        this.price_Label.setText(this.resourceBundle.getString("Label_Price") + " (" + room_reservation.price().year() + "): ");
        this.price_Text.setText(String.valueOf(room_reservation.price().price()));
        this.note_TextArea.setText(room_reservation.note());
    }

    /**
     * Gets the updated RoomReservation if any
     *
     * @return RoomReservation if updated
     */
    Optional<RoomReservation> getRoomReservation() {
        return (updated_flag ? Optional.of(this.room_reservation) : Optional.empty());
    }

    public void handleCloseAction(ActionEvent actionEvent) {
        this.reservation = null;
        this.room_reservation = null;
        this.updated_flag = false;
        closeButton.getScene().getWindow().hide();
    }

    public void handleSaveAction(ActionEvent actionEvent) {
        if (updated_flag) {
            try {
                this.room_reservation = new RoomReservation(
                        this.room_reservation.room(),
                        this.room_reservation.reservationStart(),
                        this.room_reservation.reservationEnd(),
                        this.room_reservation.seatedCount(),
                        this.room_reservation.hasCateringRequired(),
                        this.note_TextArea.getText(), //New text
                        this.room_reservation.price(),
                        this.room_reservation.isCancelled()
                );

                IRmiReservationServices services = this.sessionManager.getReservationServices();
                services.updateReservedRoomNote(this.sessionManager.getToken(), this.reservation, this.room_reservation);

                log.log("RoomReservation updated: ", this.room_reservation);
                mainWindowController.status_left.setText(this.resourceBundle.getString("Status_RoomReservationNoteUpdated"));
                saveButton.getScene().getWindow().hide();
            } catch (InvalidReservation e) {
                log.log_Error("Reservation [", this.reservation.id(), "] is invalid with RoomReservation: ", this.room_reservation);
                log.log_Exception(e);
                AlertDialog.showExceptionAlert(
                        this.resourceBundle.getString("ErrorDialogTitle_Generic"),
                        this.resourceBundle.getString("ErrorMsg_InvalidReservation"),
                        e
                );
            } catch (FailedDbWrite e) {
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
}
