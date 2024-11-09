package ru.lingvofriend.tgbot;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import okhttp3.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.List;

public class YandexGPTClient {
  private final String yandexApiKey;
  private final OkHttpClient client;
  private final Gson gson;
  private static final String YANDEX_GPT_URL = "https://llm.api.cloud.yandex.net/foundationModels/v1/completion";
  private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
  private static final Logger logger = LogManager.getLogger();

  public YandexGPTClient(String yandexApiKey) {
    this.yandexApiKey = yandexApiKey;
    this.client = new OkHttpClient();
    this.gson = new Gson();
  }

  public String generateResponse(String userMessage) {
    String requestBody = gson.toJson(new YandexGPTRequest(userMessage));

    Request request = new Request.Builder()
        .url(YANDEX_GPT_URL)
        .addHeader("Authorization", "Api-Key " + yandexApiKey)
        .post(RequestBody.create(requestBody, JSON))
        .build();

    try (Response response = client.newCall(request).execute()) {
      if (!response.isSuccessful()) {
        logger.error("YandexGPT API error: {}", response.body().string());
        return "Sorry, I'm having trouble processing your request.";
      }

      YandexGPTResponse gptResponse = gson.fromJson(
          response.body().string(),
          YandexGPTResponse.class);
      return gptResponse.result.alternatives.get(0).message.text;
    } catch (IOException e) {
      logger.error("Failed to call YandexGPT API: {}", e.getMessage());
      return "Sorry, I encountered an error while processing your request.";
    }
  }

  // Helper classes for JSON serialization/deserialization
  private static class YandexGPTRequest {
    @SerializedName("modelUri")
    final String modelUri = "gpt://b1gq1u711tpqm1mskqc5/yandexgpt-lite";

    @SerializedName("completionOptions")
    final CompletionOptions completionOptions = new CompletionOptions();

    @SerializedName("messages")
    final List<Message> messages;

    YandexGPTRequest(String userMessage) {
      this.messages = List.of(new Message(userMessage));
    }

    static class CompletionOptions {
      @SerializedName("temperature")
      final double temperature = 0.6;

      @SerializedName("maxTokens")
      final int maxTokens = 2000;
    }

    static class Message {
      @SerializedName("role")
      final String role = "user";

      @SerializedName("text")
      final String text;

      Message(String text) {
        this.text = text;
      }
    }
  }

  private static class YandexGPTResponse {
    Result result;

    static class Result {
      List<Alternative> alternatives;

      static class Alternative {
        Message message;

        static class Message {
          String text;
        }
      }
    }
  }
}
