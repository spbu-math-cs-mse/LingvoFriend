package com.lingvoFriend.backend.Services.ChatService.models;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Message {
    private String role;
    private String text;

    public boolean isSystem() {
        return role.equals("system");
    }
}
