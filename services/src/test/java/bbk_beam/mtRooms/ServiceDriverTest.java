package bbk_beam.mtRooms;

import bbk_beam.mtRooms.admin.authentication.IAuthenticationSystem;
import bbk_beam.mtRooms.admin.authentication.Token;
import bbk_beam.mtRooms.reservation.ReservationSession;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Paths;

public class ServiceDriverTest {
    private ServiceDriver serviceDriver;

    @Before
    public void setUp() throws Exception {
        Files.deleteIfExists(Paths.get("ServiceDriver_test.db"));
        this.serviceDriver = new ServiceDriver("ServiceDriver_test.db");
    }

    @After
    public void tearDown() throws Exception {
        this.serviceDriver = null;
        Files.deleteIfExists(Paths.get("ServiceDriver_test.db"));
    }

    @Test
    public void getAuthenticationSystem() throws Exception {
        IAuthenticationSystem authenticationSystem = this.serviceDriver.getAuthenticationSystem();
        Token token = authenticationSystem.login("root", "letmein");
        ReservationSession session = this.serviceDriver.createNewReservationSession(token);
    }

    @Test
    public void getAdminSession() {
    }

    @Test
    public void createNewReservationSession() {
    }
}