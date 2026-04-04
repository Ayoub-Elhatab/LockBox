package com.ayoub.lockBox.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import lombok.Getter;

/**
 * Controller for the delete confirmation modal dialog.
 * <p>
 * Displays a warning modal asking the user to confirm deletion of an account.
 * The modal shows the account name and provides Cancel and Delete buttons.
 * Uses a boolean flag to track whether the user confirmed the deletion.
 * <p>
 * This is a transparent modal overlay displayed on top of the main window.
 *
 * @see DashboardController
 * @see AccountDetailController
 *
 * @author Ayoub Elhatab
 * LinkedIn: <a href="https://www.linkedin.com/in/ayoub-elhatab/">Ayoub Elhatab</a>
 */
public class DeleteConfirmController {

    @FXML private Label accountNameLabel;

    @Getter private boolean confirmed = false;

    /**
     * Sets the account name to display in the confirmation message.
     * <p>
     * Formats the name with quotes (e.g., "Facebook"?) for the confirmation prompt.
     *
     * @param name the name of the account to be deleted
     */
    public void setAccountName(String name) {
        accountNameLabel.setText("\"" + name + "\"?");
    }

    /**
     * Handles the Cancel button click.
     * <p>
     * Sets confirmed flag to false and closes the modal without deleting.
     */
    @FXML
    private void handleCancel() {
        confirmed = false;
        closeDialog();
    }

    /**
     * Handles the Delete button click.
     * <p>
     * Sets confirmed flag to true and closes the modal, signaling that
     * the deletion should proceed.
     */
    @FXML
    private void handleConfirmDelete() {
        confirmed = true;
        closeDialog();
    }

    /**
     * Closes the confirmation modal dialog.
     */
    private void closeDialog() {
        Stage stage = (Stage) accountNameLabel.getScene().getWindow();
        stage.close();
    }
}