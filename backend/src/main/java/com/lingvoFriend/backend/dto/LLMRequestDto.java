package com.lingvoFriend.backend.dto;

import com.lingvoFriend.backend.LLMService.Message;
import lombok.Data;

import java.util.List;

@Data
public class LLMRequestDto {
    private String modelUri;
    private String apiKey;
    private String folderId;
    private List<Message> messages;
}
