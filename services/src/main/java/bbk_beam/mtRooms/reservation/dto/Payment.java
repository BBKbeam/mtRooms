package bbk_beam.mtRooms.reservation.dto;

import bbk_beam.mtRooms.db.TimestampConverter;
import bbk_beam.mtRooms.reservation.exception.FailedPaymentHashing;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.io.Serializable;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Date;
import java.util.Objects;

public class Payment implements Serializable {
    private Integer id;
    private Double amount;
    private Date timestamp;
    private PaymentMethod paymentMethod;
    private String hashID;
    private String note;

    /**
     * Unique Hash ID generator for payments
     *
     * @param reservation   Reservation DTO linked to the payment
     * @param amount        Payment amount
     * @param timestamp     Payment timestamp
     * @param paymentMethod Payment method DTO
     * @return HashID generated
     * @throws FailedPaymentHashing when hashing encountered an internal exception
     */
    static String generateHashID(Reservation reservation, Double amount, Date timestamp, PaymentMethod paymentMethod) throws FailedPaymentHashing {
        final int SALT_BYTES = 512;
        final int ITERATIONS = 65536;
        final String PBKDF2_ALGO = "PBEWithHmacSHA512AndAES_256";
        String concatenate = amount + TimestampConverter.getUTCTimestampString(timestamp) + paymentMethod.id() + reservation.id();
        SecureRandom random = new SecureRandom();
        byte[] byte_set = new byte[SALT_BYTES];
        random.nextBytes(byte_set);
        String salt = DatatypeConverter.printHexBinary(byte_set);
        try {
            PBEKeySpec pbeKeySpec = new PBEKeySpec(concatenate.toCharArray(), DatatypeConverter.parseHexBinary(salt), ITERATIONS, SALT_BYTES);
            SecretKeyFactory skf = SecretKeyFactory.getInstance(PBKDF2_ALGO);
            byte[] hash = skf.generateSecret(pbeKeySpec).getEncoded();
            return DatatypeConverter.printHexBinary(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new FailedPaymentHashing("Hard-coded algorithm \"" + PBKDF2_ALGO + "\" is not valid.", e);
        } catch (InvalidKeySpecException e) {
            throw new FailedPaymentHashing("Key specifications were found to be invalid. (Key: '" + concatenate + "', Salt: '" + salt + "')", e);
        }
    }

    /**
     * Constructor
     *
     * @param reservation   Reservation linked to the payment
     * @param amount        Amount
     * @param timestamp     Transaction timestamp
     * @param note          Note
     * @param paymentMethod Method used for payment
     * @throws FailedPaymentHashing when failure occurs during HashID generation
     */
    public Payment(Reservation reservation, Double amount, Date timestamp, String note, PaymentMethod paymentMethod) throws FailedPaymentHashing {
        this.id = -1;
        this.amount = amount;
        this.timestamp = timestamp;
        this.note = note;
        this.paymentMethod = paymentMethod;
        this.hashID = Payment.generateHashID(reservation, amount, timestamp, paymentMethod);
    }

    /**
     * Constructor
     *  @param id            ID
     * @param hashID        HashID for the payment
     * @param amount        Amount
     * @param timestamp     Transaction timestamp
     * @param note          Note
     * @param paymentMethod Method used for payment
     */
    public Payment(Integer id, String hashID, Double amount, Date timestamp, String note, PaymentMethod paymentMethod) {
        this.id = id;
        this.hashID = hashID;
        this.amount = amount;
        this.timestamp = timestamp;
        this.note = note;
        this.paymentMethod = paymentMethod;
    }

    /**
     * Gets the payment's ID
     *
     * @return ID
     */
    public Integer id() {
        return this.id;
    }

    /**
     * Gets the payment's HashID
     *
     * @return HashID
     */
    public String hashID() {
        return this.hashID;
    }

    /**
     * Gets the payment's amount
     *
     * @return Amount
     */
    public Double amount() {
        return this.amount;
    }

    /**
     * Gets the payment's transaction timestamp
     *
     * @return Timestamp
     */
    public Date timestamp() {
        return this.timestamp;
    }

    /**
     * Gets the payment note
     *
     * @return Note
     */
    public String note() {
        return this.note;
    }

    /**
     * Sets the note for the Payment
     *
     * @param note Note
     */
    public void setNote(String note) {
        this.note = note;
    }

    /**
     * Gets the payment's method used
     *
     * @return Payment method
     */
    public PaymentMethod paymentMethod() {
        return this.paymentMethod;
    }

    @Override
    public String toString() {
        return "[" + id + "]={ "
                + "hashID: " + hashID + ", "
                + "amount: " + amount + ", "
                + "timestamp: " + timestamp + ", "
                + "paymentMethod: " + paymentMethod + ", "
                + "note: " + note + " }";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Payment payment = (Payment) o;
        return Objects.equals(id, payment.id) &&
                Objects.equals(amount, payment.amount) &&
                Objects.equals(timestamp, payment.timestamp) &&
                Objects.equals(paymentMethod, payment.paymentMethod) &&
                Objects.equals(hashID, payment.hashID) &&
                Objects.equals(note, payment.note);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, amount, timestamp, paymentMethod, hashID, note);
    }
}
