package com.lingvoFriend.backend.Controllers;

import com.lingvoFriend.backend.Services.AuthService.AuthService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

import lombok.AllArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/jwt")
@AllArgsConstructor
public class JwtController {

    private final AuthService authService;

    @GetMapping("/validate")
    public ResponseEntity<String> validateToken(@CookieValue("__Host-auth-token") String token) {
        return authService.validateToken(token);
    }

    @GetMapping("/clear")
    public ResponseEntity<String> clearToken(HttpServletResponse response) {
        Cookie cookie = new Cookie("__Host-auth-token", null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        response.addCookie(cookie);

        return ResponseEntity.ok("Token cleared successfully");
    }
}
