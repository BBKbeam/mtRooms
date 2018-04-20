package bbk_beam.mtRooms.ui;

import bbk_beam.mtRooms.admin.authentication.Token;
import bbk_beam.mtRooms.network.IRmiClient;
import bbk_beam.mtRooms.network.IRmiServices;
import bbk_beam.mtRooms.network.RmiServer;
import bbk_beam.mtRooms.ui.network.RmiClient;
import eadjlib.logger.Logger;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.rmi.Naming;

public class MtRoomsGUI extends Application {
    private final Logger log = Logger.getLoggerInstance(MtRoomsGUI.class.getName());
    static IRmiServices remoteServices;
    static IRmiClient client;

    @Override
    public void start(Stage primaryStage) throws Exception {
        try {
            BorderPane root = new BorderPane();
            Scene scene = new Scene(root, 800, 600);
            primaryStage.setTitle("mtRooms");
            primaryStage.setScene(scene);
            primaryStage.show();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        final Logger log = Logger.getLoggerInstance(MtRoomsGUI.class.getName());
        try {
            remoteServices = (IRmiServices) Naming.lookup("//localhost:9999/RmiServices");
            client = new RmiClient();
            //Testing...
            remoteServices.login(client, "root", "letmein");
            if( remoteServices.hasAdministrativeAccess(client.getToken()) )
                log.log("Client [", client.getToken(), "] has administrative rights.");
            remoteServices.logout(client.getToken());
            //End Testing
            launch(args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
