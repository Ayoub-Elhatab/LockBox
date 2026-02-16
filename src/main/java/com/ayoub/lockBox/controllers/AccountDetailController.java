package com.ayoub.lockBox.controllers;

import com.ayoub.lockBox.model.Account;
import com.ayoub.lockBox.security.SecureCredentials;
import com.ayoub.lockBox.service.AccountService;
import com.ayoub.lockBox.utils.IconUtil;
import com.ayoub.lockBox.utils.ToastUtil;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lombok.Setter;
import java.util.List;
import java.util.Objects;
import static com.ayoub.lockBox.utils.AlertUtil.showAlert;
import static com.ayoub.lockBox.utils.ClipboardUtil.copyToClipboard;
import static com.ayoub.lockBox.utils.ToastUtil.showToast;

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
        String iconPath = switch (currentAccount.getCategory().toLowerCase()) {
            case "email" -> "/icons/gmail.png";
            case "facebook" -> "/icons/facebook.png";
            case "instagram" -> "/icons/instagram.png";
            case "linkedin" -> "/icons/linkedin.png";
            default -> "/icons/other.png";
        };

        try {
            categoryIcon.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream(iconPath))));
        } catch (Exception e) {
            System.err.println("Icon not found: " + iconPath);
        }
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
            showAlert("Error", "Failed to load account form");
        }
    }

    @FXML
    private void handleBack() {
        Stage stage = (Stage) accountLabel.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void handleDelete() {
        try {
            // Show delete confirmation modal
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/delete-confirmation.fxml"));
            Parent modal = loader.load();

            DeleteConfirmController controller = loader.getController();
            controller.setAccountName(currentAccount.getLabel());

            // Create modal stage
            Stage modalStage = new Stage();
            modalStage.initModality(Modality.APPLICATION_MODAL);
            modalStage.initOwner(accountLabel.getScene().getWindow());
            modalStage.initStyle(StageStyle.TRANSPARENT);

            Scene scene = new Scene(modal);
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/css/style.css")).toExternalForm());

            modalStage.setScene(scene);
            modalStage.showAndWait();

            // If confirmed, delete account
            if (controller.isConfirmed()) {
                deleteAccount();
            }

        } catch (Exception e) {
            showAlert("Error", "Failed to show delete confirmation");
        }
    }

    private void deleteAccount() {
        try {
            // Load all accounts
            List<Account> accounts = AccountService.load(credentials);

            // Remove current account
            accounts.removeIf(acc -> acc.getId().equals(currentAccount.getId()));

            // Save updated list
            AccountService.save(credentials, accounts);

            // Refresh dashboard
            if (dashboardController != null) {
                dashboardController.refreshAccounts();
            }

            // Close detail window
            Stage stage = (Stage) accountLabel.getScene().getWindow();
            stage.close();

        } catch (Exception e) {
            showAlert("Error", "Failed to delete account");
        }
    }


}