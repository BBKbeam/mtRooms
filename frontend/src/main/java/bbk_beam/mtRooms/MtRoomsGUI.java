package bbk_beam.mtRooms;

import bbk_beam.mtRooms.exception.ClientInitFailure;
import bbk_beam.mtRooms.ui.controller.MainWindowController;
import bbk_beam.mtRooms.ui.model.SessionManager;
import eadjlib.logger.Logger;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.commons.cli.*;

import java.util.ResourceBundle;

public class MtRoomsGUI extends Application {
    private final Logger log = Logger.getLoggerInstance(MtRoomsGUI.class.getName());
    private static Integer port = 9999; //default
    private static String server_address = "localhost";
    private static SessionManager sessionManager;

    @Override
    public void start(Stage primaryStage) throws Exception {
        try {
            FXMLLoader loader = new FXMLLoader();
            ResourceBundle languageResource = ResourceBundle.getBundle("bundles.MtRooms_en");
            loader.setLocation(MtRoomsGUI.class.getResource("/view/MainWindow.fxml"));
            loader.setResources(languageResource);
            Parent root = loader.load();

            MainWindowController mainWindowController = loader.getController();
            mainWindowController.setSessionManager(sessionManager);
            mainWindowController.setViewMenuAccessibility();
            mainWindowController.showLoginPane();

            Scene scene = new Scene(root);
            primaryStage.setTitle("mtRooms");
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stop() throws Exception {
        sessionManager.logout();
        super.stop();
        System.exit(0);
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

            sessionManager = new SessionManager(server_address, port);
            launch(argv);
        } catch (ParseException e) {
            System.err.println("Parsing failed. Reason: " + e.getMessage());
        } catch (ClientInitFailure e) {
            log.log_Fatal("Could not start client.");
            log.log_Exception(e);
            System.exit(1);
        }
    }
}
