package com.lingvoFriend.backend.Services.ChatService;

import com.lingvoFriend.backend.Services.AuthService.models.UserModel;
import com.lingvoFriend.backend.Services.ChatService.models.Message;
import com.lingvoFriend.backend.Services.UserService.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LlmReminderService {
    @Autowired private UserService userService;

    public void sendSystemReminder(UserModel user) {
        String userGoals = userService.constructUserGoalsString(user);
        String userPreferences = userService.constructUserPreferencesString(user);

        String prompt =
                "Remember that you need to chat with user in English. "
                        + "You should not answer him in other languages except the situations when user asks for in explicitly (for example, when he asks for word translations, you can answer in Russian). "
                        + "Act like you are a native British speaker. "
                        + "Remember that user has the following goals: "
                        + userGoals
                        + ". "
                        + "Remember that user's preferred topics are: "
                        + userPreferences
                        + ". "
                        + "Continue the conversation with user but take the information above into account for future communication.";

        Message topicMsg = new Message();
        topicMsg.setRole("system");
        topicMsg.setText(prompt);
        userService.addMessageToUser(user, topicMsg);
    }
}
