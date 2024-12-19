package ru.lingvofriend.tgbot;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;

public class BackendApiClient {
    private final String backendUrl;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private String authToken;
    private String username;

    public BackendApiClient(String backendUrl) {
        this.backendUrl = backendUrl;
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    public void login(String username, String password) throws Exception {
        var authUserDto = new AuthUserDto(username, password);
        String jsonBody = objectMapper.writeValueAsString(authUserDto);

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(backendUrl + "/api/auth/login"))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
            .build();

        HttpResponse<String> response = httpClient.send(request, 
            HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            this.authToken = extractToken(response);
            this.username = username;
        } else {
            throw new RuntimeException("Login failed: " + response.body() + " - " + response.statusCode());
        }
    }

    public String sendMessage(String message) throws Exception {
        var messageDto = new MessageDto("user", message);
        var requestBody = new RequestDto(username, messageDto);
        
        String jsonBody = objectMapper.writeValueAsString(requestBody);
        
        System.out.println("Sending request with body: " + jsonBody);
        
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(backendUrl + "/api/llm"))
            .header("Content-Type", "application/json")
            .header("Cookie", "__Host-auth-token=" + authToken)
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
}

class MessageDto {
    private String role;
    private String text;

    public MessageDto(String role, String text) {
        this.role = role;
        this.text = text;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}

class UserMessageDto {
    private MessageDto message;

    public UserMessageDto(MessageDto message) {
        this.message = message;
    }

    public MessageDto getMessage() {
        return message;
    }

    public void setMessage(MessageDto message) {
        this.message = message;
    }
}

class AuthUserDto {
    private String username;
    private String password;

    public AuthUserDto(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}

class RequestDto {
    private String username;
    private MessageDto message;

    public RequestDto(String username, MessageDto message) {
        this.username = username;
        this.message = message;
    }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public MessageDto getMessage() { return message; }
    public void setMessage(MessageDto message) { this.message = message; }
} 
