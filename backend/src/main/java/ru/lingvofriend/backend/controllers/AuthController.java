package ru.lingvofriend.backend.controllers;

import ru.lingvofriend.backend.services.AuthService;
import ru.lingvofriend.backend.dto.AuthUserDto;
import ru.lingvofriend.backend.dto.TelegramAuthDto;

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
