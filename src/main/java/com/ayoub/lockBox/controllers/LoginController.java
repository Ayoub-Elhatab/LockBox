package com.ayoub.lockBox.controllers;

import com.ayoub.lockBox.security.SecureCredentials;
import com.ayoub.lockBox.security.SessionManager;
import com.ayoub.lockBox.service.AccountService;
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

/**
 * Controller for the login screen and first-time LockBox setup.
 * <p>
 * Handles two distinct modes:
 * <ul>
 *   <li><b>First Launch:</b> Creates a new encrypted LockBox with password confirmation</li>
 *   <li><b>Login:</b> Authenticates user and decrypts existing LockBox</li>
 * </ul>
 * <p>
 * On first launch, the controller detects the absence of lockBox.enc and displays
 * additional UI for password confirmation and LockBox creation. On subsequent launches,
 * it validates the password against the encrypted LockBox and navigates to the dashboard
 * upon successful authentication.
 *
 * @see DashboardController
 * @see AccountService
 * @see SecureCredentials
 *
 * @author Ayoub Elhatab
 * LinkedIn: <a href="https://www.linkedin.com/in/ayoub-elhatab/">Ayoub Elhatab</a>
 */
public class LoginController {

    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Label errorLabel;
    @FXML private VBox firstLaunchBox;

    private boolean isFirstLaunch = false;

    /**
     * Initializes the login screen by checking if lockBox.enc exists.
     * <p>
     * If the LockBox file doesn't exist (first launch), shows the password
     * confirmation field and first-launch info box. Called automatically
     * by JavaFX after FXML loading.
     */
    @FXML
    private void initialize() {
        if (!AccountService.isFileExist()) {
            isFirstLaunch = true;
            firstLaunchBox.setVisible(true);
            firstLaunchBox.setManaged(true);
            confirmPasswordField.setVisible(true);
            confirmPasswordField.setManaged(true);
        }
    }

    /**
     * Handles the unlock button click.
     * <p>
     * Validates password input and delegates to either {@link #handleFirstLaunch(String)}
     * or {@link #handleLogin(String)} based on the launch mode.
     */
    @FXML
    private void handleUnlock() {
        String password = passwordField.getText();

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

    /**
     * Handles first-time lockbox creation with password confirmation.
     * <p>
     * Workflow:
     * <ol>
     *   <li>Validates password confirmation matches</li>
     *   <li>Validates password length (minimum 6 characters)</li>
     *   <li>Creates SecureCredentials from password</li>
     *   <li>Initializes empty encrypted vault (lockBox.enc)</li>
     *   <li>Navigates to dashboard</li>
     * </ol>
     * Wipes credentials from memory on failure.
     *
     * @param password the master password entered by the user
     */
    private void handleFirstLaunch(String password) {
        String confirmPassword = confirmPasswordField.getText();

        if (confirmPassword.isEmpty()) {
            showError("Please confirm your password");
            return;
        }

        if (!password.equals(confirmPassword)) {
            showError("Passwords do not match");
            return;
        }

        if (password.length() < 6) {
            showError("Password must be at least 6 characters");
            return;
        }

        try {
            SecureCredentials credentials = new SecureCredentials(password);
            SessionManager.setActive(credentials);
            AccountService.save(credentials, new ArrayList<>());
            loadDashboard(credentials);

        } catch (Exception e) {
            showError("Failed to create LockBox: " + e.getMessage());
            SessionManager.wipeActive();
        }
    }

    /**
     * Handles login authentication for existing lockbox.
     * <p>
     * Workflow:
     * <ol>
     *   <li>Creates SecureCredentials from password</li>
     *   <li>Attempts to decrypt lockbox with provided password</li>
     *   <li>If successful, navigates to dashboard</li>
     *   <li>If failed, shows error and wipes credentials</li>
     * </ol>
     * Decryption failure indicates incorrect password or corrupted lockbox file.
     *
     * @param password the master password entered by the user
     */
    private void handleLogin(String password) {
        try {
            SecureCredentials credentials = new SecureCredentials(password);
            SessionManager.setActive(credentials);
            AccountService.load(credentials);
            loadDashboard(credentials);

        } catch (Exception e) {
            showError("Incorrect password. Try again.");
            passwordField.clear();
            SessionManager.wipeActive();
        }
    }

    /**
     * Loads the dashboard scene and passes credentials to the controller.
     * <p>
     * Initializes the dashboard with the authenticated credentials, loads
     * all accounts, and transitions from the login scene to the dashboard scene.
     *
     * @param credentials the authenticated user credentials
     */
    private void loadDashboard(SecureCredentials credentials) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/dashboard.fxml"));
            Parent dashboard = loader.load();

            DashboardController controller = loader.getController();
            controller.setCredentials(credentials);
            controller.loadAccounts();

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

    /**
     * Displays an error message in the UI.
     * <p>
     * Shows the error label with the provided message prefixed by a cross symbol (✕).
     *
     * @param message the error message to display
     */
    private void showError(String message) {
        errorLabel.setText("✕  " + message);
        errorLabel.setVisible(true);
        errorLabel.setManaged(true);
    }
}