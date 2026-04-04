package com.ayoub.lockBox.service;

import com.ayoub.lockBox.model.Account;
import com.ayoub.lockBox.security.SecureCredentials;
import com.ayoub.lockBox.storage.LockBoxStorage;
import java.util.List;

/**
 * Service layer facade for account storage operations.
 * <p>
 * Provides a simplified API for checking lockbox existence, saving accounts,
 * and loading accounts. Delegates all storage operations to {@link LockBoxStorage}
 * while handling {@link SecureCredentials} and account lists at a higher level.
 * <p>
 * This service acts as the single point of entry for account persistence,
 * abstracting the underlying encryption and file I/O details.
 *
 * @see LockBoxStorage
 * @see Account
 * @see SecureCredentials
 *
 * @author Ayoub Elhatab
 * LinkedIn: <a href="https://www.linkedin.com/in/ayoub-elhatab/">Ayoub Elhatab</a>
 */
public class AccountService {

    /**
     * Checks if the encrypted lockBox file exists.
     * <p>
     * Used during login initialization to determine if this is the first launch.
     *
     * @return true if lockBox.enc exists, false otherwise
     */
    public static boolean isFileExist(){
        return LockBoxStorage.lockBoxExists();
    }

    /**
     * Saves the account list to encrypted storage.
     * <p>
     * Encrypts the entire account list using AES-256-GCM and writes it
     * atomically to lockBox.enc. Overwrites any existing lockBox file.
     *
     * @param credentials the master password credentials for encryption
     * @param accounts the list of accounts to save
     * @throws Exception if encryption or file write fails
     */
    public static void save(SecureCredentials credentials, List<Account> accounts) throws Exception {
        LockBoxStorage.createLockBox(credentials, accounts);
    }

    /**
     * Loads all accounts from encrypted storage.
     * <p>
     * Decrypts the lockBox.enc file using the provided credentials and
     * returns the deserialized account list.
     *
     * @param credentials the master password credentials for decryption
     * @return list of decrypted accounts
     * @throws Exception if decryption fails (wrong password or corrupted file)
     */
    public static List<Account> load(SecureCredentials credentials) throws Exception {
        return LockBoxStorage.loadLockBox(credentials);
    }
}
