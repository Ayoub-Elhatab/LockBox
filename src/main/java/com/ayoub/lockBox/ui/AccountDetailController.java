package com.ayoub.lockBox.ui;

import com.ayoub.lockBox.model.Account;
import com.ayoub.lockBox.security.SecureCredentials;
import com.ayoub.lockBox.service.AccountService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lombok.Setter;

import java.util.List;
import java.util.Objects;

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
        notesLabel.setText(currentAccount.getNotes() != null && !currentAccount.getNotes().isEmpty()
                ? currentAccount.getNotes()
                : "No notes added.");

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
    private void togglePassword() {
        if (isPasswordVisible) {
            // Hide password
            passwordLabel.setText("••••••••••••");
            updateToggleIcon("/icons/eye.png");
            isPasswordVisible = false;
        } else {
            // Show password
            passwordLabel.setText(currentAccount.getPassword());
            updateToggleIcon("/icons/closed-eye.png");
            isPasswordVisible = true;
        }
    }

    private void updateToggleIcon(String iconPath) {
        try {
            ImageView icon = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream(iconPath))));
            icon.setFitWidth(18);
            icon.setFitHeight(18);
            icon.setPreserveRatio(true);
            togglePasswordBtn.setGraphic(icon);
        } catch (Exception e) {
            System.err.println("Failed to load icon: " + iconPath);
        }
    }

    @FXML
    private void copyUsername() {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();
        content.putString(usernameLabel.getText());
        clipboard.setContent(content);
        System.out.println("Username copied!");
        // TODO: Show toast notification
    }

    @FXML
    private void copyPassword() {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();
        content.putString(currentAccount.getPassword());
        clipboard.setContent(content);
        System.out.println("Password copied!");
        // TODO: Show toast notification
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
        try {
            // Show delete confirmation modal
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/delete-confirm.fxml"));
            Parent modal = loader.load();

            DeleteConfirmController controller = loader.getController();
            controller.setAccountName(currentAccount.getLabel());

            // Create modal stage
            Stage modalStage = new Stage();
            modalStage.initModality(Modality.APPLICATION_MODAL);
            modalStage.initStyle(StageStyle.TRANSPARENT);
            modalStage.setScene(new Scene(modal));
            modalStage.showAndWait();

            // If confirmed, delete account
            if (controller.isConfirmed()) {
                deleteAccount();
            }

        } catch (Exception e) {
            System.err.println("Failed to show delete confirmation: " + e.getMessage());
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

            // Go back to dashboard
            handleBack();

        } catch (Exception e) {
            System.err.println("Failed to delete account: " + e.getMessage());
        }
    }

    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/dashboard.fxml"));
            Parent dashboard = loader.load();

            DashboardController controller = loader.getController();
            controller.setCredentials(credentials);
            controller.loadAccounts();

            Stage stage = (Stage) accountLabel.getScene().getWindow();
            Scene scene = new Scene(dashboard, 950, 800);
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/css/style.css")).toExternalForm());
            stage.setScene(scene);

        } catch (Exception e) {
            System.err.println("Failed to load dashboard: " + e.getMessage());
        }
    }
}