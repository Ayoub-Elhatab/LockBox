package com.ayoub.lockBox.ui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.VBox;

public class LoginController {

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private Label errorLabel;

    @FXML
    private VBox firstLaunchBox;

    @FXML
    private void handleUnlock() {
        String password = passwordField.getText();

        System.out.println("Password entered: " + password);
        errorLabel.setVisible(true);
        errorLabel.setManaged(true);
    }
}