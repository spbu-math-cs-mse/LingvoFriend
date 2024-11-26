package com.lingvoFriend.backend.Controllers;

import com.lingvoFriend.backend.Repositories.UserRepository;
import com.lingvoFriend.backend.Services.AuthService.models.UserModel;
import com.lingvoFriend.backend.Services.QuestionnaireService.QuestionnaireQuestion;
import com.lingvoFriend.backend.Services.QuestionnaireService.QuestionnaireService;
import com.lingvoFriend.backend.Services.QuestionnaireService.dto.QuestionnaireGoalsDto;

import com.lingvoFriend.backend.Services.QuestionnaireService.dto.QuestionnaireInterestsDto;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/questionnaire")
public class QuestionnaireController {
    private final QuestionnaireService questionnaireService;
    final UserRepository userRepository;

    public QuestionnaireController(
            QuestionnaireService questionnaireService, UserRepository userRepository) {
        this.questionnaireService = questionnaireService;
        this.userRepository = userRepository;
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
            @RequestParam String userId, @RequestParam String answer) {
        try {
            return ResponseEntity.ok(questionnaireService.handleResponse(userId, answer));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/saveGoals")
    public ResponseEntity<String> saveGoals(
            @RequestBody QuestionnaireGoalsDto questionnaireGoalsDto) {
        String username = questionnaireGoalsDto.getUsername();
        List<String> goals = questionnaireGoalsDto.getGoals();

        UserModel user =
                userRepository
                        .findByUsername(username)
                        .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        user.setGoals(goals);

        userRepository.save(user);

        return ResponseEntity.ok("Goals saved successfully");
    }

    @PostMapping("/saveInterests")
    public ResponseEntity<String> saveInterests(
            @RequestBody QuestionnaireInterestsDto questionnaireInterestsDto) {
        String username = questionnaireInterestsDto.getUsername();
        List<String> goals = questionnaireInterestsDto.getInterests();

        UserModel user =
                userRepository
                        .findByUsername(username)
                        .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        user.setInterests(goals);

        userRepository.save(user);

        return ResponseEntity.ok("Interests saved successfully");
    }
}
