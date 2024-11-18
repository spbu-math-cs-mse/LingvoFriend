package com.lingvoFriend.backend.Controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.lingvoFriend.backend.Services.ChatService.ChatService;
import com.lingvoFriend.backend.Services.ChatService.LlmRequest;
import com.lingvoFriend.backend.Services.ChatService.dto.LlmRequestDto;
import com.lingvoFriend.backend.Services.ChatService.models.LlmRequestModel;
import com.lingvoFriend.backend.Services.ChatService.models.Message;

import io.github.cdimascio.dotenv.Dotenv;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api")
class Controller {

    private final LlmRequest llmRequest;
    private final ChatService chatService;
    private final String MODEL_URI;
    private final String API_KEY;
    private final String FOLDER_ID;

    public Controller(LlmRequest llmRequest, ChatService chatService) {
        this.llmRequest = llmRequest;
        this.chatService = chatService;

        Dotenv dotenv = Dotenv.load();
        this.MODEL_URI = dotenv.get("MODEL_URI");
        this.API_KEY = dotenv.get("API_KEY");
        this.FOLDER_ID = dotenv.get("FOLDER_ID");
    }

    @PostMapping("/llm")
    public Mono<ResponseEntity<String>> sendRequestToLLM(@RequestBody LlmRequestDto llmRequestDto)
            throws JsonProcessingException {

        Dotenv dotenv = Dotenv.load();

        if (!(Objects.equals(llmRequestDto.getMessage().getRole(), "user")
                || (Objects.equals(llmRequestDto.getMessage().getRole(), "system")))) {
            return Mono.just(
                    ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bad message role"));
        }

        return Mono.defer(
                        () -> {
                            List<Message> updatedMessages =
                                    chatService.addMessageToUser(
                                            llmRequestDto.getUsername(),
                                            llmRequestDto.getMessage());

                            if (Objects.equals(llmRequestDto.getMessage().getRole(), "system")) {
                                return Mono.just(
                                        ResponseEntity.status(HttpStatus.CREATED)
                                                .body("System message successfully saved"));
                            }

                            LlmRequestModel request =
                                    new LlmRequestModel(
                                            MODEL_URI,
                                            new LlmRequestModel.CompletionOptions(
                                                    false, 0.6, "2000"),
                                            updatedMessages);

                            return llmRequest
                                    .sendPostRequest(API_KEY, FOLDER_ID, request)
                                    .map(
                                            res -> {
                                                Message msg =
                                                        res.getResult()
                                                                .getAlternatives()
                                                                .get(0)
                                                                .getMessage();

                                                chatService.addMessageToUser(
                                                        llmRequestDto.getUsername(), msg);
                                                return ResponseEntity.ok(msg.getText());
                                            });
                        })
                .onErrorResume(
                        e -> {
                            if (e instanceof BadCredentialsException) {
                                return Mono.just(
                                        ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                                .body(e.getMessage()));
                            }
                            return Mono.just(
                                    ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                            .body("An unexpected error occurred"));
                        });
    }

    @GetMapping("/test")
    public void test() {}
}
