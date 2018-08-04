package bbk_beam.mtRooms.ui.controller.administration;

import bbk_beam.mtRooms.MtRoomsGUI;
import bbk_beam.mtRooms.admin.dto.Account;
import bbk_beam.mtRooms.admin.dto.Usage;
import bbk_beam.mtRooms.admin.exception.FailedRecordFetch;
import bbk_beam.mtRooms.admin.exception.FailedRecordWrite;
import bbk_beam.mtRooms.exception.LoginException;
import bbk_beam.mtRooms.network.IRmiAdministrationServices;
import bbk_beam.mtRooms.network.exception.Unauthorised;
import bbk_beam.mtRooms.reservation.dto.Building;
import bbk_beam.mtRooms.reservation.dto.Floor;
import bbk_beam.mtRooms.reservation.dto.Membership;
import bbk_beam.mtRooms.reservation.dto.Room;
import bbk_beam.mtRooms.ui.controller.MainWindowController;
import bbk_beam.mtRooms.ui.controller.common.AlertDialog;
import bbk_beam.mtRooms.ui.model.SessionManager;
import bbk_beam.mtRooms.ui.model.administration.*;
import eadjlib.logger.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicBoolean;

public class AdministrationController implements Initializable {
    private final Logger log = Logger.getLoggerInstance(AdministrationController.class.getName());
    private AlertDialog alertDialog;
    private ResourceBundle resourceBundle;
    private MainWindowController mainWindowController;
    private SessionManager sessionManager;
    //Parent UI elements
    public TabPane admin_TabPane;
    public Tab userAccount_Tab;
    public Tab membership_Tab;
    public Tab inventory_Tab;
    //User account tab UI elements
    private UserAccountTable userAccountTable; //Model
    public TableView<UserAccountItem> account_table; //UI Accounts Table
    public TableColumn<UserAccountItem, Integer> id_col;
    public TableColumn<UserAccountItem, String> username_col;
    public TableColumn<UserAccountItem, String> created_col;
    public TableColumn<UserAccountItem, String> login_col;
    public TableColumn<UserAccountItem, String> pwd_change_col;
    public TableColumn<UserAccountItem, String> type_col;
    public TableColumn<UserAccountItem, Boolean> active_col;
    public Button newAccount_Button;
    public Button editAccount_Button;
    //Membership & Discount tab UI elements
    private MembershipList membershipList; //Model
    public ListView<CustomerMembershipItem> membership_ListView;
    public Button addMembership_Button;
    public Button removeMembership_Button;
    public TextArea membershipDescription_TextArea;
    //Inventory tab UI elements
    public MenuButton newInventory_MenuButton;
    public MenuItem newBuilding_MenuItem;
    public MenuItem newInventory_MenuItem;
    public Button editInventory_Button;
    public Button deleteInventory_Button;
    public TreeView<InventoryTreeItem> inventory_TreeView;
    public TextArea inventoryDescription_TextArea;

    /**
     * Opens the edit dialog window for a user account
     *
     * @param event           ActionEvent
     * @param userAccountItem UserAccountItem DTO to edit
     */
    private void openEditAccountDialog(ActionEvent event, UserAccountItem userAccountItem) {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(MtRoomsGUI.class.getResource("/view/administration/UserAccountView.fxml"));
        loader.setResources(this.resourceBundle);
        try {
            Stage stage = new Stage();
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(((Node) event.getTarget()).getScene().getWindow());
            stage.setScene(new Scene(loader.load()));
            stage.setOnHidden(e -> populateAccountTable());
            UserAccountController userAccountController = loader.getController();
            userAccountController.setSessionManager(this.sessionManager);
            if (userAccountItem != null) {
                stage.setTitle(this.resourceBundle.getString("DialogTitle_EditUserAccount"));
                userAccountController.setEditAccountFields(userAccountItem);
            } else {
                stage.setTitle(this.resourceBundle.getString("DialogTitle_NewUserAccount"));
                userAccountController.setNewAccountFields();
            }
            stage.show();
        } catch (RemoteException e) {
            this.alertDialog.showGenericError(e);
        } catch (IOException e) {
            log.log_Error("Could not load UI scene.");
            log.log_Exception(e);
            this.alertDialog.showGenericError(e);
        } catch (LoginException e) {
            this.alertDialog.showGenericError(e);
        } catch (Unauthorised e) {
            this.alertDialog.showGenericError(e);
        }
    }

