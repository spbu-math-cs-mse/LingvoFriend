package com.lingvoFriend.backend.Controllers;

import com.lingvoFriend.backend.Services.AuthService.AuthService;
import com.lingvoFriend.backend.Services.AuthService.dto.LoginDto;
import com.lingvoFriend.backend.Services.AuthService.dto.RegisterDto;

import lombok.AllArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// here is the logic and mapping for AuthControllers

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
@CrossOrigin
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterDto registerDto) {
        return authService.register(registerDto);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginDto loginDto) {
        return authService.login(loginDto);
    }
}
