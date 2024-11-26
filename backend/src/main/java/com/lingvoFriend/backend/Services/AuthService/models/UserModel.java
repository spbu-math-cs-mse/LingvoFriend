package com.lingvoFriend.backend.Services.AuthService.models;

import com.lingvoFriend.backend.Services.ChatService.models.Message;
import com.lingvoFriend.backend.Services.QuestionnaireService.QuestionState;

import com.lingvoFriend.backend.Services.QuestionnaireService.QuestionnaireResponse;
import lombok.Data;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

// users table in database

@Data
@Document("Users")
public class UserModel {
    @Id
    private String id;

    private String username;
    private String password;

    @DBRef
    private List<RoleModel> roles;

    private List<Message> messages;

    // Add questionnaire fields
    private String name;
    private String gender;
    private Integer age;
    private String goals;
    private String englishExperience;
    private List<String> interests;
    private QuestionState questionState = QuestionState.NOT_STARTED;

    private Integer levelEvaluationQuestionsAsked = 0;
    private String cefrLevel;

    public UserModel(
            String username, String password, List<RoleModel> roles, List<Message> messages) {
        this.username = username;
        this.password = password;
        this.roles = roles;
        this.messages = messages;
    }

    public QuestionnaireResponse getQuestionnaireResponse() {
        QuestionnaireResponse response = new QuestionnaireResponse();
        response.setUserId(id);
        response.setAge(age);
        response.setGoals(goals);
        response.setGender(gender);
        response.setEnglishExperience(englishExperience);
        response.setInterests(interests);
        return response;
    }
}
