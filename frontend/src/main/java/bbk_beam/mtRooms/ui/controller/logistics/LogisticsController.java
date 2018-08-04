package bbk_beam.mtRooms.ui.controller.logistics;

import bbk_beam.mtRooms.exception.InvalidDate;
import bbk_beam.mtRooms.exception.LoginException;
import bbk_beam.mtRooms.network.IRmiLogisticsServices;
import bbk_beam.mtRooms.network.exception.Unauthorised;
import bbk_beam.mtRooms.operation.dto.LogisticsEntry;
import bbk_beam.mtRooms.operation.dto.LogisticsInfo;
import bbk_beam.mtRooms.reservation.dto.Building;
import bbk_beam.mtRooms.reservation.dto.Floor;
import bbk_beam.mtRooms.reservation.dto.Room;
import bbk_beam.mtRooms.reservation.exception.FailedDbFetch;
import bbk_beam.mtRooms.revenue.exception.InvalidPeriodException;
import bbk_beam.mtRooms.ui.controller.MainWindowController;
import bbk_beam.mtRooms.ui.controller.common.AlertDialog;
import bbk_beam.mtRooms.ui.model.SessionManager;
import bbk_beam.mtRooms.ui.model.common.GenericChoiceBoxItem;
import bbk_beam.mtRooms.ui.model.logistics.*;
import eadjlib.logger.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.text.Text;

import java.net.URL;
import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

public class LogisticsController implements Initializable {
    private enum InfoType {
        INVALID,
        BUILDING,
        FLOOR,
        ROOM
    }

    private final Logger log = Logger.getLoggerInstance(LogisticsController.class.getName());
    static private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/YYYY");
    private AlertDialog alertDialog;
    private SessionManager sessionManager;
    private MainWindowController mainWindowController;
    private ResourceBundle resourceBundle;

    private BuildingListModel buildingListModel;
    private FloorListModel floorListModel;
    private RoomListModel roomListModel;
    private LogisticsTable logisticsTable;
    private LogisticsInfo logisticsInfo_cache; //cached LogisticsInfo DTO

    public DatePicker fromDate_DatePicker;
    public DatePicker toDate_DatePicker;
    public ChoiceBox<GenericChoiceBoxItem<Building>> building_ChoiceBox;
    public ChoiceBox<GenericChoiceBoxItem<Floor>> floor_ChoiceBox;
    public ChoiceBox<GenericChoiceBoxItem<Room>> room_ChoiceBox;
    public Button go_Button;
    public Text searchParam_Text;
    public TableView<LogisticsEntryModel> info_TableView;
    public TableColumn<LogisticsEntryModel, Integer> building_col;
    public TableColumn<LogisticsEntryModel, Integer> floor_col;
    public TableColumn<LogisticsEntryModel, Integer> room_col;
    public TableColumn<LogisticsEntryModel, String> seating_col;
    public TableColumn<LogisticsEntryModel, String> timestampIn_col;
    public TableColumn<LogisticsEntryModel, String> timestampOut_col;
    public TableColumn<LogisticsEntryModel, String> catering_col;
    public TableColumn<LogisticsEntryModel, String> notes_col;

