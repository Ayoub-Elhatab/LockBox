package com.ayoub.lockBox.storage;

import com.ayoub.lockBox.model.Account;
import com.ayoub.lockBox.model.DerivedKey;
import com.ayoub.lockBox.model.EncryptedData;
import com.ayoub.lockBox.model.LockBoxData;
import com.ayoub.lockBox.security.EncryptionService;
import com.ayoub.lockBox.security.KeyDerivationService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class LockBoxStorage {

    private static final String LOCKBOX_FILE = "lockBox.enc";
    private static final Gson gson = new Gson();

    public static boolean lockBoxExists() {
        return new File(LOCKBOX_FILE).exists();
    }

    public static void createLockBox(String masterPassword, List<Account> accounts) throws Exception {
        // Derive key from password
        DerivedKey derivedKey = KeyDerivationService.deriveKey(masterPassword, null);

        // Convert accounts to JSON
        String accountsJson = gson.toJson(accounts);

        // Encrypt
        EncryptedData encrypted = EncryptionService.encrypt(accountsJson, derivedKey.getKey());

        // Create lockBox data
        LockBoxData lockBoxData = new LockBoxData(
                KeyDerivationService.encodeSalt(derivedKey.getSalt()),
                encrypted.getIv(),
                encrypted.getCiphertext()
        );

        // Save to file
        String lockBoxJson = gson.toJson(lockBoxData);
        Files.write(Paths.get(LOCKBOX_FILE), lockBoxJson.getBytes());
    }

    public static List<Account> loadLockBox(String masterPassword) throws Exception {
        // Read lockBox file
        String lockBoxJson = new String(Files.readAllBytes(Paths.get(LOCKBOX_FILE)));
        LockBoxData lockBoxData = gson.fromJson(lockBoxJson, LockBoxData.class);

        // Derive key from password using stored salt
        byte[] salt = KeyDerivationService.decodeSalt(lockBoxData.getSalt());
        DerivedKey derivedKey = KeyDerivationService.deriveKey(masterPassword, salt);

        // Decrypt
        String accountsJson = EncryptionService.decrypt(
                lockBoxData.getCiphertext(),
                lockBoxData.getIv(),
                derivedKey.getKey()
        );

        // Parse accounts
        Type listType = new TypeToken<ArrayList<Account>>(){}.getType();
        return gson.fromJson(accountsJson, listType);
    }

    public static void saveAccounts(String masterPassword, List<Account> accounts) throws Exception {
        createLockBox(masterPassword, accounts);
    }
}