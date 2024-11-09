package ru.lingvofriend.tgbot;

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
import java.util.Set;

public class Bot extends TelegramLongPollingBot {
    public static void main(String[] args) {
        if (args.length != 2) {
            logger.fatal("expected two command-line arguments: bot token and YandexGPT API key");
            return;
        }
        logger.info("starting lingvofriend tgbot...");
        logger.info("token: {}", args[0]);
        logger.info("yandex api key: {}", args[1]);
        String token = args[0];
        String yandexApiKey = args[1];
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(new Bot(token, yandexApiKey));
        } catch (TelegramApiException e) {
            logger.fatal("telegram api exception: {}", e);
        }
    }

    public Bot(String token, String yandexApiKey) {
        super(token);
        this.token = token;
        this.gptClient = yandexApiKey != null ? new YandexGPTClient(yandexApiKey) : null;
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
}
