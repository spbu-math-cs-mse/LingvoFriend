package ru.lingvofriend.backend.services;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class QuestionnaireResponse {
    private String userId;
    private String name;
    private String gender;
    private Integer age;
    private String goals;
    private String englishExperience;
    private List<String> interests;

}
