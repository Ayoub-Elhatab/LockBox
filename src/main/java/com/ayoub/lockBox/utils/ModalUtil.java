package com.ayoub.lockBox.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Utility class for displaying modal dialogs.
 * <p>
 * Provides two types of modal windows:
 * <ul>
 *   <li><b>Standard Modal:</b> Decorated window with title bar and custom size</li>
 *   <li><b>Transparent Modal:</b> Borderless overlay for confirmation dialogs</li>
 * </ul>
 * <p>
 * Both modal types are application-modal, blocking interaction with the parent
 * window until dismissed. All modals automatically apply the application's
 * CSS stylesheet and security icon.
 * <p>
 * Uses a {@link Consumer} callback pattern to configure the loaded controller
 * before displaying the modal.
 *
 * @author Ayoub Elhatab
 * LinkedIn: <a href="https://www.linkedin.com/in/ayoub-elhatab/">Ayoub Elhatab</a>
 */
public class ModalUtil {

    /**
     * Displays a standard modal dialog with title bar and custom dimensions.
     * <p>
     * Loads the FXML, applies CSS styling, sets the security window icon,
     * and invokes the controller setup callback before showing the modal.
     * The modal is non-resizable and blocks until closed.
     *
     * @param <T> the controller type
     * @param fxmlPath the resource path to the FXML file
     * @param title the window title
     * @param owner the parent window
     * @param width the modal width in pixels
     * @param height the modal height in pixels
     * @param controllerSetup callback to configure the controller (e.g., set data)
     */
    public static <T> void showModal(String fxmlPath, String title, Window owner, int width, int height, Consumer<T> controllerSetup) {
        try {
            FXMLLoader loader = new FXMLLoader(ModalUtil.class.getResource(fxmlPath));
            Parent root = loader.load();

            T controller = loader.getController();
            controllerSetup.accept(controller);

            Stage modalStage = new Stage();
            modalStage.initModality(Modality.APPLICATION_MODAL);
            modalStage.initOwner(owner);

            Scene scene = new Scene(root, width, height);
            scene.getStylesheets().add(Objects.requireNonNull(ModalUtil.class.getResource("/css/style.css")).toExternalForm());

            modalStage.getIcons().add(new Image(Objects.requireNonNull(ModalUtil.class.getResourceAsStream("/icons/security.png"))));
            modalStage.setScene(scene);
            modalStage.setTitle(title);
            modalStage.setResizable(false);
            modalStage.showAndWait();

        } catch (Exception e) {
            System.err.println("Failed to show modal: " + e.getMessage());
        }
    }

    /**
     * Displays a transparent borderless modal overlay.
     * <p>
     * Used for confirmation dialogs and overlays that need to appear on top
     * of existing content without a traditional window frame. Loads FXML,
     * applies CSS, and invokes the controller setup callback.
     * <p>
     * The modal has no title bar or window decorations (StageStyle.TRANSPARENT).
     *
     * @param <T> the controller type
     * @param fxmlPath the resource path to the FXML file
     * @param owner the parent window
     * @param controllerSetup callback to configure the controller (e.g., set confirmation message)
     */
    public static <T> void showTransparentModal(String fxmlPath, Window owner, Consumer<T> controllerSetup) {
        try {
            FXMLLoader loader = new FXMLLoader(ModalUtil.class.getResource(fxmlPath));
            Parent root = loader.load();

            T controller = loader.getController();
            controllerSetup.accept(controller);

            Stage modalStage = new Stage();
            modalStage.initModality(Modality.APPLICATION_MODAL);
            modalStage.initOwner(owner);
            modalStage.initStyle(StageStyle.TRANSPARENT);

            Scene scene = new Scene(root);
            scene.getStylesheets().add(Objects.requireNonNull(ModalUtil.class.getResource("/css/style.css")).toExternalForm());

            modalStage.setScene(scene);
            modalStage.showAndWait();

        } catch (Exception e) {
            System.err.println("Failed to show modal: " + e.getMessage());
        }
    }
}