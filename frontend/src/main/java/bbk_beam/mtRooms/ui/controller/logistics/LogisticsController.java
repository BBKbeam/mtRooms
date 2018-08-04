package bbk_beam.mtRooms.ui.controller.logistics;

import bbk_beam.mtRooms.exception.LoginException;
import bbk_beam.mtRooms.network.IRmiLogisticsServices;
import bbk_beam.mtRooms.network.exception.Unauthorised;
import bbk_beam.mtRooms.reservation.dto.Building;
import bbk_beam.mtRooms.reservation.dto.Floor;
import bbk_beam.mtRooms.reservation.dto.Room;
import bbk_beam.mtRooms.reservation.exception.FailedDbFetch;
import bbk_beam.mtRooms.ui.controller.MainWindowController;
import bbk_beam.mtRooms.ui.controller.common.AlertDialog;
import bbk_beam.mtRooms.ui.model.SessionManager;
import bbk_beam.mtRooms.ui.model.common.GenericChoiceBoxItem;
import bbk_beam.mtRooms.ui.model.logistics.BuildingListModel;
import bbk_beam.mtRooms.ui.model.logistics.FloorListModel;
import bbk_beam.mtRooms.ui.model.logistics.RoomListModel;
import eadjlib.logger.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.text.Text;

import java.net.URL;
import java.rmi.RemoteException;
import java.util.List;
import java.util.ResourceBundle;

public class LogisticsController implements Initializable {
    private final Logger log = Logger.getLoggerInstance(LogisticsController.class.getName());
    private AlertDialog alertDialog;
    private SessionManager sessionManager;
    private MainWindowController mainWindowController;
    private ResourceBundle resourceBundle;

    private BuildingListModel buildingListModel;
    private FloorListModel floorListModel;
    private RoomListModel roomListModel;

    public DatePicker fromDate_DatePicker;
    public DatePicker toDate_DatePicker;
    public ChoiceBox<GenericChoiceBoxItem<Building>> building_ChoiceBox;
    public ChoiceBox<GenericChoiceBoxItem<Floor>> floor_ChoiceBox;
    public ChoiceBox<GenericChoiceBoxItem<Room>> room_ChoiceBox;
    public Button go_Button;
    public Text searchParam_Text;
    public TableView info_TableView;
    public TableColumn building_col;
    public TableColumn floor_col;
    public TableColumn room_col;
    public TableColumn seating_col;
    public TableColumn timestampIn_col;
    public TableColumn timestampOut_col;
    public TableColumn catering_col;
    public TableColumn notes_col;

    //TODO check building has floors, floor has rooms before enabling the 'go' button

    private void loadBuildingChoiceBox() {
        try {
            IRmiLogisticsServices services = this.sessionManager.getLogisticsServices();

            List<Building> buildings = services.getBuildings(this.sessionManager.getToken());
            this.buildingListModel.clear();
            this.buildingListModel.appendData(new GenericChoiceBoxItem<>(this.resourceBundle.getString("ChoiceBox_AllBuildings")));
            this.buildingListModel.appendData(buildings);

            this.building_ChoiceBox.setItems(this.buildingListModel.getData());
            this.building_ChoiceBox.getSelectionModel().selectFirst();
            this.building_ChoiceBox.setDisable(false);
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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.resourceBundle = resources;
        this.alertDialog = new AlertDialog(resources);

        this.building_ChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                GenericChoiceBoxItem<Building> selected = this.building_ChoiceBox.getSelectionModel().getSelectedItem();
                if (selected.hasDTO()) {
                    loadFloorChoiceBox(selected.getDTO());
                } else { //'All buildings'
                    this.floorListModel.clear();
                    this.floor_ChoiceBox.setDisable(true);
                    this.roomListModel.clear();
                    this.room_ChoiceBox.setDisable(true);
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

    public void handleGoAction(ActionEvent actionEvent) { //TODO
        IRmiLogisticsServices services = this.sessionManager.getLogisticsServices();
    }
}
