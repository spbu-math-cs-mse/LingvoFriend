package com.lingvoFriend.backend.Controllers;

import com.lingvoFriend.backend.Services.AuthService.AuthService;
import com.lingvoFriend.backend.Services.AuthService.dto.AuthUserDto;
import com.lingvoFriend.backend.Services.AuthService.dto.TelegramAuthDto;

import jakarta.servlet.http.HttpServletResponse;

import lombok.AllArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// here is the logic and mapping for AuthControllers

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> register(
            @RequestBody AuthUserDto authUserDto, HttpServletResponse response) {
        return authService.register(authUserDto, response);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody AuthUserDto authUserDto, HttpServletResponse response) {
        return authService.login(authUserDto, response);
    }

    @PostMapping("/telegram-login")
    public ResponseEntity<?> telegramLogin(
            @RequestBody TelegramAuthDto telegramAuth,
            HttpServletResponse response) {
        return authService.telegramLogin(telegramAuth, response);
    }
}
