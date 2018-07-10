package bbk_beam.mtRooms.ui.controller.frontdesk;

import bbk_beam.mtRooms.MtRoomsGUI;
import bbk_beam.mtRooms.exception.LoginException;
import bbk_beam.mtRooms.network.IRmiReservationServices;
import bbk_beam.mtRooms.network.exception.Unauthorised;
import bbk_beam.mtRooms.reservation.dto.Customer;
import bbk_beam.mtRooms.reservation.dto.Membership;
import bbk_beam.mtRooms.reservation.dto.Reservation;
import bbk_beam.mtRooms.reservation.exception.FailedDbFetch;
import bbk_beam.mtRooms.reservation.exception.FailedDbWrite;
import bbk_beam.mtRooms.reservation.exception.InvalidMembership;
import bbk_beam.mtRooms.reservation.exception.InvalidReservation;
import bbk_beam.mtRooms.ui.AlertDialog;
import bbk_beam.mtRooms.ui.controller.MainWindowController;
import bbk_beam.mtRooms.ui.model.SessionManager;
import bbk_beam.mtRooms.ui.model.frontdesk.ReservationModel;
import bbk_beam.mtRooms.ui.model.frontdesk.ReservationTable;
import bbk_beam.mtRooms.ui.model.frontdesk.RoomReservationModel;
import bbk_beam.mtRooms.ui.model.frontdesk.RoomReservationTable;
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
    public Button viewReservation_button;
    public Button cancelReservation_button;
    public Button newReservation_button;
    public Button cancelRoomReservation_button;
    public TableView<ReservationModel> reservation_Table;
    public TableColumn<ReservationModel, Integer> reservationId_col;
    public TableColumn<ReservationModel, String> created_col;
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
        this.discountRate_field.setText(String.valueOf(this.membership.discount().rate()));

        loadReservationTable(this.customer);
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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.resourceBundle = resources;
        this.alertDialog = new AlertDialog(resources);
        closeAccount_Button.setMinSize(64, 64);
        closeAccount_Button.setMaxSize(64, 64);

        customerAccount_TabPane.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.equals(customer_Tab)) {
                System.out.println("switched to customer tab");
                //TODO
            } else if (newValue.equals(reservation_Tab)) {
                System.out.println("switched to reservation tab");
                try {
                    loadCustomer(this.customer);
                } catch (LoginException e) {
                    this.alertDialog.showGenericError(e);
                } catch (Unauthorised e) {
                    this.alertDialog.showGenericError(e);
                } catch (RemoteException e) {
                    this.alertDialog.showGenericError(e);
                } catch (InvalidMembership e) {
                    this.alertDialog.showGenericError(e);
                } catch (FailedDbFetch e) {
                    this.alertDialog.showGenericError(e);
                }
            } else if (newValue.equals(payment_Tab)) {
                System.out.println("switched to payment tab");
                //TODO
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
        reservationId_col.prefWidthProperty().bind(reservation_Table.widthProperty().multiply(0.25));
        created_col.prefWidthProperty().bind(reservation_Table.widthProperty().multiply(0.74));

        reservationId_col.setCellValueFactory(cellData -> cellData.getValue().reservationIdProperty().asObject());
        created_col.setCellValueFactory(cellData -> cellData.getValue().createdProperty());

        reservation_Table.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                loadReservationDetailsTable(reservation_Table.getSelectionModel().getSelectedItem().getReservation());
                viewReservation_button.setText("View reservation");
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

        reservationDetails_Table.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                menuItem_CancelRoomReservation.setDisable(newValue.getRoomReservation().isCancelled());
                cancelRoomReservation_button.setDisable(newValue.getRoomReservation().isCancelled());

                Double roomPrice = newValue.getRoomReservation().price().price();
                Double discountRate = membership.discount().rate();
                this.mainWindowController.status_right.setText(
                        this.resourceBundle.getString("InfoMsg_RoomPricePostDiscount") + (roomPrice * (100 - discountRate) / 100)
                );
            }
        });

        reservation_Table.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                this.mainWindowController.status_right.setText("");
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
    public void handleViewReservationAction(ActionEvent actionEvent) {
        System.out.println("handleViewReservationAction");
        //TODO
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
    public void handleNewReservationAction(ActionEvent actionEvent) {
        System.out.println("handleNewReservationAction");
        //TODO
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
    public void handleAddPaymentAction(ActionEvent actionEvent) {
        System.out.println("handleAddPaymentAction");
        //TODO
    }

    @FXML
    public void handleViewRoomDetailsAction(ActionEvent actionEvent) {
        System.out.println("handleViewRoomDetailsAction");
        //TODO
    }
}
