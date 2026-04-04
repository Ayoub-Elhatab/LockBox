package com.ayoub.lockBox.utils;

import javafx.scene.control.Alert;
import javafx.scene.control.DialogPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import java.util.Objects;

/**
 * Utility class for displaying styled error alert dialogs.
 * <p>
 * Provides a consistent error alert UI with custom warning icon, window icon,
 * and CSS styling. All alerts are modal and block user interaction until dismissed.
 * <p>
 * Features:
 * <ul>
 *   <li>Custom warning icon displayed in the alert content</li>
 *   <li>Custom security icon for the window title bar</li>
 *   <li>Consistent CSS styling from style.css</li>
 *   <li>Modal dialog blocking interaction until dismissed</li>
 * </ul>
 *
 * @author Ayoub Elhatab
 * LinkedIn: <a href="https://www.linkedin.com/in/ayoub-elhatab/">Ayoub Elhatab</a>
 */
public class AlertUtil {

    /**
     * Displays a styled error alert dialog with the given title and message.
     * <p>
     * Shows a modal alert with a warning icon (48x48), custom window icon,
     * and CSS styling. The alert blocks until the user clicks OK.
     *
     * @param title the alert title (currently unused, fixed to "Alert")
     * @param message the error message to display
     */
    public static void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Alert");
        alert.setHeaderText(null);
        alert.setContentText(message);

        try {
            ImageView icon = new ImageView(new Image(Objects.requireNonNull(AlertUtil.class.getResourceAsStream("/icons/warning.png"))));
            icon.setFitWidth(48);
            icon.setFitHeight(48);
            icon.setPreserveRatio(true);
            alert.setGraphic(icon);
        } catch (Exception e) {
            System.err.println("Failed to load warning icon");
        }

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
