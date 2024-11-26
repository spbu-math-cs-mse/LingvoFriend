package com.lingvoFriend.backend.Services.QuestionnaireService.dto;

import lombok.Data;

import java.util.List;

@Data
public class QuestionnaireInterestsDto {
    private String username;
    private List<String> interests;
}
