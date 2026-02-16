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

public class ModalUtil {

    public static Stage createModal(String fxmlPath, String title, Window owner, int width, int height, boolean transparent) {
        try {
            FXMLLoader loader = new FXMLLoader(ModalUtil.class.getResource(fxmlPath));
            Parent root = loader.load();

            Stage modalStage = new Stage();
            modalStage.initModality(Modality.APPLICATION_MODAL);
            modalStage.initOwner(owner);

            if (transparent) {
                modalStage.initStyle(StageStyle.TRANSPARENT);
            }

            Scene scene = new Scene(root, width, height);
            scene.getStylesheets().add(Objects.requireNonNull(ModalUtil.class.getResource("/css/style.css")).toExternalForm());

            modalStage.getIcons().add(new Image(Objects.requireNonNull(ModalUtil.class.getResourceAsStream("/icons/security.png"))));
            modalStage.setScene(scene);
            modalStage.setTitle(title);
            modalStage.setResizable(false);

            return modalStage;
        } catch (Exception e) {
            System.err.println("Failed to create modal: " + e.getMessage());
            return null;
        }
    }

    public static <T> T showModalAndGetController(String fxmlPath, String title, Window owner, int width, int height) {
        try {
            FXMLLoader loader = new FXMLLoader(ModalUtil.class.getResource(fxmlPath));
            Parent root = loader.load();

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

            return loader.getController();
        } catch (Exception e) {
            System.err.println("Failed to show modal: " + e.getMessage());
            return null;
        }
    }
}