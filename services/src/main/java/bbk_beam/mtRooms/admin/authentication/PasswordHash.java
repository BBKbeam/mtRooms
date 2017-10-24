package bbk_beam.mtRooms.admin.authentication;

import bbk_beam.mtRooms.admin.exception.AuthenticationHasherException;
import eadjlib.logger.Logger;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;

public class PasswordHash {
    private static final Logger log = Logger.getLoggerInstance(PasswordHash.class.getName());
    private static final String PBKDF2_ALGO = "PBEWithHmacSHA512AndAES_256";
    private static final int SALT_BYTES = 512;
    private static final int ITERATIONS = 65536;

    /**
     * Creates a random salt
     *
     * @return Random salt
     */
    public static String createSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[SALT_BYTES];
        random.nextBytes(salt);
        return DatatypeConverter.printHexBinary(salt);
    }

    /**
     * Creates a hash of a password and a salt
     *
     * @param password Password
     * @param salt     Salt
     * @return Hashed password + salt
     * @throws AuthenticationHasherException when something goes wrong with the hashing
     */
    public static String createHash(String password, String salt) throws AuthenticationHasherException {
        try {
            PBEKeySpec pbeKeySpec = new PBEKeySpec(password.toCharArray(), DatatypeConverter.parseHexBinary(salt), ITERATIONS, SALT_BYTES);
            SecretKeyFactory skf = SecretKeyFactory.getInstance(PBKDF2_ALGO);
            byte[] hash = skf.generateSecret(pbeKeySpec).getEncoded();
            return DatatypeConverter.printHexBinary(hash);
        } catch (NoSuchAlgorithmException e) {
            log.log_Fatal("PasswordHash algorithm '", PBKDF2_ALGO, "' is not recognised.");
            throw new AuthenticationHasherException("PasswordHash algorithm '" + PBKDF2_ALGO + "' is not recognised.", e);
        } catch (InvalidKeySpecException e) {
            log.log_Fatal("PBE Key specification invalid");
            throw new AuthenticationHasherException("PBE Key specification invalid", e);
        }
    }

    /**
     * Validates a password against a hash and salt from records
     *
     * @param password        Entered password to check against the one on record
     * @param salt            Salt in records
     * @param hashed_password Hash in the records
     * @return Validity of the password
     * @throws AuthenticationHasherException when something goes wrong with the hashing
     */
    public static boolean validateHash(String password, String salt, String hashed_password) throws AuthenticationHasherException {
        try {
            PBEKeySpec pbeKeySpec = new PBEKeySpec(password.toCharArray(), DatatypeConverter.parseHexBinary(salt), ITERATIONS, SALT_BYTES);
            SecretKeyFactory skf = SecretKeyFactory.getInstance(PBKDF2_ALGO);
            byte[] hash = skf.generateSecret(pbeKeySpec).getEncoded();
            return DatatypeConverter.printHexBinary(hash).equals(hashed_password);
        } catch (NoSuchAlgorithmException e) {
            log.log_Fatal("PasswordHash algorithm '", PBKDF2_ALGO, "' is not recognised.");
            throw new AuthenticationHasherException("PasswordHash algorithm '" + PBKDF2_ALGO + "' is not recognised.", e);
        } catch (InvalidKeySpecException e) {
            log.log_Fatal("PBE Key specification invalid");
            throw new AuthenticationHasherException("PBE Key specification invalid", e);
        }
    }
}
