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
import java.util.stream.Collectors;
import java.security.MessageDigest;
import org.apache.commons.codec.digest.HmacUtils;
import java.util.TreeMap;

public class Bot extends TelegramLongPollingBot {
    public static void main(String[] argv) {
        Dotenv dotenv = Dotenv.load();
        String token = dotenv.get("TGBOT_TOKEN");
        logger.info("starting lingvofriend tgbot...");
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(new Bot(token));
        } catch (TelegramApiException e) {
            logger.fatal("telegram api exception", e);
        }
    }

    public Bot(String token) {
        super(token);
        this.frontendUrl = "https://lingvofriend.fun";
        this.token = token;
        this.backendClient = new BackendApiClient(frontendUrl);
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

        if (!authenticatedUsers.contains(chatId)) {
            try {
                authenticateUser(user, chatId);
            } catch(Exception e) {
                logger.error("Failed to authenticate user: {}", e.getMessage());
                sendMessage(chatId, "Failed to authenticate. Please try again later.");
                return;
            }
        }
        if (userMessage.equals("/start")) {
            sendMessage(chatId, "Welcome to LingvoFriend! Use /chat to start chatting with AI");
            if (usersInChatMode.contains(chatId)) {
                usersInChatMode.remove(chatId);
            }
            return;
        } if ((userMessage.equals("Чат") || userMessage.equals("/chat")) && !usersInChatMode.contains(chatId)) {
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
        } else if ((userMessage.equals("Выйти") || userMessage.equals("/exit")) && usersInChatMode.contains(chatId)) {
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
        } else if (usersInChatMode.contains(chatId)) {
            String response;
            try {
                response = backendClient.sendMessage(userMessage);
            } catch (Exception e) {
                logger.error("Failed to get response from backend: {}", e.getMessage());
                response = "Sorry, I'm having trouble understanding you. Please try again later.";
            }
            try {
                execute(SendMessage.builder()
                        .chatId(chatId.toString())
                        .text(response)
                        .replyMarkup(createChatKeyboard())
                        .build());
            } catch (TelegramApiException e) {
                logger.error("Failed to send message: {}", e.getMessage());
            }
        } else {
            ReplyKeyboardMarkup keyboardMarkup = createMainKeyboard();
            SendMessage sendMessage = SendMessage.builder()
                    .chatId(chatId.toString())
                    .text("Use /chat to start chatting with AI")
                    .replyMarkup(keyboardMarkup)
                    .build();
            try {
                execute(sendMessage);
            } catch (TelegramApiException e) {
                logger.error("Failed to send message with keyboard: {}", e.getMessage());
            }
            return;
        }
    }

    private final String token;
    private final String frontendUrl;
    private final BackendApiClient backendClient;
    private static final Logger logger = LogManager.getLogger();
    private final Set<Long> usersInChatMode = new HashSet<>();

    private final Set<Long> authenticatedUsers = new HashSet<>();

    private void authenticateUser(User user, Long chatId) throws Exception {
        String authDate = String.valueOf(System.currentTimeMillis() / 1000);
        
        Map<String, String> data = new TreeMap<>();
        data.put("auth_date", authDate);
        data.put("first_name", user.getFirstName());
        data.put("id", user.getId().toString());
        if (user.getUserName() != null) {
            data.put("username", user.getUserName());
        }
        String photoUrl = null; // Get actual photo URL if available
        if (photoUrl != null) {
            data.put("photo_url", photoUrl);
        }
 
        String dataCheckString = data.entrySet().stream()
                .map(e -> e.getKey() + "=" + e.getValue())
                .collect(Collectors.joining("\n"));
 
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] secretKey = digest.digest(token.getBytes());
 
        String hash = new HmacUtils("HmacSHA256", secretKey)
                .hmacHex(dataCheckString.getBytes());
 
        logger.trace("Data check string: " + dataCheckString);
        logger.trace("Generated hash: " + hash);
 
        backendClient.loginWithTelegram(
            user.getId().toString(),
            user.getFirstName(),
            user.getUserName(),
            photoUrl,
            authDate,
            hash
        );
        authenticatedUsers.add(chatId);
    } 

    private void sendMessage(Long chatId, String text) {
        try {
            execute(SendMessage.builder()
                .chatId(chatId.toString())
                .text(text)
                .replyMarkup(createMainKeyboard())
                .build());
        } catch (TelegramApiException e) {
            logger.error("Failed to send message: {}", e.getMessage());
        }
    }

    private ReplyKeyboardMarkup createMainKeyboard() {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboard = new ArrayList<>();

        KeyboardRow row = new KeyboardRow();
        row.add(new KeyboardButton("Чат"));

        keyboard.add(row);

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
