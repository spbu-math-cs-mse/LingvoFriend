package com.lingvoFriend.backend.Services.ChatService;

import com.lingvoFriend.backend.Services.ChatService.models.LlmRequestModel;
import com.lingvoFriend.backend.Services.ChatService.models.LlmResponseModel;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class LlmRequest {

    private final RestTemplate restTemplate;

    public LlmRequest(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public LlmResponseModel sendPostRequest(
            String APIKEY, String folderID, LlmRequestModel requestModel) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        headers.set("Authorization", "Api-key " + APIKEY);
        headers.set("x-folder-id", folderID);

        HttpEntity<LlmRequestModel> entity = new HttpEntity<>(requestModel, headers);

        ResponseEntity<LlmResponseModel> response =
                restTemplate.exchange(
                        "https://llm.api.cloud.yandex.net/foundationModels/v1/completion",
                        HttpMethod.POST,
                        entity,
                        LlmResponseModel.class);

        return response.getBody();
    }
}
