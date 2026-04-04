package com.ayoub.lockBox.utils;

import javafx.animation.FadeTransition;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * Utility class for displaying toast notifications.
 * <p>
 * Shows temporary non-blocking notifications at the bottom-center of the window
 * with fade-in and fade-out animations. Toasts automatically dismiss after 2 seconds.
 * <p>
 * <b>Animation Sequence:</b>
 * <ol>
 *   <li>Fade in over 300ms</li>
 *   <li>Display for 2 seconds</li>
 *   <li>Fade out over 300ms</li>
 *   <li>Auto-hide and cleanup</li>
 * </ol>
 * <p>
 * Used for success feedback (password copied, account saved, etc.) with a
 * green background and white text.
 *
 * @author Ayoub Elhatab
 * LinkedIn: <a href="https://www.linkedin.com/in/ayoub-elhatab/">Ayoub Elhatab</a>
 */
public class ToastUtil {

    /**
     * Displays a toast notification at the bottom-center of the stage.
     * <p>
     * Creates a styled label in a popup window, positions it based on the
     * stage dimensions and yOffset, and animates it with fade transitions.
     * The toast is non-blocking and auto-dismisses.
     *
     * @param stage the parent stage to display the toast on
     * @param message the notification message to display
     * @param yOffset the vertical offset from the bottom of the stage in pixels
     */
    public static void showToast(Stage stage, String message, double yOffset) {
        Label toast = new Label(message);
        toast.setStyle("-fx-background-color: #27AE60; -fx-text-fill: white; -fx-padding: 12px 20px; -fx-background-radius: 8; -fx-font-size: 16px; -fx-font-weight: bold;");

        Popup popup = new Popup();
        popup.getContent().add(toast);
        popup.setAutoHide(false);

        Scene scene = stage.getScene();

        double x = stage.getX() + (scene.getWidth() / 2) - 75;
        double y = stage.getY() + scene.getHeight() - yOffset;

        popup.show(stage, x, y);

        toast.setOpacity(0);
        FadeTransition fadeIn = new FadeTransition(Duration.millis(300), toast);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);

        FadeTransition fadeOut = new FadeTransition(Duration.millis(300), toast);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);
        fadeOut.setDelay(Duration.seconds(2));
        fadeOut.setOnFinished(e -> popup.hide());

        fadeIn.play();
        fadeOut.play();
    }


}
