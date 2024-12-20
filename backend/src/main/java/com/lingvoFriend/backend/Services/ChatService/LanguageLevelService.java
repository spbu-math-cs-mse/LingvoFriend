package com.lingvoFriend.backend.Services.ChatService;

import com.lingvoFriend.backend.Services.AuthService.models.UserModel;
import com.lingvoFriend.backend.Services.ChatService.models.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class LanguageLevelService {
    public boolean isEvaluated(UserModel user) {
        Integer status = user.getLevelEvaluationQuestionsAsked();
        return status > levelEvalQuestionsNumber;
    }

    public Message evaluate(UserModel user) {
        Integer status = user.getLevelEvaluationQuestionsAsked();
        if (status.equals(0)) {
            startEvaluation(user);
            String initialPrompt = "New user entered chat. Evaluate his level of English by asking questions. Now ask the first question.";
            Message initMessage = new Message();
            initMessage.setRole("system");
            initMessage.setText(initialPrompt);
            userService.addMessageToUser(user, initMessage);
            return llm.generateLlmResponse(user);
        }
        if (status < levelEvalQuestionsNumber) {
            questionsAskedPlusOne(user);
            String initialPrompt = "User answered, now ask one more question";
            Message initMessage = new Message();
            initMessage.setRole("system");
            initMessage.setText(initialPrompt);
            userService.addMessageToUser(user, initMessage);
            return llm.generateLlmResponse(user);
        }
        if (status.equals(levelEvalQuestionsNumber)) {
            String prompt = "User answered, now based on user's previous answers evaluate the user's level on the CEFR scale based on their answers. Just write A1, A2, B1, B2, C1, or C2 as an answer.";
            Message levelMessage = new Message();
            levelMessage.setRole("system");
            levelMessage.setText(prompt);
            userService.addMessageToUser(user, levelMessage);
            questionsAskedPlusOne(user);

            Message msg = llm.generateLlmResponse(user);
            userService.addMessageToUser(user, msg);
            String cefrLevel = extractCEFRLevel(msg.getText());
            setCefrLevel(user, cefrLevel);

            String userGoals = userService.constructUserGoalsString(user);
            String userPreferences = userService.constructUserPreferencesString(user);

            String topicPrompt = "Do not answer about CEFR level, it is no longer needed. " +
                    "From now on just chat with the user in English, notice that you should not answer in any other language except the situations when user asks for it explicitly. " +
                    "The user has the following goals: " + userGoals + ". " +
                    "The user's preferred topics are: " + userPreferences + ". " +
                    "Start the conversation by discussing one of these topics." +
                    "Talk with user as if you were native " + (userDialect.equals("british") ? "British" : "American") + " speaker.";
            
            Message topicMsg = new Message();
            topicMsg.setRole("system");
            topicMsg.setText(topicPrompt);
            userService.addMessageToUser(user, topicMsg);

            return llm.generateLlmResponse(user);
        }
        throw new IllegalStateException("evaluate must not be called if user has already been evaluated");
    }

    private void questionsAskedPlusOne(UserModel user) {
        user.setLevelEvaluationQuestionsAsked(user.getLevelEvaluationQuestionsAsked() + 1);
        user.setCefrLevel(null);
        userService.getUserRepository().save(user);
    }

    private void startEvaluation(UserModel user) {
        user.setLevelEvaluationQuestionsAsked(1);
        user.setCefrLevel(null);
        userService.getUserRepository().save(user);
    }

    private void setCefrLevel(UserModel user, String level) {
        user.setCefrLevel(level);
        userService.getUserRepository().save(user);
    }

    private String extractCEFRLevel(String text) {
        String[] cefrLevels = {"A1", "A2", "B1", "B2", "C1", "C2"};
        for (String level : cefrLevels) {
            if (text.toUpperCase().contains(level)) {
                return level;
            }
        }
        Random random = new Random();
        int index = random.nextInt(cefrLevels.length);
        return cefrLevels[index];
    }

    private final Integer levelEvalQuestionsNumber = 5;

    @Autowired
    private UserService userService;
    @Autowired
    private LlmService llm;
}
