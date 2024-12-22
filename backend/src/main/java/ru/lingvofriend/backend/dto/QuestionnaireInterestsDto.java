package ru.lingvofriend.backend.dto;

import lombok.Data;

import java.util.List;

@Data
public class QuestionnaireInterestsDto {
    private List<String> interests;
}
