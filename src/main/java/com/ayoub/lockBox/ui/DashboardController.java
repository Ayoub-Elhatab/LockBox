package com.ayoub.lockBox.ui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class DashboardController {

    @FXML
    private Label lockTimerLabel;

    @FXML
    private TextField searchField;

    @FXML
    private Button filterAll;

    @FXML
    private Button filterEmail;

    @FXML
    private Button filterFacebook;

    @FXML
    private Button filterInstagram;

    @FXML
    private Button filterLinkedin;

    @FXML
    private Button filterOther;

    @FXML
    private VBox accountListContainer;

    @FXML
    private Button addButton;

    @FXML
    private void initialize() {
        // This runs when the FXML loads
        System.out.println("Dashboard loaded");
        loadAccounts();
    }

    @FXML
    private void filterAll() {
        resetFilterButtons();
        filterAll.getStyleClass().remove("filter-btn");
        filterAll.getStyleClass().add("filter-btn-active");
        System.out.println("Filter: All");
    }

    @FXML
    private void filterEmail() {
        resetFilterButtons();
        filterEmail.getStyleClass().remove("filter-btn");
        filterEmail.getStyleClass().add("filter-btn-active");
        System.out.println("Filter: Email");
    }

    @FXML
    private void filterFacebook() {
        resetFilterButtons();
        filterFacebook.getStyleClass().remove("filter-btn");
        filterFacebook.getStyleClass().add("filter-btn-active");
        System.out.println("Filter: Facebook");
    }

    @FXML
    private void filterInstagram() {
        resetFilterButtons();
        filterInstagram.getStyleClass().remove("filter-btn");
        filterInstagram.getStyleClass().add("filter-btn-active");
        System.out.println("Filter: Instagram");
    }

    @FXML
    private void filterLinkedin() {
        resetFilterButtons();
        filterLinkedin.getStyleClass().remove("filter-btn");
        filterLinkedin.getStyleClass().add("filter-btn-active");
        System.out.println("Filter: Linkedin");
    }

    @FXML
    private void filterOther() {
        resetFilterButtons();
        filterOther.getStyleClass().remove("filter-btn");
        filterOther.getStyleClass().add("filter-btn-active");
        System.out.println("Filter: Other");
    }

    @FXML
    private void handleAddAccount() {
        System.out.println("Add account clicked");
        // TODO: Open add account form
    }

    private void resetFilterButtons() {
        filterAll.getStyleClass().remove("filter-btn-active");
        filterAll.getStyleClass().add("filter-btn");

        filterEmail.getStyleClass().remove("filter-btn-active");
        filterEmail.getStyleClass().add("filter-btn");

        filterFacebook.getStyleClass().remove("filter-btn-active");
        filterFacebook.getStyleClass().add("filter-btn");

        filterInstagram.getStyleClass().remove("filter-btn-active");
        filterInstagram.getStyleClass().add("filter-btn");

        filterLinkedin.getStyleClass().remove("filter-btn-active");
        filterLinkedin.getStyleClass().add("filter-btn");

        filterOther.getStyleClass().remove("filter-btn-active");
        filterOther.getStyleClass().add("filter-btn");
    }

    private void loadAccounts() {
        // TODO: Load accounts from storage and add to accountListContainer
        System.out.println("Loading accounts...");
    }
}