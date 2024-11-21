package com.lingvoFriend.backend.Services.QuestionnaireService;

import com.lingvoFriend.backend.Repositories.UserRepository;
import com.lingvoFriend.backend.Services.AuthService.models.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class QuestionnaireService {
    private static final List<String> INTERESTS = Arrays.asList(
            "Книги", "Музыка", "Путешествия", "Искусство", "Спорт",
            "Игры", "Кулинария", "Технологии", "Стиль и мода", "Наука");

    private final UserRepository userRepository;

    @Autowired
    public QuestionnaireService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public QuestionnaireQuestion startQuestionnaire(String userId) {
        UserModel user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setQuestionState(QuestionState.NAME);
        userRepository.save(user);

        return new QuestionnaireQuestion("""
                Добро пожаловать в Lingvo Friend! Давай познакомимся получше:

                Как тебя зовут?""", null);
    }

    public QuestionnaireQuestion handleResponse(String userId, String answer) {
        UserModel user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getQuestionState() == QuestionState.NOT_STARTED) {
            return new QuestionnaireQuestion("Сначала нужно познакомиться.", null);
        }

        return processAnswer(user, answer);
    }

    private QuestionnaireQuestion processAnswer(UserModel user, String answer) {
        QuestionnaireQuestion nextQuestion = switch (user.getQuestionState()) {
            case NAME -> {
                user.setName(answer);
                user.setQuestionState(QuestionState.GENDER);
                yield new QuestionnaireQuestion(
                        "Твой пол?",
                        Arrays.asList("Мужской", "Женский"));
            }
            case GENDER -> {
                if (processGender(user, answer)) {
                    user.setQuestionState(QuestionState.AGE);
                    yield new QuestionnaireQuestion("Сколько тебе лет?", null);
                }
                yield new QuestionnaireQuestion("Пожалуйста, выбери валидную опцию.",
                        Arrays.asList("Мужской", "Женский"));
            }
            case AGE -> {
                try {
                    int age = Integer.parseInt(answer.trim());
                    user.setAge(age);
                    user.setQuestionState(QuestionState.GOALS);
                    yield new QuestionnaireQuestion(
                            "Какие твои цели в изучении английского?",
                            null);
                } catch (NumberFormatException e) {
                    yield new QuestionnaireQuestion(
                            "Пожалуйста, введите число.",
                            null);
                }
            }
            case GOALS -> {
                user.setGoals(answer);
                user.setQuestionState(QuestionState.ENGLISH_EXPERIENCE);
                yield new QuestionnaireQuestion(
                        "Вы изучали английский до этого?",
                        Arrays.asList("Да", "Нет"));
            }
            case ENGLISH_EXPERIENCE -> {
                user.setEnglishExperience(answer);
                user.setQuestionState(QuestionState.INTERESTS);
                yield new QuestionnaireQuestion("Выбери свои интересы: ", INTERESTS);
            }
            case INTERESTS -> {
                if (processInterests(user, answer)) {
                    user.setQuestionState(QuestionState.COMPLETED);
                    yield new QuestionnaireQuestion(
                            "Спасибо за прохождение опроса!",
                            null);
                }
                yield new QuestionnaireQuestion(
                        "Выберите интересы из списка.",
                        INTERESTS);
            }
            case COMPLETED -> {
                yield new QuestionnaireQuestion(
                        "Вы уже проходили опрос.",
                        null);
            }
            default -> {
                yield new QuestionnaireQuestion(
                        "Сначала нужно познакомиться.",
                        null);
            }
        };

        userRepository.save(user);
        return nextQuestion;
    }

    private boolean processGender(UserModel user, String answer) {
        return switch (answer) {
            case "Мужской" -> {
                user.setGender("He/Him");
                yield true;
            }
            case "Женский" -> {
                user.setGender("She/Her");
                yield true;
            }
            default -> false;
        };
    }

    private boolean processInterests(UserModel user, String answer) {
        String[] selections = answer.split(",");
        List<String> selectedInterests = new ArrayList<>();

        for (String selection : selections) {
            if (INTERESTS.contains(selection)) {
                selectedInterests.add(selection);
            }
        }

        if (selectedInterests.isEmpty()) {
            return false;
        }

        user.setInterests(selectedInterests);
        return true;
    }

    public List<String> getAvailableInterests() {
        return INTERESTS;
    }

    public QuestionnaireResponse getResponse(String userId) {
        UserModel user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return user.getQuestionnaireResponse();
    }
}
