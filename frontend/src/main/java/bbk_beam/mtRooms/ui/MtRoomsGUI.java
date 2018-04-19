package bbk_beam.mtRooms.ui;

import bbk_beam.mtRooms.admin.authentication.Token;
import bbk_beam.mtRooms.network.IRmiClient;
import bbk_beam.mtRooms.network.IRmiServices;
import bbk_beam.mtRooms.ui.network.RmiClient;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.rmi.Naming;

public class MtRoomsGUI extends Application {

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
        try {
            IRmiServices remoteService = (IRmiServices) Naming.lookup("//localhost:9999/RmiServices");
            IRmiClient client = new RmiClient();
            //Testing...
            Token token = remoteService.login(client, "root", "letmein");
            remoteService.logout(client.getToken());
            //End Testing
            launch(args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
