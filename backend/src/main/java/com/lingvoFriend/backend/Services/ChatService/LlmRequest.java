package com.lingvoFriend.backend.Services.ChatService;

import com.lingvoFriend.backend.Services.ChatService.models.LlmRequestModel;
import com.lingvoFriend.backend.Services.ChatService.models.LlmResponseModel;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;

@Service
public class LlmRequest {

    private final WebClient webClient;

    public LlmRequest(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("https://llm.api.cloud.yandex.net").build();
    }

    public Mono<LlmResponseModel> sendPostRequest(
            String APIKEY, String folderID, LlmRequestModel requestModel) {
        return webClient
                .post()
                .uri("/foundationModels/v1/completion")
                .headers(
                        headers -> {
                            headers.add("Content-Type", "application/json");
                            headers.add("Authorization", "Api-key " + APIKEY);
                            headers.add("x-folder-id", folderID);
                        })
                .bodyValue(requestModel)
                .retrieve()
                .bodyToMono(LlmResponseModel.class);
    }
}
