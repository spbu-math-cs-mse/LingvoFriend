package com.lingvoFriend.backend.Controllers;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lingvoFriend.backend.Services.ChatService.ChatService;
import com.lingvoFriend.backend.Services.ChatService.WordsReminderService;
import com.lingvoFriend.backend.Services.ChatService.dto.UserMessageDto;
import com.lingvoFriend.backend.Services.ChatService.dto.WordsReminderDto;
import com.lingvoFriend.backend.Services.ChatService.models.Message;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api")
class ChatController {

    @Autowired private ChatService chatService;
    @Autowired private WordsReminderService wordsReminderService;

    @PostMapping("/llm")
    public ResponseEntity<String> chat(@RequestBody UserMessageDto userMessageDto) {
        if (!(Objects.equals(userMessageDto.getMessage().getRole(), "user")
                || Objects.equals(userMessageDto.getMessage().getRole(), "system"))) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bad message role");
        }

        return processRequest(userMessageDto);
    }

    @PostMapping("/saveUnknownWord")
    public ResponseEntity<String> saveUnknownWord(@RequestBody WordsReminderDto wordsReminderDto) {
        try {
            wordsReminderService.saveUnknownWord(wordsReminderDto);
            return ResponseEntity.ok("Word successfully saved");
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/history/{username}")
    public ResponseEntity<List<Message>> getChatHistory(
            HttpServletRequest request, @PathVariable String username) {
        try {
            List<Message> messages = chatService.getHistory(username);
            return ResponseEntity.ok(messages);
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    private ResponseEntity<String> processRequest(UserMessageDto userMessageDto) {
        try {
            String responseText = chatService.chat(userMessageDto);
            return ResponseEntity.ok(responseText);
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
