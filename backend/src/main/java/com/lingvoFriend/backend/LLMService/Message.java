package com.lingvoFriend.backend.LLMService;

import lombok.Data;

@Data
public class Message {
    private String role;
    private String text;
}
