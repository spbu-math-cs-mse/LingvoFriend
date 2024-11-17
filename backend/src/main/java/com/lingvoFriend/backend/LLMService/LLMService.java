package com.lingvoFriend.backend.LLMService;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;

@Service
public class LLMService {

    private final WebClient webClient;

    public LLMService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("https://llm.api.cloud.yandex.net").build();
    }

    public Mono<ResponseModel> sendPostRequest(
            String APIKEY, String folderID, RequestModel requestModel) {
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
                .bodyToMono(ResponseModel.class);
    }
}
