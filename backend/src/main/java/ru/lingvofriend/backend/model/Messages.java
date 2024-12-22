package ru.lingvofriend.backend.model;

import lombok.Data;

import java.util.List;

@Data
public class Messages {
    private List<Message> messages;
}
