package bbk_beam.mtRooms.ui.controller.frontdesk;

import bbk_beam.mtRooms.ui.model.SessionManager;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;

public class ReservationController implements Initializable {
    private  ResourceBundle resourceBundle;
    private SessionManager sessionManager;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.resourceBundle = resources;
    }

    /**
     * Sets the session manager for the controller
     *
     * @param sessionManager SessionManager instance
     */
    void setSessionManager(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }
}
