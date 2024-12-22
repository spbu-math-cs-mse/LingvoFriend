package ru.lingvofriend.backend.model;

import lombok.Data;

@Data
public class Message {
    private String role;
    private String text;

    public boolean isSystem() {
        return role.equals("system");
    }
}
