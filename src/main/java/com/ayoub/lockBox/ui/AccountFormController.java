package com.ayoub.lockBox.ui;

import com.sun.javafx.fxml.builder.JavaFXImageBuilder;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;

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
            togglePasswordBtn.setGraphic(new ImageView("/icons/eye.png"));
            isPasswordVisible = false;
        } else {
            // Show password
            passwordFieldVisible.setText(passwordField.getText());
            passwordFieldVisible.setVisible(true);
            passwordFieldVisible.setManaged(true);
            passwordField.setVisible(false);
            passwordField.setManaged(false);
            togglePasswordBtn.setGraphic(new ImageView("/icons/closed-eye.png"));
            isPasswordVisible = true;
        }
    }

    @FXML
    private void handleSave() {
        String label = labelField.getText();
        String category = categoryComboBox.getValue();
        String username = usernameField.getText();
        String password = isPasswordVisible ? passwordFieldVisible.getText() : passwordField.getText();
        String notes = notesArea.getText();

        // Validation
        if (label.isEmpty() || username.isEmpty() || password.isEmpty()) {
            System.out.println("Please fill all required fields");
            return;
        }

        // TODO: Save account
        System.out.println("Saving account: " + label);
        System.out.println("Category: " + category);
        System.out.println("Username: " + username);
        System.out.println("Password: " + password);
        System.out.println("Notes: " + notes);
    }

    @FXML
    private void handleCancel() {
        System.out.println("Cancel clicked - close form");
        // TODO: Close window or go back to dashboard
    }
}
