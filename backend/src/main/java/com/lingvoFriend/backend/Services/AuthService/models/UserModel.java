package com.lingvoFriend.backend.Services.AuthService.models;

import com.lingvoFriend.backend.Services.ChatService.models.Message;

import lombok.Data;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

// users table in database

@Data
@Document("Users")
public class UserModel {
    @Id private String id;

    private String username;
    private String password;

    @DBRef private List<RoleModel> roles;

    private List<Message> messages;

    public UserModel(
            String username, String password, List<RoleModel> roles, List<Message> messages) {
        this.username = username;
        this.password = password;
        this.roles = roles;
        this.messages = messages;
    }
}
