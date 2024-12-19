package com.lingvoFriend.backend.Controllers;

import com.lingvoFriend.backend.Security.JwtGenerator;
import com.lingvoFriend.backend.Services.ChatService.ChatService;
import com.lingvoFriend.backend.Services.ChatService.WordsReminderService;
import com.lingvoFriend.backend.Services.ChatService.dto.UserMessageDto;
import com.lingvoFriend.backend.Services.ChatService.dto.WordsReminderDto;
import com.lingvoFriend.backend.Services.ChatService.models.Message;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api")
class ChatController {

    @Autowired private ChatService chatService;
    @Autowired private WordsReminderService wordsReminderService;
    @Autowired private JwtGenerator jwtGenerator;

    @PostMapping("/llm")
    public ResponseEntity<String> chat(
            @CookieValue("__Host-auth-token") String token,
            @RequestBody UserMessageDto userMessageDto) {
        if (!(Objects.equals(userMessageDto.getMessage().getRole(), "user")
                || Objects.equals(userMessageDto.getMessage().getRole(), "system"))) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bad message role");
        }

        try {
            String responseText = chatService.chat(token, userMessageDto);
            return ResponseEntity.ok(responseText);
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/saveUnknownWord")
    public ResponseEntity<String> saveUnknownWord(
            @CookieValue("__Host-auth-token") String token,
            @RequestBody WordsReminderDto wordsReminderDto) {
        try {
            wordsReminderService.saveUnknownWord(token, wordsReminderDto);
            return ResponseEntity.ok("Word successfully saved");
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/history")
    public ResponseEntity<List<Message>> getChatHistory(
            @CookieValue("__Host-auth-token") String token) {
        try {
            String username = jwtGenerator.getUsernameFromToken(token);
            List<Message> messages = chatService.getHistory(username);
            return ResponseEntity.ok(messages);
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
