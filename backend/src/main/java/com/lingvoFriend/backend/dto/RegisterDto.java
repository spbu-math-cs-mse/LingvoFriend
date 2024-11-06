package com.lingvoFriend.backend.dto;

import lombok.Data;

// basically its json that we are waiting from user to register

@Data
public class RegisterDto {
    public String username;
    public String password;
}
