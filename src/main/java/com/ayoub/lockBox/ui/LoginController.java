package com.ayoub.lockBox.ui;

import com.ayoub.lockBox.storage.LockBoxStorage;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.util.ArrayList;
import java.util.Objects;

public class LoginController {

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private Label errorLabel;

    @FXML
    private VBox firstLaunchBox;

    private boolean isFirstLaunch = false;

    @FXML
    private void initialize() {
        // Check if LockBox file exists
        if (!LockBoxStorage.lockBoxExists()) {
            // First launch - show the info box and confirm field
            isFirstLaunch = true;
            firstLaunchBox.setVisible(true);
            firstLaunchBox.setManaged(true);
            confirmPasswordField.setVisible(true);
            confirmPasswordField.setManaged(true);
        }
    }

    @FXML
    private void handleUnlock() {
        String password = passwordField.getText();

        // Validation
        if (password.isEmpty()) {
            showError("Please enter a password");
            return;
        }

        if (isFirstLaunch) {
            handleFirstLaunch(password);
        } else {
            handleLogin(password);
        }
    }

    private void handleFirstLaunch(String password) {
        String confirmPassword = confirmPasswordField.getText();

        // Validate passwords match
        if (confirmPassword.isEmpty()) {
            showError("Please confirm your password");
            return;
        }

        if (!password.equals(confirmPassword)) {
            showError("Passwords do not match");
            return;
        }

        // Validate password strength (optional)
        if (password.length() < 6) {
            showError("Password must be at least 6 characters");
            return;
        }

        try {
            // Create new LockBox with empty accounts list
            LockBoxStorage.createLockBox(password, new ArrayList<>());

            // Go to dashboard
            loadDashboard(password);

        } catch (Exception e) {
            showError("Failed to create LockBox: " + e.getMessage());
        }
    }

    private void handleLogin(String password) {
        try {
            // Try to load vault with this password
            LockBoxStorage.loadLockBox(password);

            // Success - go to dashboard
            loadDashboard(password);

        } catch (Exception e) {
            // Wrong password or corrupted LockBox
            showError("Incorrect password. Try again.");
            passwordField.clear();
        }
    }

    private void loadDashboard(String masterPassword) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/dashboard.fxml"));
            Parent dashboard = loader.load();

            // Pass master password to dashboard controller
            DashboardController controller = loader.getController();
            controller.setMasterPassword(masterPassword);
            controller.loadAccounts();

            // Get current stage and switch scene
            Stage stage = (Stage) passwordField.getScene().getWindow();
            Scene scene = new Scene(dashboard, 950, 800);
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/css/style.css")).toExternalForm());

            stage.setScene(scene);
            stage.setResizable(true);
            stage.centerOnScreen();
            stage.setMinWidth(720);
            stage.setMinHeight(800);

        } catch (Exception e) {
            showError("Failed to load dashboard: " + e.getMessage());
        }
    }

    private void showError(String message) {
        errorLabel.setText("✕  " + message);
        errorLabel.setVisible(true);
        errorLabel.setManaged(true);
    }
}