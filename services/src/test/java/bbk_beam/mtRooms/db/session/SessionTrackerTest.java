package bbk_beam.mtRooms.db.session;

import bbk_beam.mtRooms.db.exception.SessionCorruptedException;
import bbk_beam.mtRooms.db.exception.SessionException;
import bbk_beam.mtRooms.db.exception.SessionExpiredException;
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
            sessionTracker.addSession("0001", date, SessionType.USER, 0);
        } catch (SessionException e) {
            Assert.assertTrue(false); //Should not have thrown
        }
        sessionTracker.addSession("0001", date, SessionType.USER, 0);
    }

    @Test(expected = SessionInvalidException.class)
    public void removeSession() throws Exception {
        Date date = Date.from(Instant.now());
        SessionTracker sessionTracker = new SessionTracker();
        sessionTracker.addSession("0001", date, SessionType.USER, 0);
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
        sessionTracker.addSession("0001", date, SessionType.USER, 0);
        Assert.assertTrue(sessionTracker.isTracked("0001"));
        sessionTracker.removeSession("0001");
        Assert.assertFalse(sessionTracker.isTracked("0001"));
    }

    @Test
    public void getSessionExpiry() throws Exception {
        Date date = Date.from(Instant.now());
        SessionTracker sessionTracker = new SessionTracker();
        sessionTracker.addSession("0001", date, SessionType.USER, 0);
        Assert.assertEquals(date, sessionTracker.getSessionExpiry("0001"));
    }

    @Test(expected = SessionInvalidException.class)
    public void getSessionExpiry_fail() throws Exception {
        Date date = Date.from(Instant.now());
        SessionTracker sessionTracker = new SessionTracker();
        sessionTracker.getSessionExpiry("0001");
    }

    @Test
    public void getSessionType() throws Exception {
        Date date_valid = Date.from(Instant.now().plus(1, ChronoUnit.HOURS));
        SessionTracker sessionTracker = new SessionTracker();
        sessionTracker.addSession("valid_session1", date_valid, SessionType.USER, 0);
        sessionTracker.addSession("valid_session2", date_valid, SessionType.ADMIN, 0);
        Assert.assertEquals(SessionType.USER, sessionTracker.getSessionType("valid_session1"));
        Assert.assertEquals(SessionType.ADMIN, sessionTracker.getSessionType("valid_session2"));
    }

    @Test(expected = SessionInvalidException.class)
    public void getSessionType_fails() throws Exception {
        SessionTracker sessionTracker = new SessionTracker();
        sessionTracker.getSessionType("0001");
    }

    @Test
    public void getSessionAccountID() throws Exception {
        SessionTracker sessionTracker = new SessionTracker();
        sessionTracker.addSession("0001", Date.from(Instant.now()), SessionType.USER, 0);
        Assert.assertEquals((Integer) 0, sessionTracker.getSessionAccountID("0001"));
    }

    @Test(expected = SessionInvalidException.class)
    public void getSessionAccountID_fail() throws Exception {
        SessionTracker sessionTracker = new SessionTracker();
        sessionTracker.getSessionAccountID("0001");
    }

    @Test
    public void exists() throws Exception {
        Date date = Date.from(Instant.now());
        SessionTracker sessionTracker = new SessionTracker();
        Assert.assertFalse(sessionTracker.exists("0001"));
        sessionTracker.addSession("0001", date, SessionType.USER, 0);
        Assert.assertTrue(sessionTracker.exists("0001"));
        sessionTracker.removeSession("0001");
        Assert.assertFalse(sessionTracker.exists("0001"));
    }

    @Test
    public void checkValidity_Internal() throws Exception {
        Date date_valid = Date.from(Instant.now().plus(1, ChronoUnit.HOURS));
        Date date_invalid = Date.from(Instant.EPOCH);
        SessionTracker sessionTracker = new SessionTracker();
        sessionTracker.addSession("valid_session", date_valid, SessionType.USER, 0);
        sessionTracker.addSession("invalid_session", date_invalid, SessionType.USER, 0);
        sessionTracker.checkValidity("valid_session");
        Assert.assertTrue(true); //No exception thrown
    }

    @Test(expected = SessionExpiredException.class)
    public void checkValidity_Internal_fail_expired() throws Exception {
        Date date_invalid = Date.from(Instant.EPOCH);
        SessionTracker sessionTracker = new SessionTracker();
        sessionTracker.addSession("invalid_session", date_invalid, SessionType.USER, 0);
        sessionTracker.checkValidity("invalid_session");
    }

    @Test(expected = SessionInvalidException.class)
    public void checkValidity_Internal_fail_invalid() throws Exception {
        SessionTracker sessionTracker = new SessionTracker();
        sessionTracker.checkValidity("0001");
    }

    @Test
    public void checkValidity_Full() throws Exception {
        Date date_valid = Date.from(Instant.now().plus(1, ChronoUnit.HOURS));
        SessionTracker sessionTracker = new SessionTracker();
        sessionTracker.addSession("valid_session", date_valid, SessionType.USER, 0);
        sessionTracker.checkValidity("valid_session", date_valid);
        Assert.assertTrue(true); //No exception thrown
    }

    @Test(expected = SessionExpiredException.class)
    public void checkValidity_Full_fail_expired() throws Exception {
        Date date_invalid = Date.from(Instant.EPOCH);
        SessionTracker sessionTracker = new SessionTracker();
        sessionTracker.addSession("valid_session", date_invalid, SessionType.USER, 0);
        sessionTracker.checkValidity("valid_session", date_invalid);
    }


    @Test(expected = SessionCorruptedException.class)
    public void checkValidity_Full_fail_corrupted() throws Exception {
        Date date_valid = Date.from(Instant.now().plus(1, ChronoUnit.HOURS));
        Date date_invalid = Date.from(Instant.EPOCH);
        SessionTracker sessionTracker = new SessionTracker();
        sessionTracker.addSession("valid_session", date_valid, SessionType.USER, 0);
        sessionTracker.checkValidity("valid_session", date_invalid);
    }

    @Test(expected = SessionInvalidException.class)
    public void checkValidity_Full_fail_invalid() throws Exception {
        Date date_valid = Date.from(Instant.now().plus(1, ChronoUnit.HOURS));
        SessionTracker sessionTracker = new SessionTracker();
        sessionTracker.checkValidity("0001", date_valid);
    }
}