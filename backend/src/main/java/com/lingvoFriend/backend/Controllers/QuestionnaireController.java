package com.lingvoFriend.backend.Controllers;

import com.lingvoFriend.backend.Services.QuestionnaireService.QuestionnaireQuestion;
import com.lingvoFriend.backend.Services.QuestionnaireService.QuestionnaireService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/questionnaire")
public class QuestionnaireController {
    private final QuestionnaireService questionnaireService;

    public QuestionnaireController(QuestionnaireService questionnaireService) {
        this.questionnaireService = questionnaireService;
    }

    @PostMapping("/start")
    public QuestionnaireQuestion startQuestionnaire(@RequestParam String userId) {
        return questionnaireService.startQuestionnaire(userId);
    }

    @PostMapping("/answer")
    public QuestionnaireQuestion submitAnswer(
            @RequestParam String userId,
            @RequestParam String answer
    ) {
        return questionnaireService.handleResponse(userId, answer);
    }
}
