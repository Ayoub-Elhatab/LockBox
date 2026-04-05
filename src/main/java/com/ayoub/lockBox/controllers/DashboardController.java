package com.ayoub.lockBox.controllers;

import com.ayoub.lockBox.model.Account;
import com.ayoub.lockBox.enums.Category;
import com.ayoub.lockBox.security.SecureCredentials;
import com.ayoub.lockBox.service.AccountService;
import com.ayoub.lockBox.utils.AlertUtil;
import com.ayoub.lockBox.utils.IconUtil;
import com.ayoub.lockBox.utils.ModalUtil;
import com.ayoub.lockBox.utils.ToastUtil;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.Setter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import static com.ayoub.lockBox.utils.ClipboardUtil.copyToClipboard;

/**
 * Main dashboard controller for the LockBox password manager.
 * <p>
 * Manages the primary view displaying all saved accounts with search, filtering,
 * and CRUD operations. Provides an interactive account list with category-based
 * filtering, real-time search, and quick actions (copy, edit, delete).
 * <p>
 * This controller manages:
 * <ul>
 *   <li>Loading and displaying encrypted accounts from storage</li>
 *   <li>Real-time search across account labels, usernames, and notes</li>
 *   <li>Category filtering (All, Email, Facebook, Instagram, LinkedIn, Other)</li>
 *   <li>Account cards with action buttons and right-click context menu</li>
 *   <li>Empty state display when no accounts exist</li>
 *   <li>Navigation to add/edit/detail/delete modals</li>
 *   <li>Password copy to clipboard with toast notifications</li>
 * </ul>
 *
 * @see Account
 * @see AccountDetailController
 * @see AccountFormController
 * @see DeleteConfirmController
 *
 * @author Ayoub Elhatab.
 * LinkedIn: <a href="https://www.linkedin.com/in/ayoub-elhatab/">Ayoub Elhatab</a>
 */
public class DashboardController {

    @FXML private TextField searchField;
    @FXML private Button filterAll;
    @FXML private Button filterEmail;
    @FXML private Button filterFacebook;
    @FXML private Button filterInstagram;
    @FXML private Button filterLinkedin;
    @FXML private Button filterOther;
    @FXML private VBox accountListContainer;
    @FXML private Button addButton;

    @Setter
    private SecureCredentials credentials;

    private List<Account> allAccounts = new ArrayList<>();
    private Category currentFilter = Category.ALL;

    /**
     * Initializes the dashboard by setting up the search field listener.
     * <p>
     * Attaches a real-time text change listener to the search field that
     * automatically filters accounts as the user types. Called automatically
     * by JavaFX after FXML loading.
     */
    @FXML
    private void initialize() {
        searchField.textProperty().addListener((obs, oldVal, newVal) -> filterAccounts());
    }

    /**
     * Loads all accounts from encrypted storage and displays them.
     * <p>
     * Decrypts the lockBox.enc file using the provided credentials and
     * populates the account list. Displays an error alert if loading fails.
     */
    public void loadAccounts() {
        try {
            allAccounts = AccountService.load(credentials);
            displayAccounts(allAccounts);
        } catch (Exception e) {
            AlertUtil.showAlert("Error", "Failed to load accounts");
        }
    }

