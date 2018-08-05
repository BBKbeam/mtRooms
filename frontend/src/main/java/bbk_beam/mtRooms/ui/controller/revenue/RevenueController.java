package bbk_beam.mtRooms.ui.controller.revenue;

import bbk_beam.mtRooms.exception.LoginException;
import bbk_beam.mtRooms.network.IRmiRevenueServices;
import bbk_beam.mtRooms.network.exception.Unauthorised;
import bbk_beam.mtRooms.reservation.exception.FailedDbFetch;
import bbk_beam.mtRooms.revenue.dto.SimpleCustomerBalance;
import bbk_beam.mtRooms.ui.controller.MainWindowController;
import bbk_beam.mtRooms.ui.controller.common.AlertDialog;
import bbk_beam.mtRooms.ui.model.SessionManager;
import bbk_beam.mtRooms.ui.model.revenue.CustomerBalanceTable;
import bbk_beam.mtRooms.ui.model.revenue.SimpleCustomerBalanceModel;
import eadjlib.logger.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.paint.Color;

import java.net.URL;
import java.rmi.RemoteException;
import java.util.List;
import java.util.ResourceBundle;

public class RevenueController implements Initializable {
    private final Logger log = Logger.getLoggerInstance(RevenueController.class.getName());
    private AlertDialog alertDialog;
    private SessionManager sessionManager;
    private MainWindowController mainWindowController;
    private ResourceBundle resourceBundle;

    //Parent UI elements
    public Tab revenues_Tab;
    public Tab customerBalances_Tab;
    public Tab occupancy_Tab;
    //Customer Balances Tab
    private CustomerBalanceTable customerBalanceTable;
    public TableView<SimpleCustomerBalanceModel> customerBalance_TableView;
    public TableColumn<SimpleCustomerBalanceModel, Integer> customerId_col;
    public TableColumn<SimpleCustomerBalanceModel, Integer> reservationCount_col;
    public TableColumn<SimpleCustomerBalanceModel, Double> cost_col;
    public TableColumn<SimpleCustomerBalanceModel, Double> paid_col;
    public TableColumn<SimpleCustomerBalanceModel, Double> balance_col;
    //Occupancy Tab
    public DatePicker fromDate_DatePicker;
    public DatePicker toDate_DatePicker;
    public Button showOccupancy_Button;
    public ChoiceBox building_ChoiceBox;
    public ChoiceBox floor_ChoiceBox;
    public ChoiceBox room_ChoiceBox;
    public ChoiceBox granularity_ChoiceBox;

    /**
     * Populates the customerBalance_TableView UI TableView
     */
    private void populateCustomerBalancesTable() {
        try {
            IRmiRevenueServices services = this.sessionManager.getRevenueServices();
            List<SimpleCustomerBalance> list = services.getCustomerBalance(this.sessionManager.getToken());

            this.customerBalanceTable = new CustomerBalanceTable(this.sessionManager);
            this.customerBalanceTable.loadData(list);
            this.customerBalance_TableView.setItems(this.customerBalanceTable.getData());
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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.resourceBundle = resources;
        this.alertDialog = new AlertDialog(resources);

        this.customerBalances_Tab.setOnSelectionChanged(value -> populateCustomerBalancesTable());

        this.customerId_col.setCellValueFactory(cellData -> cellData.getValue().customerIdProperty().asObject());
        this.reservationCount_col.setCellValueFactory(cellData -> cellData.getValue().reservationCountProperty().asObject());
        this.cost_col.setCellFactory(cell -> new TableCell<SimpleCustomerBalanceModel, Double>() {
            @Override
            protected void updateItem(Double amount, boolean empty) {
                super.updateItem(amount, empty);
                if (empty)
                    setText(null);
                else
                    setText(String.format("%.2f", amount));
            }
        });
        this.cost_col.setCellValueFactory(cellData -> cellData.getValue().costProperty().asObject());
        this.paid_col.setCellFactory(cell -> new TableCell<SimpleCustomerBalanceModel, Double>() {
            @Override
            protected void updateItem(Double amount, boolean empty) {
                super.updateItem(amount, empty);
                if (empty)
                    setText(null);
                else
                    setText(String.format("%.2f", amount));
            }
        });
        this.paid_col.setCellValueFactory(cellData -> cellData.getValue().paidProperty().asObject());
        this.balance_col.setCellFactory(cell -> new TableCell<SimpleCustomerBalanceModel, Double>() {
            @Override
            protected void updateItem(Double amount, boolean empty) {
                super.updateItem(amount, empty);
                if (empty) {
                    setText(null);
                } else {
                    setText(String.format("%.2f", amount));
                    if (amount < 0)
                        this.setTextFill(Color.RED);
                    else
                        this.setTextFill(Color.BLACK);
                }
            }
        });
        this.balance_col.setCellValueFactory(cellData -> cellData.getValue().balanceProperty().asObject());
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
    public void handleShowOccupancyAction(ActionEvent actionEvent) { //TODO
        System.out.println("handleShowOccupancyAction");
    }
}
