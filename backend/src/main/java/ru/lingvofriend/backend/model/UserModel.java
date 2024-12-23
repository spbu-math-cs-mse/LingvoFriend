package ru.lingvofriend.backend.model;

import ru.lingvofriend.backend.services.QuestionState;
import ru.lingvofriend.backend.services.QuestionnaireResponse;

import lombok.Data;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.*;

// users table in database

@Data
@Document("Users")
public class UserModel {
    @Id private String id;

    private String username;
    private String password;

    @DBRef private List<RoleModel> roles;

    private List<Message> messages;

    private TreeSet<Word> unknownWords = new TreeSet<>(Comparator.comparing(Word::getTime));

    // current questionnaire fields
    private List<String> goals;
    private List<String> interests;

    // up for debate
    private String name;
    private String gender;
    private Integer age;
    private String englishExperience;
    private QuestionState questionState = QuestionState.NOT_STARTED;

    private Integer levelEvaluationQuestionsAsked = 0;
    private String cefrLevel;

    private String dialect = "british";

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
        response.setGoals(goals.toString());
        response.setGender(gender);
        response.setEnglishExperience(englishExperience);
        response.setInterests(interests);
        return response;
    }

    public String getDialect() {
        return dialect;
    }

    public void setDialect(String dialect) {
        this.dialect = dialect;
    }
}
