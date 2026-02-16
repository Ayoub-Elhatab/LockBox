package com.ayoub.lockBox.enums;

import lombok.Getter;

@Getter
public enum Category {
    ALL("All"),
    EMAIL("Email"),
    FACEBOOK("Facebook"),
    INSTAGRAM("Instagram"),
    LINKEDIN("LinkedIn"),
    OTHER("Other");

    private final String displayName;

    Category(String displayName) {
        this.displayName = displayName;
    }

    public static Category fromString(String text) {
        for (Category category : Category.values()) {
            if (category.displayName.equalsIgnoreCase(text)) {
                return category;
            }
        }
        return OTHER;
    }
}