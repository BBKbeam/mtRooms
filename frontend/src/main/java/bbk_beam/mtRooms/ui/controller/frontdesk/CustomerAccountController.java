package bbk_beam.mtRooms.ui.controller.frontdesk;

import bbk_beam.mtRooms.MtRoomsGUI;
import bbk_beam.mtRooms.exception.LoginException;
import bbk_beam.mtRooms.network.IRmiReservationServices;
import bbk_beam.mtRooms.network.IRmiRevenueServices;
import bbk_beam.mtRooms.network.exception.Unauthorised;
import bbk_beam.mtRooms.reservation.dto.*;
import bbk_beam.mtRooms.reservation.exception.FailedDbFetch;
import bbk_beam.mtRooms.reservation.exception.FailedDbWrite;
import bbk_beam.mtRooms.reservation.exception.InvalidMembership;
import bbk_beam.mtRooms.reservation.exception.InvalidReservation;
import bbk_beam.mtRooms.revenue.dto.CustomerBalance;
import bbk_beam.mtRooms.ui.controller.MainWindowController;
import bbk_beam.mtRooms.ui.controller.common.AlertDialog;
import bbk_beam.mtRooms.ui.model.SessionManager;
import bbk_beam.mtRooms.ui.model.frontdesk.*;
import eadjlib.logger.Logger;
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

