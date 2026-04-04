package com.ayoub.lockBox.utils;

import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;

/**
 * Utility class for clipboard operations.
 * <p>
 * Provides simple text copying to the system clipboard. Used throughout the
 * application for copying usernames and passwords to the clipboard with a
 * single method call.
 * <p>
 * Uses JavaFX's {@link javafx.scene.input.Clipboard} API to interact with
 * the system clipboard across different operating systems.
 *
 * @author Ayoub Elhatab
 * LinkedIn: <a href="https://www.linkedin.com/in/ayoub-elhatab/">Ayoub Elhatab</a>
 */
public class ClipboardUtil {

    /**
     * Copies the specified text to the system clipboard.
     * <p>
     * Replaces any existing clipboard content with the provided text.
     *
     * @param text the text to copy to clipboard
     */
    public static void copyToClipboard(String text) {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();
        content.putString(text);
        clipboard.setContent(content);
    }
}
