package com.ayoub.lockBox.ui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class DeleteConfirmController {

    @FXML
    private Label accountNameLabel;

    private boolean confirmed = false;

    public void setAccountName(String name) {
        accountNameLabel.setText("\"" + name + "\"?");
    }

    @FXML
    private void handleCancel() {
        confirmed = false;
        closeDialog();
    }

    @FXML
    private void handleConfirmDelete() {
        confirmed = true;
        closeDialog();
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    private void closeDialog() {
        Stage stage = (Stage) accountNameLabel.getScene().getWindow();
        stage.close();
    }
}