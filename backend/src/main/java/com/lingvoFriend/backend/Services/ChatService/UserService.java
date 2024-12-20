package com.lingvoFriend.backend.Services.ChatService;

import com.lingvoFriend.backend.Repositories.UserRepository;
import com.lingvoFriend.backend.Services.AuthService.models.UserModel;
import com.lingvoFriend.backend.Services.ChatService.models.Message;
import com.lingvoFriend.backend.Services.ChatService.models.Word;

import lombok.Getter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.StringJoiner;

@Service
public class UserService {

    @Getter @Autowired private UserRepository userRepository;

    public UserModel findOrThrow(String username) {
        return userRepository
                .findByUsername(username)
                .orElseThrow(
                        () -> new BadCredentialsException("user %s not found".formatted(username)));
    }

    public Optional<UserModel> find(String username) {
        return userRepository.findByUsername(username);
    }

    public void addMessageToUser(UserModel user, Message message) {
        if (message != null) {
            user.getMessages().add(message);
            userRepository.save(user);
        }
    }

    public void addUnknownWordToUser(UserModel user, Word word) {
        user.getUnknownWords().add(word);

        // if the storage limit is reached,
        // we'll remove the word with the latest scheduled reminder
        if (user.getUnknownWords().size() > WordsReminderService.storageCapacity) {
            user.getUnknownWords().remove(user.getUnknownWords().last());
        }

        userRepository.save(user);
    }

    public String constructUserGoalsString(UserModel user) {
        List<String> goals = user.getGoals();
        StringJoiner joiner = new StringJoiner(", ");
        for (String goal : goals) {
            joiner.add(goal);
        }
        return joiner.toString();
    }

    public String constructUserPreferencesString(UserModel user) {
        List<String> interests = user.getInterests();
        StringJoiner joiner = new StringJoiner(", ");
        for (String interest : interests) {
            joiner.add(interest);
        }
        return joiner.toString();
    }

    public List<String> getGoals(String username) {
        UserModel user = findOrThrow(username);
        return user.getGoals();
    }

    public List<String> getInterests(String username) {
        UserModel user = findOrThrow(username);
        return user.getInterests();
    }

    public String getLevel(String username) {
        UserModel user = findOrThrow(username);
        return user.getCefrLevel();
    }

    public String getDialect(String username) {
        UserModel user = findOrThrow(username);
        return user.getDialect();
    }

    public void setDialect(String username, String dialect) {
        UserModel user = findOrThrow(username);
        user.setDialect(dialect);
        userRepository.save(user);
    }
}
