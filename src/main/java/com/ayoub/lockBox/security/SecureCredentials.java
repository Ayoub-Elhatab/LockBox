package com.ayoub.lockBox.security;

import java.util.Arrays;

/**
 * Securely holds password credentials in memory as a char array.
 * <p>
 * Stores passwords as {@code char[]} instead of {@code String} to enable explicit
 * memory wiping. Java Strings are immutable and remain in memory until garbage
 * collected, potentially exposing passwords in memory dumps. This class allows
 * immediate clearing of password data by overwriting the char array with zeros.
 * <p>
 * Implements {@link AutoCloseable} for use with try-with-resources to ensure
 * automatic cleanup.
 * <p>
 * <b>Security Best Practice:</b>
 * Always call {@link #wipe()} or use try-with-resources when done with credentials.
 *
 * @see KeyDerivationService
 *
 * @author Ayoub Elhatab
 * LinkedIn: <a href="https://www.linkedin.com/in/ayoub-elhatab/">Ayoub Elhatab</a>
 */
public class SecureCredentials implements AutoCloseable {
    
    private final char[] password;
    private boolean wiped = false;

    /**
     * Creates SecureCredentials from a String password.
     * <p>
     * Converts the String to a char array. Note: The original String still
     * exists in memory until garbage collected.
     *
     * @param password the password string
     */
    public SecureCredentials(String password) {
        this.password = password.toCharArray();
    }

    /**
     * Creates SecureCredentials from a char array password.
     * <p>
     * Creates a defensive copy of the provided array.
     *
     * @param password the password char array
     */
    public SecureCredentials(char[] password) {
        this.password = Arrays.copyOf(password, password.length);
    }

    /**
     * Gets the password as a char array.
     * <p>
     * Returns the internal array - do not modify it directly.
     *
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
     * Checks if credentials have been wiped.
     *
     * @return true if wiped, false otherwise
     */
    public boolean isWiped() {
        return wiped;
    }

    /**
     * Wipes the password from memory by overwriting with zeros.
     * <p>
     * After wiping, all getter methods will throw {@link IllegalStateException}.
     */
    public void wipe() {
        if (password != null && !wiped) {
            Arrays.fill(password, '\0');
            wiped = true;
        }
    }

    /**
     * AutoCloseable implementation - wipes credentials when closed.
     * <p>
     * Enables try-with-resources usage for automatic cleanup.
     */
    @Override
    public void close() {
        wipe();
    }
}
