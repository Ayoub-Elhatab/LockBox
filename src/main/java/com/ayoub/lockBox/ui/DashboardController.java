package com.ayoub.lockBox.ui;

import com.ayoub.lockBox.model.Account;
import com.ayoub.lockBox.storage.LockBoxStorage;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.Setter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class DashboardController {

    /**
     * TODO :
     *        add error dialog
     *        when i small window when i click right that displays multiple choices
     */
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

    @Setter
    private String masterPassword;
    private List<Account> allAccounts = new ArrayList<>();
    private String currentFilter = "All";

    @FXML
    private void initialize() {
        // Add search listener
        searchField.textProperty().addListener((obs, oldVal, newVal) -> filterAccounts());
    }

    public void loadAccounts() {
        try {
            allAccounts = LockBoxStorage.loadLockBox(masterPassword);
            displayAccounts(allAccounts);
        } catch (Exception e) {
            System.err.println("Failed to load accounts: " + e.getMessage());
        }
    }

    private void displayAccounts(List<Account> accounts) {
        accountListContainer.getChildren().clear();

        if (accounts.isEmpty()) {
            Label emptyLabel = new Label("No accounts yet. Click + to add one!");
            emptyLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #7F8C8D; -fx-padding: 40px;");
            accountListContainer.getChildren().add(emptyLabel);
            return;
        }

        for (Account account : accounts) {
            accountListContainer.getChildren().add(createAccountCard(account));
        }
    }

    private HBox createAccountCard(Account account) {
        HBox card = new HBox(16);
        card.setAlignment(Pos.CENTER_LEFT);
        card.getStyleClass().add("account-card");
        card.setPadding(new Insets(14, 16, 14, 16));

        // Category icon
        ImageView icon = new ImageView();
        icon.setFitWidth(36);
        icon.setFitHeight(36);
        icon.setPreserveRatio(true);

        String iconPath = switch (account.getCategory().toLowerCase()) {
            case "email" -> "/icons/gmail.png";
            case "facebook" -> "/icons/facebook.png";
            case "instagram" -> "/icons/instagram.png";
            case "linkedin" -> "/icons/linkedin.png";
            default -> "/icons/other.png";
        };

        try {
            icon.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream(iconPath))));
        } catch (Exception e) {
            System.err.println("Icon not found: " + iconPath);
        }

        // Account info
        VBox info = new VBox(4);
        info.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(info, javafx.scene.layout.Priority.ALWAYS);

        Label labelText = new Label(account.getLabel());
        labelText.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #2C3E50;");

        Label usernameText = new Label(account.getUsername());
        usernameText.setStyle("-fx-font-size: 13px; -fx-text-fill: #7F8C8D;");

        info.getChildren().addAll(labelText, usernameText);

        // Action buttons
        HBox actions = new HBox(8);
        actions.setAlignment(Pos.CENTER_RIGHT);

        // Copy button
        Button copyBtn = createIconButton("/icons/copy.png", "btn-icon");
        copyBtn.setOnAction(e -> copyPassword(account));

        // Edit button
        Button editBtn = createIconButton("/icons/edit.png", "btn-icon");
        editBtn.setOnAction(e -> editAccount(account));

        // Delete button
        Button deleteBtn = createIconButton("/icons/delete.png", "btn-icon-danger");
        deleteBtn.setOnAction(e -> deleteAccount(account));

        actions.getChildren().addAll(copyBtn, editBtn, deleteBtn);

        // Make card clickable to view details
        card.setOnMouseClicked(e -> viewAccountDetail(account));
        card.setStyle(card.getStyle() + "-fx-cursor: hand;");

        card.getChildren().addAll(icon, info, actions);
        return card;
    }

    private Button createIconButton(String iconPath, String styleClass) {
        Button btn = new Button();
        btn.getStyleClass().add(styleClass);

        try {
            ImageView icon = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream(iconPath))));
            icon.setFitWidth(16);
            icon.setFitHeight(16);
            icon.setPreserveRatio(true);
            btn.setGraphic(icon);
        } catch (Exception e) {
            System.err.println("Icon not found: " + iconPath);
        }

        return btn;
    }

    private void copyPassword(Account account) {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();
        content.putString(account.getPassword());
        clipboard.setContent(content);
        System.out.println("Password copied for: " + account.getLabel());
        // TODO: Show toast notification
    }

    private void editAccount(Account account) {
        System.out.println("Edit account: " + account.getLabel());
        // TODO: Open account form in edit mode
    }

    private void deleteAccount(Account account) {
        System.out.println("Delete account: " + account.getLabel());
        // TODO: Show delete confirmation dialog
    }

    private void viewAccountDetail(Account account) {
//        try {
//            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/account-detail.fxml"));
//            Parent detail = loader.load();
//
//            AccountDetailController controller = loader.getController();
//            controller.setMasterPassword(masterPassword);
//            controller.setAccount(account);
//            controller.setDashboardController(this);
//
//            Stage stage = (Stage) accountListContainer.getScene().getWindow();
//            Scene scene = new Scene(detail, 600, 700);
//            scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
//            stage.setScene(scene);
//
//        } catch (Exception e) {
//            System.err.println("Failed to load account detail: " + e.getMessage());
//        }
    }

    @FXML
    private void filterAll() {
        currentFilter = "All";
        updateFilterButtons();
        filterAccounts();
    }

    @FXML
    private void filterEmail() {
        currentFilter = "Email";
        updateFilterButtons();
        filterAccounts();
    }

    @FXML
    private void filterFacebook() {
        currentFilter = "Facebook";
        updateFilterButtons();
        filterAccounts();
    }

    @FXML
    private void filterInstagram() {
        currentFilter = "Instagram";
        updateFilterButtons();
        filterAccounts();
    }

    @FXML
    private void filterLinkedin() {
        currentFilter = "LinkedIn";
        updateFilterButtons();
        filterAccounts();
    }

    @FXML
    private void filterOther() {
        currentFilter = "Other";
        updateFilterButtons();
        filterAccounts();
    }

    private void updateFilterButtons() {
        // Remove active class from all
        filterAll.getStyleClass().removeAll("filter-btn-active");
        filterEmail.getStyleClass().removeAll("filter-btn-active");
        filterFacebook.getStyleClass().removeAll("filter-btn-active");
        filterInstagram.getStyleClass().removeAll("filter-btn-active");
        filterLinkedin.getStyleClass().removeAll("filter-btn-active");
        filterOther.getStyleClass().removeAll("filter-btn-active");

        filterAll.getStyleClass().add("filter-btn");
        filterEmail.getStyleClass().add("filter-btn");
        filterFacebook.getStyleClass().add("filter-btn");
        filterInstagram.getStyleClass().add("filter-btn");
        filterLinkedin.getStyleClass().add("filter-btn");
        filterOther.getStyleClass().add("filter-btn");

        // Add active class to selected
        Button activeButton = switch (currentFilter) {
            case "Email" -> filterEmail;
            case "Facebook" -> filterFacebook;
            case "Instagram" -> filterInstagram;
            case "LinkedIn" -> filterLinkedin;
            case "Other" -> filterOther;
            default -> filterAll;
        };

        activeButton.getStyleClass().remove("filter-btn");
        activeButton.getStyleClass().add("filter-btn-active");
    }

    private void filterAccounts() {
        String searchText = searchField.getText().toLowerCase();

        List<Account> filtered = allAccounts.stream()
                .filter(account -> {
                    // Filter by category
                    if (!currentFilter.equals("All") && !account.getCategory().equalsIgnoreCase(currentFilter)) {
                        return false;
                    }
                    // Filter by search text
                    if (!searchText.isEmpty()) {
                        return account.getLabel().toLowerCase().contains(searchText) ||
                                account.getUsername().toLowerCase().contains(searchText) ||
                                (account.getNotes() != null && account.getNotes().toLowerCase().contains(searchText));
                    }
                    return true;
                })
                .collect(Collectors.toList());

        displayAccounts(filtered);
    }

    @FXML
    private void handleAddAccount() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/account-form.fxml"));
            Parent form = loader.load();

            AccountFormController controller = loader.getController();
            controller.setMasterPassword(masterPassword);
            controller.setDashboardController(this);

            Stage stage = (Stage) addButton.getScene().getWindow();
            Scene scene = new Scene(form, 600, 850);
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/css/style.css")).toExternalForm());
            stage.setScene(scene);

        } catch (Exception e) {
            System.err.println("Failed to load account form: " + e.getMessage());
        }
    }

    public void refreshAccounts() {
        loadAccounts();
    }
}