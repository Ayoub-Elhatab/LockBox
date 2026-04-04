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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles encrypted file I/O for the password vault.
 * <p>
 * Manages the persistence of account data to the encrypted lockBox.enc file using
 * AES-256-GCM encryption with PBKDF2 key derivation. Provides atomic file writes
 * to prevent data corruption in case of crashes or power loss.
 * <p>
 * <b>File Structure (lockBox.enc):</b>
 * <pre>
 * {
 *   "salt": "Base64-encoded PBKDF2 salt",
 *   "iv": "Base64-encoded AES-GCM initialization vector",
 *   "ciphertext": "Base64-encoded encrypted account JSON"
 * }
 * </pre>
 * <p>
 * The salt and IV are stored alongside the ciphertext because they are required
 * for decryption but are not sensitive (they don't need to be kept secret).
 *
 * @see EncryptionService
 * @see KeyDerivationService
 * @see LockBoxData
 *
 * @author Ayoub Elhatab
 * LinkedIn: <a href="https://www.linkedin.com/in/ayoub-elhatab/">Ayoub Elhatab</a>
 */
public class LockBoxStorage {

    private static final String LOCKBOX_FILE = "lockBox.enc";
    private static final Gson gson = new Gson();

    /**
     * Checks if the lockBox.enc file exists in the current directory.
     *
     * @return true if lockBox.enc exists, false otherwise
     */
    public static boolean lockBoxExists() {
        return new File(LOCKBOX_FILE).exists();
    }

    /**
     * Creates or overwrites the encrypted lockbox file with the provided accounts.
     * <p>
     * Workflow:
     * <ol>
     *   <li>Derive AES key from password using PBKDF2 (generates new salt)</li>
     *   <li>Serialize accounts to JSON</li>
     *   <li>Encrypt JSON with AES-256-GCM (generates new IV)</li>
     *   <li>Create LockBoxData with salt, IV, and ciphertext</li>
     *   <li>Write atomically to lockBox.enc</li>
     * </ol>
     *
     * @param credentials the master password credentials
     * @param accounts the list of accounts to encrypt and save
     * @throws Exception if key derivation, encryption, or file write fails
     */
    public static void createLockBox(SecureCredentials credentials, List<Account> accounts) throws Exception {
        DerivedKey derivedKey = KeyDerivationService.deriveKey(credentials.getPassword(), null);

        String accountsJson = gson.toJson(accounts);

        EncryptedData encrypted = EncryptionService.encrypt(accountsJson, derivedKey.key());

        LockBoxData lockBoxData = new LockBoxData(
                KeyDerivationService.encodeSalt(derivedKey.salt()),
                encrypted.iv(),
                encrypted.ciphertext()
        );

        String lockBoxJson = gson.toJson(lockBoxData);
        writeAtomic(LOCKBOX_FILE, lockBoxJson);
    }

    /**
     * Loads and decrypts all accounts from the lockbox file.
     * <p>
     * Workflow:
     * <ol>
     *   <li>Read lockBox.enc and parse JSON</li>
     *   <li>Derive AES key from password using stored salt</li>
     *   <li>Decrypt ciphertext with derived key and stored IV</li>
     *   <li>Deserialize JSON to account list</li>
     * </ol>
     *
     * @param credentials the master password credentials
     * @return list of decrypted accounts
     * @throws Exception if file read, key derivation, or decryption fails
     */
    public static List<Account> loadLockBox(SecureCredentials credentials) throws Exception {
        String lockBoxJson = new String(Files.readAllBytes(Paths.get(LOCKBOX_FILE)));
        LockBoxData lockBoxData = gson.fromJson(lockBoxJson, LockBoxData.class);

        byte[] salt = KeyDerivationService.decodeSalt(lockBoxData.salt());
        DerivedKey derivedKey = KeyDerivationService.deriveKey(credentials.getPassword(), salt);

        String accountsJson = EncryptionService.decrypt(
                lockBoxData.ciphertext(),
                lockBoxData.iv(),
                derivedKey.key()
        );

        Type listType = new TypeToken<ArrayList<Account>>(){}.getType();
        return gson.fromJson(accountsJson, listType);
    }

    /**
     * Writes data to a file atomically to prevent corruption.
     * <p>
     * Uses the write-to-temp-then-atomic-move pattern to ensure that the file
     * is never left in a partially written state. If the write fails, the original
     * file remains intact.
     * <p>
     * <b>Atomic Write Process:</b>
     * <ol>
     *   <li>Create temporary file in same directory</li>
     *   <li>Write content to temp file</li>
     *   <li>Atomically move temp file to target (ATOMIC_MOVE + REPLACE_EXISTING)</li>
     *   <li>Clean up temp file on failure</li>
     * </ol>
     *
     * @param targetFile the target file path
     * @param content the content to write
     * @throws Exception if write or atomic move fails
     */
    private static void writeAtomic(String targetFile, String content) throws Exception {
        Path targetPath = Paths.get(targetFile);
        Path parentDir = targetPath.getParent();

        if (parentDir == null) {
            parentDir = Paths.get(".");
        }
        
        Path tempFile = Files.createTempFile(parentDir, ".lockbox-", ".tmp");
        
        try {
            Files.writeString(tempFile, content);

            Files.move(tempFile, targetPath,
                StandardCopyOption.REPLACE_EXISTING,
                StandardCopyOption.ATOMIC_MOVE);
        } catch (Exception e) {
            try {
                Files.deleteIfExists(tempFile);
            } catch (Exception ignored) {
                // Ignore cleanup errors
            }
            throw e;
        }
    }
}
