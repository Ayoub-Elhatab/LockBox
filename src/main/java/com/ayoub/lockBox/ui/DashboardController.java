package com.ayoub.lockBox.ui;

import com.ayoub.lockBox.model.Account;
import com.ayoub.lockBox.enums.Category;
import com.ayoub.lockBox.security.SecureCredentials;
import com.ayoub.lockBox.service.AccountService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lombok.Setter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class DashboardController {

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
    private SecureCredentials credentials;
    private List<Account> allAccounts = new ArrayList<>();
    private Category currentFilter = Category.ALL;

    @FXML
    private void initialize() {
        // Add search listener
        searchField.textProperty().addListener((obs, oldVal, newVal) -> filterAccounts());
    }

    public void loadAccounts() {
        try {
            allAccounts = AccountService.load(credentials);
            displayAccounts(allAccounts);
        } catch (Exception e) {
            System.err.println("Failed to load accounts: " + e.getMessage());
        }
    }

    private void displayAccounts(List<Account> accounts) {
        accountListContainer.getChildren().clear();

        if (accounts.isEmpty()) {
            VBox emptyState = new VBox(16);
            emptyState.setAlignment(Pos.CENTER);
            emptyState.setPadding(new Insets(190, 60, 60, 20));

            // Icon
            try {
                ImageView emptyIcon = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/icons/empty.png"))));
                emptyIcon.setFitWidth(80);
                emptyIcon.setFitHeight(80);
                emptyIcon.setPreserveRatio(true);
                emptyIcon.setOpacity(0.5);
                emptyState.getChildren().add(emptyIcon);
            } catch (Exception e) {
                System.err.println("Empty icon not found");
            }

            Label emptyLabel = new Label("No accounts yet");
            emptyLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #7F8C8D;");

            Label emptySubLabel = new Label("Click + to add your first account");
            emptySubLabel.setStyle("-fx-font-size: 19px; -fx-text-fill: #95A5A6;");

            emptyState.getChildren().addAll(emptyLabel, emptySubLabel);
            accountListContainer.getChildren().add(emptyState);
            VBox.setVgrow(emptyState, javafx.scene.layout.Priority.ALWAYS);
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

        // Create context menu (right-click menu)
        ContextMenu contextMenu = new ContextMenu();

        MenuItem viewDetailsItem = new MenuItem("View Details");
        viewDetailsItem.setOnAction(e -> viewAccountDetail(account));

        MenuItem copyPasswordItem = new MenuItem("Copy Password");
        copyPasswordItem.setOnAction(e -> copyPassword(account));

        MenuItem editItem = new MenuItem("Edit");
        editItem.setOnAction(e -> editAccount(account));

        MenuItem deleteItem = new MenuItem("Delete");
        deleteItem.setOnAction(e -> deleteAccount(account));

        contextMenu.getItems().addAll(viewDetailsItem, copyPasswordItem, editItem, deleteItem);

        // Mouse click handler
        card.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                // Left click - view details
                viewAccountDetail(account);
            } else if (e.getButton() == MouseButton.SECONDARY) {
                // Right click - show context menu
                contextMenu.show(card, e.getScreenX(), e.getScreenY());
            }
        });

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

    private void viewAccountDetail(Account account) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/account-detail.fxml"));
            Parent detail = loader.load();

            AccountDetailController controller = loader.getController();
            controller.setCredentials(credentials);
            controller.setAccount(account);
            controller.setDashboardController(this);

            // Create modal stage
            Stage modalStage = new Stage();
            modalStage.initModality(Modality.APPLICATION_MODAL);
            modalStage.initOwner(accountListContainer.getScene().getWindow());

            Scene scene = new Scene(detail, 600, 850);
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/css/style.css")).toExternalForm());

            modalStage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/icons/security.png"))));
            modalStage.setScene(scene);
            modalStage.setTitle("Account Details");
            modalStage.setResizable(false);
            modalStage.showAndWait();

        } catch (Exception e) {
            System.err.println("Failed to load account detail: " + e.getMessage());
        }
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
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/account-form.fxml"));
            Parent form = loader.load();

            AccountFormController controller = loader.getController();
            controller.setCredentials(credentials);
            controller.setDashboardController(this);
            controller.setEditMode(account);

            // Create modal stage
            Stage modalStage = new Stage();
            modalStage.initModality(Modality.APPLICATION_MODAL);
            modalStage.initOwner(accountListContainer.getScene().getWindow());

            Scene scene = new Scene(form, 600, 850);
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/css/style.css")).toExternalForm());

            modalStage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/icons/security.png"))));
            modalStage.setScene(scene);
            modalStage.setTitle("Edit Account");
            modalStage.setResizable(false);
            modalStage.showAndWait();

        } catch (Exception e) {
            System.err.println("Failed to load account form: " + e.getMessage());
        }
    }

    private void deleteAccount(Account account) {
        try {
            // Show delete confirmation modal
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/delete-confirmation.fxml"));
            Parent modal = loader.load();

            DeleteConfirmController controller = loader.getController();
            controller.setAccountName(account.getLabel());

            // Create modal stage
            Stage modalStage = new Stage();
            modalStage.initModality(Modality.APPLICATION_MODAL);
            modalStage.initStyle(StageStyle.TRANSPARENT);

            Scene scene = new Scene(modal);
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/css/style.css")).toExternalForm());

            modalStage.setScene(scene);
            modalStage.showAndWait();

            // If confirmed, delete account
            if (controller.isConfirmed()) {
                performDelete(account);
            }

        } catch (Exception e) {
            System.err.println("Failed to show delete confirmation: " + e.getMessage());
        }
    }

    private void performDelete(Account account) {
        try {
            // Load all accounts
            List<Account> accounts = AccountService.load(credentials);

            // Remove account
            accounts.removeIf(acc -> acc.getId().equals(account.getId()));

            // Save updated list
            AccountService.save(credentials, accounts);

            // Refresh display
            refreshAccounts();

        } catch (Exception e) {
            System.err.println("Failed to delete account: " + e.getMessage());
        }
    }

    @FXML
    private void filterAll() {
        applyFilter(Category.ALL);
    }

    @FXML
    private void filterEmail() {
        applyFilter(Category.EMAIL);
    }

    @FXML
    private void filterFacebook() {
        applyFilter(Category.FACEBOOK);
    }

    @FXML
    private void filterInstagram() {
        applyFilter(Category.INSTAGRAM);
    }

    @FXML
    private void filterLinkedin() {
        applyFilter(Category.LINKEDIN);
    }

    @FXML
    private void filterOther() {
        applyFilter(Category.OTHER);
    }

    private void applyFilter(Category category) {
        currentFilter = category;
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
            case EMAIL -> filterEmail;
            case FACEBOOK -> filterFacebook;
            case INSTAGRAM -> filterInstagram;
            case LINKEDIN -> filterLinkedin;
            case OTHER -> filterOther;
            case ALL -> filterAll;
        };

        activeButton.getStyleClass().remove("filter-btn");
        activeButton.getStyleClass().add("filter-btn-active");
    }

    private void filterAccounts() {
        String searchText = searchField.getText().toLowerCase();

        List<Account> filtered = allAccounts.stream()
                .filter(account -> {
                    // Filter by category
                    if (currentFilter != Category.ALL && !account.getCategory().equalsIgnoreCase(currentFilter.getDisplayName())) {
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
            controller.setCredentials(credentials);
            controller.setDashboardController(this);

            // Create modal stage
            Stage modalStage = new Stage();
            modalStage.initModality(Modality.APPLICATION_MODAL);
            modalStage.initOwner(addButton.getScene().getWindow());

            Scene scene = new Scene(form, 600, 850);
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/css/style.css")).toExternalForm());

            modalStage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/icons/security.png"))));
            modalStage.setScene(scene);
            modalStage.setTitle("Add Account");
            modalStage.setResizable(false);
            modalStage.showAndWait();

        } catch (Exception e) {
            System.err.println("Failed to load account form: " + e.getMessage());
        }
    }

    public void refreshAccounts() {
        loadAccounts();
    }


}