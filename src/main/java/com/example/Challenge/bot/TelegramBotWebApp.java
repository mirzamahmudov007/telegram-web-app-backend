package com.example.Challenge.bot;

import com.example.Challenge.model.Permission;
import com.example.Challenge.model.User;
import com.example.Challenge.service.PermissionService;
import com.example.Challenge.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.webapp.WebAppInfo;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Component
public class TelegramBotWebApp extends TelegramLongPollingBot {

    @Value("${telegram.bot.username}")
    private String botUsername;

    @Value("${telegram.bot.token}")
    private String botToken;

    @Value("${webapp.url}")
    private String webAppUrl;

    private final UserService userService;

    private final PermissionService permissionService;
    public TelegramBotWebApp(UserService userService, PermissionService permissionService) {
        this.userService = userService;
        this.permissionService = permissionService;
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();
            String username = update.getMessage().getFrom().getUserName();
            String firstName = update.getMessage().getFrom().getFirstName();
            String lastName = update.getMessage().getFrom().getLastName();

            // Handle /start command - register the user and send web app button
            if (messageText.equals("/start")) {
                User registeredUser = registerUser(chatId, username, firstName, lastName);
                sendWebAppButton(chatId);
            } else {
                // Handle other commands or messages
                processMessage(chatId, messageText);
            }
        }
    }

    private User registerUser(long telegramId, String username, String firstName, String lastName) {
        // Check if user already exists
        if (!userService.existsByTelegramId(String.valueOf(telegramId))) {
            User user = new User();
            user.setTelegramId(String.valueOf(telegramId));

            // If username is null, generate one
            if (username == null || username.isEmpty()) {
                username = "user" + telegramId;
            }

            if (userService.existsByUsername(username)) {
                username = username + telegramId;
            }

            HashSet<String> allPermissions = new HashSet<>();
            permissionService.getAllPermissions().forEach(permission ->
                    allPermissions.add(permission.getId())
            );

            user.setPermissions(allPermissions);


            user.setUsername(username);
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setRole("USER");
            // New users have no permissions by default
            User savedUser = userService.saveUser(user);
            log.info("New user registered: {}", username);
            return savedUser;
        } else {
            log.info("User already exists: {}", username);
            return userService.getUserByTelegramId(String.valueOf(telegramId)).orElse(null);
        }
    }

    private void sendWebAppButton(long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText("Xush kelibsiz! Ilovani ochish uchun quyidagi tugmani bosing:");

        // Create web app button
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> rowInline = new ArrayList<>();

        InlineKeyboardButton webAppButton = new InlineKeyboardButton();
        webAppButton.setText("Ilovani ochish");

        // For Telegram Web App, we need to use a valid URL
        WebAppInfo webAppInfo = new WebAppInfo();
        webAppInfo.setUrl(webAppUrl);
        webAppButton.setWebApp(webAppInfo);

        rowInline.add(webAppButton);
        rowsInline.add(rowInline);
        markupInline.setKeyboard(rowsInline);
        message.setReplyMarkup(markupInline);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error("Error sending web app button", e);
        }
    }

    private void processMessage(long chatId, String text) {
        // Process other commands or messages
        sendMessage(chatId, "Men sizning xabaringizni qabul qildim: " + text);
    }

    private void sendMessage(long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(text);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error("Error sending message", e);
        }
    }
}

