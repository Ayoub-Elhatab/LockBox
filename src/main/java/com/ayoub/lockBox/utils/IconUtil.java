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
}
