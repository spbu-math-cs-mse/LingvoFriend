package com.lingvoFriend.backend.Services.QuestionnaireService;

import java.util.*;

public class QuestionnaireService {
    private static final List<String> INTERESTS = Arrays.asList(
            "Книги", "Музыка", "Путешествия", "Искусство", "Спорт",
            "Игры", "Кулинария", "Технологии", "Стиль и мода", "Наука"
    );

    private final Map<String, QuestionState> userStates = new HashMap<>();
    private final Map<String, QuestionnaireResponse> responses = new HashMap<>();

    public QuestionnaireQuestion startQuestionnaire(String userId) {
        userStates.put(userId, QuestionState.NAME);
        responses.put(userId, new QuestionnaireResponse());
        return new QuestionnaireQuestion("""
                Добро пожаловать в Lingvo Friend! Давай познакомимся получше:
                
                Как тебя зовут?""", null);
    }

    public QuestionnaireQuestion handleResponse(String userId, String answer) {
        QuestionState state = userStates.getOrDefault(userId, QuestionState.NOT_STARTED);
        QuestionnaireResponse response = responses.get(userId);

        if (response == null || state == QuestionState.NOT_STARTED) {
            return new QuestionnaireQuestion("Сначала нужно познакомиться.", null);
        }

        return processAnswer(userId, answer, state, response);
    }

    private QuestionnaireQuestion processAnswer(String userId, String answer, QuestionState state, QuestionnaireResponse response) {
        switch (state) {
            case NAME:
                response.setName(answer);
                userStates.put(userId, QuestionState.GENDER);
                return new QuestionnaireQuestion(
                        "Твой пол?",
                        Arrays.asList("Мужской", "Женский")
                );

            case GENDER:
                if (processGender(response, answer)) {
                    userStates.put(userId, QuestionState.AGE);
                    return new QuestionnaireQuestion("Сколько тебе лет?", null);
                }
                return new QuestionnaireQuestion("Пожалуйста, выбери валидную опцию.", Arrays.asList("Мужской", "Женский"));

            case AGE:
                try {
                    int age = Integer.parseInt(answer.trim());
                    response.setAge(age);
                    userStates.put(userId, QuestionState.GOALS);
                    return new QuestionnaireQuestion(
                            "Какие твои цели в изучении английского?",
                            null
                    );
                } catch (NumberFormatException e) {
                    return new QuestionnaireQuestion(
                            "Пожалуйста, введите число.",
                            null
                    );
                }

            case GOALS:
                response.setGoals(answer);
                userStates.put(userId, QuestionState.ENGLISH_EXPERIENCE);
                return new QuestionnaireQuestion(
                        "Вы изучали английский до этого?",
                        Arrays.asList("Да", "Нет")
                );

            case ENGLISH_EXPERIENCE:
                response.setEnglishExperience(answer);
                userStates.put(userId, QuestionState.INTERESTS);
                return new QuestionnaireQuestion("Выбери свои интересы: ", INTERESTS);

            case INTERESTS:
                if (processInterests(response, answer)) {
                    userStates.put(userId, QuestionState.COMPLETED);
                    return new QuestionnaireQuestion(
                            "Спасибо за прохождение опроса!",
                            null
                    );
                }
                return new QuestionnaireQuestion(
                        "Выберите интересы из списка.",
                        INTERESTS
                );

            case COMPLETED:
                return new QuestionnaireQuestion(
                        "Вы уже проходили опрос.",
                        null
                );

            default:
                return new QuestionnaireQuestion(
                        "Сначала нужно познакомиться.",
                        null
                );
        }
    }

    private boolean processGender(QuestionnaireResponse response, String answer) {
        return switch (answer) {
            case "Мужской" -> {
                response.setGender("He/Him");
                yield true;
            }
            case "Женский" -> {
                response.setGender("She/Her");
                yield true;
            }
            default -> false;
        };
    }

    private boolean processInterests(QuestionnaireResponse response, String answer) {
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

        response.setInterests(selectedInterests);
        return true;
    }

    public List<String> getAvailableInterests() {
        return INTERESTS;
    }

    public QuestionnaireResponse getResponse(String userId) {
        return responses.get(userId);
    }
} 
