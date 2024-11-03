package ru.lingvofriend.tgbot;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public class Bot extends TelegramLongPollingBot {
    public static void main(String[] args) {
        if (args.length != 1) {
            logger.fatal("expected one command-line argument: bot token");
            return;
        }
        logger.info("starting lingvofriend tgbot...");
        String token = args[0];
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(new Bot(token));
        } catch (TelegramApiException e) {
            logger.fatal("telegram api exception: {}", e);
        }
    }

    public Bot(String token) {
        this.token = token;
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
        logger.trace("{} (user {}) wrote '{}'", user.getFirstName(), user.getUserName(), message.getText());
    }

    private final String token;

    private static final Logger logger = LogManager.getLogger();
}
