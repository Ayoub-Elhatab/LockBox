package com.ayoub.lockBox.security;


/**
 * Holds the currently active session's credentials and ensures they're
 * wiped from memory on logout or app shutdown .
 *
 * @author Ayoub Elhatab
 * LinkedIn: <a href="https://www.linkedin.com/in/ayoub-elhatab/">Ayoub Elhatab</a>
 */
public class SessionManager {

    private static SecureCredentials active;

    private SessionManager() {}

    /**
     * Sets the active session's credentials, wiping any previous
     * session's credentials first.
     *
     * @param credentials the newly authenticated session's credentials
     */
    public static void setActive(SecureCredentials credentials) {
        wipeActive();
        active = credentials;
    }

    /**
     * Returns the currently active session's credentials.
     *
     * @return the active credentials, or null if no session is active
     */
    public static SecureCredentials getActive() {
        return active;
    }

    /**
     * Wipes and clears the active session's credentials, if any.
     */
    public static void wipeActive() {
        if (active != null) {
            active.wipe();
            active = null;
        }
    }
}