    /**
     * Displays the provided list of accounts in the UI.
     * <p>
     * Clears the current account list and creates account cards for each
     * provided account. Shows an empty state with icon and message if the
     * list is empty.
     *
     * @param accounts the list of accounts to display
     */
    private void displayAccounts(List<Account> accounts) {
        accountListContainer.getChildren().clear();

        if (accounts.isEmpty()) {
            VBox emptyState = new VBox(16);
            emptyState.setAlignment(Pos.CENTER);
            emptyState.setPadding(new Insets(190, 60, 60, 20));

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

    /**
     * Creates an interactive account card UI component.
     * <p>
     * Builds an HBox containing category icon, account info (label and username),
     * and action buttons (copy, edit, delete). Supports both left-click to view
     * details and right-click to show context menu.
     *
     * @param account the account to create a card for
     * @return HBox representing the account card
     */
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

        IconUtil.setIcon(icon, IconUtil.getCategoryIconPath(account.getCategory()));

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
        ContextMenu contextMenu = createContextMenu(account);

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

    /**
     * Creates a right-click context menu for an account card.
     * <p>
     * Provides menu items for View Details, Copy Password, Edit, and Delete actions.
     *
     * @param account the account to create a context menu for
     * @return ContextMenu with account actions
     */
    private ContextMenu createContextMenu(Account account) {
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
        return contextMenu;
    }

    /**
     * Creates an icon button with the specified icon and style class.
     * <p>
     * Helper method for creating consistent action buttons (copy, edit, delete).
     *
     * @param iconPath the resource path to the button icon
     * @param styleClass the CSS style class to apply
     * @return Button with icon graphic
     */
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

    /**
     * Filters accounts to show all categories.
     * FXML event handler for the "All" filter button.
     */
    @FXML
    private void filterAll() {
        applyFilter(Category.ALL);
    }

    /**
     * Filters accounts to show only Email category.
     * FXML event handler for the "Email" filter button.
     */
    @FXML
    private void filterEmail() {
        applyFilter(Category.EMAIL);
    }

    /**
     * Filters accounts to show only Facebook category.
     * FXML event handler for the "Facebook" filter button.
     */
    @FXML
    private void filterFacebook() {
        applyFilter(Category.FACEBOOK);
    }

    /**
     * Filters accounts to show only Instagram category.
     * FXML event handler for the "Instagram" filter button.
     */
    @FXML
    private void filterInstagram() {
        applyFilter(Category.INSTAGRAM);
    }

    /**
     * Filters accounts to show only LinkedIn category.
     * FXML event handler for the "LinkedIn" filter button.
     */
    @FXML
    private void filterLinkedin() {
        applyFilter(Category.LINKEDIN);
    }

    /**
     * Filters accounts to show only Other category.
     * FXML event handler for the "Other" filter button.
     */
    @FXML
    private void filterOther() {
        applyFilter(Category.OTHER);
    }

    /**
     * Applies the selected category filter and updates the UI.
     * <p>
     * Updates the current filter state, refreshes filter button styles,
     * and re-filters the account list.
     *
     * @param category the category filter to apply
     */
    private void applyFilter(Category category) {
        currentFilter = category;
        updateFilterButtons();
        filterAccounts();
    }

    /**
     * Updates filter button styles to reflect the current active filter.
     * <p>
     * Removes the active style class from all buttons and applies it only
     * to the currently selected filter button.
     */
    private void updateFilterButtons() {
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

    /**
     * Filters and displays accounts based on current category and search text.
     * <p>
     * Combines category filtering with real-time search across account labels,
     * usernames, and notes. Updates the displayed account list immediately.
     */
    private void filterAccounts() {
        String searchText = searchField.getText().toLowerCase();

        List<Account> filtered = allAccounts.stream()
                .filter(account -> {
                    if (currentFilter != Category.ALL && !account.getCategory().equalsIgnoreCase(currentFilter.getDisplayName())) {
                        return false;
                    }
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

    /**
     * Opens the account detail modal for the specified account.
     * <p>
     * Displays a modal window showing full account information with options
     * to copy credentials, edit, or delete the account.
     *
     * @param account the account to view details for
     */
    private void viewAccountDetail(Account account) {
        ModalUtil.showModal(
                "/fxml/account-detail.fxml",
                "Account Details",
                accountListContainer.getScene().getWindow(),
                600, 850,
                (AccountDetailController controller) -> {
                    controller.setCredentials(credentials);
                    controller.setDashboardController(this);
                    controller.setAccount(account);
                }
        );
    }

    /**
     * Copies the account's password to the system clipboard.
     * <p>
     * Displays a success toast notification at the bottom of the window
     * after copying the password.
     *
     * @param account the account whose password to copy
     */
    private void copyPassword(Account account) {
        copyToClipboard(account.getPassword());
        Stage stage = (Stage) accountListContainer.getScene().getWindow();
        ToastUtil.showToast(stage, "✓ Password copied!", 85);
    }

    /**
     * Opens the account edit modal for the specified account.
     * <p>
     * Displays the account form modal pre-filled with the account's current
     * data in edit mode. Updates are saved to encrypted storage.
     *
     * @param account the account to edit
     */
    private void editAccount(Account account) {
        ModalUtil.showModal(
                "/fxml/account-form.fxml",
                "Edit Account",
                accountListContainer.getScene().getWindow(),
                600, 850,
                (AccountFormController controller) -> {
                    controller.setCredentials(credentials);
                    controller.setDashboardController(this);
                    controller.setEditMode(account);
                }
        );
    }

    /**
     * Shows delete confirmation and removes the account if confirmed.
     * <p>
     * Workflow:
     * <ol>
     *   <li>Display transparent delete confirmation modal</li>
     *   <li>If confirmed, remove account from encrypted storage</li>
     *   <li>Refresh the account list to reflect changes</li>
     * </ol>
     * Displays an error alert if deletion fails.
     *
     * @param account the account to delete
     */
    private void deleteAccount(Account account) {
        DeleteConfirmController controller = ModalUtil.showTransparentModal(
                "/fxml/delete-confirmation.fxml",
                accountListContainer.getScene().getWindow(),
                c -> c.setAccountName(account.getLabel())
        );

        if (controller != null && controller.isConfirmed()) {
            try {
                List<Account> accounts = AccountService.load(credentials);
                accounts.removeIf(acc -> acc.getId().equals(account.getId()));
                AccountService.save(credentials, accounts);
                refreshAccounts();
            } catch (Exception e) {
                AlertUtil.showAlert("Error", "Failed to delete account");
            }
        }
    }

    /**
     * Opens the add account modal.
     * <p>
     * Displays the account form modal in add mode (empty fields).
     * New accounts are saved to encrypted storage upon submission.
     * FXML event handler for the FAB (Floating Action Button).
     */
    @FXML
    private void handleAddAccount() {
        ModalUtil.showModal(
                "/fxml/account-form.fxml",
                "Add Account",
                addButton.getScene().getWindow(),
                600, 850,
                (AccountFormController controller) -> {
                    controller.setCredentials(credentials);
                    controller.setDashboardController(this);
                }
        );
    }

    /**
     * Reloads all accounts from encrypted storage and refreshes the display.
     * <p>
     * Called after add/edit/delete operations to ensure the UI reflects
     * the latest data from storage.
     */
    public void refreshAccounts() {
        loadAccounts();
    }


}