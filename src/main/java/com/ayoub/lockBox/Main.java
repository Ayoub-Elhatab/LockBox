package com.ayoub.lockBox;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import java.util.Objects;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/fxml/login.fxml")));

        Scene scene = new Scene(root, 600, 800);
//        Scene scene = new Scene(root, 600, 1000);

        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/css/style.css")).toExternalForm());

        // load fonts
        Font.loadFont(Objects.requireNonNull(getClass().getResource("/fonts/PatrickHand-Regular.ttf")).toExternalForm(), 14);
        Font.loadFont(Objects.requireNonNull(getClass().getResource("/fonts/BitcountSingle-Regular.ttf")).toExternalForm(), 14);

        primaryStage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/icons/security.png"))));

        primaryStage.setTitle("LockBox");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}