package com.ayoub.lockBox.ui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import lombok.Getter;

public class DeleteConfirmController {

    @FXML
    private Label accountNameLabel;

    @Getter
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

    private void closeDialog() {
        Stage stage = (Stage) accountNameLabel.getScene().getWindow();
        stage.close();
    }
}