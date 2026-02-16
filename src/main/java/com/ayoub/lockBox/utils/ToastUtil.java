package com.ayoub.lockBox.utils;

import javafx.animation.FadeTransition;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.util.Duration;

public class ToastUtil {

    public static void showToast(Stage stage, String message, double yOffset) {
        // Create toast label
        Label toast = new Label(message);
        toast.setStyle("-fx-background-color: #27AE60; -fx-text-fill: white; -fx-padding: 12px 20px; -fx-background-radius: 8; -fx-font-size: 16px; -fx-font-weight: bold;");

        // Create popup
        Popup popup = new Popup();
        popup.getContent().add(toast);
        popup.setAutoHide(false);

        Scene scene = stage.getScene();

        // Calculate position
        double x = stage.getX() + (scene.getWidth() / 2) - 75;
        double y = stage.getY() + scene.getHeight() - yOffset;

        popup.show(stage, x, y);

        // Fade in
        toast.setOpacity(0);
        FadeTransition fadeIn = new FadeTransition(Duration.millis(300), toast);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);

        // Fade out and hide
        FadeTransition fadeOut = new FadeTransition(Duration.millis(300), toast);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);
        fadeOut.setDelay(Duration.seconds(2));
        fadeOut.setOnFinished(e -> popup.hide());

        fadeIn.play();
        fadeOut.play();
    }


}
