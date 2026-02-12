package com.ayoub.lockBox.storage;

import com.ayoub.lockBox.model.Account;
import com.ayoub.lockBox.model.DerivedKey;
import com.ayoub.lockBox.model.EncryptedData;
import com.ayoub.lockBox.model.LockBoxData;
import com.ayoub.lockBox.security.EncryptionService;
import com.ayoub.lockBox.security.KeyDerivationService;
import com.ayoub.lockBox.security.SecureCredentials;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

public class LockBoxStorage {

    private static final String LOCKBOX_FILE = "lockBox.enc";
    private static final Gson gson = new Gson();

    public static boolean lockBoxExists() {
        return new File(LOCKBOX_FILE).exists();
    }

    public static void createLockBox(SecureCredentials credentials, List<Account> accounts) throws Exception {
        // Derive key from password
        DerivedKey derivedKey = KeyDerivationService.deriveKey(credentials.getPassword(), null);

        // Convert accounts to JSON
        String accountsJson = gson.toJson(accounts);

        // Encrypt
        EncryptedData encrypted = EncryptionService.encrypt(accountsJson, derivedKey.key());

        // Create lockBox data
        LockBoxData lockBoxData = new LockBoxData(
                KeyDerivationService.encodeSalt(derivedKey.salt()),
                encrypted.iv(),
                encrypted.ciphertext()
        );

        // Save to file atomically
        String lockBoxJson = gson.toJson(lockBoxData);
        writeAtomic(LOCKBOX_FILE, lockBoxJson);
    }

    public static List<Account> loadLockBox(SecureCredentials credentials) throws Exception {
        // Read lockBox file
        String lockBoxJson = new String(Files.readAllBytes(Paths.get(LOCKBOX_FILE)));
        LockBoxData lockBoxData = gson.fromJson(lockBoxJson, LockBoxData.class);

        // Derive key from password using stored salt
        byte[] salt = KeyDerivationService.decodeSalt(lockBoxData.getSalt());
        DerivedKey derivedKey = KeyDerivationService.deriveKey(credentials.getPassword(), salt);

        // Decrypt
        String accountsJson = EncryptionService.decrypt(
                lockBoxData.getCiphertext(),
                lockBoxData.getIv(),
                derivedKey.key()
        );

        // Parse accounts
        Type listType = new TypeToken<ArrayList<Account>>(){}.getType();
        return gson.fromJson(accountsJson, listType);
    }

    /**
     * Writes data to a file atomically to prevent corruption.
     * Writes to a temporary file first, then atomically moves it to the target location.
     */
    private static void writeAtomic(String targetFile, String content) throws Exception {
        Path targetPath = Paths.get(targetFile);
        Path parentDir = targetPath.getParent();
        
        // If parent directory is null, use current directory
        if (parentDir == null) {
            parentDir = Paths.get(".");
        }
        
        // Create temp file in the same directory as target (required for atomic move on same filesystem)
        Path tempFile = Files.createTempFile(parentDir, ".lockbox-", ".tmp");
        
        try {
            // Write to temp file
            Files.writeString(tempFile, content);
            
            // Atomically move temp file to target location
            Files.move(tempFile, targetPath, 
                StandardCopyOption.REPLACE_EXISTING,
                StandardCopyOption.ATOMIC_MOVE);
        } catch (Exception e) {
            // Clean up temp file if write failed
            try {
                Files.deleteIfExists(tempFile);
            } catch (Exception ignored) {
                // Ignore cleanup errors
            }
            throw e;
        }
    }
}
