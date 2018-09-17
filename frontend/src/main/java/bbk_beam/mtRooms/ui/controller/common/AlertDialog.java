package bbk_beam.mtRooms.ui.controller.common;

import bbk_beam.mtRooms.db.exception.DbQueryException;
import bbk_beam.mtRooms.exception.LoginException;
import bbk_beam.mtRooms.exception.RemoteFailure;
import bbk_beam.mtRooms.network.exception.Unauthorised;
import bbk_beam.mtRooms.reservation.exception.*;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.rmi.RemoteException;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Alert dialog helper/wrapper
 */
public class AlertDialog {
    private ResourceBundle resourceBundle;

    /**
     * Show a confirmation
     *
     * @param title   Dialog title
     * @param header  Dialog header
     * @param content Dialog content
     * @return Button pressed
     */
    static public Optional<ButtonType> showConfirmation(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        return alert.showAndWait();
    }

    /**
     * Show an alert
     *
     * @param type   Alert type
     * @param title  Dialog title
     * @param header Dialog header
     */
    static public void showAlert(Alert.AlertType type, String title, String header) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.showAndWait();
    }

    /**
     * Show an alert
     *
     * @param type    Alert type
     * @param title   Dialog title
     * @param header  Dialog header
     * @param content Dialog content
     */
    static public void showAlert(Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);

        TextArea textArea = new TextArea(content);
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(textArea, 0, 0);
        alert.getDialogPane().setExpandableContent(expContent);

        alert.showAndWait();
    }

    /**
     * Show an alert
     *
     * @param title  Dialog title
     * @param header Dialog header
     * @param e      Exception
     */
    static public void showExceptionAlert(String title, String header, Exception e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);

        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        Label label = new Label("Stacktrace:");

        TextArea textArea = new TextArea(sw.toString());
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(label, 0, 0);
        expContent.add(textArea, 0, 1);
        alert.getDialogPane().setExpandableContent(expContent);

        alert.showAndWait();
    }

    /**
     * Constructor
     *
     * @param resources ResourceBundle used
     */
    public AlertDialog(ResourceBundle resources) {
        this.resourceBundle = resources;
    }

    /**
     * Shows a generic alert dialog
     *
     * @param e Unauthorised exception
     */
    public void showGenericError(Unauthorised e) {
        AlertDialog.showAlert(
                Alert.AlertType.ERROR,
                this.resourceBundle.getString("ErrorDialogTitle_Generic"),
                this.resourceBundle.getString("ErrorMsg_Unauthorized")
        );
    }

    /**
     * Shows a generic alert dialog
     *
     * @param e LoginException exception
     */
    public void showGenericError(LoginException e) {
        AlertDialog.showAlert(
                Alert.AlertType.ERROR,
                this.resourceBundle.getString("ErrorDialogTitle_Generic"),
                this.resourceBundle.getString("ErrorMsg_LoggedOut")
        );
    }

    /**
     * Shows a generic alert dialog
     *
     * @param e RemoteException exception
     */
    public void showGenericError(RemoteException e) {
        AlertDialog.showExceptionAlert(
                this.resourceBundle.getString("ErrorDialogTitle_Generic"),
                this.resourceBundle.getString("ErrorMsg_RemoteIssue"),
                e
        );
    }

    /**
     * Shows a generic alert dialog
     *
     * @param e RemoteFailure exception
     */
    public void showGenericError(RemoteFailure e) {
        AlertDialog.showExceptionAlert(
                this.resourceBundle.getString("ErrorDialogTitle_Generic"),
                this.resourceBundle.getString("ErrorMsg_RemoteFailure"),
                e
        );
    }

    /**
     * Shows a generic alert dialog
     *
     * @param e FailedDbFetch exception
     */
    public void showGenericError(FailedDbFetch e) {
        AlertDialog.showExceptionAlert(
                this.resourceBundle.getString("ErrorDialogTitle_UserAccount"),
                this.resourceBundle.getString("ErrorMsg_FailedBackendFetch"),
                e
        );
    }

    /**
     * Shows a generic alert dialog
     *
     * @param e FailedDbWrite exception
     */
    public void showGenericError(FailedDbWrite e) {
        AlertDialog.showExceptionAlert(
                this.resourceBundle.getString("ErrorDialogTitle_Generic"),
                this.resourceBundle.getString("ErrorMsg_FailedBackendWrite"),
                e
        );
    }

    /**
     * Shows a generic alert dialog
     *
     * @param e DbQueryException exception
     */
    public void showGenericError(DbQueryException e) {
        AlertDialog.showExceptionAlert(
                this.resourceBundle.getString("ErrorDialogTitle_Generic"),
                this.resourceBundle.getString("ErrorMsg_DbQueryException"),
                e
        );
    }

    /**
     * Shows a generic alert dialog
     *
     * @param e InvalidMembership exception
     */
    public void showGenericError(InvalidMembership e) {
        AlertDialog.showAlert(
                Alert.AlertType.ERROR,
                this.resourceBundle.getString("ErrorDialogTitle_Generic"),
                this.resourceBundle.getString("ErrorMsg_InvalidMembership")
        );
    }

    /**
     * Shows a generic alert dialog
     *
     * @param e InvalidCustomer exception
     */
    public void showGenericError(InvalidCustomer e) {
        AlertDialog.showAlert(
                Alert.AlertType.ERROR,
                this.resourceBundle.getString("ErrorDialogTitle_Generic"),
                resourceBundle.getString("ErrorMsg_InvalidCustomerID")
        );
    }

    /**
     * Shows a generic alert dialog
     *
     * @param e InvalidReservation exception
     */
    public void showGenericError(InvalidReservation e) {
        AlertDialog.showAlert(
                Alert.AlertType.ERROR,
                this.resourceBundle.getString("ErrorDialogTitle_Generic"),
                resourceBundle.getString("ErrorMsg_ReservationID")
        );
    }

    /**
     * Shows a generic alert dialog
     *
     * @param e IOException exception
     */
    public void showGenericError(IOException e) {
        AlertDialog.showExceptionAlert(
                this.resourceBundle.getString("ErrorDialogTitle_Generic"),
                this.resourceBundle.getString("ErrorMsg_UiResourceIO"),
                e
        );
    }
}
