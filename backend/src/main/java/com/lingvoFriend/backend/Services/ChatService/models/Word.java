package com.lingvoFriend.backend.Services.ChatService.models;

import lombok.Data;

import java.time.Instant;

@Data
public class Word implements Comparable<Word> {
    private String word;
    private Instant time = Instant.now();
    private int step;

    public Word(String word) {
        this.word = word;
    }

    @Override
    public int compareTo(Word other) {
        return this.time.compareTo(other.time);
    }
}
