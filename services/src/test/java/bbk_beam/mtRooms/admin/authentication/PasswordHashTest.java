package bbk_beam.mtRooms.admin.authentication;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class PasswordHashTest {
    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void createSalt() {
        String salt = PasswordHash.createSalt();
        for (int i = 0; i < 100; i++) {
            Assert.assertNotEquals(salt, PasswordHash.createSalt());
        }
    }

    @Test
    public void create_and_validate_Hash() throws Exception {
        String salt = PasswordHash.createSalt();
        String hash = PasswordHash.createHash("password", salt);
        Assert.assertTrue(PasswordHash.validateHash("password", salt, hash));
        Assert.assertFalse(PasswordHash.validateHash("passw0rd", salt, hash));
    }

}