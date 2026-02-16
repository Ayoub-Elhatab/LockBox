package com.ayoub.lockBox.model;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Getter;
import lombok.Setter;
import java.util.UUID;

@Getter
@Setter
@ToString(exclude = {"password", "username", "notes"})
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Account {
    @EqualsAndHashCode.Include
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

    private String generateId() { return UUID.randomUUID().toString(); }
}