    /**
     * Shows the Membership create dialog
     */
    private void showMembershipDialog() {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(MtRoomsGUI.class.getResource("/view/administration/MembershipView.fxml"));
        loader.setResources(this.resourceBundle);
        try {
            AnchorPane pane = loader.load();
            MembershipController membershipController = loader.getController();
            membershipController.setMainWindowController(this.mainWindowController);
            membershipController.setSessionManager(this.sessionManager);
            Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setOnHiding(event -> {
                if (membershipController.getMembership().isPresent())
                    populateMembershipListView();
            });
            dialog.setTitle(this.resourceBundle.getString("DialogTitle_NewMembership"));
            Scene scene = new Scene(pane);
            dialog.setScene(scene);
            dialog.show();
        } catch (IOException e) {
            log.log_Error("Could not load the 'Create membership' dialog.");
            log.log_Exception(e);
            this.alertDialog.showGenericError(e);
        }
    }

    /**
     * Shows the Building create/edit dialog
     *
     * @param building Building DTO to update or null when creating a new one
     */
    private void showBuildingDialog(Building building) {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(MtRoomsGUI.class.getResource("/view/administration/BuildingView.fxml"));
        loader.setResources(resourceBundle);
        try {
            AnchorPane pane = loader.load();
            BuildingController buildingController = loader.getController();
            buildingController.setSessionManager(sessionManager);
            buildingController.setMainWindowController(this.mainWindowController);
            buildingController.loadBuilding(building);
            Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setOnHiding(event -> {
                if (buildingController.getBuilding().isPresent()) {
                    inventoryDescription_TextArea.clear();
                    populateInventoryTree();
                }
            });
            dialog.setTitle((building == null
                    ? this.resourceBundle.getString("DialogTitle_NewBuilding")
                    : this.resourceBundle.getString("DialogTitle_EditBuilding") + " [" + building.id() + "]"));
            Scene scene = new Scene(pane);
            dialog.setScene(scene);
            dialog.show();
        } catch (IOException e) {
            log.log_Error("Could not load the 'Create/Edit building' dialog.");
            log.log_Exception(e);
            this.alertDialog.showGenericError(e);
        }
    }

    /**
     * Shows the floor creation dialog
     *
     * @param building Parent Building DTO
     */
    private void showFloorDialog(Building building) {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(MtRoomsGUI.class.getResource("/view/administration/FloorView.fxml"));
        loader.setResources(resourceBundle);
        try {
            AnchorPane pane = loader.load();
            FloorController floorController = loader.getController();
            floorController.setSessionManager(sessionManager);
            floorController.setMainWindowController(this.mainWindowController);
            floorController.loadFloor(building.id());
            Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setOnHiding(event -> {
                if (floorController.getFloor().isPresent()) {
                    inventoryDescription_TextArea.clear();
                    populateInventoryTree();
                }
            });
            dialog.setTitle(this.resourceBundle.getString("DialogTitle_NewFloor"));
            Scene scene = new Scene(pane);
            dialog.setScene(scene);
            dialog.show();
        } catch (IOException e) {
            log.log_Error("Could not load the 'Create floor' dialog.");
            log.log_Exception(e);
            this.alertDialog.showGenericError(e);
        }
    }

