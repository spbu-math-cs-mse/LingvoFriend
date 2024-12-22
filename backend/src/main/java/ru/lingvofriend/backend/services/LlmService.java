package ru.lingvofriend.backend.services;

import ru.lingvofriend.backend.model.UserModel;
import ru.lingvofriend.backend.model.LlmRequestModel;
import ru.lingvofriend.backend.model.LlmResponseModel;

import ru.lingvofriend.backend.model.Message;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class LlmService {

    public LlmService() {
        Dotenv dotenv = Dotenv.load();
        this.apiKey = dotenv.get("API_KEY");
        this.folderID = dotenv.get("FOLDER_ID");
        this.modelURI = dotenv.get("MODEL_URI");
    }

    public LlmResponseModel sendPostRequest(LlmRequestModel requestModel) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        headers.set("Authorization", "Api-key " + apiKey);
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

    public Message generateLlmResponse(UserModel user) {
        LlmRequestModel request =
            new LlmRequestModel(
                modelURI,
                new LlmRequestModel.CompletionOptions(false, 0.6, "2000"),
                user.getMessages());
        LlmResponseModel response = sendPostRequest(request);
        if (response == null || response.getResult() == null)
            throw new IllegalStateException("LLM returned empty response");
        return response.getResult().getAlternatives().get(0).getMessage();
    }

    @Autowired
    private RestTemplate restTemplate;
    private final String apiKey;
    private final String folderID;
    private final String modelURI;
}
