package ru.lingvofriend.backend.dto;

import lombok.Data;

import java.util.List;

@Data
public class QuestionnaireGoalsDto {
    private List<String> goals;
}
