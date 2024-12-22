package ru.lingvofriend.backend.services;

import ru.lingvofriend.backend.model.UserModel;
import ru.lingvofriend.backend.model.Message;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LlmReminderService {
    @Autowired private UserService userService;

    public void sendSystemReminder(UserModel user) {
        String userGoals = userService.constructUserGoalsString(user);
        String userPreferences = userService.constructUserPreferencesString(user);
        String dialect = user.getDialect();

        String prompt = "Remember that you need to chat with user in English. " +
                    "You should not answer him in other languages except the situations when user asks for in explicitly (for example, when he asks for word translations, you can answer in Russian). " +
                    "Act like you are a native " + (dialect.equals("british") ? "British" : "American") + " speaker. " +
                    (getDialectMessage(dialect)) + " " +
                    "Remember that user has the following goals: " + userGoals + ". " +
                    "Remember that user's preferred topics are: " + userPreferences + ". " +
                    "Continue the conversation with user but take the information above into account for future communication.";

        Message topicMsg = new Message();
        topicMsg.setRole("system");
        topicMsg.setText(prompt);
        userService.addMessageToUser(user, topicMsg);
    }

    private String getDialectMessage(String dialect) {
        if (dialect.equals("british")) {
            return "Use British vocabulary (e.g., \"flat\" instead of \"apartment\", \"lorry\" instead of \"truck\") and grammar (e.g., \"present perfect\" for recent actions: \"I’ve just finished\").";
        } else {
            return "Use American vocabulary (e.g., \"apartment\" instead of \"flat\", \"truck\" instead of \"lorry\") and grammar (e.g., \"past simple\" for recent actions: \"I just finished\")";
        }
    }
}
