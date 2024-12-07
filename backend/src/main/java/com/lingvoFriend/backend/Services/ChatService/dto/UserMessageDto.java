package com.lingvoFriend.backend.Services.ChatService.dto;

import com.lingvoFriend.backend.Services.ChatService.models.Message;

import lombok.Data;

@Data
public class UserMessageDto {
    private String username;
    private Message message;
}
