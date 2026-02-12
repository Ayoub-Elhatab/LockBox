package com.ayoub.lockBox.security;

import java.util.Arrays;

/**
 * Securely holds password credentials in memory as char array.
 * Allows wiping of sensitive data when no longer needed.
 */
public class SecureCredentials implements AutoCloseable {
    
    private final char[] password;
    private boolean wiped = false;

    public SecureCredentials(String password) {
        this.password = password.toCharArray();
    }

    public SecureCredentials(char[] password) {
        this.password = Arrays.copyOf(password, password.length);
    }

    /**
     * Get the password as char array.
     * @return char array containing password
     * @throws IllegalStateException if credentials have been wiped
     */
    public char[] getPassword() {
        if (wiped) {
            throw new IllegalStateException("Credentials have been wiped");
        }
        return password;
    }

    /**
     * Get the password as String (use sparingly, only when required by APIs).
     * @return String containing password
     * @throws IllegalStateException if credentials have been wiped
     */
    public String getPasswordAsString() {
        if (wiped) {
            throw new IllegalStateException("Credentials have been wiped");
        }
        return new String(password);
    }

    /**
     * Check if credentials have been wiped.
     */
    public boolean isWiped() {
        return wiped;
    }

    /**
     * Wipe the password from memory by overwriting with zeros.
     */
    public void wipe() {
        if (password != null && !wiped) {
            Arrays.fill(password, '\0');
            wiped = true;
        }
    }

    /**
     * AutoCloseable implementation - wipes credentials when closed.
     */
    @Override
    public void close() {
        wipe();
    }

    @Override
    protected void finalize() throws Throwable {
        try {
            wipe();
        } finally {
            super.finalize();
        }
    }
}
