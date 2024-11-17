package com.lingvoFriend.backend.Controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.lingvoFriend.backend.LLMService.LLMService;
import com.lingvoFriend.backend.LLMService.RequestModel;
import com.lingvoFriend.backend.dto.LLMRequestDto;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@AllArgsConstructor
@RequestMapping("/api")
class Controller {

    private final LLMService llmService;

    @PostMapping("/llm")
    public Mono<String> sendRequestToLLM(@RequestBody LLMRequestDto llmRequestDto)
            throws JsonProcessingException {

        RequestModel request =
                new RequestModel(
                        llmRequestDto.getModelUri(),
                        new RequestModel.CompletionOptions(false, 0.6, "2000"),
                        llmRequestDto.getMessages());

        return llmService
                .sendPostRequest(llmRequestDto.getApiKey(), llmRequestDto.getFolderId(), request)
                .map(res -> res.getResult().getAlternatives().get(0).getMessage().getText());
    }
}
