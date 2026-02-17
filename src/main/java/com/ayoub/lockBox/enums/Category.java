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

}