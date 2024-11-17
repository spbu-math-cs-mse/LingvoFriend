package com.lingvoFriend.backend.LLMService;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class RequestModel {
    private String modelUri;
    private CompletionOptions completionOptions;
    private List<Message> messages;

    @Data
    @AllArgsConstructor
    public static class CompletionOptions {
        private Boolean stream;
        private double temperature;
        private String maxTokens;
    }
}
