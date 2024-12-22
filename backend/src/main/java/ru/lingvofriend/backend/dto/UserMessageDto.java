package ru.lingvofriend.backend.dto;

import ru.lingvofriend.backend.model.Message;

import lombok.Data;

@Data
public class UserMessageDto {
    private Message message;
}
