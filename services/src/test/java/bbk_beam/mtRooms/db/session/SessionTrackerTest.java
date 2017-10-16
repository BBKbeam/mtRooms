package bbk_beam.mtRooms.db.session;

import bbk_beam.mtRooms.db.exception.SessionException;
import bbk_beam.mtRooms.db.exception.SessionInvalidException;
import org.junit.Assert;
import org.junit.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

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
        Date date = Date.from(Instant.now());
        SessionTracker sessionTracker = new SessionTracker();
        Assert.assertFalse(sessionTracker.isTracked("0001"));
        sessionTracker.addSession("0001", date);
        Assert.assertTrue(sessionTracker.isTracked("0001"));
        sessionTracker.removeSession("0001");
        Assert.assertFalse(sessionTracker.isTracked("0001"));
    }

    @Test
    public void getSessionExpiry() throws Exception {
        Date date = Date.from(Instant.now());
        SessionTracker sessionTracker = new SessionTracker();
        Assert.assertNull(sessionTracker.getSessionExpiry("0001"));
        sessionTracker.addSession("0001", date);
        Assert.assertEquals(date, sessionTracker.getSessionExpiry("0001"));
        sessionTracker.removeSession("0001");
        Assert.assertNull(sessionTracker.getSessionExpiry("0001"));
    }

    @Test
    public void exists() throws Exception {
        Date date = Date.from(Instant.now());
        SessionTracker sessionTracker = new SessionTracker();
        Assert.assertFalse(sessionTracker.exists("0001"));
        sessionTracker.addSession("0001", date);
        Assert.assertTrue(sessionTracker.exists("0001"));
        sessionTracker.removeSession("0001");
        Assert.assertFalse(sessionTracker.exists("0001"));
    }

    @Test
    public void isValid() throws Exception {
        Date date_valid = Date.from(Instant.now().plus(1, ChronoUnit.HOURS));
        Date date_invalid = Date.from(Instant.EPOCH);
        SessionTracker sessionTracker = new SessionTracker();
        sessionTracker.addSession("valid_session", date_valid);
        sessionTracker.addSession("invalid_session", date_invalid);
        Assert.assertTrue(sessionTracker.isValid("valid_session"));
        Assert.assertFalse(sessionTracker.isValid("invalid_session"));
    }

    @Test(expected = SessionInvalidException.class)
    public void isValid_fail() throws Exception {
        SessionTracker sessionTracker = new SessionTracker();
        sessionTracker.isValid("0001");
    }
}