import java.io.IOException;
import java.net.URL;
import java.rmi.RemoteException;
import java.sql.Date;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class CustomerAccountController implements Initializable {
    private final Logger log = Logger.getLoggerInstance(CustomerAccountController.class.getName());
    private AlertDialog alertDialog;
    private SessionManager sessionManager;
    private MainWindowController mainWindowController;
    private ResourceBundle resourceBundle;
    private Customer customer;
    private Membership membership;

    //Tabs
    public TabPane customerAccount_TabPane;
    public Tab customer_Tab;
    public Tab reservation_Tab;
    public Tab payment_Tab;
    //Context menus
    private ContextMenu reservation_menu = new ContextMenu();
    private ContextMenu roomReservation_menu = new ContextMenu();
    //Top bar
    public Button closeAccount_Button;
    public Label customer_field;
    //Customer tab
    public Text id_field;
    public Text address1_field;
    public Text address2_field;
    public Text city_field;
    public Text county_field;
    public Text country_field;
    public Text phone1_field;
    public Text phone2_field;
    public Text email_field;
    public Text creationDate_field;
    public Text membershipType_field;
    public Text discountRate_field;
    //Reservation tab
    public Button newReservation_button;
    public Button cancelReservation_button;
    public Button invoice_Button;
    public Button viewRoomReservationDetails_Button;
    public Button cancelRoomReservation_button;
    public TableView<ReservationModel> reservation_Table;
    public TableColumn<ReservationModel, Integer> reservationId_col;
    public TableColumn<ReservationModel, String> created_col;
    public TableColumn<ReservationModel, String> discount_col;
    public TableView<RoomReservationModel> reservationDetails_Table;
    public TableColumn room_col;
    public TableColumn<RoomReservationModel, Integer> buildingId_col;
    public TableColumn<RoomReservationModel, Integer> floorId_col;
    public TableColumn<RoomReservationModel, Integer> roomId_col;
    public TableColumn timeSpan_col;
    public TableColumn<RoomReservationModel, String> in_col;
    public TableColumn<RoomReservationModel, String> out_col;
    public TableColumn<RoomReservationModel, String> duration_col;
    public TableColumn<RoomReservationModel, Integer> seated_col;
    public TableColumn<RoomReservationModel, String> catering_col;
    public TableColumn<RoomReservationModel, String> cancelled_col;
    public TableColumn<RoomReservationModel, String> hasNote_col;
    //Payments tab
    public Button addReservationPayment_Button;
    public Button createRefund_Button;
    public Button viewPaymentDetails_Button;
    public Button detailedBalance_Button;
    public TextField customerCreditField_TextField;
    public TableView<CustomerPaymentsModel> payments_TableView;
    public TableColumn<CustomerPaymentsModel, Integer> paymentID_col;
    public TableColumn<CustomerPaymentsModel, String> paymentTimestamp_col;
    public TableColumn<CustomerPaymentsModel, Double> paymentAmount_col;
    public TableColumn<CustomerPaymentsModel, Integer> paymentReservationID_col;
    public TableColumn<CustomerPaymentsModel, String> paymentMethod_col;
    public TableColumn<CustomerPaymentsModel, String> paymentNote_col;

    /**
     * Shows the customer edit dialog
     */
    private void showEditCustomerDialog(Customer customer) {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(MtRoomsGUI.class.getResource("/view/frontdesk/CustomerCreationView.fxml"));
        loader.setResources(resourceBundle);
        try {
            AnchorPane pane = loader.load();
            CustomerCreationController customerCreationController = loader.getController();
            customerCreationController.setSessionManager(sessionManager);
            customerCreationController.setMainWindowController(this.mainWindowController);
            customerCreationController.loadCustomer(customer);
            Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setOnHiding(event -> {
                if (customerCreationController.getCustomer().isPresent()) {
                    try {
                        loadCustomer(customerCreationController.getCustomer().get());
                    } catch (FailedDbFetch e) {
                        this.alertDialog.showGenericError(e);
                    } catch (InvalidMembership e) {
                        this.alertDialog.showGenericError(e);
                    } catch (LoginException e) {
                        this.alertDialog.showGenericError(e);
                    } catch (RemoteException e) {
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
            log.log_Error("Could not load the 'Edit customer' dialog.");
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
     * Shows the RoomReservation details dialog
     *
     * @param reservation      Reservation DTO
     * @param room_reservation RoomReservation DTO
     */
    private void showRoomReservationDetailsDialog(Reservation reservation, RoomReservation room_reservation) {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(MtRoomsGUI.class.getResource("/view/frontdesk/RoomReservationDetailsView.fxml"));
        loader.setResources(this.resourceBundle);
        try {
            AnchorPane pane = loader.load();
            RoomReservationDetailsController roomReservationDetailsController = loader.getController();
            roomReservationDetailsController.setSessionManager(sessionManager);
            roomReservationDetailsController.setMainWindowController(this.mainWindowController);
            roomReservationDetailsController.load(reservation, room_reservation);
            Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setOnHiding(event -> {
                if (roomReservationDetailsController.getRoomReservation().isPresent()) {
                    try {
                        loadReservationTable(this.customer);
                    } catch (LoginException e) {
                        this.alertDialog.showGenericError(e);
                    } catch (FailedDbFetch e) {
                        this.alertDialog.showGenericError(e);
                    } catch (Unauthorised e) {
                        this.alertDialog.showGenericError(e);
                    } catch (RemoteException e) {
                        this.alertDialog.showGenericError(e);
                    }
                }
            });
            Scene scene = new Scene(pane);
            dialog.setScene(scene);
            dialog.show();
        } catch (IOException e) {
            log.log_Error("Could not load the 'Show RoomReservation details' dialog.");
            log.log_Exception(e);
        }
    }

    /**
     * Shows the Payment details dialog
     *
     * @param payment Payment DTO
     */
    private void showPaymentDetailsDialog(Payment payment) {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(MtRoomsGUI.class.getResource("/view/frontdesk/PaymentDetailsView.fxml"));
        loader.setResources(this.resourceBundle);
        try {
            AnchorPane pane = loader.load();
            PaymentDetailsController paymentDetailsController = loader.getController();
            paymentDetailsController.setMainWindowController(this.mainWindowController);
            paymentDetailsController.load(payment);
            Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            Scene scene = new Scene(pane);
            dialog.setScene(scene);
            dialog.show();
        } catch (IOException e) {
            log.log_Error("Could not load the 'Show Payment details' dialog.");
            log.log_Exception(e);
        }
    }

    /**
     * Cancel selected Reservation from the table
     */
    private void cancelSelectedReservation() {
        IRmiReservationServices services = this.sessionManager.getReservationServices();
        ReservationModel reservationModel = reservation_Table.getSelectionModel().getSelectedItem();
        try {
            Double credit = services.cancelReservation(this.sessionManager.getToken(), reservationModel.getReservation());

            Integer reservationID = reservationModel.reservationId();
            loadReservationTable(customer);

            for (ReservationModel model : reservation_Table.getItems())
                if (model.reservationId().equals(reservationID))
                    reservation_Table.getSelectionModel().select(model);

            AlertDialog.showAlert(
                    Alert.AlertType.INFORMATION,
                    this.resourceBundle.getString("InfoDialogTitle_Generic"),
                    this.resourceBundle.getString("InfoMsg_AccountCredited") + credit
            );
        } catch (InvalidReservation e) {
            log.log_Error("Reservation [", reservationModel.reservationId(), "] is invalid.");
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
        } catch (FailedDbFetch e) {
            this.alertDialog.showGenericError(e);
        }
    }

    /**
     * Cancel selected RoomReservation from the table
     */
    private void cancelSelectedRoomReservation() {
        IRmiReservationServices services = this.sessionManager.getReservationServices();
        ReservationModel reservationModel = reservation_Table.getSelectionModel().getSelectedItem();
        RoomReservationModel roomReservationModel = reservationDetails_Table.getSelectionModel().getSelectedItem();
        try {
            services.cancelReservedRoom(
                    this.sessionManager.getToken(),
                    reservationModel.getReservation(),
                    roomReservationModel.getRoomReservation()
            );

            Integer reservationID = reservationModel.reservationId();
            loadReservationTable(customer);

            for (ReservationModel model : reservation_Table.getItems())
                if (model.reservationId().equals(reservationID))
                    reservation_Table.getSelectionModel().select(model);

        } catch (InvalidReservation e) {
            log.log_Error("RoomReservation in Reservation [", reservationModel.reservationId(), "] is invalid: ", roomReservationModel.getRoomReservation());
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
        } catch (FailedDbFetch e) {
            this.alertDialog.showGenericError(e);
        }
    }

    /**
     * Load a Customer DTO info into the fields
     *
     * @param customer Customer DTO
     * @throws LoginException    when there is not a current session
     * @throws Unauthorised      when this client session is not authorised to access the resource
     * @throws RemoteException   when network issues occur during the remote call
     * @throws InvalidMembership when the customer has an invalid membership ID (doesn't exist in records)
     * @throws FailedDbFetch     when membership or records or customer could not be fetched
     */
    void loadCustomer(Customer customer) throws LoginException, Unauthorised, RemoteException, InvalidMembership, FailedDbFetch {
        IRmiReservationServices services = this.sessionManager.getReservationServices();
        this.membership = services.getMembership(sessionManager.getToken(), customer.membershipTypeID());

        this.customer = customer;
        this.id_field.setText(String.valueOf(customer.customerID()));
        this.customer_field.setText(customer.title() + " " + customer.name() + " " + customer.surname().toUpperCase());
        this.address1_field.setText(customer.address1());
        this.address2_field.setText(customer.address2());
        this.city_field.setText(customer.city());
        this.county_field.setText(customer.county());
        this.country_field.setText(customer.country());
        this.phone1_field.setText(customer.phone1());
        this.phone2_field.setText(customer.phone2());
        this.email_field.setText(customer.email());
        this.creationDate_field.setText(customer.accountCreationDate().toString());
        this.membershipType_field.setText(this.membership.description());
        this.discountRate_field.setText(this.membership.discount().rate() + "%");

        loadReservationTable(this.customer);
        loadCustomerPaymentsTable(this.customer);
        loadCustomerCreditField(this.customer);
    }

    /**
     * Loads the ReservationTable with data
     *
     * @param customer Customer DTO
     * @throws LoginException  when there is not a current session
     * @throws FailedDbFetch   when membership or records or customer could not be fetched
     * @throws Unauthorised    when this client session is not authorised to access the resource
     * @throws RemoteException when network issues occur during the remote call
     */
    private void loadReservationTable(Customer customer) throws LoginException, FailedDbFetch, Unauthorised, RemoteException {
        ReservationTable table = new ReservationTable(this.sessionManager);
        IRmiReservationServices services = this.sessionManager.getReservationServices();
        List<Reservation> reservations = services.getReservations(this.sessionManager.getToken(), customer);
        table.loadData(reservations);
        this.reservation_Table.setItems(table.getData());
        this.reservation_Table.getSelectionModel().selectFirst();
    }

    /**
     * Loads the RoomReservationDetailsTable with data
     *
     * @param reservation Reservation DTO
     */
    private void loadReservationDetailsTable(Reservation reservation) {
        RoomReservationTable table = new RoomReservationTable(this.sessionManager);
        table.loadData(reservation.rooms());
        this.reservationDetails_Table.setItems(table.getData());
    }

    private void loadCustomerPaymentsTable(Customer customer) throws LoginException, FailedDbFetch, Unauthorised, RemoteException {
        CustomerPaymentsTable table = new CustomerPaymentsTable(this.sessionManager);
        IRmiReservationServices services = this.sessionManager.getReservationServices();
        List<Reservation> reservations = services.getReservations(this.sessionManager.getToken(), customer);
        for (Reservation reservation : reservations)
            table.loadData(reservation.id(), services.getPayments(this.sessionManager.getToken(), reservation));
        this.payments_TableView.setItems(table.getData());
    }

    private void loadCustomerCreditField(Customer customer) throws LoginException, FailedDbFetch, Unauthorised, RemoteException {
        IRmiRevenueServices service = this.sessionManager.getRevenueServices();
        CustomerBalance balance = service.getCustomerBalance(this.sessionManager.getToken(), customer);
        this.customerCreditField_TextField.setText(String.format("%.2f", balance.getBalance()));
        if (balance.getBalance() < 0)
            this.customerCreditField_TextField.setStyle("-fx-text-fill: red;");
        else
            this.customerCreditField_TextField.setStyle("-fx-text-fill: black;");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.resourceBundle = resources;
        this.alertDialog = new AlertDialog(resources);
        closeAccount_Button.setMinSize(64, 64);
        closeAccount_Button.setMaxSize(64, 64);

        customerAccount_TabPane.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            try {
                if (newValue.equals(customer_Tab)) {
                    loadCustomer(this.customer);
                } else if (newValue.equals(reservation_Tab)) {
                    loadReservationTable(this.customer);
                    viewRoomReservationDetails_Button.setDisable(true);
                    cancelRoomReservation_button.setDisable(true);
                } else if (newValue.equals(payment_Tab)) {
                    loadCustomerPaymentsTable(this.customer);
                    loadCustomerCreditField(this.customer);
                }
            } catch (FailedDbFetch e) {
                this.alertDialog.showGenericError(e);
            } catch (InvalidMembership e) {
                this.alertDialog.showGenericError(e);
            } catch (LoginException e) {
                this.alertDialog.showGenericError(e);
            } catch (RemoteException e) {
                this.alertDialog.showGenericError(e);
            } catch (Unauthorised e) {
                this.alertDialog.showGenericError(e);
            }
        });

        //Reservation context menu
        MenuItem menuItem_AddPayment = new MenuItem(this.resourceBundle.getString("MenuItem_AddPayment"));
        MenuItem menuItem_CancelReservation = new MenuItem(this.resourceBundle.getString("MenuItem_CancelReservation"));
        MenuItem menuItem_ShowRoomDetails = new MenuItem(this.resourceBundle.getString("MenuItem_ShowRoomDetails"));
        MenuItem menuItem_CancelRoomReservation = new MenuItem(this.resourceBundle.getString("MenuItem_CancelRoomReservation"));
        this.reservation_menu.getItems().add(menuItem_AddPayment);
        this.reservation_menu.getItems().add(menuItem_CancelReservation);
        this.roomReservation_menu.getItems().add(menuItem_ShowRoomDetails);
        this.roomReservation_menu.getItems().add(menuItem_CancelRoomReservation);
        reservation_Table.setContextMenu(this.reservation_menu);
        reservationDetails_Table.setContextMenu(this.roomReservation_menu);

        menuItem_CancelReservation.setOnAction(this::handleCancelReservationAction);
        menuItem_ShowRoomDetails.setOnAction(this::handleViewRoomDetailsAction);
        menuItem_CancelRoomReservation.setOnAction(this::handleCancelRoomReservationAction);

        //Reservation table
        reservationId_col.setCellValueFactory(cellData -> cellData.getValue().reservationIdProperty().asObject());
        created_col.setCellValueFactory(cellData -> cellData.getValue().createdProperty());
        discount_col.setCellValueFactory(cellData -> cellData.getValue().discountRateProperty());

        reservation_Table.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                this.mainWindowController.status_right.setText("");
                loadReservationDetailsTable(reservation_Table.getSelectionModel().getSelectedItem().getReservation());

                boolean tooLateForFullCancellation_flag = false;
                for (RoomReservationModel model : reservationDetails_Table.getItems()) {
                    if (model.inTimestamp().compareTo(Date.from(Instant.now())) <= 0)
                        tooLateForFullCancellation_flag = true;
                }
                invoice_Button.setDisable(false);
                cancelReservation_button.setDisable(tooLateForFullCancellation_flag);
            }
        });

        //Reservation details table
        buildingId_col.setCellValueFactory(cellData -> cellData.getValue().buildingIdProperty().asObject());
        floorId_col.setCellValueFactory(cellData -> cellData.getValue().floorIdProperty().asObject());
        roomId_col.setCellValueFactory(cellDate -> cellDate.getValue().roomIdProperty().asObject());
        in_col.setCellValueFactory(cellData -> cellData.getValue().inProperty());
        out_col.setCellValueFactory(cellData -> cellData.getValue().outProperty());
        duration_col.setCellValueFactory(cellData -> cellData.getValue().durationProperty());
        seated_col.setCellValueFactory(cellData -> cellData.getValue().seatedProperty().asObject());
        catering_col.setCellValueFactory(cellData -> cellData.getValue().cateringProperty());
        cancelled_col.setCellValueFactory(cellData -> cellData.getValue().cancelledProperty());
        hasNote_col.setCellValueFactory(cellData -> cellData.getValue().hasNoteProperty());

        reservationDetails_Table.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                menuItem_CancelRoomReservation.setDisable(newValue.getRoomReservation().isCancelled());
                viewRoomReservationDetails_Button.setDisable(false);
                cancelRoomReservation_button.setDisable(newValue.getRoomReservation().isCancelled());
                cancelRoomReservation_button.setDisable(newValue.inTimestamp().compareTo(Date.from(Instant.now())) <= 0);

                Double roomPrice = newValue.getRoomReservation().price().price();
                Double discountRate = membership.discount().rate();
                this.mainWindowController.status_right.setText(
                        this.resourceBundle.getString("InfoMsg_RoomPricePostDiscount") + (roomPrice * (100 - discountRate) / 100)
                );
            } else {
                viewRoomReservationDetails_Button.setDisable(true);
            }
        });

        //Payment details table
        paymentID_col.setCellValueFactory(cellData -> cellData.getValue().paymentIdProperty().asObject());
        paymentTimestamp_col.setCellValueFactory(cellData -> cellData.getValue().timestampProperty());
        paymentAmount_col.setCellFactory(cell -> new TableCell<CustomerPaymentsModel, Double>() {
            @Override
            protected void updateItem(Double amount, boolean empty) {
                super.updateItem(amount, empty);
                if (empty)
                    setText(null);
                else
                    setText(String.format("%.2f", amount));
            }
        });
        paymentAmount_col.setCellValueFactory(cellData -> cellData.getValue().amountProperty().asObject());
        paymentReservationID_col.setCellValueFactory(cellData -> cellData.getValue().reservationIdProperty().asObject());
        paymentMethod_col.setCellValueFactory(cellData -> cellData.getValue().methodProperty());
        paymentNote_col.setCellValueFactory(cellData -> cellData.getValue().noteProperty());

        payments_TableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                viewPaymentDetails_Button.setDisable(false);
            } else {
                viewPaymentDetails_Button.setDisable(true);
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
    public void setMainWindowController(MainWindowController mainWindowController) {
        this.mainWindowController = mainWindowController;
    }

    @FXML
    public void handleEditAction(ActionEvent actionEvent) {
        showEditCustomerDialog(this.customer);
    }

    @FXML
    public void handleCloseAccountAction(ActionEvent actionEvent) {
        this.mainWindowController.showCustomerSearchPane();
    }

    @FXML
    public void handleCancelReservationAction(ActionEvent actionEvent) {
        Optional<ButtonType> result = AlertDialog.showConfirmation(
                resourceBundle.getString("ConfirmationDialogTitle_Generic"),
                resourceBundle.getString("ConfirmationDialog_CancelReservation"),
                ""
        );
        result.ifPresent(response -> {
            if (response == ButtonType.OK) {
                cancelSelectedReservation();
            }
        });
    }

    @FXML
    public void handleNewReservationAction(ActionEvent actionEvent) { //TODO
        System.out.println("handleNewReservationAction");
    }

    @FXML
    public void handleCancelRoomReservationAction(ActionEvent actionEvent) {
        Optional<ButtonType> result = AlertDialog.showConfirmation(
                resourceBundle.getString("ConfirmationDialogTitle_Generic"),
                resourceBundle.getString("ConfirmationDialog_CancelRoomReservation"),
                ""
        );
        result.ifPresent(response -> {
            if (response == ButtonType.OK) {
                cancelSelectedRoomReservation();
            }
        });
    }

    @FXML
    public void handleInvoiceAction(ActionEvent actionEvent) { //TODO
        System.out.println("handleInvoiceAction");
    }

    @FXML
    public void handleViewRoomDetailsAction(ActionEvent actionEvent) {
        showRoomReservationDetailsDialog(
                this.reservation_Table.getSelectionModel().getSelectedItem().getReservation(),
                this.reservationDetails_Table.getSelectionModel().getSelectedItem().getRoomReservation()
        );
    }

    @FXML
    public void handleViewPaymentDetailsAction(ActionEvent actionEvent) {
        showPaymentDetailsDialog(
                this.payments_TableView.getSelectionModel().getSelectedItem().getPayment()
        );
    }

    @FXML
    public void handleAddReservationPaymentAction(ActionEvent actionEvent) { //TODO
        System.out.println("handleAddReservationPaymentAction");
    }

    @FXML
    public void handleCreateRefundButton(ActionEvent actionEvent) { //TODO
        System.out.println("handleCreateRefundButton");
    }

    @FXML
    public void handleViewDetailedBalanceAction(ActionEvent actionEvent) { //TODO
        System.out.println("handleViewDetailedBalanceAction");
    }
}
