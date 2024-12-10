package com.lingvoFriend.backend.Services.ChatService;

import com.lingvoFriend.backend.Services.AuthService.models.UserModel;
import com.lingvoFriend.backend.Services.ChatService.dto.WordsReminderDto;
import com.lingvoFriend.backend.Services.ChatService.models.Message;
import com.lingvoFriend.backend.Services.ChatService.models.Word;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class WordsReminderService {
    @Autowired private UserService userService;
    public static final int storageCapacity = 100;
    public static final int reminderSteps = 5;

    public void saveUnknownWord(WordsReminderDto wordsReminderDto) {
        UserModel user = userService.findOrThrow(wordsReminderDto.getUsername());
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

        switch (currentStep) {
            case 1:
                word.setTime(word.getTime().plus(Duration.ofDays(1)));
                break;
            case 2:
                word.setTime(word.getTime().plus(Duration.ofDays(3)));
                break;
            case 3:
                word.setTime(word.getTime().plus(Duration.ofDays(5)));
                break;
            case 4:
                word.setTime(word.getTime().plus(Duration.ofDays(7)));
                break;
        }

        word.setStep(word.getStep() + 1);

        return word;
    }
}