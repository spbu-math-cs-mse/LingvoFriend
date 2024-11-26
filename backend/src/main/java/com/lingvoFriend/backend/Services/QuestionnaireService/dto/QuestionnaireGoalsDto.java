package com.lingvoFriend.backend.Services.QuestionnaireService.dto;

import lombok.Data;

import java.util.List;

@Data
public class QuestionnaireGoalsDto {
    private String username;
    private List<String> goals;
}
