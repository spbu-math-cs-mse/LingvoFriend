package com.lingvoFriend.backend.Services.ChatService.models;

import lombok.Data;

import java.util.List;

@Data
public class Messages {
    private List<Message> messages;
}
