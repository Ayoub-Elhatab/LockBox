package com.ayoub.lockBox.controllers;

import com.ayoub.lockBox.model.Account;
import com.ayoub.lockBox.security.SecureCredentials;
import com.ayoub.lockBox.service.AccountService;
import com.ayoub.lockBox.utils.IconUtil;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import lombok.Setter;
import java.util.List;
import static com.ayoub.lockBox.utils.AlertUtil.showAlert;

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
    private SecureCredentials credentials;

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

            IconUtil.updatePasswordIcon(togglePasswordBtn,"/icons/eye.png");
            isPasswordVisible = false;
        } else {
            // Show password
            passwordFieldVisible.setText(passwordField.getText());
            passwordFieldVisible.setVisible(true);
            passwordFieldVisible.setManaged(true);
            passwordField.setVisible(false);
            passwordField.setManaged(false);

            IconUtil.updatePasswordIcon(togglePasswordBtn,"/icons/closed-eye.png");
            isPasswordVisible = true;
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
            List<Account> accounts = AccountService.load(credentials);

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
            AccountService.save(credentials, accounts);

            // Refresh dashboard if available
            if (dashboardController != null) {
                dashboardController.refreshAccounts();
            }

            // Close the modal window
            Stage stage = (Stage) labelField.getScene().getWindow();
            stage.close();

        } catch (Exception e) {
            showAlert("Error", "Failed to save account: " + e.getMessage());

        }
    }

    @FXML
    private void handleCancel() {
        goBackToDashboard();
    }

    private void goBackToDashboard() {
        Stage stage = (Stage) labelField.getScene().getWindow();
        stage.close();
    }


}