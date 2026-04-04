package com.ayoub.lockBox;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import java.util.Objects;

/**
 * Main application entry point for LockBox password manager.
 * <p>
 * Initializes the JavaFX application by loading the login screen, applying
 * custom fonts and CSS styling, and setting the application window properties.
 * The login screen is the first view displayed to the user.
 * <p>
 * <b>Application Initialization:</b>
 * <ul>
 *   <li>Loads login.fxml as the initial scene</li>
 *   <li>Applies global CSS stylesheet (style.css)</li>
 *   <li>Loads custom fonts (PatrickHand, BitcountSingle)</li>
 *   <li>Sets window icon (security.png)</li>
 *   <li>Configures initial window size (600x800, non-resizable)</li>
 * </ul>
 *
 * @author Ayoub Elhatab
 * LinkedIn: <a href="https://www.linkedin.com/in/ayoub-elhatab/">Ayoub Elhatab</a>
 * @see com.ayoub.lockBox.controllers.LoginController
 */
public class Main extends Application {

    /**
     * Initializes and displays the primary application window.
     * <p>
     * Loads the login FXML, applies CSS and custom fonts, sets the window
     * icon and title, and displays the non-resizable window at 600x800 pixels.
     * <p>
     * Called by the JavaFX runtime after {@link #main(String[])} invokes
     * {@link Application#launch(String...)}.
     *
     * @param primaryStage the primary stage provided by JavaFX runtime
     * @throws Exception if FXML loading or resource loading fails
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/fxml/login.fxml")));

        Scene scene = new Scene(root, 600, 800);

        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/css/style.css")).toExternalForm());

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