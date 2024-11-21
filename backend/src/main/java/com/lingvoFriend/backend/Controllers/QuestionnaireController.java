package com.lingvoFriend.backend.Controllers;

import com.lingvoFriend.backend.Services.QuestionnaireService.QuestionnaireQuestion;
import com.lingvoFriend.backend.Services.QuestionnaireService.QuestionnaireService;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<QuestionnaireQuestion> startQuestionnaire(@RequestParam String userId) {
        try {
            return ResponseEntity.ok(questionnaireService.startQuestionnaire(userId));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/answer")
    public ResponseEntity<QuestionnaireQuestion> submitAnswer(
            @RequestParam String userId,
            @RequestParam String answer) {
        try {
            return ResponseEntity.ok(questionnaireService.handleResponse(userId, answer));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
