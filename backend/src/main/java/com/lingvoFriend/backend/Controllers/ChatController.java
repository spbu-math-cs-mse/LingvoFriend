package com.lingvoFriend.backend.Controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.lingvoFriend.backend.Services.ChatService.ChatService;
import com.lingvoFriend.backend.Services.ChatService.LlmRequest;
import com.lingvoFriend.backend.Services.ChatService.dto.LlmRequestDto;
import com.lingvoFriend.backend.Services.ChatService.models.LlmRequestModel;
import com.lingvoFriend.backend.Services.ChatService.models.LlmResponseModel;
import com.lingvoFriend.backend.Services.ChatService.models.Message;

import io.github.cdimascio.dotenv.Dotenv;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api")
class ChatController {

    private final RestTemplate restTemplate;
    private final LlmRequest llmRequest;
    private final ChatService chatService;
    private final String MODEL_URI;
    private final String API_KEY;
    private final String FOLDER_ID;

    @Autowired
    public ChatController(
            RestTemplate restTemplate, LlmRequest llmRequest, ChatService chatService) {
        this.restTemplate = restTemplate;
        this.llmRequest = llmRequest;
        this.chatService = chatService;

        Dotenv dotenv = Dotenv.load();
        this.MODEL_URI = dotenv.get("MODEL_URI");
        this.API_KEY = dotenv.get("API_KEY");
        this.FOLDER_ID = dotenv.get("FOLDER_ID");
    }

    @PostMapping("/llm")
    public ResponseEntity<String> sendRequestToLLM(@RequestBody LlmRequestDto llmRequestDto)
            throws JsonProcessingException {

        if (!(Objects.equals(llmRequestDto.getMessage().getRole(), "user")
                || Objects.equals(llmRequestDto.getMessage().getRole(), "system"))) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bad message role");
        }

        return processRequest(llmRequestDto);
    }

    public ResponseEntity<String> processRequest(LlmRequestDto llmRequestDto)
            throws JsonProcessingException {
        try {
            List<Message> updatedMessages =
                    chatService.addMessageToUser(
                            llmRequestDto.getUsername(), llmRequestDto.getMessage());

            if (Objects.equals(llmRequestDto.getMessage().getRole(), "system")) {
                return ResponseEntity.status(HttpStatus.CREATED)
                        .body("System message successfully saved");
            }

            LlmRequestModel request =
                    new LlmRequestModel(
                            MODEL_URI,
                            new LlmRequestModel.CompletionOptions(false, 0.6, "2000"),
                            updatedMessages);

            LlmResponseModel response = llmRequest.sendPostRequest(API_KEY, FOLDER_ID, request);

            if (response != null && response.getResult() != null) {
                Message msg = response.getResult().getAlternatives().get(0).getMessage();
                chatService.addMessageToUser(llmRequestDto.getUsername(), msg);
                return ResponseEntity.ok(msg.getText());
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Empty response from LLM API");
            }

        } catch (Exception e) {
            if (e instanceof BadCredentialsException) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred");
        }
    }

    @GetMapping("/history/{username}")
    public ResponseEntity<List<Message>> getChatHistory(
            HttpServletRequest request, @PathVariable String username) {
        try {
            List<Message> messages = chatService.getMessagesByUsername(username);
            return ResponseEntity.ok(messages);
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
