package com.lingvoFriend.backend.Services.ChatService;

import com.lingvoFriend.backend.Security.JwtGenerator;
import com.lingvoFriend.backend.Services.AuthService.models.UserModel;
import com.lingvoFriend.backend.Services.ChatService.dto.WordsReminderDto;
import com.lingvoFriend.backend.Services.ChatService.models.Message;
import com.lingvoFriend.backend.Services.ChatService.models.Word;
import com.lingvoFriend.backend.Services.UserService.UserService;

import com.lingvoFriend.backend.Services.UserService.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

@Service
public class WordsReminderService {
    @Autowired private UserService userService;
    @Autowired private JwtGenerator jwtGenerator;

    public static final int storageCapacity = 100;
    public static final int reminderSteps = 7;

    public void saveUnknownWord(String token, WordsReminderDto wordsReminderDto) {
        String username = jwtGenerator.getUsernameFromToken(token);
        UserModel user = userService.findOrThrow(username);
        Word unknownWord = new Word(wordsReminderDto.getWord());

        // when user clicks on unknownWord for the first time
        // it'll be shown not earlier than 30 minutes from now as the first reminderStep
        unknownWord.setTime(unknownWord.getTime().plus(Duration.ofMinutes(30)));
        unknownWord.setStep(1);

        userService.addUnknownWordToUser(user, unknownWord);
    }

    public Message addWordsReminderPrompt(UserModel user) {

        // here we are pulling out the least word on date and remove it from tree
        Word word = user.getUnknownWords().first();
        user.getUnknownWords().remove(word);

        // if it's the last step then we should ignore it
        if (word.getStep() == reminderSteps) {
            return null;
        }

        // here we are changing the word's step, providing a new date when it has to be shown
        // and adding it back to the tree
        user.getUnknownWords().add(changeWordReminderStep(word));

        String prompt = String.format("Use word '%s' in your next response", word.getWord());
        Message message = new Message();
        message.setRole("system");
        message.setText(prompt);
        return message;
    }

    public Word changeWordReminderStep(Word word) {
        int currentStep = word.getStep();

        List<Duration> periodList =
                List.of(
                        Duration.ofDays(1),
                        Duration.ofDays(2),
                        Duration.ofDays(4),
                        Duration.ofDays(8),
                        Duration.ofDays(16),
                        Duration.ofDays(32)); // 63 days ~ 2 months

        switch (currentStep) {
            case 1:
                word.setTime(Instant.now().plus(periodList.get(0)));
                break;
            case 2:
                word.setTime(Instant.now().plus(periodList.get(1)));
                break;
            case 3:
                word.setTime(Instant.now().plus(periodList.get(2)));
                break;
            case 4:
                word.setTime(Instant.now().plus(periodList.get(3)));
                break;
            case 5:
                word.setTime(Instant.now().plus(periodList.get(4)));
                break;
            case 6:
                word.setTime(Instant.now().plus(periodList.get(5)));
                break;
        }

        word.setStep(word.getStep() + 1);

        return word;
    }
}
