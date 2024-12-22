package ru.lingvofriend.backend.controllers;

import ru.lingvofriend.backend.repositories.UserRepository;
import ru.lingvofriend.backend.security.JwtGenerator;
import ru.lingvofriend.backend.model.UserModel;
import ru.lingvofriend.backend.services.QuestionnaireQuestion;
import ru.lingvofriend.backend.services.QuestionnaireService;
import ru.lingvofriend.backend.dto.QuestionnaireGoalsDto;
import ru.lingvofriend.backend.dto.QuestionnaireInterestsDto;

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
    @Autowired private JwtGenerator jwtGenerator;

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
            @CookieValue("__Host-auth-token") String token,
            @RequestBody QuestionnaireGoalsDto questionnaireGoalsDto) {
        String username = jwtGenerator.getUsernameFromToken(token);
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
            @CookieValue("__Host-auth-token") String token,
            @RequestBody QuestionnaireInterestsDto questionnaireInterestsDto) {
        String username = jwtGenerator.getUsernameFromToken(token);
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
