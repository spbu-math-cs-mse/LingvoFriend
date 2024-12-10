package com.lingvoFriend.backend.Services.ChatService;

import com.lingvoFriend.backend.Repositories.UserRepository;
import com.lingvoFriend.backend.Services.AuthService.models.UserModel;
import com.lingvoFriend.backend.Services.ChatService.models.Message;
import com.lingvoFriend.backend.Services.ChatService.models.Word;

import lombok.Getter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import java.util.Optional;

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
}
