package com.ayoub.lockBox.security;

import com.ayoub.lockBox.model.EncryptedData;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Provides AES-256-GCM encryption and decryption services.
 * <p>
 * Implements authenticated encryption with associated data using AES in
 * Galois/Counter Mode (GCM). GCM provides both confidentiality and authenticity,
 * protecting against tampering and ensuring data integrity.
 * <p>
 * <b>Encryption Parameters:</b>
 * <ul>
 *   <li>Algorithm: AES-256-GCM</li>
 *   <li>IV Length: 12 bytes (96 bits) - recommended for GCM</li>
 *   <li>Authentication Tag: 128 bits - ensures data integrity</li>
 *   <li>Padding: None (GCM is a stream cipher mode)</li>
 * </ul>
 * <p>
 * A new random IV is generated for each encryption operation to ensure that
 * encrypting the same plaintext twice produces different ciphertexts.
 *
 * @see KeyDerivationService
 * @see EncryptedData
 *
 * @author Ayoub Elhatab
 * LinkedIn: <a href="https://www.linkedin.com/in/ayoub-elhatab/">Ayoub Elhatab</a>
 */
public class EncryptionService {

    private static final String ALGORITHM = "AES/GCM/NoPadding";
    private static final int GCM_IV_LENGTH = 12;
    private static final int GCM_TAG_LENGTH = 128;

    /**
     * Encrypts plaintext using AES-256-GCM with a randomly generated IV.
     * <p>
     * The IV is generated using {@link SecureRandom} and is returned alongside
     * the ciphertext. Both the ciphertext and IV are Base64-encoded for safe
     * storage and transmission.
     *
     * @param plaintext the plaintext string to encrypt
     * @param key the 256-bit AES encryption key
     * @return EncryptedData containing Base64-encoded ciphertext and IV
     * @throws Exception if encryption fails
     */
    public static EncryptedData encrypt(String plaintext, byte[] key) throws Exception {
        // Generate IV
        byte[] iv = new byte[GCM_IV_LENGTH];
        SecureRandom random = new SecureRandom();
        random.nextBytes(iv);

        // Create cipher
        SecretKey secretKey = new SecretKeySpec(key, "AES");
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, spec);

        // Encrypt
        byte[] ciphertext = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));

        // Encode to Base64
        String ciphertextBase64 = Base64.getEncoder().encodeToString(ciphertext);
        String ivBase64 = Base64.getEncoder().encodeToString(iv);

        return new EncryptedData(ciphertextBase64, ivBase64);
    }

    /**
     * Decrypts ciphertext using AES-256-GCM with the provided IV and key.
     * <p>
     * The GCM authentication tag is verified during decryption. If the ciphertext
     * has been tampered with or the wrong key is used, decryption will fail with
     * an exception.
     *
     * @param ciphertextBase64 the Base64-encoded ciphertext
     * @param ivBase64 the Base64-encoded initialization vector
     * @param key the 256-bit AES decryption key
     * @return the decrypted plaintext string
     * @throws Exception if decryption fails or authentication tag verification fails
     */
    public static String decrypt(String ciphertextBase64, String ivBase64, byte[] key) throws Exception {
        // Decode from Base64
        byte[] ciphertext = Base64.getDecoder().decode(ciphertextBase64);
        byte[] iv = Base64.getDecoder().decode(ivBase64);

        // Create cipher
        SecretKey secretKey = new SecretKeySpec(key, "AES");
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, spec);

        // Decrypt
        byte[] plaintext = cipher.doFinal(ciphertext);

        return new String(plaintext, StandardCharsets.UTF_8);
    }
}