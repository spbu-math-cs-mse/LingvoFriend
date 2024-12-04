package ru.lingvofriend.tgbot;

import io.github.cdimascio.dotenv.Dotenv;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.webapp.WebAppInfo;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import ru.lingvofriend.tgbot.questionnaire.QuestionnaireHandler;
import ru.lingvofriend.tgbot.questionnaire.UserResponse;
import ru.lingvofriend.tgbot.questionnaire.UserState;

public class Bot extends TelegramLongPollingBot {
    public static void main(String[] argv) {
        Dotenv dotenv = Dotenv.load();
        String token = dotenv.get("TGBOT_TOKEN");
        String yandexApiKey = dotenv.get("YANDEXGPT_API_KEY");
        logger.info("starting lingvofriend tgbot...");
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(new Bot(token, yandexApiKey));
        } catch (TelegramApiException e) {
            logger.fatal("telegram api exception", e);
        }
    }

    public Bot(String token, String yandexApiKey) {
        super(token);
        this.token = token;
        this.gptClient = yandexApiKey != null ? new YandexGPTClient(yandexApiKey) : null;
        this.questionnaireHandler = new QuestionnaireHandler(userStates, userResponses, this);
    }

    @Override
    public String getBotUsername() {
        return "LingvoFriendBot";
    }

    @Override
    public String getBotToken() {
        return token;
    }

    @Override
    public void onUpdateReceived(Update update) {
        Message message = update.getMessage();
        User user = message.getFrom();
        String userMessage = message.getText();
        Long chatId = message.getChatId();

        logger.trace("{} (user {}) wrote '{}'", user.getFirstName(), user.getUserName(), userMessage);

        String response;
        if (userMessage.equals("/start")) {
            ReplyKeyboardMarkup keyboardMarkup = createMainKeyboard();
            SendMessage sendMessage = SendMessage.builder()
                    .chatId(chatId.toString())
                    .text("Welcome! Choose an option:")
                    .replyMarkup(keyboardMarkup)
                    .build();
            try {
                execute(sendMessage);
            } catch (TelegramApiException e) {
                logger.error("Failed to send message with keyboard: {}", e.getMessage());
            }
            return;
        } else if (userMessage.equals("Чат") || userMessage.equals("/chat")) {
            usersInChatMode.add(chatId);
            ReplyKeyboardMarkup keyboardMarkup = createChatKeyboard();
            SendMessage sendMessage = SendMessage.builder()
                    .chatId(chatId.toString())
                    .text("Chat mode enabled. You can now talk directly with the AI. Press 'Выйти' to end the chat.")
                    .replyMarkup(keyboardMarkup)
                    .build();
            try {
                execute(sendMessage);
            } catch (TelegramApiException e) {
                logger.error("Failed to send message with keyboard: {}", e.getMessage());
            }
            return;
        } else if (userMessage.equals("Выйти") || userMessage.equals("/exit")) {
            usersInChatMode.remove(chatId);
            ReplyKeyboardMarkup keyboardMarkup = createMainKeyboard();
            SendMessage sendMessage = SendMessage.builder()
                    .chatId(chatId.toString())
                    .text("Chat mode disabled. Goodbye!")
                    .replyMarkup(keyboardMarkup)
                    .build();
            try {
                execute(sendMessage);
            } catch (TelegramApiException e) {
                logger.error("Failed to send message with keyboard: {}", e.getMessage());
            }
            return;
        } else if (userMessage.equals("Опрос") || userMessage.equals("/questionnaire")) {
            questionnaireHandler.handleResponse(chatId, "/questionnaire");
            return;
        } else if (gptClient != null && usersInChatMode.contains(chatId)) {
            response = gptClient.generateResponse(userMessage);
        } else {
            response = "Use /chat to start chatting with AI";
        }

        try {
            execute(SendMessage.builder()
                    .chatId(chatId.toString())
                    .text(response)
                    .build());
        } catch (TelegramApiException e) {
            logger.error("Failed to send message: {}", e.getMessage());
        }
    }

    private final String token;
    private final YandexGPTClient gptClient;
    private static final Logger logger = LogManager.getLogger();
    private final Set<Long> usersInChatMode = new HashSet<>();

    private final Map<Long, UserState> userStates = new ConcurrentHashMap<>();
    private final Map<Long, UserResponse> userResponses = new ConcurrentHashMap<>();
    private final QuestionnaireHandler questionnaireHandler;

    private ReplyKeyboardMarkup createMainKeyboard() {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboard = new ArrayList<>();

        KeyboardRow row1 = new KeyboardRow();
        row1.add(new KeyboardButton("Чат"));

        KeyboardRow row2 = new KeyboardRow();
        KeyboardButton webAppButton = new KeyboardButton("Приложение");
        WebAppInfo webAppInfo = new WebAppInfo();
        webAppInfo.setUrl("https://lingvofriend.fun");
        webAppButton.setWebApp(webAppInfo);

        row2.add(new KeyboardButton("Опрос (TODO)"));
        row2.add(webAppButton);

        keyboard.add(row1);
        keyboard.add(row2);

        keyboardMarkup.setKeyboard(keyboard);
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setOneTimeKeyboard(false);

        return keyboardMarkup;
    }

    private ReplyKeyboardMarkup createChatKeyboard() {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboard = new ArrayList<>();

        KeyboardRow row = new KeyboardRow();
        row.add(new KeyboardButton("Выйти"));
        keyboard.add(row);

        keyboardMarkup.setKeyboard(keyboard);
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setOneTimeKeyboard(false);

        return keyboardMarkup;
    }
}
