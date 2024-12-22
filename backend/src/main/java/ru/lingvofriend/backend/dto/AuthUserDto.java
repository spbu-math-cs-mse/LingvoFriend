package ru.lingvofriend.backend.dto;

import lombok.Data;

@Data
public class AuthUserDto {
    private String username;
    private String password;
}
