package com.lingvoFriend.backend.Services.ChatService;

import com.lingvoFriend.backend.Repositories.UserRepository;
import com.lingvoFriend.backend.Services.ChatService.models.Message;
import com.lingvoFriend.backend.Services.AuthService.models.UserModel;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Random;

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

    public List<Message> getMessagesByUsername(String username) {
        Optional<UserModel> user = userRepository.findByUsername(username);

        return user.map(UserModel::getMessages)
                   .orElseThrow(() -> new BadCredentialsException("User not found"));
    }

    public boolean isEvaluating(String username) {
        Optional<UserModel> user = userRepository.findByUsername(username);
        if (user.isPresent()) {
            Integer status = user.get().getLevelEvaluationQuestionsAsked();
            return status > 0 && status < levelEvalQuestionsNumber;
        }
        throw new BadCredentialsException("User not found");
    }

    public boolean isEvaluatingFinished(String username) {
        Optional<UserModel> user = userRepository.findByUsername(username);
        if (user.isPresent()) {
            Integer status = user.get().getLevelEvaluationQuestionsAsked();
            return status >= levelEvalQuestionsNumber;
        }
        throw new BadCredentialsException("User not found");
    }

    public boolean isEvaluatinCEFR(String username) {
        Optional<UserModel> user = userRepository.findByUsername(username);
        if (user.isPresent()) {
            Integer status = user.get().getLevelEvaluationQuestionsAsked();
            return status == levelEvalQuestionsNumber;
        }
        throw new BadCredentialsException("User not found");
    }

    public void questionsAskedPlusOne(String username) {
        Optional<UserModel> userOpt = userRepository.findByUsername(username);
        if (userOpt.isPresent()) {
            UserModel user = userOpt.get();
            user.setLevelEvaluationQuestionsAsked(user.getLevelEvaluationQuestionsAsked() + 1);
            user.setCefrLevel(null);
            userRepository.save(user);
        } else {
            throw new BadCredentialsException("User not found");
        }
    }

    public void startEvaluation(String username) {
        Optional<UserModel> userOpt = userRepository.findByUsername(username);
        if (userOpt.isPresent()) {
            UserModel user = userOpt.get();
            user.setLevelEvaluationQuestionsAsked(1);
            user.setCefrLevel(null);
            userRepository.save(user);
        } else {
            throw new BadCredentialsException("User not found");
        }
    }

    public void setCefrLevel(String username, String level) {
        Optional<UserModel> userOpt = userRepository.findByUsername(username);
        if (userOpt.isPresent()) {
            UserModel user = userOpt.get();
            user.setCefrLevel(level);
            userRepository.save(user);
        } else {
            throw new BadCredentialsException("User not found");
        }
    }  

    public String extractCEFRLevel(String text) {
        String[] cefrLevels = {"A1", "A2", "B1", "B2", "C1", "C2"};
        for (String level : cefrLevels) {
            if (text.toUpperCase().contains(level)) {
                return level;
            }
        }
        Random random = new Random();
        int index = random.nextInt(cefrLevels.length);
        return cefrLevels[index];
    }

    private final Integer levelEvalQuestionsNumber = 5;
}
