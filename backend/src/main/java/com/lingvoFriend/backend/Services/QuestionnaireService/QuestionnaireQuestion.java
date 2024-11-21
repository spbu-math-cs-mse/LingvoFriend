package com.lingvoFriend.backend.Services.QuestionnaireService;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class QuestionnaireQuestion {
    private String question;
    private List<String> options;
    
    public QuestionnaireQuestion(String question, List<String> options) {
        this.question = question;
        this.options = options;
    }

}
