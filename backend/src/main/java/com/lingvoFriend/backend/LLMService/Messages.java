package com.lingvoFriend.backend.LLMService;

import lombok.Data;

import java.util.List;

@Data
public class Messages {
    private List<Message> messages;
}