    private void loadBuildingChoiceBox() {
        try {
            IRmiLogisticsServices services = this.sessionManager.getLogisticsServices();

            List<Building> buildings = services.getBuildings(this.sessionManager.getToken());
            this.buildingListModel.clear();
            this.buildingListModel.appendData(buildings);

            if (!this.buildingListModel.isEmpty()) {
                this.building_ChoiceBox.setItems(this.buildingListModel.getData());
                this.building_ChoiceBox.getSelectionModel().selectFirst();
                this.building_ChoiceBox.setDisable(false);
                this.go_Button.setDisable(false);
            } else {
                this.go_Button.setDisable(true);
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

    private void loadFloorChoiceBox(Building building) {
        try {
            IRmiLogisticsServices services = this.sessionManager.getLogisticsServices();
            List<Floor> floors = services.getFloors(this.sessionManager.getToken(), building);
            this.floorListModel.clear();
            this.floorListModel.appendData(new GenericChoiceBoxItem<>(this.resourceBundle.getString("ChoiceBox_AllFloors")));
            this.floorListModel.appendData(floors);

            this.floor_ChoiceBox.setItems(this.floorListModel.getData());
            this.floor_ChoiceBox.getSelectionModel().selectFirst();
            this.floor_ChoiceBox.setDisable(false);
        } catch (FailedDbFetch e) {
            this.alertDialog.showGenericError(e);
        } catch (Unauthorised e) {
            this.alertDialog.showGenericError(e);
        } catch (RemoteException e) {
            this.alertDialog.showGenericError(e);
        } catch (LoginException e) {
            this.alertDialog.showGenericError(e);
        }
    }

    private void loadRoomChoiceBox(Floor floor) {
        try {
            IRmiLogisticsServices services = this.sessionManager.getLogisticsServices();
            List<Room> rooms = services.getRooms(this.sessionManager.getToken(), floor);
            this.roomListModel.clear();
            this.roomListModel.appendData(new GenericChoiceBoxItem<>(this.resourceBundle.getString("ChoiceBox_AllRooms")));
            this.roomListModel.appendData(rooms);

            this.room_ChoiceBox.setItems(this.roomListModel.getData());
            this.room_ChoiceBox.getSelectionModel().selectFirst();
            this.room_ChoiceBox.setDisable(false);
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

    /**
     * Validate the start date from the DatePicker
     *
     * @param date DatePicker date
     * @throws InvalidDate when date is null (i.e. nothing is picked)
     */
    private void validateFromDate(LocalDate date) throws InvalidDate {
        if (date != null)
            return;

        AlertDialog.showAlert(
                Alert.AlertType.ERROR,
                this.resourceBundle.getString("ErrorDialogTitle_Generic"),
                this.resourceBundle.getString("ErrorMsg_NoFromDateSelected")
        );
        throw new InvalidDate("Start date not selected.");
    }

    /**
     * Validate the end date from the DatePicker
     *
     * @param date DatePicker date
     * @throws InvalidDate when date is null (i.e. nothing is picked)
     */
    private void validateToDate(LocalDate date) throws InvalidDate {
        if (date != null)
            return;

        AlertDialog.showAlert(
                Alert.AlertType.ERROR,
                this.resourceBundle.getString("ErrorDialogTitle_Generic"),
                this.resourceBundle.getString("ErrorMsg_NoToDateSelected")
        );
        throw new InvalidDate("End date not selected.");
    }

    /**
     * Loads the cached data into the 'LogisticsTable' UI TableView
     */
    private void loadLogisticsTable() {
        if (this.logisticsInfo_cache == null) {
            log.log_Error("LogisticsInfo cache is empty. No data can be loaded into table.");
            return;
        }
        this.logisticsTable = new LogisticsTable(this.sessionManager);
        for (LogisticsEntry entry : this.logisticsInfo_cache.getEntries()) {
            this.logisticsTable.appendData(new LogisticsEntryModel(entry));
        }
        this.info_TableView.setItems(this.logisticsTable.getData());
    }

    private void getLogisticsTableData(Date from,
                                       Date to,
                                       GenericChoiceBoxItem<Building> selected_building) throws LoginException, Unauthorised, InvalidPeriodException, RemoteException, FailedDbFetch {
        IRmiLogisticsServices services = this.sessionManager.getLogisticsServices();
        this.logisticsInfo_cache = services.getInfo(
                this.sessionManager.getToken(),
                selected_building.getDTO().id(),
                from,
                to
        );
        this.searchParam_Text.setText(this.resourceBundle.getString("TextField_ForBuilding") + " " +
                selected_building.getDTO().id() + " (" + selected_building.getDTO().name() + ") " +
                this.resourceBundle.getString("TextField_Between") +
                dateFormat.format(from) + " " +
                this.resourceBundle.getString("TextField_And") +
                dateFormat.format(to)
        );
    }

    private void getLogisticsTableData(Date from,
                                       Date to,
                                       GenericChoiceBoxItem<Building> selected_building,
                                       GenericChoiceBoxItem<Floor> selected_floor) throws LoginException, Unauthorised, InvalidPeriodException, RemoteException, FailedDbFetch {
        IRmiLogisticsServices services = this.sessionManager.getLogisticsServices();
        this.logisticsInfo_cache = services.getInfo(
                this.sessionManager.getToken(),
                selected_building.getDTO().id(),
                selected_floor.getDTO().floorID(),
                from,
                to
        );
        this.searchParam_Text.setText(this.resourceBundle.getString("TextField_ForBuilding") + " " +
                selected_building.getDTO().id() + " (" + selected_building.getDTO().name() + ") " +
                this.resourceBundle.getString("TextField_ForFloor") +
                selected_floor.getDTO().floorID() + " " +
                this.resourceBundle.getString("TextField_Between") +
                dateFormat.format(from) + " " +
                this.resourceBundle.getString("TextField_And") +
                dateFormat.format(to)
        );
    }

    private void getLogisticsTableData(Date from,
                                       Date to,
                                       GenericChoiceBoxItem<Building> selected_building,
                                       GenericChoiceBoxItem<Floor> selected_floor,
                                       GenericChoiceBoxItem<Room> selected_room) throws LoginException, Unauthorised, InvalidPeriodException, RemoteException, FailedDbFetch {
        IRmiLogisticsServices services = this.sessionManager.getLogisticsServices();
        this.logisticsInfo_cache = services.getInfo(
                this.sessionManager.getToken(),
                selected_room.getDTO(),
                from,
                to
        );
        this.searchParam_Text.setText(this.resourceBundle.getString("TextField_ForBuilding") + " " +
                selected_building.getDTO().id() + " (" + selected_building.getDTO().name() + ") " +
                this.resourceBundle.getString("TextField_ForFloor") +
                selected_floor.getDTO().floorID() + " " +
                this.resourceBundle.getString("TextField_ForRoom") +
                selected_room.getDTO().id() + " " +
                this.resourceBundle.getString("TextField_Between") +
                dateFormat.format(from) + " " +
                this.resourceBundle.getString("TextField_And") +
                dateFormat.format(to)
        );
    }

    /**
     * Checks the options and gets the LogisticsInfo data from the records
     */
    private void getLogisticsTableData() {
        IRmiLogisticsServices services = this.sessionManager.getLogisticsServices();
        LocalDate fromLocalDate = this.fromDate_DatePicker.getValue();
        LocalDate toLocalDate = this.toDate_DatePicker.getValue();
        try {
            validateFromDate(fromLocalDate);
            validateToDate(toLocalDate);

            Date fromDate = Date.from(Instant.from(fromLocalDate.atStartOfDay(ZoneId.systemDefault())));
            Date toDate = Date.from(Instant.from(toLocalDate.atTime(LocalTime.MAX).atZone(ZoneId.systemDefault())));
            GenericChoiceBoxItem<Building> selected_building = this.building_ChoiceBox.getValue();
            GenericChoiceBoxItem<Floor> selected_floor = this.floor_ChoiceBox.getValue();
            GenericChoiceBoxItem<Room> selected_room = this.room_ChoiceBox.getValue();

            InfoType infoType = InfoType.INVALID;
            if (selected_building.hasDTO()) {
                infoType = InfoType.BUILDING;
                if (selected_floor != null && selected_floor.hasDTO()) {
                    infoType = InfoType.FLOOR;
                    if (selected_room != null && selected_room.hasDTO())
                        infoType = InfoType.ROOM;
                }
            }

            switch (infoType) {
                case INVALID:
                    this.searchParam_Text.setText("");
                    log.log_Error("No building selected in UI for getting LogisticsInfo from the records.");
                    break;
                case BUILDING:
                    getLogisticsTableData(fromDate, toDate, selected_building);
                    break;
                case FLOOR:
                    getLogisticsTableData(fromDate, toDate, selected_building, selected_floor);
                    break;
                case ROOM:
                    getLogisticsTableData(fromDate, toDate, selected_building, selected_floor, selected_room);
                    break;
            }

            loadLogisticsTable();

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
        } catch (Unauthorised e) {
            this.alertDialog.showGenericError(e);
        } catch (RemoteException e) {
            this.alertDialog.showGenericError(e);
        } catch (InvalidDate e) {
            log.log_Debug("User forgot to select from/to date(s)");
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.resourceBundle = resources;
        this.alertDialog = new AlertDialog(resources);

        this.building_col.setCellValueFactory(cellValue -> cellValue.getValue().buildingIdProperty().asObject());
        this.floor_col.setCellValueFactory(cellValue -> cellValue.getValue().floorIdProperty().asObject());
        this.room_col.setCellValueFactory(cellValue -> cellValue.getValue().roomIdProperty().asObject());
        this.seating_col.setCellValueFactory(cellValue -> cellValue.getValue().seatedProperty());
        this.timestampIn_col.setCellValueFactory(cellValue -> cellValue.getValue().inProperty());
        this.timestampOut_col.setCellValueFactory(cellValue -> cellValue.getValue().outProperty());
        this.catering_col.setCellValueFactory(cellValue -> cellValue.getValue().cateringFlagProperty());
        this.notes_col.setCellValueFactory(cellValue -> cellValue.getValue().noteProperty());

        this.building_ChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                GenericChoiceBoxItem<Building> selected = this.building_ChoiceBox.getSelectionModel().getSelectedItem();
                if (selected.hasDTO()) {
                    loadFloorChoiceBox(selected.getDTO());
                }
            }
        });

        this.floor_ChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                GenericChoiceBoxItem<Floor> selected = this.floor_ChoiceBox.getSelectionModel().getSelectedItem();
                if (selected.hasDTO()) {
                    loadRoomChoiceBox(selected.getDTO());
                } else { //'All floors'
                    this.roomListModel.clear();
                    this.room_ChoiceBox.setDisable(true);
                }
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
        this.buildingListModel = new BuildingListModel(this.sessionManager);
        this.floorListModel = new FloorListModel(this.sessionManager);
        this.roomListModel = new RoomListModel(this.sessionManager);

        loadBuildingChoiceBox();
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
    public void handleGoAction(ActionEvent actionEvent) {
        getLogisticsTableData();
    }
}
