package com.ayoub.lockBox.ui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;

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

    private String actualPassword = "mySecretPassword123"; // TODO: Get from account data
    private boolean isPasswordVisible = false;

    @FXML
    private void initialize() {
        // TODO: Load account data and populate fields
        loadAccountData();
    }

    private void loadAccountData() {
        // TODO: Replace with actual account data
        accountLabel.setText("Work Gmail");
        categoryBadge.setText("EMAIL");
        usernameLabel.setText("ayoub.lh@gmail.com");
        notesLabel.setText("Important work account");

        // Set category icon
        categoryIcon.setImage(new Image(getClass().getResourceAsStream("/icons/gmail.png")));
    }

    @FXML
    private void togglePassword() {
        if (isPasswordVisible) {
            // Hide password
            passwordLabel.setText("••••••••••••");
            ImageView eyeIcon = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/icons/eye.png"))));
            eyeIcon.setFitWidth(18);
            eyeIcon.setFitHeight(18);
            eyeIcon.setPreserveRatio(true);
            togglePasswordBtn.setGraphic(eyeIcon);
            isPasswordVisible = false;
        } else {
            // Show password
            passwordLabel.setText(actualPassword);
            ImageView eyeClosedIcon = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/icons/closed-eye.png"))));
            eyeClosedIcon.setFitWidth(18);
            eyeClosedIcon.setFitHeight(18);
            eyeClosedIcon.setPreserveRatio(true);
            togglePasswordBtn.setGraphic(eyeClosedIcon);
            isPasswordVisible = true;
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
        content.putString(actualPassword);
        clipboard.setContent(content);
        System.out.println("Password copied!");
        // TODO: Show toast notification
    }

    @FXML
    private void handleEdit() {
        System.out.println("Edit account");
        // TODO: Open account form in edit mode
    }

    @FXML
    private void handleDelete() {
        System.out.println("Delete account");
        // TODO: Show delete confirmation dialog
    }

    @FXML
    private void handleBack() {
        System.out.println("Back to dashboard");
        // TODO: Navigate back to dashboard
    }
}