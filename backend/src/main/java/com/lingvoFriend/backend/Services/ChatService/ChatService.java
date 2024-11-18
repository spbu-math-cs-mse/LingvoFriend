package com.lingvoFriend.backend.Services.ChatService;

import com.lingvoFriend.backend.Repositories.UserRepository;
import com.lingvoFriend.backend.Services.ChatService.models.Message;
import com.lingvoFriend.backend.Services.AuthService.models.UserModel;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ChatService {

    private final UserRepository userRepository;

    public List<Message> addMessageToUser(String username, Message message) {
        Optional<UserModel> User = userRepository.findByUsername(username);

        User.ifPresentOrElse(
                user -> {
                    user.getMessages().add(message);
                    userRepository.save(user);
                },
                () -> {
                    throw new BadCredentialsException("User not found");
                });
        return User.get().getMessages();
    }
}
