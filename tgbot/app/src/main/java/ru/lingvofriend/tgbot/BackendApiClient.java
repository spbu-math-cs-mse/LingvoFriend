package ru.lingvofriend.tgbot;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;

public class BackendApiClient {
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private String authToken;
    private String username;
    private final String frontendUrl;
    private final String cookiePrefix = "__Host-";

    public BackendApiClient(String frontendUrl) {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
        this.frontendUrl = frontendUrl;
    }

    public void login(String username, String password) throws Exception {
        var authUserDto = new AuthUserDto(username, password);
        String jsonBody = objectMapper.writeValueAsString(authUserDto);

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(frontendUrl + "/api/auth/login"))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
            .build();


        System.out.println("Sending login request with body: " + jsonBody);

        HttpResponse<String> response = httpClient.send(request,
            HttpResponse.BodyHandlers.ofString());

        System.out.println("Response status: " + response.statusCode());
        System.out.println("Response body: " + response.body());

        if (response.statusCode() == 200) {
            this.authToken = extractToken(response);
            this.username = username;
        } else {
            throw new RuntimeException("Login failed: " + response.body());
        }
    }


    public String sendMessage(String message) throws Exception {
        var messageDto = new MessageDto("user", message);
        var requestBody = new RequestDto(username, messageDto);  // Use stored username
       
        String jsonBody = objectMapper.writeValueAsString(requestBody);
       
        System.out.println("Sending request with body: " + jsonBody);
       
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(frontendUrl + "/api/llm"))
            .header("Content-Type", "application/json")
            .header("Cookie", cookiePrefix + "auth-token=" + authToken)
            .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
            .build();

        HttpResponse<String> response = httpClient.send(request,
            HttpResponse.BodyHandlers.ofString());

        System.out.println("Response status: " + response.statusCode());
        System.out.println("Response body: " + response.body());

        if (response.statusCode() != 200) {
            throw new RuntimeException("Backend API error: " + response.statusCode() + " - " + response.body());
        }

        return response.body();
    }

    private String extractToken(HttpResponse<String> response) throws Exception {
        var responseJson = objectMapper.readTree(response.body());
       
        var tokenNode = responseJson.get("accessToken");
        if (tokenNode == null) {
            throw new RuntimeException("AccessToken field not found in response: " + response.body());
        }
       
        return tokenNode.asText();
    }

    public void loginWithTelegram(String id, String firstName, String username,
                                String photoUrl, String authDate, String hash) throws Exception {
        var telegramAuthDto = new TelegramAuthDto();
        telegramAuthDto.setId(id);
        telegramAuthDto.setFirst_name(firstName);
        telegramAuthDto.setUsername(username);
        telegramAuthDto.setPhoto_url(photoUrl);
        telegramAuthDto.setAuth_date(authDate);
        telegramAuthDto.setHash(hash);

        String jsonBody = objectMapper.writeValueAsString(telegramAuthDto);

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(frontendUrl + "/api/auth/telegram-login"))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
            .build();

        HttpResponse<String> response = httpClient.send(request,
            HttpResponse.BodyHandlers.ofString());


        System.out.println("Response status: " + response.statusCode());
        System.out.println("Response body: " + response.body());

        if (response.statusCode() == 200) {
            this.authToken = extractToken(response);
            this.username = "telegram_" + id;
        } else {
            throw new RuntimeException("Telegram login failed: " + response.body() + " " + response.statusCode());
        }
    }
}
