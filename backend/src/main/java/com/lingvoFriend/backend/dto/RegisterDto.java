package com.lingvoFriend.backend.dto;

import lombok.Data;

// basically its json that we are waiting from user to register

@Data
public class RegisterDto {
    private String username;
    private String password;
}
