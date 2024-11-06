package com.lingvoFriend.backend.dto;

import lombok.Data;

// basically its json that we are waiting from user to login

@Data
public class LoginDto {
    public String username;
    public String password;
}
