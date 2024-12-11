package com.lingvoFriend.backend.Services.ChatService;

import com.lingvoFriend.backend.Services.AuthService.models.UserModel;
import com.lingvoFriend.backend.Services.ChatService.dto.UserMessageDto;
import com.lingvoFriend.backend.Services.ChatService.models.Message;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChatService {
    @Autowired private UserService userService;
    @Autowired private LlmService llmService;
    @Autowired private LanguageLevelService languageLevelService;
    private LlmReminderService llmReminderService;
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final Integer remainderFrequency = 25;

    public String chat(UserMessageDto userMessageDto) {
        UserModel user = userService.findOrThrow(userMessageDto.getUsername());
        userService.addMessageToUser(user, userMessageDto.getMessage());

        if (userMessageDto.getMessage().isSystem()) return "successfully added system message";

        Message llmMessage = generateResponseImpl(user);
        userService.addMessageToUser(user, llmMessage);
        return llmMessage.getText();
    }

    public List<Message> getHistory(String username) {
        UserModel user = userService.findOrThrow(username);
        return user.getMessages().stream()
                .filter(message -> !message.isSystem())
                .collect(Collectors.toList());
    }

    private Message generateResponseImpl(UserModel user) {
        if (!languageLevelService.isEvaluated(user)) {
            logger.info(
                    "User {} is not evaluated. Delegating to LanguageLevelService", user.getName());
            return languageLevelService.evaluate(user);
        }
        if (user.getMessages().size() % remainderFrequency == 0) {
            llmReminderService.sendSystemReminder(user);
        }
        return llmService.generateLlmResponse(user);
    }
}
