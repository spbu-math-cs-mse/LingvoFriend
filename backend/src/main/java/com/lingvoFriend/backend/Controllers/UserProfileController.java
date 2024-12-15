package com.lingvoFriend.backend.Controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lingvoFriend.backend.Services.ChatService.UserService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/profile")
class UserProfileController {

    @Autowired
    private UserService userService;

    @GetMapping("/goals/{username}")
    public ResponseEntity<List<String>> getGoalsInfo(
        HttpServletRequest request, @PathVariable String username) {
        try {
            List<String> goals = userService.getGoals(username);
            return ResponseEntity.ok(goals);
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/interests/{username}")
    public ResponseEntity<List<String>> getInterestsInfo(
        HttpServletRequest request, @PathVariable String username) {
        try {
            List<String> interests = userService.getInterests(username);
            return ResponseEntity.ok(interests);
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/level/{username}")
    public ResponseEntity<String> getLevelInfo(
        HttpServletRequest request, @PathVariable String username) {
        try {
            String level = userService.getLevel(username);
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
