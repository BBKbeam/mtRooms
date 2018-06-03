package bbk_beam.mtRooms.ui.controller.frontdesk;

import bbk_beam.mtRooms.MtRoomsGUI;
import bbk_beam.mtRooms.db.exception.DbQueryException;
import bbk_beam.mtRooms.exception.LoginException;
import bbk_beam.mtRooms.network.IRmiServices;
import bbk_beam.mtRooms.network.exception.Unauthorised;
import bbk_beam.mtRooms.reservation.dto.Customer;
import bbk_beam.mtRooms.reservation.exception.FailedDbFetch;
import bbk_beam.mtRooms.reservation.exception.InvalidCustomer;
import bbk_beam.mtRooms.reservation.exception.InvalidMembership;
import bbk_beam.mtRooms.ui.AlertDialog;
import bbk_beam.mtRooms.ui.controller.MainWindowController;
import bbk_beam.mtRooms.ui.model.SessionManager;
import eadjlib.logger.Logger;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.io.IOException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class CustomerSearchController implements Initializable {
    private final Logger log = Logger.getLoggerInstance(CustomerSearchController.class.getName());
    private AlertDialog alertDialog;
    private SessionManager sessionManager;
    private MainWindowController mainWindowController;
    private ResourceBundle resourceBundle;

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
    public Button createCustomer_button;
    public Text searchSummary_TextField;
    public ChoiceBox<CustomerEntry> customer_choiceBox;
    public Button ok_button;

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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.resourceBundle = resources;
        this.alertDialog = new AlertDialog(resources);

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

        id_field.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                searchID_button.setDefaultButton(true);
                searchSurname_button.setDefaultButton(false);
                createCustomer_button.setDefaultButton(false);
            }
        });

        surname_field.textProperty().addListener((observable, oldValue, newValue) -> {
            surname_field.setStyle("-fx-control-inner-background: white;");
            if (newValue.isEmpty())
                searchSurname_button.setDisable(true);
            else
                searchSurname_button.setDisable(false);
        });

        surname_field.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                searchID_button.setDefaultButton(false);
                searchSurname_button.setDefaultButton(true);
                createCustomer_button.setDefaultButton(false);
            }
        });

        createCustomer_button.focusedProperty().addListener(((observable, oldValue, newValue) -> {
            if (newValue) {
                searchID_button.setDefaultButton(false);
                searchSurname_button.setDefaultButton(false);
                createCustomer_button.setDefaultButton(true);
            }
        }));
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
        searchSummary_TextField.setText(this.resourceBundle.getString("TextField_CustomerAccountFound") + result_list.size());
        result_tab.setDisable(false);
        search_tabPane.getSelectionModel().select(result_tab);
        ok_button.defaultButtonProperty().bind(ok_button.focusedProperty());
        if (result_list.size() == 1)
            ok_button.requestFocus();
        else
            customer_choiceBox.requestFocus();
    }

    /**
     * Shows the customer creation dialog
     */
    private void showCreateCustomerDialog() {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(MtRoomsGUI.class.getResource("/view/frontdesk/CustomerCreationView.fxml"));
        loader.setResources(resourceBundle);
        try {
            AnchorPane pane = loader.load();
            CustomerCreationController customerCreationController = loader.getController();
            customerCreationController.setSessionManager(sessionManager);
            customerCreationController.setMainWindowController(this.mainWindowController);
            customerCreationController.loadCustomer(null);
            Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setOnHiding(event -> {
                if (customerCreationController.getCustomer().isPresent()) {
                    try {
                        showCustomerAccountView(customerCreationController.getCustomer().get());
                    } catch (FailedDbFetch e) {
                        this.alertDialog.showGenericError(e);
                    } catch (InvalidMembership e) {
                        this.alertDialog.showGenericError(e);
                    } catch (LoginException e) {
                        this.alertDialog.showGenericError(e);
                    } catch (Unauthorised e) {
                        this.alertDialog.showGenericError(e);
                    }
                }
            });
            Scene scene = new Scene(pane);
            dialog.setScene(scene);
            dialog.show();
        } catch (IOException e) {
            log.log_Error("Could not load the 'create customer' dialog.");
            log.log_Exception(e);
            this.alertDialog.showGenericError(e);
        } catch (LoginException e) {
            this.alertDialog.showGenericError(e);
        } catch (Unauthorised e) {
            this.alertDialog.showGenericError(e);
        } catch (FailedDbFetch e) {
            this.alertDialog.showGenericError(e);
        } catch (InvalidMembership e) {
            this.alertDialog.showGenericError(e);
        }
    }

    /**
     * Shows the customer account view
     *
     * @param customer Customer DTO
     */
    private void showCustomerAccountView(Customer customer) throws LoginException, Unauthorised, InvalidMembership, FailedDbFetch {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(MtRoomsGUI.class.getResource("/view/frontdesk/CustomerAccountView.fxml"));
        loader.setResources(resourceBundle);
        try {
            SplitPane pane = loader.load();
            CustomerAccountController customerAccountController = loader.getController();
            customerAccountController.setSessionManager(sessionManager);
            customerAccountController.setMainWindowController(this.mainWindowController);
            customerAccountController.loadCustomer(customer);
            mainWindowController.main_pane.setFitToWidth(true);
            mainWindowController.main_pane.setFitToHeight(true);
            mainWindowController.main_pane.setContent(pane);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleSearchIDAction(ActionEvent actionEvent) {
        surname_field.clear();
        IRmiServices services = this.sessionManager.getServices();
        try {
            Customer customer = services.getCustomerAccount(this.sessionManager.getToken(), Integer.valueOf(id_field.getText()));
            showCustomerAccountView(customer);
        } catch (DbQueryException e) {
            log.log_Fatal("Db query in server was invalid. See server log for more info.");
            log.log_Exception(e);
            this.alertDialog.showGenericError(e);
        } catch (InvalidCustomer e) {
            this.alertDialog.showGenericError(e);
            id_field.setStyle("-fx-control-inner-background: red;");
        } catch (RemoteException e) {
            this.alertDialog.showGenericError(e);
        } catch (LoginException e) {
            this.alertDialog.showGenericError(e);
        } catch (Unauthorised e) {
            this.alertDialog.showGenericError(e);
        } catch (FailedDbFetch e) {
            this.alertDialog.showGenericError(e);
        } catch (InvalidMembership e) {
            this.alertDialog.showGenericError(e);
        }
    }

    @FXML
    public void handleSearchSurnameAction(ActionEvent actionEvent) {
        id_field.clear();
        IRmiServices services = this.sessionManager.getServices();
        try {
            List<Pair<Integer, String>> list = services.findCustomer(this.sessionManager.getToken(), surname_field.getText());
            if (!list.isEmpty())
                showSearchResults(list);
            else {
                surname_field.setStyle("-fx-control-inner-background: red;");
                ///TODO status bar msg
            }
        } catch (RemoteException e) {
            this.alertDialog.showGenericError(e);
        } catch (LoginException e) {
            this.alertDialog.showGenericError(e);
        } catch (Unauthorised e) {
            this.alertDialog.showGenericError(e);
        } catch (FailedDbFetch e) {
            this.alertDialog.showGenericError(e);
        }
    }

    @FXML
    public void handleCreateCustomerAction(ActionEvent actionEvent) {
        showCreateCustomerDialog();
    }

    @FXML
    public void handleOkAction(ActionEvent actionEvent) {
        try {
            IRmiServices services = this.sessionManager.getServices();
            Integer customer_id = customer_choiceBox.getSelectionModel().getSelectedItem().id;
            Customer customer = services.getCustomerAccount(this.sessionManager.getToken(), customer_id);
            showCustomerAccountView(customer);
        } catch (InvalidCustomer e) {
            e.printStackTrace(); //TODO
        } catch (DbQueryException e) {
            log.log_Fatal("Db query in server was invalid. See server log for more info.");
            log.log_Exception(e);
            this.alertDialog.showGenericError(e);
        } catch (Unauthorised e) {
            this.alertDialog.showGenericError(e);
        } catch (RemoteException e) {
            this.alertDialog.showGenericError(e);
        } catch (LoginException e) {
            this.alertDialog.showGenericError(e);
        } catch (FailedDbFetch e) {
            this.alertDialog.showGenericError(e);
        } catch (InvalidMembership e) {
            this.alertDialog.showGenericError(e);
        }

    }
}
