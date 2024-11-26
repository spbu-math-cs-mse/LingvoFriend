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
    //uncomment for local tests
    //@CrossOrigin(origins = "http://localhost:3000")
    @PostMapping("/llm")
    public Mono<ResponseEntity<String>> sendRequestToLLM(@RequestBody LlmRequestDto llmRequestDto)
            throws JsonProcessingException {

        Dotenv dotenv = Dotenv.load();

        String username = llmRequestDto.getUsername();
        Message userMessage = llmRequestDto.getMessage();

        if (!(Objects.equals(userMessage.getRole(), "user")
                || (Objects.equals(userMessage.getRole(), "system")))) {
            return Mono.just(
                    ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bad message role"));
        }

        List<Message> updatedMessages;

        chatService.addMessageToUser(username, userMessage);
        if (!chatService.isEvaluating(username) && !chatService.isEvaluatingFinished(username)) {
            chatService.startEvaluation(username);
            String initialPrompt = "New user entered chat. Evaluate his level of English by asking questions. Now ask the first question.";
            Message initMessage = new Message();
            initMessage.setRole("system");
            initMessage.setText(initialPrompt);
            updatedMessages = chatService.addMessageToUser(username, initMessage);
        }
        else if (chatService.isEvaluating(username)) {
            chatService.questionsAskedPlusOne(username);
            String initialPrompt = "User answered, now ask one more question";
            Message initMessage = new Message();
            initMessage.setRole("system");
            initMessage.setText(initialPrompt);
            updatedMessages = chatService.addMessageToUser(username, initMessage);
        }
        else if (chatService.isEvaluatinCEFR(username)) {
            String prompt = "User answered, now based on user's previous answers evaluate the user's level on the CEFR scale based on their answers. Just write A1, A2, B1, B2, C1, or C2 as an answer.";
            Message levelMessage = new Message();
            levelMessage.setRole("system");
            levelMessage.setText(prompt);
            List<Message> updatedMessages1 = chatService.addMessageToUser(username, levelMessage);
            chatService.questionsAskedPlusOne(username);
            
            LlmRequestModel request = new LlmRequestModel(
                MODEL_URI,
                new LlmRequestModel.CompletionOptions(false, 0.6, "2000"),
                updatedMessages1
            );
            
            return llmRequest.sendPostRequest(API_KEY, FOLDER_ID, request)
                .flatMap(res -> {
                    Message msg = res.getResult().getAlternatives().get(0).getMessage();
                    String cefrLevel = chatService.extractCEFRLevel(msg.getText());
                    chatService.setCefrLevel(username, cefrLevel);
            
                    String topicPrompt = "Do not answer about CEFR level, it is no longer needed. From now on just chat with user in English, start the conversation by talking about watermalons";
                    Message topicMsg = new Message();
                    topicMsg.setRole("system"); 
                    topicMsg.setText(topicPrompt);
                    List<Message> updatedMessages2 = chatService.addMessageToUser(username, topicMsg);

                    LlmRequestModel topicRequest = new LlmRequestModel(
                        MODEL_URI,
                        new LlmRequestModel.CompletionOptions(false, 0.6, "2000"),
                        updatedMessages2
                    );

                        return llmRequest.sendPostRequest(API_KEY, FOLDER_ID, topicRequest)
                            .map(topicRes -> {
                                Message topicResponse = topicRes.getResult().getAlternatives().get(0).getMessage();
                                topicResponse.setText("Thanks for completing questionnaire! Now let's talk and improve your English!\n\n" + topicResponse.getText());
                                chatService.addMessageToUser(username, topicResponse);
                                return ResponseEntity.ok(topicResponse.getText());
                            });
                    })
                .onErrorResume(e -> {
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body("An error occurred during the evaluation."));
                });
                
        }
        else {
             updatedMessages = chatService.getMessagesByUsername(username);
        }


        return Mono.defer(
                        () -> {
                            if (Objects.equals(userMessage.getRole(), "system")) {
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

                                                chatService.addMessageToUser(username, msg);
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

    //uncomment for local tests
    //@CrossOrigin(origins = "http://localhost:3000")
    @GetMapping("/history/{username}")
    public ResponseEntity<List<Message>> getChatHistory(@PathVariable String username) {
        try {
            List<Message> allMessages = chatService.getMessagesByUsername(username);
            List<Message> notSystemMessages = allMessages.stream()
                .filter(message -> !"system".equalsIgnoreCase(message.getRole()))
                .toList();
            return ResponseEntity.ok(notSystemMessages);
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/test")
    public void test() {}
}
