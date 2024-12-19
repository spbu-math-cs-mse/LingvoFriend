package com.lingvoFriend.backend.Services.AuthService.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponseDto {
    private String accessToken;
    private String errorMessage;
}