    /**
     * Shows the floor edit dialog
     *
     * @param floor Floor DTO to edit
     */
    private void showFloorDialog(Floor floor) {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(MtRoomsGUI.class.getResource("/view/administration/FloorView.fxml"));
        loader.setResources(resourceBundle);
        try {
            AnchorPane pane = loader.load();
            FloorController floorController = loader.getController();
            floorController.setSessionManager(sessionManager);
            floorController.setMainWindowController(this.mainWindowController);
            floorController.loadFloor(floor);
            Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setOnHiding(event -> {
                if (floorController.getFloor().isPresent()) {
                    inventoryDescription_TextArea.clear();
                    populateInventoryTree();
                }
            });
            dialog.setTitle(this.resourceBundle.getString("DialogTitle_EditFloor") + " [" + floor.buildingID() + "." + floor.floorID() + "]");
            Scene scene = new Scene(pane);
            dialog.setScene(scene);
            dialog.show();
        } catch (IOException e) {
            log.log_Error("Could not load the 'Edit floor' dialog.");
            log.log_Exception(e);
            this.alertDialog.showGenericError(e);
        }
    }

    /**
     * Shows the room creation dialog
     *
     * @param floor Parent Floor DTO
     */
    private void showRoomDialog(Floor floor) {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(MtRoomsGUI.class.getResource("/view/administration/RoomView.fxml"));
        loader.setResources(resourceBundle);
        try {
            AnchorPane pane = loader.load();
            RoomController roomController = loader.getController();
            roomController.setSessionManager(sessionManager);
            roomController.setMainWindowController(this.mainWindowController);
            roomController.loadRoom(floor);
            Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setOnHiding(event -> {
                if (roomController.getRoom().isPresent()) {
                    inventoryDescription_TextArea.clear();
                    populateInventoryTree();
                }
            });
            dialog.setTitle(this.resourceBundle.getString("DialogTitle_NewRoom"));
            Scene scene = new Scene(pane);
            dialog.setScene(scene);
            dialog.show();
        } catch (IOException e) {
            log.log_Error("Could not load the 'Create room' dialog.");
            log.log_Exception(e);
            this.alertDialog.showGenericError(e);
        }
    }

    /**
     * Shows the room edit dialog
     *
     * @param room Room DTO to edit
     */
    private void showRoomDialog(Room room) {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(MtRoomsGUI.class.getResource("/view/administration/RoomView.fxml"));
        loader.setResources(resourceBundle);
        try {
            AnchorPane pane = loader.load();
            RoomController roomController = loader.getController();
            roomController.setSessionManager(sessionManager);
            roomController.setMainWindowController(this.mainWindowController);
            roomController.loadRoom(room);
            Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setOnHiding(event -> {
                if (roomController.getRoom().isPresent()) {
                    inventoryDescription_TextArea.clear();
                    populateInventoryTree();
                }
            });
            dialog.setTitle(this.resourceBundle.getString("DialogTitle_EditRoom") + " [" + room.buildingID() + "." + room.floorID() + "." + room.id() + "]");
            Scene scene = new Scene(pane);
            dialog.setScene(scene);
            dialog.show();
        } catch (IOException e) {
            log.log_Error("Could not load the 'Edit room' dialog.");
            log.log_Exception(e);
            this.alertDialog.showGenericError(e);
        } catch (FailedRecordFetch e) {
            AlertDialog.showExceptionAlert(
                    resourceBundle.getString("ErrorDialogTitle_Generic"),
                    resourceBundle.getString("ErrorMsg_FailedBackendFetch"),
                    e
            );
        } catch (LoginException e) {
            this.alertDialog.showGenericError(e);
        } catch (Unauthorised e) {
            this.alertDialog.showGenericError(e);
        }
    }

