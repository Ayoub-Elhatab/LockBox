package com.ayoub.lockBox.utils;

import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.util.Objects;

public class IconUtil {

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

    public static String getCategoryIconPath(String category) {
        return switch (category.toLowerCase()) {
            case "email" -> "/icons/gmail.png";
            case "facebook" -> "/icons/facebook.png";
            case "instagram" -> "/icons/instagram.png";
            case "linkedin" -> "/icons/linkedin.png";
            default -> "/icons/other.png";
        };
    }

    public static void setIcon(ImageView imageView, String iconPath) {
        try {
            imageView.setImage(new Image(Objects.requireNonNull(IconUtil.class.getResourceAsStream(iconPath))));
        } catch (Exception e) {
            System.err.println("Icon not found: " + iconPath);
        }
    }
}
