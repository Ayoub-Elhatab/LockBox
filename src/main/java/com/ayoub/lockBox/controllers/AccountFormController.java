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

/**
 * Controller for the account form modal (Add/Edit Account).
 * <p>
 * Provides a unified form interface for both creating new accounts and editing
 * existing ones. The form includes input validation, password visibility toggle,
 * and category selection. All account data is encrypted before being saved to storage.
 * <p>
 * This controller manages:
 * <ul>
 *   <li>Add mode: Creating new accounts with validation</li>
 *   <li>Edit mode: Pre-filling and updating existing account data</li>
 *   <li>Category selection (Email, Facebook, Instagram, LinkedIn, Other)</li>
 *   <li>Password visibility toggle between masked and plain text</li>
 *   <li>Input validation (label, username, password required)</li>
 *   <li>Encrypted account persistence to lockBox.enc</li>
 * </ul>
 *
 * @see Account
 * @see DashboardController
 * @see AccountService
 *
 * @author Ayoub Elhatab.
 * LinkedIn: <a href="https://www.linkedin.com/in/ayoub-elhatab/">Ayoub Elhatab</a>
 */
public class AccountFormController {

    @FXML private Label formTitle;
    @FXML private TextField labelField;
    @FXML private ComboBox<String> categoryComboBox;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private TextField passwordFieldVisible;
    @FXML private Button togglePasswordBtn;
    @FXML private TextArea notesArea;

    private boolean isPasswordVisible = false;

    @Setter
    private SecureCredentials credentials;

    @Setter
    private DashboardController dashboardController;

    private Account editingAccount = null; // null = add mode, not null = edit mode

    /**
     * Switches the form to edit mode and pre-fills all fields with existing account data.
     * <p>
     * Changes the form title to "Edit Account" and populates label, category,
     * username, password, and notes fields with the provided account's data.
     *
     * @param account the account to edit
     */
    public void setEditMode(Account account) {
        this.editingAccount = account;
        formTitle.setText("Edit Account");

        labelField.setText(account.getLabel());
        categoryComboBox.setValue(account.getCategory());
        usernameField.setText(account.getUsername());
        passwordField.setText(account.getPassword());
        notesArea.setText(account.getNotes());
    }

    /**
     * Initializes the form by populating the category dropdown with predefined options.
     * <p>
     * Sets "Email" as the default category selection. This method is called
     * automatically by JavaFX after FXML loading.
     */
    @FXML
    private void initialize() {
        categoryComboBox.getItems().addAll("Email", "Facebook", "Instagram", "LinkedIn", "Other");
        categoryComboBox.setValue("Email");
    }

    /**
     * Toggles password visibility between masked (PasswordField) and plain text (TextField).
     * <p>
     * Synchronizes the password value between the two fields and updates the
     * toggle button icon (eye/closed-eye) accordingly.
     */
    @FXML
    private void togglePasswordVisibility() {
        if (isPasswordVisible) {
            passwordField.setText(passwordFieldVisible.getText());
            passwordField.setVisible(true);
            passwordField.setManaged(true);
            passwordFieldVisible.setVisible(false);
            passwordFieldVisible.setManaged(false);

            IconUtil.updatePasswordIcon(togglePasswordBtn,"/icons/eye.png");
            isPasswordVisible = false;
        } else {
            passwordFieldVisible.setText(passwordField.getText());
            passwordFieldVisible.setVisible(true);
            passwordFieldVisible.setManaged(true);
            passwordField.setVisible(false);
            passwordField.setManaged(false);

            IconUtil.updatePasswordIcon(togglePasswordBtn,"/icons/closed-eye.png");
            isPasswordVisible = true;
        }
    }

    /**
     * Validates and saves the account data to encrypted storage.
     * <p>
     * Workflow:
     * <ol>
     *   <li>Validate required fields (label, username, password)</li>
     *   <li>Load existing accounts from encrypted storage</li>
     *   <li>In edit mode: Update existing account by ID</li>
     *   <li>In add mode: Create new account with generated UUID</li>
     *   <li>Save updated account list with AES-256-GCM encryption</li>
     *   <li>Refresh dashboard and close modal</li>
     * </ol>
     * Displays validation or error alerts if the operation fails.
     */
    @FXML
    private void handleSave() {
        String label = labelField.getText().trim();
        String category = categoryComboBox.getValue();
        String username = usernameField.getText().trim();
        String password = isPasswordVisible ? passwordFieldVisible.getText() : passwordField.getText();
        String notes = notesArea.getText().trim();

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
            List<Account> accounts = AccountService.load(credentials);

            if (editingAccount != null) {
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
                Account newAccount = new Account(label, category, username, password, notes);
                accounts.add(newAccount);
            }

            AccountService.save(credentials, accounts);

            if (dashboardController != null) {
                dashboardController.refreshAccounts();
            }

            Stage stage = (Stage) labelField.getScene().getWindow();
            stage.close();

        } catch (Exception e) {
            showAlert("Error", "Failed to save account: " + e.getMessage());
        }
    }

    /**
     * Cancels the form operation and closes the modal without saving.
     * Delegates to {@link #goBackToDashboard()}.
     */
    @FXML
    private void handleCancel() {
        goBackToDashboard();
    }

    /**
     * Closes the account form modal and returns to the dashboard.
     * No data is saved when this method is called.
     */
    private void goBackToDashboard() {
        Stage stage = (Stage) labelField.getScene().getWindow();
        stage.close();
    }


}