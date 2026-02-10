package com.ayoub.lockBox.model;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ToString
@EqualsAndHashCode
public class Account {
    private String id;
    private String label;
    private String category;
    private String username;
    private String password;
    private String notes;

    public Account() {
        this.id = generateId();
    }

    public Account(String label, String category, String username, String password, String notes) {
        this.id = generateId();
        this.label = label;
        this.category = category;
        this.username = username;
        this.password = password;
        this.notes = notes;
    }

    private String generateId() {
        return System.currentTimeMillis() + "_" + (int)(Math.random() * 1000);
    }
}