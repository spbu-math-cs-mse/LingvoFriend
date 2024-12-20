package com.lingvoFriend.backend.Controllers;

import lombok.Getter;

import com.lingvoFriend.backend.Services.UserService.UserService;
import com.lingvoFriend.backend.Security.JwtGenerator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/preferences")
public class LanguagePreferencesController {
    @Autowired private UserService userService;
    @Autowired private JwtGenerator jwtGenerator;

    @GetMapping("/dialect/{username}")
    public ResponseEntity<String> getLanguagePreference(@CookieValue("__Host-auth-token") String token) {
        try {
            String username = jwtGenerator.getUsernameFromToken(token);
            String dialect = userService.getDialect(username);
            return ResponseEntity.ok(dialect != null ? dialect : "No dialect set");
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to fetch preference: " + e.getMessage());
        }
    }

    @PostMapping("/dialect/{username}")
    public ResponseEntity<String> saveLanguagePreference(
            @CookieValue("__Host-auth-token") String token,
            @RequestParam String dialect) {
        try {
            String username = jwtGenerator.getUsernameFromToken(token);
            userService.setDialect(username, dialect);

            return ResponseEntity.ok("Dialect preference saved successfully");
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to fetch preference: " + e.getMessage());
        }
    }
}