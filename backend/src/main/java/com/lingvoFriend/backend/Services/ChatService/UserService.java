package com.lingvoFriend.backend.Services.ChatService;

import com.lingvoFriend.backend.Repositories.UserRepository;
import com.lingvoFriend.backend.Services.AuthService.models.UserModel;
import com.lingvoFriend.backend.Services.ChatService.models.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    public UserModel findOrThrow(String username) {
        return userRepository
            .findByUsername(username)
            .orElseThrow(() -> new BadCredentialsException("user %s not found".formatted(username)));
    }

    public Optional<UserModel> find(String username) {
        return userRepository.findByUsername(username);
    }

    public void addMessageToUser(UserModel user, Message message) {
        user.getMessages().add(message);
        userRepository.save(user);
    }

    public UserRepository getUserRepository() {
        return userRepository;
    }

    @Autowired
    private UserRepository userRepository;
}
