package ru.lingvofriend.tgbot.questionnaire;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import ru.lingvofriend.tgbot.Bot;

import java.util.*;

public class QuestionnaireHandler {
    private final Map<Long, UserState> userStates;
    private final Map<Long, UserResponse> userResponses;
    private final Bot bot;

    // Define interests
    private static final List<String> INTERESTS = Arrays.asList(
        "Technology", "Sports", "Music", "Travel", "Art",
        "Science", "Literature", "Gaming", "Health", "Education",
        "Finance", "Cooking", "Photography", "Movies", "Fitness",
        "History", "Nature", "Politics", "Business", "Fashion"
    );

    public QuestionnaireHandler(Map<Long, UserState> userStates,
                                Map<Long, UserResponse> userResponses,
                                Bot bot) {
        this.userStates = userStates;
        this.userResponses = userResponses;
        this.bot = bot;
    }

    public void startQuestionnaire(Long chatId) {
        userStates.put(chatId, UserState.ASK_NAME);
        userResponses.put(chatId, new UserResponse());
        sendMessage(chatId, "Welcome to LingvoFriend Bot! Let's get to know you better.\n\n" +
                "What is your name?");
    }

    public Boolean handleResponse(Long chatId, String text) {
        UserState state = userStates.getOrDefault(chatId, UserState.START);
        UserResponse response = userResponses.get(chatId);

        switch (state) {
            case ASK_NAME:
                response.setName(text);
                userStates.put(chatId, UserState.ASK_GENDER);
                sendMessage(chatId, "Great, " + text + "! What is your preferred pronoun?\n\n" +
                        "1. He/Him\n2. She/Her\n3. They/Them\n\nPlease reply with the number corresponding to your choice.");
                return false;

            case ASK_GENDER:
                handleGender(chatId, text);
                return false;

            case ASK_AGE:
                handleAge(chatId, text);
                return false;

            case ASK_GOALS:
                response.setGoals(text);
                userStates.put(chatId, UserState.ASK_ENGLISH_EXPERIENCE);
                sendMessage(chatId, "Do you have any previous experience learning English?\n\n" +
                        "Please reply with Yes or No.");
                return false;

            case ASK_ENGLISH_EXPERIENCE:
                response.setEnglishExperience(text);
                userStates.put(chatId, UserState.ASK_INTERESTS);
                sendInterestsOptions(chatId);
                return false;

            case ASK_INTERESTS:
                handleInterests(chatId, text);
                return true;

            case COMPLETED:
                sendMessage(chatId, "You have already completed the questionnaire. Thank you!");
                return true;

            default:
                sendMessage(chatId, "Please start the questionnaire by sending /start.");
                return false;
        }
    }

    private void handleGender(Long chatId, String text) {
        UserResponse response = userResponses.get(chatId);
        switch (text.trim()) {
            case "1":
                response.setPronoun("He/Him");
                break;
            case "2":
                response.setPronoun("She/Her");
                break;
            default:
                sendMessage(chatId, "Invalid option. Please choose 1, 2, or 3.");
                return;
        }
        userStates.put(chatId, UserState.ASK_AGE);
        sendMessage(chatId, "How old are you?");
    }

    private void handleAge(Long chatId, String text) {
        try {
            int age = Integer.parseInt(text.trim());
            UserResponse response = userResponses.get(chatId);
            response.setAge(age); 
            userStates.put(chatId, UserState.ASK_GOALS);
            sendMessage(chatId, "What are your goals for learning English?");
        } catch (NumberFormatException e) {
            sendMessage(chatId, "Please enter a valid age number.");
        }
    }

    private void handleInterests(Long chatId, String text) {
        String[] selections = text.split(",");
        List<String> selectedInterests = new ArrayList<>();

        for (String selection : selections) {
            try {
                int index = Integer.parseInt(selection.trim()) - 1;
                if (index >= 0 && index < INTERESTS.size()) {
                    selectedInterests.add(INTERESTS.get(index));
                }
            } catch (NumberFormatException e) {
                // Ignore invalid input
            }
        }

        if (selectedInterests.isEmpty()) {
            sendMessage(chatId, "No valid interests selected. Please choose at least one interest by sending the numbers separated by commas.");
            return;
        }

        UserResponse response = userResponses.get(chatId);
        response.setInterests(selectedInterests);
        userStates.put(chatId, UserState.COMPLETED);
        sendMessage(chatId, "Thank you for completing the questionnaire!");
    }

    private void sendInterestsOptions(Long chatId) {
        StringBuilder sb = new StringBuilder("Please select your interests by sending the numbers separated by commas (e.g., 1,3,5):\n");
        for (int i = 0; i < INTERESTS.size(); i++) {
            sb.append((i + 1)).append(". ").append(INTERESTS.get(i)).append("\n");
        }
        sendMessage(chatId, sb.toString());
    }

    private void sendMessage(Long chatId, String text) {
        SendMessage message = SendMessage.builder()
                .chatId(chatId.toString())
                .text(text)
                .build();
        try {
            bot.execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
