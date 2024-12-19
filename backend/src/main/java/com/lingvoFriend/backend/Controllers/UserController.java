package com.lingvoFriend.backend.Controllers;

import com.lingvoFriend.backend.Security.JwtGenerator;
import com.lingvoFriend.backend.Services.UserService.UserService;
import com.lingvoFriend.backend.Services.UserService.dto.UserProfileDto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
class UserController {
    @Autowired private UserService userService;
    @Autowired private JwtGenerator jwtGenerator;

    @GetMapping("/getProfileData")
    public ResponseEntity<UserProfileDto> getUserData(
            @CookieValue("__Host-auth-token") String token) {
        String username = jwtGenerator.getUsernameFromToken(token);
        List<String> goals = userService.getGoals(username);
        List<String> interests = userService.getInterests(username);
        String cefrLevel = userService.getCefrLevel(username);

        return ResponseEntity.ok(new UserProfileDto(username, goals, interests, cefrLevel));
    }

    @GetMapping("/username")
    public ResponseEntity<String> getUsernameInfo(@CookieValue("__Host-auth-token") String token) {
        try {
            String username = jwtGenerator.getUsernameFromToken(token);
            return ResponseEntity.ok(username);
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/goals")
    public ResponseEntity<List<String>> getGoalsInfo(
            @CookieValue("__Host-auth-token") String token) {
        try {
            String username = jwtGenerator.getUsernameFromToken(token);
            List<String> goals = userService.getGoals(username);
            return ResponseEntity.ok(goals);
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/interests")
    public ResponseEntity<List<String>> getInterestsInfo(
            @CookieValue("__Host-auth-token") String token) {
        try {
            String username = jwtGenerator.getUsernameFromToken(token);
            List<String> interests = userService.getInterests(username);
            return ResponseEntity.ok(interests);
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/level")
    public ResponseEntity<String> getLevelInfo(@CookieValue("__Host-auth-token") String token) {
        try {
            String username = jwtGenerator.getUsernameFromToken(token);
            String level = userService.getCefrLevel(username);
            if (level == null || level.isEmpty()) {
                return ResponseEntity.ok("unknown");
            }
            return ResponseEntity.ok(level);
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
