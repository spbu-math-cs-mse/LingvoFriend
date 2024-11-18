package com.lingvoFriend.backend.Services.AuthService.models;

import lombok.Data;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

// roles table in database

@Data
@Document("Roles")
public class RoleModel {
    @Id private String id;
    private String roleName;

    public RoleModel(String roleName) {
        this.roleName = roleName;
    }
}
