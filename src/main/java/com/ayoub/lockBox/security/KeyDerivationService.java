package com.ayoub.lockBox.security;

import com.ayoub.lockBox.model.DerivedKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Provides password-based key derivation using PBKDF2-HMAC-SHA256.
 * <p>
 * Derives cryptographically secure encryption keys from user passwords using
 * the Password-Based Key Derivation Function 2 (PBKDF2) with HMAC-SHA256.
 * This protects against brute-force and rainbow table attacks through salting
 * and computational cost (iterations).
 * <p>
 * <b>Security Parameters:</b>
 * <ul>
 *   <li>Algorithm: PBKDF2WithHmacSHA256</li>
 *   <li>Iterations: 100,000 - slows down brute-force attacks</li>
 *   <li>Key Length: 256 bits - for AES-256</li>
 *   <li>Salt Length: 32 bytes (256 bits) - ensures unique keys per password</li>
 * </ul>
 * <p>
 * The salt is randomly generated for each vault and stored alongside the encrypted
 * data. The same salt must be used for both key derivation and decryption.
 *
 * @see EncryptionService
 * @see DerivedKey
 *
 * @author Ayoub Elhatab
 * LinkedIn: <a href="https://www.linkedin.com/in/ayoub-elhatab/">Ayoub Elhatab</a>
 */
public class KeyDerivationService {

    private static final int ITERATIONS = 100000;
    private static final int KEY_LENGTH = 256;
    private static final int SALT_LENGTH = 32;

    /**
     * Derives a 256-bit AES key from a password using PBKDF2-HMAC-SHA256.
     * <p>
     * If no salt is provided, generates a new random salt. The password is
     * wiped from the PBEKeySpec after key derivation to minimize exposure in memory.
     *
     * @param password the password as char array (more secure than String)
     * @param salt the salt bytes, or null to generate a new random salt
     * @return DerivedKey containing the derived key and salt
     * @throws Exception if key derivation fails
     */
    public static DerivedKey deriveKey(char[] password, byte[] salt) throws Exception {
        if (salt == null) {
            salt = generateSalt();
        }

        PBEKeySpec spec = new PBEKeySpec(password, salt, ITERATIONS, KEY_LENGTH);
        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            byte[] key = factory.generateSecret(spec).getEncoded();
            return new DerivedKey(key, salt);
        } finally {
            // Wipe the spec to clear password from memory
            spec.clearPassword();
        }
    }

    /**
     * Generates a cryptographically secure random salt.
     * <p>
     * Uses {@link SecureRandom} to generate 32 bytes of random data for use
     * as a PBKDF2 salt.
     *
     * @return 32 bytes of random salt
     */
    public static byte[] generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[SALT_LENGTH];
        random.nextBytes(salt);
        return salt;
    }

    /**
     * Encodes a salt byte array to Base64 string for storage.
     *
     * @param salt the salt bytes to encode
     * @return Base64-encoded salt string
     */
    public static String encodeSalt(byte[] salt) {
        return Base64.getEncoder().encodeToString(salt);
    }

    /**
     * Decodes a Base64-encoded salt string back to bytes.
     *
     * @param saltString the Base64-encoded salt
     * @return the decoded salt bytes
     */
    public static byte[] decodeSalt(String saltString) {
        return Base64.getDecoder().decode(saltString);
    }
}