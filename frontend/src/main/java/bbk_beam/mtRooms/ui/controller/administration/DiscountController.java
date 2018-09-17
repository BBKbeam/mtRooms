package bbk_beam.mtRooms.ui.controller.administration;

import bbk_beam.mtRooms.reservation.dto.Discount;
import bbk_beam.mtRooms.reservation.dto.DiscountCategory;
import bbk_beam.mtRooms.ui.controller.MainWindowController;
import bbk_beam.mtRooms.ui.controller.common.AlertDialog;
import bbk_beam.mtRooms.ui.controller.common.FieldValidator;
import bbk_beam.mtRooms.ui.model.SessionManager;
import eadjlib.logger.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class DiscountController implements Initializable {
    private final Logger log = Logger.getLoggerInstance(DiscountController.class.getName());
    private AlertDialog alertDialog;
    private FieldValidator fieldValidator;
    private SessionManager sessionManager;
    private MainWindowController mainWindowController;
    private ResourceBundle resourceBundle;
    private Discount discount = null;

    public TextField categoryDescription_TextField;
    public TextField discountRate_TextField;
    public Button cancelButton;
    public Button saveButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.resourceBundle = resources;
        this.alertDialog = new AlertDialog(resources);
        this.fieldValidator = new FieldValidator();

        this.fieldValidator.add(categoryDescription_TextField);
        this.fieldValidator.add(discountRate_TextField);

        categoryDescription_TextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() < 3) {
                categoryDescription_TextField.setStyle("-fx-control-inner-background: red;");
                this.fieldValidator.set(categoryDescription_TextField, false);
            } else {
                categoryDescription_TextField.setStyle("-fx-control-inner-background: white;");
                this.fieldValidator.set(categoryDescription_TextField, true);
            }
        });

        discountRate_TextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.isEmpty() || !newValue.matches("\\d+(\\.\\d+)?")) { //REGEX: integer or double
                discountRate_TextField.setStyle("-fx-control-inner-background: red;");
                fieldValidator.set(discountRate_TextField, false);
            } else {
                discountRate_TextField.setStyle("-fx-control-inner-background: white;");
                fieldValidator.set(discountRate_TextField, true);
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
     * Gets the created Discount DTO if any
     *
     * @return Optional Discount DTO
     */
    Optional<Discount> getDiscount() {
        return Optional.ofNullable(this.discount);
    }

    @FXML
    public void handleCancelAction(ActionEvent actionEvent) {
        this.discount = null;
        cancelButton.getScene().getWindow().hide();
    }

    @FXML
    public void handleSaveAction(ActionEvent actionEvent) {
        if (this.fieldValidator.check()) {
            this.discount = new Discount(
                    -1,
                    Double.parseDouble(this.discountRate_TextField.getText()),
                    new DiscountCategory(
                            -1,
                            this.categoryDescription_TextField.getText()
                    )
            );
            mainWindowController.status_left.setText(this.resourceBundle.getString("Status_DiscountCreated"));
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
