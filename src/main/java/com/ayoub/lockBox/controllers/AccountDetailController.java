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

public class AccountDetailController {

    @FXML
    private ImageView categoryIcon;

    @FXML
    private Label accountLabel;

    @FXML
    private Label categoryBadge;

    @FXML
    private Label usernameLabel;

    @FXML
    private Label passwordLabel;

    @FXML
    private Label notesLabel;

    @FXML
    private Button togglePasswordBtn;

    @Setter
    private SecureCredentials credentials;

    @Setter
    private DashboardController dashboardController;

    private Account currentAccount;
    private boolean isPasswordVisible = false;

    public void setAccount(Account account) {
        this.currentAccount = account;
        loadAccountData();
    }

    private void loadAccountData() {
        // Set labels
        accountLabel.setText(currentAccount.getLabel());
        categoryBadge.setText(currentAccount.getCategory().toUpperCase());
        usernameLabel.setText(currentAccount.getUsername());
        notesLabel.setText(currentAccount.getNotes() != null && !currentAccount.getNotes().isEmpty() ? currentAccount.getNotes() : "No notes added.");

        // Set category icon
        IconUtil.setIcon(categoryIcon, IconUtil.getCategoryIconPath(currentAccount.getCategory()));
    }

    @FXML
    private void togglePasswordVisibility() {
        if (isPasswordVisible) {
            // Hide password
            passwordLabel.setText("••••••••••••");
            IconUtil.updatePasswordIcon(togglePasswordBtn,"/icons/eye.png");
            isPasswordVisible = false;
        } else {
            // Show password
            passwordLabel.setText(currentAccount.getPassword());
            IconUtil.updatePasswordIcon(togglePasswordBtn,"/icons/closed-eye.png");
            isPasswordVisible = true;
        }
    }

    @FXML
    private void copyUsername() {
        copyToClipboard(usernameLabel.getText());
        Stage stage = (Stage) accountLabel.getScene().getWindow();
        ToastUtil.showToast(stage, "✓ Username copied!", 117);
    }

    @FXML
    private void copyPassword() {
        copyToClipboard(currentAccount.getPassword());
        Stage stage = (Stage) accountLabel.getScene().getWindow();
        ToastUtil.showToast(stage, "✓ Password copied!", 117);
    }

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

    @FXML
    private void handleDelete() {
        ModalUtil.<DeleteConfirmController>showTransparentModal(
                "/fxml/delete-confirmation.fxml",
                accountLabel.getScene().getWindow(),
                controller -> {
                    controller.setAccountName(currentAccount.getLabel());
                    if (controller.isConfirmed()) {
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
        );
    }

    @FXML
    private void handleBack() {
        Stage stage = (Stage) accountLabel.getScene().getWindow();
        stage.close();
    }


}