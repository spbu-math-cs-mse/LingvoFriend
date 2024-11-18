package com.lingvoFriend.backend.Services.ChatService.models;

import lombok.Data;

import java.util.List;

@Data
public class LlmResponseModel {
    private Result result;

    @Data
    public static class Result {
        private List<Alternatives> alternatives;
        private Usage usage;
        private String modelVersion;
    }

    @Data
    public static class Alternatives {
        private Message message;
        private String status;
    }

    @Data
    public static class Usage {
        private String inputTextTokens;
        private String completionTokens;
        private String totalTokens;
    }
}