    /**
     * Removes a building
     *
     * @param building Building DTO
     * @return Success
     */
    private boolean removeBuilding(Building building) {
        IRmiAdministrationServices services = this.sessionManager.getAdministrationServices();
        AtomicBoolean inventoryTree_update_flag = new AtomicBoolean(false);
        Optional<ButtonType> result = AlertDialog.showConfirmation(
                resourceBundle.getString("ConfirmationDialogTitle_Generic"),
                resourceBundle.getString("ConfirmationDialog_RemoveBuilding"),
                ""
        );
        result.ifPresent(response -> {
            try {
                if (response == ButtonType.OK) {
                    inventoryTree_update_flag.set(services.remove(this.sessionManager.getToken(), building));
                    if (!inventoryTree_update_flag.get())
                        AlertDialog.showAlert(Alert.AlertType.INFORMATION,
                                resourceBundle.getString("ErrorDialogTitle_Deletion"),
                                resourceBundle.getString("ErrorMsg_FailedDeletionBuilding")
                        );
                }
            } catch (FailedRecordWrite e) {
                AlertDialog.showExceptionAlert(
                        resourceBundle.getString("ErrorDialogTitle_Deletion"),
                        resourceBundle.getString("ErrorMsg_FailedBackendWrite"),
                        e
                );
            } catch (Unauthorised e) {
                this.alertDialog.showGenericError(e);
            } catch (RemoteException e) {
                this.alertDialog.showGenericError(e);
            } catch (LoginException e) {
                this.alertDialog.showGenericError(e);
            }
        });
        return inventoryTree_update_flag.get();
    }

    /**
     * Removes a floor
     *
     * @param floor Floor DTO
     * @return Success
     */
    private boolean removeFloor(Floor floor) {
        IRmiAdministrationServices services = this.sessionManager.getAdministrationServices();
        AtomicBoolean inventoryTree_update_flag = new AtomicBoolean(false);
        Optional<ButtonType> result = AlertDialog.showConfirmation(
                resourceBundle.getString("ConfirmationDialogTitle_Generic"),
                resourceBundle.getString("ConfirmationDialog_RemoveFloor"),
                ""
        );
        result.ifPresent(response -> {
            try {
                if (response == ButtonType.OK) {
                    inventoryTree_update_flag.set(services.remove(this.sessionManager.getToken(), floor));
                    if (!inventoryTree_update_flag.get())
                        AlertDialog.showAlert(Alert.AlertType.INFORMATION,
                                resourceBundle.getString("ErrorDialogTitle_Deletion"),
                                resourceBundle.getString("ErrorMsg_FailedDeletionFloor")
                        );
                }
            } catch (FailedRecordWrite e) {
                AlertDialog.showExceptionAlert(
                        resourceBundle.getString("ErrorDialogTitle_Deletion"),
                        resourceBundle.getString("ErrorMsg_FailedBackendWrite"),
                        e
                );
            } catch (Unauthorised e) {
                this.alertDialog.showGenericError(e);
            } catch (RemoteException e) {
                this.alertDialog.showGenericError(e);
            } catch (LoginException e) {
                this.alertDialog.showGenericError(e);
            }
        });
        return inventoryTree_update_flag.get();
    }

