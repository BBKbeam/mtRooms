package bbk_beam.mtRooms.admin.authentication;

import java.math.BigInteger;
import java.security.SecureRandom;

public class SessionIdGenerator {
    private SecureRandom secure_random = new SecureRandom();

    /**
     * Generates a new session ID
     *
     * @return New session ID
     */
    public String nextSessionId() {
        return new BigInteger(130, secure_random).toString(32);
    }
}
