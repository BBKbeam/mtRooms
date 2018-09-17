package bbk_beam.mtRooms.ui.controller.revenue;

import bbk_beam.mtRooms.exception.InvalidDate;
import bbk_beam.mtRooms.exception.LoginException;
import bbk_beam.mtRooms.network.IRmiRevenueServices;
import bbk_beam.mtRooms.network.exception.Unauthorised;
import bbk_beam.mtRooms.reservation.exception.FailedDbFetch;
import bbk_beam.mtRooms.revenue.dto.DetailedPayment;
import bbk_beam.mtRooms.revenue.dto.SimpleCustomerBalance;
import bbk_beam.mtRooms.revenue.exception.InvalidPeriodException;
import bbk_beam.mtRooms.ui.controller.MainWindowController;
import bbk_beam.mtRooms.ui.controller.common.AlertDialog;
import bbk_beam.mtRooms.ui.model.SessionManager;
import bbk_beam.mtRooms.ui.model.revenue.*;
import eadjlib.logger.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

import java.net.URL;
import java.rmi.RemoteException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
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
    //Revenues Tab
    private RevenuesChart revenuesChart_cache;
    private DetailedPaymentTable detailedPaymentTable;
    public Button showPayments_Button;
    public DatePicker paymentsFrom_DatePicker;
    public DatePicker paymentsTo_DatePicker;
    public TextField revenueTotal_TextField;
    public TableView<DetailedPaymentModel> revenues_TableView;
    public TableColumn<DetailedPaymentModel, Integer> paymentId_col;
    public TableColumn<DetailedPaymentModel, Integer> customerId2_col;
    public TableColumn<DetailedPaymentModel, Integer> reservationId2_col;
    public TableColumn<DetailedPaymentModel, Double> amount_col;
    public TableColumn<DetailedPaymentModel, String> timestamp_col;
    public TableColumn<DetailedPaymentModel, String> paymentMethod_col;
    public MenuButton revenuesChartTypes_MenuButton;
    public Pane revenuesChart_Pane;
    //Customer Balances Tab
    private CustomerBalanceTable customerBalanceTable;
    public TableView<SimpleCustomerBalanceModel> customerBalance_TableView;
    public TableColumn<SimpleCustomerBalanceModel, Integer> customerId_col;
    public TableColumn<SimpleCustomerBalanceModel, Integer> reservationCount_col;
    public TableColumn<SimpleCustomerBalanceModel, Double> cost_col;
    public TableColumn<SimpleCustomerBalanceModel, Double> paid_col;
    public TableColumn<SimpleCustomerBalanceModel, Double> balance_col;

    /**
     * Validate a DatePicker date
     *
     * @param date    DatePicker date
     * @param err_msg Error message to display in AlertDialog
     * @throws InvalidDate when date is null (i.e. nothing is picked)
     */
    private void validateDate(LocalDate date, String err_msg) throws InvalidDate {
        if (date != null)
            return;

        AlertDialog.showAlert(
                Alert.AlertType.ERROR,
                this.resourceBundle.getString("ErrorDialogTitle_Generic"),
                err_msg
        );
        throw new InvalidDate("Start date not selected.");
    }

    /**
     * Populates the revenues_TableView UI TableView
     */
    private void populateRevenuesTable() {
        IRmiRevenueServices services = this.sessionManager.getRevenueServices();
        LocalDate fromLocalDate = this.paymentsFrom_DatePicker.getValue();
        LocalDate toLocalDate = this.paymentsTo_DatePicker.getValue();
        this.revenueTotal_TextField.setText("");
        try {
            validateDate(fromLocalDate, this.resourceBundle.getString("ErrorMsg_NoFromDateSelected"));
            validateDate(toLocalDate, this.resourceBundle.getString("ErrorMsg_NoToDateSelected"));

            Date fromDate = Date.from(Instant.from(fromLocalDate.atStartOfDay(ZoneId.systemDefault())));
            Date toDate = Date.from(Instant.from(toLocalDate.atTime(LocalTime.MAX).atZone(ZoneId.systemDefault())));
            List<DetailedPayment> payments = services.getPayments(this.sessionManager.getToken(), fromDate, toDate);

            this.detailedPaymentTable = new DetailedPaymentTable(this.sessionManager);
            this.detailedPaymentTable.loadData(payments);
            this.revenues_TableView.setItems(this.detailedPaymentTable.getData());

            this.revenueTotal_TextField.setText(String.format("%.2f", this.detailedPaymentTable.getPaymentTotal()));
            if (this.detailedPaymentTable.getPaymentTotal() < 0)
                this.revenueTotal_TextField.setStyle("-fx-text-fill: red;");
            else
                this.revenueTotal_TextField.setStyle("-fx-text-fill: black;");

            this.revenuesChart_cache = new RevenuesChart(this.resourceBundle, payments);
            this.revenuesChartTypes_MenuButton.setDisable(false);
            this.revenuesChart_Pane.getChildren().clear();
        } catch (InvalidPeriodException e) {
            AlertDialog.showAlert(
                    Alert.AlertType.ERROR,
                    this.resourceBundle.getString("ErrorDialogTitle_Generic"),
                    this.resourceBundle.getString("ErrorMsg_InvalidPeriodException")
            );
        } catch (FailedDbFetch e) {
            this.alertDialog.showGenericError(e);
        } catch (LoginException e) {
            this.alertDialog.showGenericError(e);
        } catch (RemoteException e) {
            this.alertDialog.showGenericError(e);
        } catch (Unauthorised e) {
            this.alertDialog.showGenericError(e);
        } catch (InvalidDate e) {
            log.log_Debug("User forgot to select from/to date(s)");
        }
    }

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

    private void loadOccupancyChart() { //TODO
        log.log_Error("@loadOccupancyChart(): unimplemented");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.resourceBundle = resources;
        this.alertDialog = new AlertDialog(resources);

        //Detailed payments TableView
        this.paymentId_col.setCellValueFactory(cellData -> cellData.getValue().paymentIdProperty().asObject());
        this.customerId2_col.setCellValueFactory(cellData -> cellData.getValue().customerIdProperty().asObject());
        this.reservationId2_col.setCellValueFactory(cellData -> cellData.getValue().reservationIdProperty().asObject());
        this.amount_col.setCellFactory(cell -> new TableCell<DetailedPaymentModel, Double>() {
            @Override
            protected void updateItem(Double amount, boolean empty) {
                super.updateItem(amount, empty);
                if (empty)
                    setText(null);
                else
                    setText(String.format("%.2f", amount));
            }
        });
        this.amount_col.setCellValueFactory(cellData -> cellData.getValue().amountProperty().asObject());
        this.timestamp_col.setCellValueFactory(cellData -> cellData.getValue().timestampProperty());
        this.paymentMethod_col.setCellValueFactory(cellData -> cellData.getValue().methodProperty());

        //Customer balance TableView
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
        //
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
    public void handleShowPaymentsAction(ActionEvent actionEvent) {
        populateRevenuesTable();
    }

    @FXML
    public void handleShowPaymentMethodChartAction(ActionEvent actionEvent) {
        BarChart chart = this.revenuesChart_cache.getMethodBarChart();
        this.revenuesChart_Pane.getChildren().clear();
        this.revenuesChart_Pane.getChildren().add(chart);
        chart.setPadding(new Insets(5, 5, 5, 5));
        chart.prefWidthProperty().bind(this.revenuesChart_Pane.widthProperty());
        chart.prefHeightProperty().bind(this.revenuesChart_Pane.heightProperty());
    }

    @FXML
    public void handleShowHourlyPaymentChartAction(ActionEvent actionEvent) {
        BarChart chart = this.revenuesChart_cache.getHourlyBarChart();
        this.revenuesChart_Pane.getChildren().clear();
        this.revenuesChart_Pane.getChildren().add(chart);
        chart.setPadding(new Insets(5, 5, 5, 5));
        chart.prefWidthProperty().bind(this.revenuesChart_Pane.widthProperty());
        chart.prefHeightProperty().bind(this.revenuesChart_Pane.heightProperty());
    }

    @FXML
    public void handleShowWeekdayPaymentChartAction(ActionEvent actionEvent) {
        BarChart chart = this.revenuesChart_cache.getWeekdayBarChart();
        this.revenuesChart_Pane.getChildren().clear();
        this.revenuesChart_Pane.getChildren().add(chart);
        chart.setPadding(new Insets(5, 5, 5, 5));
        chart.prefWidthProperty().bind(this.revenuesChart_Pane.widthProperty());
        chart.prefHeightProperty().bind(this.revenuesChart_Pane.heightProperty());
    }

    @FXML
    public void handleShowWeeklyRevenueBarChartAction(ActionEvent actionEvent) {
        BarChart chart = this.revenuesChart_cache.getWeekInYearBarChart();
        this.revenuesChart_Pane.getChildren().clear();
        this.revenuesChart_Pane.getChildren().add(chart);
        chart.setPadding(new Insets(5, 5, 5, 5));
        chart.prefWidthProperty().bind(this.revenuesChart_Pane.widthProperty());
        chart.prefHeightProperty().bind(this.revenuesChart_Pane.heightProperty());
    }

    @FXML
    public void handleShowDailyRevenueChartAction(ActionEvent actionEvent) {
        LineChart chart = this.revenuesChart_cache.getDailyLineChart();
        this.revenuesChart_Pane.getChildren().clear();
        this.revenuesChart_Pane.getChildren().add(chart);
        chart.setPadding(new Insets(5, 5, 5, 5));
        chart.prefWidthProperty().bind(this.revenuesChart_Pane.widthProperty());
        chart.prefHeightProperty().bind(this.revenuesChart_Pane.heightProperty());
    }

    @FXML
    public void handleShowMonthlyRevenueChartAction(ActionEvent actionEvent) {
        BarChart chart = this.revenuesChart_cache.getMonthlyBarChart();
        this.revenuesChart_Pane.getChildren().clear();
        this.revenuesChart_Pane.getChildren().add(chart);
        chart.setPadding(new Insets(5, 5, 5, 5));
        chart.prefWidthProperty().bind(this.revenuesChart_Pane.widthProperty());
        chart.prefHeightProperty().bind(this.revenuesChart_Pane.heightProperty());
    }

    @FXML
    public void handleShowYearlyRevenueChartAction(ActionEvent actionEvent) {
        BarChart chart = this.revenuesChart_cache.getYearlyBarChart();
        this.revenuesChart_Pane.getChildren().clear();
        this.revenuesChart_Pane.getChildren().add(chart);
        chart.setPadding(new Insets(5, 5, 5, 5));
        chart.prefWidthProperty().bind(this.revenuesChart_Pane.widthProperty());
        chart.prefHeightProperty().bind(this.revenuesChart_Pane.heightProperty());
    }
}
