package com.lingvoFriend.backend.dto;

import lombok.Data;

// basically its json that we are waiting from user to login

@Data
public class LoginDto {
    private String username;
    private String password;
}
