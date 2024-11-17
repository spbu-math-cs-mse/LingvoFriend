package ru.lingvofriend.tgbot.questionnaire;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import ru.lingvofriend.tgbot.Bot;

import java.util.*;

public class QuestionnaireHandler {
    private final Map<Long, UserState> userStates;
    private final Map<Long, UserResponse> userResponses;
    private final Bot bot;

    // Define interests
    private static final List<String> INTERESTS = Arrays.asList(
        "Книги", "Музыка", "Путешествия", "Искусство", "Спорт",
        "Игры", "Кулинария", "Технологии", "Стиль и мода", "Наука"
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
        sendMessage(chatId, "Добро пожаловать в Lingvo Friend! Давай познакомимся получше:\n\n" +
                "Сдохни, ублюдок.");
    }

    public void handleResponse(Long chatId, String text) {
        UserState state = userStates.getOrDefault(chatId, UserState.START);
        UserResponse response = userResponses.get(chatId);

        switch (state) {
            case ASK_NAME:
                response.setName(text);
                userStates.put(chatId, UserState.ASK_GENDER);

                SendMessage message = new SendMessage();
                message.setChatId(chatId);
                message.setText( "Отлично! Теперь определимся с полом:\n\n" +
                "1. Мужчина\n" + "2. Женщина\n\n" + "Введи 1 или 2");
                ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
                List<KeyboardRow> keyboard = new ArrayList<>();
                KeyboardRow row = new KeyboardRow();
                row.add(new KeyboardButton("1"));
                row.add(new KeyboardButton("2"));
                keyboard.add(row);
                keyboardMarkup.setResizeKeyboard(true);
                keyboardMarkup.setKeyboard(keyboard);
                message.setReplyMarkup(keyboardMarkup);
                try {
                    bot.execute(message);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
                return;

            case ASK_GENDER:
                handleGender(chatId, text);
                return;

            case ASK_AGE:
                handleAge(chatId, text);
                return;

            case ASK_GOALS:
                response.setGoals(text);
                userStates.put(chatId, UserState.ASK_ENGLISH_EXPERIENCE);

                SendMessage message1 = new SendMessage();
                message1.setChatId(chatId);
                message1.setText( "Вы изучали английский до этого?\n\n");
                ReplyKeyboardMarkup keyboardMarkup1 = new ReplyKeyboardMarkup();
                List<KeyboardRow> keyboard1 = new ArrayList<>();
                KeyboardRow row1 = new KeyboardRow();
                row1.add(new KeyboardButton("Да"));
                row1.add(new KeyboardButton("Нет"));
                keyboard1.add(row1);
                keyboardMarkup1.setResizeKeyboard(true);
                keyboardMarkup1.setKeyboard(keyboard1);
                message1.setReplyMarkup(keyboardMarkup1);
                try {
                    bot.execute(message1);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
                return;

            case ASK_ENGLISH_EXPERIENCE:
                response.setEnglishExperience(text);
                userStates.put(chatId, UserState.ASK_INTERESTS);
                sendInterestsOptions(chatId);
                return;

            case ASK_INTERESTS:
                handleInterests(chatId, text);
                return;

            case COMPLETED:
                sendMessage(chatId, "Ты закончи проходить опрос! Спасибо!");
                return;

            default:
                sendMessage(chatId, "Для начала опроса напиши /start.");
                return;
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
                sendMessage(chatId, "Введи 1 или 2");
                return;
        }
        userStates.put(chatId, UserState.ASK_AGE);
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        ReplyKeyboardRemove keyboardRemove = new ReplyKeyboardRemove();
        keyboardRemove.setRemoveKeyboard(true);
        message.setReplyMarkup(keyboardRemove);
        message.setText("Сколько тебе лет?");
        try {
            bot.execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void handleAge(Long chatId, String text) {
        try {
            int age = Integer.parseInt(text.trim());
            UserResponse response = userResponses.get(chatId);
            response.setAge(age); 
            userStates.put(chatId, UserState.ASK_GOALS);
            sendMessage(chatId, "Какие твои цели в изучении английского?");
        } catch (NumberFormatException e) {
            sendMessage(chatId, "Введите число");
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
            sendMessage(chatId, "Следуйте инструкциям по вводу");
            return;
        }

        UserResponse response = userResponses.get(chatId);
        response.setInterests(selectedInterests);
        userStates.put(chatId, UserState.COMPLETED);
        sendMessage(chatId, "Спасибо за заполение анкеты!");
    }

    private void sendInterestsOptions(Long chatId) {
        StringBuilder sb = new StringBuilder("Выбери интересующие тебя темы (введи числа через запятую):\n");
        for (int i = 0; i < INTERESTS.size(); i++) {
            sb.append((i + 1)).append(". ").append(INTERESTS.get(i)).append("\n");
        }
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        ReplyKeyboardRemove keyboardRemove = new ReplyKeyboardRemove();
        keyboardRemove.setRemoveKeyboard(true);
        message.setReplyMarkup(keyboardRemove);
        message.setText(sb.toString());
        try {
            bot.execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
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