    /**
     * Removes a room
     *
     * @param room Room DTO
     * @return Success
     */
    private boolean removeRoom(Room room) {
        IRmiAdministrationServices services = this.sessionManager.getAdministrationServices();
        AtomicBoolean inventoryTree_update_flag = new AtomicBoolean(false);
        Optional<ButtonType> result = AlertDialog.showConfirmation(
                resourceBundle.getString("ConfirmationDialogTitle_Generic"),
                resourceBundle.getString("ConfirmationDialog_RemoveRoom"),
                ""
        );
        result.ifPresent(response -> {
            try {
                if (response == ButtonType.OK) {
                    inventoryTree_update_flag.set(services.remove(this.sessionManager.getToken(), room));
                    if (!inventoryTree_update_flag.get())
                        AlertDialog.showAlert(Alert.AlertType.INFORMATION,
                                resourceBundle.getString("ErrorDialogTitle_Deletion"),
                                resourceBundle.getString("ErrorMsg_FailedDeletionRoom")
                        );
                }
            } catch (FailedRecordWrite e) {
                AlertDialog.showExceptionAlert(
                        resourceBundle.getString("ErrorDialogTitle_Deletion"),
                        resourceBundle.getString("ErrorMsg_FailedBackendWrite"),
                        e
                );
            } catch (Unauthorised e) {
                this.alertDialog.showGenericError(e);
            } catch (RemoteException e) {
                this.alertDialog.showGenericError(e);
            } catch (LoginException e) {
                this.alertDialog.showGenericError(e);
            }
        });
        return inventoryTree_update_flag.get();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.resourceBundle = resources;
        this.alertDialog = new AlertDialog(resources);

        id_col.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
        username_col.setCellValueFactory(cellData -> cellData.getValue().usernameProperty());
        created_col.setCellValueFactory(cellData -> cellData.getValue().createdProperty().asString());
        login_col.setCellValueFactory(cellData -> cellData.getValue().LastLoginProperty().asString());
        pwd_change_col.setCellValueFactory(cellData -> cellData.getValue().lastPwdChangeProperty().asString());
        created_col.setCellValueFactory(cellData -> cellData.getValue().createdProperty().asString());
        type_col.setCellValueFactory(cellData -> cellData.getValue().accountTypeDescProperty().asString());
        active_col.setCellValueFactory(cellData -> cellData.getValue().isActiveProperty().asObject());

        this.userAccount_Tab.setOnSelectionChanged(value -> populateAccountTable());
        this.membership_Tab.setOnSelectionChanged(value -> populateMembershipListView());
        this.inventory_Tab.setOnSelectionChanged(value -> populateInventoryTree());

        this.membership_ListView.getSelectionModel().selectedItemProperty().addListener(((observable, oldValue, newValue) -> {
            if (newValue != null) {
                this.removeMembership_Button.setDisable(
                        newValue.getMembership().id() == 1 || !newValue.getUsageByCustomerID().isEmpty() //Membership 'NONE' || Membership used by customer(s)
                );
                this.membershipDescription_TextArea.setText(
                        this.resourceBundle.getString("Label_Discount") + ": " + newValue.getMembership().discount().rate() + "%" +
                                (newValue.getUsageByCustomerID().isEmpty()
                                        ? ""
                                        : "\n\n" + this.resourceBundle.getString("Label_MembershipUsingCustomers") + "\n" + newValue.getUsageByCustomerID().toString()
                                )
                );
            }
        }));

        this.inventory_TreeView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null)
                if (newValue.getValue() instanceof BuildingTreeItem) {
                    this.newInventory_MenuItem.setVisible(true);
                    this.newInventory_MenuItem.setText(this.resourceBundle.getString("Button_AddFloor"));
                    this.editInventory_Button.setDisable(false);
                    this.deleteInventory_Button.setDisable(false);
                    Building building = ((BuildingTreeItem) newValue.getValue()).getBuilding();
                    inventoryDescription_TextArea.setText(building.name() + "\n" +
                            building.address1() + "\n" +
                            (building.address2() != null ? building.address2() + "\n" : "") +
                            building.city() + "\n" +
                            building.postcode() + "\n" +
                            building.country() + "\n\n" +
                            "Tel: " + building.phone()
                    );
                } else if (newValue.getValue() instanceof FloorTreeItem) {
                    this.newInventory_MenuItem.setVisible(true);
                    this.newInventory_MenuItem.setText(this.resourceBundle.getString("Button_AddRoom"));
                    this.editInventory_Button.setDisable(false);
                    this.deleteInventory_Button.setDisable(false);
                    Floor floor = ((FloorTreeItem) newValue.getValue()).getFloor();
                    inventoryDescription_TextArea.setText(floor.description());
                } else if (newValue.getValue() instanceof RoomTreeItem) {
                    this.newInventory_MenuItem.setVisible(false);
                    this.editInventory_Button.setDisable(false);
                    this.deleteInventory_Button.setDisable(false);
                    Room room = ((RoomTreeItem) newValue.getValue()).getRoom();
                    inventoryDescription_TextArea.setText(room.description() + "\n" + "(Cat. " + room.category() + ")");
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
        this.userAccountTable = new UserAccountTable(this.sessionManager);
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
     * Loads the account model into the UI account table
     */
    public void populateAccountTable() {
        try {
            IRmiAdministrationServices services = sessionManager.getAdministrationServices();
            List<Account> accountList = services.getAccounts(sessionManager.getToken());
            this.userAccountTable.loadData(accountList);
            this.account_table.setItems(this.userAccountTable.getData());
        } catch (LoginException e) {
            this.alertDialog.showGenericError(e);
        } catch (Unauthorised e) {
            this.alertDialog.showGenericError(e);
        } catch (RemoteException e) {
            this.alertDialog.showGenericError(e);
        }
    }

    /**
     * Loads the inventory model into the UI inventory TreeView
     */
    public void populateInventoryTree() {
        try {
            IRmiAdministrationServices services = this.sessionManager.getAdministrationServices();
            List<Building> buildings = services.getBuildings(this.sessionManager.getToken());
            TreeItem<InventoryTreeItem> root = new TreeItem<>(new InventoryTreeItem());
            root.setExpanded(true);

            for (Building building : buildings) {
                TreeItem<InventoryTreeItem> building_item = new TreeItem<>(new BuildingTreeItem(building));
                building_item.setExpanded(true);
                root.getChildren().add(building_item);
                for (Floor floor : services.getFloors(this.sessionManager.getToken(), building)) {
                    TreeItem<InventoryTreeItem> floor_item = new TreeItem<>(new FloorTreeItem(floor));
                    floor_item.setExpanded(true);
                    building_item.getChildren().add(floor_item);
                    for (Room room : services.getRooms(this.sessionManager.getToken(), floor)) {
                        TreeItem<InventoryTreeItem> room_item = new TreeItem<>(new RoomTreeItem(room));
                        floor_item.getChildren().add(room_item);
                    }
                }
            }

            this.newInventory_MenuItem.setVisible(false);
            this.editInventory_Button.setDisable(true);
            this.deleteInventory_Button.setDisable(true);
            this.inventoryDescription_TextArea.clear();

            this.inventory_TreeView.setRoot(root);
            this.inventory_TreeView.setShowRoot(false);
        } catch (FailedRecordFetch e) {
            AlertDialog.showAlert(
                    Alert.AlertType.ERROR,
                    this.resourceBundle.getString("ErrorDialogTitle_Generic"),
                    this.resourceBundle.getString("ErrorMsg_FailedRecordFetch")
            );
        } catch (Unauthorised e) {
            this.alertDialog.showGenericError(e);
        } catch (RemoteException e) {
            this.alertDialog.showGenericError(e);
        } catch (LoginException e) {
            this.alertDialog.showGenericError(e);
        }
    }

    /**
     * Loads the membership model into the UI membership ListView
     */
    public void populateMembershipListView() {
        try {
            IRmiAdministrationServices services = this.sessionManager.getAdministrationServices();
            List<Usage<Membership, Integer>> list = services.getMemberships(this.sessionManager.getToken());
            this.membershipList = new MembershipList(this.sessionManager);
            this.membershipList.loadData(list);
            this.membership_ListView.setItems(this.membershipList.getData());

            this.removeMembership_Button.setDisable(true);
            this.membershipDescription_TextArea.clear();
        } catch (FailedRecordFetch e) {
            AlertDialog.showAlert(
                    Alert.AlertType.ERROR,
                    this.resourceBundle.getString("ErrorDialogTitle_Generic"),
                    this.resourceBundle.getString("ErrorMsg_FailedRecordFetch")
            );
        } catch (RemoteException e) {
            this.alertDialog.showGenericError(e);
        } catch (LoginException e) {
            this.alertDialog.showGenericError(e);
        } catch (Unauthorised e) {
            this.alertDialog.showGenericError(e);
        }
    }

    @FXML
    public void handleEditAccountAction(ActionEvent actionEvent) {
        UserAccountItem userAccountItem = this.account_table.getSelectionModel().getSelectedItem();
        if (userAccountItem != null)
            openEditAccountDialog(actionEvent, userAccountItem);
    }

    @FXML
    public void handleNewAccountAction(ActionEvent actionEvent) {
        openEditAccountDialog(actionEvent, null);
    }

    public void handleAddBuildingAction(ActionEvent actionEvent) {
        showBuildingDialog(null);
    }

    public void handleAddInventoryAction(ActionEvent actionEvent) {
        TreeItem<InventoryTreeItem> selected = this.inventory_TreeView.getSelectionModel().getSelectedItem();
        if (selected.getValue() instanceof BuildingTreeItem) {
            showFloorDialog(((BuildingTreeItem) selected.getValue()).getBuilding());
        } else if (selected.getValue() instanceof FloorTreeItem) {
            showRoomDialog(((FloorTreeItem) selected.getValue()).getFloor());
        }
    }

    public void handleDeleteInventoryAction(ActionEvent actionEvent) {
        TreeItem<InventoryTreeItem> selected = this.inventory_TreeView.getSelectionModel().getSelectedItem();
        if (selected.getValue() instanceof BuildingTreeItem) {
            if (removeBuilding(((BuildingTreeItem) selected.getValue()).getBuilding()))
                populateInventoryTree();
        } else if (selected.getValue() instanceof FloorTreeItem) {
            if (removeFloor(((FloorTreeItem) selected.getValue()).getFloor()))
                populateInventoryTree();
        } else if (selected.getValue() instanceof RoomTreeItem) {
            if (removeRoom(((RoomTreeItem) selected.getValue()).getRoom()))
                populateInventoryTree();
        }
    }

    public void handleEditInventoryAction(ActionEvent actionEvent) {
        TreeItem<InventoryTreeItem> selected = this.inventory_TreeView.getSelectionModel().getSelectedItem();
        if (selected.getValue() instanceof BuildingTreeItem) {
            showBuildingDialog(((BuildingTreeItem) selected.getValue()).getBuilding());
        } else if (selected.getValue() instanceof FloorTreeItem) {
            showFloorDialog(((FloorTreeItem) selected.getValue()).getFloor());
        } else if (selected.getValue() instanceof RoomTreeItem) {
            showRoomDialog(((RoomTreeItem) selected.getValue()).getRoom());
        }
    }

    public void handleAddMembershipAction(ActionEvent actionEvent) {
        showMembershipDialog();
    }

    public void handleRemoveMembershipAction(ActionEvent actionEvent) {
        IRmiAdministrationServices services = this.sessionManager.getAdministrationServices();
        CustomerMembershipItem customerMembershipItem = this.membership_ListView.getSelectionModel().getSelectedItem();
        try {
            services.remove(this.sessionManager.getToken(), customerMembershipItem.getMembership());
            populateMembershipListView();
        } catch (FailedRecordWrite e) {
            AlertDialog.showAlert(
                    Alert.AlertType.ERROR,
                    this.resourceBundle.getString("ErrorDialogTitle_Generic"),
                    this.resourceBundle.getString("ErrorMsg_FailedRecordWrite")
            );
        } catch (RemoteException e) {
            this.alertDialog.showGenericError(e);
        } catch (LoginException e) {
            this.alertDialog.showGenericError(e);
        } catch (Unauthorised e) {
            this.alertDialog.showGenericError(e);
        }
    }
}
