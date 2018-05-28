package bbk_beam.mtRooms.ui.controller.frontdesk;

import bbk_beam.mtRooms.db.exception.DbQueryException;
import bbk_beam.mtRooms.exception.LoginException;
import bbk_beam.mtRooms.network.IRmiServices;
import bbk_beam.mtRooms.network.exception.Unauthorised;
import bbk_beam.mtRooms.reservation.dto.Customer;
import bbk_beam.mtRooms.reservation.exception.FailedDbFetch;
import bbk_beam.mtRooms.reservation.exception.InvalidCustomer;
import bbk_beam.mtRooms.ui.controller.MainWindowController;
import bbk_beam.mtRooms.ui.model.SessionManager;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.util.Pair;

import java.net.URL;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class CustomerSearchController implements Initializable {
    /**
     * Local DTO class to store customer ID and full names for use in a ChoiceBox
     */
    class CustomerEntry {
        Integer id;
        String name;

        /**
         * Constructor
         *
         * @param id   Customer ID
         * @param name Customer title/name/surname line
         */
        CustomerEntry(Integer id, String name) {
            this.id = id;
            this.name = name;
        }

        @Override
        public String toString() {
            return "[" + id + "] " + name;
        }
    }

    public TabPane search_tabPane;
    public Tab search_tab;
    public Tab result_tab;
    public TextField id_field;
    public TextField surname_field;
    public Button searchID_button;
    public Button searchSurname_button;
    public ChoiceBox<CustomerEntry> customer_choiceBox;
    private SessionManager sessionManager;
    private MainWindowController mainWindowController;
    private ResourceBundle resourceBundle;

    /**
     * Loads the choice box with the customer results finding
     *
     * @param list List of results
     */
    private void loadSearchResultsChoiceBox(List<Pair<Integer, String>> list) {
        List<CustomerEntry> customer_list = new ArrayList<>();
        for (Pair<Integer, String> entry : list) {
            customer_list.add(new CustomerEntry(entry.getKey(), entry.getValue()));
        }
        customer_choiceBox.setItems(FXCollections.observableList(customer_list));
        customer_choiceBox.getSelectionModel().select(0);
    }

    /**
     * Helper method for showing an alert dialog
     *
     * @param msg Message to print in the dialog
     */
    private void showErrorAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(this.resourceBundle.getString("ErrorDialogTitle_CustomerSearch"));
        alert.setHeaderText(msg);
        alert.showAndWait();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.resourceBundle = resources;
        searchID_button.setDisable(true);
        searchSurname_button.setDisable(true);
        id_field.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("^\\d*$")) {
                id_field.setStyle("-fx-control-inner-background: red;");
                searchID_button.setDisable(true);
            } else if (newValue.isEmpty()) {
                id_field.setStyle("-fx-control-inner-background: white;");
                searchID_button.setDisable(true);
            } else {
                id_field.setStyle("-fx-control-inner-background: white;");
                searchID_button.setDisable(false);
            }
        });
        surname_field.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.isEmpty())
                searchSurname_button.setDisable(true);
            else
                searchSurname_button.setDisable(false);
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
     * Shows the customer search results (for surname search)
     */
    private void showSearchResults(List<Pair<Integer, String>> result_list) {
        loadSearchResultsChoiceBox(result_list);
        result_tab.setDisable(false);
        search_tabPane.getSelectionModel().select(result_tab);
    }

    @FXML
    public void handleSearchIDAction(ActionEvent actionEvent) {
        surname_field.clear();
        IRmiServices services = this.sessionManager.getServices();
        try {
            Customer customer = services.getCustomerAccount(this.sessionManager.getToken(), Integer.valueOf(id_field.getText()));
            System.out.println(customer);
        } catch (DbQueryException e) {
            e.printStackTrace();
        } catch (InvalidCustomer invalidCustomer) {
            showErrorAlert(resourceBundle.getString("ErrorMsg_InvalidCustomerID"));
            id_field.setStyle("-fx-control-inner-background: red;");
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (LoginException e) {
            e.printStackTrace();
        } catch (Unauthorised unauthorised) {
            unauthorised.printStackTrace();
        }
    }

    @FXML
    public void handleSearchSurnameAction(ActionEvent actionEvent) {
        id_field.clear();
        IRmiServices services = this.sessionManager.getServices();
        try {
            List<Pair<Integer, String>> list = services.findCustomer(this.sessionManager.getToken(), surname_field.getText());
            if (list.isEmpty())
                System.out.println("No customers found by this surname: '" + surname_field.getText() + "'.");
            showSearchResults(list);
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (LoginException e) {
            e.printStackTrace();
        } catch (Unauthorised unauthorised) {
            unauthorised.printStackTrace();
        } catch (FailedDbFetch failedDbFetch) {
            failedDbFetch.printStackTrace();
        }
    }

    public void handleOkAction(ActionEvent actionEvent) {
    }
}
