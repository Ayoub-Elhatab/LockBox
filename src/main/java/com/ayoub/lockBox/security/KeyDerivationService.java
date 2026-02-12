package com.ayoub.lockBox.security;

import com.ayoub.lockBox.model.DerivedKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.SecureRandom;
import java.util.Base64;

public class KeyDerivationService {

    private static final int ITERATIONS = 100000;
    private static final int KEY_LENGTH = 256;
    private static final int SALT_LENGTH = 32;

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

    public static byte[] generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[SALT_LENGTH];
        random.nextBytes(salt);
        return salt;
    }

    public static String encodeSalt(byte[] salt) {
        return Base64.getEncoder().encodeToString(salt);
    }

    public static byte[] decodeSalt(String saltString) {
        return Base64.getDecoder().decode(saltString);
    }
}