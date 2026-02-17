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

public class ModalUtil {

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