package com.ayoub.lockBox.controllers;

import com.ayoub.lockBox.model.Account;
import com.ayoub.lockBox.security.SecureCredentials;
import com.ayoub.lockBox.service.AccountService;
import com.ayoub.lockBox.utils.AlertUtil;
import com.ayoub.lockBox.utils.IconUtil;
import com.ayoub.lockBox.utils.ModalUtil;
import com.ayoub.lockBox.utils.ToastUtil;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import lombok.Setter;
import java.util.List;
import java.util.Objects;
import static com.ayoub.lockBox.utils.ClipboardUtil.copyToClipboard;

/**
 * Controller for the account detail view modal.
 * <p>
 * Displays detailed information about a selected account including username,
 * password (with toggle visibility), category, and notes. Provides actions
 * to copy credentials to clipboard, edit the account, or delete it with confirmation.
 * <p>
 * This controller manages:
 * <ul>
 *   <li>Displaying account information with category-specific icons</li>
 *   <li>Password visibility toggle (show/hide)</li>
 *   <li>Copy username/password to clipboard with toast notifications</li>
 *   <li>Edit account navigation</li>
 *   <li>Delete account with confirmation modal</li>
 * </ul>
 *
 * @see Account
 * @see DashboardController
 * @see AccountFormController
 * @see DeleteConfirmController
 *
 * @author Ayoub Elhatab.
 * LinkedIn: <a href="https://www.linkedin.com/in/ayoub-elhatab/">Ayoub Elhatab</a>
 */
public class AccountDetailController {

    @FXML private ImageView categoryIcon;
    @FXML private Label accountLabel;
    @FXML private Label categoryBadge;
    @FXML private Label usernameLabel;
    @FXML private Label passwordLabel;
    @FXML private Label notesLabel;
    @FXML private Button togglePasswordBtn;

    @Setter
    private SecureCredentials credentials;

    @Setter
    private DashboardController dashboardController;

    private Account currentAccount;
    private boolean isPasswordVisible = false;

    /**
     * Sets the account to be displayed in the detail view.
     * Loads and populates all account information into the UI components.
     *
     * @param account the account to display
     */
    public void setAccount(Account account) {
        this.currentAccount = account;
        loadAccountData();
    }

    /**
     * Loads account data into UI components.
     * <p>
     * Populates labels with account information (label, category, username, notes)
     * and sets the appropriate category icon. Initializes password as masked dots.
     */
    private void loadAccountData() {
        accountLabel.setText(currentAccount.getLabel());
        categoryBadge.setText(currentAccount.getCategory().toUpperCase());
        usernameLabel.setText(currentAccount.getUsername());
        notesLabel.setText(currentAccount.getNotes() != null && !currentAccount.getNotes().isEmpty() ? currentAccount.getNotes() : "No notes added.");

        IconUtil.setIcon(categoryIcon, IconUtil.getCategoryIconPath(currentAccount.getCategory()));
    }

    /**
     * Toggles password visibility between masked dots and plain text.
     * Updates the toggle button icon accordingly (eye/closed-eye).
     */
    @FXML
    private void togglePasswordVisibility() {
        if (isPasswordVisible) {
            passwordLabel.setText("••••••••••••");
            IconUtil.updatePasswordIcon(togglePasswordBtn,"/icons/eye.png");
            isPasswordVisible = false;
        } else {
            passwordLabel.setText(currentAccount.getPassword());
            IconUtil.updatePasswordIcon(togglePasswordBtn,"/icons/closed-eye.png");
            isPasswordVisible = true;
        }
    }

    /**
     * Copies the username to system clipboard.
     * Displays a success toast notification at the bottom of the modal.
     */
    @FXML
    private void copyUsername() {
        copyToClipboard(usernameLabel.getText());
        Stage stage = (Stage) accountLabel.getScene().getWindow();
        ToastUtil.showToast(stage, "✓ Username copied!", 117);
    }

    /**
     * Copies the password to system clipboard.
     * Displays a success toast notification at the bottom of the modal.
     */
    @FXML
    private void copyPassword() {
        copyToClipboard(currentAccount.getPassword());
        Stage stage = (Stage) accountLabel.getScene().getWindow();
        ToastUtil.showToast(stage, "✓ Password copied!", 117);
    }

    /**
     * Opens the account edit form in the current modal window.
     * Replaces the detail view scene with the account form scene in edit mode.
     */
    @FXML
    private void handleEdit() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/account-form.fxml"));
            Parent form = loader.load();

            AccountFormController controller = loader.getController();
            controller.setCredentials(credentials);
            controller.setDashboardController(dashboardController);
            controller.setEditMode(currentAccount);

            Stage stage = (Stage) accountLabel.getScene().getWindow();
            Scene scene = new Scene(form, 600, 850);
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/css/style.css")).toExternalForm());
            stage.setScene(scene);

        } catch (Exception e) {
            System.err.println("Failed to load account form: " + e.getMessage());
        }
    }

    /**
     * Shows a delete confirmation modal and performs account deletion if confirmed.
     * <p>
     * Workflow:
     * <ol>
     *   <li>Display transparent delete confirmation modal</li>
     *   <li>If confirmed, remove account from encrypted storage</li>
     *   <li>Refresh dashboard account list</li>
     *   <li>Close the detail modal</li>
     * </ol>
     * On error, displays an error alert to the user.
     */
    @FXML
    private void handleDelete() {
        DeleteConfirmController controller = ModalUtil.<DeleteConfirmController>showTransparentModal(
                "/fxml/delete-confirmation.fxml",
                accountLabel.getScene().getWindow(),
                c -> c.setAccountName(currentAccount.getLabel())
        );

        if (controller != null && controller.isConfirmed()) {
            try {
                List<Account> accounts = AccountService.load(credentials);
                accounts.removeIf(acc -> acc.getId().equals(currentAccount.getId()));
                AccountService.save(credentials, accounts);
                if (dashboardController != null) dashboardController.refreshAccounts();
                ((Stage) accountLabel.getScene().getWindow()).close();
            } catch (Exception e) {
                AlertUtil.showAlert("Error", "Failed to delete account");
            }
        }
    }

    /**
     * Closes the account detail modal and returns to the dashboard.
     */
    @FXML
    private void handleBack() {
        Stage stage = (Stage) accountLabel.getScene().getWindow();
        stage.close();
    }

}