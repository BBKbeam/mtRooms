package bbk_beam.mtRooms.ui.controller.frontdesk;

import bbk_beam.mtRooms.reservation.dto.Payment;
import bbk_beam.mtRooms.ui.controller.MainWindowController;
import eadjlib.logger.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.text.Text;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ResourceBundle;

public class PaymentDetailsController implements Initializable {
    static private final SimpleDateFormat dateFormat = new SimpleDateFormat("YYYY/MM/dd - HH:mm:ss");
    private final Logger log = Logger.getLoggerInstance(PaymentDetailsController.class.getName());
    private MainWindowController mainWindowController;
    private ResourceBundle resourceBundle;

    public Text amount_Text;
    public Text method_Text;
    public Text timestamp_Text;
    public TextArea hash_TextArea;
    public TextArea notes_TextArea;
    public Button closeButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.resourceBundle = resources;
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
     * @param payment Payment DTO
     */
    void load(Payment payment) {
        this.amount_Text.setText(String.format("%.2f", payment.amount()));
        this.method_Text.setText(payment.paymentMethod().description());
        this.timestamp_Text.setText(dateFormat.format(payment.timestamp()));
        this.hash_TextArea.setText(payment.hashID());
        this.notes_TextArea.setText(payment.note());
    }

    public void handleCloseAction(ActionEvent actionEvent) {
        closeButton.getScene().getWindow().hide();
    }
}
