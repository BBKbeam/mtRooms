package bbk_beam.mtRooms.ui.controller.administration;

import bbk_beam.mtRooms.MtRoomsGUI;
import bbk_beam.mtRooms.admin.exception.FailedRecordFetch;
import bbk_beam.mtRooms.admin.exception.FailedRecordWrite;
import bbk_beam.mtRooms.exception.LoginException;
import bbk_beam.mtRooms.network.IRmiAdministrationServices;
import bbk_beam.mtRooms.network.exception.Unauthorised;
import bbk_beam.mtRooms.reservation.dto.Discount;
import bbk_beam.mtRooms.reservation.dto.Membership;
import bbk_beam.mtRooms.ui.controller.MainWindowController;
import bbk_beam.mtRooms.ui.controller.common.AlertDialog;
import bbk_beam.mtRooms.ui.controller.common.FieldValidator;
import bbk_beam.mtRooms.ui.model.SessionManager;
import bbk_beam.mtRooms.ui.model.administration.DiscountItem;
import bbk_beam.mtRooms.ui.model.administration.DiscountList;
import eadjlib.logger.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicBoolean;

public class MembershipController implements Initializable {
    private final Logger log = Logger.getLoggerInstance(MembershipController.class.getName());
    private AlertDialog alertDialog;
    private FieldValidator fieldValidator;
    private SessionManager sessionManager;
    private MainWindowController mainWindowController;
    private ResourceBundle resourceBundle;
    private Membership membership = null;

    private DiscountList discountList; //ComboBox Model

    public TextField membershipDescription_TextField;
    public ComboBox<DiscountItem> discount_ComboBox;
    public Button addDiscount_Button;
    public Button cancelButton;
    public Button saveButton;

    /**
     * Adds a Membership to the records
     *
     * @return Success
     */
    private boolean addMembership() {
        AtomicBoolean success_flag = new AtomicBoolean(false);
        IRmiAdministrationServices services = this.sessionManager.getAdministrationServices();
        try {
            this.membership = new Membership(
                    -1,
                    this.membershipDescription_TextField.getText(),
                    this.discount_ComboBox.getSelectionModel().getSelectedItem().getDiscount()
            );
            this.membership = services.add(this.sessionManager.getToken(), this.membership);
            success_flag.set(true);
        } catch (FailedRecordWrite e) {
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
        return success_flag.get();
    }

    private void showDiscountDialog() {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(MtRoomsGUI.class.getResource("/view/administration/DiscountView.fxml"));
        loader.setResources(this.resourceBundle);
        try {
            AnchorPane pane = loader.load();
            DiscountController discountController = loader.getController();
            discountController.setMainWindowController(this.mainWindowController);
            discountController.setSessionManager(this.sessionManager);
            Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setOnHiding(event -> {
                if (discountController.getDiscount().isPresent()) {
                    this.discountList.addData(discountController.getDiscount().get());
                    this.discount_ComboBox.getSelectionModel().selectLast();
                }
            });
            dialog.setTitle(this.resourceBundle.getString("DialogTitle_NewDiscount"));
            Scene scene = new Scene(pane);
            dialog.setScene(scene);
            dialog.show();
        } catch (IOException e) {
            log.log_Error("Could not load the 'Create discount' dialog.");
            log.log_Exception(e);
            this.alertDialog.showGenericError(e);
        }
    }

    /**
     * Loads Discount content into discount_ComboBox
     */
    private void loadDiscountComboBox() {
        try {
            IRmiAdministrationServices services = this.sessionManager.getAdministrationServices();
            List<Discount> discounts = services.getDiscounts(this.sessionManager.getToken());
            this.discountList = new DiscountList(this.sessionManager);
            this.discountList.loadData(discounts);
            this.discount_ComboBox.setItems(discountList.getData());
            this.discount_ComboBox.getSelectionModel().selectFirst();
        } catch (FailedRecordFetch e) {
            AlertDialog.showExceptionAlert(
                    resourceBundle.getString("ErrorDialogTitle_Generic"),
                    resourceBundle.getString("ErrorMsg_FailedBackendFetch"),
                    e
            );
        } catch (Unauthorised e) {
            this.alertDialog.showGenericError(e);
        } catch (RemoteException e) {
            this.alertDialog.showGenericError(e);
        } catch (LoginException e) {
            this.alertDialog.showGenericError(e);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.resourceBundle = resources;
        this.alertDialog = new AlertDialog(resources);
        this.fieldValidator = new FieldValidator();

        this.fieldValidator.add(this.membershipDescription_TextField);

        membershipDescription_TextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() < 3) {
                membershipDescription_TextField.setStyle("-fx-control-inner-background: red;");
                this.fieldValidator.set(membershipDescription_TextField, false);
            } else {
                membershipDescription_TextField.setStyle("-fx-control-inner-background: white;");
                this.fieldValidator.set(membershipDescription_TextField, true);
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
        loadDiscountComboBox();
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
     * Gets the created Membership DTO if any
     *
     * @return Optional Membership DTO
     */
    Optional<Membership> getMembership() {
        return Optional.ofNullable(this.membership);
    }

    @FXML
    public void handleCancelAction(ActionEvent actionEvent) {
        this.membership = null;
        cancelButton.getScene().getWindow().hide();
    }

    @FXML
    public void handleSaveAction(ActionEvent actionEvent) {
        if (this.fieldValidator.check()) {
            if (addMembership()) {
                log.log("Membership added: ", this.membership);
                mainWindowController.status_left.setText(this.resourceBundle.getString("Status_MembershipCreated"));
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

    @FXML
    public void handleNewDiscountAction(ActionEvent actionEvent) {
        showDiscountDialog();
    }
}
