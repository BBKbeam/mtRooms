package bbk_beam.mtRooms.ui;

import bbk_beam.mtRooms.admin.exception.AuthenticationFailureException;
import bbk_beam.mtRooms.db.exception.SessionInvalidException;
import bbk_beam.mtRooms.network.IRmiClient;
import bbk_beam.mtRooms.network.IRmiServices;
import bbk_beam.mtRooms.uaa.exception.SessionInactive;
import bbk_beam.mtRooms.ui.network.RmiClient;
import eadjlib.logger.Logger;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.apache.commons.cli.*;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class MtRoomsGUI extends Application {
    private final Logger log = Logger.getLoggerInstance(MtRoomsGUI.class.getName());
    private static Integer port = 9999; //default
    private static String server_address = "localhost";
    private static IRmiServices remoteServices;
    private static IRmiClient client;

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

    public static void main(String[] argv) {
        Logger log = Logger.getLoggerInstance(MtRoomsGUI.class.getName());
        //CLI parser options
        Options options = new Options();
        options.addOption(Option.builder("p")
                .longOpt("port")
                .desc("port number to use for server (default=9999)")
                .hasArg()
                .argName("PORT")
                .type(Integer.class)
                .build()
        );
        options.addOption(Option.builder("a")
                .longOpt("address")
                .desc("IP address of the mtRooms server (default=localhost)")
                .hasArg()
                .argName("ADDRESS")
                .build()
        );
        options.addOption(Option.builder("h")
                .longOpt("help")
                .build()
        );
        CommandLineParser parser = new DefaultParser();

        try {
            CommandLine cmd = parser.parse(options, argv);
            if(cmd.hasOption("help")) {
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp("backend", options);
                System.exit(0);
            }
            if (cmd.hasOption("port"))
                port = Integer.parseInt(cmd.getOptionValue("port"));
            if(cmd.hasOption("address"))
                server_address = cmd.getOptionValue("address");
            log.log("Trying to connect to services on //" + server_address + ":" + port + "/RmiServices");
            remoteServices = (IRmiServices) Naming.lookup("//" + server_address + ":" + port + "/RmiServices");
            client = new RmiClient();
            //Testing...
            remoteServices.login(client, "root", "letmein");
            if (remoteServices.hasAdministrativeAccess(client.getToken()))
                log.log("Client [", client.getToken(), "] has administrative rights.");
            remoteServices.logout(client.getToken());
            //End Testing
            launch(argv);
        } catch (ParseException e) {
            System.err.println("Parsing failed. Reason: " + e.getMessage());
        } catch (SessionInvalidException e) {
            e.printStackTrace();
        } catch (NotBoundException e) {
            e.printStackTrace();
        } catch (AuthenticationFailureException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (SessionInactive sessionInactive) {
            sessionInactive.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
