package com.lingvoFriend.backend.Controllers;

import com.lingvoFriend.backend.Repositories.UserRepository;
import com.lingvoFriend.backend.Services.AuthService.models.UserModel;
import com.lingvoFriend.backend.Services.QuestionnaireService.QuestionnaireQuestion;
import com.lingvoFriend.backend.Services.QuestionnaireService.QuestionnaireService;
import com.lingvoFriend.backend.Services.QuestionnaireService.dto.QuestionnaireGoalsDto;

import com.lingvoFriend.backend.Services.QuestionnaireService.dto.QuestionnaireInterestsDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/questionnaire")
public class QuestionnaireController {
    @Autowired private QuestionnaireService questionnaireService;
    @Autowired private UserRepository userRepository;
    @Autowired private MongoTemplate mongoTemplate;

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

        Query query = new Query(Criteria.where("_id").is(user.getId()));
        Update update = new Update().set("goals", goals);
        mongoTemplate.updateFirst(query, update, UserModel.class);

        return ResponseEntity.ok("Goals saved successfully");
    }

    @PostMapping("/saveInterests")
    public ResponseEntity<String> saveInterests(
            @RequestBody QuestionnaireInterestsDto questionnaireInterestsDto) {
        String username = questionnaireInterestsDto.getUsername();
        List<String> interests = questionnaireInterestsDto.getInterests();

        UserModel user =
                userRepository
                        .findByUsername(username)
                        .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        user.setInterests(interests);

        Query query = new Query(Criteria.where("_id").is(user.getId()));
        Update update = new Update().set("interests", interests);
        mongoTemplate.updateFirst(query, update, UserModel.class);

        return ResponseEntity.ok("Interests saved successfully");
    }
}
