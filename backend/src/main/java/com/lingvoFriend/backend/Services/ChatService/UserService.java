package com.lingvoFriend.backend.Services.ChatService;

import java.util.List;
import java.util.Optional;
import java.util.StringJoiner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import com.lingvoFriend.backend.Repositories.UserRepository;
import com.lingvoFriend.backend.Services.AuthService.models.UserModel;
import com.lingvoFriend.backend.Services.ChatService.models.Message;

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

    @Autowired
    private UserRepository userRepository;
}
