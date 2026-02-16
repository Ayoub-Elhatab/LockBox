package com.ayoub.lockBox.utils;

import javafx.scene.control.Alert;
import javafx.scene.control.DialogPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import java.util.Objects;

public class AlertUtil {

    public static void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Alert");
        alert.setHeaderText(null);
        alert.setContentText(message);

        // Set custom icon in the middle-left
        try {
            ImageView icon = new ImageView(new Image(Objects.requireNonNull(AlertUtil.class.getResourceAsStream("/icons/warning.png"))));
            icon.setFitWidth(48);
            icon.setFitHeight(48);
            icon.setPreserveRatio(true);
            alert.setGraphic(icon);
        } catch (Exception e) {
            System.err.println("Failed to load warning icon");
        }

        // Set custom window icon
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        try {
            stage.getIcons().add(new Image(Objects.requireNonNull(AlertUtil.class.getResourceAsStream("/icons/security.png"))));
        } catch (Exception e) {
            System.err.println("Failed to load window icon");
        }

        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(Objects.requireNonNull(AlertUtil.class.getResource("/css/style.css")).toExternalForm());

        alert.showAndWait();
    }
}
