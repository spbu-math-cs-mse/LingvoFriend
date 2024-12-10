package com.lingvoFriend.backend.Services.ChatService;

import com.lingvoFriend.backend.Services.AuthService.models.UserModel;
import com.lingvoFriend.backend.Services.ChatService.dto.UserMessageDto;
import com.lingvoFriend.backend.Services.ChatService.models.Message;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class ChatService {
    @Autowired private UserService userService;
    @Autowired private LlmService llmService;
    @Autowired private LanguageLevelService languageLevelService;
    @Autowired private WordsReminderService wordsReminderService;
    private final Logger logger = LoggerFactory.getLogger(getClass());

    public String chat(UserMessageDto userMessageDto) {
        UserModel user = userService.findOrThrow(userMessageDto.getUsername());
        userService.addMessageToUser(user, userMessageDto.getMessage());

        if (userMessageDto.getMessage().isSystem()) return "successfully added system message";

        // if there is word, and it's time to show it (word's time is before now)
        // then we add the prompt for llm to use it
        if (!user.getUnknownWords().isEmpty()
                && user.getUnknownWords().first().getTime().isBefore(Instant.now())) {
            Message wordsReminderPrompt = wordsReminderService.addWordsReminderPrompt(user);
            userService.addMessageToUser(user, wordsReminderPrompt);
        }

        Message llmMessage = generateResponseImpl(user);
        userService.addMessageToUser(user, llmMessage);
        return llmMessage.getText();
    }

    public List<Message> getHistory(String username) {
        UserModel user = userService.findOrThrow(username);
        return user.getMessages();
    }

    private Message generateResponseImpl(UserModel user) {
        if (!languageLevelService.isEvaluated(user)) {
            logger.info(
                    "User {} is not evaluated. Delegating to LanguageLevelService", user.getName());
            return languageLevelService.evaluate(user);
        }
        return llmService.generateLlmResponse(user);
    }
}
