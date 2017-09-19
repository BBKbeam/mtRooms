package bbk_beam.mtRooms.db.session;

import bbk_beam.mtRooms.db.exception.SessionException;
import bbk_beam.mtRooms.db.exception.SessionInvalidException;
import org.junit.Assert;
import org.junit.Test;

import java.util.Date;
import java.time.Instant;

import static org.junit.Assert.*;

public class SessionTrackerTest {
    @Test(expected = SessionException.class)
    public void addSession() throws Exception {
        Date date = Date.from(Instant.now());
        SessionTracker sessionTracker = new SessionTracker();
        try {
            sessionTracker.addSession("0001", date);
        } catch (SessionException e) {
            Assert.assertTrue(false); //Should not have thrown
        }
        sessionTracker.addSession("0001", date);
    }

    @Test(expected = SessionInvalidException.class)
    public void removeSession() throws Exception {
        Date date = Date.from(Instant.now());
        SessionTracker sessionTracker = new SessionTracker();
        sessionTracker.addSession("0001", date);
        try {
            sessionTracker.removeSession("0001");
        } catch (SessionInvalidException e) {
            Assert.assertTrue(false); //Should not have thrown
        }
        sessionTracker.removeSession("0001");
    }

    @Test
    public void isTracked() throws Exception {
        //TODO
        Assert.assertTrue(false);
    }

    @Test
    public void getSessionType() throws Exception {
        //TODO
        Assert.assertTrue(false);
    }

    @Test
    public void exists() throws Exception {
        //TODO
        Assert.assertTrue(false);
    }

    @Test
    public void isValid() throws Exception {
        //TODO
        Assert.assertTrue(false);
    }

}