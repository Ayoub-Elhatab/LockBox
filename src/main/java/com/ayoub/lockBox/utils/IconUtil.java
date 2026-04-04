package com.ayoub.lockBox.utils;

import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.util.Objects;

/**
 * Utility class for icon management and loading.
 * <p>
 * Provides centralized icon handling for password visibility toggles, category
 * icons, and general image views. Handles icon loading with proper error handling
 * and consistent sizing.
 * <p>
 * Supported category icons:
 * <ul>
 *   <li>Email → gmail.png</li>
 *   <li>Facebook → facebook.png</li>
 *   <li>Instagram → instagram.png</li>
 *   <li>LinkedIn → linkedin.png</li>
 *   <li>Other → other.png (default)</li>
 * </ul>
 *
 * @author Ayoub Elhatab
 * LinkedIn: <a href="https://www.linkedin.com/in/ayoub-elhatab/">Ayoub Elhatab</a>
 */
public class IconUtil {

    /**
     * Updates a password toggle button's icon.
     * <p>
     * Used to switch between eye.png (password hidden) and closed-eye.png
     * (password visible). Sets icon to 18x18 pixels.
     *
     * @param togglePasswordBtn the button to update
     * @param iconPath the resource path to the new icon
     */
    public static void updatePasswordIcon(Button togglePasswordBtn, String iconPath) {
        try {
            ImageView icon = new ImageView(new Image(Objects.requireNonNull(IconUtil.class.getResourceAsStream(iconPath))));
            icon.setFitWidth(18);
            icon.setFitHeight(18);
            icon.setPreserveRatio(true);
            togglePasswordBtn.setGraphic(icon);
        } catch (Exception e) {
            System.err.println("Failed to load icon: " + iconPath);
        }
    }

    /**
     * Returns the icon resource path for a given category.
     * <p>
     * Maps category names (case-insensitive) to their corresponding icon paths.
     * Returns /icons/other.png as the default if category is not recognized.
     *
     * @param category the category name (email, facebook, instagram, linkedin, other)
     * @return the resource path to the category icon
     */
    public static String getCategoryIconPath(String category) {
        return switch (category.toLowerCase()) {
            case "email" -> "/icons/gmail.png";
            case "facebook" -> "/icons/facebook.png";
            case "instagram" -> "/icons/instagram.png";
            case "linkedin" -> "/icons/linkedin.png";
            default -> "/icons/other.png";
        };
    }

    /**
     * Sets an icon image on an ImageView.
     * <p>
     * Loads the icon from resources and applies it to the ImageView.
     * Logs an error if the icon cannot be loaded but does not throw an exception.
     *
     * @param imageView the ImageView to set the icon on
     * @param iconPath the resource path to the icon
     */
    public static void setIcon(ImageView imageView, String iconPath) {
        try {
            imageView.setImage(new Image(Objects.requireNonNull(IconUtil.class.getResourceAsStream(iconPath))));
        } catch (Exception e) {
            System.err.println("Icon not found: " + iconPath);
        }
    }
}
