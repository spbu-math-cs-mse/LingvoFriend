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
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.HashSet;
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

        if (userMessage.startsWith("/start")) {
            questionnaireHandler.startQuestionnaire(chatId);
            return;
        } else if (userStates.containsKey(chatId) && userStates.getOrDefault(chatId, UserState.START) != UserState.COMPLETED) {
            questionnaireHandler.handleResponse(chatId, userMessage);
            if (userStates.getOrDefault(chatId, UserState.START) != UserState.COMPLETED)
                return;
        }

        String response;
        if (gptClient != null) {
            if ("/chat".equals(userMessage)) {
                usersInChatMode.add(chatId);
                response = "Chat mode enabled. You can now talk directly with the AI. Use /exit to end the chat.";
            } else if ("/exit".equals(userMessage)) {
                usersInChatMode.remove(chatId);
                response = "Chat mode disabled. Goodbye!";
            } else if (usersInChatMode.contains(chatId)) {
                response = gptClient.generateResponse(userMessage);
            } else {
                response = "Use /chat to start chatting with AI";
            }
        } else {
            response = "AI chat is not available at the moment";
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
}
