package com.lingvoFriend.backend.LLMService;

import lombok.Data;

import java.util.List;

@Data
public class ResponseModel {
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
