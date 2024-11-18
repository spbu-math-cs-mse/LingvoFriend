package com.lingvoFriend.backend.Controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.lingvoFriend.backend.Services.ChatService.ChatService;
import com.lingvoFriend.backend.Services.ChatService.LlmRequest;
import com.lingvoFriend.backend.Services.ChatService.dto.LlmRequestDto;
import com.lingvoFriend.backend.Services.ChatService.models.LlmRequestModel;
import com.lingvoFriend.backend.Services.ChatService.models.Message;

import io.github.cdimascio.dotenv.Dotenv;

import lombok.AllArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api")
class Controller {

    private final LlmRequest llmRequest;
    private final ChatService chatService;

    @PostMapping("/llm")
    public Mono<ResponseEntity<String>> sendRequestToLLM(@RequestBody LlmRequestDto llmRequestDto)
            throws JsonProcessingException {

        Dotenv dotenv = Dotenv.load();

        return Mono.defer(
                        () -> {
                            List<Message> updatedMessages =
                                    chatService.addMessageToUser(
                                            llmRequestDto.getUsername(),
                                            llmRequestDto.getMessage());

                            LlmRequestModel request =
                                    new LlmRequestModel(
                                            System.getenv("MODEL_URI"),
                                            new LlmRequestModel.CompletionOptions(
                                                    false, 0.6, "2000"),
                                            updatedMessages);

                            return llmRequest
                                    .sendPostRequest(
                                            dotenv.get("API_KEY"), dotenv.get("FOLDER_ID"), request)
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
