package com.lingvoFriend.backend.Services.AuthService.dto;

import lombok.Data;

@Data
public class TelegramAuthDto {
    private String id;
    private String first_name;
    private String username;
    private String photo_url;
    private String auth_date;
    private String hash;
}
