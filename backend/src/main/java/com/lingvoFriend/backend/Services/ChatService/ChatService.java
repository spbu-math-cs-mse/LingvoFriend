package com.lingvoFriend.backend.Services.ChatService;

import com.lingvoFriend.backend.Security.JwtGenerator;
import com.lingvoFriend.backend.Services.AuthService.models.UserModel;
import com.lingvoFriend.backend.Services.ChatService.dto.UserMessageDto;
import com.lingvoFriend.backend.Services.ChatService.dto.WordsReminderDto;
import com.lingvoFriend.backend.Services.ChatService.models.Message;
import com.lingvoFriend.backend.Services.UserService.UserService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChatService {
    @Autowired private UserService userService;
    @Autowired private LlmService llmService;
    @Autowired private LanguageLevelService languageLevelService;
    @Autowired private WordsReminderService wordsReminderService;
    @Autowired private LlmReminderService llmReminderService;
    @Autowired private JwtGenerator jwtGenerator;

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final Integer messageRetentionLimit = 50;

    public String chat(String token, UserMessageDto userMessageDto) {
        String username = jwtGenerator.getUsernameFromToken(token);
        UserModel user = userService.findOrThrow(username);

        if (userMessageDto.getMessage().getText().startsWith("/add")) {
            return manualUnknownWordsAddition(token, user, userMessageDto);
        }

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

        if (userService.countMeaningfulMessages(user) > messageRetentionLimit) {
            userService.cutOffMessages(user);
            llmReminderService.sendSystemReminder(user);
        }

        // if there is word, and it's time to show it (word's time is before now)
        // then we add the prompt for llm to use it
        if (!user.getUnknownWords().isEmpty()
                && user.getUnknownWords().first().getTime().isBefore(Instant.now())) {
            Message wordsReminderPrompt = wordsReminderService.addWordsReminderPrompt(user);
            userService.addMessageToUser(user, wordsReminderPrompt);
        }

        return llmService.generateLlmResponse(user);
    }

    private String manualUnknownWordsAddition(String token, UserModel user, UserMessageDto userMessageDto) {
        String messageText = userMessageDto.getMessage().getText().replaceFirst("^/add\\s*", "");
        String[] words = messageText.split("[,\\s]+");

        for (String word : words) {
            WordsReminderDto wordsReminderDto = new WordsReminderDto();
            wordsReminderDto.setWord(word.trim());
            wordsReminderService.saveUnknownWord(token, wordsReminderDto);
        }
    
        return "Unknown words have been added.";
    }
}
