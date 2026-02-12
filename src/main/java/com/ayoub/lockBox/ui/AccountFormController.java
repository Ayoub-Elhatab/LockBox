package com.ayoub.lockBox.ui;

import com.ayoub.lockBox.model.Account;
import com.ayoub.lockBox.storage.LockBoxStorage;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import lombok.Setter;
import java.util.List;
import java.util.Objects;

public class AccountFormController {

    @FXML
    private Label formTitle;

    @FXML
    private TextField labelField;

    @FXML
    private ComboBox<String> categoryComboBox;

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField passwordFieldVisible;

    @FXML
    private Button togglePasswordBtn;

    @FXML
    private TextArea notesArea;

    private boolean isPasswordVisible = false;
    @Setter
    private String masterPassword;
    @Setter
    private DashboardController dashboardController;
    private Account editingAccount = null; // null = add mode, not null = edit mode

    public void setEditMode(Account account) {
        this.editingAccount = account;
        formTitle.setText("Edit Account");

        // Pre-fill form with account data
        labelField.setText(account.getLabel());
        categoryComboBox.setValue(account.getCategory());
        usernameField.setText(account.getUsername());
        passwordField.setText(account.getPassword());
        notesArea.setText(account.getNotes());
    }

    @FXML
    private void initialize() {
        // Populate category dropdown
        categoryComboBox.getItems().addAll("Email", "Facebook", "Instagram", "LinkedIn", "Other");
        categoryComboBox.setValue("Email"); // Default
    }

    @FXML
    private void togglePasswordVisibility() {
        if (isPasswordVisible) {
            // Hide password
            passwordField.setText(passwordFieldVisible.getText());
            passwordField.setVisible(true);
            passwordField.setManaged(true);
            passwordFieldVisible.setVisible(false);
            passwordFieldVisible.setManaged(false);

            updateToggleIcon("/icons/eye.png");
            isPasswordVisible = false;
        } else {
            // Show password
            passwordFieldVisible.setText(passwordField.getText());
            passwordFieldVisible.setVisible(true);
            passwordFieldVisible.setManaged(true);
            passwordField.setVisible(false);
            passwordField.setManaged(false);

            updateToggleIcon("/icons/closed-eye.png");
            isPasswordVisible = true;
        }
    }

    private void updateToggleIcon(String iconPath) {
        try {
            javafx.scene.image.ImageView icon = new javafx.scene.image.ImageView(
                    new javafx.scene.image.Image(Objects.requireNonNull(getClass().getResourceAsStream(iconPath)))
            );
            icon.setFitWidth(20);
            icon.setFitHeight(20);
            icon.setPreserveRatio(true);
            togglePasswordBtn.setGraphic(icon);
        } catch (Exception e) {
            System.err.println("Failed to load icon: " + iconPath);
        }
    }

    @FXML
    private void handleSave() {
        String label = labelField.getText().trim();
        String category = categoryComboBox.getValue();
        String username = usernameField.getText().trim();
        String password = isPasswordVisible ? passwordFieldVisible.getText() : passwordField.getText();
        String notes = notesArea.getText().trim();

        // Validation
        if (label.isEmpty()) {
            showAlert("Validation Error", "Please enter a label");
            return;
        }
        if (username.isEmpty()) {
            showAlert("Validation Error", "Please enter a username/email");
            return;
        }
        if (password.isEmpty()) {
            showAlert("Validation Error", "Please enter a password");
            return;
        }

        try {
            // Load existing accounts
            List<Account> accounts = LockBoxStorage.loadLockBox(masterPassword);

            if (editingAccount != null) {
                // Edit mode - update existing account
                for (Account acc : accounts) {
                    if (acc.getId().equals(editingAccount.getId())) {
                        acc.setLabel(label);
                        acc.setCategory(category);
                        acc.setUsername(username);
                        acc.setPassword(password);
                        acc.setNotes(notes);
                        break;
                    }
                }
            } else {
                // Add mode - create new account
                Account newAccount = new Account(label, category, username, password, notes);
                accounts.add(newAccount);
            }

            // Save updated accounts
            LockBoxStorage.saveAccounts(masterPassword, accounts);

            // Go back to dashboard
            goBackToDashboard();

        } catch (Exception e) {
            showAlert("Error", "Failed to save account: " + e.getMessage());
        }
    }

    @FXML
    private void handleCancel() {
        goBackToDashboard();
    }

    private void goBackToDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/dashboard.fxml"));
            Parent dashboard = loader.load();

            DashboardController controller = loader.getController();
            controller.setMasterPassword(masterPassword);
            controller.loadAccounts();

            Stage stage = (Stage) labelField.getScene().getWindow();
            Scene scene = new Scene(dashboard, 950, 700);
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/css/style.css")).toExternalForm());
            stage.setScene(scene);

        } catch (Exception e) {
            System.err.println("Failed to load dashboard: " + e.getMessage());
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}