package com.ayoub.lockBox.service;

import com.ayoub.lockBox.model.Account;
import com.ayoub.lockBox.security.SecureCredentials;
import com.ayoub.lockBox.storage.LockBoxStorage;
import java.util.List;

public class AccountService {

    public static boolean isFileExist(){
        return LockBoxStorage.lockBoxExists();
    }
    public static void save(SecureCredentials credentials, List<Account> accounts) throws Exception {
        LockBoxStorage.createLockBox(credentials, accounts);
    }

    public static List<Account> load(SecureCredentials credentials) throws Exception {
        return LockBoxStorage.loadLockBox(credentials);
    }
}
