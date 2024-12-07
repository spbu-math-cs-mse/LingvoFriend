package com.lingvoFriend.backend.Services.ChatService.models;

import lombok.Data;

@Data
public class Message {
    private String role;
    private String text;

    public boolean isSystem() {
        return role.equals("system");
    }
}
